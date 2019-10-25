package xworker.app.view.swt.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.lang.actions.ActionContainer;
import xworker.swt.design.Designer;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

public class DataObjectEditor {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Control control = null;
		Designer.pushCreator(self);
		try {
			if(checkFolder(self, "BottomTabFolder") || checkFolder(self, "RightTabFodler")){
				control = self.doAction("createSashForm", actionContext);
			}else{
				control = self.doAction("createEditor", actionContext, "editorRoot", true);
			}
			
			//创建子节点
			actionContext.peek().put("parent", control);
		    for(Thing child : self.getChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally {
			Designer.popCreator();
		}
		
		Designer.attach(control, self.getMetadata().getPath(), actionContext);
		Designer.attachCreator(control, self.getMetadata().getPath(), actionContext);
		return control;
	}
	
	public static boolean checkFolder(Thing self, String name){
	    Thing tab = self.getThing(name + "@0");
	    if(tab != null && tab.getChilds().size() > 0){
	        return true;
	    }else{
	        return false;
	    }
	}
	
	public static Object createEditor(ActionContext actionContext){
		Composite parent = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		//创建一个空的布局
		int style = SWT.NONE;
		if(UtilData.isTrue(actionContext.get("DataObjectEditorBorder")) == true){
		    style = style | SWT.BORDER;
		}
		
		Composite composite = new Composite(parent, style);
		composite.setLayout(new FillLayout());

		//创建事物
		Thing thing = self.detach();
		ThingUtils.removeActions(thing);

		//变量上下文
		ActionContext dataObjectContext = new ActionContext();
		dataObjectContext.put("parent", composite);
		dataObjectContext.put("parentContext", actionContext);
		dataObjectContext.getScope(0).put("editorThing", self);
		if(self.getStringBlankAsNull("actionContext") != null){
		    actionContext.getScope(0).put(self.getString("actionContext"), dataObjectContext);
		}

		//println("create:" + dataObjectContext.hashCode());
		thing.setData("actionContext", dataObjectContext);
		thing.setData("composite", composite);
		thing.setData("parentContext", actionContext);
		thing.setData("self", self);


		//配置
		Thing dataObject = world.getThing(self.getString("dataObject"));
		if(dataObject == null){
			Thing dataObjects = self.getThing("dataObjects@0");
		    if(dataObjects != null && dataObjects.getChilds().size() > 0){
		        dataObject = dataObjects.getChilds().get(0);
		    }
		}
		
		if(dataObject == null){
		    //log.warn("QueryTalbeComposite: dataObject is null, dataObject=" + self.dataObject);
		}else{
		    thing.doAction("setDataObject", actionContext, "dataObject", dataObject);
		}
		

		actionContext.getScope(0).put(self.getMetadata().getName(), thing);
		if(actionContext.getBoolean("editorRoot") == true) {
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		}else {
			Designer.attachCreator(composite, self.getMetadata().getPath(), false, actionContext);
		}
		//println("create2:" + dataObjectContext.hashCode());
		return composite;
	}
	
	public static Object createSashForm(ActionContext actionContext){
		Composite parent = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		//World world = World.getInstance();

		//创建SashForm
		Thing bottom = self.getThing("BottomTabFolder@0");
		if(bottom != null && bottom.getChilds().size() == 0){
		    bottom = null;
		}

		Thing right = self.getThing("RightTabFodler@0");
		if(right != null && right.getChilds().size() == 0){
		    right = null;
		}

		if(bottom != null && right != null){
			SashForm mainSash = new SashForm(parent, SWT.VERTICAL);			
			SashForm topSash = new SashForm(mainSash, SWT.HORIZONTAL);
		    createEditor(topSash, self, actionContext);
		    createTabFolder(topSash, right, actionContext);
		    createTabFolder(mainSash, bottom, actionContext);
		    setWeights(topSash, right, actionContext);
		    setWeights(mainSash, bottom, actionContext);
		    return mainSash;
		}else if(bottom != null){
			SashForm mainSash = new SashForm(parent, SWT.VERTICAL);
			createEditor(mainSash, self, actionContext);
		    createTabFolder(mainSash, bottom, actionContext);
		    setWeights(mainSash, bottom, actionContext);
		    return mainSash;
		}else if(right != null){
			SashForm mainSash = new SashForm(parent, SWT.HORIZONTAL);
			createEditor(mainSash, self, actionContext);
		    createTabFolder(mainSash, right, actionContext);
		    setWeights(mainSash, right, actionContext);
		    return mainSash;
		}else{
			return null;
		}
		
	}
	
	public static void createTabFolder(SashForm sash, Thing tabItems, ActionContext actionContext){
	     //创建TabFolder
	    int tabStyle = SWT.TOP | SWT.BORDER | SWT.FLAT;
	    Thing self = actionContext.getObject("self");
	    CTabFolder tab = new CTabFolder(sash, tabStyle);
	    Designer.attach(tab, self.getMetadata().getPath(), actionContext);
	    for(Thing child : tabItems.getChilds()){
	    	CTabItem tabItem = new CTabItem(tab, SWT.NONE);
	        tabItem.setText(child.getMetadata().getLabel());
	        Control control = (Control) child.doAction("create", actionContext, "parent",  tab);
	        if(control != null){
	            tabItem.setControl(control);
	        }
	    }
	    tab.setSelection(0);
	}
	
	public static void createEditor(SashForm sash, Thing self, ActionContext actionContext){
	    self.doAction("createEditor", actionContext, "DataObjectEditorBorder", true, "parent",  sash, "editorRoot", false);
	}
	
	public static void setWeights(SashForm sash, Thing tabItems, ActionContext actionContext){
	    String weights = tabItems.getStringBlankAsNull("sashFormWeights");
	    if(weights != null){
	        try{
	            sash.setWeights(UtilString.toIntArray(weights));
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	}
	
	public static void setDataObject(ActionContext actionContext){
		//Composite parent = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		Thing thing = self;
		self = (Thing) self.getData("self");
		
		Designer.pushCreator(self);
		try {
			World world = World.getInstance();
			Thing dataObject = actionContext.getObject("dataObject");
			
			
	
			Thing queryDataObject = world.getThing(self.getString("queryDataObject"));
			if(queryDataObject == null){
			    Thing queryDataObjects = self.getThing("queryDataObjects@0");
			    if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
			        queryDataObject = queryDataObjects.getChilds().get(0);
			    }
			    //log.info("query=" + queryDataObject + ",queryDataObjects=" + queryDataObjects);
			}
	
			if(queryDataObject == null){    
			    Thing queryDataObjects = dataObject.getThing("QueryFormDataObject@0");
			    if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
			        queryDataObject = queryDataObjects.getChilds().get(0);
			    }
			}
	
			if(queryDataObject == null){
			    queryDataObject = dataObject;
			}
			Thing queryConfig = world.getThing(self.getString("queryConfig"));
			if(queryConfig == null){
			    queryConfig = self.getThing("queryConfig@0");
			}
	
			//数据对象编辑所在变量上下文
			ActionContext editorContext = (ActionContext) thing.getData("actionContext");
			editorContext.put("dataObject", dataObject);
			editorContext.put("queryConfig", queryConfig);
			editorContext.put("queryDataObject", queryDataObject);
	
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("dataObject", dataObject.getMetadata().getPath());
			data.put("label", dataObject.getMetadata().getLabel());
			if(queryConfig != null){
			    data.put("queryConfig", queryConfig.getMetadata().getPath());
			}else{
			    data.put("queryConfig", dataObject.getStringBlankAsNull("defaultQueryConfig"));
			    if(data.get("queryConfig") == null){
			        queryConfig = dataObject.getThing("Condition@0");
			        if(queryConfig != null){
			            data.put("queryConfig", queryConfig.getMetadata().getPath());
			        }        
			    }
			    
			    if(data.get("queryConfig") == null){
			        data.put("queryConfig", "");
			    }
			}
			data.put("queryDataObject", queryDataObject.getMetadata().getPath());
			data.put("queryButton", self.getBoolean("queryButton"));
			data.put("newButton", self.getBoolean("newButton"));
			data.put("editButton", self.getBoolean("editButton"));
			data.put("editBatchButton", self.getBoolean("editBatchButton"));
			data.put("deleteButton", self.getBoolean("deleteButton"));
			data.put("pagingToolbar", self.getBoolean("pagingToolbar"));
			data.put("tableCheck", self.getString("tableCheck"));
			data.put("queryFormEditCols", self.getString("queryFormEditCols"));
			data.put("selectAllButton", self.getBoolean("selectAllButton"));
			data.put("selectRerverseButton", self.getBoolean("selectRerverseButton"));
			data.put("newDataObjectInitValues", self.getString("newDataObjectInitValues"));
			data.put("toolItems", self.getChilds("ToolItems"));
			data.put("toolsButton", self.getBoolean("toolsButton"));
			data.put("tableBorder", self.getString("tableBorder"));
			data.put("initValues", self.getString("initValues"));
			//和选择相关的Item
			List<String> selectItems = new ArrayList<String>();
			selectItems.add("editToolItem");
			selectItems.add("deleteToolItem");
			for(Thing toolItems : self.getChilds("ToolItems")){
			    for(Thing item : toolItems.getChilds("Item")){
			        getEnableItems(item, selectItems);
			    }
			}
			data.put("selectItems", selectItems);
	
			Thing buttons = self.getThing("Buttons@0");
			if(buttons != null){
			    data.put("buttons", buttons.getChilds());
			}else{
			    data.put("buttons", Collections.emptyList());
			}
	
			//log.info("data=" + data);
			//通过模板生成Composite
			Thing template = world.getThing("xworker.app.view.swt.widgets.prototype.DataObjectEditorNoChildsTpl");
			ActionContext ac = new ActionContext();
			ac.put("data", data);
			Thing compositeThing = ((Thing) template.doAction("process", ac)).getChilds().get(0);
			if(compositeThing != null){
			    if(self.getBoolean("tableCheck")){
			    }
			    if(self.getBoolean("debug")){
			        compositeThing.set("label", self.getMetadata().getLabel());
			        XWorkerUtils.ideOpenThing(compositeThing);
			    }
			    ActionContext dataObjectContext = (ActionContext) thing.getData("actionContext");  
			    //情况原有的内容
			    Composite composite = (Composite) thing.getData("composite");  
			    for(Control child : composite.getChildren()){
			        child.dispose();
			    }
			    dataObjectContext.peek().put("parent", composite);
			    dataObjectContext.getScope(0).put("parentContext", thing.getData("parentContext"));
			    compositeThing.doAction("create", dataObjectContext);
			    //有些功能取自DataObjectEditorComposite，那里的table用dataTable变量存储
			    dataObjectContext.put("dataTable", dataObjectContext.get("table"));
			    dataObjectContext.put("dataObjectComposite", dataObjectContext.get("composite"));
			    //println("setDAtaObject:" + dataObjectContext.hashCode());
			    ActionContext parentContext = (ActionContext) thing.getData("parentContext");
			    try{
			        Bindings bindings = parentContext.push(null);
			        bindings.put("parent", dataObjectContext.get("buttonComposite"));
			        for(Thing child : (List<Thing>) self.getChilds("Buttons")){
			            for(Thing c : child.getChilds()){
			                c.doAction("create", parentContext);
			            }
			        }
			        
			        composite.layout();
			    }finally{
			        parentContext.pop();
			    }
			}
		}finally {
			Designer.popCreator();
		}
	}
	
	public static void getEnableItems(Thing item, List<String> list){
	    if(item.getBoolean("enableOnSelect")){
	        list.add(item.getMetadata().getName());
	    }
	    
	    for(Thing childItem : item.getChilds("Item")){
	        getEnableItems(childItem, list);
	    }
	}
	
	public static void query(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Thing thing = self;
		self = (Thing) self.getData("self");

		//设置查询参数
		ActionContext ac = (ActionContext) thing.getData("actionContext");
		ac.g().put("queryValues", actionContext.get("values"));

		//查询
		((Thing) ac.get("dataStore")).doAction("load", ac, "params", ac.get("queryValues"));
	}
	
	public static void setInitValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thing = self;
		self = (Thing) self.getData("self");

		//初始化变量的名字
		String initName = self.getString("initValues");
		
		//设置查询参数
		ActionContext ac = (ActionContext) thing.getData("actionContext");
		ac.g().put(initName, actionContext.get("values"));
	}
	
