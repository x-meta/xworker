package xworker.swt.reacts.datas;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.swt.reacts.DataReactor;

public class IdNameDataReactor extends DataReactor{
	Thing dataObject = null;
	
	public IdNameDataReactor(Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		dataObject = new Thing("xworker.dataObject.AbstractDataObject");
		dataObject.set("displayName", "id");
		
		Thing id = new Thing("xworker.dataObject.AbstractDataObject/@attribute");
		id.put("name", "id");
		id.put("key", "true");
		dataObject.addChild(id);
		
		Thing name = new Thing("xworker.dataObject.AbstractDataObject/@attribute");
		name.put("name", "name");		
		dataObject.addChild(name);
		
		List<String> values = self.doAction("getValues", actionContext);
		if(values != null) {
			for(String value : values) {
				if("".equals(value.trim())) {
					continue;
				}
				
				String[] vs = value.split("[,]");
				DataObject data = new DataObject(dataObject);
				data.put("id", vs[0]);
				if(vs.length > 1) {
					data.put("name", vs[1]);
				}else {
					data.put("name", vs[0]);
				}
				datas.add(data);
			}
						
		}
		fireLoaded();
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		IdNameDataReactor reactor = new IdNameDataReactor(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), reactor);
		return reactor;
	}

}
