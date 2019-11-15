package xworker.swt.reacts.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataFilter;
import xworker.swt.reacts.DataReactor;

public class FileFilter extends DataFilter{

	@Override
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas) {
		List<Object> list = new ArrayList<Object>();
		for(Object data : datas) {
			if(data instanceof File) {
				list.add(data);
			}else if(data instanceof String) {
				File file = new File((String) data);
				if(file.exists()) {
					list.add(data);
				}
			}
		}
		
		return list;
	}

	public static FileFilter create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		FileFilter filter = new FileFilter();
		actionContext.g().put(self.getMetadata().getName(), filter);
		
		return filter;
	}
}
