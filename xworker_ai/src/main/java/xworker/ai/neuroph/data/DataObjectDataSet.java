package xworker.ai.neuroph.data;

import java.util.List;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;

public class DataObjectDataSet {
	@SuppressWarnings({ "unchecked" })
	public static DataSet create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new ActionException("Dataojbect is null, path=" + self.getMetadata().getPath());
		}
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setLimit(-1);
		
		//获取数据
		List<DataObject> datas = (List<DataObject>) dataObject.doAction("query", actionContext, UtilMap.toMap("pageInfo", pageInfo));
		
		int inputCount = self.getInt("inputsCount", 0, actionContext);
		int outputCount = self.getInt("outputsCount", 0, actionContext);
		DataSet dataSet = new DataSet(inputCount, outputCount);
		List<Thing> attributes = dataObject.getChilds("attribute");
		for(DataObject data : datas){
			double[] ins = new double[inputCount];
			double[] outs = new double[outputCount];
			for(int i=0; i<attributes.size(); i++){
				Thing attr = attributes.get(i);
				double value = data.getDouble(attr.getMetadata().getName());
				if(i < inputCount){
					ins[i] = value;
				}else{
					outs[i - inputCount] = value;
				}
			}
			
			dataSet.addRow(new DataSetRow(ins, outs));
		}
		
		Thing normalizer = self.getThing("Normalizer@0");
		if(normalizer != null){
			return (DataSet) normalizer.doAction("normalize", actionContext, UtilMap.toMap("dataSet", dataSet));
		}else{
			return dataSet;
		}
	}
}
