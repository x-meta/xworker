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
package xworker.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilJava;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import ognl.OgnlException;

/**
 * 和数据相关的一些类。
 *  
 * @author zyx
 *
 */
public class UtilData {
	private static Logger log = LoggerFactory.getLogger(UtilData.class);	
	
	/** 一般作为范围查询的数据类型 */
	public static String[] scopeTypes = new String[]{"integer", "long", "short", "float", "double", "byte", "date", "time", "timestamp"};
	
	/**
	 * size是byte的长度，返回可读的字符串，比如1KB，2MB等。
	 * 
	 * @param size
	 * @return
	 */
	public static String getSizeInfo(long size){
		return org.xmeta.util.UtilData.getSizeInfo(size);
	}
	
	/**
	 * 是否是一般作为范围查询的类型，比如：日期，数字等。
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isScopeType(String type){
		if(type == null){
			return false;
		}
		
		for(String scopeType : scopeTypes){
			if(scopeType.equals(type)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 如果proertiesFile不存在，那么返回参数thing，如果存在，那么先thing.detach()一个新的事物，然后
	 * 在properties里的参数复制到新的事物上，并返回新的事物。
	 * 
	 * @param thing
	 * @param propertiesFile
	 * @return
	 * @throws IOException 
	 */
	public static Thing createPropertiesThing(Thing thing, String propertiesFile) throws IOException{
		if(propertiesFile != null && !"".equals(propertiesFile)){
			File file = new File(propertiesFile);
			if(file.exists()){
				Properties p = new Properties();
				FileInputStream fin = new FileInputStream(file);
				try{
					p.load(fin);
				}finally{
					fin.close();
				}
				
				//新建一个thing，用于替换属性
				Thing newThing = thing.detach();
				for(Object key : p.keySet()){
					newThing.getAttributes().put(key.toString(), p.get(key));
				}
					
				return newThing;
			}
			
			return thing;
		}else{
			return thing;
		}
	}
	
