package xworker.swt.data.inputdatamanagers;

import java.util.List;

import org.eclipse.swt.widgets.Combo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueComboIDM extends ThingValueIDM{
	Combo combo;
	
	public ThingValueComboIDM(Combo combo, List<Thing> values, ActionContext actionContext) {
		super(values, actionContext);
		
		this.combo = combo;
	}

	@Override
	public void setValue(Object value) {
		if(value == null || "".equals(value)) {
			combo.setText("");
			return;
		}
		
		List<Integer> indexs = this.getValuesIndex(value);
		if(indexs.size() > 0 && indexs.get(0) < combo.getItemCount()) {
			combo.select(indexs.get(0));
			return;
		}
		
		combo.setText(String.valueOf(value));
	}
	
	@Override
	public Object getControlValue() {
		return combo.getText();
	}

	@Override
	public Object getValue() {
		int index = combo.getSelectionIndex();
		if(index == -1) {
			return combo.getText();
		}else {
			return getValue(index);
		}
	}

}
