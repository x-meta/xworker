package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeCursor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.app.view.swt.data.events.SwtStoreUtils;
import xworker.swt.custom.TreeCursorEditor;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

@ActionClass(creator="getInstance")
public class ThingPropertiesEditor {
	public static final String EDITOR = "editor";
	
	Thing self;
	Tree tree;
	ActionContext actionContext;
	//正在编辑的事物
	Thing editThing;
	Thing descriptor;
	Thing attributeEditor = new Thing("xworker.swt.custom.tableEditors.AttributeEditor");
	TreeCursor cursor;
	boolean showDescriptor = false;
	
	public ThingPropertiesEditor(Thing self, Tree tree, TreeCursor cursor, ActionContext actionContext) {
		this.self = self;
		this.tree = tree;
		this.actionContext = actionContext;
		this.cursor = cursor;
		
		editThing = self.doAction("getThing", actionContext);
		showDescriptor = UtilData.isTrue(self.doAction("isShowDescriptor", actionContext));
		if(editThing != null) {
			setEditThing(editThing);
		}
		
		Thing descriptor = self.doAction("getDescriptor", actionContext);		
		if(descriptor != null) {
			if(editThing == null) {
				editThing = new Thing(descriptor.getMetadata().getPath());
				setEditThing(editThing);
			}else {
				setDescriptor(descriptor);
			}
		}
	}
	
	public void setEditThing(Thing editThing) {
		this.editThing = editThing;
		
		tree.removeAll();
		
		//第一个节点是描述者选择节点
		TreeItem item = null;
		if(showDescriptor) {
			item = new TreeItem(tree, SWT.NONE);
			item.setText(UtilString.getString("lang:d=描述者&en=Descriptors", actionContext));
			item.setBackground(0, tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));			
		}
		
		//描述者的编辑属性事物
		Thing descAttr = new Thing("xworker.lang.MetaDescriptor3/@attribute");
		descAttr.set("inputtype", "select");
		for(Thing desc : editThing.getAllDescriptors()) {
			Thing value = new Thing("xworker.lang.MetaDescriptor3/@attribute/@value");
			value.set("label", desc.getMetadata().getLabel());
			value.set("value", desc.getMetadata().getPath());
			descAttr.addChild(value);
		}
		if(item != null) {
			item.setData(descAttr);
		}
		
