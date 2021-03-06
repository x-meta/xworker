package xworker.swt.xworker.attributeEditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.form.ThingDescriptorForm.ColorListener;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class ColorpickerAttributeEditor extends AttributeEditor{

	public ColorpickerAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int dataSelectCompositeStyle = SWT.NONE;
        Composite dataSelectComposite = new Composite(parent, dataSelectCompositeStyle);

        GridLayout dataSelectComGridLayout = new GridLayout();
        dataSelectComGridLayout.numColumns = 2;
        dataSelectComGridLayout.marginWidth= 0;
        dataSelectComGridLayout.marginHeight= 0;        
        dataSelectComposite.setLayout(dataSelectComGridLayout);

        GridData dataSelectCompositeGridData = new GridData();
        dataSelectCompositeGridData.horizontalSpan = getColspan(xgridData.colspan);
        dataSelectCompositeGridData.verticalSpan = xgridData.rowspan;
        dataSelectComposite.setLayoutData(dataSelectCompositeGridData);

        int dataSelectTextStyle = SWT.SINGLE | SWT.LEFT;
        dataSelectTextStyle |= SWT.BORDER;
        Combo dataSelectText = new Combo(dataSelectComposite, dataSelectTextStyle);
        for(Thing value : attribute.getAllChilds("value")) {
        	String v = value.getStringBlankAsNull("value");
        	if(v == null) {
        		v = value.getMetadata().getName();
        	}
        	dataSelectText.add(v);
        }
        for(String key : SwtUtils.getSWTKeys()) {
			if(key.startsWith("COLOR")) {
				dataSelectText.add(key);
			}
		}
        Designer.attach(dataSelectText, path, context, true);
        //dataSelectText.setFont(Resources.getFont("default"));			        

        GridData dataSelectTextGridData = new GridData(GridData.FILL_HORIZONTAL);
        dataSelectTextGridData.widthHint = _width * attribute.getInt("size", 25);
        dataSelectText.setLayoutData(dataSelectTextGridData);

        int selectDataButtonStyle = SWT.PUSH;
        Button selectDataButton = new Button(dataSelectComposite, selectDataButtonStyle);
        selectDataButton.setText("*");
        
        selectDataButton.addSelectionListener(new ColorListener(dataSelectText));
        //UtilSwt.setBackground(dataSelectText, dataSelectText.getText(), context);
        if(modifyListener != null){
        	dataSelectText.addModifyListener(modifyListener);
        }
        addDefaultSelection(dataSelectText, defaultSelection);
	    context.getScope(0).put(inputName, dataSelectText);	
	    
	    return dataSelectComposite;
	}

}