	@SuppressWarnings("unchecked")
	public static void parentSelected(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		ActionContext ac = (ActionContext) self.getData("actionContext");

		self = (Thing) self.getData("self");
		String dataToQueryValues = self.getStringBlankAsNull("dataToQueryValues");
		Map<String, Object> data = (Map<String, Object>) actionContext.get("data");
		String dataToInitValues = self.getStringBlankAsNull("dataToInitValues");
		String initValues = self.getStringBlankAsNull("initValues");
		ac.g().put("queryValues", convertValues(dataToQueryValues, data));
		if(initValues != null){
		    ac.g().put(initValues, convertValues(dataToInitValues, data));    
		}
		((Thing) ac.get("dataStore")).doAction("load", ac, "params", ac.g().get("queryValues"));

		//设置按钮状态
		if(actionContext.get("data") != null){
		    setItemStatus((ToolItem) ac.get("searchToolItem"), true);
		    setItemStatus((ToolItem) ac.get("addToolItem"), true);
		    setItemStatus((ToolItem) ac.get("toolsItem"), true);
		}else{
		    setItemStatus((ToolItem) ac.get("searchToolItem"), false);
		    setItemStatus((ToolItem) ac.get("addToolItem"), false);
		    setItemStatus((ToolItem) ac.get("toolsItem"), true);
		}
		
	}
	
