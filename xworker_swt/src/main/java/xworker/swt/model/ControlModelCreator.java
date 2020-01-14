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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import xworker.swt.util.DialogCallback;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilModel;
import xworker.swt.util.UtilSwt;

public class ControlModelCreator {
	private static Logger log = LoggerFactory.getLogger(ControlModelCreator.class);
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		self.doAction("init", actionContext);
		
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getString("name"), self);
		return self;        
	}

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
		    try{
		        data = OgnlUtil.getValue("[\"" + propertyName + "\"]", data);
		    }catch(Exception e){
		        //log.info("get value " + propertyName, e);
		    }
		}
		
		if(data == null) {
		    data = self.getAttribute("defaultValue");
		}
		
		String swtControl = self.getString("swtControl");
		if(swtControl != null && swtControl != ""){
		    Object control = actionContext.get(swtControl);
		    UtilModel.setValue(data, control, self.getString("viewPattern"), self.getString("editPattern"));
		}else{
		    //binding.setVariable
		}
		
		//设置子Model的值
		for(Thing child : self.getAllChilds()){
		    actionContext.push(null);    
		    try{
		        try{         
		        	String childCropertyName = child.getString("propertyName");
		            if(childCropertyName != null && childCropertyName != ""){
		                Object v = OgnlUtil.getValue("[\"" + childCropertyName+ "\"]", data);
		                actionContext.peek().put("_model_it_", v);
		            }
		        }catch (Exception e) {
		            //e.printStackTrace();
		        }
		    
		        child.doAction("setValue", actionContext);
		    }finally{
		        actionContext.pop();
		    }
		}        
    }

    public static Object getValue(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		if("Map".equals(self.getString("dataType"))){
		    HashMap<String, Object> data = new HashMap<String, Object>();
		    for(Thing child : self.getChilds()){        
		        data.put(child.getString("propertyName"), child.doAction("getValue", actionContext));
		    }
		    
		    return data;
		}
		
		String swtControl = self.getString("swtControl");
		if(swtControl == null || swtControl == ""){
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
		}else{
		    return control;
		}
		
		return null;        
    }

    public static boolean validate(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		//println "校验:" + self.name;
    	String swtControl = self.getString("swtControl");
		if(swtControl == null || swtControl == ""){
		    for(Thing child : self.getChilds()){
		        if(!(Boolean) child.doAction("validate", actionContext)){
		            return false;
		        }
		    }
		    
		    return true;
		}else{
		    Object control = actionContext.get(swtControl);
		    if(control == null){
		        log.warn("control not exists : " + swtControl);
		    }
		    
		    if(!(Boolean) self.doAction("doValidate", actionContext)){
		        //println "dfdfdfd";
		    	String errorColor = self.getString("errorColor");
		    	Control swtcontrol = (Control) control;
		        if(errorColor != null && errorColor != ""){
		            //保存旧的背景
		            if(swtcontrol.getData("oldBackground") == null){
		            	swtcontrol.setData("oldBackground", swtcontrol.getBackground());
		            }
		            Color bkColor = ResourceManager.createColor(swtcontrol, errorColor, actionContext);
		            if(bkColor != null){
		            	swtcontrol.setBackground(bkColor);
		            }
		        }
		        
		        Shell tempShell = swtcontrol.getShell();
		        MessageBox box = new MessageBox(tempShell, SWT.OK);
		        box.setText(UtilString.getString("lang:d=校验&en=Validate", actionContext));
		        String errorMessage = self.getString("errorMessage");
		        if(!"".equals(errorMessage) && errorMessage != null){
		            String message = UtilString.getString(errorMessage, actionContext);
		            box.setMessage(message);
		            /*if(self.errorMessage.startsWith("\"")){
		                String mes = self.errorMessage.substring(1, self.errorMessage.length() - 1);
		                box.setMessage(mes);
		            }else{
		                box.setMessage(binding.getVariable(self.errorMessage));
		            }*/
		        }else{
		            box.setMessage("数据校验失败，请输入正确的数据！\n数据类型: ${self.type}");
		        }
		        
		        if(SwtUtils.isRWT()) {        	
		        	Thing swt = World.getInstance().getThing("xworker.swt.SWT");
		        	swt.doAction("openMessageBoxRWT", actionContext, "messageBox", box, "callback", new DialogCallback() {
						@Override
						public void dialogClosed(int returnCode) {							
						}
		        	});
		        }else {
		        	box.open();
		        }
		        //tempShell.dispose();
		
		        swtcontrol.forceFocus();               
		        return false;
		    }else{
		        //println "dfdf";
		        if(control instanceof Control){
		        	Control swtcontrol = (Control) control;
		            if(swtcontrol.getData("oldBackground") != null){
		            	swtcontrol.setBackground((Color) swtcontrol.getData("oldBackground"));
		            	swtcontrol.setData("oldBackground", null);
		                return true;
		            }
		            
		            if(!swtcontrol.getEnabled()) return true;
		            
		            String requiredColor = self.getString("requiredColor");
		            String background = self.getString("background");
		            if(self.getBoolean("optional") == false && !UtilString.isNull(requiredColor)){
		                //必须颜色
		                Color bkColor = ResourceManager.createColor(swtcontrol, requiredColor, actionContext);
		                if(bkColor != null){
		                	swtcontrol.setBackground(bkColor);
		                }
		            }else if(background != null && background != ""){
		                Color bkColor = ResourceManager.createColor(swtcontrol, background, actionContext);
		                if(bkColor != null){
		                	swtcontrol.setBackground(bkColor);
		                }
		            }else{
		                //默认颜色
		                //Color bkColor = new Color(null, 255, 255, 255);
		                //control.setBackground(bkColor);
		            }
		        }
		
		        return true;
		    }
		}        
	}

    public static boolean doValidate(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Object control = actionContext.get(self.getString("swtControl"));
		if(control == null){
		    //log.warn("control not exists : " + self.swtName);
		}
		
		boolean va = SwtUtils.validate(control, self.getString("type"), self.getString("pattern"), self.getBoolean("required"));
		if(va == false){
		    log.info(self.getString("name"));
		}
		return va;       
	}

    public static void init(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		//初始化
		Object control = actionContext.get(self.getString("swtControl"));
		if(control != null && control instanceof Control){
			Control swtControl = (Control) control;
			
		    //添加事件监听
		    UtilModel.addFocusListener(swtControl, self.getString("focusColor"), actionContext);
		    
		    //控件颜色
		    UtilSwt.setBackground(swtControl, self.getString("background"), actionContext);
		    UtilSwt.setForeground(swtControl, self.getString("foreground"), actionContext);
		    if(self.getBoolean("required")){
		        UtilSwt.setBackground(swtControl, self.getString("requiredColor"), actionContext);
		    }
		
		    //设置默认事件
		    Listener defaultListener = (Listener) actionContext.get(self.getString("defaultSelection"));
		    if(defaultListener == null){
		        //查找父Model定义的默认事件
		        defaultListener = (Listener) actionContext.get("_parentModelDefaultSelection");
		    }
		    if(defaultListener != null){
		        swtControl.addListener(SWT.DefaultSelection, defaultListener);
		    }
		    Listener defaultModifiyListener = (Listener) actionContext.get(self.getString("defaultModify"));
		    if(defaultModifiyListener == null){
		        defaultModifiyListener = (Listener) actionContext.get("_parentModelDefaultModify");
		    }
		    if(defaultModifiyListener != null){
		        swtControl.addListener(SWT.Modify, defaultModifiyListener);
		    }
		    
		    //设置焦点
		    if(self.getBoolean("focus")){
		        swtControl.setFocus();
		    }
		}        
    }

}