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
package xworker.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.data.InputDataManager;
import xworker.swt.data.inputdatamanagers.ThingValueComboIDM;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ComboCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfType = self.getString("type");
		if("DROP_DOWN".equals(selfType)){
			style |= SWT.DROP_DOWN;
		}else if("SIMPLE".equals(selfType)){
			style |= SWT.SIMPLE;
		}		
		
		if(self.getBoolean("READ_ONLY")){
		    style = SWT.READ_ONLY;
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		Combo combo  = new Combo(parent, style);
		List<Thing> values = self.getChilds("value"); 
		List<Object> vs = new ArrayList<Object>();
		for(Thing v : values){
		    combo.add(UtilString.getString(v.getMetadata().getLabel(), actionContext));
		    vs.add(v.getObject("value"));
		}
		if(values.size() > 0) {
			InputDataManager.setInputDataManager(combo, new ThingValueComboIDM(combo, values, actionContext));
		}
		
		String text = self.getStringBlankAsNull("text");
		if(text != null) {
			combo.setText(text);
		}
		combo.setData(vs);
		
		Bindings bindings = actionContext.push(null);
		bindings.put("control", combo);
		try{
		    self.doAction("super.init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), combo);
		actionContext.peek().put("parent", combo);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		if(self.getStringBlankAsNull("selectIndex") != null){
			combo.select(self.getInt("selectIndex"));
		}
		Designer.attach(combo, self.getMetadata().getPath(), actionContext);
		return combo;        
	}

}