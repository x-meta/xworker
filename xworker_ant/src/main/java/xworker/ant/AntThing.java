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
package xworker.ant;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class AntThing {
	public static String toAntXML(ActionContext actionContext) throws XMLStreamException, UnsupportedEncodingException{
		//自己
		Thing self = (Thing) actionContext.get("self");
		
		//XML编码器
		boolean isSelfStart = false;
		XMLStreamWriter writer = (XMLStreamWriter) actionContext.get("_ant_XML_writer_");
		ByteArrayOutputStream out = (ByteArrayOutputStream) actionContext.get("_ant_XML_out_");
		String ident = (String) actionContext.get("_ant_XML_ident_");
		if(writer == null){
			isSelfStart = true;
			out = new ByteArrayOutputStream();
			
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(out, "utf-8");
			
			writer.writeStartDocument("utf-8", "1.0");
			writer.writeCharacters("\n");
			
			ident = "";
			
			actionContext.put("_ant_XML_writer_", writer);
			actionContext.put("_ant_XML_out_", out);
			actionContext.put("_ant_XML_ident_", ident);
		}
		
		toXML(self, writer, ident, actionContext);
		
		if(isSelfStart){
			return out.toString("utf-8");
		}else{
			return null;
		}
	}
	
	private static void toXML(Thing thing, XMLStreamWriter writer, String ident, ActionContext actionContext) throws XMLStreamException{
		//编码自己
		writer.writeCharacters("\n" + ident);
		
		//属性缓存，避免写入重复的属性
		Map<String, String> attrContext = new HashMap<String, String>();
		//节点名
		String thingName = thing.getThingName();
		writer.writeStartElement(thingName);		
		
		//其他属性
		List<Thing> attributes = thing.getAllAttributesDescriptors();
		List<Thing> cDataAttributes = new ArrayList<Thing>();	
		for(Thing attribute : attributes){
			String attrname = attribute.getMetadata().getName();
			if(attrContext.get(attrname) != null){
				//过滤重复的属性
				continue;
			}
			
			if(attribute.getMetadata().getPath().startsWith("xworker.lang")){
				//过滤XWorker元事物的属性
				continue;
			}
			
			attrContext.put(attrname, attrname);
			
			String defaultValue = attribute.getString("default");
			String value = thing.getString(attrname);
			if(value == null || "".equals(value)){
				//if(defaultValue != null && !"".equals(defaultValue.trim()) && !"false".equals(defaultValue.trim())){
				//	writer.writeAttribute(name, "");
				//}
				continue;
			}
							
			if(!"_text_".equals(attrname)){
				if(!value.equals(defaultValue)){
					writer.writeAttribute(attrname, value);
				}
			}else{
				cDataAttributes.add(attribute);
			}
		}
		
		if(cDataAttributes.size() == 0 && thing.getChilds().size() == 0){
			writer.writeEndElement();
		}else{
			for(Thing attribute : cDataAttributes){
				String attrname = attribute.getMetadata().getName();
				String str = thing.getString(attrname);
				writer.writeCharacters("\n" + ident + "    ");
				writer.writeStartElement(attrname);
				writer.writeCharacters(str);
				//writer.writeCData(str);
				writer.writeEndElement();
			}
			
			//子节点
			for(Thing child : thing.getChilds()){			
				child.doAction("toString", actionContext, UtilMap.toMap(new Object[]{"_ant_XML_ident_", ident + "    "}));
				//toXML(child, writer, ident + "    "	);
			}
			
			//节点结束
			if(cDataAttributes.size() > 0 || thing.getChilds().size() > 0){
				writer.writeCharacters("\n" + ident);
			}
			
			writer.writeEndElement();
		}
	}
}