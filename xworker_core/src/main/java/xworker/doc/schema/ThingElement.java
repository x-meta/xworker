package xworker.doc.schema;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import freemarker.template.utility.StringUtil;

/**
 * 事物对应的Schema元素。
 * 
 * @author bookroom
 *
 */
public class ThingElement {
	ThingDocument doc;
	Thing thing;
	String name;
	
	public ThingElement(ThingDocument doc, Thing thing){
		this.doc = doc;
		this.thing = thing;
		
		this.name = thing.getMetadata().getName();
		
	}
	
	public ThingElement getParent(){
		List<Thing> extendThings = thing.getExtends();
		if(extendThings.size() > 0){
			ThingElement el = doc.elementsByPath.get(extendThings.get(0).getMetadata().getPath());//.getElementByPath(extendThings.get(0).getMetadata().getPath());
			return el;
			/*
			if(doc.isConflict(el.name, el.thing)){
				return null;
			}else{
				return el;
			}*/
		}else{
			return null;
		}
	}
		
	public String getParentName(){
		ThingElement element = getParent();
		if(element != null){
			return element.getName();			
		}else{
			return null;
		}
	}
	
	public boolean hasParent(){
		return getParent() != null;
	}
	
	public List<Thing> getAllChilds(){
		List<Thing> childs = null;
		if(!this.hasParent()){
				childs = thing.getAllChilds("thing");
		}else{
			childs = thing.getChilds("thing");
			int index = 0;
			for(Thing ext : thing.getExtends()){
				if(this.hasParent()){
					//忽略第一个继承
					if(index == 0){
						index++;
						continue;
					}
				}
				childs.addAll(ext.getChilds("thing"));
			}
		}
		//过滤
		Map<String, Thing> context = new HashMap<String, Thing>();
		for(int i=0; i<childs.size(); i++){
			Thing child = childs.get(i);
			String name = child.getMetadata().getName();
			if(context.get(name) != null){
				childs.remove(i);
				i--;
			}else{
				context.put(name, child);
			}
		}
		return childs;
	}
	
	public List<Thing> getAllAttributes(){		
		List<Thing> childs = null;
		if(!this.hasParent()){
			childs = thing.getAllChilds("attribute");
		}else{
			childs = thing.getChilds("attribute");
			int index = 0;
			for(Thing ext : thing.getAllExtends()){
				if(this.hasParent()){
					//忽略第一个继承
					if(index == 0){
						index++;
						continue;
					}
				}
				childs.addAll(ext.getChilds("attribute"));
			}
	}
		
		//过滤
		Map<String, Thing> context = new HashMap<String, Thing>();
		for(int i=0; i<childs.size(); i++){
			Thing child = childs.get(i);
			String name = child.getMetadata().getName();
			if(context.get(name) != null || name.startsWith("#") || "descriptors".equals(name)){ //#开始的是非法字符，在事物中是模板
				childs.remove(i);
				i--;
			}else{
				context.put(name, child);
			}
		}
		
		return childs;
	}
	
