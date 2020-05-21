package xworker.swt.xworker;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import freemarker.template.TemplateException;
import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;

/**
 * CodeEditor的RWT版本。
 * @author zyx
 *
 */
public class CodeEditorText {
    public static Object create(ActionContext actionContext) throws IOException, TemplateException{
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("codeEditorThing");
    	Composite parent = (Composite) actionContext.get("parent");
    	
    	String codeName = UtilData.getString(self.getString("codeName"), actionContext);
    	String codeType = UtilData.getString(self.getString("codeType"), actionContext);
		ActionContext newContext = new ActionContext();
		newContext.put("parent", parent);
		newContext.put("codeName", codeName);
		newContext.put("codeType", codeType);
		newContext.put("parentContext", actionContext);
		
		ActionContext assistActionContext = self.doAction("getAssistActionContext", actionContext);
		newContext.put("assistActionContext", assistActionContext);
		
		Thing codeEditor = world.getThing("xworker.swt.xworker.CodeEditor/@editorViewForm");
		Designer.pushCreator(self);
		try{
			Control control = (Control) codeEditor.doAction("create", newContext);
			Designer.attachCreator(control, self.getMetadata().getPath(), actionContext);
		}finally{
			Designer.popCreator();
		}
		
		Object textEditor = null;
		if(SwtUtils.isRWT()) {
			Text textEdtorT = newContext.getObject("editInput");
			textEdtorT.setData("actionContext", newContext);
			
			if(self.getStringBlankAsNull("code") != null){
				textEdtorT.setText(self.getString("code"));
			}
			
			textEdtorT.setData("lineLabel", newContext.get("editorLineLabel"));
			
			textEditor = textEdtorT;
		}else {
			Text textEditorS = (Text) newContext.get("editInput");
			textEditorS.setData("actionContext", newContext);
			
			if(self.getStringBlankAsNull("code") != null){
				textEditorS.setText(self.getString("code"));
			}
			
			//设置redoundo和其他
			//TextEditor.attach(textEditorS);
			textEditorS.setData("lineLabel", newContext.get("editorLineLabel"));
			
			textEditor = textEditorS;
		}
		
		//创建子节点
		actionContext.getScope(0).put(self.getString("name"), textEditor);
		actionContext.peek().put("parent", newContext.get("editorViewForm"));
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		//log.info("newContext.editorViewForm=" + newContext.editorViewForm.getLayoutData());
		
		//创建toolbar的自定义子节点
		newContext.peek().put("parent", newContext.get("editorToolBar"));
		for(Thing toolbarItems : self.getChilds("LeftToolItems")) {
			for(Thing item : toolbarItems.getChilds()) {
				item.doAction("create",  newContext);
			}
		}		
		
		Designer.attach((Control) textEditor, self.getMetadata().getPath(), actionContext);		
		
		return textEditor;        
	}

    public static void textKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	Text text = (Text) event.widget;
    	
    	int carOffset = SwtTextUtils.getCaretOffset(text);
    	int line =  SwtTextUtils.getLineAtOffset(text, carOffset) + 1;    	
    	int lineOffset = SwtTextUtils.getOffsetAtLine(text, line - 1);

    	((Label) text.getData("lineLabel")).setText("" + line + ":" + (carOffset - lineOffset + 1));
    	((Label) text.getData("lineLabel")).update();

    	if(event.keyCode == 's' && event.stateMask == SWT.CTRL){
    		SwtListener okButtonSelection = (SwtListener) actionContext.get("okButtonSelection");
    	    if(okButtonSelection != null){
    	         okButtonSelection.handleEvent(null);
    	    }
    	}
    }
    
    public static void textMouseUp(ActionContext actionContext){
    	
    }
    
    public static void toolItemSelecitonScript(ActionContext actionContext){
    	World world = World.getInstance();
    	
        //取ViewForm数据对象的定义，要编辑的数据对象放置在ViewForm的数据对象中
    	if(SwtUtils.isRWT()) {
    		Text textEditor = (Text) actionContext.get("editInput");
    		
	    	ActionContext newContext = new ActionContext();
	    	newContext.put("parent", textEditor.getShell());
	    	newContext.put("fileType", textEditor.getData("fileType"));
	    	newContext.put("parentText", textEditor);
	    	newContext.put("saveButtonSelection", actionContext.get("okButtonSelection"));
	
	    	Thing shellObject = world.getThing("xworker.swt.xworker.CodeEditorPop/@shell");
	    	shellObject.doAction("create", newContext);
	
	    	Text newtextEditor = (Text) newContext.get("textEditor");
	   
	    	newtextEditor.setText(textEditor.getText());
	    	newtextEditor.setData("oldText", textEditor);
	
	    	//newtextEditor.setTopIndex(textEditor.getTopIndex());
	
	    	((Shell) newContext.get("shell")).open();
    	}else {
	    	Text textEditor = (Text) actionContext.get("editInput");
	
	    	ActionContext newContext = new ActionContext();
	    	newContext.put("parent", textEditor.getShell());
	    	newContext.put("fileType", textEditor.getData("fileType"));
	    	newContext.put("parentText", textEditor);
	    	newContext.put("saveButtonSelection", actionContext.get("okButtonSelection"));
	
	    	Thing shellObject = world.getThing("xworker.swt.xworker.CodeEditorPop/@shell");
	    	shellObject.doAction("create", newContext);
	
	    	Text newtextEditor = (Text) newContext.get("textEditor");
	    	
	    	newtextEditor.setText(textEditor.getText());
	    	newtextEditor.setData("oldText", textEditor);
	
	    	newtextEditor.setTopIndex(textEditor.getTopIndex());
    		SwtTextUtils.setCaretOffset(newtextEditor, SwtTextUtils.getCaretOffset(textEditor));
	
	    	//CodeEditorSyncer.attach(textEditor, newContext.textEditor);
	    	//CodeEditorSyncer.attach(newContext.textEditor, textEditor);
	
	    	//克隆CodeAssistor
	    	CodeAssistor.clone(textEditor, newtextEditor);
	    	((Shell) newContext.get("shell")).open();
    	}
    }
    
    public static void contentTextKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");

    	Text text = (Text) event.widget;

    	int carOffset = SwtTextUtils.getCaretOffset(text);
    	int line =  SwtTextUtils.getLineAtOffset(text, carOffset) + 1;    	
    	int lineOffset = SwtTextUtils.getOffsetAtLine(text, line - 1);
    		
    	((Label) text.getData("lineLabel")).setText("" + line + ":" + (carOffset - lineOffset + 1));
    	((Label) text.getData("lineLabel")).update();

    	if(event.keyCode == 's' && event.stateMask == SWT.CTRL){
    		SwtListener okButtonSelection = (SwtListener) actionContext.get("okButtonSelection");
    	    if(okButtonSelection != null){
    	        okButtonSelection.handleEvent(null);
    	    }
    	}
    }
}
