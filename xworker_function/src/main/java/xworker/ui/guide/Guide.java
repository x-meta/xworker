package xworker.ui.guide;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Guide {
	/**
	 * 获取向导的可选后续向导。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getNextGuides(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing guide = self;
		
		//获取NextActions子节点
		Thing nextGuides = guide.getThing("NextGuides@0");
		if(nextGuides != null){
			return nextGuides.getChilds();
		}else{
			return Collections.emptyList();
		}
	}
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		GuideExecutor executor = new GuideExecutor(actionContext);
		executor.addGuide(self);
	}
	
	public static void doGuide(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		GuideExecutor executor = GuideExecutor.getGuideExecutor(actionContext);
		executor.guideFinished(self);
	}
}