	public void writeComplexType(BufferedWriter bw, Map<String, Thing> context, int childCount) throws IOException{
		String ident = "";
		for(int i=0; i<childCount; i++){
			ident = ident + "        ";
		}
		boolean isChild = childCount > 0;
		if(childCount > 0){
			if(context.get(thing.getMetadata().getPath()) != null){			
				return;
			}else{
				context.put(thing.getMetadata().getPath(), thing);
			}
		}
		
		if(!isChild){
			bw.write(ident + "    <xsd:complexType name=\"" + name + "\">\r\n");
		}else{
			bw.write(ident + "    <xsd:complexType>\r\n");
		}
		if(doc.includeDescription){
			String description = thing.getStringBlankAsNull("description");
			if(description != null){
				bw.write(ident + "        <xsd:annotation>\r\n");
				bw.write(ident + "            <xsd:documentation><![CDATA[");
				bw.write(StringUtil.XHTMLEnc(description));
				bw.write("]]>\r\n");
				bw.write(ident + "            </xsd:documentation>\r\n");
				bw.write(ident + "        </xsd:annotation>\r\n");
			}
		}
		if(this.hasParent()){
			bw.write(ident + "        <xsd:complexContent>\r\n");
			bw.write(ident + "            <xsd:extension base=\"" + this.getParentName() + "\">\r\n");
		}
		
		//写入子元素
		List<Thing> childs = this.getAllChilds();  
		//查找注册的
		List<String> regists = doc.registCache.get(thing.getMetadata().getPath());
		if(regists != null){
			for(String rpath : regists){				
				Thing rthing = World.getInstance().getThing(rpath);
				if(rthing != null){
					childs.add(rthing);
				}
			}
		}
		if(childs.size() > 0){
			if(this.hasParent()){
				bw.write("    ");
			}
			bw.write(ident + "        <xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\r\n");
			//过滤子节点重名
			Map<String, String> childContext = new HashMap<String, String>();
			for(Thing child : childs){			
				ThingElement el = doc.getElementByPath(child.getMetadata().getPath());
				if(el != null){
					String elementName = child.getMetadata().getName();
					if(isThingDefiendByParent(elementName, new HashMap<String, String>())){
						continue;
					}
					
					if(childContext.get(elementName) != null){
						continue;
					}else{
						childContext.put(elementName, elementName);
					}
					if(this.hasParent()){
						bw.write("    ");
					}
					/*if(doc.isConflict(el.getName(), el.thing)){
						el = doc.elementsByPath.get(child.getMetadata().getPath());
						if(el!= null && childCount <= 20){ //有时会出现递归，限制丢给递归数量
							bw.write(ident + "            <xsd:element name=\"" + el.getName() + "\">\r\n");
							el.writeComplexType(bw, context, childCount+1);
							bw.write(ident + "            </xsd:element>\r\n");
						}
					}else{*/
						bw.write(ident + "            <xsd:element name=\"" + elementName + "\" type=\"" + el.getName() + "\"/>\r\n");
					//}
				}
			}									
			
			if(!this.hasParent()){
				//bw.write("            <xsd:any minOccurs=\"0\"/>\r\n");
			}			
			
			if(this.hasParent()){
				bw.write("    ");
			}
			bw.write(ident + "        </xsd:choice>\r\n");
		}
		
		//写入描述者
		if(doc.hasDescriptor(thing)){
			bw.write(ident + "            <xsd:attribute name=\"descriptors\" type=\"xsd:string\" default=\""
					+ StringUtil.XMLEnc(thing.getMetadata().getPath()) + "\"/>\r\n");
		}
		
		List<Thing> attributes = getAllAttributes();		
		for(Thing attr : attributes){
			String attrName = attr.getMetadata().getName();
			if(isAttributeDefiendByParent(attrName, new HashMap<String, String>())){
				continue;
			}		
			if("descritpors".equals(attrName)){
				continue;
			}
			
			String type="xsd:string";
			String inputType = attr.getString("inputtype");
			String defaultV = attr.getStringBlankAsNull("default");
			if("truefalse".equals(inputType) || "truefalseselect".equals(inputType) || "checkBox".equals(inputType)){
				type = "truefalseType";
			}else{
				
				List<Thing> values = attr.getAllChilds("value");
				
				if(values.size() > 0){
					Thing v1 = values.get(0);
					String rootPath = v1.getMetadata().getPath();
					type = v1.getParent().getMetadata().getName() + "_" + rootPath.hashCode();
					
					Map<String, String> valueContext = new HashMap<String, String>();
					List<String> vs = new ArrayList<String>();
					boolean defualtHas = false;
					for(Thing v : values){
						String value = v.getStringBlankAsNull("value");
						if(value != null){
							if(valueContext.get(value) != null){
								continue;
							}else{
								valueContext.put(value, value);
							}
							vs.add(value);
							
							if(defaultV != null && value.equals(defaultV)){
								defualtHas = true;
							}
						}
					}
					if(!defualtHas){
						//默认值不在列表里则过滤掉
						defaultV = null;
					}
					doc.attributes.put(type, new AttributeType(type, vs));
					
					
				}
			}
						
			String description = null;
			if(doc.includeDescription){
				description = attr.getStringBlankAsNull("description");
			}
			if(description != null){
				bw.write(ident + "            <xsd:attribute name=\"" + attrName + "\" type=\"" + type + "\"" + getAttr("default", type, defaultV) + ">\r\n");
				bw.write(ident + "                <xsd:annotation>\r\n");
				bw.write(ident + "                    <xsd:documentation><![CDATA[");
				bw.write(StringUtil.XHTMLEnc(description));
				bw.write("]]>\r\n");
				bw.write(ident + "                    </xsd:documentation>\r\n");
				bw.write(ident + "                </xsd:annotation>\r\n");
				bw.write(ident + "            </xsd:attribute>\r\n");
			}else{
				bw.write(ident + "            <xsd:attribute name=\"" + attrName + "\" type=\"" + type + "\"" + getAttr("default", type, defaultV) + "/>\r\n");
			}
		}
		
		if(!this.hasParent()){
			//bw.write("            <xsd:anyAttribute/>\r\n");
		}
		if(hasParent()){
			bw.write(ident + "            </xsd:extension>\r\n        </xsd:complexContent>\r\n");
		}
		
		//if(hasExtend(thing, nameContext, thingRealName)){
	//		bw.write("        </xsd:extension></xsd:complexContent>\r\n");
		//}
		bw.write(ident + "    </xsd:complexType>\r\n\r\n");
	}
	
