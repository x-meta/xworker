package xworker.swt.data.inputs;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.data.AbstractInput;

public class TextInput extends AbstractInput{
	Text text;
	
	public TextInput(Control control, Thing thing, ActionContext actionContext) {
		super(control, thing, actionContext);
		
		this.text = (Text) control;
	}

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doSetValue(Object value) {
		// TODO Auto-generated method stub
		
	}

}
