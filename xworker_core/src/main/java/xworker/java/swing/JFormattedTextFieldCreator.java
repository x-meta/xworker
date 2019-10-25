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
package xworker.java.swing;

import java.awt.Container;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

public class JFormattedTextFieldCreator {
	public static void createFormatter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JFormattedTextField parent = (JFormattedTextField) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			AbstractFormatterFactory f = (AbstractFormatterFactory) child.doAction("create", actionContext);
			if(f != null){
				parent.setFormatterFactory(f);
			}
		}
	}
	
	public static JFormattedTextField create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JFormattedTextField comp = new JFormattedTextField();
		if(parent != null){
			parent.add(comp);
		}
		
		//初始化
		init(comp, self, null, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", comp);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), comp);
		return comp;
	}
	
	public static void init(JFormattedTextField comp, Thing thing, Container parent, ActionContext actionContext){
		JTextFieldCreator.init(comp, thing, parent, actionContext);
		
		Integer focusLostBehavior = null;
		String v = thing.getString("focusLostBehavior");
		if("COMMIT_OR_REVERT".equals(v)){
			focusLostBehavior = JFormattedTextField.COMMIT_OR_REVERT;
		}else if("REVERT".equals(v)){
			focusLostBehavior = JFormattedTextField.REVERT;
		}else if("COMMIT".equals(v)){
			focusLostBehavior = JFormattedTextField.COMMIT;
		}else if("PERSIST".equals(v)){
			focusLostBehavior = JFormattedTextField.PERSIST;
		}
		if(focusLostBehavior != null){
			comp.setFocusLostBehavior(focusLostBehavior);
		}
		
		String value = thing.getString("value");
		if(value != null){
			Object vobj;
			vobj = OgnlUtil.getValue(thing, "value", value, actionContext);
			if(vobj != null){
				comp.setValue(vobj);
			}
		}
	}
}