	private String getAttr(String name, String type, String value){
		if(value != null){
			if("truefalseType".equals(type)){
				value = String.valueOf(UtilData.getBoolean(value, false));
			}
			return " " + name + "=\"" + StringUtil.XMLEnc(value) + "\"";
		}else{
			return "";
		}
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * 是否子事物已经被被定义过了，Schame中子Element不能重复定义。
	 * 
	 * @param name
	 * @return
	 */
	public boolean isThingDefiendByParent(String name, Map<String, String> context){
		String path = thing.getMetadata().getPath();
		if(context.get(path) != null){
			return false;
		}else{
			context.put(path, path);
		}
		ThingElement element  = this.getParent();
		if(element != null){
			Thing parent = element.thing;
			for(Thing child : parent.getAllChilds("thing")){
				if(child.getMetadata().getName().equals(name)){
					return true;
				}
			}
			
			for(Thing ext : parent.getExtends()){
				for(Thing child : ext.getAllChilds("thing")){
					if(child.getMetadata().getName().equals(name)){
						return true;
					}
				}
			}
			
			List<String> regists = doc.registCache.get(path);
			if(regists != null){
				for(String rpath : regists){				
					Thing rthing = World.getInstance().getThing(rpath);
					if(rthing != null){
						if(rthing.getMetadata().getName().equals(name)){
							return true;
						}
					}
				}
			}
			
			return element.isThingDefiendByParent(name, context);
		}	
		return false;
	}
	
	public boolean isAttributeDefiendByParent(String name, Map<String,String> context){
		String path = thing.getMetadata().getPath();
		if(context.get(path) != null){
			return false;
		}else{
			context.put(path, path);
		}
		ThingElement element  = this.getParent();
		if(element != null){
			Thing parent = element.thing;
			for(Thing child : parent.getAllChilds("attribute")){
				if(child.getMetadata().getName().equals(name)){
					return true;
				}
			}
			
			for(Thing ext : parent.getExtends()){
				for(Thing child : ext.getAllChilds("attribute")){
					if(child.getMetadata().getName().equals(name)){
						return true;
					}
				}
			}
			
			return element.isAttributeDefiendByParent(name, context);
		}	
		return false;
	}
	
	/**
	 * 是否可以用新的事物替换自己，如果自己没有定义任何属性和子事物，且是继承于该事物那么可以用个目标事物替换自己。
	 * 
	 * 一般用于名字重复时。
	 * 
	 * @param newThing
	 * @return
	 */
	public boolean replaceByThing(Thing newThing){
		if(thing.getChilds("thing").size() == 0 && thing.getChilds("attribute").size() == 0){
			for(Thing ext : thing.getExtends()){
				if(ext == newThing){
					this.thing = newThing;
					return true;
				}
			}
		}
		
		return false;
	}	
	
	/**
	 * 在名字相同时且不能被替换时，判断新的事物是否是一个其它事物。
	 * 
	 * 如果目标事物没有定义子事物和属性，且继承了自己，那么就不是一个新的其它事物。
	 * 
	 * @param newThing
	 * @return
	 */
	public boolean isAnotherThing(Thing newThing){
		if(newThing.getChilds("thing").size() == 0 && newThing.getChilds("attribute").size() == 0){
			for(Thing ext : newThing.getExtends()){
				if(ext == thing){
					return false;
				}
			}
		}
		
		return true;
	}
}
