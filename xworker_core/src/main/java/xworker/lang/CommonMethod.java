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
package xworker.lang;

import java.lang.reflect.Method;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class CommonMethod {

	// 设置一个属性的值
	public static void setValue(Class<?> cls, Object obj,
			Thing attributeDescriptor, String type, Thing thing, Object value) throws OgnlException, ClassNotFoundException {
		String setMethod = attributeDescriptor.getString("setMethod");

		if (setMethod != null) {
			// 指定了设置值的方法
			Class<?>[] classArray = new Class[1];
			// println("get attribute type,name=" + attributeDescriptor.name +
			// ",type=" + type);

			classArray[0] = getClassByName(type);
			Object[] valueArray = new Object[1];
			valueArray[0] = value;

			// 使用setMethod中定义的方法
			if (invokeMethod(attributeDescriptor, cls, obj, setMethod,
					classArray, valueArray)) {
				return;
			}
		} else {
			// 其他按属性设置
			OgnlUtil.setValue(attributeDescriptor.getString("name"), obj, value);
		}
	}

	// 调用一个方法
	public static boolean invokeMethod(Thing thing, Class<?> cls, Object obj,
			String methodName, Class<?>[] paramClass, Object[] paramValue) {
		try {
			Method method = cls.getMethod(methodName, paramClass);
			if (method != null) {
				method.invoke(obj, paramValue);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// actionContext.log.info("invoke method error, method=" +
			// methodName + ",path=" + thing.metadata.path, e);
			return false;
		}
	}

	// 获取属性的值
	public static Object getAttributeValue(Thing descThing, Thing thing,
			Object value, ActionContext actionContext) {
		String attributeName = descThing.getString("name");
		if (value == null) {
			return null;
		} else if (value instanceof String
				&& ((String) value).startsWith("var:")) {
			// 变量引用
			return actionContext.get(((String) value).substring(4,
					((String) value).length()));
		} else if (value instanceof String && "=null".equals(value)) {
			return null;
		} else if (value instanceof String
				&& ((String) value).startsWith("label:")) {
			// i18n标签
			String thingPath = ((String) value).substring(6, ((String) value)
					.length());
			Thing labelThing = World.getInstance().getThing(thingPath);
			if (labelThing != null) {
				return labelThing.getMetadata().getLabel();
			} else {
				return thingPath;
			}
		} else {
			String vType = descThing.getString("vType");
			if ("string".equals(vType)) {
				return thing.getString(attributeName);
			} else if ("bigDecimal".equals(vType)) {
				return thing.getBigDecimal(attributeName);
			} else if ("bigInteger".equals(vType)) {
				return thing.getBigInteger(attributeName);
			} else if ("boolean".equals(vType)) {
				return thing.getBoolean(attributeName);
			} else if ("byte".equals(vType)) {
				return thing.getByte(attributeName);
			} else if ("bytes".equals(vType)) {
				return thing.getBytes(attributeName);
			} else if ("char".equals(vType)) {
				return thing.getChar(attributeName);
			} else if ("date".equals(vType)) {
				return thing.getDate(attributeName);
			} else if ("double".equals(vType)) {
				return thing.getDouble(attributeName);
			} else if ("float".equals(vType)) {
				return thing.getFloat(attributeName);
			} else if ("int".equals(vType)) {
				return thing.getInt(attributeName);
			} else if ("long".equals(vType)) {
				return thing.getLong(attributeName);
			} else if ("short".equals(vType)) {
				return thing.getShort(attributeName);
			} else {
				return value;
			}
		}
	}

	public static Class<?> getClassByName(String className) throws ClassNotFoundException {
		if("int.class".equals(className) ||	"int".equals(className)){
			return int.class;
		}else if("byte.class".equals(className) || "byte".equals(className)){
			return byte.class;
		}else if("char.class".equals(className) || "char".equals(className)){
			return char.class;
		}else if("short.class".equals(className) || "short".equals(className)){
			return short.class;
		}else if("long.class".equals(className) || "long".equals(className)){
			return long.class;
		}else if("float.class".equals(className) || "float".equals(className)){
			return float.class;
		}else if("double.class".equals(className) || "double".equals(className)){
			return double.class;
		}else if("boolean.class".equals(className) || "boolean".equals(className)){
			return boolean.class;
		}else if("string".equals(className)){
			return String.class;
		}else {
			return Class.forName(className);

		}
	}
}