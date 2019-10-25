package xworker.ai.aima.learning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilJava;

import aima.core.learning.framework.Attribute;
import aima.core.learning.framework.AttributeSpecification;
import aima.core.learning.framework.DataSet;
import aima.core.learning.framework.DataSetSpecification;
import aima.core.learning.framework.Example;
import aima.core.util.Util;
import ognl.Ognl;
import ognl.OgnlException;

public class DataSetActions {
	/**
	 * 创建DataSet。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException
	 */
	public static DataSet create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//创建数据集的描述
		DataSetSpecification spec = new DataSetSpecification();
		List<Thing> attrThings = self.getChilds("attribute"); 
		for(Thing attr : attrThings){
			String attrName = attr.getMetadata().getName();
			if("number".equals(attr.getString("type"))){
				//定义数字
				spec.defineNumericAttribute(attrName);
			}else{
				//定义字符串
				String[] values = null;
				if("yesno".equals(attr.getString("stringType"))){
					values = Util.yesno();
				}else{
					List<String> vs = new ArrayList<String>();
					for(Thing vthing : attr.getChilds("value")){
						vs.add(vthing.getString("value"));
					}
					values = new String[vs.size()];
					values = vs.toArray(values);
				}
				
				spec.defineStringAttribute(attrName, values);
			}
			
			if(attr.getBoolean("isTarget")){
				spec.setTarget(attrName);
			}
		}
		
		if(spec.getTarget() == null){
			throw new ActionException("DataSet not defined target, thing=" + self.getMetadata().getPath());
		}
		
		//数据样本数据
		Object datas = self.doAction("getDatas", actionContext);
		Iterator<Object> itera = UtilJava.getIterable(datas).iterator();		
		DataSet dataSet = new DataSet(spec);
		while(itera.hasNext()){
			Object data = itera.next();
			Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
			for(Thing attr : attrThings){
				String attrName = attr.getMetadata().getName(); 
				Object value = Ognl.getValue(attrName, data);
				AttributeSpecification attributeSpec = spec.getAttributeSpecFor(attrName);
				attributes.put(attrName, attributeSpec.createAttribute(String.valueOf(value)));
			}
			
			Attribute target = attributes.get(spec.getTarget());
			if(target == null){
				throw new ActionException("Target value is null, data=" + data + ", thing=" + self.getMetadata().getPath());
			}
			Example example = new Example(attributes, attributes.get(spec.getTarget()));
			dataSet.add(example);
		}
		
		return dataSet;
	}
	
	public static Object getDatas(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing dataStore = null;
		
		String dataStoreVar = self.getStringBlankAsNull("dataStoreVar");
		if(dataStoreVar != null){
			dataStore = (Thing) actionContext.get(dataStoreVar);
		}
		
		Thing dataStoreThing = null;
		if(dataStore == null){
			String dataStoreStr = self.getStringBlankAsNull("dataStoreThingPath");
			if(dataStoreStr != null){
				dataStoreThing = World.getInstance().getThing(dataStoreStr);
			}
		}
		if(dataStore == null && dataStoreThing == null){
			dataStoreThing = self.getThing("DataStore@0");
		}
		
		if(dataStoreThing != null){
			dataStore = (Thing) dataStoreThing.doAction("create", actionContext);
		}
		
		if(dataStore != null){
			if(dataStore.getBoolean("dataLoaded")){
				return dataStore.get("records");
			}else{
				dataStore.doAction("laod", actionContext);
				return dataStore.get("records");
			}
		}else{
			return null;
		}
	}
	
	/**
	 * 通过数据创建Example。
	 * 
	 * @param dataSet
	 * @param data
	 * @return
	 * @throws OgnlException
	 */
	public static Example createExample(DataSet dataSet, Object data) throws OgnlException{
		if(data != null && dataSet != null){
			Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
			List<String> attrNames = dataSet.getAttributeNames();
			for(String attrName : attrNames){
				Object value = Ognl.getValue(attrName, data);
				AttributeSpecification attributeSpec = dataSet.specification.getAttributeSpecFor(attrName);
				attributes.put(attrName, attributeSpec.createAttribute(String.valueOf(value)));
			}
			return new Example(attributes, attributes.get(dataSet.specification.getTarget()));
		}
		
		return null;
	}
}
