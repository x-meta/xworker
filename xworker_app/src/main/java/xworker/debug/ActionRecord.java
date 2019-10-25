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
package xworker.debug;

import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionRecord {
	public long time = System.currentTimeMillis();
	public Action action;
	public Object caller;
	public Map<String, Object> parameters;
	public long namoTime;
	public boolean successed;
	public Thread thread = Thread.currentThread();
	public ActionContext actionContext;
	public int scopeSize = 0;
	public String method;
	public String type;
	public String thingPath;
	
	public ActionRecord(Action action, Object caller, ActionContext actionContext, Map<String, Object> parameters, long namoTime, boolean successed){
		this.thingPath = action.getThing().getMetadata().getPath();
		this.method = action.getThing().getMetadata().getName();
		this.parameters = parameters;
		this.namoTime = namoTime;
		this.successed = successed;
		scopeSize = actionContext.getScopesSize();
		this.type = "action";
	}
	
	public ActionRecord(Action action, Object caller, ActionContext actionContext, Map<String, Object> parameters, long namoTime, boolean successed, int scope){
		this.thingPath = action.getThing().getMetadata().getPath();
		this.method = action.getThing().getMetadata().getName();
		this.action = action;
		this.caller = caller;
		this.parameters = parameters;
		this.namoTime = namoTime;
		this.successed = successed;
		scopeSize = scope;
		this.type = "action";
	}
	
	public ActionRecord(Thing thing, String method, ActionContext actionContext, Map<String, Object> parameters, long namoTime, boolean successed){
		this.thingPath = thing.getMetadata().getPath();
		this.method = thing.getMetadata().getName() + "(" + method + ")";
		this.parameters = parameters;
		this.namoTime = namoTime;
		this.successed = successed;
		scopeSize = actionContext.getScopesSize();
		this.type = "thing";
	}
	
	public ActionRecord(Thing thing, String method, ActionContext actionContext, Map<String, Object> parameters, long namoTime, boolean successed, int scope){
		this.thingPath = thing.getMetadata().getPath();
		this.method = thing.getMetadata().getName() + "(" + method + ")";
		this.parameters = parameters;
		this.namoTime = namoTime;
		this.successed = successed;
		scopeSize = scope;
		this.type = "thing";
	}
}