package xworker.swt.xworker.attributeEditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;

import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class PathSelectorAttributeEditor extends AttributeEditor{

	public PathSelectorAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
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
        Text dataSelectText = new Text(dataSelectComposite, dataSelectTextStyle);
        //dataSelectText.setFont(Resources.getFont("default"));			        
        Designer.attach(dataSelectText, path, context, true);

        GridData dataSelectTextGridData = new GridData(GridData.FILL_HORIZONTAL);
        dataSelectTextGridData.widthHint = _width * attribute.getInt("size", 25);
        dataSelectText.setLayoutData(dataSelectTextGridData);

        int selectDataButtonStyle = SWT.PUSH;
        Button selectDataButton = new Button(dataSelectComposite, selectDataButtonStyle);
        selectDataButton.setText("*");
        
        ActionContext newContext = new ActionContext();
        newContext.put("shell", parent.getShell());
        newContext.put("parent", parent.getShell());
        newContext.put("text", dataSelectText);	        
        newContext.put("selectType", "category");
        Thing listenerThing = World.getInstance().getThing(ThingRegistor.getPath("_xworker_thing_attribute_editor_openDataListener")); 
	    selectDataButton.addListener(SWT.Selection, new SwtListener(listenerThing, newContext, true));	        
        
	    if(modifyListener != null){
	    	dataSelectText.addModifyListener(modifyListener);
        }
	    addDefaultSelection(dataSelectText, defaultSelection);
	    context.getScope(0).put(inputName, dataSelectText);		
	    
	    return dataSelectComposite;
	}

}
