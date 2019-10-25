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
package xworker.dataObject.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataObjectCoder {
	private static Logger logger = LoggerFactory.getLogger(DataObjectCoder.class);	
	public static final byte TYPE_END = 0;
	/**
	 * 字符串，使用int4个字节表示长度。
	 */
	public static final byte TYPE_STRING = 1;

	public static final byte TYPE_BYTE = 2;
	
	public static final byte TYPE_SHORT = 3;
	
	public static final byte TYPE_INT = 4;
	
	public static final byte TYPE_LONG = 5;
	
	public static final byte TYPE_FLOAT = 6;
	
	public static final byte TYPE_DOUBLE = 7;
	
	public static final byte TYPE_DATE = 8;
	
	public static final byte TYPE_BYTES = 9;
	
	public static final byte TYPE_BOOLEAN = 10;
	
	public static final byte TYPE_STREAM = 11;
	
	public static final byte TYPE_OBJECT = 12;
	
	/**
	 * 把一个数据对象编码到Buffer中。
	 * 
	 * @param dataObject 数据对象
	 * @param out 输出流
	 * @throws IOException IO异常 
	 */
	public static void encode(DataObject dataObject, ObjectOutputStream out) throws IOException{
		for(Thing attributeThing : dataObject.getMetadata().getAttributes()){
			String name = attributeThing.getMetadata().getName();
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}

	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByNames(DataObject dataObject, List<String> attributes, ObjectOutputStream out) throws IOException{
		for(String name : attributes){
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}

	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByAttributes(DataObject dataObject, List<Thing> attributes, ObjectOutputStream out) throws IOException{
		for(Thing attribute : attributes){
			String name = attribute.getMetadata().getName();
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}
	
	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByNames(Map<String, Object> dataObject, List<String> attributes, ObjectOutputStream out) throws IOException{
		for(String name : attributes){
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}

	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByAttributes(Map<String, Object> dataObject, List<Thing> attributes, ObjectOutputStream out) throws IOException{
		for(Thing attribute : attributes){
			String name = attribute.getMetadata().getName();
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}
	
	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByNames(Thing dataObject, List<String> attributes, ObjectOutputStream out) throws IOException{
		for(String name : attributes){
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}

	/**
	 * 编码指定的属性列表。
	 * 
	 * @param dataObject
	 * @param attributes
	 * @param out
	 * @throws IOException
	 */
	public static void encodeByAttributes(Thing dataObject, List<Thing> attributes, ObjectOutputStream out) throws IOException{
		for(Thing attribute : attributes){
			String name = attribute.getMetadata().getName();
			write(out, name, dataObject.get(name));			
		}
		out.writeByte(DataObjectCoder.TYPE_END);
	}
	
	/**
	 * 解码一个数据对象。
	 * 
	 * @param dataObject
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void decode(DataObject dataObject, ObjectInputStream in) throws IOException, ClassNotFoundException{
		while(true){
			String name = in.readUTF();
			byte type = in.readByte();
			if(type == DataObjectCoder.TYPE_END){
				break;
			}else{
				dataObject.put(name, read(in, type));
			}
		}
	}
	
	/**
	 * 解码数据到Map。
	 * 
	 * @param dataObject
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void decode(Map<String, Object> dataObject, ObjectInputStream in) throws IOException, ClassNotFoundException{
		while(true){
			String name = in.readUTF();
			byte type = in.readByte();
			if(type == DataObjectCoder.TYPE_END){
				break;
			}else{
				dataObject.put(name, read(in, type));
			}
		}
	}
	
	/**
	 * 解码数据到Thing。
	 * 
	 * @param dataObject
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void decode(Thing dataObject, ObjectInputStream in) throws IOException, ClassNotFoundException{
		while(true){
			String name = in.readUTF();
			byte type = in.readByte();
			if(type == DataObjectCoder.TYPE_END){
				break;
			}else{
				dataObject.put(name, read(in, type));
			}
		}
	}
	
	/**
	 * 写入一个对象，目前只支持写入基本对象。
	 * 
	 * @param out
	 * @param name
	 * @param value
	 * @throws IOException
	 */
	public static void write(ObjectOutputStream out, String name, Object value) throws IOException{
		if(value == null){
			return;
		}
		
		out.writeUTF(name);
		if(value instanceof String){			
			out.writeByte(DataObjectCoder.TYPE_STRING);
			out.writeUTF((String) value);
		}else if(value instanceof Integer){
			out.writeByte(DataObjectCoder.TYPE_INT);
			out.writeInt((Integer) value);
		}else if(value instanceof Float){
			out.writeByte(DataObjectCoder.TYPE_FLOAT);
			out.writeFloat((Float) value);
		}else if(value instanceof Double){
			out.writeByte(DataObjectCoder.TYPE_DOUBLE);
			out.writeDouble((Double) value);
		}else if(value instanceof Byte){
			out.writeByte(DataObjectCoder.TYPE_BYTE);
			out.writeByte((Byte) value);
		}else if(value instanceof Short){
			out.writeByte(DataObjectCoder.TYPE_SHORT);
			out.writeShort((Short) value);
		}else if(value instanceof Date){
			out.writeByte(DataObjectCoder.TYPE_DATE);
			out.writeLong(((Date) value).getTime());
		}else if(value instanceof Byte[]){
			out.writeByte(DataObjectCoder.TYPE_BYTES);
			byte[] bytes = (byte[]) value;
			out.writeInt(bytes.length);
			out.write(bytes);			
		}else if(value instanceof Boolean){
			out.writeByte(DataObjectCoder.TYPE_BOOLEAN);
			out.writeBoolean((Boolean) value);
		}else if(value instanceof InputStream){
			out.writeByte(DataObjectCoder.TYPE_STREAM);
			InputStream in = (InputStream) value;
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			out.writeInt(bytes.length);
			out.write(bytes);
		}else{
			out.writeByte(DataObjectCoder.TYPE_OBJECT);
			out.writeObject(value);
		}
	}
	
	public static Object read(ObjectInputStream in, byte type) throws IOException, ClassNotFoundException{
		switch(type){
		case DataObjectCoder.TYPE_STRING:
			return in.readUTF();
		case DataObjectCoder.TYPE_INT:
			return in.readInt();
		case DataObjectCoder.TYPE_FLOAT:
			return in.readFloat();
		case DataObjectCoder.TYPE_DOUBLE:
			return in.readDouble();
		case DataObjectCoder.TYPE_BYTE:
			return in.readByte();
		case DataObjectCoder.TYPE_SHORT:
			return in.readShort();
		case DataObjectCoder.TYPE_DATE:
			return new Date(in.readLong());
		case DataObjectCoder.TYPE_BYTES:
			byte[] bytes = new byte[in.readInt()];
			in.read(bytes);
			return bytes;
		case DataObjectCoder.TYPE_BOOLEAN:
			return in.readBoolean();
		case DataObjectCoder.TYPE_STREAM:
			byte[] ibytes = new byte[in.readInt()];
			in.read(ibytes);
			return new ByteArrayInputStream(ibytes);
		case DataObjectCoder.TYPE_OBJECT:
			return in.readObject();			
		}
		
		logger.warn("unknown data type " + type);
		return null;

	}
	
	public static void main(String args[]){
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			dout.writeByte(1);
			dout.writeUTF("name");
			dout.writeUTF("tom");
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			DataInputStream din = new DataInputStream(bin);
			System.out.println(din.readByte());
			System.out.println(din.readUTF());
			System.out.println(din.readUTF());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", 2l);
			
			System.out.println(map.get("name") instanceof Long);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}