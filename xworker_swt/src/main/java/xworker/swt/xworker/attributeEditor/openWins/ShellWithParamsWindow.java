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
package xworker.swt.xworker.attributeEditor.openWins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class ShellWithParamsWindow {
	/**
	 * 确定按钮的选择事件。
	 * 
	 * @param actionContext
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	public static void okButtonSelection(ActionContext actionContext) throws UnsupportedEncodingException{
		Thing thingForm = (Thing) actionContext.get("thingForm");		
		Map<String, Object> values = (Map<String, Object>) thingForm.doAction("getValues", actionContext);
		String shellPath = (String) values.get("shellPath");
		if(shellPath == null){
			Thing configThing = (Thing) actionContext.get("configThing");
			if(configThing != null){
				shellPath = configThing.getString("shellPath");
			}else{		
				shellPath = (String) actionContext.get("shellPath");
			}
		}
		String params = "";
		for(String key : values.keySet()){
			if("shellPath".equals(key)){
				continue;
			}
			
			 String value = (String) values.get(key);
	    	    if(value != null && !"".equals(value)){
	    	        if(!"".equals(params)){
	    	        	params = params + "&";
	    	        }
	    	        params = params + key + "=" + URLEncoder.encode(String.valueOf(value), "utf-8");
	    	    }
		}
		if(shellPath != null){
			if(!"".equals(params)){
				actionContext.put("result", shellPath + "?" + params);
			}else{
				actionContext.put("result", shellPath);
			}
		}else{
			actionContext.put("result", "");
		}
		
		Shell shell = (Shell) actionContext.get("shell");
		shell.dispose();
	}
	
	/**
	 * 初始化的函数。
	 * 
	 * @param actionContext
	 * @throws OgnlException
	 */
	public static void init(ActionContext actionContext) throws OgnlException{
		
		String value = (String) actionContext.get("value");
		String shellPath = value;
	    Map<String, String> params = new HashMap<String, String>();
		if(value != null){		    
		    if(value.indexOf("?") != -1){
		        shellPath = value.substring(0, value.indexOf("?"));
		        String paramsStr = value.substring(value.indexOf("?") + 1, value.length());
		        params = UtilString.getParams(paramsStr); 
		    }
		    
		    actionContext.getScope(0).put("shellPath", shellPath);
		    actionContext.getScope(0).put("params", params);
		}
		if(shellPath == null){
			shellPath = "";
		}
		/*
		Tree shellsTree = (Tree) actionContext.get("shellsTree");
	    boolean ok = false;
	    for(TreeItem item : shellsTree.getItems()){
	        ok = ok | initTreeItem(item, shellPath, params, actionContext);
	        if(ok){
	            break;
	        }
	    }
	    
	    if(!ok){		    		    	
	    	Thing thingForm = (Thing) actionContext.get("thingForm");
	    	thingForm.doAction("setDescriptor", actionContext, UtilMap.toMap(new Object[]{"descriptor", World.getInstance().getThing("xworker.swt.xworker.attributeEditor.openWins.ShellWithParams/@help/@ParamsThing")}));
	        thingForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", UtilMap.toMap(new Object[]{"shellPath",shellPath})}));
	        shellsTree.select(shellsTree.getItems()[0].getItems()[0]);
	    }*/
	}
	
	private static boolean  initTreeItem(TreeItem item, String shellPath, Map<String, String> params, ActionContext actionContext) throws OgnlException{
		Object data = item.getData();
		Object leaf = OgnlUtil.getValue("leaf", data);
	    if(leaf.equals(false) || "false".equals(leaf)){
	        boolean ok = false;
	        for(TreeItem childItem : item.getItems()){
	            ok = ok | initTreeItem(childItem, shellPath, params, actionContext);
	            if(ok){
	                return true;
	            }
	        }
	        
	        return ok;
	    }else{
	        String id = (String) OgnlUtil.getValue("id", data);
	        String configPath = id.substring(6, id.length());
	        Thing configThing = World.getInstance().getThing(configPath);
	        if(configThing != null){
	        	if(shellPath.equals(configThing.getString("shellPath"))){	        		
		            Thing paramsThing = configThing.getThing("ParamsThing@0");
		            if(paramsThing != null){                
	                	Thing thingForm = (Thing) actionContext.get("thingForm");
	                    thingForm.doAction("setDescriptor", actionContext, UtilMap.toMap(new Object[]{"descriptor", paramsThing}));
	                    thingForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));  
	                    item.getParent().select(item);
	                    return true;
		            }
	        	}
	        }   
	    }
	    
	    return false;
	}
}