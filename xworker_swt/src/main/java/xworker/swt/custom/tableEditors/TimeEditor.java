package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;
import xworker.swt.xworker.AttributeEditor;

@ActionClass(creator="createInstance")
public class TimeEditor extends AbstractTableEditor{
	String pattern;
	DateTime dateTime;
	ActionContainer actions;

	@Override
	public Object create(ActionContext actionContext) {
		//创建时间控件
		dateTime = new DateTime((Composite) actionContext.get("parent"), SWT.TIME | SWT.LONG | SWT.BORDER);
		dateTime.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				//System.out.println("selection ");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//System.out.println("default selection ");
				try {
					TimeEditor.this.saveValue();
					if(!ItemEditorUtils.isNotDisposeOnSaveValue(TimeEditor.this.actionContext)) {
						TimeEditor.this.doDispose();
					}
				}catch(Exception ee) {					
				}
			}			
		});		
		//创建并返回ActionContainer
		ActionContext ac = new ActionContext();
		ac.put("control", dateTime);
		String pattern = self.getStringBlankAsNull("pattern");
		
		if(pattern == null || pattern == ""){
		    pattern = "HH:mm:ss";
		}
		ac.put("pattern", pattern);
		Thing actionThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.TimeEditor/@actions1");
		actions = actionThing.doAction("create", ac);
		
		Designer.attachCreator(dateTime, self.getMetadata().getPath(), actionContext);
		dateTime.setData(AttributeEditor.ACTIONCONTAINER, actions);
		
		dateTime.forceFocus();
		return dateTime;
	}

	@Override
	public void setValue(Object value, ActionContext actionContext) {
		actions.doAction("setValue", actionContext, "value", value);
		dateTime.forceFocus();
	}

	@Override
	public Object getValue(ActionContext actionContext) {		
		return actions.doAction("getValue", actionContext);
	}

	@Override
	public void doDispose() {
		dateTime.dispose();
	}
	
	public static TimeEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(TimeEditor.class, actionContext);
	}

}
