package xworker.swt.reacts.filters;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataFilter;
import xworker.swt.reacts.DataReactor;

public class ClassDataFilter  extends DataFilter{

	@Override
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas) {
		List<Object> list = new ArrayList<Object>();
		for(Object data : datas) {
			if(data instanceof Class<?>) {
				list.add(data);
			}else if(data instanceof String) {
				try {
					Class<?> cls = Class.forName((String) data);
					if(cls != null) {
						list.add(cls);
					}
				}catch(Exception e){					
				}
			}
		}
		
		return list;
	}

	public static ClassDataFilter create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ClassDataFilter filter = new ClassDataFilter();
		actionContext.g().put(self.getMetadata().getName(), filter);
		
		return filter;
	}

}
