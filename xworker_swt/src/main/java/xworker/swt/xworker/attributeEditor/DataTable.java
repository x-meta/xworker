package xworker.swt.xworker.attributeEditor;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilString;

import xworker.dataObject.DataObject;
import xworker.swt.xworker.AttributeEditor;

public class DataTable {
	@SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();		

		// 为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentActionContext", actionContext);

		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;

		// 输入参数
		String inputattrs = attribute.getStringBlankAsNull("inputattrs");
		if (inputattrs == null) {
			Thing thing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.SetinputattrsInfo/@setInpuattrsLabel");
			return thing.doAction("create", context);
			//return null;
		}

		Map<String, String> params = UtilString.getParams(inputattrs);
		context.put("params", params);

		// 输入编辑器
		Thing tableThing = new Thing("xworker.swt.widgets.Table");
		tableThing.initDefaultValue();
		tableThing.put("name", "table");		
		if ("true".equals(params.get("checkbox"))) {
			tableThing.set("CHECK", "true");
		}
		Table table = (Table) tableThing.doAction("create", context);
		GridData gData = new GridData(org.eclipse.swt.layout.GridData.FILL_BOTH);
		gData.horizontalSpan = gridData.colspan;
		gData.verticalSpan = gridData.rowspan;
		table.setLayoutData(gData);

		// 配置数据仓库
		context.put("parent", table);
		Thing dataStore = world.getThing(params.get("dataStorePath"));
		if (dataStore != null) {
			Thing store = (Thing) dataStore.doAction("create", context);
			Thing listener = world.getThing("xworker.swt.xworker.attributeEditor.DataTableEditor/@Listeners/@Listener");
			List<Thing> listeners = (List<Thing>) store.get("listeners");
			listeners.add(listener);
			context.put("store", store);			
		}

		// 创建ActionContainer
		Thing actionThing = world
				.getThing("xworker.swt.xworker.attributeEditor.DataTableEditor/@actions1");
		ActionContainer actionContainer = actionThing.doAction("create", context);
		table.setData(AttributeEditor.ACTIONCONTAINER, actionContainer);
		return table;
	}
	
	public static Object getValue(ActionContext actionContext){
		Thing store = (Thing) actionContext.get("store");
		if(store == null){
			return null;
		}
		
		Table table = (Table) actionContext.get("table");
		String  value = null;
		for(TableItem item : table.getItems()){
			if(item.getChecked() == true){
				DataObject data = (DataObject) item.getData();
				String v = String.valueOf(data.getKeyAndDatas()[0][1]);
				if(v != null && !"".equals(v)){
					if(value == null){
						value = v;
					}else{
						value = value + "," + v;
					}
				}
			}
		}
			
		return value;
	}
	
	public static Object setValue(ActionContext actionContext){
		Thing store = (Thing) actionContext.get("store");
		if(store == null){
			return null;
		}
		
		if(!"true".equals(actionContext.getScope(0).get("loaded"))){
			actionContext.getScope(0).put("value", actionContext.get("value"));
			return null;
		}
		
		Object valueObj = actionContext.get("value");
		if(valueObj == null || "".equals(valueObj)){
			return null;
		}
		String value = String.valueOf(valueObj);
		Table table = (Table) actionContext.get("table");
		for(String v : value.split("[,]")){
			for(TableItem item : table.getItems()){			
				DataObject data = (DataObject) item.getData();
				Object key = data.getKeyAndDatas()[0][1];
				if(v.equals(String.valueOf(key))){					
					item.setChecked(true);
				}
			}
		}
		return null;
	}
	
	public static Object getRootControl(ActionContext actionContext){
		return null;
	}
	
	public static Object getControl(ActionContext actionContext){
		return actionContext.get("table");
	}
	
	public static void onLoaded(final ActionContext actionContext){
		Table table = (Table) actionContext.get("table");
		table.getDisplay().asyncExec(new Runnable(){
			public void run(){
				try{
					actionContext.getScope(0).put("loaded", "true");
					setValue(actionContext);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});		
	}
}
