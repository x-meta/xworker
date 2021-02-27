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
package xworker.html.extjs.tools;

import java.io.File;

import org.xmeta.Thing;
import org.xmeta.World;


public class InitThingEvents {
	public static void main(String args[]){
		//重新初始化Extjs对象的事件列表
		try{
			World world = World.getInstance();			
			world.init("E:\\git\\xworker\\xworker\\");
			
			File dir = new File("D:\\文档\\extjs\\output\\");
			for(File html : dir.listFiles()){
				if(html.isFile() && html.getName().endsWith(".html")){
					String name = html.getName();
					//System.out.println(name);
					
					String objectName = name.substring(0, name.lastIndexOf("."));
					String objName = objectName;
					int index = objectName.indexOf(".");
					if(index != -1){
						objName = objectName.substring(index + 1, objectName.length());
					}
					ExtjsDoc doc = new ExtjsDoc(html);
										
					Thing thing = World.getInstance().getThing("xworker.html.extjs." + objectName);
					if(thing != null && thing.getThing("listeners@0") == null && doc.events.size() > 0){
						Thing listeners = new Thing("xworker.lang.MetaDescriptor3/@thing");
						
						String extendsStr = "xworker.html.extjs.ExtThing";
						for(Thing ext : thing.getExtends()){
							if(ext.getMetadata().getPath().equals("xworker.html.extjs.ExtThing")){
								continue;
							}
							
							extendsStr = extendsStr + "," + ext.getMetadata().getPath() + "//@listeners";
						}
						
						listeners.put("extends", extendsStr);
						listeners.put("name", "listeners");
						listeners.put("group", "Listeners." + objName);
						listeners.getMetadata().setId("listeners");
												
						for(ExtjsMethod event : doc.events){
							if(event.source != null && !"".equals(event.source)){
								continue;
							}
							
							Thing e = new Thing("xworker.lang.MetaDescriptor3/@thing");
							e.put("extends", "xworker.html.extjs.Function");
							e.put("description", event.doc);
							e.put("name", event.name);
							e.put("group", objName);
							e.getMetadata().setId(event.name);							
							
							Thing attrName = new Thing("xworker.lang.MetaDescriptor3/@attribute");
							attrName.put("name", "name");
							e.addChild(attrName);
							
							Thing attrLabel = new Thing("xworker.lang.MetaDescriptor3/@attribute");
							attrLabel.put("name", "label");
							e.addChild(attrLabel);
							
							Thing attrParams = new Thing("xworker.lang.MetaDescriptor3/@attribute");
							attrParams.put("name", "params");
							attrParams.put("default", event.getArgs());
							attrParams.put("size", "60");
							attrParams.put("colspan", "2");
							e.addChild(attrParams);
							
							listeners.addChild(e);
						}
						
						thing.addChild(listeners);
						thing.save();
						System.out.println(thing);
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}