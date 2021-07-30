package xworker.swt.app.prototypes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;

import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;

@ActionClass(creator="createInstance")
public class SaveResourceDialog {
	public void okButtonSelection(){
        //xworker.swt.app.prototypes.SaveResourceDialog/@179440/@179443/@179445/@179446/@GroovyAction
        //保存编辑器
        for(TableItem item : editorsTable.getItems()){
            if(item.getChecked()){
                IEditor editor = (IEditor) item.getData();
                editor.doSave();
            }
        }
        
        actionContext.g().put("result", true);
        //关闭窗口
        shell.dispose();
    }
    
    public void cancelButtonSelection(){
        //xworker.swt.app.prototypes.SaveResourceDialog/@179440/@179447/@179449/@179450/@GroovyAction
        actionContext.g().put("result", false);
        shell.dispose();
    }
    

    public static SaveResourceDialog createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = SaveResourceDialog.class.getName();
        SaveResourceDialog obj = actionContext.getObject(key);
        if(obj == null){
            obj = new SaveResourceDialog();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
    
    public void init(){
        //xworker.swt.app.prototypes.SaveResourceDialog/@init
    	ActionContext parentContext = actionContext.getObject("parentContext");
    	IEditorContainer editorContainer = parentContext.getObject("editorContainer");
    	
        for(xworker.thingeditor.IEditor<Composite, Control, Image> editor : editorContainer.getEditors(true)){
            TableItem item = new TableItem(editorsTable, SWT.NONE);
            item.setData(editor);
            item.setText(new String[] {editor.getSimpleTitle(), editor.getTitle()});
            item.setChecked(true);
        }
    }
        
    @ActionField
    public ActionContext actionContext;
    
    public World world = World.getInstance();
    
    @ActionField
    public org.xmeta.Thing ataStore;
        
    @ActionField
    public org.eclipse.swt.widgets.Button cancelButton;
        
    @ActionField
    public org.eclipse.swt.widgets.Table editorsTable;
        
    @ActionField
    public org.eclipse.swt.widgets.Button okButton;
    
    @ActionField
    public org.eclipse.swt.widgets.Shell shell;
}
