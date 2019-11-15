package xworker.swt.reacts.filters;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.reacts.DataFilter;
import xworker.swt.reacts.DataReactor;

public class ThingFilter extends DataFilter{

	@Override
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas) {
		List<Object> list = new ArrayList<Object>();
		for(Object data : datas) {
			if(data instanceof Thing) {
				list.add(data);
			}else if(data instanceof String) {
				Thing thing = World.getInstance().getThing((String) data);
				if(thing != null) {
					list.add(thing);
				}
			}
		}
		
		return list;
	}

	public static ThingFilter create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingFilter filter = new ThingFilter();
		actionContext.g().put(self.getMetadata().getName(), filter);
		
		return filter;
	}
}
