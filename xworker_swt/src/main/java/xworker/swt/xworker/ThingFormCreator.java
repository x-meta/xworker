/*******************************************************************************
*Copyright 2007-2013 See AUTHORS file.
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
package xworker.swt.xworker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.design.Designer;
import xworker.swt.editor.EditorModifyListener;
import xworker.swt.events.SwtListener;
import xworker.swt.form.ThingDescriptorForm;

public class ThingFormCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        final Thing self = (Thing) actionContext.get("self");
		
		final ActionContext ac = new ActionContext();
		ac.setLabel("ThingForm");
		ac.put("parent", actionContext.get("parent"));
		ac.put("params", actionContext.get("params"));
		ac.put("parentActionContext", actionContext);
		ac.put("parentContext", actionContext);
		ac.put("variablesActionContext", self.doAction("getVariablesActionContext", actionContext));
		
		/*
		String defaultSelection = self.getString("defaultSelection");
		if(defaultSelection != null && !"".equals(defaultSelection)){
		    ac.put(defaultSelection, actionContext.get(defaultSelection));
		}*/
		
		ThingForm thingForm = new ThingForm();
		ac.put("defaultSelection", thingForm);
		
		//创建面板		
		Thing compositeThing = world.getThing("xworker.swt.xworker.ThingForm/@composite");		
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		Thing form = new Thing("xworker.swt.xworker.ThingForm");
		form.getAttributes().putAll(self.getAttributes());
		form.setData("formThing", self);
		form.setData("parent", composite);
		form.setData("parentActionContext", actionContext);
		form.set("extends", self.getMetadata().getPath());
		form.setData("actionContext", ac);
		form.setData("composite", composite);
		//取消了从外部传入ModifyListener的方法
		//String defaultModify = self.getStringBlankAsNull("defaultModify");
		Object defaultModifyObj = thingForm;//defaultModify == null ? null : actionContext.get(defaultModify);
		ThingFormModifyListener modifyListener = new ThingFormModifyListener(self, defaultModifyObj, actionContext);
		form.setData("defaultModify", modifyListener);
		form.set("defaultSelection", "defaultSelection");
		form.set("defaultModify", "defaultModify");
		ac.put("defaultModify", modifyListener);
		thingForm.setForm(form);
		
		/*
	    if(defaultModify != null){
	    	final Object defaultModifyObj = actionContext.get(defaultModify);
	    	
	    	if(defaultModifyObj != null){
	    		ModifyListener listener = new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent event) {
						if(defaultModifyObj instanceof ModifyListener){
							((ModifyListener) defaultModifyObj).modifyText(event);
						}else if(defaultModifyObj instanceof SwtListener){
							Event newEvent = new Event();
							if(event != null){
								newEvent.item = event.widget;
								newEvent.widget = event.widget;
							}
							((SwtListener) defaultModifyObj).handleEvent(newEvent);
						}
					}
	    			
	    		};
	    		form.setData("defaultModify", listener);
	    	}	    	
	    }*/
		composite.setData("formThing", form);
		
		try{
		    //创建子事物，通常是布局数据
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", composite);
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
			actionContext.pop();
		}
		
		//创建事物定义

		ac.getScope(0).put("self", form);
		ac.getScope(0).put("formThing", form);
		//form.H_SCROLL = "true";
		//form.V_SCROLL = "true";
			
		if(self.getString("thing") != null && !"".equals(self.getString("thing"))){
		    Thing thing = self.doAction("getThing", actionContext); 
		    if(thing != null){
		    	world.getThing(self.getString("thing"));
		    }
		    if(thing != null){
		    	form.doAction("setThing", ac, UtilMap.toParams(new Object[]{"thing", thing}));
		    }
		}else{
			Thing thing = world.getThing(self.getString("descriptorThing"));
			if(thing == null){
				thing = self.getThing("Thing@0");
			}
			if(thing != null){
				//生成一个新的事物来编辑
				Thing editThing = new Thing(thing.getMetadata().getPath());
				form.doAction("setThing", ac, UtilMap.toParams(new Object[]{"thing", editThing}));
				//form.doAction("setDescriptor", ac, UtilMap.toParams(new Object[]{"descriptor", thing}));
			}
		}
						
		modifyListener.setEnabled(true); //之前先关闭
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getString("name"), form);
		return composite;        
	}

    public static void setDescriptor(ActionContext actionContext){
       Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = (ActionContext) self.getData("actionContext");
		if(actionContext.get("descriptor") == null){
		    for(Control child : ((Composite) ac.get("thingComposite")).getChildren()){
		        child.dispose();
		    }
		    return;
		}
		
		ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
	    modifyListener.setEnabled(false);
	    try{
		    for(Control child : ((Composite) ac.get("thingComposite")).getChildren()){
		        child.dispose();
		    }
		    Bindings bindings = ac.push(null);
		    
		    Thing descriptor = (Thing) actionContext.get("descriptor");
		    self.put("descriptorPath", descriptor.getMetadata().getPath()); //描述者的路径
		    if(self.getData("defaultModify")  != null){
		    	bindings.put("modifyListener", self.getData("defaultModify"));
		    }
		    //bindings.put("thing", thing);
		    //初始化表单的值
		    Thing valueThing = new Thing(descriptor.getMetadata().getPath());
		    bindings.put("thingAttributes", valueThing.getAttributes());
		    bindings.put("parent", ac.get("thingComposite"));
		    
		    int column = self.getInt("column", -1);
		    if(column < 1){
		    	column = -1;
		    }
		    
		    
		    Designer.pushCreator((Thing) self.getData("formThing"));
			try{
			    Composite formComposite = (Composite) ThingDescriptorForm.createForm(ac, column);
			    ActionContext newContext = (ActionContext) formComposite.getData("actionContext");    
			    //log.info(self.defaultSelection + "=" + ac.get(self.defaultSelection));
			    String defaultSelection = self.getString("defaultSelection");
			    Thing model = (Thing) newContext.get("model");
			    if(model != null){
				    if(defaultSelection != null && !"".equals(defaultSelection)){		    	
				    	model.put("defaultSelection", defaultSelection);
				        newContext.put(defaultSelection, ac.get(defaultSelection));
				    }
				    model.doAction("init", newContext);
				    model.doAction("setValue", newContext);
			    }
			    self.setData("formContext", newContext);
			    self.setData("model", model);
			    ((Composite) ac.get("thingComposite")).layout();
			}finally{
				Designer.popCreator();
			}
		}finally{
		    ac.pop();
		    
		    modifyListener.setEnabled(true);		    
		}        
	}

    public static void setFocus(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	String descriptorPath = (String) self.get("descriptorPath");
    	Thing descriptor = World.getInstance().getThing(descriptorPath);
    	if(descriptor == null) {
    		return;
    	}
    	
    	ActionContext ac = (ActionContext) self.getData("formContext");
    	for(Thing attr : descriptor.getChilds("attribute")) {
    		String name = attr.getMetadata().getName();
    		Object control = ac.get(name + "Input");
    		if(control instanceof Control) {
    			((Control) control).forceFocus();
    			return;
    		}
    	}
    	
    	/*
    	
    	if(ac != null){
	    	Composite thingComposite = (Composite) ac.get("thingComposite");
	    	if(thingComposite != null){
	    		//thingComposite.getChildren()[0].setFocus();
	    		thingComposite.forceFocus();
	    		return;
	    	}
    	}*/
    	
    	Composite composite = (Composite) self.getData("composite");
    	if(composite != null){
    		composite.setFocus();
    	}
    }
    
    public static void setThing(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");    	
    	ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
    	modifyListener.setEnabled(false);
    	try {
			if(actionContext.get("thing") != null){
				Thing thing = (Thing) actionContext.get("thing");
			    Thing descriptor = thing.getDescriptor();
			    ActionContext ac = (ActionContext) self.getData("actionContext");
			    self.doAction("setDescriptor", actionContext, UtilMap.toParams(new Object[]{"descriptor", descriptor}));
	
			    ac.g().put("thing", thing);
			    ActionContext formContext = (ActionContext) self.getData("formContext");
			    formContext.g().put("thing", thing);
			    formContext.g().put("currentThing", thing);
			    formContext.getLabel();
			    
			    self.doAction("setValues", actionContext, UtilMap.toParams(new Object[]{"values", thing.getAttributes()}));
			}    
    	}finally {
    		modifyListener.setEnabled(true);
    	}
	}

    public static Thing getFormThing(ActionContext actionContext) {
    	Thing self = (Thing) actionContext.get("self");
    	String descriptorPath = self.getString("descriptorPath");
    	Thing thing = new Thing(descriptorPath);
    	Map<String, Object> values = self.doAction("getValues", actionContext);
    	thing.putAll(values);
    	return thing;
    }
    
    public static void setValues(ActionContext actionContext){
	    Thing self = (Thing) actionContext.get("self");
	    
	    ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
    	modifyListener.setEnabled(false);
    	try {
			ActionContext formContext = (ActionContext) self.getData("formContext");
			Thing model = (Thing) self.getData("model");
			self.put("values", actionContext.get("values"));
			Map<String, Object> values = actionContext.getObject("values");		
			if(formContext != null && model != null && actionContext.get("values") != null){
				//编辑器需要一个事物
				Thing thing = formContext.getObject("thing");
				if(thing == null){
					thing = new Thing();									
					formContext.g().put("thing", thing);
				}
				thing.getAttributes().putAll(values);
			    formContext.put("thingAttributes", actionContext.get("values"));    
			    model.doAction("setValue", formContext);
			}        
    	}finally {
    		modifyListener.setEnabled(true);
    	}
	}

    @SuppressWarnings("unchecked")
	public static void setPartialValues(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
    	modifyListener.setEnabled(false);
    	try {
			ActionContext formContext = (ActionContext) self.getData("formContext");
			Thing model = (Thing) self.getData("model");
			Map<String, Object> v = (Map<String, Object> ) model.doAction("getValue", formContext);//self.get("values");
			Map<String, Object> values = (Map<String, Object>) actionContext.get("values");
			if(v == null){
			    v = new HashMap<String, Object>();
			    //self.put("v", values);
			}
			v.putAll(values);
			if(formContext != null && model != null){
				//编辑器需要一个事物
				Thing thing = formContext.getObject("thing");
				if(thing == null){
					thing = new Thing();				
					formContext.g().put("thing", thing);
				}
				thing.getAttributes().putAll(values);
				
			    formContext.put("thingAttributes", v);    
			    model.doAction("setValue", formContext);
			}        
    	}finally {
    		modifyListener.setEnabled(true);
    	}
	}

    public static Object getValues(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
		ActionContext formContext = (ActionContext) self.getData("formContext");
		Thing model = (Thing) self.getData("model");
		if(formContext != null && model != null){
		    return model.doAction("getValue", formContext);
		}else{
		    return null;
		}        
	}

    public static boolean validate(ActionContext actionContext){
	    Thing self = (Thing) actionContext.get("self");
	    
		ActionContext formContext = (ActionContext) self.getData("formContext");
		Thing model = (Thing) self.getData("model");
		if(formContext != null && model != null){
		    return (Boolean) model.doAction("validate", formContext);
		}else{
		    return false;
		}        
	}
    
    static class ThingFormModifyListener extends EditorModifyListener{
    	Object defaultModifyObj;
    	Thing formThing;
    	ActionContext actionContext;
    	boolean enabled = false;
    	
    	public ThingFormModifyListener(Thing formThing, Object defaultModifyObj, ActionContext actionContext) {
    		super(null);
    		
    		this.formThing = formThing;
    		this.defaultModifyObj = defaultModifyObj;
    		this.actionContext = actionContext;
    	}
    	
		@Override
		protected void handlerEvent(TypedEvent event) {
			if(!enabled) {
				return;
			}
			
			if(defaultModifyObj instanceof ModifyListener){
				if(event instanceof ModifyEvent) {
					((ModifyListener) defaultModifyObj).modifyText((ModifyEvent) event);
				}else {
					Event newEvent = new Event();
					if(event != null){
						newEvent.item = event.widget;
						newEvent.widget = event.widget;
					}
					((ModifyListener) defaultModifyObj).modifyText(new ModifyEvent(newEvent) );
				}
			}else if(defaultModifyObj instanceof SwtListener){
				Event newEvent = new Event();
				if(event != null){
					newEvent.item = event.widget;
					newEvent.widget = event.widget;
				}
				((SwtListener) defaultModifyObj).handleEvent(newEvent);
			}
			
			formThing.doAction("modified", actionContext);
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			setEnable(enabled);
		}
    }
}