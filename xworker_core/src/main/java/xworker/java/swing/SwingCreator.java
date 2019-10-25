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
package xworker.java.swing;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.DebugGraphics;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

public class SwingCreator {
	public static KeyStroke createKeyStroke(Thing thing, String name, ActionContext actionContext){
		return null;
	}
	
	public static Integer createDebugGraphicsConstants(Thing thing, String name){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		if("BUFFERED_OPTION".equals(value)){
			return DebugGraphics.BUFFERED_OPTION;
		}else if("FLASH_OPTION".equals(value)){
			return DebugGraphics.FLASH_OPTION;
		}else if("LOG_OPTION".equals(value)){
			return DebugGraphics.LOG_OPTION;
		}else if("NONE_OPTION".equals(value)){
			return DebugGraphics.NONE_OPTION;
		}else{
			return null;
		}
	}
	
	public static Icon createIcon(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
	
		String fileName = params.get("fileName");
		if(fileName != null && !"".equals(fileName)){
			return new ImageIcon(fileName);
		}
		
		String location = params.get("location");
		if(location != null && !"".equals(location)){
			try {
				return new ImageIcon(new URL(location));
			} catch (MalformedURLException e) {
				throw new ActionException(e);
			}
		}
		
		String imageVarName = params.get("imageVarName");
		if(imageVarName != null && !"".equals(imageVarName)){
			Image image = (Image) OgnlUtil.getValue(imageVarName, actionContext);
			return new ImageIcon(image);
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			return (Icon) OgnlUtil.getValue(varName, actionContext);
		}
		return null;
	}
	
	public static Integer createConstants(Thing thing, String name){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		if("BOTTOM".equals(value)){
			return SwingConstants.BOTTOM;
		}else if("CENTER".equals(value)){
			return SwingConstants.CENTER;
		}else if("EAST".equals(value)){
			return SwingConstants.EAST;
		}else if("HORIZONTAL".equals(value)){
			return SwingConstants.HORIZONTAL;
		}else if("LEADING".equals(value)){
			return SwingConstants.LEADING;
		}else if("LEFT".equals(value)){
			return SwingConstants.LEFT;
		}else if("NEXT".equals(value)){
			return SwingConstants.NEXT;
		}else if("NORTH".equals(value)){
			return SwingConstants.NORTH;
		}else if("NORTH_EAST".equals(value)){
			return SwingConstants.NORTH_EAST;
		}else if("NORTH_WEST".equals(value)){
			return SwingConstants.NORTH_WEST;
		}else if("PREVIOUS".equals(value)){
			return SwingConstants.PREVIOUS;
		}else if("RIGHT".equals(value)){
			return SwingConstants.RIGHT;
		}else if("SOUTH".equals(value)){
			return SwingConstants.SOUTH;
		}else if("SOUTH_EAST".equals(value)){
			return SwingConstants.SOUTH_EAST;
		}else if("SOUTH_WEST".equals(value)){
			return SwingConstants.SOUTH_WEST;
		}else if("TOP".equals(value)){
			return SwingConstants.TOP;
		}else if("TRAILING".equals(value)){
			return SwingConstants.TRAILING;
		}else if("VERTICAL".equals(value)){
			return SwingConstants.VERTICAL;
		}else if("WEST".equals(value)){
			return SwingConstants.WEST;
		}else{
			return null;
		}
	}
}