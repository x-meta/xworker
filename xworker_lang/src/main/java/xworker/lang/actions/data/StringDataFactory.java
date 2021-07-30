package xworker.lang.actions.data;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.enums.EnumUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import org.xmeta.util.UtilString;
import xworker.lang.Configuration;
import xworker.lang.actions.ActionUtils;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;
import xworker.util.UtilTemplate;
import xworker.util.XWorkerUtils;

public class StringDataFactory {
	private static final String TAG = StringDataFactory.class.getName();

	public static Object getObjectData(Thing owner, Thing action, Object value, ActionContext actionContext) throws Exception {
		if(value instanceof String) {
			String v = (String) value;
			return getStringData(owner, action, v, actionContext);
		}
		
		return value;
	}

	/**
	 * 根据value参数的值返回指定的对象。
	 *
	 * @param owner
	 * @param action
	 * @param value
	 * @param actionContext
	 * @return
	 * @throws Exception
	 */
	public static Object getStringData(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		if(value == null) {
			return null;
		}
		
		if(value.startsWith("_c_.")) {
			Object data = getConfig(owner, value, actionContext);
			return getObjectData(owner, action, data, actionContext);
		}else if(value.contains(":")) {
			if (value.startsWith("boolean:")) {
				return getBoolean(owner, action, value, actionContext);
			} else if (value.startsWith("bigDecimal:")) {
				return getBigDecimal(owner, action, value, actionContext);
			} else if (value.startsWith("bigInteger:")) {
				return getBigInteger(owner, action, value, actionContext);
			} else if (value.startsWith("byte:")) {
				return getBigInteger(owner, action, value, actionContext);
			} else if (value.startsWith("bytes:")) {
				return getBytes(owner, action, value, actionContext);
			} else if (value.startsWith("char:")) {
				return getChar(owner, action, value, actionContext);
			} else if (value.startsWith("class:")) {
				return getClass(owner, action, value, actionContext);
			} else if (value.startsWith("classes:")) {
				return getClasses(owner, action, value, actionContext);
			} else if (value.startsWith("date:")) {
				return getDate(owner, action, value, actionContext);
			} else if (value.startsWith("double:")) {
				return getDouble(owner, action, value, actionContext);
			} else if (value.startsWith("enum:")) {
				return getEnum(owner, action, value, actionContext);
			} else if (value.startsWith("file:")) {
				return getFile(owner, action, value, actionContext);
			} else if (value.startsWith("float:")) {
				return getFloat(owner, action, value, actionContext);
			} else if (value.startsWith("int:")) {
				return getInt(owner, action, value, actionContext);
			} else if (value.startsWith("jsonObj:")) {
				return getJsonObj(owner, action, value, actionContext);
			} else if (value.startsWith("long:")) {
				return getLong(owner, action, value, actionContext);
			} else if (value.startsWith("ognl:")) {
				return getOgnl(owner, action, value, actionContext);
			} else if (value.startsWith("self:")) {
				return getSelf(owner, action, value, actionContext);
			} else if (value.startsWith("short:")) {
				return getShort(owner, action, value, actionContext);
			} else if (value.startsWith("string:")) {
				return getString(owner, action, value, actionContext);
			} else if(value.startsWith("cstr:")){
				return getCString(owner, action, value, actionContext);
			}else if (value.startsWith("string[]:")) {
				return getStringArray(owner, action, value, actionContext);
			} else if (value.startsWith("stringList:")) {
				return getStirngList(owner, action, value, actionContext);
			} else if (value.startsWith("thing:")) {
				return getThing(owner, action, value, actionContext);
			} else if (value.startsWith("url:")) {
				return getURL(owner, action, value, actionContext);
			} else if (value.startsWith("var:")) {
				return getVar(owner, action, value, actionContext);
			} else if (value.startsWith("parentVar:")) {
				return getParnetVar(owner, action, value, actionContext);
			} else if (value.startsWith("template:")) {
				return getTemplate(owner, action, value, actionContext);
			} else if (value.startsWith("action:")) {
				return getAction(owner, action, value, actionContext);
			} else if (value.startsWith("label:")) {
				return getLabel(owner, action, value, actionContext);
			} else if (value.startsWith("desc:")) {
				return getDesc(owner, action, value, actionContext);
			} else if (value.startsWith("descurl:")) {
				return getDescUrl(owner, action, value, actionContext);
			} else if (value.startsWith("attr:")) {
				return getAttr(owner, action, value, actionContext);
			} else if (value.startsWith("xworker:")) {
				return getXWorker(owner, action, value, actionContext);
			} else if (value.startsWith("hex:")) {
				return getHex(owner, action, value, actionContext);
			} else if (value .startsWith("base64:")){
				return getBase64(owner, action, value, actionContext);
			} else if (value.startsWith("lang:")){
				return getLang(owner, action, value, actionContext);
			}
		}
		
		return value;
	}

	public static Object getLang(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());

