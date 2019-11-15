package xworker.swt.reacts.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.model.tree.SimpleTreeNode;
import xworker.app.model.tree.TreeModel;
import xworker.swt.reacts.DataFilter;
import xworker.swt.reacts.DataReactor;

public class SourceDataFilter extends DataFilter{

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas) {
		List<Object> list = new ArrayList<Object>();
		for(Object data : datas) {
			if(data instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) data;
				Object obj = map.get(TreeModel.Source);
				if(obj != null) {
					list.add(obj);
				} else {
					list.add(data);
				}
			}else if(data instanceof SimpleTreeNode) {
				SimpleTreeNode node = (SimpleTreeNode) data;
				list.add(node.getData());
			}else {
				list.add(data);
			}
		}
		
		return list;
	}

	public static SourceDataFilter create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		SourceDataFilter filter = new SourceDataFilter();
		actionContext.g().put(self.getMetadata().getName(), filter);
		
		return filter;
	}

}
