package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import ognl.OgnlException;

public class TreeActions {
	public static Tree create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		Skin skin = null;
		skin = (Skin) actionContext.get(self.getString("skin"));
		
		String styleName = self.getStringBlankAsNull("styleName");
		
		Tree widget = null;
		if(styleName == null){
			widget = new Tree(skin);
		}else{
			widget = new Tree(skin, styleName);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), widget);
		init(self, widget, actionContext);		
		
		return widget;
	}
	
	public static Tree.Node createTreeNode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Actor actor = null;
		Thing actorThing = self.getThing("Actor@0");
		if(actorThing != null && actorThing.getChilds().size() > 0){
			actor = (Actor) actorThing.doAction("create", actionContext);
		}
		if(actor == null){
			throw new ActionException("Tree.Node: Actor is null, path=" + self.getMetadata().getPath());
		}	
		
		Tree.Node node= new Tree.Node(actor) {
			
		};
		if(self.getStringBlankAsNull("expanded") != null){
			node.setExpanded(self.getBoolean("expanded"));
		}
	
		if(self.getStringBlankAsNull("icon") != null){
			Drawable icon = (Drawable) actionContext.get("icon");
			if(icon != null){
				node.setIcon(icon);
			}
		}
		
		if(self.getStringBlankAsNull("object") != null){
			Object object = (Object) actionContext.get("object");
			if(object != null){
				node.setValue(object);
				//node.setObject(object);
			}
		}
		
		if(self.getStringBlankAsNull("selectable") != null){
			node.setSelectable(self.getBoolean("selectable"));
		}
		
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", node);
			for(Thing child : self.getChilds()){
				if(child.getThingName().equals("Actor")){
					continue;
				}
				
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Tree.Node){
					node.add((Tree.Node) obj);
				}
			}
		}finally{
			actionContext.pop();
		}
		
		return node;
	}
	
	public static void createTreeNodeIcon(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Tree.Node node = (Tree.Node) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Drawable){
				node.setIcon((Drawable) obj);
				break;
			}
		}
	}
	
	public static void createNewRow(ActionContext actionContext){
		Table table = (Table) actionContext.get("parent");
		table.row();
	}
	
	public static void createBlankCell(ActionContext actionContext){
		Table table = (Table) actionContext.get("parent");
		table.add();
	}
	
	public static void init(Thing self, Tree item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("iconSpacingX") != null && self.getStringBlankAsNull("iconSpacingY") !=null){
			item.setIconSpacing(self.getFloat("iconSpacingX", 0, actionContext), 
					self.getFloat("iconSpacingY", 0, actionContext));			
		}
		
		//item.setMultiSelect(self.getBoolean("multiSelect"));
		
		if(self.getStringBlankAsNull("padding") != null){
			item.setPadding(self.getFloat("padding", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("ySpacing") != null){
			item.setYSpacing(self.getFloat("ySpacing", 0, actionContext));
		}
	}
}
