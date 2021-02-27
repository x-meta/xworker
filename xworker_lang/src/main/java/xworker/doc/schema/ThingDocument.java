package xworker.doc.schema;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import freemarker.template.utility.StringUtil;
import xworker.java.assist.ClassThingCache;
import xworker.java.assist.JavaCacheItem;

public class ThingDocument {
	//重名过滤的上下文
	Map<String, ThingElement> elements = new HashMap<String, ThingElement>();
	//属性对应的简单类型
	Map<String, AttributeType> attributes = new HashMap<String, AttributeType>();
	//事物对应的元素
	Map<String, ThingElement> elementsByPath = new HashMap<String, ThingElement>();
	
	Map<String, List<Thing>> conflictElements = new HashMap<String, List<Thing>>();
	//要被继承的元素列表，这些元素没有descriptors属性
	Map<String, ThingElement> parents = new HashMap<String, ThingElement>();
	//注册缓存，child子节点的注册也被当作是子节点
	Map<String, List<String>> registCache = new HashMap<String, List<String>>();
	//必须拥有描述着的事物缓存
	Map<String, String> thingWithDescriptors = new HashMap<String, String>();
	
	/** 是否包含描述文档 */
	boolean includeDescription = false;
	/** 是否包含元事物，这样可以用它定义新的事物，通常是mxworker.lang.MetaDescriptor3 */	
	boolean includeMetadata = true;
	
	public ThingDocument(Thing ... things){
		this(false, true, things);
	}
	
	public ThingDocument(boolean includeDescription, boolean includeMetadata, Thing ... things){
		this.includeDescription = includeDescription;
		this.includeMetadata = includeMetadata;
		
		//初始化注册
		initRegist();
		
		//解析事物，找出所有事物节点的定义
		Map<String, Thing> context = new HashMap<String, Thing>();
		for(Thing thing : things){
			if(thing != null){
				addToThingWithDescriptors(thing);
				parse(thing, context);
			}
		}
		
		//元事物
		if(includeMetadata){
			Thing metaThing = World.getInstance().getThing("xworker.lang.MetaDescriptor3");
			if(metaThing != null){
				addToThingWithDescriptors(metaThing);
				parse(metaThing, context);
			}
		}
		
		//找出所有被继承的父类
		for(String key : elementsByPath.keySet()){
			ThingElement el = elementsByPath.get(key);
			ThingElement parent = el.getParent();
			if(parent != null && el.thing.getParent() == null){
				parents.put(parent.thing.getMetadata().getPath(), parent);
			}
		}
		
		//添加一个truefalse类型
		List<String> tvalues = new ArrayList<String>();
		tvalues.add("true");
		tvalues.add("false");
		attributes.put("truefalseType", new AttributeType("truefalseType", tvalues));
		
		System.out.println("冲突的事物：");
		for(String key : conflictElements.keySet()){
			System.out.println(key);
			for(Thing t : conflictElements.get(key)){
				System.out.println("    " + t.getMetadata().getPath());
			}
		}
	}
	
	private void addToThingWithDescriptors(Thing thing){
		String path = thing.getMetadata().getPath();
		thingWithDescriptors.put(path, path);
	}
	
	protected boolean hasDescriptor(Thing thing){
		return thingWithDescriptors.get(thing.getMetadata().getPath()) != null;
	}
	
	private void initRegist(){
		Map<String, String> context = new HashMap<String,String>();
		
		//初始化所有事物管理器下的注册		
		for(ThingManager manager : World.getInstance().getThingManagers()){
			Iterator<Thing> iter = manager.iterator(null, true);
			while(iter.hasNext()){
				Thing thing = iter.next();
				String path = thing.getMetadata().getPath();
				if(context.get(path) != null){
					continue;
				}else{
					context.put(path, path);
				}
				
				initRegist(thing);
			}
		}
		
		//初始化类路径下的注册
		for(JavaCacheItem item : ClassThingCache.getAll()){
			if(item.isClass()){
				Thing thing = World.getInstance().getThing(item.path);
				if(thing != null){
					String path = thing.getMetadata().getPath();
					if(context.get(path) != null){
						continue;
					}else{
						context.put(path, path);
					}
					
					initRegist(thing);
				}
			}
		}
	}
	
	private void initRegist(Thing thing){
		String reg = thing.getStringBlankAsNull("th_registThing");
		if(reg != null){
			String lss[] = reg.split("[,]");
			for(String ls : lss){
				String ll[] = ls.split("[|]");
				if(ll.length >= 2){
					if("child".equals(ll[0])){
						List<String> regs = registCache.get(ll[1]);
						if(regs == null){
							regs = new ArrayList<String>();
							registCache.put(ll[1], regs);
						}
						
						if(!regs.contains(thing.getMetadata().getPath())){
							regs.add(thing.getMetadata().getPath());
						}
					}
				}
			}
		}
		
		for(Thing child : thing.getChilds()){
			initRegist(child);
		}
	}
	
	protected boolean isParent(ThingElement el){
		boolean elementHasParent = parents.get(el.thing.getMetadata().getPath()) != null;
		return elementHasParent || el.thing.getParent() != null;
		//return parents.get(el.thing.getMetadata().getPath()) != null;
	}
	
