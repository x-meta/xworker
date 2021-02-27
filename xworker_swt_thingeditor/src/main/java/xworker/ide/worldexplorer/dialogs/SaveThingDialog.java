package xworker.ide.worldexplorer.dialogs;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.UtilData;

import xworker.util.XWorkerUtils;

@ActionClass(creator="createInstance")
public class SaveThingDialog {
    public static SaveThingDialog createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = SaveThingDialog.class.getName();
        SaveThingDialog obj = actionContext.getObject(key);
        if(obj == null){
            obj = new SaveThingDialog();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }   
    
    @ActionField
    public org.eclipse.swt.widgets.Text nameText;
        
    @ActionField
    public org.eclipse.swt.widgets.Button okButton;
        
    @ActionField
    public org.eclipse.swt.widgets.Text pathText;
        
    @ActionField
    public org.eclipse.swt.widgets.Tree projectTree;
        
    @ActionField
    public org.eclipse.swt.widgets.Shell shell;
    
	public void nameTextModify(ActionContext actionContext) {
		projectTreeSelection(actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public void projectTreeSelection(ActionContext actionContext) {
		TreeItem[] items = projectTree.getSelection();
		if(items == null || items.length == 0) {
			return;
		}
		
		Map<String, Object> data = (Map<String, Object>) items[0].getData();
		Index index = (Index) data.get("index");
		String name = nameText.getText().trim();
		if(Index.TYPE_THINGMANAGER.equals(index.getType())) {
			pathText.setText(index.getName() + "/" + name);
			if("".equals(name)) {
				okButton.setEnabled(false);	
			}else {
				okButton.setEnabled(true);;
			}
		}else if(Index.TYPE_CATEGORY.equals(index.getType())) {
			Category cat = (Category) index.getIndexObject();
			pathText.setText(cat.getThingManager().getName() + "/" + cat.getName() + "." + name);
			if("".equals(name)) {
				okButton.setEnabled(false);	
			}else {
				okButton.setEnabled(true);;
			}
		}else {
			okButton.setEnabled(false);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void okButtonSelection(ActionContext actionContext) {
	    org.xmeta.Action noName = actionContext.getObject("noName");
	    org.xmeta.Action noPath = actionContext.getObject("noPath");
	    org.xmeta.Action exists = actionContext.getObject("exists");
	    
		String name = nameText.getText().trim();
		if("".equals(name)) {
			noName.run(actionContext);
			return;
		}
		
		TreeItem[] items = projectTree.getSelection();
		if(items == null || items.length == 0) {
			noPath.run(actionContext);
			return;
		}
		
		String thingManager = null;
		String path = null;
		Map<String, Object> data = (Map<String, Object>) items[0].getData();
		Index index = (Index) data.get("index");
		if(Index.TYPE_THINGMANAGER.equals(index.getType())) {
			path = name;
			thingManager = index.getName();
		}else if(Index.TYPE_CATEGORY.equals(index.getType())) {
			Category cat = (Category) index.getIndexObject();
			thingManager = cat.getThingManager().getName();
			path = cat.getName() + "." + name;
		}
		
		if(World.getInstance().getThing(path) != null) {
			exists.run(actionContext);
			return;
		}
		
		Thing thing = actionContext.getObject("thing");
		thing = thing .detach();
		thing.saveAs(thingManager, path);
		
		Thing actionThing = actionContext.getObject("actionThing");
		if(actionThing != null) {
			actionThing.doAction("saved", actionContext.getObject("parentContext"), "thing", thing);
			
			if(UtilData.isTrue(actionThing.doAction("isOpenAfterSaved", actionContext.getObject("parentContext")))) {
				XWorkerUtils.ideOpenThing(thing);
			}
		}
		
		shell.dispose();
	}
	
	public void cancelButtonSelection(ActionContext actionContext) {
		Thing actionThing = actionContext.getObject("actionThing");
		if(actionThing != null) {
			actionThing.doAction("unsaved", actionContext.getObject("parentContext"));
		}
		
		shell.dispose();
	}
}
