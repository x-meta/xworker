package xworker.swt.xworker.attributeEditor.editors;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.form.FormModifyListener;
import xworker.swt.form.GridData;
import xworker.swt.xworker.attributeEditor.AttributeEditor;
import xworker.swt.xworker.attributeEditor.AttributeEditorFactory;

public class SubGroupEditor extends AttributeEditor{

	public SubGroupEditor(Thing formThing, Thing attribute, GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(attribute.getMetadata().getLabel());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		
		boolean formNoLabel = attribute.getBoolean("formNoLabel");
		List<Thing> fs = attribute.getChilds();
		List<List<xworker.swt.form.GridData>> rows = xworker.swt.form.GridLayout.layout(fs, 1);
		for(int i=0; i<rows.size(); i++){
			List<xworker.swt.form.GridData> row = rows.get(i);
			
		    for(int n=0; n<row.size(); n++){
		    	//long start = System.currentTimeMillis();
		    	//输入字段的标签		 
		    	xworker.swt.form.GridData xgridData = row.get(n);
		    	Thing f = (Thing) xgridData.source;
		    	
		    	xworker.swt.xworker.attributeEditor.AttributeEditor editor = 
		    			AttributeEditorFactory.createAttributeEditor(formThing, f, xgridData, context);
		    	if(editor != null) {
		    		editor.setWidthHeight(_width, _height);
		    		editor.create(group, modifyListener, defaultSelection, formNoLabel);
		    	}
		    }
		}
		
		//设置布局数据
		org.eclipse.swt.layout.GridData gridData = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.FILL_HORIZONTAL);
		//gridData.widthHint = _width * attribute.getInt("size", 25);
		gridData.horizontalSpan = getColspan(xgridData.colspan);
		gridData.verticalSpan = xgridData.rowspan;
        group.setLayoutData(gridData);
        
		return group;
	}
}
