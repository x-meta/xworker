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
package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.custom.textutils.StyledTextLineNumber;
import xworker.swt.design.Designer;
import xworker.swt.form.TextEditor;
import xworker.swt.util.SwtUtils;
import xworker.swt.widgets.TextCreator;
import xworker.swt.xworker.CodeAssistor;
import xworker.swt.xworker.Colorer;

public class StyledTextCreator {
	private static final String TAG = StyledTextCreator.class.getName();
	
    public static Object create(ActionContext actionContext){
    	if(SwtUtils.isRWT()) {
    		//RWT环境不支持StyledTtext
    		Thing self = actionContext.getObject("self");
    		self.set("style", "MULTI"); //使用多行
    		return TextCreator.create(actionContext);    		
    	}
    	
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("SINGLE".equals(selfStyle)){
			style |= SWT.SINGLE;
		}else if("MULTI".equals(selfStyle)){
			style |= SWT.MULTI;
		}
	
		
		if(UtilString.eq(self, "H_SCROLL", "true")){
			style |= SWT.H_SCROLL;
		}
		if(UtilString.eq(self, "V_SCROLL", "true")){
			style |= SWT.V_SCROLL;
		}
		if(UtilString.eq(self, "WRAP", "true")){
			style |= SWT.WRAP;
		}
		if(UtilString.eq(self, "READ_ONLY", "true")){
			style |= SWT.READ_ONLY;
		}
		if(UtilString.eq(self, "BORDER", "true")){
			style |= SWT.BORDER;
		}
		if(UtilString.eq(self, "FULL_SELECTION", "true")){
			style |= SWT.FULL_SELECTION;
		}
		 
		Composite parent = (Composite) actionContext.get("parent");
		StyledText text = new StyledText(parent, style);
		
		String textstr = UtilString.getString(self.getString("text"), actionContext);
		if(textstr != null)
		    text.setText(textstr);
		
		String align = self.getStringBlankAsNull("align");
		if(align != null){
			if(align.equals("LEFT")){
				text.setAlignment(SWT.LEFT);
			}else if(align.equals("CENTER")){
				text.setAlignment(SWT.CENTER);
			}else if(align.equals("RIGHT")){
				text.setAlignment(SWT.RIGHT);
			}
		}
		
		if(self.getBoolean("lineNumber")){
			StyledTextLineNumber.attach(text);
		}
		
		    
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), text);
		actionContext.peek().put("parent", text);
		actionContext.peek().put("control", text);
		
		Action action = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		action.run(actionContext);    
		
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(text, self.getMetadata().getPath(), actionContext);
		return text;       
	}
    
    public static void createColorer(ActionContext actionContext){
    	if(SwtUtils.isRWT()) {
    		return;
    	}
    	
    	Thing self = (Thing) actionContext.get("self");
    	StyledText parent = (StyledText) actionContext.get("parent");

    	String codeName = self.doAction("getCodeName", actionContext);//UtilString.getString(self.getString("codeName"), actionContext);
    	String codeType = self.doAction("getCodeType", actionContext);//UtilString.getString(self.getString("codeType"), actionContext);
    	try{
    		Colorer.attach(parent, codeName, codeType);
    	}catch(Throwable e){
    		Executor.info(TAG, "Attach colorer exception, path=" + self.getMetadata().getPath(), e);
    	}
    }
    
    public static void createTextEditor(ActionContext actionContext){
    	if(SwtUtils.isRWT() == false) {
	    	StyledText parent = (StyledText) actionContext.get("parent");	
	    	TextEditor.attach(parent);
    	}else {
    		
    	}
    }
    
    public static void createCodeAssistor(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Object parent = actionContext.get("parent");
    	CodeAssistor codeAssistor = null;
    	
    	ActionContext ac = self.doAction("getActionContext", actionContext);
    	if(ac == null) {
    		ac = actionContext;
    	}
    	if(parent instanceof StyledText){
    		 codeAssistor = CodeAssistor.attach(self, (StyledText) parent, actionContext);
    	}else{
    		if(parent instanceof Text){
    			codeAssistor = CodeAssistor.attach(self, (Text) parent, actionContext);
    		}
    	}
    	
    	if(self.getBoolean("initCache")){
    		codeAssistor.initCache(ac);
    	}
    	
    	actionContext.getScope(0).put(self.getMetadata().getName(), codeAssistor);
    }
}