package xworker.swt.xworker.attributeEditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class TruefalseAttributeEditor extends AttributeEditor{

	public TruefalseAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int checkButtonStyle = SWT.CHECK;
        Button checkButton = new Button(parent, checkButtonStyle);
        Designer.attach(checkButton, path, context, true);
        //checkButton.setFont(Resources.getFont("default"));
        checkButton.setData("selected", "true");
        checkButton.setData("unSelected", "false");			        
        
        GridData checkButtonGridData = new GridData();
        checkButtonGridData.horizontalSpan = getColspan(xgridData.colspan);
        checkButtonGridData.verticalSpan = xgridData.rowspan;
        checkButton.setLayoutData(checkButtonGridData);

        if(modifyListener != null){
        	checkButton.addSelectionListener(modifyListener.getSelectionListener());
        }
        addDefaultSelection(checkButton, defaultSelection);
        context.getScope(0).put(inputName, checkButton);	
        
        return checkButton;
	}

}
