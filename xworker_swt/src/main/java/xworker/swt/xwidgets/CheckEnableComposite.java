package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class CheckEnableComposite {
	ActionContext actionContext;
	ActionContext parentContext;
	Thing thing;
	Button checkBox;
	Composite contentComposite;
	Control content;
	
	public CheckEnableComposite(Thing thing, ActionContext actionContext, ActionContext parentContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.parentContext = parentContext;
		
		this.checkBox = actionContext.getObject("checkBox");
		this.contentComposite = actionContext.getObject("contentComposite");
		
		actionContext.g().put("cc", this);
		
		Thing contentCompositeThing = thing.doAction("getContentControl", actionContext);
		setContentControl(contentCompositeThing);
	
		if(thing.getBoolean("checked")) {
			this.setChecked(true);
		}else {
			this.setChecked(false);
		}
	}
	
	public void setChecked(boolean checked) {
		checkBox.setSelection(checked);
		
		setEnabled(content, checked);
	}
	
	public boolean getChecked() {
		return checkBox.getSelection();
	}
	
	protected void setEnabled(Control control, boolean enabled) {
		if(control != null && control.isDisposed() == false) {
			control.setEnabled(enabled);
			
			if(control instanceof Composite) {
				for(Control child : ((Composite) control).getChildren()) {
					setEnabled(child, enabled);
				}
			}
		}
	}
	public static void checkBoxSelection(ActionContext actionContext) {
		CheckEnableComposite cc = actionContext.getObject("cc");
		cc.setEnabled(cc.content, cc.checkBox.getSelection());		
	}
	
	public void setContentControl(Thing contentControl) {
		for(Control child : contentComposite.getChildren()) {
			child.dispose();
		}
		
		if(contentControl != null) {
			content = contentControl.doAction("create", parentContext, "parent", contentComposite);
			if(checkBox.getSelection() == false) {
				content.setEnabled(false);
			}
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.CheckEnableCompositeShell/@checkComposite"));
		Thing rootComposite = self.getThing("RootComposite");
		if(rootComposite != null) {
			cc.setReplaceCompositeThing(rootComposite);
		}
		
		Composite root = cc.create();
		CheckEnableComposite ccompoiste = new CheckEnableComposite(self, cc.getNewActionContext(), actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), ccompoiste);
		
		return root;
	}
}
