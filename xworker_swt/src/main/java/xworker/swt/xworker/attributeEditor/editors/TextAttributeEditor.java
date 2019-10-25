package xworker.swt.xworker.attributeEditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class TextAttributeEditor extends AttributeEditor{

	public TextAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int textTextStyle = SWT.SINGLE | SWT.LEFT;
        textTextStyle |= SWT.BORDER;
        Text textText = new Text(parent, textTextStyle);
        //textText.setFont(Resources.getFont("default"));

        GridData textTextGridData = new GridData();
        textTextGridData.widthHint = _width * attribute.getInt("size", 25);
        textTextGridData.horizontalSpan = getColspan(xgridData.colspan);
        textTextGridData.verticalSpan = xgridData.rowspan;
        textText.setLayoutData(textTextGridData);
        Designer.attach(textText, path, context, true);
        if(modifyListener != null){
        	textText.addModifyListener(modifyListener);
        }
        addDefaultSelection(textText, defaultSelection);
        
        initCodeAssistor(textText, context);
        
        context.getScope(0).put(inputName, textText);        
		return textText;
	}

}