	/**
	 * 根据事物的定义转化Map数据中的数据类型。。
	 * 
	 * @param thing
	 * @param data
	 * @param excludeNull 是否删除null数据
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map convertMap(Thing thing, Map data, boolean excludeNull){
		Map<String, Object> d = new HashMap<String, Object>();
		if(thing == null || data == null) return d;
		
		List childs = thing.getAllChilds("attribute");
		for(int i=0; i<childs.size(); i++){
			try{
				Thing child = (Thing) childs.get(i);
				String type = child.getString("type");
				String name = child.getMetadata().getName();
				putValue(child, d, name, data, excludeNull);
				
				//如果存在范围的，也转换
				if(UtilData.isScopeType(type)){
					putValue(child, d, name + "Start", data, excludeNull);
					putValue(child, d, name + "End", data, excludeNull);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//遍历子节点，如果子节点数目唯一，那么应该存在一个名称为子节点名的map，该map保存子节点的信息，如果子节点数目是多个那么可能映射成List		
		for(Iterator iter = thing.getAllChilds("thing").iterator(); iter.hasNext();){
			Thing child = (Thing) iter.next();
			Object o = data.get(child.getMetadata().getName());
			if(o != null && o instanceof Map){
				Map childMap = (Map) o;
				//如果内容不为空且有数据，那么加入查询
				if(childMap != null && childMap.size() > 0){
					Map childMapData = convertMap(child, childMap, excludeNull);
					if(childMapData.size() != 0){
						d.put(child.getMetadata().getName(), childMapData);
					}
				}
			}else if(o != null && o instanceof List){
				List list = (List) o;
				List datas = new ArrayList();
				for(Object ol : list){
					if(ol instanceof Map){
						Map childMapData = convertMap(child, (Map) ol, excludeNull);
						if(childMapData.size() != 0){
							datas.add(childMapData);
						}
					}
				}
				d.put(child.getMetadata().getName(), datas);
			}
		}
		return d;
	}
	
	private static void putValue(Thing child, Map<String, Object> data, String name, Map<String, Object> source, boolean excludeNull){
		Object value = source.get(name);
		String v = null;
		//如果map里的数据不为空或者不是String那么返回，因为属性对应的原始数据要么为空要么就是字符串
		if(value != null && !(value instanceof String)){
			putData(data, name, value);
			//v = value.toString();
			return;
		}else{		
			v = (String) value;
		}
		Object vv = convertValue(child, v, excludeNull);
		if(vv != null){
			putData(data, name, vv);
			//data.put(name, vv);
		}
	}
	
	/**
	 * 通过paramsStr分析返回一个Map&lt;String, Object&gt;，paramsStr的格式是xxx1:yyy1,xxx2。
	 * 如果有多个参数用逗号分割，如果需要改名用英文冒号分割，后者是新的变量名，其中xxx1,xx2是从
	 * 变量上下文中取得值。
	 * 
	 * @param paramsStr
	 * @param actionContext
	 * @return
	 */
	public static Map<String, Object> getParams(String paramsStr, ActionContext actionContext){
		if(paramsStr == null || "".equals(paramsStr.trim())){
			return null;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		for(String pstr : paramsStr.split("[,]")){			
			String ps[] =  null;
			int index = pstr.indexOf("|");
			if(index != -1) {
				ps = new String[2];
				ps[0] = pstr.substring(0, index);
				ps[1] = pstr.substring(index + 1, pstr.length());
			}else {
				ps = pstr.split("[:]");
			}
			
			String name = null;
			String target = null;
			if(ps.length == 1){
				name = ps[0].trim();
				target = name;
			}else{
				name = ps[0].trim();
				target = ps[1].trim();
			}
			
			Object value = null;
			try{
				if(name.indexOf(":") != -1) {
					value = UtilData.getData(name, actionContext);
				} else {
					value = OgnlUtil.getValue(name, actionContext);
				}
			}catch(Exception e){
				log.warn("get params value error", e);
			}
			params.put(target, value);
		}
		
		return params;
	}
	
	/**
	 * 把值放入Map中，比如xx.xx就是放入到map的xx的map中。
	 * 
	 * @param data
	 * @param name
	 * @param value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void putData(Map data, String name, Object value){
		String[] names = name.split("[.]");
		Map currentMap = data;
		
		for(int i=0; i<names.length; i++){
			if(i < (names.length -1)){
				Map cmap = (Map) currentMap.get(names[i]);
				if(cmap == null){
					cmap = new HashMap();
					currentMap.put(names[i], cmap);
				}
				
				currentMap = cmap;
			}else{
				currentMap.put(names[i], value);
			}
		}
	}
	
	/**
	 * 根据属性值类型的定义，把相对应的字符串的值转换为指定类型的值。
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static Object convertValue(Thing attribute, String value, boolean excludeNull){
		try{
			String type = attribute.getString("type");
			String v = value;
			if(v == null || "".equals(v)){
				if(excludeNull) return null;
				
				//如果值为空，取缺省值
				v = attribute.getString("default");
			}
			
			if("int".equals(type) || "integer".equals(type)){	
				if(v != null && !"".equals(v)){					
				    return new Double(v).intValue();
				}else{
					//return new Integer(0);
				}
			}else if("double".equals(type)){
				if(v != null && !"".equals(v)){
					return new Double(v);
				}else{
					//return new Double(0);
				}
			}else if("long".equals(type)){
				if(v != null && !"".equals(v)){
					return new Double(v).longValue();
				}else{
					//return new Long(0);
				}
			}else if("short".equals(type)){
				if(v != null && !"".equals(v)){
					return new Double(v).shortValue();
				}else{
					//return new Short((short) 0);
				}
			}else if("float".equals(type)){
				if(v != null && !"".equals(v)){
					return new Float(v);
				}else{
					//return new Float(0);
				}
			}else if("yes_no".equals(type) || "boolean".equals(type) || "true_false".equals(type)){
				if(v == null || "".equals(v)){
					//d.put(name, new Boolean(false));
				}else if("false".equals(v) || "0".equals(v)){
					return new Boolean(false);
				}else{
					//return new Boolean(true);
				}
			}else if("date".equals(type)){			
				if(v != null && !"".equals(v)){
					return UtilDate.getDate(v);
				}
			}else if("time".equals(type) || "org.xmeta.db.TimeType".equals(type)){
				if(v != null && !"".equals(v)){
					return UtilDate.getTime(v);
				}
			}else if("timestamp".equals(type)){
				if(v != null && !"".equals(v)){
					return UtilDate.getTimestamp(v);
				}
			}else if("currency".equals(type)){
				if(v != null && !"".equals(v)){
					return new Double(v);
				}else{
					return new Double(0);
				}
			}else{
				return v;
			}
			
			return null;
		}catch(Exception e){
			log.warn("convert value", e);
			//e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 从变量上下文通过变量名取指定类型的变量。
	 * 
	 * @param varName 变量名
	 * @param actionContext 变量上下文
	 * @param cls 类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(String varName, ActionContext actionContext, Class<T> cls){
		Object obj = actionContext.get(varName);
		if(obj != null && cls.isInstance(obj)){
			return (T) obj;
		}else{
			return null;
		}
	}
	
	/**
	 * 把对象数组转化成Map。
	 * 
	 * @param objectArray
	 * @return
	 */
	public static Map<String, Object> toMap(Object ... objectArray){
		return UtilMap.toMap(objectArray);
	}
		
	public static List<Object> toList(Object ... objectArray){
		if(objectArray == null){
			return Collections.emptyList();
		}
		
		List<Object> list = new ArrayList<Object>();
		for(Object obj : objectArray){
			list.add(obj);
		}
		
		return list;
	}
	
	public static BigDecimal getBigDecimal(Object v, BigDecimal defaultValue){		
		return org.xmeta.util.UtilData.getBigDecimal(v, defaultValue);
	}
	
	public static BigInteger getBigInteger(Object v, BigInteger defaultValue){  
		return org.xmeta.util.UtilData.getBigInteger(v, defaultValue);
	}
	
	public static byte getByte(Object v, byte defaultValue){    
		return org.xmeta.util.UtilData.getByte(v, defaultValue);
	}
	
	public static byte[] getBytes(Object v, byte[] defaultValue){
		return org.xmeta.util.UtilData.getBytes(v, defaultValue);
	}
	
	public static char getChar(Object v, char defaultValue){
		return org.xmeta.util.UtilData.getChar(v, defaultValue);
	}
	
	public static Date getDate(Object v, Date defaultValue){ 
		return org.xmeta.util.UtilData.getDate(v, defaultValue);
	}
	
	public static Date getDate(Object v, Date defaultValue, String pattern){    
		return org.xmeta.util.UtilData.getDate(v, defaultValue, pattern);		
	}
	
	public static double getDouble(Object v, double defaultValue){  
		return org.xmeta.util.UtilData.getDouble(v, defaultValue);
	}
	
	public static float getFloat(Object v, float defaultValue){
		return org.xmeta.util.UtilData.getFloat(v, defaultValue);
	}
	
	public static long getLong(Object v, long defaultValue){  
		return org.xmeta.util.UtilData.getLong(v, defaultValue);
	}
	
	public static int getInt(Object v, int defaultValue){   
		return org.xmeta.util.UtilData.getInt(v, defaultValue);
	}
	
	public static short getShort(Object v, short defaultValue){  
		return org.xmeta.util.UtilData.getShort(v, defaultValue);
	}
	
	public static boolean getBoolean(Object v, boolean defaultValue){
		return org.xmeta.util.UtilData.getBoolean(v, defaultValue);
	}
	
	public static String getString(Object v, String defaultValue){    
		return org.xmeta.util.UtilData.getString(v, defaultValue);
	}
	
	public static String getSizeInfo(double size) {
		return org.xmeta.util.UtilData.getSizeInfo(size);
	}
	
	public static byte[] hexStringToByteArray(String hex) {
		return org.xmeta.util.UtilData.hexStringToByteArray(hex);
	}
	
	 public static String bytesToHexString(byte[] bytes) {
		 return org.xmeta.util.UtilData.bytesToHexString(bytes);
	 }
	 
	 public static Object getObject(Thing thing, String attributeName, ActionContext actionContext) throws OgnlException{
		 return org.xmeta.util.UtilData.getObject(thing, attributeName, actionContext);
	 }
	 
	 public static <T> T getObjectByType(Thing thing, String attributeName, Class<T> t, ActionContext actionContext) throws OgnlException{
		 return org.xmeta.util.UtilData.getObjectByType(thing, attributeName, t, actionContext);
	 }
	 
	 public static Object getData(Thing thing, String attributeName, ActionContext actionContext) throws OgnlException, IOException{
		 Object value = thing.get(attributeName);
		 if(value != null && value instanceof String){
			 String str = (String) value;
			 return getData(str, actionContext);
	 	 }else{
	 		 return value;
	 	 }
		 //return org.xmeta.util.UtilData.getData(thing, attributeName, actionContext);
	 }
	 
	 public static Object getData(String value, ActionContext actionContext) throws OgnlException, IOException{
		 Object object = actionContext.get(value);
		 if(object != null) {
			 return object;
		 }
		 
		 if(value.startsWith("template:")){
			 return StringUtils.getString(value, actionContext);
		 }
		 return org.xmeta.util.UtilData.getData(value, actionContext);
	 }
	 
	 public static boolean isTrue(Object condition){
		 return org.xmeta.util.UtilData.isTrue(condition);
	 }
	 
	 public static boolean isIn(int aint, int[] array){
		 if(array == null){
			 return false;
		 }
		 
		 for(int i=0;  i<array.length; i++){
			 if(aint == array[i]){
				 return true;
			 }
		 }
		 
		 return false;
	 }
	 
	 public static boolean isIn(byte aint, byte[] array){
		 if(array == null){
			 return false;
		 }
		 
		 for(int i=0;  i<array.length; i++){
			 if(aint == array[i]){
				 return true;
			 }
		 }
		 
		 return false;
	 }
	 
	 public static boolean isIn(String astr, String[] array){
		 if(array == null || astr == null) {
			 return false;
		 }
		 
		 for(int i=0;  i<array.length; i++){
			 if(array[i] != null && array[i].equals(astr)){
				 return true;
			 }
		 }
		 
		 return false;
	 }
	 
	/**
	 * 对org.xmeta.util.StringUtil的补充。
	 * 
	 * @param value
	 * @param actionContext
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public static String getString(String value, ActionContext actionContext) throws IOException, TemplateException{
		return StringUtils.getString(value, actionContext);
	}
	
	public static String getString(Thing thing, String attribute, ActionContext actionContext) throws IOException, TemplateException{
		return StringUtils.getString(thing, attribute, actionContext);
	}
	
	/**
	 * 如果textForIndex数组的每个字符串在text中indexOf都不为-1，那么返回true，否则返回false。
	 * 
	 * @param text
	 * @param textForIndex
	 * @return
	 */
	public static boolean indexHas(String text, String[] textForIndex){
		if(text == null || textForIndex == null){
			return false;
		}
		
		for(String index : textForIndex){
			if(text.indexOf(index) == -1){
				return false;
			}
		}
		
		return true;
	}
	
	public static Iterable<Object> getIterable(Object collection){
		return UtilJava.getIterable(collection);
	}
	
	/**
	 * 一个强制转换的工具类。
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object obj){
		T obj2 = (T) obj;
		return obj2;
	}
	
	/**
	 * 从两个标记间读取字节。
	 * 
	 * @param source
	 * @param index
	 * @param startMark
	 * @param endMark
	 * @return
	 * @throws IOException 
	 */
	public static byte[] getBytes(byte[] source, Index index, byte[] startMark, byte[] endMark) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		boolean copy = false;
		while(index.index < source.length){
			if(copy){
				if(isMark(source, index.index, endMark)){
					bout.write(endMark);
					return bout.toByteArray();
				}else{
					bout.write(source[index.index]);
					index.index++;
				}
			}else if(isMark(source, index.index, startMark)){
				bout.write(source[index.index]);
				index.index++;
				copy = true;
			}else{
				index.index++;
			}
		}
		
		return bout.toByteArray();
	}
	
