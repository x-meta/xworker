package xworker.swt.data.inputdatamanagers;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueListIDM extends ThingValueIDM{
	org.eclipse.swt.widgets.List list;
	
	public ThingValueListIDM(org.eclipse.swt.widgets.List combo, List<Thing> values, ActionContext actionContext) {
		super(values, actionContext);
		
		this.list = combo;
	}

	@Override
	public void setValue(Object value) {		
		if(value == null || "".equals(value)) {
			this.list.deselectAll();
			return;
		}
		
		List<Integer> indexs = this.getValuesIndex(value);
		int[] ids = new int[indexs.size()];
		for(int i =0; i<indexs.size(); i++) {
			ids[i] = indexs.get(i);
		}
		
		list.setSelection(ids);
	}

	@Override
	public Object getValue() {
		int[] indexs = list.getSelectionIndices();
		if(indexs == null || indexs.length == 0) {
			return null;
		}
		
		List<Object> datas = new ArrayList<Object>();
		for(int index : indexs) {
			datas.add(getValue(index));
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
