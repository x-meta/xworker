package xworker.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xmeta.Thing;
import org.xmeta.ThingCoderException;
import org.xmeta.util.UtilData;

public class XmlUtils {
	/**
	 * 把事物编码成XML字符串。
	 * 
	 * @param thing
	 * @return
	 */
	public static String encodeToXml(Thing thing){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try{
			encodeToXml(thing, bout);
			return new String(bout.toByteArray());
		}catch(Exception e){
			throw new ThingCoderException(e);
		}finally{
			try {
				bout.close();
			} catch (IOException e) {
				throw new ThingCoderException(e);
			}
		}
	}
	
	/**
	 * 把指定的事物以XML编码到输出流中。
	 * 
	 * @param thing
	 * @param out
	 * @throws XMLStreamException
	 * @throws IOException 
	 */
	public static void encodeToXml(Thing thing, OutputStream out) throws XMLStreamException, IOException{
		//element.a
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out, "utf-8");
		try{			
			//writer.writeStartDocument("utf-8", "1.0");
			//writer.writeCharacters("\n");
			encode(thing, null, writer, "");
			writer.writeEndDocument();
		}finally{
			writer.close();
		}
	}
		
	/**
	 * 编码事物到XML。<p/>
	 * 
	 * 由于每个节点都有descriptors属性，使用XML编写比较麻烦，因此简化子节点的描述者：<br/>
	 * 1. 如果描述者只有一个，且描述者是父节点的第一个描述者的事物子节点，那么节点名是描述者的名字。
	 * 
	 * @param thing
	 * @param descriptor
	 * @param writer
	 * @param ident
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private static void encode(Thing thing, Thing parentDescriptors, XMLStreamWriter writer, String ident) throws XMLStreamException, IOException{
		Thing descriptor = thing.getDescriptor();
		if(descriptor.getBoolean("notXmlAttribute")){
			return;
		}
		writer.writeCharacters("\n" + ident);
		
		//属性缓存，避免写入重复的属性
		Map<String, String> attrContext = new HashMap<String, String>();
		//节点名
		String thingName = thing.getThingName();
		writer.writeStartElement(thingName);		
		
		//name和id
		String name = thing.getMetadata().getName(); //总是先写入name属性
		if(!name.equals(thingName)){
			writer.writeAttribute("name", name);
		}
		attrContext.put("name", name);
		attrContext.put("descriptors", "descriptors");
		
		
		//其他属性
		List<Thing> attributes = thing.getAttributesDescriptors();
		List<Thing> cDataAttributes = new ArrayList<Thing>();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		for(Thing attribute : attributes){
			String attrname = attribute.getMetadata().getName();
			if(attrContext.get(attrname) != null){
				//过滤重复的属性
				continue;
			}
			attrContext.put(attrname, attrname);
			if(attribute.getBoolean("notXmlAttribute")){
				//不是XML结构本身的属性
				continue;
			}
			
			String defaultValue = attribute.getString("default");
			Object value = thing.getAttribute(attrname);
			if(value == null || "".equals(value)){
				//if(defaultValue != null && !"".equals(defaultValue.trim()) && !"false".equals(defaultValue.trim())){
				//	writer.writeAttribute(name, "");
				//}
				continue;
			}
			
			boolean isCdata = false;
			String type = attribute.getString("type");
			String xmlGenerateType = attribute .getStringBlankAsNull("xmlGenerateType");
			String strValue = null;
			if("__PCDATA__".equals(attrname) || "__CDATA__".equals(attrname) || "CDATA".equals(xmlGenerateType) || "TEXT".equals(xmlGenerateType)){
				cDataAttributes.add(attribute);
				isCdata = true;
			}else{
				if ("int".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("long".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("double".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("float".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("bigDecimal".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("bigInteger".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("boolean".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("byte".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("bytes".equals(type)) {
					byte[] bytes = thing.getBytes(attrname);
					strValue = UtilData.bytesToHexString(bytes);
				} else if ("char".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("short".equals(type)) {
					strValue = String.valueOf(value);
				} else if ("date".equals(type)) {
					Date date = thing.getDate(attrname);
					strValue = dateFormater.format(date);					
				} else if ("object".equals(type)) {
					if (value instanceof Serializable) {
										ByteArrayOutputStream bout = new ByteArrayOutputStream();
						ObjectOutputStream oout = new ObjectOutputStream(bout);
						oout.writeObject(value);
						oout.flush();
						byte[] bs = bout.toByteArray();
						strValue = UtilData.bytesToHexString(bs);
					}
				} else {
					//默认都当作字符串来保存
					strValue = String.valueOf(value);
				}
			}
			
			if(!isCdata){//cdata另外保存				
				if(strValue != null){				
					if(defaultValue == null || (defaultValue != null && !strValue.equals(defaultValue))){
						//XML不保存和默认值相同的值
						writer.writeAttribute(attrname, strValue);
					}
				}else if(defaultValue != null && !"".equals(defaultValue)){
					//如果默认值不为空但是属性值为空，写入一个空字符串，避免读取xml时重赋值为默认值
					writer.writeAttribute(attrname, "");
				}
			}

		}
		
		for(Thing attribute : cDataAttributes){
			String attrname = attribute.getMetadata().getName();
			String xmlGenerateType = attribute .getStringBlankAsNull("xmlGenerateType");
			String str = thing.getString(attrname);
			writer.writeCharacters("\n" + ident + "    ");			
			writer.writeStartElement(attrname);
			if( "TEXT".equals(xmlGenerateType)){
				writer.writeCharacters(str);
			}else{
				writer.writeCData(str);
			}
			writer.writeEndElement();
		}
		
		//子节点
		for(Thing child : thing.getChilds()){
			
			encode(child, descriptor, writer, ident + "    ");
		}
		
		//节点结束
		if(cDataAttributes.size() > 0 || thing.getChilds().size() > 0){
			writer.writeCharacters("\n" + ident);
		}
		
		writer.writeEndElement();
	}
}
