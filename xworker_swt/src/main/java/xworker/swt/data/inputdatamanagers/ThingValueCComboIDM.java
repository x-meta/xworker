package xworker.swt.data.inputdatamanagers;

import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueCComboIDM extends ThingValueIDM{
	CCombo ccombo;
	
	@Override
	public Object getControlValue() {
		return ccombo.getText();
	}

	public ThingValueCComboIDM(CCombo combo, List<Thing> values, ActionContext actionContext) {
		super(values, actionContext);
		
		this.ccombo = combo;
	}

	@Override
	public void setValue(Object value) {
		if(value == null || "".equals(value)) {
			ccombo.setText("");
			return;
		}
		
		List<Integer> indexs = this.getValuesIndex(value);
		if(indexs.size() > 0 && indexs.get(0) < ccombo.getItemCount()) {
			ccombo.select(indexs.get(0));
			return;
		}
		
		ccombo.setText(String.valueOf(value));
	}

	@Override
	public Object getValue() {
		int index = ccombo.getSelectionIndex();
		if(index == -1) {
			return ccombo.getText();
		}else {
			return getValue(index);
		}
	}

}
