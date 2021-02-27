package xworker.dataObject.query;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class DataObjectQueryActions {
	private static final String TAG = DataObjectQueryActions.class.getName();
	
	/**
	 * 获取数据对象。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static Object getDataObject(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//先从属性获取
		Object obj = UtilData.getData(self, "dataObject", actionContext);
		if(obj instanceof Thing){
			return obj;
		}else if(obj instanceof String && !"".equals(obj)){
			return World.getInstance().getThing((String) obj);
		}
		
		//然后从子事物获取
		Thing dataObjects = self.getThing("DataObjects@0");
		if(dataObjects != null && dataObjects.getChilds().size() > 0){
			return dataObjects.getChilds().get(0);
		}
		
		return null;
	}
	
	public static Object getConditionConfig(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//先从属性获取
		Object obj = UtilData.getData(self, "condition", actionContext);
		if(obj instanceof Thing){
			return obj;
		}else if(obj instanceof String && !"".equals(obj)){
			return World.getInstance().getThing((String) obj);
		}
		
		//然后从子事物获取
		return self.getThing("Condition@0");		
	}
	
	/**
	 * 查询。
	 * 
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> query(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new ActionException("DataObject cannt be null, path=" + self.getMetadata().getPath());
		}
		
		Object conditionConfig = self.doAction("getConditionConfig", actionContext);
		Object conditionData = actionContext.get("conditionData");
		Object pageInfo = actionContext.get("pageInfo");
		
		if(debug){
			Executor.info(TAG, "DataObject query: dataObjectPath=" + dataObject.getMetadata().getPath());
			Executor.info(TAG, "    condition=" + conditionConfig);
			Executor.info(TAG, "    conditionData=" + conditionData);
			Executor.info(TAG, "    pageInfo=" + pageInfo);
		}
		
		List<DataObject> datas = (List<DataObject>) dataObject.doAction("query", actionContext, UtilMap.toMap(
				new Object[]{"conditionConfig", conditionConfig, "conditionData", conditionData, "pageInfo", pageInfo}));
		if(datas == null){
			throw new ActionException("DataObject query return null, dataObject error, path=" + self.getMetadata().getPath());
		}
		
		if(debug){
			Executor.info(TAG, "    dataSize=" + datas.size());
		}
		
		return datas;
	}
}
