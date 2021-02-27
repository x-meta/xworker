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
package xworker.html.jquery;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlConstants;
import xworker.html.HtmlUtil;
import xworker.lang.executor.Executor;

public class JQuery {
	private static final String TAG = JQuery.class.getName();
	
	public static void toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HtmlUtil.addHeader("jquery", "<script type=\"text/javascript\" src=\"${request.contextPath}/js/jquery/" + self.getString("version") + "\"></script>", actionContext);
	}
	
	public static void onReady(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing scriptThing =  (Thing) actionContext.get(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING);
		
		if(scriptThing != null){
			//OnReady应该只有一个
			boolean have = false;
			for(Thing child : scriptThing.getChilds()){
				if("JqueryOnReady".equals(child.getThingName())){
					have = true;
					for(Thing mchild : self.getChilds()){
						child.addChild(mchild, false);
						return;
					}
				}
			}
			
			if(!have){
				scriptThing.addChild(self, false);
			}
		}		
	}
	
	public static void addToOnReady(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getScriptThing", actionContext);
		if(thing != null){
			Thing scriptThing =  (Thing) actionContext.get(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING);			
			if(scriptThing != null){
				//OnReady应该只有一个
				boolean have = false;
				for(Thing child : scriptThing.getChilds()){
					if("JqueryOnReady".equals(child.getThingName())){
						have = true;
						child.addChild(thing, false);
						return;
					}
				}
				
				if(!have){
					Thing onReay = new Thing("xworker.html.jquery.OnReady");
					onReay.addChild(thing, false);
					scriptThing.addChild(onReay, false);
				}
			}		
		}else{
			Executor.warn(TAG, "scriptThing is null , action=" + self.getMetadata().getPath());
		}
	}
	
	public static String onReayToJavaScriptCode(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String code = "$(" + self.getString("element") + ").ready(function(){";
		for(Thing child : self.getChilds()){
			String childCode = (String) child.doAction("toJavaScriptCode", actionContext);
			String[] lines = childCode.split("[\n]");
			for(String line : lines){
				code = code + "\n" + "    " + line;
			}
		}
		code = code + "\n});";
		return code;
	}
}