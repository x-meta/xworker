package xworker.swt.reacts.datas;

import java.util.Collection;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactor;

public class CollectionDataReactor extends DataReactor{
	@SuppressWarnings("unchecked")
	public CollectionDataReactor(Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		Object value = self.doAction("getValues", actionContext);
		if(value instanceof List<?>) {			
			datas.addAll((List<Object>) value);
			fireLoaded(null);
		}else if(value instanceof Collection) {
			setCollection((Collection<?>) value);
		}else if(value != null) {
			datas.add(value);
			fireLoaded(null);
		}
	}
	
	public void setValues(List<Object> values) {
		datas.clear();
		
		datas.addAll(values);
		fireLoaded(null);
	}
	
	public void setCollection(Collection<?> c) {
		datas.clear();
		datas.addAll(c);
		
		fireLoaded(null);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CollectionDataReactor reactor = new CollectionDataReactor(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), reactor);
		return reactor;
	}
	
}