		Map<String, String> params = UtilString.getParams(value);
		Session session = SessionManager.getSession( actionContext);
		Locale locale = session.getLocale();
		String language = locale.getLanguage();
		value = params.get(language);
		if(value == null){
			value = params.get("d");
		}
		if(value == null){
			value = params.get("default");
		}

		return value;
	}

	public static Object getBase64(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(7, value.length());
		Object obj = getStringData(owner, action, value, actionContext);

		if(obj instanceof byte[]){
			return Base64.encodeBase64String((byte[]) obj);
		}else if(obj instanceof  String){
			return Base64.decodeBase64((String) obj);
		}else{
			Executor.debug(TAG, "Can not handle Base64, return null , object=" + obj + ", owner=" + owner.getMetadata().getPath()
					+ ",action=" + action.getMetadata().getName());
			return null;
		}
	}

	public static Object getHex(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(4, value.length());
		Object obj = getStringData(owner, action, value, actionContext);

		if(obj instanceof byte[]){
			return Hex.encodeHexString((byte[]) obj);
		}else if(obj instanceof  String){
			return Hex.decodeHex((String) obj);
		}else{
			Executor.debug(TAG, "Can not handle HEX, return null , object=" + obj + ", owner=" + owner.getMetadata().getPath()
					+ ",action=" + action.getMetadata().getName());
			return null;
		}
	}

	public static Object getXWorker(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(8, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			return World.getInstance().getPath() + "/" + value;
		}
		return obj;
	}

	public static Object getAttr(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			World.getInstance().get((String) obj);
		}
		return obj;
	}

	public static Object getDesc(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			Thing thing = World.getInstance().getThing((String) obj);
			if(thing != null){
				return thing.getMetadata().getDescription();
			}
		}else if(obj instanceof  Thing){
			return ((Thing) obj).getMetadata().getDescription();
		}

		return obj;
	}

	public static Object getDescUrl(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(8, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			return XWorkerUtils.getThingDescUrl((String) obj);
		}else if(obj instanceof  Thing){
			return XWorkerUtils.getThingDescUrl((Thing) obj);
		}

		return obj;
	}

	public static Object getLabel(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			String thingPath = (String) obj;
			Thing labelThing = World.getInstance().getThing(thingPath);
			if(labelThing == null){
				return thingPath;
			}else{
				return labelThing.getMetadata().getLabel(actionContext);
			}
		}else if(obj instanceof Thing){
			return ((Thing) obj).getMetadata().getLabel(actionContext);
		}

		return obj;
	}

	public static Object getAction(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(7, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			try{
				return owner.doAction((String) obj, actionContext);
			}catch(Exception e){
				throw new ActionException("Get object from action error, path=" + owner.getMetadata().getPath(), e);
			}
		}

		return obj;
	}

	public static Object getTemplate(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(9, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			try{
				return UtilTemplate.processString(actionContext, (String) obj);
			}catch(Exception e){
				throw new ActionException("Get string from template error, path=" + owner.getMetadata().getPath(), e);
			}
		}
		
		return obj;
	}
	
	public static Object getParnetVar(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(10, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			return XWorkerUtils.getParentVar(actionContext, (String) obj);
		}
		
		return obj;
	}
	
	public static Object getVar(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(4, value.length());
		Object obj = getStringData(owner, action, value, actionContext);
		if(obj instanceof String) {
			return actionContext.get((String) obj);
		}
		
		return obj;
	}
	
	public static URL getURL(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(4, value.length());
		Object obj = getStringData(owner, action, value, actionContext);

		if(obj instanceof URL){
			return (URL) obj;
		}else if(obj instanceof String){
			return new URL((String) obj);
		}else if(obj instanceof URI){
			return ((URI) obj).toURL();
		}else if(obj instanceof  File) {
			return ((File) obj).toURI().toURL();
		}else{
			return null;
		}
	}
	
	public static Thing getThing(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof Thing) {
			return (Thing) data;
		}
		
		if(data instanceof String) {
			return World.getInstance().getThing((String) data);
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> getStirngList(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(11, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		
		if(data instanceof String) {
			value = (String) data;
			if(value == null || "".equals(value)) {
				return Collections.emptyList();
			}else {
				boolean splitLine = true;
				List<String> list = new ArrayList<String>();
				
				char delimiter = ',';
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
			return (List<?>) data;
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] getStringArray(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(9, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof String) {
			value = (String) data;
			if(value == null || "".equals(value)) {
				return new String[0];
			}else {
				boolean splitLine = true;
				List<String> list = new ArrayList<String>();
				
				char delimiter = ',';
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
		}else if(data != null) {
			return new String[]{data.toString()};
		}else{
			return new String[0];
		}
	}
	
	public static String getString(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(7, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof String){
			return (String) data;
		}else if(data != null){
			return data.toString();
		}else{
			return null;
		}
	}

	public static String getCString(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		return value;
	}

	public static short getShort(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getShort(data, action.getShort("defaultValue"));
	}
	
	public static Object getSelf(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		
		if(data == null) {
			return owner;
		}
		
		if(data instanceof String) {
			value = (String) data;
			if(!"".equals(value)) {
				return owner.get(value);
			}
		}
		
		return owner;
	}
	
	public static Object getOgnl(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof String) {
			return OgnlUtil.getValue((String) data, actionContext);
		}else {
			return data;
		}
	}
	
	public static long getLong(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getLong(data, action.getLong("defaultValue"));
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getJsonObj(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(8, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof String) {
			Thing jasonFormat = World.getInstance().getThing("xworker.lang.text.JsonDataFormat");
			data = jasonFormat.doAction("parse", actionContext, UtilMap.toParams(new Object[]{"jsonText", data}));
            if(data instanceof Map) {
            	ActionUtils.filterMapObject((Map) data, actionContext);
            }else if(data instanceof List) {
            	ActionUtils.filterListObject((List) data, actionContext);
            }
            
            return data;
		}else {
			return data;
		}
	}
	
	public static int getInt(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(4, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getInt(data, action.getInt("defaultValue"));
	}
	
	public static float getFloat(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getFloat(data, action.getFloat("defaultValue"));
	}
	
	public static File getFile(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof File) {
			return (File) data;
		}else if(data instanceof String) {
			return new File((String) data);
		}else {
			return null;
		}
	}
	
	public static Object getEnum(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data instanceof String) {
			String[] strs = ((String) data).split("[#]");
			Class<?> enumClass = Class.forName(strs[0]);
			return EnumUtils.getEnum(enumClass, strs[1]);
		} else {
			return data;
		}
	}
	
	public static double getDouble(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(7, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getDouble(data, action.getDouble("defaultValue"));
	}
	
	public static Date getDate(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data == null) {
			return null;
		}
		
		if(data instanceof Date) {
			return (Date) data;
		}
		
		if(data instanceof String) {
			value = (String) data;
			if(value == null || "".equals(value)) {
				value = action.getData("defaultValue");
			}
			if(value == null || "".equals(value)) {
				return null;
			}
			String format = null;
			
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
		} else {
			throw new ActionException("Can not convert " + data.getClass().getName() + " to date, action=" + action.getMetadata().getPath());
		}
	}
	
	public static Class<?>[] getClasses(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(8, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data == null) {
			return new  Class<?>[0];
		}
		
		if(data instanceof Class<?>[]) {
			return (Class<?>[]) data; 
		}
		
		if(data instanceof String) {
			value = (String) data;
			if(value == null || "".equals(value)) {
				return new Class<?>[0];
			}else {
				String vs[] = value.split("[,]");
				Class<?>[] clses = new Class<?>[vs.length];
				for(int i=0; i<vs.length; i++) {
					Object obj = UtilData.getData(vs[i], actionContext);
					if(obj == null) {
						clses[i] = null;
					}else {
						if(obj instanceof String) {
							clses[i] = ActionUtils.parseClass((String) obj);
						}else if(obj instanceof Class<?>) {
							clses[i] = (Class<?>) obj;
						}else {
							clses[i] = obj.getClass();
						}
					}
				}
				
				return clses;
			}
		}else {
			return new Class<?>[] {data.getClass()};
		}
	}
	
	public static Class<?> getClass(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		if(data == null) {
			return null;
		}
		if(data instanceof String) {
			return ActionUtils.parseClass((String) data);
		}
		if(data instanceof Class) {
			return (Class<?>) data;
		}
		
		return data.getClass();
	}
	
	public static char getChar(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getChar(data, action.getChar("defaultValue"));
	}
	
	public static byte[] getBytes(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(6, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getBytes(data, action.getBytes("defaultValue"));
	}
	
	public static byte getByte(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(5, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getByte(data, action.getByte("defaultValue"));
	}	
	
	public static BigInteger getBigInteger(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(11, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getBigInteger(data, action.getBigInteger("defaultValue"));
	}
	
	public static BigDecimal getBigDecimal(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(11, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getBigDecimal(data, action.getBigDecimal("defaultValue"));
	}
	
	public static Object getBoolean(Thing owner, Thing action, String value, ActionContext actionContext) throws Exception {
		value = value.substring(8, value.length());
		Object data = getStringData(owner, action, value, actionContext);
		return UtilData.getBoolean(data, action.getBoolean("defaultValue"));
	}
	
	public static Object getConfig(Thing owner, String value, ActionContext actionContext) {
		 String str = value.substring(4,  value.length());
		 int index = str.indexOf(":");
		 String path = null;
		 String name = str;
		 if(index !=  -1) {
			 name = str.substring(0, index);
			 path = str.substring(index + 1, str.length());
		 }
		 Thing config = Configuration.getConfiguration(name, owner, actionContext);
		 if(config != null && path != null) {
			 return config.get(path);
		 }else {
			 return config;
		 }
	}
}
