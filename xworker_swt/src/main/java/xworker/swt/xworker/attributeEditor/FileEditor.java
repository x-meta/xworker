package xworker.swt.xworker.attributeEditor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

import xworker.swt.xworker.AttributeEditor;

public class FileEditor {
	public static Object create(ActionContext actionContext) {
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");

		// 为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentActionContext", actionContext);
		
		// 输入编辑器
		Thing inputThing = world
				.getThing("xworker.swt.xworker.attributeEditor.FileEditor/@composite");
		Composite composite = (Composite) inputThing.doAction("create", context);
	
		Text text = (Text) context.get("text");
		text.setData("actionContext", context);

		// 创建子节点
		if (actionContext.get("gridData") == null) {
			// 在一般的swt界面中被调用
			Bindings bindings = actionContext.push(null);
			bindings.put("parent", context.get("composite"));
			try {
				for (Thing child : self.getAllChilds()) {
					child.doAction("create", actionContext);
				}
			} finally {
				actionContext.pop();
			}
			actionContext.getScope(0).put(self.getString("name"), text);
		} else {
			// 在事物编辑器里被调用
			xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext
					.get("gridData");
			// Thing attribute = gridData.source;
			// 设置布局

			String inputattrs = gridData.source.getStringBlankAsNull("inputattrs");
			if(inputattrs != null){
				Map<String, String> params = UtilString.getParams(inputattrs);
				context.put("params", params);
			}
					
			composite.setLayoutData(actionContext.get("layoutData"));

			if (actionContext.get("modifyListener") != null) {
				text.addModifyListener((ModifyListener) actionContext
						.get("modifyListener"));
			}
			// Text输入框为输入属性编辑器的Model事物
			actionContext.getScope(0).put(
					gridData.source.getString("name") + "Input", text);
		}

		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;
	}

	@SuppressWarnings("unchecked")
	public static void editButtonAction(ActionContext actionContext) {
		Text text = (Text) actionContext.get("text");
		String path = text.getText();
		Map<String, String> params = (Map<String, String>) actionContext.get("params");
		int style = SWT.OPEN;
		if(params != null){
			String type = params.get("type");
			if("OPEN".equals(type)){
				style = SWT.OPEN;
			}else if("SAVE".equals(type)){
				style = SWT.SAVE;
			}else if("MULTI".equals(type)){
				style = SWT.MULTI;
			}
		}
		FileDialog dialog = new FileDialog(text.getShell(), style);
		dialog.setFileName(path);
		if(params != null){
			if(params.get("filterExtensions") != null){
				String filterExtensions[] = UtilString.getStringArray(params.get("filterExtensions"));
				if(filterExtensions != null){
					dialog.setFilterExtensions(filterExtensions);
				}
			}
			
			if(params.get("filterNames") != null){
				String filterNames [] = UtilString.getStringArray(params.get("filterNames"));
				if(filterNames != null){
					dialog.setFilterNames(filterNames);
				}
			}
		}
		
		String fileName = dialog.open();
		if (fileName != null) {
			fileName = UtilFile.toXWorkerFilePath(fileName);

			text.setText(fileName);
		}
	}
}
