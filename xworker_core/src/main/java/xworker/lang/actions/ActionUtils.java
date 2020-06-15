package xworker.lang.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.db.sql.SQLConnection;
import xworker.lang.Configuration;
import xworker.lang.actions.data.StringDataFactory;
import xworker.util.StringUtils;
import xworker.util.UtilData;
import xworker.util.UtilTemplate;
import xworker.util.XWorkerUtils;

public class ActionUtils {
	/**
	 * 执行一个事物模型的行为和名字为name的子节点。先执行动作，后执行名字为name的子节点，如果名字为name的子节点存在，
	 * 那么把子节点转为动作执行，且返回执行结果，否则返回动作的结果。
	 * 
	 * @param thing
	 * @param name
	 * @param actionContext
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T executeActionAndChild(Thing thing, String name, ActionContext actionContext, Object ... params) {
		Object obj = thing.doAction(name, actionContext, params);
		for(Thing child : thing.getChilds()) {
			if(child.getMetadata().getName().equals(name)) {
				obj = child.getAction().run(actionContext, params);
			}
		}
		
		return (T) obj;
	}
	
	public static Boolean getBoolean(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		boolean returnBoolean = self.getBoolean("Boolean");
		String attributeName = self.getString("attributeName");
		Object data = UtilData.getData(realSelf, attributeName, actionContext);
		if(returnBoolean && (data == null || "".equals(data)) && self.getStringBlankAsNull("defaultValue") == null) {
			return null;
		}
		
		return UtilData.getBoolean(UtilData.getData(realSelf, attributeName, actionContext), self.getBoolean("defaultValue"));
	}
	
	public static BigInteger getBigInteger(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getBigInteger(UtilData.getData(realSelf, attributeName, actionContext), self.getBigInteger("defaultValue"));
	}
	
	public static BigDecimal getBigDecimal(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getBigDecimal(UtilData.getData(realSelf, attributeName, actionContext), self.getBigDecimal("defaultValue"));
	}
		
	public static byte[] getBytes(ActionContext actionContext) throws IOException, TemplateException, OgnlException, DecoderException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getBytes(UtilData.getData(realSelf, attributeName, actionContext), self.getBytes("defaultValue"));
	}
	
	public static byte getByte(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getByte(UtilData.getData(realSelf, attributeName, actionContext), self.getByte("defaultValue"));
	}
	
	public static short getShort(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getShort(UtilData.getData(realSelf, attributeName, actionContext), self.getShort("defaultValue"));
	}
	
	public static char getChar(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getChar(UtilData.getData(realSelf, attributeName, actionContext), self.getChar("defaultValue"));
	}
	
	public static float getFloat(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getFloat(UtilData.getData(realSelf, attributeName, actionContext), self.getFloat("defaultValue"));
	}
	
	public static double getDouble(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getDouble(UtilData.getData(realSelf, attributeName, actionContext), self.getDouble("defaultValue"));
	}
	
	public static long getLong(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getLong(UtilData.getData(realSelf, attributeName, actionContext), self.getLong("defaultValue"));
	}
	
	public static int getInt(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		return UtilData.getInt(UtilData.getData(realSelf, attributeName, actionContext), self.getInt("defaultValue"));
	}
	
	public static Object getObject(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value == null) {
			return null;
		}
		
		Object obj = StringDataFactory.getStringData(realSelf, self, value, actionContext);
		
		if(obj instanceof String && self.getBoolean("variable")) {
			Object var = actionContext.get((String) obj);
			if(var != null) {
				obj = var;
			}
		}
		
		if(obj == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return obj;
	}
	
	public static Object[] getObjectArray(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String values = realSelf.getString(attributeName);
		List<Object> list = new ArrayList<Object>();
		if(values != null &&  !"".equals(values)) {
			for(String value : values.split("[,]")) {
				if(value != null && value.startsWith("template:")){
					String template = value.substring(9, value.length());
					try{
						list.add(UtilTemplate.processString(actionContext, template));
					}catch(Exception e){
						throw new ActionException("Get string from template error", e);
					}
				}
				
				Object obj = UtilData.getData(realSelf, attributeName, actionContext);
				if(obj instanceof Collection) {
					list.addAll((Collection<?>) obj);
				}else {
					list.add(obj);
				}
			}
		}
		
		Object[] objs = new Object[list.size()];
		list.toArray(objs);
		return objs;
	}
	
	public static Object getShell(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		Object obj = UtilData.getData(realSelf, attributeName, actionContext);
		if(obj != null){
			return obj; 
		}
		
		if(self.getBoolean("defaultIDEShell")){
			return XWorkerUtils.getIDEShell();
		}
		
		if(obj == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return null;
	}
	
	public static Object getThing(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value != null){
			World world = World.getInstance();
			Thing thing = world.getThing(value);
			if(thing != null){
				return thing;
			}
			
			Object obj =  UtilData.getData(realSelf, attributeName, actionContext);
			if(obj instanceof Thing){
				return (Thing) obj;
			}else if(obj instanceof String){
				thing = world.getThing((String) obj);
				if(thing != null){
					return thing;
				}
			}
		}
		
		String childThingName = self.getStringBlankAsNull("childThingName");
		if(childThingName != null){
			Thing thing = realSelf.getThing(childThingName + "@0");
			if(thing != null && self.getBoolean("childThingFirstChild", false)){
				if(thing.getChilds().size() > 0){
					thing = thing.getChilds().get(0);
				}else{
					thing = null;
				}
			}
			
			if(thing != null){
				return thing;
			}
		}
		
		if(self.getBoolean("childThingFirstChild", false) && childThingName == null && realSelf.getChilds().size() > 0){
			return realSelf.getChilds().get(0);
		}
		
		if(self.getBoolean("returnSelf", false)){
			return realSelf;
		}
			
		if(self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return null;					
	}
	
	public static Thing getThing(Thing self, Thing realSelf, String attributeName_, String childThingName_, boolean childThingFirstChild, boolean notnull, ActionContext actionContext) throws IOException,  OgnlException{
		String attributeName = self.getString(attributeName_);
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value != null){
			World world = World.getInstance();
			Thing thing = world.getThing(value);
			if(thing != null){
				return thing;
			}
			
			Object obj =  UtilData.getData(realSelf, attributeName, actionContext);
			if(obj instanceof Thing){
				return (Thing) obj;
			}else if(obj instanceof String){
				return world.getThing((String) obj);
			}
		}
		
		String childThingName = self.getStringBlankAsNull(childThingName_);
		if(childThingName != null){
			Thing thing = realSelf.getThing(childThingName + "@0");
			if(thing != null && childThingFirstChild){
				if(thing.getChilds().size() > 0){
					thing = thing.getChilds().get(0);
				}else{
					thing = null;
				}
			}
			
			return thing;
		}else{
			if(notnull){
				throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
			}
			
			return null;
		}			
	}
	
	public static Thing getThing(Thing self, String childThingName, boolean childThingFirstChild){
		Thing thing = self.getThing(childThingName + "@0");
		if(thing != null && childThingFirstChild){
			if(thing.getChilds().size() > 0){
				thing = thing.getChilds().get(0);
			}else{
				thing = null;
			}
		}
		
		return thing;
	}
	
	public static File getFile(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		String attributeName = self.getString("attributeName");
		
		Object obj = UtilData.getData(realSelf, attributeName, actionContext);
		if(obj instanceof File){
			return (File) obj;
		}else if(obj instanceof String){
			return new File((String) obj);
		}else{
			if(self.getBoolean("notNull")){
				throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
			}
			return null;
		}
	}
	
	public static URL getURL(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		String attributeName = self.getString("attributeName");
		
		Object obj = UtilData.getData(realSelf, attributeName, actionContext);
		if(obj instanceof URL){
			return (URL) obj;
		}else if(obj instanceof String){
			return new URL((String) obj);
		}else if(obj instanceof URI){
			return ((URI) obj).toURL();
		}else{
			if(self.getBoolean("notNull")){
				throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
			}
			return null;
		}
	}
	
	public static Object getObjectByClass(ActionContext actionContext) throws OgnlException, ClassNotFoundException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		String attributeName = self.getString("attributeName");
		String className = self.getString("className");
		
		Class<?> cls = Class.forName(className);
		
		Object obj = UtilData.getObjectByType(realSelf, attributeName, cls, actionContext);		
		
		if(obj == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return obj;
	}
	
	public static String getString(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		actionContext.peek().put("self", realSelf); //如果attributename是：ognl:self...的情形确定self正确
		String str = StringUtils.getString(realSelf, attributeName, actionContext);
		if(str == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return str;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getStringList(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		Object data = getObject(actionContext);
		if(data instanceof String) {
			String value = (String) data;
			if(value == null || "".equals(value)) {
				return Collections.emptyList();
			}else {
				boolean splitLine = UtilData.isTrue(self.doAction("isSplitLine", actionContext));
				List<String> list = new ArrayList<String>();
				
				char delimiter = self.doAction("getDelimiter", actionContext);
				if(splitLine) {
					for(String line : value.split("[\n]")) {						
						for(String v : line.split("[" + delimiter + "]")) {
							list.add(v.trim());
						}
					}
				}else {
					for(String v : value.split("[" + delimiter + "]")) {
						list.add(v.trim());
					}
				}
				return list;
			}
		}else if(data instanceof List<?>) {
			return data;
		}else if(data instanceof String[]){
			List<String> list = new ArrayList<String>();
			for(String v : (String[]) data) {
				list.add(v);
			}
			return list;
		}else if(data instanceof Collection) {
			Collection c = (Collection) data;
			List<?> list = new ArrayList();
			list.addAll(c);
			return list;
		}else if(data instanceof Iterator) {
			Iterator itera = (Iterator) data;
			List<String> list = new ArrayList<String>();
			while(itera.hasNext()) {
				list.add(String.valueOf(itera.next()));
			}
			return list;
		}else if(data != null){
			List<String> list = new ArrayList<String>();
			list.add(data.toString());
			return list;
		}else {
			return Collections.emptyList();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String[] getStringArray(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		Object data = getObject(actionContext);
		if(data instanceof String) {
			String value = (String) data;
			if(value == null || "".equals(value)) {
				return new String[0];
			}else {
				boolean splitLine = UtilData.isTrue(self.doAction("isSplitLine", actionContext));
				List<String> list = new ArrayList<String>();
				
				char delimiter = self.doAction("getDelimiter", actionContext);
				if(splitLine) {
					for(String line : value.split("[\n]")) {						
						for(String v : line.split("[" + delimiter + "]")) {
							list.add(v.trim());
						}
					}
				}else {
					for(String v : value.split("[" + delimiter + "]")) {
						list.add(v.trim());
					}
				}
				String[] array = new String[list.size()];
				list.toArray(array);
				return array;
			}
		}else if(data instanceof String[]){
			return (String[]) data;
		}else if(data instanceof Collection) {
			Collection c = (Collection) data;
			String[] array = new String[c.size()];
			c.toArray(array);
			return array;
		}else if(data instanceof Iterator) {
			Iterator itera = (Iterator) data;
			List<String> list = new ArrayList<String>();
			while(itera.hasNext()) {
				list.add(String.valueOf(itera.next()));
			}
			String[] array = new String[list.size()];
			list.toArray(array);
			return array;
		}else {
			return new String[0];
		}
	}
	
	/**
	 * 获取真正的self。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing getSelf(ActionContext actionContext){
		 List<Thing> things = actionContext.getThings();
         if(things.size() > 1){
             return things.get(things.size() - 2);
         }
         
         throw new ActionException("Not running under an self action or thing");
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParameters(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		Object params = UtilData.getData(realSelf, attributeName, actionContext);
		if(params instanceof Map){
			return (Map<String, Object>) params;
		}else if(params != null){
			return xworker.util.UtilData.getParams(String.valueOf(params), actionContext); 
		}else{
			return null;
		}
	}
	
	public static Object getOgnl(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getStringBlankAsNull("attributeName");
		String express = realSelf.getStringBlankAsNull(attributeName);
		Object obj = null;
		if(express != null){
			obj = OgnlUtil.getValue(express, actionContext);
		}
		
		if(obj == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		return obj;
	}
	
	public static Class<?> parseClass(String className) throws ClassNotFoundException{
		return org.xmeta.util.UtilAction.parseClass(className);
	}
	
	public static Class<?>[] parseClasses(String classNames) throws ClassNotFoundException{
		return org.xmeta.util.UtilAction.parseClasses(classNames);
	}
	
	public static Object getClass_(ActionContext actionContext) throws ClassNotFoundException, OgnlException, IOException {
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		Object obj = UtilData.getData(realSelf, attributeName, actionContext);
		if(obj == null) {
			return null;
		}else {
			if(obj instanceof String) {
				return parseClass((String) obj);
			}else {
				return obj.getClass();
			}
		}
	}
	
	public static Object getClasses(ActionContext actionContext) throws ClassNotFoundException, OgnlException, IOException {
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String values = realSelf.getString(attributeName);
		if(values == null || "".equals(values)) {
			return new Class<?>[0];
		}else {
			String vs[] = values.split("[,]");
			Class<?>[] clses = new Class<?>[vs.length];
			for(int i=0; i<vs.length; i++) {
				Object obj = UtilData.getData(vs[i], actionContext);
				if(obj == null) {
					clses[i] = null;
				}else {
					if(obj instanceof String) {
						clses[i] = parseClass((String) obj);
					}else {
						clses[i] = obj.getClass();
					}
				}
			}
			
			return clses;
		}		
	}
	
	/**
	 * 通过arg[]对象列表返回匹配的参数类列表。如果methodName==null返回构造函数的参数，否则返回方法的参数。
	 * 
	 * @param cls
	 * @param arg
	 * @param methodName
	 * @return
	 */
	public static Class<?>[] getParamTypes(Class<?> cls, Object[] arg, String methodName){
		boolean hasNull = false;
		for(int i=0; i<arg.length; i++){
			if(arg[i] == null){
				hasNull = true;
				break;
			}
		}
		if(hasNull){
			if(methodName != null){
				Method best = null;
				int maxFit = 0;
				for(Method method : cls.getDeclaredMethods()){
					if(methodName.equals(method.getName())){
						Class<?>[] types = method.getParameterTypes();
						if(types.length == arg.length){
							if(best == null){
								best = method;
							}
							
							int fit = 0;
							boolean ok = true;
							for(int i =0; i<types.length; i++){
								if(arg[i] != null){
									if(types[i].isInstance(arg[i])){
										fit++;
									}else{
										ok = false;
										break;
									}									
								}
							}
							
							if(ok && (fit > maxFit || best == null)){
								best = method;
								maxFit = fit;
							}
						}
					}
				}
				if(best != null){
					return best.getParameterTypes();
				}else{
					return null;
				}
			}else{
				Constructor<?> best = null;
				int maxFit = 0;
				for(Constructor<?> method : cls.getDeclaredConstructors()){
					Class<?>[] types = method.getParameterTypes();
					if(types.length == arg.length){
						if(best == null){
							best = method;
						}
						
						int fit = 0;
						boolean ok = true;
						for(int i =0; i<types.length; i++){
							if(arg[i] != null){
								if(types[i].isInstance(arg[i])){
									fit++;
								}else{
									ok = false;
									break;
								}									
							}
						}
						
						if(ok && (fit > maxFit || best == null)){
							best = method;
							maxFit = fit;
						}
					}
				}
				if(best != null){
					return best.getParameterTypes();
				}else{
					return null;
				}
			}
		}else{
			return null;
		}
	}
	