	public static boolean isMark(byte[] src, int index, byte[] mark){
		for(int i=0; i<mark.length; i++){
			if(src[i + index] != mark[i]){
				return false;
			}
		}
		
		return true;
	}
	
	public static class Index{
		public int index = 0;
	}
	
	/**
	 * 从当前上下文或parentContext中返回指定名称的变量，会递归的取，直到返回符合条件的。
	 * 
	 * @param actionContext
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getParentContextValue(ActionContext actionContext, String name){
		Object value = null;
		ActionContext ac = actionContext;
		Map<ActionContext, ActionContext> context  = new HashMap<ActionContext, ActionContext>();
		while(ac != null){
			//避免递归，加入context判断
			if(context.get(ac) != null) {
				break;
			}else {
				context.put(ac, ac);
			}
			
			value = ac.get(name);
			if(value != null){			   
			    return (T) value;
			}else if(ac.containsKey(name)){
				return null;
			}
								
			Object acParent = ac.getObject("parentActionContext");
			if(acParent instanceof ActionContext){
				ac = (ActionContext) acParent;
				continue;
			}
			
			acParent = ac.getObject("parentContext");
			if(acParent instanceof ActionContext){
				ac = (ActionContext) acParent;
				continue;
			}
		}
		
		return null;
	}
	
	/**
	 * 返回在当前上下文或parentContext中的ActionContainer变量，会递归的取，查找时回合thingPath比对。
	 * 
	 * @param actionContext
	 * @param name
	 * @param thingPath
	 * @return
	 */
	public static ActionContainer getParentCcontextActionContainer(ActionContext actionContext, String name, String thingPath){
		String parentContextVarName = "parentContext";
		ActionContext parentContext = actionContext;//actionContext.getObject(parentContextVarName);
		while(parentContext != null){
			Object value = parentContext.get(name);
			if(value != null && value instanceof ActionContainer){
				ActionContainer ac = (ActionContainer) value;
				//System.out.println(ac.getThing().getMetadata().getPath());
				//System.out.println(thingPath);
				if(ac.getThing().getMetadata().getPath().equals(thingPath)){
					return ac;
				}
			}
			
			parentContext = parentContext.getObject(parentContextVarName);
					
		}
		
		return null;
	}
	
	public static int[] getIntArray(String array) {
		String[] as = array.split("[,]");
		int[] a = new int[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Integer.parseInt(as[i]);
		}
		return a;
	}
	
	public static byte[] getByteArray(String array) {
		String[] as = array.split("[,]");
		byte[] a = new byte[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Byte.parseByte(as[i]);
		}
		return a;
	}
	
	public static short[] getShortArray(String array) {
		String[] as = array.split("[,]");
		short[] a = new short[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Short.parseShort(as[i]);
		}
		return a;
	}
	
	public static long[] getLongArray(String array) {
		String[] as = array.split("[,]");
		long[] a = new long[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Long.parseLong(as[i]);
		}
		return a;
	}
	
	public static double[] getDoulbeArray(String array) {
		String[] as = array.split("[,]");
		double[] a = new double[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Double.parseDouble(as[i]);
		}
		return a;
	}
	
	public static float[] getFloatArray(String array) {
		String[] as = array.split("[,]");
		float[] a = new float[as.length];
		for(int i=0; i<as.length; i++) {
			a[i] = Float.parseFloat(as[i]);
		}
		return a;
	}
		
}