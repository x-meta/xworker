package xworker.swt.reacts;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataReactorActions {
	@SuppressWarnings("unchecked")
	public static void fireEvent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//获取参数
		String event = self.doAction("getEvent", actionContext);
		DataReactorContext context = self.doAction("getContext", actionContext);
		List<Object> datas = null;
		if(!"unselected".equals(event)) {
			Object data = self.doAction("getListValue", actionContext);
			if(data instanceof List<?>) {
				datas = (List<Object>) data;
			}else {
				datas = new ArrayList<Object>();
				datas.add(data);
			}
		}
		
		//触发事件
		List<String> reactors = self.doAction("getDataReactors", actionContext);
		if(reactors != null) {
			for(String reactorName : reactors) {
				fireEvent(event, reactorName, datas, context, actionContext);
			}
		}		
	}
	
	public static void fireEvent(String event, String reactorName, List<Object> datas, DataReactorContext context, ActionContext actionContext) {
		if(reactorName == null || "".equals(reactorName)) {
			return;
		}
		
		//解析DataRactor的参数
		String[] params = reactorName.trim().split("[|]"); 				
		String[] ns = params[0].split("[?]");
		String name = ns[0].trim();
		String action = null;
		if(ns.length > 1) {
			action = ns[1].trim();
		}
		
		//获取或者创建DataReactor
		DataReactor dataReactor = null;
		Object control = actionContext.get(name);
		if(control instanceof DataReactor) {
			dataReactor = (DataReactor) control;
		} else {
			Object obj = actionContext.get(name + "DataReactor");
			if(obj instanceof DataReactor) {
				dataReactor = (DataReactor) obj;
			}else if(control != null) {
				dataReactor = DataReactorFactory.create(action, params, control, actionContext);
			}
		}
		
		//触发事件
		if(dataReactor != null) {
			if("selected".equals(event)) {
				dataReactor.onSelected(datas, context);
			}else if("unselected".equals(event)) {
				dataReactor.onUnselected(context);
			}else if("added".equals(event)) {
				dataReactor.onAdded(-1, datas, context);
			}else if("updated".equals(event)) {
				dataReactor.onUpdated(datas, context);
			}else if("removed".equals(event)) {
				dataReactor.onRemoved(datas, context);
			}else if("loaded".equals(event)) {
				dataReactor.onLoaded(datas, context);
			}
		}
	}
}
