package xworker.swt.app.prototypes;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;

@ActionClass(creator="createInstance")
public class OutlineShell {
	public void setComposite(){
        //xworker.swt.app.prototypes.OutlineShell/@outlineComposite/@actions/@setComposite
        //println outlineStackLayout;
		Composite composite = actionContext.getObject("composite");
        if(actionContext.get("composite") == null){
            outlineStackLayout.topControl = blankTree;
        }else{
            outlineStackLayout.topControl= composite;
        }
        
        outlineComposite.layout();
    }
    
    public Object getComposite(){
        //xworker.swt.app.prototypes.OutlineShell/@outlineComposite/@actions/@getComposite
        return outlineComposite;
    }
    

    public static OutlineShell createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = OutlineShell.class.getName();
        OutlineShell obj = actionContext.getObject(key);
        if(obj == null){
            obj = new OutlineShell();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
    
    public World world = World.getInstance();
    
    @ActionField
    public org.xmeta.util.ActionContainer actions;
        
    @ActionField
    public org.eclipse.swt.widgets.Tree blankTree;
        
    @ActionField
    public org.eclipse.swt.widgets.Composite outlineComposite;
        
    @ActionField
    public org.eclipse.swt.custom.StackLayout outlineStackLayout;
}
