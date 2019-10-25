package xworker.swt.xworker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.util.ActionContainer;

import xworker.swt.custom.StyledTextProxy;

public class AttributeEditor {
	/** 属性编辑器返回的Composite的data中ActionContainer的键值 */
	public static final String ACTIONCONTAINER = "__attribute_actionContainer__";
	
	public static final String INPUT_CONTROL = "__attribute_inputControl__";
	
	public static ActionContainer getActionContainer(Control control){
		return (ActionContainer) control.getData(ACTIONCONTAINER);
	}
	
	public static Control getInputControl(Control control){
		return (Control) control.getData(INPUT_CONTROL);
	}
	
	public static void attachModifyListener(ActionContext actionContext) {
		Control editControl = actionContext.getObject("parent");
		ModifyListener modifyListener = actionContext.getObject("modifyListener");
		
		if(editControl != null && modifyListener != null){
			if(editControl instanceof Combo){
				((Combo) editControl).addModifyListener(modifyListener);
			}else if(editControl instanceof Spinner){
				((Spinner) editControl).addModifyListener(modifyListener);
			}else if(editControl instanceof Text){
				((Text) editControl).addModifyListener(modifyListener);
			}else if(editControl instanceof CCombo){
				((CCombo) editControl).addModifyListener(modifyListener);
			}else if(StyledTextProxy.isStyledText(editControl)) {
    			StyledTextProxy.addModifyListener(editControl, modifyListener);
    		}
		}
		
		Listener defaultSelection = actionContext.getObject("defaultSelection");
		if(defaultSelection != null) {
			editControl.addListener(SWT.DefaultSelection, defaultSelection);
		}
	}
	
}
