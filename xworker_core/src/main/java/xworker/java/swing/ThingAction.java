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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	Thing thing;
	ActionContext actionContext;
	

	public ThingAction(Thing thing, ActionContext actionContext, String name){
		super(name);
		
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public ThingAction(Thing thing, ActionContext actionContext, String name, Icon icon){
		super(name, icon);
		
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		try{
			actionContext.push().put("event", e);
			thing.doAction("run", actionContext);			
		}finally{
			actionContext.pop();
		}
	}

}