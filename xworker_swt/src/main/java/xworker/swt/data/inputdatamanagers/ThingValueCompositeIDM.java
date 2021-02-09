package xworker.swt.data.inputdatamanagers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueCompositeIDM extends ThingValueIDM{
	Composite composite;
	
	public ThingValueCompositeIDM(Composite composite, List<Thing> values, ActionContext actionContext) {
		super(values, actionContext);
		
		this.composite = composite;
	}

	@Override
	public void setValue(Object value) {
		if(value == null || "".equals(value)) {
			//首先清空选择
			for(Control child : composite.getChildren()) {
				if(child instanceof Button) {
					((Button) child).setSelection(false);
				}
			}
			return;
		}
		
		List<Integer> indexs = this.getValuesIndex(value);
		int index = 0;
		for(Control child : composite.getChildren()) {
			if(child instanceof Button) {
				boolean ok = false;
				for(Integer id : indexs) {
					if(id == index) {
						ok = true;
						break;
					}
				}
				
				((Button) child).setSelection(ok);
				index++;
			}
		}
	}

	@Override
	public Object getValue() {
		List<Object> datas = new ArrayList<Object>();
		int index = 0;
		for(Control child : composite.getChildren()) {
			if(child instanceof Button) {
				if(((Button) child).getSelection()) {
					datas.add(getValue(index));
				}
				index++;
			}
		}
		
		String str = null;
		for(int i=0; i<datas.size(); i++) {
			if(i == 0) {
				str = String.valueOf(datas.get(i));
			}else {
				str = str + "," + String.valueOf(datas.get(i));
			}
		}
		return str;
	}
	
	@Override
	public Object getControlValue() {
		return null;
	}
}
