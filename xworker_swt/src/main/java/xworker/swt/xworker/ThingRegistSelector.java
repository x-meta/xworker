package xworker.swt.xworker;

import java.io.IOException;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.xwidgets.SplitButton;
import xworker.util.UtilData;

public class ThingRegistSelector {
	//private static Logger logger = LoggerFactory.getLogger(ThingRegistSelector.class);
	
	public static Object create(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		//String thingPath = (String) self.doAction("getThing", actionContext);		
		Thing thing =  (Thing) self.doAction("getThing", actionContext);//world.getThing(thingPath);
		if(thing == null){
			//logger.warn("ThingRegistSelector: thing is null, path=" + self.getMetadata().getPath());
		}
		String registType = (String) self.doAction("getRegistType", actionContext);//self.getStringBlankAsNull("registType");
		if(registType == null){
			registType = "child";
		}
		boolean searchByKeywords = self.getBoolean("searchByKeywords");
		String keywords = UtilData.getString(self.getStringBlankAsNull("keywords"), actionContext);
		boolean showCategoryTree = self.getBoolean("showCategoryTree");
		boolean showDescBrowser = self.getBoolean("showDescBrowser");
		boolean autoLoad = self.getBoolean("autoLoad");
		boolean descBrowserRight = true;//self.getBoolean("descBrowserRight");
		if(self.getBoolean("showContent")){
			//TabFolder替代了内容Composite
			showDescBrowser = true; 
		}
		
		Control control = null;
		Thing controlThing = null;
		if(searchByKeywords && showCategoryTree && showDescBrowser){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@allItem/@topSashForm");
		}else if(searchByKeywords && showCategoryTree){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchCategoryNoDescItem/@mainSashForm");
		}else if(searchByKeywords && showDescBrowser && descBrowserRight){
			String displayType = self.doAction("getContentDisplayType", actionContext);
			if("Composite".equals(displayType)){
				controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchDescRightCompositeITem/@mainSashForm");
			}else if("Browser".equals(displayType)){
				controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchDescRightCompositeITem/@mainSashForm");
			}else{
				controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchDesc1/@mainSashForm");
			}
		}else if(searchByKeywords && showDescBrowser){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchDesc/@mainSashForm");
		}else if(searchByKeywords){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@search/@composite");
		}else if(showCategoryTree && showDescBrowser){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@categoryDesc/@topSashForm");
			//autoLoad = true;
		}else if(showCategoryTree){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@categoryItem/@mainSashForm");
			//autoLoad = true;
		}else if(showDescBrowser && descBrowserRight){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@dscItem1/@topSashForm");
			//autoLoad = true;
		}else if(showDescBrowser){
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@dscItem/@topSashForm");
			//autoLoad = true;
		}else{
			controlThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@treeItem/@thingTree");
			//autoLoad = true;
		}
		Thing actionsThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@actions");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("controlThing", self);
		ac.put("parentContext", actionContext);
		Designer.pushCreator(self);
		try{
			control = (Control) controlThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		ac.g().put("rootControl", control);
		String desc = (String) self.doAction("getDescritporForNewThing", actionContext);
		ac.put("descritporForNewThing", desc);
		ac.put("contentDefaultOpenMethod", self.doAction("getContentDefaultOpenMethod", actionContext));
		ac.put("forceUseDefaultOpenMethod", self.doAction("isForceUseDefaultOpenMethod", actionContext));
		Thing defaultViewer = self.doAction("getDefaultViewer", actionContext);
		ac.put("defaultViewer", defaultViewer);
		
		ActionContainer actions = (ActionContainer) actionsThing.doAction("create", ac);
		actions.doAction("init", ac, UtilMap.toMap(new Object[]{"thing", thing, "type", registType, "autoLoad", autoLoad, "keywords", keywords}));
		
		String sashFormWeights = self.getStringBlankAsNull("sashFormWeights");
		if(sashFormWeights != null){
			SashForm sashForm = ac.getObject("mainSashForm");
			if(sashForm != null){
			    try{
				   sashForm.setWeights(UtilString.toIntArray(sashFormWeights));
			    }catch(Exception e){
			        e.printStackTrace();
			    }
			}
		}
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", control);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//绑定树的修改分组的弹出菜单，如果树存在的话
		Tree thingTree = ac.getObject("thingTree");
		if(thingTree != null){
			ac.peek().put("parent", thingTree);
			Thing menu = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@thingTreePopMenu");
			Designer.pushCreator(self);
			try{
				Menu popMenu = menu.doAction("create", ac);
				
				SplitButton homeSplitButton = ac.getObject("homeButton");
				if(homeSplitButton != null){
					homeSplitButton.setMenu(popMenu);
				}
			}finally{
				Designer.popCreator();
			}
		}
			
		Designer.attachCreator(control, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), actions);
		
		return control;
	}
}