	public static Object createObject(ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
					
		//构造对象
		Class<?> cls = Class.forName(self.getString("className"));
		Object object = null;
		String contructors = self.getStringBlankAsNull("contructors");
		if(contructors == null){
			object = cls.getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
			//object = cls.newInstance();
		}else{
			List<Thing> params = self.getChilds("Param");
			Map<String, Object> pmap = new HashMap<String, Object>();
			for(Thing pthing : params){
				pmap.put(pthing.getMetadata().getName(), realSelf.doAction(pthing.getString("actionName"), actionContext));
			}
			
			Contructors cs = new Contructors(contructors);
			Object[] arg = cs.getParams(pmap);
			if(arg != null){
				Class<?>[] paramTypes = getParamTypes(cls, arg, null);
				if(paramTypes == null){
					object = ConstructorUtils.invokeConstructor(cls, arg);
				}else{
					object = ConstructorUtils.invokeConstructor(cls, arg, paramTypes);
				}
			}else{
				object = cls.getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
				//object = cls.newInstance();
			}
		}
		
		//设置属性
		List<Thing> attributes = self.getChilds("Attribute");
		for(Thing attr : attributes){
			Object value = realSelf.doAction(attr.getString("actionName"), actionContext);
			String fieldName = attr.getStringBlankAsNull("fieldName");
			if(fieldName != null){
				FieldUtils.writeDeclaredField(object, fieldName, value);
			}else{
				String methodName = attr.getStringBlankAsNull("methodName");
				if(methodName != null){
					MethodUtils.invokeMethod(object, methodName, value);
				}
			}
		}
		
		return object;
	}
	
