package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;

import ognl.OgnlException;

public class SplitPaneActions {
	public static SplitPane create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		Actor firstWidget = null;
		Actor secondWidget = null;
		Skin skin = null;
		String styleName = self.getStringBlankAsNull("styleName");
		String firstWidgetStr = self.getStringBlankAsNull("firstWidget");
		if(firstWidgetStr != null){
			firstWidget = (Actor) actionContext.get(firstWidgetStr);			
		}
		String secondWidgetStr = self.getStringBlankAsNull("secondWidget");
		if(secondWidgetStr != null){
			secondWidget = (Actor) actionContext.get(secondWidgetStr);			
		}
		if(firstWidget == null){
			firstWidget = createActor(self, "FirstWidget", actionContext);
		}
		if(secondWidget == null){
			secondWidget = createActor(self, "SecondWidget", actionContext);
		}
		
		skin = (Skin) actionContext.get(self.getString("skin"));
		SplitPane group = new SplitPane(firstWidget, secondWidget, self.getBoolean("vertical"), skin, styleName);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), group);
		init(self, group, actionContext);		
				
		return group;
	}
	
	public static Actor createActor(Thing self, String nodeName, ActionContext actionContext){
		Thing thing  = self.getThing(nodeName + "@0");
		if(thing != null && thing.getChilds().size() > 0){
			return (Actor) thing.getChilds().get(0).doAction("create", actionContext, UtilMap.toMap("parent", null));
		}else{
			return null;
		}
	}
	
	public static void init(Thing self, SplitPane item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("maxSplitAmount") != null){
			item.setMaxSplitAmount(self.getFloat("maxSplitAmount", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("minSplitAmount") != null){
			item.setMinSplitAmount(self.getFloat("minSplitAmount", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("splitAmount") != null){
			item.setSplitAmount(self.getFloat("splitAmount", 0, actionContext));
		}		
	}
}
