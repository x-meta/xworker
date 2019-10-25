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
package xworker.lang.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import xworker.lang.VariableDesc;

public class ActionActions {
	//private static Logger logger = LoggerFactory.getLogger(ActionActions.class);
	
	/**
	 * Detach自己并且生成属性模板。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static Thing processAttributeTemplate(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		return detach(self, null, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static Thing detach(Thing thing, Thing detachedParentThing, ActionContext actionContext) throws IOException, TemplateException{
		Thing newThing = new Thing();
		newThing.getAttributes().putAll(thing.getAttributes());
		newThing.getMetadata().setId(thing.getMetadata().getId());
		newThing.getMetadata().setPath(thing.getMetadata().getPath());
		newThing.getMetadata().setCategory(thing.getMetadata().getCategory());
		newThing.getMetadata().setLastModified(thing.getMetadata().getLastModified());
		
		String key = "__actionAttributeTemplate__";
		Long lastmodified = (Long) thing.getData(key + "LastModified");
		List<TemplateEntry> templates = (List<TemplateEntry>) thing.getData(key + "Templates");
		
		Map<String, Object> attributes = newThing.getAttributes();
		if(templates == null || lastmodified == null || lastmodified != thing.getMetadata().getLastModified()){
			templates = new ArrayList<TemplateEntry>();			
			for(String name : attributes.keySet()){
				Object value = attributes.get(name);
				if(value != null && value instanceof String){
					String str = (String) value;
					if(str.indexOf("$") != -1 || str.indexOf("<#") != -1 || str.indexOf("<@") != -1){
						//Freemakrer的相关标签，表示是模板
						ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
						InputStreamReader reader = new InputStreamReader(bin);
						
						TemplateEntry entry = new TemplateEntry();
						entry.name = name;
						entry.template = new Template(thing.getMetadata().getPath() + ":" + name, reader, null);
						templates.add(entry);
					}
				}
			}
			
			thing.setData(key + "LastModified", thing.getMetadata().getLastModified());
			thing.setData(key + "Templates", templates);
		}
		
		for(TemplateEntry entry : templates){
			ByteArrayOutputStream out = new ByteArrayOutputStream();   
			try{
				entry.template.process(actionContext, new OutputStreamWriter(out));
			}catch(TemplateException e){
				//logger.warn("Generate attribute template error, path=" + thing.getMetadata().getPath());
				throw new ActionException("Generate attribute template error, path=" + thing.getMetadata().getPath(), e);
			}
			
			newThing.put(entry.name, out.toString());
		}
		
		if(detachedParentThing != null){
			detachedParentThing.getChilds().add(newThing);
		}
		
		if(thing.getBoolean("childsAttributeTemplate")){
			for(Thing child : thing.getChilds()){
				detach(child, newThing, actionContext);
			}
		}else{
			for(Thing child : thing.getChilds()){
				newThing.addChild(child, false);
			}
		}
		
		return newThing;
	}
	
	static class TemplateEntry{
		String name;
		Template template;
	}
	
	public static VariableDesc createSeflVarDesc(ActionContext actionContext) {
		Thing thing = actionContext.getObject("thing");
		if(thing == null) {
			return null;
		}
		
		Thing parent = thing.getParent();
		if(parent == null || parent.getThingName().equals("actions")){
		    return new VariableDesc("self", VariableDesc.OBJECT, "org.xmeta.Thing", null);
		}

		parent = parent.getParent();
		if(parent == null){
		    return new VariableDesc("self", VariableDesc.OBJECT, "org.xmeta.Thing", null);
		}else{
		    return new VariableDesc("self", VariableDesc.THING, null, parent);
		}
	}
	
	public static List<VariableDesc> createVariableDefineDescs(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		if(thing == null) {
			return null;
		}
		
		List<VariableDesc> list = new ArrayList<VariableDesc>();
		for(Thing child : thing.getChilds()) {
			//获得被动定义的变量
			List<VariableDesc> vars = VariableDesc.getVariableDescs(child, true,  actionContext);
			if(vars != null) {
				VariableDesc.addAll(vars, list);
			}
		}
		
		return list;
	}
	
	public static List<VariableDesc> createDefineAcionsDescs(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		if(thing == null) {
			return null;
		}
		
		List<VariableDesc> list = new ArrayList<VariableDesc>();
		for(Thing child : thing.getChilds()) {
			//获得被动定义的变量
			VariableDesc var = new VariableDesc(child.getMetadata().getName(), VariableDesc.ACTION, null, child);
			list.add(var);
		}
		
		return list;
	}
}