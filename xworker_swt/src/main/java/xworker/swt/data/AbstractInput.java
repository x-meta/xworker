package xworker.swt.data;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.util.SwtUtils;

public abstract class AbstractInput implements Input, DisposeListener, FocusListener{
	Thing thing;
	ActionContext actionContext;
	Control control;
	Color foreground;
	Color background;
	
	/** 当控件获取焦点时的背景色 */
	Color focusBackground;
	/** 当控件标记时的背景色*/
	Color markTipBackground;
	/** 控件原本的背景色 */
	Color controlBackground;
	/** 是否正在markTip的状态 */
	boolean markTip = false;
	String controlToolTip;
	
	String viewPattern;
	String editPattern;
	
	Object value;
	
	public AbstractInput(Control control, Thing thing, ActionContext actionContext) {
		this.control = control;
		this.thing = thing;
		this.actionContext = actionContext;
		viewPattern = thing.getStringBlankAsNull("viewPattern");
		editPattern = thing.getStringBlankAsNull("editPattern");
		
		if(control != null) {
			foreground = SwtUtils.createColor(control, thing.getStringBlankAsNull("foreground"), actionContext);
			if(foreground != null) {
				control.setForeground(foreground);
			}
			
			background = SwtUtils.createColor(control, thing.getStringBlankAsNull("background"), actionContext);
			if(background != null) {
				control.setBackground(background);
			}
			
			focusBackground = SwtUtils.createColor(control,  thing.getStringBlankAsNull("focusColor"), actionContext);
			
			this.controlBackground = control.getBackground();
			this.controlToolTip = control.getToolTipText();
			
			control.addDisposeListener(this);
			control.addFocusListener(this);
			
			if(thing.getBoolean("focus")) {
				control.getDisplay().asyncExec(new Runnable() {
					public void run() {
						control.forceFocus();
					}
				});				
			}
			
			
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent arg0) {
		if(focusBackground != null) {
			focusBackground.dispose();
		}
		
		if(markTipBackground != null) {
			markTipBackground.dispose();
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		if(markTip && markTipBackground != null && markTipBackground.isDisposed() == false) {
			control.setBackground(markTipBackground);
			return;
		}
		
		if(focusBackground != null && focusBackground.isDisposed() == false) {
			control.setBackground(focusBackground);
			return;
		}		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if(markTip && markTipBackground != null && markTipBackground.isDisposed() == false) {
			return;
		}
		
		control.setBackground(controlBackground);		
		
		if(thing.getBoolean("validateOnBlur")) {
			String message = validate();
			if(message != null && !"".equals(message)) {
				markTip(message);
			}
		}
	}

	@Override
	public void markTip(String message) {
		this.markTip = true;
		if(markTip && markTipBackground != null && markTipBackground.isDisposed() == false) {
			control.setBackground(markTipBackground);
			return;
		}
		if(message != null) {
			control.setToolTipText(message);
			
		}
	}

	@Override
	public void removeMarkTip() {
		this.markTip = false;
		if(control != null && control.isDisposed() == false) {
			if(control.isFocusControl() && focusBackground != null && focusBackground.isDisposed() == false) {
				control.setBackground(focusBackground);
			}else {
				control.setBackground(controlBackground);
			}
		}
	}

	@Override
	public String validate() {
		return thing.doAction("doValidate", actionContext, "input", this);
	}
		
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	protected abstract void doSetValue(Object value);

	public static String doValidate(ActionContext actionContext) {
		AbstractInput input = actionContext.getObject("input");
		
		Thing self = input.thing;
		//取值
		Object value = null;
		try{
		    value = input.getValue();
		}catch(Exception e){
		    return UtilString.getString(self.getString("invalidText"), actionContext);
		}
		
		//不能为空的校验
		String validateAllowBlank = self.getString("validateAllowBlank");
		if(validateAllowBlank != null && !validateAllowBlank.equals("")){
		    if(!self.getBoolean("validateAllowBlank") && (value == null || value == "")){
		        //不能为空
		        String text = self.getString("blankText");
		        if(text == null || text.equals("")){
		            text = UtilString.getString("label:xworker.swt.model.ModelMessages/@blankText", actionContext);
		        }else {
		        	return UtilString.getString(text, actionContext);
		        }
		    }
		}
		
		if(value == null || value == ""){
		    return null;   //可以为空，为空后后面不用校验
		}
		
		//正则表达式的校验
		String valueStr = String.valueOf(value);
		String regex = self.getString("regex");
		if(regex != null && !regex.equals("")){
		    if(!valueStr.matches(regex)){
		        //不能为空
		    	String text = self.getString("regexText");
		        if(text == null || text.equals("")){
		            return UtilString.getString("label:xworker.swt.model.ModelMessages/@regexMessage", actionContext);
		        }else {
		        	return UtilString.getString(text, actionContext);
		        }
		    }
		}
		
		//最大长度、最小长度、正负和数大小的校验
		boolean error = false;
		//最大长度
		String maxLength = self.getString("maxLength");
		if(maxLength != null && !maxLength.equals("")){
		    if(valueStr.length() > self.getInt("maxLength")){
		        error = true;
		    }
		}
		
		//最小长度
		String minLength = self.getString("minLength");
		if(!error && minLength != null && !minLength.equals("")){
		    if(valueStr.length() < self.getInt("minLength")){
		        error = true;
		    }
		}
		
		//数字校验
		String type = self.getString("type");
		if(type != null && "bigDecimal,bigInteger,byte,char,double,float,int,long,short".indexOf(type) != -1){
		    //允许小数
			String allowDecimals = self.getString("allowDecimals");
		    if(!error && allowDecimals != null && !allowDecimals.equals("")){
		        if(value instanceof Number && !self.getBoolean("allowDecimals") && !valueStr.matches("[\\d]+")){
		            error = true;
		        }
		    }
		    
		    double numberValue = 0;
		    if(value instanceof String){
		        try{
		            numberValue = Double.parseDouble((String) value);
		        }catch(Exception e){
		        }
		    }
		    
		    if(!(value instanceof Number)){
		        numberValue = 0;
		    }
		    //log.info("error1=" + error + ",self.allowNegative=" + self.allowNegative);
		    //允许负数
		    String allowNegative = self.getString("allowNegative");
		    if(!error && allowNegative != null && !allowNegative.equals("")){
		        if(!self.getBoolean("allowNegative") && numberValue < 0){
		            //log.info("不允许为负数,name=" + self.name);
		            error = true;
		        }
		    }
		    
		    //最大值
		    String maxValue = self.getString("maxValue");
		    if(!error && maxValue != null && !maxValue.equals("")){
		        if(numberValue > self.getDouble("maxValue")){
		            error = true;
		        }
		    }
		    
		    //最小值
		    String minValue = self.getString("minValue");
		    if(!error && minValue != null && !minValue.equals("")){
		        if(numberValue < self.getDouble("minValue")){
		            error = true;
		        }
		    }
		}
		
		if(error){
		    String text = self.getString("invalidText");
		    if(text == null || text.equals("")){
		        return UtilString.getString("label:xworker.swt.model.ModelMessages/@errorMessage", actionContext);
		    }else {
	        	return UtilString.getString(text, actionContext);
	        }
		}else{
		    return null;
		}        
	}
}
