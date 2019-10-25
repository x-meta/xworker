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
package xworker.java;

import java.io.File;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

public class JavaCreator {
	public static File createFile(Thing thing, String name, ActionContext actionContext){
		return null;
	}
	
	public static Boolean createBoolean(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getBoolean(attributeName);
		}
	}
	
	public static Long createLong(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getLong(attributeName);
		}
	}
	
	public static Integer createInteger(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getInt(attributeName);
		}
	}
	
	public static Character createChar(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getChar(attributeName);
		}
	}
	
	public static Float createFloat(Map<String, String> params, String name){
		String value = params.get(name);
		if(value == null || "".equals(value)){
			return null;
		}else{
			return Float.parseFloat(value);
		}
	}
	
	public static Integer createInteger(Map<String, String> params, String name){
		String value = params.get(name);
		if(value == null || "".equals(value)){
			return null;
		}else{
			return Integer.parseInt(value);
		}
	}
	
	public static Float createFloat(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getFloat(attributeName);
		}
	}
	
	public static Double createDouble(Thing thing, String attributeName){
		String value = thing.getStringBlankAsNull(attributeName);
		if(value == null){
			return null;
		}else{
			return thing.getDouble(attributeName);
		}
	}
	
	public static String createText(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		String string = params.get("string");
		if(string != null && !"".equals(string)){
			return string;
		}
		
		String thingPath = params.get("thingPath");
		if(thingPath != null && !"".equals(thingPath)){
			Thing labelthing = World.getInstance().getThing(thingPath);
			if(labelthing != null){
				return labelthing.getMetadata().getLabel();
			}
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			Object v = OgnlUtil.getValue(varName, actionContext);
			if(v != null){
				return v.toString();
			}
		}
		
		return value;
	}
}