		setDescriptor(editThing.getDescriptor());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setDescriptor(Thing descriptor) {
		this.descriptor = descriptor;
		if(showDescriptor) {
			tree.getItem(0).setText(1, descriptor.getMetadata().getLabel());
		}
		
		//取消属性的Item
		TreeItem items[] = tree.getItems();
		for(int i=1; i<items.length; i++) {
			items[i].dispose();
		}
		
		List<Thing> fs = new ArrayList<Thing>();
		
		//过滤相同名称的属性
		for(Thing f : descriptor.getAllChilds("attribute")){
			if(!ThingDescriptorForm.isPublic(f)) {
				continue;
			}
			
			boolean have = false;
			for(Thing ff : fs){
				if(f.getMetadata().getName().equals(ff.getMetadata().getName())){
					have = true; 
					break;
				}
			}
			
			if(!have){
				fs.add(f);
			}
		}
		
		//去掉隐藏字段
		for(int i=0; i<fs.size();i++){
			Thing f = fs.get(i);
			if("hidden".equals(f.getString("inputtype"))){
				fs.remove(i);
				i--;
	        }
		}
		
		//字段的分组
		List<Thing> rootList = new ArrayList<Thing>();
		List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
		for(Thing f : fs){
			String group = f.getString("group");
		    int groupIndex = f.getInt("groupIndex", 0);
		    if(group == null ||"".equals(group)){
		    	rootList.add(f);		        
		    }else{
		    	Map<String, Object> gp = null;
		        for(Map<String, Object> g : groups){
		        	if(g.get("name").equals(group)){
		        		gp = g;
		        		break;
		        	}
		        }
		        
		        if(gp == null){
		        	gp = new HashMap<String, Object>();
		        	gp.put("name", group);
		        	gp.put("groupIndex", groupIndex);
		        	gp.put("childs", new ArrayList<Thing>());
		        	groups.add(gp);
		        }
		        
		        ((List<Thing>) gp.get("childs")).add(f);
		    }
		}
		//字段分组排序
		Collections.sort(groups, new Comparator(){
			public int compare(Object o1, Object o2) {
				Map<String, Object> m1 = (Map<String, Object>) o1;
				Map<String, Object> m2 = (Map<String, Object>) o2;
				
				Integer i1 = (Integer) m1.get("groupIndex");
				Integer i2 = (Integer) m2.get("groupIndex");
				
				if(i1 < i2){
					return -1;
				}else if(i1.equals(i2)){
					return 0;
				}else{
					return 1;
				}
			}			
		});
		
		if(groups.size() != 0){		
			//基础属性
			if(rootList.size() > 0) {
				TreeItem baseItem = new TreeItem(tree, SWT.NONE);
				baseItem.setText(UtilString.getString("res:res.w_exp:baseAttribute:基础属性", actionContext));
				baseItem.setBackground(tree.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
				createItems(rootList, baseItem);
				baseItem.setExpanded(true);
			}
			
			//创建其它分组
			for(int i=0; i<groups.size(); i++){
				TreeItem groupItem = new TreeItem(tree, SWT.NONE);
				groupItem.setText((String) groups.get(i).get("name"));
				groupItem.setBackground(tree.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
				
				createItems( (List<Thing>) groups.get(i).get("childs"), groupItem);
				groupItem.setExpanded(true);
			}			
			if(tree.getItemCount() > 0) {
				TreeItem group1Item = tree.getItem(0);
				if(showDescriptor) {
					cursor.setSelection(group1Item, 1);
				}else {
					cursor.setSelection(group1Item.getItem(0), 1);
				}
				tree.showItem(tree.getItem(0));
			}
		}else {
			//没有分组，直接创建到树下面
			createItems(rootList, tree);
			cursor.setSelection(tree.getItem(0), 1);
		}						
		
	}
	
	private void createItems(List<Thing> attributes, Tree parent) {
		for(Thing attr : attributes) {
			TreeItem item = new TreeItem(parent, SWT.NONE);
			initItem(attr, item);
		}
	}
	
	private void createItems(List<Thing> attributes, TreeItem parent) {
		for(Thing attr : attributes) {
			TreeItem item = new TreeItem(parent, SWT.NONE);
			initItem(attr, item);
		}
	}
	
	private void initItem(Thing attribute, TreeItem item) {
		//item.setBackground(0, tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		item.setData(attribute);
		String name = attribute.getMetadata().getName();
		String label = attribute.getMetadata().getLabel();
		String value = editThing.getStringBlankAsNull(name);
		if(value == null) {
			item.setText(new String[] {label, ""});
		}else {
			item.setText(new String[] {label, SwtStoreUtils.getDisplayText(editThing, value, attribute, actionContext)});
		}
	}
	
	@ActionParams(names="item, column")
	public Object getEditorThing(TreeItem item, int column, ActionContext actionContext) {
		if(column == 1) {
			Thing attribute = (Thing) item.getData();
			if(attribute != null) {
				attributeEditor.set("attributeThing", attribute.getMetadata().getPath());
				return attributeEditor;
			}
		}
		
		return null;
	}
	
	@ActionParams(names="item,column,value")
	public void setValue(TreeItem item, int column, Object value) {
		if(showDescriptor && item == tree.getItem(0)) {
			//根节点是描述者的节点，设置新的描述者
			Thing desc = World.getInstance().getThing(String.valueOf(value));
			if(desc != null) {
				this.setDescriptor(desc);
				item.setText(1, desc.getMetadata().getLabel());
			}
		}else {
			//设置属性的值
			Thing attribute = (Thing) item.getData();
			if(attribute != null) {
				String name = attribute.getMetadata().getName();
				editThing.set(name, value);
				Object v = editThing.get(name);
				if(v == null) {
					item.setText(1, "");
				}else {
					item.setText(1, SwtStoreUtils.getDisplayText(attribute, v, attribute, actionContext));
				}
				
				self.doAction("valueChanged", actionContext);
			}
		}
	}
	
	@ActionParams(names="item,column")
	public Object getValue(TreeItem item, int column) {
		if(item == tree.getItem(0)) {
			return descriptor.getMetadata().getLabel();
		}else {
			//设置属性的值
			Thing attribute = (Thing) item.getData();
			if(attribute != null) {
				String name = attribute.getMetadata().getName();
				return editThing.get(name);
			}
		}
		return null;
	}
	
	public void onSelection(ActionContext actionContext) {
		self.doAction("onSelection", actionContext);
	}
	
	/**
	 * 刷新，重新设置值
	 */
	public void refresh() {
		for(TreeItem item : tree.getItems()) {
			refreshItem(item);
		}
	}
	
	private void refreshItem(TreeItem item) {
		if(item == tree.getItem(0)) {
			return;
		}
		
		Thing attribute = (Thing)item.getData();
		if(attribute != null) {
			String name = attribute.getMetadata().getName();
			String label = attribute.getMetadata().getLabel();
			String value = editThing.getStringBlankAsNull(name);
			if(value == null) {
				item.setText(new String[] {label, value});
			}else {
				item.setText(new String[] {label, ""});
			}
		}else {
			refreshItem(item);
		}
	}
	
	public static Object create(ActionContext actionContext) {
		World world = World.getInstance();
		Thing self = actionContext.getObject("self");

		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(world.getThing("xworker.swt.xworker.prototype.ThingPropertyEditorShell/@tree"));
		Tree tree = cc.create();
		/*
		//创建控件
		Thing treeThing = ;
		Tree tree = treeThing.doAction("create", actionContext);
		
		//创建子节点
		actionContext.peek().put("parent", tree);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}*/
		
		TreeCursorEditor cursorEditor = cc.getNewActionContext().getObject("treeCursorEditor");
		TreeCursor cursor = (TreeCursor) cursorEditor.getCursor();
		cursor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TreeCursor cursor = (TreeCursor) event.widget;
				if(cursor.getColumn() == 0) {
					cursor.setSelection(cursor.getRow(), 1);
				}
				//System.out.println("tree curosr selection " + event.widget);
			}			
		});
		
		ThingPropertiesEditor editor = new ThingPropertiesEditor(self, tree, cursor, actionContext);
		tree.setData(EDITOR, editor);
		editor.cursor = cursor;
		actionContext.g().put(self.getMetadata().getName(), editor);
		
		return tree;
	}
	
	public static ThingPropertiesEditor getInstance(ActionContext actionContext) {
		Tree tree = actionContext.getObject("tree");
		
		return (ThingPropertiesEditor) tree.getData(EDITOR);
	}

}
