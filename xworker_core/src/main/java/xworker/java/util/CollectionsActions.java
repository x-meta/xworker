package xworker.java.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CollectionsActions {
	private static Logger logger = LoggerFactory.getLogger(CollectionsActions.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sort(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//要排序的list
		List list = (List) self.doAction("getList", actionContext);
		if(list == null){
			logger.info("list is null, thing=" + self.getMetadata().getPath());
			return;
		}
		
		//比较器
		Comparator comparator = (Comparator) self.doAction("getComparator", actionContext);
		
		//比较动作
		if(comparator != null){
			Collections.sort(list,comparator);
		}else{
			Collections.sort(list);
		}
		
	}
}
