package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import ognl.OgnlException;
import xworker.libdgx.ConstantsUtils;
import xworker.libdgx.ConstructException;
import xworker.libdgx.utils.AlignActions;

public class TableActions {
	public static Table create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		String constructor = self.getString("constructor");
		Table item = null;
		if("default".equals(constructor)){
			item = new Table();
		}else if("skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new Table(skin);			
		}else{
			throw new ConstructException(self);
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);		

		return item;
	}
	
	public static void childActorCreated(ActionContext actionContext){
		Table parent = (Table) actionContext.get("parent");
		Actor actor = (Actor) actionContext.get("actor");
		parent.add(actor);
	}
	
	@SuppressWarnings("rawtypes")
	public static void createDefaults(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Table parent = (Table) actionContext.get("parent");
		Cell cell = parent.defaults();
		initCell(self, cell, actionContext);
	}
	public static void createText(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Table table = (Table) actionContext.get("parent");
		String text = self.getStringBlankAsNull("text");
		String fontName = self.getStringBlankAsNull("fontName");
		String labelStyleName = self.getStringBlankAsNull("labelStyleName");
		if(text == null){
			return;
		}
		
		Cell<?> cell = null;
		if(labelStyleName == null && fontName == null){
			cell = table.add(text);
		}else if(fontName == null){
			cell = table.add(text, labelStyleName);
		}else{
			cell = table.add(text, fontName, labelStyleName);
		}
		if(cell != null){
			initCell(self, cell, actionContext);
		}
	}
	
	public static void createActorCell(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Table table = (Table) actionContext.get("parent");
		
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		if(actor == null){
			Thing actors = self.getThing("Actors@0");
			if(actors != null && actors.getChilds().size() > 0){
				actor = (Actor) actors.getChilds().get(0).doAction("create", actionContext);
			}
		}
		if(actor != null){
			Cell<?> cell = table.add(actor);
			initCell(self, cell, actionContext);
		}		
	}
	
	public static void createNewRow(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Table table = (Table) actionContext.get("parent");
		Cell<?> cell = table.row();
		initCell(self, cell, actionContext);
	}
	
	public static void createBlankCell(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Table table = (Table) actionContext.get("parent");
		Cell<?> cell = table.add();
		initCell(self, cell, actionContext);
	}
	
	public static void init(Thing self, Table item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		String align = self.getStringBlankAsNull("align");
		if(align != null){
			item.align(ConstantsUtils.getAlign(align));
		}
		
		if(self.getStringBlankAsNull("columnDefaults") != null){
			item.columnDefaults(self.getInt("columnDefaults", 0, actionContext));
		}
		
		if(self.getBoolean("debug")){
			item.debug();
		}	
		
		if(self.getBoolean("debugCell")){
			item.debugCell();
		}	
		
		if(self.getBoolean("debugTable")){
			item.debugTable();
		}
		
		if(self.getBoolean("debugWidget")){
			//item.debugWidget();
		}
		
		if(self.getBoolean("defaults")){
			item.defaults();
		}
		
		if(self.getStringBlankAsNull("pad") != null){
			item.pad(self.getFloat("pad", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padBottom") != null){
			item.padBottom(self.getFloat("padBottom", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padLeft") != null){
			item.padLeft(self.getFloat("padLeft", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padRight") != null){
			item.padRight(self.getFloat("padRight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padTop") != null){
			item.padTop(self.getFloat("padTop", 0, actionContext));
		}
		
		String background = self.getStringBlankAsNull("background");
		if(background != null){
			Drawable drawable = (Drawable) actionContext.get(background);
			item.setBackground(drawable);
		}
		
		String backgroundDrawableName = self.getStringBlankAsNull("backgroundDrawableName");
		if(backgroundDrawableName != null){
			item.setBackground(backgroundDrawableName);
		}
		
		if(self.getStringBlankAsNull("clip") != null){
			item.setClip(self.getBoolean("clip", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("round") != null){
			item.setRound(self.getBoolean("round", false, actionContext));
		}		
		
		if(self.getBoolean("pack")){
			item.pack();
		}
		
		if(self.getBoolean("layout")){
			item.layout();
		}
	}
	
	public static void initCell(Thing self, Cell<?> cell, ActionContext actionContext) throws OgnlException{
		if(self.getStringBlankAsNull("align") != null){
			cell.align(AlignActions.getAlign(self.getString("align", "", actionContext)));
		}
		
		if(self.getStringBlankAsNull("fill") != null){
			cell.fill();
		}
		
		if(self.getStringBlankAsNull("colspan") != null){
			cell.colspan(self.getInt("colspan", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("expandX") != null){
			cell.expandX();
		}
		
		if(self.getStringBlankAsNull("expandY") != null){
			cell.expandY();
		}
		
	
		
		if(self.getStringBlankAsNull("fillX") != null){
			cell.fillX();
		}
		
		if(self.getStringBlankAsNull("fillY") != null){
			cell.fillY();
		}
		
		if(self.getStringBlankAsNull("height") != null){
			cell.height(self.getFloat("height", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxHeight") != null){
			cell.maxHeight(self.getFloat("maxHeight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("size") != null){
			cell.size(self.getFloat("size", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxSize") != null){
			cell.maxSize(self.getFloat("maxSize", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("width") != null){
			cell.width(self.getFloat("width", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxWidth") != null){
			cell.maxWidth(self.getFloat("maxWidth", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padBottom") != null){
			cell.padBottom(self.getFloat("padBottom", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padLeft") != null){
			cell.padLeft(self.getFloat("padLeft", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padRight") != null){
			cell.padRight(self.getFloat("padRight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padTop") != null){
			cell.padTop(self.getFloat("padTop", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("prefHeight") != null){
			cell.prefHeight(self.getFloat("prefHeight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("prefSize") != null){
			cell.prefSize(self.getFloat("prefSize", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("prefWidth") != null){
			cell.prefWidth(self.getFloat("prefWidth", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("spaceBottom") != null){
			cell.spaceBottom(self.getFloat("spaceBottom", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("spaceLeft") != null){
			cell.spaceLeft(self.getFloat("spaceLeft", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("spaceRight") != null){
			cell.spaceRight(self.getFloat("spaceRight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("spaceTop") != null){
			cell.spaceTop(self.getFloat("spaceTop", 0, actionContext));
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), cell);
	}
}