	public static void setItemStatus(ToolItem item, boolean status){
	     if(item != null){
	         item.setEnabled(status);
	     }
	}
	
	public static Map<String, Object> convertValues(String valuesConfig, Map<String, Object> data){
	    Map<String, Object> values = new HashMap<String, Object>();   
	    if(valuesConfig != null && data != null){
	        for(String qs : valuesConfig.split("[,]")){
	            String[] q = qs.split("[:]");
	            if(q.length == 2){
	                 values.put(q[0].trim(), data.get(q[1].trim()));
	            }
	        }
	    }
	    //println(valuesConfig + "=" + values);
	    return values;
	}
	
	public static void tpl_openQueryWindow(ActionContext actionContext){
		ToolBar toolBar = actionContext.getObject("toolBar");
		ToolItem searchToolItem = actionContext.getObject("searchToolItem");
		Shell shell = toolBar.getShell();
		Thing dataStore = actionContext.getObject("dataStore");
		World world = World.getInstance();

		//创建查询窗口
		Thing thing = world.getThing("xworker.app.view.swt.widgets.prototype.QueryWindow");
		ActionContext ac = new ActionContext();
		ac.put("parent", shell);
		ac.put("dataStore", dataStore);
		ac.put("parentContext", actionContext);
		ac.put("dataObject", actionContext.get("queryDataObject"));

		//设置查询数据对象
		Shell newShell = (Shell) thing.doAction("create", ac);
		if(actionContext.get("queryDataObject") != null){
		     ((Thing) ac.get("form")).doAction("setDataObject", ac, "dataObject", actionContext.get("queryDataObject"));
		     if(actionContext.get("queryValues") != null){
		    	 ((Thing) ac.get("form")).doAction("setValues", ac, "values", actionContext.get("queryValues"));
		     }
		     newShell.pack();
		}

		//显示窗口
		Rectangle rect = searchToolItem.getBounds();
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = searchToolItem.getParent().toDisplay(pt);

		newShell.setLocation(pt.x, pt.y);
							    //menu.update();
		newShell.setVisible(true);
	}
	
