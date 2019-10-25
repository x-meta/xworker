/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
//import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.swt.ActionContainer;
import xworker.swt.custom.StyledTextProxy;
import xworker.swt.form.HiddenInput;
import xworker.swt.model.Model;
import xworker.swt.model.ModelManager;

public class UtilModel {
	@SuppressWarnings("rawtypes")
	public static void setValue(Object value, Object control, String viewPattern, String editPattern) throws ParseException{
		if(control == null) {
			return;
		}
		
		if(control instanceof Widget){
			Widget widget = (Widget) control;
			if(widget.isDisposed()){
				return;
			}
		}
		
		Model model = ModelManager.getModel(control.getClass());
		if(model != null) {
			model.setValue(control, value, viewPattern, editPattern);
			return;
		}
		
		if(control instanceof HiddenInput){
			((HiddenInput) control).setValue(value);
		}else if(control instanceof Text){
			Text t = (Text) control;
			if(t.isFocusControl() && editPattern != "" && !"".equals(editPattern)){
				t.setText(UtilData.format(value, editPattern));
			}else{
	            t.setText(UtilData.format(value, viewPattern));
			}
	    }else if(StyledTextProxy.isStyledText(control)){
	    	//StyledText t = (StyledText) control;
	    	if(StyledTextProxy.isFocusControl(control) && editPattern != "" && !"".equals(editPattern)){
	    		StyledTextProxy.setText(control, UtilData.format(value, editPattern));
			}else{
				StyledTextProxy.setText(control, UtilData.format(value, viewPattern));
			}
	    }else if(control instanceof Label){
	    	Label l = (Label) control;
	    	if(l.isFocusControl() && editPattern != "" && !"".equals(editPattern)){
				l.setText(UtilData.format(value, editPattern));
			}else{
	            l.setText(UtilData.format(value, viewPattern));
			}
	        l.pack();
	    }else if(control instanceof Button){
	    	Button b = (Button) control;
	    	
	    	if((b.getStyle() & SWT.RADIO) != 0){
	    		//是radio
	    	}else if((b.getStyle() & SWT.CHECK) != 0){
	    		//是checkBox
	    		if(UtilData.getBoolean(value, false)){
	    			b.setSelection(true);
	    		}else{
	    			b.setSelection(false);
	    		}
	    		/*
	    		if(value instanceof Boolean){
	    			Boolean bv = (Boolean) value;
	    			if(bv.booleanValue()){
	    				b.setSelection(true);
	    			}else{
	    				b.setSelection(false);
	    			}
	    		}else if(value instanceof String){
	    			String sv = (String) value;
	    			
	    			if("TRUE".equals(sv.toUpperCase()) || "1".equals(sv)){
	    				b.setSelection(true);
	    			}else{
	    				b.setSelection(false);
	    			}	    			
	    		}else if(value != null){
	    			b.setSelection(true);
	    		}else{
	    			b.setSelection(false);
	    		}*/
	    	}else{
	    		b.setText(UtilData.format(value, viewPattern));
	    	}
	    }else if(control instanceof DateTime){
	    	Date dateValue;
	        if(value instanceof Date){
	            dateValue = (Date) value;
			}else if(value == null){
				dateValue = new Date(0);
			}else{
	            dateValue = (Date) UtilData.parse(value.toString(), "Date", viewPattern);
	        }
	        
	        if(dateValue != null){
	        	DateTime dt = (DateTime) control;
		        GregorianCalendar c = new GregorianCalendar();
		        c.setTime(dateValue);
			    dt.setYear(c.get(Calendar.YEAR));
			    dt.setMonth(c.get(Calendar.MONTH));
			    dt.setDay(c.get(Calendar.DAY_OF_MONTH));
	        }
	        
	    }else if(control instanceof Thing){
	    	Thing dataControl = (Thing) control;
	    	Map<String, Object> data = new HashMap<String, Object>();
	    	data.put("value", value);
	    	data.put("viewPattern", viewPattern);
	    	data.put("editPattern", editPattern);
	    	dataControl.doAction("setValue", new ActionContext(), data);
	    }else if(control instanceof ActionContainer){
	    	ActionContainer ac = (ActionContainer) control;
	    	Map<String, Object> data = new HashMap<String, Object>();
	    	data.put("value", value);
	    	data.put("viewPattern", viewPattern);
	    	data.put("editPattern", editPattern);
	    	ac.doAction("setValue", data);
	    }else if(control instanceof Combo){
	    	Combo combo = (Combo) control;
	    	if(combo.getData() instanceof List){
	    		List datas = (List) combo.getData();
	    		for(int i=0; i<datas.size(); i++){
	    			if(value != null && isMatch(value, datas.get(i))){
	    				combo.select(i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    	if(value != null){
	    		combo.setText(value.toString());
	    	}
	    	//combo.setText(value);
	    }else if(control instanceof CCombo){
	    	CCombo combo = (CCombo) control;
	    	if(combo.getData() instanceof List){
	    		List datas = (List) combo.getData();
	    		for(int i=0; i<datas.size(); i++){
	    			if(value != null && isMatch(value, datas.get(i))){
	    				combo.select(i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    	if(value != null){
	    		combo.setText(value.toString());
	    	}
	    	//combo.setText(value);
	    }else if(control instanceof Composite){
	    	//是readio或者checkBox选择
	    	Composite composite = (Composite) control;
	    	composite.setData("value", value);
	    	Control[] children = composite.getChildren();
	    	String vs[] = null;
	    	if(value instanceof String){
	    		vs = ((String) value).split("[,]");
	    	}
	    	
	    	if(vs == null){
		    	for(int i=0; i<children.length; i++){
		    		if(children[i].getData() != null && isMatch(children[i].getData(), value) && children[i] instanceof Button){
		    			((Button) children[i]).setSelection(true);
		    		}
		    	}
	    	}else{
	    		for(int i=0; i<children.length; i++){
	    			for(int n=0; n<vs.length; n++){
			    		if(children[i].getData() != null && isMatch(children[i].getData(), vs[n]) && children[i] instanceof Button){
			    			((Button) children[i]).setSelection(true);
			    		}	
	    			}
		    	}
	    	}
	    }
	}
	
	/**
	 * 为控件添加获得焦点事件监听。
	 * 
	 * @param control
	 */
	public static ModelFocusListener addFocusListener(Control control, String focusColor, ActionContext actionContext){
		Color color = UtilSwt.createColor(control, focusColor, actionContext);	
		ModelFocusListener listener = new ModelFocusListener(color);
		control.addFocusListener(listener);
		return listener;
	}
	
	public static class ModelFocusListener implements FocusListener{
		Color focusColor;
		public ModelFocusListener(Color focusColor){
			this.focusColor = focusColor;
		}
		
		public void focusGained(FocusEvent event) {
			Control control = (Control) event.widget;
			
			if(control.getData("_focusBakColor") == null){
				control.setData("_focusBakColor", control.getBackground());
			}
			
			//设置背景色
			if(focusColor != null){
				control.setBackground(focusColor);
			}
		}

		public void focusLost(FocusEvent event){
			Control control = (Control) event.widget;
			if(control.getData("_focusBakColor") != null){
				control.setBackground((Color) control.getData("_focusBakColor"));
				control.setData("_focusBakColor", null);
			}
		}		
	}
	
	/**
	 * 比较两个值是否相等。
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean isMatch(Object obj1, Object obj2){
		if(obj1 == null || obj2 == null){
			return false;
		}
		
		if(obj1.equals(obj2) || obj1 == obj2){
			return true;
		}
		
		if(obj1 instanceof String && obj2 instanceof Number){
			return obj1.equals(obj2.toString());
		}else if(obj1 instanceof Number && obj2 instanceof String){
			return obj1.toString().equals(obj2);
		}else{
			return false;
		}
	}
}