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
package xworker.swt.model;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.form.HiddenInput;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilModel;
import xworker.swt.util.UtilModel.ModelFocusListener;
import xworker.swt.util.UtilSwt;

public class ModelCreator {
	private static Logger log = LoggerFactory.getLogger(ModelCreator.class);
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		self.doAction("initSelf", actionContext);
		
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getString("name"), self);
		return self;        
	}
    
    @SuppressWarnings("unchecked")
	public static void setPartialValues(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
 		
 		Map<String, Object> v = (Map<String, Object> ) self.doAction("getValue", actionContext);//self.get("values");
 		Map<String, Object> values = (Map<String, Object>) actionContext.get("values");
 		if(v == null){
 		    v = new HashMap<String, Object>();
 		    //self.put("v", values);
 		}
 		v.putAll(values);
 		
 		self.doAction("setValue", actionContext, self.getString("dataSource"), values);
    }

	@SuppressWarnings("rawtypes")
	public static void setValue(ActionContext actionContext) throws ParseException{
		Thing self = (Thing) actionContext.get("self");
		
		//取数据，根据dataSource、propertyName和default取值
		String dataSource = self.getString("dataSource");
		String propertyName = self.getString("propertyName");
		if(dataSource == null || dataSource == ""){
		    dataSource = "_model_it_";
		}
		Object data = actionContext.get(dataSource);
		if(data != null && propertyName != null && propertyName != ""){
		    if(data instanceof Map){
		        data = ((Map) data).get(propertyName);
		    }else{
		        try{        
		            data = OgnlUtil.getValue("[\"" + propertyName + "\"]", data);
		        }catch(Exception e){
		            //log.info("get value " + propertyName, e);
		        }
		    }
		}
		//log.info("propertyName=" + propertyName + ",data=" + data);
		if(data == null) {
		    data = self.getAttribute("defaultValue");
		}
		
		String swtControl = self.getString("swtControl");
		if(swtControl != null && swtControl != ""){
		    Object control = actionContext.get(swtControl);    
		    //log.info("data=" + data);
		    String viewPattern = self.getString("viewPattern");
		    if(viewPattern == null || viewPattern == ""){
		        viewPattern = self.getString("pattern");
		    }
		    String editPattern = self.getString("editPattern");
		    if(editPattern == null || editPattern == ""){
		        editPattern = self.getString("pattern");
		    }
		    //log.info("viewPattern=" + viewPattern + ",editPattern=" + editPattern);
		    if(control instanceof ActionContainer){                
		        ((ActionContainer) control).doAction("setValue", UtilMap.toParams(new Object[]{"model", self, "value", data, "viewPattern", viewPattern, "editPattern", editPattern}));
		    }else{
		        UtilModel.setValue(data, control, viewPattern, editPattern);
		    }
		}else{
		    //binding.setVariable
		}
		
		//设置子Model的值
		for(Thing child : self.getChilds()){
		    actionContext.push(null);   
		    actionContext.peek().put("_model_it_", data);
		    try{
		        /*try{         
		            if(child.propertyName != null && child.propertyName != ""){                             
		                if(data instanceof Map){                     
		                    Object v = data.get(child.propertyName);//OgnlUtil.getValue("[\"" + child.propertyName + "\"]", data);
		                    actionContext.peek().put("_model_it_", v);
		                }else{
		                    Object v = OgnlUtil.getValue("[\"" + child.propertyName + "\"]", data);
		                    actionContext.peek().put("_model_it_", v);
		                }
		            }
		        }catch (Exception e) {
		            //e.printStackTrace();
		        }*/
		    
		        child.doAction("setValue", actionContext);
		    }finally{
		        actionContext.pop();
		    }
		}        
	}

    public static Object getValue(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String dataType = self.getString("dataType");
    	if("Object".equals(dataType)) {
    		String dataSource = self.getString("dataSource");
    		if(dataSource == null || dataSource == ""){
    		    dataSource = "_model_it_";
    		}
    		Object data = actionContext.get(dataSource);
    		if(data != null) {
    			for(Thing child : self.getChilds()){        
    		        String propertyName = child.getString("propertyName");
    		        if(propertyName == null || "".equals(propertyName)){
    		            propertyName = child.getString("name");
    		        }
    		        
    		        //log.info("sss{}", propertyName);
    		        OgnlUtil.setValue(propertyName, data, child.doAction("getValue", actionContext));
    		        //data.put(propertyName, child.doAction("getValue", actionContext));
    		    }
    		}else {
    			//换成Map读取数据
    			dataType = "Map";
    		}
 		}
    	 
		if("Map".equals(dataType)){
			//把数据读取成map
		    Map<String, Object> data = new HashMap<String, Object>();
		    for(Thing child : self.getChilds()){        
		        String propertyName = child.getString("propertyName");
		        if(propertyName == null || "".equals(propertyName)){
		            propertyName = child.getString("name");
		        }
		        
		        //log.info("sss{}", propertyName);
		        data.put(propertyName, child.doAction("getValue", actionContext));
		    }
		
		    return data;
		}
		
		String swtControl = self.getString("swtControl");
		if(swtControl == null || swtControl.equals("")){
		    return null;
		}
		
		//println binding;
		Object control = actionContext.get(swtControl);
		if(control instanceof Control){			
		    try{        
		        return SwtUtils.getValue(control, self.getString("dataType"), self.getString("pattern"));
		    }catch(Exception e){        
		        //e.printStackTrace();
		        log.error("get value from control", e);
		    }
		}else if(control instanceof Thing){
		    try{
		        return ((Thing) control).doAction("getValue", actionContext);
		    }catch(Exception e){
		        log.error("get value from thing control", e);
		    }
		}else if(control instanceof ActionContainer){
		   
		    try{
		        Object value = ((ActionContainer) control).doAction("getValue");
		        //log.info("swtControl=" + self.swtControl + ",control=" + control + ",value=" + value);
		        return value;
		    }catch(Exception e){
		        log.error("get value from actionContainer control", e);
		    }
		}else if(control instanceof HiddenInput){
		    return ((HiddenInput) control).getValue();
		}else{
		    return control;
		}
		
		return null;       
	}

    @SuppressWarnings("unchecked")
	public static boolean validate(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//println "校验:" + self.name;
		String swtControl = self.getString("swtControl");
		if(swtControl == null || swtControl.equals("")){
		    boolean validateResult = true;
		    for(Thing child : self.getChilds()){
		    	Object result = child.doAction("validate", actionContext); 
		        if(result != null && ! (Boolean) result){
		            if(actionContext.get("validateType") == null || "default" == actionContext.get("validateType")){
		                return false;
		            }
		            validateResult = false;
		        }
		    }
		    
		    return validateResult;
		}else{
		    Object control = actionContext.get(swtControl);
		    if(control == null){
		        log.warn("control not exists : " + swtControl);
		    }
		
		    Map<String, Object> validateMessage = new HashMap<String, Object>();
		    boolean validateOk = true;
		    Action vaction = null;
		    String validationAction = self.getString("validationAction");
		    if(validationAction != null && !validationAction.equals("")){
		        vaction = World.getInstance().getAction(validationAction);
		    }
		    if(vaction != null){
		        //自定义校验方法
		        validateOk = (Boolean) vaction.run(actionContext, UtilMap.toParams(new Object[]{"control", control, "model", self, "message", validateMessage}));
		    }else{
		        //默认校验方法
		        validateOk = (Boolean) self.doAction("doValidate", actionContext, UtilMap.toParams(new Object[]{"message", validateMessage, "control",control}));
		    }
		    if(!validateOk){
		        if(control instanceof ActionContainer){
		             control = ((ActionContainer) control).doAction("getControl");
		        }
		        
		        if(control != null && control instanceof Control){
		        	Control ccontrol = (Control) control;
		            //保存原先的样式
		            if(ccontrol.getData("_model_oldBackground") == null){
		            	ccontrol.setData("_model_oldBackground", ccontrol.getBackground());
		            }
		            if(ccontrol.getData("_model_oldFont") == null){
		            	ccontrol.setData("_model_oldFont", ccontrol.getFont());
		            }
		            if(ccontrol.getData("_model_oldForeground") == null){
		            	ccontrol.setData("_model_oldForeground", ccontrol.getForeground());
		            }
		            //设置错误的样式
		            String invalidClass = self.getString("invalidClass");
		            Thing invalidStyle = null;
		            if(invalidClass != null && invalidClass != ""){
		            	Thing StyleManager = (Thing) actionContext.get("StyleManager");
		                if(StyleManager != null){
		                    invalidStyle = (Thing) StyleManager.get(invalidClass);
		                }    
		                if(invalidStyle == null){
		                    invalidStyle = World.getInstance().getThing(invalidClass);
		                }
		            }
		            if(invalidStyle == null){ //默认的
		                invalidStyle = World.getInstance().getThing("xworker.swt.model.ModelStyleSet/@errorStyle");
		            }
		            //应用能够错误的样式
		            invalidStyle.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", control}));
		            
		            //遇到校验错误的处理方式
		            if(actionContext.get("validateType") == null || "default" == actionContext.get("validateType")){
		                Shell tempShell = ccontrol.getShell();
		                MessageBox box = new MessageBox(tempShell, SWT.OK);
		                box.setText("校验失败");
		                if(validateMessage.get("message") != null){
		                    box.setMessage((String) validateMessage.get("message"));
		                }else{
		                    box.setMessage("数据校验失败，请输入正确的数据！doValidate没有返回错误消息。");
		                }
		                
		                if(SwtUtils.isRWT()) {
		                	SwtUtils.openMessageBox(box, null, actionContext);
		                }else {
		                	box.open();
		                }
		                
		                ccontrol.forceFocus();  
		            }else if(actionContext.get("validateMessages") != null){
		            	List<String> validateMessages = (List<String>) actionContext.get("validateMessages");
		                validateMessages.add((String) actionContext.get("message"));
		                
		            }
		        }else{
		            log.warn("control is null, can not show validate error message, name=" + self.getString("name") + ",control=" + actionContext.get(self.getString("swtControl")));
		        }
		                     
		        return false;
		    }else{
		        //校验成功
		        if(control instanceof Control){
		        	Control ccontrol = (Control) control;
		            //返回原先的样式
		            if(ccontrol.getData("_model_oldBackground") != null){
		            	ccontrol.setBackground((Color) ccontrol.getData("_model_oldBackground"));
		            	ccontrol.setData("_model_oldBackground", null);
		            }
		            if(ccontrol.getData("_model_oldFont") != null){
		            	ccontrol.setFont((Font) ccontrol.getData("_model_oldFont"));
		            	ccontrol.setData("_model_oldFont", null);
		            }
		            if(ccontrol.getData("_model_oldForeground") != null){
		            	ccontrol.setForeground((Color) ccontrol.getData("_model_oldForeground"));
		            	ccontrol.setData("_model_oldForeground", null);
		            }            
		        }
		
		        return true;
		    }
		}        
    }

    @SuppressWarnings("unchecked")
	public static boolean doValidate(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Map<String, Object> message = (Map<String, Object>) actionContext.get("message");
    	
		//取值
		Object value = null;
		try{
		    value = self.doAction("getValueForValidate", actionContext);
		}catch(Exception e){
		    log.error("get value for validae error", e);		    
		    message.put("type", "error");
		    message.put("message", self.getString("invalidText"));
		    message.put("control", self.getString("control"));
		    message.put("value", value);
		    return false;
		}
		
		//不能为空的校验
		String validateAllowBlank = self.getString("validateAllowBlank");
		if(validateAllowBlank != null && !validateAllowBlank.equals("")){
		    if(!self.getBoolean("validateAllowBlank") && (value == null || value == "")){
		        //不能为空
		        String text = self.getString("blankText");
		        if(text == null || text.equals("")){
		            text = UtilString.getString("label:xworker.swt.model.ModelMessages/@blankText", actionContext);
		        }
		        /*
		        try{
		            //text = UtilTemplate.processString(actionContext, text);
		        }catch(Exception e){
		            logger.warn("Model validate blank text template error", e);
		        }
		        */
		        
		        message.put("type", "blank");
		        message.put("message", text);
		        message.put("control", actionContext.get("control"));
		        message.put("value",  value);
		        return false;
		    }
		}
		
		if(value == null || value == ""){
		    return true;   //可以为空，为空后后面不用校验
		}
		
		//正则表达式的校验
		String valueStr = String.valueOf(value);
		String regex = self.getString("regex");
		if(regex != null && !regex.equals("")){
		    if(!valueStr.matches(regex)){
		        //不能为空
		    	String text = self.getString("regexText");
		        if(text == null || text.equals("")){
		            text = UtilString.getString("label:xworker.swt.model.ModelMessages/@regexMessage", actionContext);
		        }
		        /*
		        try{
		            text = UtilTemplate.processString(actionContext, text);
		        }catch(Exception e){
		            logger.warn("Model validate regexText text template error", e);
		        }*/
		        message.put("type", "regex");
		        message.put("message", text);
		        message.put("control", actionContext.get("control"));
		        message.put("value", value);
		        return false;
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
		        text = UtilString.getString("label:xworker.swt.model.ModelMessages/@errorMessage", actionContext);
		    }
		    /*
		    try{
		        text = UtilTemplate.processString(actionContext, text);
		    }catch(Exception e){
		        logger.warn("Model validate invalidText text template error", e);
		    }
		    */
		    message.put("type", "error");
		    message.put("message", text);
		    message.put("control", actionContext.get("control"));
		    message.put("value", value);
		    return false;
		}else{
		    return true;
		}        
    }

    public static void init(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		self.doAction("initSelf", actionContext);
		
		for(Thing child : self.getChilds()){
		    child.doAction("init", actionContext);
		}        
	}

    @SuppressWarnings("unchecked")
	public static void initSelf(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		//log.info("currentThing.getAttributes()=" + actionContext.get("thingAttributes").get("multSelect"));
		//初始化
		Object swtControl = actionContext.get(self.getString("swtControl"));
		
		Object defaultListener = actionContext.get(self.getString("defaultSelection"));
		if(defaultListener != null && defaultListener instanceof Listener){    
		    actionContext.put("_parentModelDefaultSelection", defaultListener);
		}else{
		    defaultListener = null;
		}
		
		if(defaultListener == null){
		    //查找父Model定义的默认事件
		    defaultListener = (Listener) actionContext.get("_parentModelDefaultSelection");
		}
		
		Object defaultModifiyListener = actionContext.get(self.getString("defaultModify"));
		if(defaultModifiyListener != null && defaultModifiyListener instanceof Listener){
		    actionContext.put("_parentModelDefaultModify", defaultModifiyListener);
		}else{
		    defaultModifiyListener = null;
		}
		if(defaultModifiyListener == null && defaultModifiyListener instanceof Listener){
		    defaultModifiyListener = actionContext.get("_parentModelDefaultModify");
		}
		
		if(swtControl != null){
		    if(swtControl instanceof ActionContainer){
		        Object control = ((ActionContainer) swtControl).doAction("getControl");
		        if(control != null){
		            swtControl = control;
		        }
		    }
		    		    
		    if(swtControl instanceof Control){
		    	String key = "__ModelCreator__inited__listeners__";
		    	//移除原来添加的监听，如果存在
		    	Control control = (Control) swtControl;
		    	Map<String, Object> listeners = (Map<String, Object>) control.getData(key);
		    	if(listeners != null) {
		    		ModelFocusListener fcus = (ModelFocusListener)listeners.get("ModelFocusListener");
		    		if(fcus != null) {		    			
		    			control.removeFocusListener(fcus);
		    		}
		    		
		    		Listener listener = (Listener) listeners.get("DefaultSelection");
		    		if(listener != null) {
		    			control.removeListener(SWT.DefaultSelection, listener);
		    		}
		    		
		    		listener = (Listener) listeners.get("Modify");
		    		if(listener != null) {
		    			control.removeListener(SWT.Modify, listener);
		    		}
		    		listeners.clear();		    		
		    	}else {
		    		listeners = new HashMap<String, Object>();
		    	}
		    	
		    	control.setData(key, listeners);
		        //添加事件监听
		    	ModelFocusListener listener = UtilModel.addFocusListener((Control) swtControl, self.getString("focusColor"), actionContext);
		        listeners.put("ModelFocusListener", listener);
		        
		        //控件颜色
		        UtilSwt.setBackground((Control) swtControl, self.getString("background"), actionContext);
		        UtilSwt.setForeground((Control) swtControl, self.getString("foreground"), actionContext);
		        if(self.getBoolean("required")){
		            //log.info("set required color" + self.requiredColor);
		            UtilSwt.setBackground((Control) swtControl, self.getString("requiredColor"), actionContext);
		        }
		    
		        //log.info(defaultListener);
		        //设置默认事件    
		        if(defaultListener != null && defaultListener instanceof Listener){    
		            ((Control) swtControl).addListener(SWT.DefaultSelection, (Listener) defaultListener);
		            listeners.put("DefaultSelection", defaultListener);
		        }
		        if(defaultModifiyListener != null && defaultModifiyListener instanceof Listener){
		            ((Control) swtControl).addListener(SWT.Modify, (Listener) defaultModifiyListener);
		            listeners.put("Modify", defaultModifiyListener);
		        }
		        
		        //设置焦点
		        if(self.getBoolean("focus")){
		            ((Control) swtControl).setFocus();
		        }
		    }else if(swtControl instanceof ActionContainer){
		        ((ActionContainer) swtControl).doAction("init", UtilMap.toParams(new Object[]{"model", self, "defaultListener", defaultListener, "defaultModifiyListener", defaultModifiyListener}));
		    }
		}        
	}

    public static void getValuesForSelect(ActionContext actionContext){
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getValueForValidate(ActionContext actionContext) throws ParseException{
    	Thing self = (Thing) actionContext.get("self");
    	
		if("Map".equals(self.getString("dataType"))){
		    Map data = new HashMap();
		    for(Thing child : self.getChilds()){        
		        String propertyName = child.getString("propertyName");
		        if(propertyName == null || propertyName.equals("")){
		            propertyName = child.getString("name");
		        }
		        
		        //log.info("sss{}", propertyName);
		        data.put(propertyName, child.doAction("getValue", actionContext));
		    }
		
		    return data;
		}
		
		String swtControl = self.getString("swtControl");
		if(swtControl == null || swtControl.equals("")){
		    return null;
		}
		
		//println binding;
		Object control = actionContext.get(swtControl);
		if(control instanceof Control){
		    return SwtUtils.getValue(control, self.getString("dataType"), self.getString("pattern"));
		}else if(control instanceof Thing){
		    return ((Thing) control).doAction("getValue", actionContext);
		}else if(control instanceof ActionContainer){
		    Object value = ((ActionContainer) control).doAction("getValue");
		    return value;
		}else if(control instanceof HiddenInput){
		    return ((HiddenInput) control).getValue();
		}else{
		    return control;
		}
	}

}