	public static void tpl_noneselected(ActionContext actionContext){
		Thing editorThing = actionContext.getObject("editorThing");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Table table = actionContext.getObject("table");
		
		//需要传入的参数
		Object queryValues = actionContext.get("queryValues");
		//println("queryValuesxx=" + queryValues);
		Object data = null;

		callItems(table, editorThing.getThing("BottomTabFolder@0"), queryValues, data, actionContext, parentContext);
		callItems(table, editorThing.getThing("RightTabFodler@0"), queryValues, data, actionContext, parentContext);
	}
	
	public static void callItems(Table table, Thing tabItems, final Object queryValues, final Object data, final ActionContext actionContext, ActionContext parentContext){
	    if(tabItems != null){
	        for(Thing item : tabItems.getChilds()){
	            final Object obj = parentContext.get(item.getMetadata().getName());
	        
	            if(obj instanceof Thing || obj instanceof ActionContainer){
	                table.getDisplay().asyncExec(new Runnable(){
	                	public void run(){
	                		if(obj instanceof Thing){
	                			((Thing) obj).doAction("parentSelected", actionContext, "queryValues", queryValues, "data", data, "dataStore", actionContext.get("dataStore"));
	                		}else if(obj instanceof Thing){
	                			((ActionContainer) obj).doAction("parentSelected", actionContext, "queryValues", queryValues, "data", data, "dataStore", actionContext.get("dataStore"));
	                		}
	                	}
	                });
	            }
	        }
	    }
	}
	
	public static void tpl_tableSelected(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		Thing editorThing = actionContext.getObject("editorThing");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Table table = actionContext.getObject("table");
		
		//需要传入的参数
		Object queryValues = actionContext.get("queryValues");
		Object data = event.item.getData();

		callItems(table, editorThing.getThing("BottomTabFolder@0"), queryValues, data, actionContext, parentContext);
		callItems(table, editorThing.getThing("RightTabFodler@0"), queryValues, data, actionContext, parentContext);
	}
	
}
