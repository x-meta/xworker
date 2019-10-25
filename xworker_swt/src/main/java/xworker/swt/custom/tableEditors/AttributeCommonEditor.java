package xworker.swt.custom.tableEditors;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.UtilMap;

import xworker.swt.util.SwtUtils;

@ActionClass(creator="createInstance")
public class AttributeCommonEditor extends AbstractTableEditor{
	Thing attribute;
	
	@Override
	public Object create(ActionContext actionContext) {
		World world = World.getInstance();
    	
		//创建Control
		Thing textThing = world.getThing("xworker.swt.custom.tableEditors.AttributeCommonEditor/@TextEditor");
		Text control = (Text) textThing.doAction("create", actionContext);
		control.addListener(SWT.Dispose, new Listener() {

			@Override
			public void handleEvent(Event event) {
				try {
					AttributeCommonEditor.this.doDispose();
				}catch(Exception e) {					
				}
			}
			
		});
		
		//属性的定义
		Object cursor = actionContext.get("cursor");
		attribute = self.doAction("getAttribute", actionContext);		
		control.setData(attribute);		
		control.setData("cursor", cursor); //cursor
		
		//同时弹出窗口，TableCellEditor一直存在编辑器不能弹出
		if (attribute != null && !ItemEditorUtils.isNotDisposeOnSaveValue(actionContext)){
			ActionContext ac = new ActionContext();
		    ac.put("parent", control.getShell());
		    ac.put("text", control);
		    ac.put("parentContext", actionContext.get("parentContext"));
		    Item tableItem = (Item) actionContext.get("item");
		    ac.put("params", tableItem.getData("_store_record")); //当前表格的行数据，有可能作为数据源的参数
		    ac.put("editor", actionContext.get("editor"));
		        
		    Thing thing = new Thing();
		    Thing attr = attribute.detach();
		    attr.set("name", "attr");
		    attr.set("showLabel", "false");
		    thing.addChild(attr, false);
		    		   
		    //弹出编辑窗口
		    Thing windowThing = world.getThing("xworker.swt.custom.tableEditors.AttributeEditorOpenWindow");
		    Shell shell = (Shell) windowThing.doAction("create", ac);
		    Thing form = (Thing) ac.get("form");
		    form.doAction("setDescriptor", ac, UtilMap.toParams(new Object[]{"descriptor", thing}));
		    control.setData("window", shell);
		    shell.pack();
		    String inputType = attribute.getString("inputtype");
		    if("textarea".equals(inputType) || "html".equals(inputType) || "codeEditor".equals(inputType)){
		    	shell.setSize(640, 480);
		    }
		    
		    //弹出窗口的位置
			Composite parent = ItemEditorUtils.getCursorParent(cursor);
			if(parent != null) {
				SwtUtils.setShellRelateLocation(shell, parent.toDisplay(ItemEditorUtils.getCursorLocation(cursor)),					
						ItemEditorUtils.getCursorSize(cursor));
			}
		    //Point location = cursor.getParent().toDisplay(cursor.getLocation());
		    //shell.setLocation(location);    
		    shell.open();    		
		    
		    actionContext.getScope(0).put("formContext", ac);
		}
		
		return control;   
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		ActionContext formContext = (ActionContext) actionContext.get("formContext");
    	Text textEditor = (Text) actionContext.get("textEditor");
    	if(value == null) {
    		textEditor.setText("");
    	}else {
    		textEditor.setText(String.valueOf(value));
    	}
	    
		if(formContext != null){
			Thing form = (Thing) formContext.get("form");
			ActionContext fformContext = (ActionContext) form.getData("formContext");
			Item tableItem = (Item) actionContext.get("item");
			fformContext.getScope(0).put("params", tableItem.getData("_store_record"));
			value = actionContext.get("value");
			if(value == null){
				value = "";
			}
		    form.doAction("setValues", formContext, UtilMap.toParams(new Object[]{"values", UtilMap.toParams(new Object[]{"attr", value})}));
		}else{			
		    textEditor.selectAll();
		    textEditor.setFocus();		
		}    
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(ActionContext actionContext) {
		ActionContext formContext = (ActionContext) actionContext.get("formContext");
		if(formContext != null){
			Thing form = (Thing) formContext.get("form");
		    Map<String, Object> values = (Map<String, Object>) form.doAction("getValues", formContext);
		    return values.get("attr");
		}else{
			Text textEditor = (Text) actionContext.get("textEditor");
		    return textEditor.getText();
		}    
	}

	@Override
	public void doDispose() {
		Text dateText = (Text) actionContext.get("textEditor");
		
		Shell window = (Shell) dateText.getData("window");
    	if(window != null){
    	    window.dispose();
    	}
    	    	
    	Composite composite = (Composite) dateText.getData("composite");
    	composite.dispose();   
	}

	public static AttributeCommonEditor createInstance(ActionContext actionContext) {
		String key = "key_AttributeCommonEditor";
		AttributeCommonEditor editor = actionContext.getObject(key);
		if(editor == null) {
			editor = new AttributeCommonEditor();
			actionContext.g().put(key, editor);
		}
		
		return editor;
	}
}