	public static Object getEnum(ActionContext actionContext) throws ClassNotFoundException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		Object value = realSelf.doAction(self.getString("actionName"), actionContext);
		if(value instanceof String){
			Class<?> enumClass = Class.forName(self.getString("enumClass"));
			return EnumUtils.getEnum(enumClass, (String) value);
		}else{
			return value;
		}
	}
	
	public static Object getDate(ActionContext actionContext) throws OgnlException, ClassNotFoundException, ParseException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		String attributeName = self.getString("attributeName");
		String format = self.getString("format");
		String defaultValue = self.getStringBlankAsNull("defaultValue");
		
		//先看是否是Date类型
		Object d = UtilData.getData(realSelf, attributeName, actionContext);
		if(d instanceof Date){
			return (Date) d;
		}
		
		if(d == null){
			d = defaultValue;			
		}
		
		//转化为String试试
		String value = UtilString.getString(String.valueOf(d), actionContext);//UtilData.getString(realSelf, attributeName, actionContext);
		if(value == null || "".equals(value)) {
			value = defaultValue;
		}
		if(value == null || "".equals(value)) {
			return null;
		}
		
		//自动匹配格式，如果没有设定格式
		if(format == null || "".equals(format)) {
			switch(value.length()) {
			case 2:
				format = "dd";   //日期
				break;
			case 4:
				format = "yyyy"; //年
				break;
			case 5:
				format = "MM-dd"; //月日
				break;
			case 8:
				format = "HH:mm:ss"; //时分秒
				break;
			case 10:
				format = "yyyy-MM-dd"; //年月日
				break;
			case 19:
				format = "yyyy-MM-dd HH:mm:ss"; //年月日时分秒
				break;
			}
		}
		SimpleDateFormat sf = new SimpleDateFormat(format);
		
		return sf.parse(value);				
	}
	
	public static Object getStaticField(ActionContext actionContext) throws ClassNotFoundException, IllegalAccessException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		Object value = realSelf.doAction(self.getString("actionName"), actionContext);
		if(value instanceof String){
			Class<?> className = Class.forName(self.getString("className"));
			return FieldUtils.readStaticField(className, (String) value);
		}else{
			return value;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getChildActionResult(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String childThingName = self.getStringBlankAsNull("childThingName");		
		List<Thing> childs = null;
		if(childThingName == null){
			String childPath = self.getStringBlankAsNull("childPath");
			if(childPath != null){
				childs = (List<Thing>) realSelf.get(childPath);
			}else{
				childs = realSelf.getChilds();
			}
		}else{
			childs = realSelf.getChilds(childThingName);
		}
		
		int index = self.getInt("index");
		if(index >= childs.size() || index < 0){
			return null;
		}
		
		Thing child = childs.get(index);
		return child.doAction(self.getString("actionName"), actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> getChildsActionResultList(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String childThingName = self.getStringBlankAsNull("childThingName");
		List<Thing> childs = null;
		if(childThingName == null){
			String childPath = self.getStringBlankAsNull("childPath");
			if(childPath != null){
				childs = (List<Thing>) realSelf.get(childPath);
			}else{
				childs = realSelf.getChilds();
			}childs = realSelf.getChilds();
		}else{
			childs = realSelf.getChilds(childThingName);
		}
		
		List<Object> results = new ArrayList<Object>();
		for(Thing child : childs){
			results.add(child.doAction(self.getString("actionName"), actionContext));
		}
		return results;
	}
	
	public static Object[] getChildsActionResultArray(ActionContext actionContext){
		List<Object> list = getChildsActionResultList(actionContext);
		Object[] a = new Object[list.size()];
		list.toArray(a);
		return a;
	}
	
	public static Object getObjectOrChildActionResult(ActionContext actionContext) throws Exception{
		Object value = getObject(actionContext);
		if(value == null){
			value = getChildActionResult(actionContext);
		}
		return value;
	}
	
	public static Object getObjectOrChildsActionResultList(ActionContext actionContext) throws Exception{
		Object value = getObject(actionContext);
		if(value == null){
			value = getChildsActionResultList(actionContext);
		}
		return value;
	}
	
	public static Object getObjectOrChildsActionResultArray(ActionContext actionContext) throws Exception{
		Object value = getObject(actionContext);
		if(value == null){
			value = getChildsActionResultArray(actionContext);
		}
		return value;
	}
	
	public static Object doAction(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		return realSelf.doAction(self.getString("actionContext"), actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thing> getThings(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		List<Thing> list = new ArrayList<Thing>();
		World world = World.getInstance();
		
		//从属性获取
		String attributeName = self.getString("attributeName");
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value != null){
			//当作事物来获取
			list.addAll(getThingsFromCode(value));			
			
			if(list.size() == 0){
				Object obj = UtilData.getData(value, actionContext);
				if(obj instanceof Thing){
					list.add((Thing) obj);
				}else if(obj instanceof List){
					list.addAll((List<Thing>) obj);
				}else if(obj instanceof Thing[]){
					for(Thing thing : (Thing[]) obj){
						list.add(thing);
					}
				}else if(obj instanceof String){
					for(String path : ((String) obj).split("[,]")){
						Thing thing = world.getThing(path);
						if(thing != null){
							list.add(thing);
						}
					}
				}
			}
		}
		
		//从子节点获取
		String childThingName = self.getStringBlankAsNull("childThingName");
		if(childThingName != null){
			List<Thing> childs = realSelf.getChilds(childThingName);			
			for(Thing child : childs){
				if(self.getBoolean("childThingOnly")) {
					list.add(child);
				}else {
					list.addAll(child.getChilds());
				}
			}
		}
		
		return list;
	}
	
	public static SQLConnection getConnection(ActionContext actionContext) throws OgnlException, IOException {
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value != null){
			Object obj = UtilData.getData(value, actionContext);
			
			Thing dataSource = null;
			if(obj instanceof Connection) {
				return new SQLConnection((Connection) obj, false);
			}else if(obj instanceof String) {
				String path = (String) obj;
				if(path.startsWith("_c_.")) {
					path = path.substring(4, path.length());
					dataSource = Configuration.getConfiguration(path, realSelf, actionContext); 
				} else {
					dataSource = World.getInstance().getThing((String) obj);
				}
			}else if(obj instanceof Thing) {
				dataSource = (Thing) obj;
			}
			
			if(dataSource != null) {
				Connection connection = dataSource.doAction("getConnection", actionContext);
				if(connection != null) {
					return new SQLConnection(connection, true);
				}
			}
		}
		
		if(self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		return null;
	}
	
	
	public static Object executeGroovy(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = getSelf(actionContext);
		
		String attributeName = self.getString("attributeName");
		String value = realSelf.getStringBlankAsNull(attributeName);
		if(value == null || "".equals(value)) {
			//代码是空的
			return null;
		} else {
			String key = "__self_action_grooy__" + attributeName;
			Thing groovy = realSelf.getData(key);
			if(groovy == null) {
				groovy = new Thing("xworker.lang.actions.GroovyAction");
				groovy.set("name", attributeName);
				groovy.set("code", value);
				groovy.getMetadata().setCategory(realSelf.getMetadata().getCategory());
				groovy.getMetadata().setPath(realSelf.getMetadata().getPath() + "/@attr_groovy_" + attributeName);
				groovy.set("lastMofidied", realSelf.getMetadata().getLastModified());
				realSelf.setData(key, groovy);
			}
			
			if(groovy.getLong("lastMofidied") != realSelf.getMetadata().getLastModified()) {
				groovy.set("code", value);
				groovy.set("lastMofidied", realSelf.getMetadata().getLastModified());
			}
			
			return groovy.getAction().run(actionContext);
		}
	}
	
	static class Contructors{
		Contructor[] cs;
		
		public Contructors(String c){
			String[] css = c.split("[\n]");
			cs = new Contructor[css.length];
			for(int i=0; i<css.length; i++){
				cs[i] = new  Contructor(css[i]);
			}
		}
		
		public Object[] getParams(Map<String, Object> valueMap){
			Contructor fit = null;
			for(int i=0; i<cs.length; i++){
				cs[i].test(valueMap);
				if(fit == null || fit.hit < cs[i].hit){
					fit = cs[i];
				}
			}
			
			return fit.values;
		}
	}
	
	static class Contructor{
		Object[] values;
		String[] names;
		int hit;
		
		public Contructor(String c){
			names = c.split("[,]");
			values = new Object[names.length];
		}
		
		public void test(Map<String, Object> valueMap){
			hit = 0;
			for(int i=0; i<names.length; i++){
				Object value = valueMap.get(names[i]);
				if(value != null){
					hit++;
				}
				values[i] = value;
			}
		}
	}
	
	public static List<Thing> getThingsFromCode(String text){
		World world = World.getInstance();
		List<Thing> list = new ArrayList<Thing>();
		
		if(text == null || "".equals(text)) {
			return list;
		}
		for(String paths : text.split("[,]")){
			for(String path : paths.split("[\n]")){
				path = path.trim();
				if(path.startsWith("#")) {
					//这是注释
					continue;
				}
				Thing thing = world.getThing(path.trim());
				if(thing != null){
					list.add(thing);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 把事物的路径加入到text中，并返回新的text。
	 * 
	 * @param thing
	 * @param text
	 * @return
	 */
	public static String addThingToCode(Thing thing, String text) {
		if(text == null || "".equals(text.trim())) {
			return thing.getMetadata().getPath();
		}
		
		World world = World.getInstance();
		boolean have = false;
		for(String paths : text.split("[,]")){
			for(String path : paths.split("[\n]")){
				path = path.trim();
				if(path.startsWith("#")) {
					//这是注释
					continue;
				}
				if(thing == world.getThing(path)){
					have = true;
					break;
				}				
			}
			if(have) {
				break;
			}
		}
				
		if(have) {
			return text;
		}else {
			return text + "\n" + thing.getMetadata().getPath();
		}
	}

	
	public static String removeThinInCode(Thing thing, String text) {
		if(text == null || "".equals(text)) {
			return text;
		}
		
		World world = World.getInstance();
		String newText = null;
		for(String paths : text.split("[,]")){
			for(String path : paths.split("[\n]")){
				path = path.trim();
				if(path.startsWith("#")) {
					if(newText == null) {
						newText = path;
					}else {
						newText = newText + "\n" + path;
					}
				}
				if(thing == world.getThing(path)){
					//过滤掉该行
				}else {
					if(newText == null) {
						newText = path;
					}else {
						newText = newText + "\n" + path;
					}
				}
			}
		}
		
		return newText;
	}
	
	public static Object executeChildsAction(ActionContext actionContext) {
		Object result = null;
		Thing self = actionContext.getObject("self");
		String action = self.doAction("getAction", actionContext);
		Thing realSelf = getSelf(actionContext);
		
		if(action != null && !"".equals(action) && realSelf != null) {
			for(Thing child : realSelf.getChilds()) {
				result = child.doAction(action, actionContext);
			}
		}
		return result;
	}
	
	@SuppressWarnings({"rawtypes"})
	public static Object getJsonObject(ActionContext actionContext) throws Exception {
		Object object = getObject(actionContext);
		
		if(object instanceof String) {
			Thing jasonFormat = World.getInstance().getThing("xworker.lang.text.JsonDataFormat");
            object = jasonFormat.doAction("parse", actionContext, UtilMap.toParams(new Object[]{"jsonText", object}));
            if(object instanceof Map) {
            	filterMapObject((Map) object, actionContext);
            }else if(object instanceof List) {
            	filterListObject((List) object, actionContext);
            }
            
            return object;
		}else {
			return object;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public static void filterMapObject(Map map, ActionContext actionContext) throws OgnlException, IOException {
		for(Object key : map.keySet()) {
			Object value = map.get(key);
			if(value instanceof String) {
				map.put(key, UtilData.getData((String) value, actionContext)) ;
			}else if(value instanceof Map) {
				filterMapObject((Map) value, actionContext);
			}else if(value instanceof List) {
				filterListObject((List) value, actionContext);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public static void filterListObject(List list, ActionContext actionContext) throws OgnlException, IOException {
		for(int i=0;i < list.size(); i++) {
			Object value = list.get(i);
			if(value instanceof String) {
				value = UtilData.getData((String) value, actionContext);
				list.set(i, value);
			}else if(value instanceof Map) {
				filterMapObject((Map) value, actionContext);
			}else if(value instanceof List) {
				filterListObject((List) value, actionContext);
			}
		}
	}
	
	public static ThingManager getThingManager(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		Thing realSelf = getSelf(actionContext);
		
		ThingManager thingManager = null;
		Object obj = getObject(actionContext);
		if(obj instanceof ThingManager) {
			thingManager = (ThingManager) obj;
		}else if(obj instanceof String) {
			thingManager = World.getInstance().getThingManager((String) obj);
		}
		
		if(thingManager == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return thingManager;
	}
	
	public static Category getCategory(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		Thing realSelf = getSelf(actionContext);
		
		Category catetgory = null;
		Object obj = getObject(actionContext);
		if(obj instanceof ThingManager) {
			catetgory = (Category) obj;
		}else if(obj instanceof String) {
			String path = (String) obj;
			ThingManager thingManager = World.getInstance().getThingManagers().get(0);
			int index = path.indexOf("|");
			if(index != -1) {
				thingManager = World.getInstance().getThingManager(path.substring(0, index));
				path = path.substring(index + 1, path.length());
			}
			
			if(thingManager != null) {
				catetgory = thingManager.getCategory(path);
			}			
		}
		
		if(catetgory == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return catetgory;
	}
	
	public static Thing getConfiguration(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing realSelf = getSelf(actionContext);
		
		String name = realSelf.getStringBlankAsNull(self.getString("attributeName"));
		//String descriptor = self.getString("configDescriptor");
		
		Thing config = Configuration.getConfiguration(name, realSelf, actionContext);
		if(config == null && self.getBoolean("notNull")){
			throw new ActionException(self.getMetadata().getName() + " return null, action=" + realSelf.getMetadata().getPath());
		}
		
		return config;
	}
}
