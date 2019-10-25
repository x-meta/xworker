package xworker.swt.custom.tableEditors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

@ActionClass(creator="createInstance")
public class CComboStringEditor extends AbstractTableEditor{
	CCombo ccombo;
	List<String> values = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	@Override
	public Object create(ActionContext actionContext) {
		Object vs = self.doAction("getValues", actionContext);
		if(vs != null) {
			if(vs instanceof String[]) {
				String[] as = (String[]) vs;
				for(String str : as) {
					values.add(str);
				}
			}else if(vs instanceof Collection) {
				Collection<Object> cs = (Collection<Object>) vs;
				for(Object o : cs) {
					values.add(String.valueOf(o));
				}
			}else if(vs instanceof Iterator) {
				Iterator<Object> iter = (Iterator<Object>) vs;
				while(iter.hasNext()) {
					values.add(String.valueOf(iter.next()));
				}
			}else {
				values.add(String.valueOf(vs));
			}
		}
		
		Thing comboThing = null;
		if(self.getBoolean("readOnly")) {
			comboThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.CComboStringEditor/@combo1");
		}else {
			comboThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.CComboStringEditor/@combo");
		}
		
		ccombo = comboThing.doAction("create", actionContext);
		ccombo.setData("thing", self);
		if(values != null) {
			for(String value : values) {
				ccombo.add(value);
			}
		}
		ccombo.addListener(SWT.DefaultSelection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				CComboStringEditor.this.saveValue();
				CComboStringEditor.this.doDispose();
			}
			
		});
		
		ccombo.forceFocus();
		return ccombo;
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		if(value == null) {
			ccombo.setText("");
			return;
		}
		
		String svalue = String.valueOf(value);
		if(values != null) {
			for(String valueThing : values) {
				if(svalue.equals(valueThing)) {
					ccombo.setText(valueThing);
					return;
				}
			}
		}
		
		ccombo.setText(svalue);
		ccombo.forceFocus();
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		int index = ccombo.getSelectionIndex();
		if(index != -1) {
			return values.get(index);
		}else {
			return ccombo.getText();
		}
	}

	@Override
	public void doDispose() {
		ccombo.dispose();
	}

	public static CComboStringEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(CComboStringEditor.class, actionContext);
	}

}
