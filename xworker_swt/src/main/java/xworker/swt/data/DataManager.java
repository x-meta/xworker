package xworker.swt.data;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BindItemFactory;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.reacts.DataReactors;

public class DataManager {
	private static final String TAG = DataManager.class.getName();
	private static final String KEY = "__DataManager_CACHED_DATA__";
	
	public static void initDataBinds(Thing self, Control control, ActionContext actionContext) throws OgnlException {
		String dataBinds = (String) control.getData("dataBinds");
		if(dataBinds != null) {
			for(String line : dataBinds.split("[\n]")) {
				line = line.trim();
				if(line.startsWith("#") || "".equals(line)) {
					continue;
				}
				
				int index = line.indexOf("|");
				if(index == -1) {
					//无法解析
					Thing thing = Designer.getThing(control);
					Executor.info(TAG, "Can not parse line '" + line + "', no '|'  path=" + thing != null ? thing.getMetadata().getPath() : "" );
					continue;
				}
				
				String dataPath = line.substring(0, index).trim();
				String attributeName = line.substring(index + 1, line.length()).trim();
				
				Object data = Ognl.getValue(dataPath, actionContext);
				if(data instanceof DataObject) {
					DataObject dataObject = (DataObject) data;
						
					DataObjectBinder binder = (DataObjectBinder) dataObject.getData(KEY);
					if(binder == null) {
						binder = new DataObjectBinder(dataObject, control, actionContext);
						dataObject.setData(KEY, binder);
						
						actionContext.g().put(dataPath + "DataObjectBinder", binder);
					}
					
					String valuePath = attributeName;
					index = attributeName.indexOf(":");					
					if(index != -1) {
						valuePath = attributeName.substring(index + 1, attributeName.length()).trim();
						attributeName = attributeName.substring(0, index).trim();
					}
					
					BinderItem item = BindItemFactory.create(attributeName, valuePath, control, actionContext);
					if(item != null) {
						binder.addBinderItem(item);
					}					
				} else {
					Thing thing = Designer.getThing(control);
					Executor.info(TAG, "Not supported object type, " + data.getClass().getSimpleName() + ", path=" + thing != null ? thing.getMetadata().getPath() : "");
				}
			}
		}
		
		String reactorRules = (String) control.getData("reactorRules");
		if(reactorRules != null && !"".equals(reactorRules)) {
			DataReactors.createRules(reactorRules, actionContext);
		}
		
		if(control instanceof Composite) {
			for(Control child : ((Composite) control).getChildren()) {
				initDataBinds(self, child, actionContext);
			}
		}
	}
	
	public static void create(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		Control parent = actionContext.getObject("parent");
		initDataBinds(self, parent, actionContext);
		
		List<String> fireLoadReactors = self.doAction("getFireLoadReactors", actionContext);
		DataReactors.fireLoadReactors(fireLoadReactors, actionContext);
	}
}
