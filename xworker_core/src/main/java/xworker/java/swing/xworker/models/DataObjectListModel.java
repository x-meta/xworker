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
package xworker.java.swing.xworker.models;

import javax.swing.AbstractListModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataObjectListModel extends AbstractListModel{
	Thing dataObject;
	Thing condition;
	ActionContext actionContext;
	
	public DataObjectListModel(Thing dataObject, Thing condition, ActionContext actionContext){
		this.dataObject = dataObject;
		this.condition = condition;
		this.actionContext = actionContext;
		
	}
	
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}
}