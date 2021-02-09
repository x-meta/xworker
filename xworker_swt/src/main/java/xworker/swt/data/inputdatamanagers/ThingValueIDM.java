package xworker.swt.data.inputdatamanagers;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.data.InputDataManager;

/**
 * 使用模型定义的value的选择器。支持Combo、CCombo和Composite。
 * 
 * @author zhangyuxiang
 *
 */
public abstract class ThingValueIDM implements InputDataManager{
	List<Thing> values;
	ActionContext actionContext;
	
	public ThingValueIDM(List<Thing> values, ActionContext actionContext) {
		this.values = values;
		this.actionContext = actionContext;	
	}
	
	/**
	 * 返回给定的单个值得位置索引。
	 * 
	 * @param value
	 * @return
	 */
	public int getValueIndex(Object value) {
		for(int i=0; i<values.size(); i++) {
			Thing tv = values.get(i);
			if(value.equals(tv.getStringBlankAsNull("value"))) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 通过Value返回符合选择列表的索引。
	 * 
	 * @param value
	 * @return
	 */
	public List<Integer> getValuesIndex(Object value){
		List<Integer> list = new ArrayList<Integer>();
		if(value instanceof String) {
			String vs[] = ((String) value).split("[,]");
			for(String v : vs) {
				v = v.trim();
				if(!"".equals(v)) {
					for(int i=0; i<values.size(); i++) {
						Thing tv = values.get(i);
						if(v.equals(tv.getStringBlankAsNull("value"))) {
							list.add(i);
						}
					}
				}
			}
		} else if(value != null){
			for(int i=0; i<values.size(); i++) {
				Thing tv = values.get(i);
				if(value.equals(tv.getStringBlankAsNull("value"))) {
					list.add(i);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 返回值的标签，如有多个值。
	 * 
	 * @param value
	 * @return
	 */
	public String getValuesLabel(Object value) {
		List<String> list = new ArrayList<String>();
		if(value instanceof String) {
			String vs[] = ((String) value).split("[,]");
			for(String v : vs) {
				v = v.trim();
				if(!"".equals(v)) {
					for(int i=0; i<values.size(); i++) {
						Thing tv = values.get(i);
						if(v.equals(tv.getStringBlankAsNull("value"))) {
							list.add(tv.getMetadata().getLabel());
						}
					}
				}
			}
		} else if(value != null){
			for(int i=0; i<values.size(); i++) {
				Thing tv = values.get(i);
				if(value.equals(tv.getStringBlankAsNull("value"))) {
					list.add(tv.getMetadata().getLabel());
				}
			}
		}
		
		String str = null;
		for(int i=0; i<list.size(); i++) {
			if(i == 0) {
				str = list.get(i);
			}else {
				str = str + "," + list.get(i);  
			}
		}
		return str;
	}
	
	public Object getValue(int index) {
		if(values.size() == 0) {
			return getControlValue();
		}
		
		Thing tv = values.get(index);
		return tv.get("value");
	}
	
	public abstract Object getControlValue();
}