	protected boolean isConflict(String name, Thing thing){
		ThingElement el = elements.get(name);
		return conflictElements.get(name) != null && (el.thing != thing);
	}
	
	public void write(String fileName) throws IOException{
		FileOutputStream fout = new FileOutputStream(fileName);
		write(fout);
		fout.close();
	}
	
	/**
	 * 把Schema写入到输出流中。
	 * 
	 * @param out
	 * @throws IOException 
	 */
	public void write(OutputStream out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
		bw.write("<xsd:schema xmlns=\"http://www.xworker.org/schema/\"\r\n");
		bw.write("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://www.xworker.org/schema/\">\r\n");
		bw.write("<xsd:import namespace=\"http://www.w3.org/XML/1998/namespace\" />\r\n\r\n");
				
		//writeElementOne(thing, bw, "", new HashMap<String, Thing>());
		for(String key : elements.keySet()){	
			ThingElement el = elements.get(key);
			Thing thing = el.thing;
			String description = null;
			if(this.includeDescription){
				description = thing.getStringBlankAsNull("description");
			}
			if(description != null){
				bw.write("    <xsd:element name=\"" + key + "\" type=\"" + key + "\">\r\n");
				bw.write("        <xsd:annotation>\r\n");
				bw.write("            <xsd:documentation><![CDATA[");
				bw.write(StringUtil.XHTMLEnc(description));
				bw.write("]]>\r\n");
				bw.write("            </xsd:documentation>\r\n");
				bw.write("        </xsd:annotation>\r\n");
				bw.write("    </xsd:element>\r\n");
			}else{
				bw.write("    <xsd:element name=\"" + key + "\" type=\"" + key + "\"/>\r\n");
			}
		}
		
		bw.write("\r\n");
		
		//写入可以被继承的组
		Map<String, Thing> contexts = new HashMap<String, Thing>(); //避免递归重复写入
		for(String key : elements.keySet()){		
			ThingElement el = elements.get(key);
			el.writeComplexType(bw, contexts, 0);
			//writeElement(nameContext.get(key), bw, nameContext, extendExcludes, thingRealName );
		}		
		
		//写入简单类型
		for(String key : attributes.keySet()){
			AttributeType attr = attributes.get(key);
			attr.write(bw);
		}
		
		bw.write("</xsd:schema>");
		bw.flush();
		out.flush();
	}
	
	protected ThingElement getElementByPath(String path){
		return elementsByPath.get(path);
	}
	
	private void parse(Thing thing, Map<String, Thing> parseContext){
		//一个事物只能被分析一次
		String path = thing.getMetadata().getPath();
		if(parseContext.get(path) != null){
			return;
		}else{
			parseContext.put(path, thing);
		}
		
		String name = thing.getMetadata().getName();
		if(name.startsWith("#")){
			return;
		}
		
		ThingElement thingElement = elements.get(name);
		if(thingElement == null){
			thingElement = new ThingElement(this, thing);
			elementsByPath.put(path, thingElement);
			elements.put(name, thingElement);
		}else{
			elementsByPath.put(path, thingElement);
			if(thingElement.replaceByThing(thing)){				
			}else if(thingElement.isAnotherThing(thing)){				
				List<Thing> cs = conflictElements.get(name);
				if(cs == null){
					cs = new ArrayList<Thing>();
					conflictElements.put(name, cs);
				}
				
				addThingToList(thing, cs);
				addThingToList(thingElement.thing, cs);			
				
				if(isMetaThing(thing) || 
						(!isMetaThing(thingElement.thing) && thing.getRoot() == null && thingElement.thing.getRoot() != null)){
					Thing oldThing = thingElement.thing;					
					thingElement.thing = thing;
					String newName = name + "_Conflict_" + cs.size();
					
					thingElement = new ThingElement(this, oldThing);
					thingElement.name = newName;
					elementsByPath.put(oldThing.getMetadata().getPath(), thingElement);
					elements.put(newName, thingElement);
				}else{
					//冲突了之加入子类
					String newName = name + "_Conflict_" + cs.size();
					thingElement = new ThingElement(this, thing);
					thingElement.name = newName;
					elementsByPath.put(path, thingElement);
					elements.put(newName, thingElement);
				}
				
			}
		}			
		
		for(Thing child : thing.getChilds("thing")){
			parse(child, parseContext);
		}
		
		//查找注册的
		List<String> regists = registCache.get(path);
		if(regists != null){
			for(String rpath : regists){
				Thing rthing = World.getInstance().getThing(rpath);
				if(rthing != null){
					addToThingWithDescriptors(rthing);
					parse(rthing, parseContext);
				}
			}
		}
		
		for(Thing ext : thing.getAllExtends()){
			parse(ext, parseContext);
		}
	}
	
	private boolean isMetaThing(Thing thing){
		return "xworker.lang.MetaDescriptor3".equals(thing.getMetadata().getPath());
	}
	
	private void addThingToList(Thing thing, List<Thing> list){
		boolean have = false;
		for(Thing t : list){
			if(t == thing){
				have = true;
				break;
			}
		}
		
		if(!have){
			list.add(thing);
		}
	}
}
