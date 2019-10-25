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
package xworker.swt.xworker;

import org.eclipse.swt.custom.StyledText;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.form.TextEditor;

public class CodeTextCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Thing parentThing = world.getThing("xworker.swt.custom.StyledText");
		StyledText textEditor = null;
		Designer.pushCreator(self);
		try{
			textEditor = (StyledText) parentThing.run("create", actionContext);
		}finally{
			Designer.popCreator();
		}
		
		//设置着色
		try{
			Colorer.attach(textEditor, self.getString("codeName"), self.getString("codeType"));
		}catch(Exception e){
			
		}
		
		//设置redoundo和其他
		TextEditor.attach(textEditor);
		
		Bindings bindings = actionContext.push(null);
		try{
		    bindings.put("parent", textEditor);
		    Thing fontThing = world.getThing("xworker.swt.xworker.CodeText/@font");
		    fontThing.doAction("create", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		Designer.attachCreator(textEditor, self.getMetadata().getPath(), actionContext);
		return textEditor;        
	}

}