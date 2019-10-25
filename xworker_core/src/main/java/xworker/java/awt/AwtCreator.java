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
package xworker.java.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.DropMode;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.java.JavaCreator;

public class AwtCreator {
	public static Insets createInsets(Thing thing , String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		Integer top = JavaCreator.createInteger(params, "top");
		Integer left = JavaCreator.createInteger(params, "left");
		Integer right = JavaCreator.createInteger(params, "right");
		Integer bottom = JavaCreator.createInteger(params, "bottom");
		
		if(top != null && left != null && right != null && bottom != null){
			return new Insets(top, left, bottom, right);
		}
		
		return null;
	}
	
	public static Rectangle createRectangle(Thing thing , String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		Integer x = JavaCreator.createInteger(params, "x");
		Integer y = JavaCreator.createInteger(params, "y");
		Integer width = JavaCreator.createInteger(params, "width");
		Integer height = JavaCreator.createInteger(params, "height");
		
		if(x != null && y != null && width != null && height != null){
			return new Rectangle(x, y, width, height);
		}
		
		return null;
	}
	
	public static Image createImage(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
	
		String fileName = params.get("fileName");
		if(fileName != null && !"".equals(fileName)){
			return Toolkit.getDefaultToolkit().createImage(fileName);
		}
		
		String location = params.get("location");
		if(location != null && !"".equals(location)){
			try {
				return Toolkit.getDefaultToolkit().createImage(new URL(location));
			} catch (MalformedURLException e) {
				throw new ActionException(e);
			}
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			return (Image) OgnlUtil.getValue(varName, actionContext);
		}
		return null;
	}
	
	public static BasicStroke createBasicStroke(Thing thing, String name, ActionContext actionContext){
		return null;
	}
	
	public static Paint createPaint(Thing thing, String name, ActionContext actionContext){
		return null;
	}
	
	public static Dimension createDimension(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		Integer width = JavaCreator.createInteger(params, "width");
		Integer height = JavaCreator.createInteger(params, "height");
		if(width != null && height != null){
			return new Dimension(width, height);
		}
		
		return null;
	}
	
	public static Point createPoint(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		Integer x = JavaCreator.createInteger(params, "x");
		Integer y = JavaCreator.createInteger(params, "y");
		
		if(x != null && y != null){
			return new Point(x, y);
		}
		return null;
	}
	
	public static Cursor createCursor(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		String predefined = params.get("predefined");
		if(predefined != null && !"".equals(predefined)){
			if("CROSSHAIR_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			}else if("CUSTOM_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.CUSTOM_CURSOR);
			}else if("DEFAULT_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
			}else if("E_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			}else if("HAND_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			}else if("MOVE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			}else if("N_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			}else if("NE_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			}else if("NW_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			}else if("S_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			}else if("SE_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			}else if("SW_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			}else if("TEXT_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
			}else if("W_RESIZE_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			}else if("WAIT_CURSOR".equals(predefined)){
				return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			}
		}
		
		String cursorName = params.get("cursorName");
		if(cursorName != null && !"".equals(cursorName)){
			try {
				return Cursor.getSystemCustomCursor(cursorName);
			} catch (Exception e) {
				throw new ActionException("Create cursor exception", e);
			} 
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			return (Cursor) OgnlUtil.getValue(varName, actionContext);
		}
		
		return null;
	}
	
	public static Integer createMnemonic(Thing thing, String name){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		
		String char_ = params.get("char");
		if(char_ != null && !"".equals(char_)){
			return (int) char_.charAt(0);
		}
		
		return null;
	}
	
	public static Color createColor(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		String color = params.get("color");
		if(color != null && !"".equals(color)){
			if(color.startsWith("\"")){
				color = color.substring(1, color.length() - 1);
			}
			
			if(color.startsWith("#")){
				color = color.substring(1, color.length());
			}
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4, 6), 16);
			
			Float alpha = JavaCreator.createFloat(params, "alpha");
			if(alpha != null){
				return new Color(r / 256f, g /256f, b /256f, alpha);
			}else{
				return new Color(r, g, b);
			}			
		}
		
		String normalColor = params.get("normalColor");
		if(normalColor != null && !"".equals(normalColor)){
			if("BLACK".equals(normalColor)){
				return Color.BLACK;
			}else if("BLUE".equals(normalColor)){
				return Color.BLUE;
			}else if("CYAN".equals(normalColor)){
				return Color.CYAN;
			}else if("DARK_GRAY".equals(normalColor)){
				return Color.DARK_GRAY;
			}else if("GRAY".equals(normalColor)){
				return Color.GRAY;
			}else if("GREEN".equals(normalColor)){
				return Color.GREEN;
			}else if("LIGHT_GRAY".equals(normalColor)){
				return Color.LIGHT_GRAY;
			}else if("MAGENTA".equals(normalColor)){
				return Color.MAGENTA;
			}else if("ORANGE".equals(normalColor)){
				return Color.ORANGE;
			}else if("PINK".equals(normalColor)){
				return Color.PINK;
			}else if("RED".equals(normalColor)){
				return Color.RED;
			}else if("WHITE".equals(normalColor)){
				return Color.WHITE;
			}else if("YELLOW".equals(normalColor)){
				return Color.YELLOW;
			}
		}
		
		String systemColor = params.get("systemColor");
		if(systemColor != null && !"".equals(systemColor)){
			if("activeCaption".equals(systemColor)){
				return SystemColor.activeCaption;
			}else if("activeCaptionBorder".equals(systemColor)){
				return SystemColor.activeCaptionBorder;
			}else if("activeCaptionText".equals(systemColor)){
				return SystemColor.activeCaptionText;
			}else if("control".equals(systemColor)){
				return SystemColor.control;
			}else if("controlDkShadow".equals(systemColor)){
				return SystemColor.controlDkShadow;
			}else if("controlHighlight".equals(systemColor)){
				return SystemColor.controlHighlight;
			}else if("controlLtHighlight".equals(systemColor)){
				return SystemColor.controlLtHighlight;
			}else if("controlShadow".equals(systemColor)){
				return SystemColor.controlShadow;
			}else if("controlText".equals(systemColor)){
				return SystemColor.controlText;
			}else if("desktop".equals(systemColor)){
				return SystemColor.desktop;
			}else if("inactiveCaption".equals(systemColor)){
				return SystemColor.inactiveCaption;
			}else if("inactiveCaptionBorder".equals(systemColor)){
				return SystemColor.inactiveCaptionBorder;
			}else if("inactiveCaptionText".equals(systemColor)){
				return SystemColor.inactiveCaptionText;
			}else if("info".equals(systemColor)){
				return SystemColor.info;
			}else if("infoText".equals(systemColor)){
				return SystemColor.infoText;
			}else if("menu".equals(systemColor)){
				return SystemColor.menu;
			}else if("menuText".equals(systemColor)){
				return SystemColor.menuText;
			}else if("scrollbar".equals(systemColor)){
				return SystemColor.scrollbar;
			}else if("text".equals(systemColor)){
				return SystemColor.text;
			}else if("textHighlight".equals(systemColor)){
				return SystemColor.textHighlight;
			}else if("textHighlightText".equals(systemColor)){
				return SystemColor.textHighlightText;
			}else if("textInactiveText".equals(systemColor)){
				return SystemColor.textInactiveText;
			}else if("textText".equals(systemColor)){
				return SystemColor.textText;
			}else if("window".equals(systemColor)){
				return SystemColor.window;
			}else if("windowBorder".equals(systemColor)){
				return SystemColor.windowBorder;
			}else if("windowText".equals(systemColor)){
				return SystemColor.windowText;
			}
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			return (Color) OgnlUtil.getValue(varName, actionContext);
		}
		return null;
	}
	
	public static Font createFont(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		Map<String,String> params = UtilString.getParams(value);
		String font = params.get("font");
		if(font != null){
			if(font.startsWith("\"")){
				font = font.substring(1, font.length());
			}
			
			String ss[] = font.split("[|]");
			if(ss.length > 2){
				return new Font(ss[0], Integer.parseInt(ss[2]), Integer.parseInt(ss[1]));
			}
		}
		
		String varName = params.get("varName");
		if(varName != null && !"".equals(varName)){
			return (Font) OgnlUtil.getValue(varName, actionContext);
		}
		return null;
	}
	
	public static ComponentOrientation createComponentOrientationConstants(Thing thing, String name){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		if("LEFT_TO_RIGHT".equals(value)){
			return ComponentOrientation.LEFT_TO_RIGHT;
		}else if("RIGHT_TO_LEFT".equals(value)){
			return ComponentOrientation.RIGHT_TO_LEFT;
		}else if("UNKNOWN".equals(value)){
			return ComponentOrientation.UNKNOWN;
		}else{
			return null;
		}
	}
	
	public static DropMode createDropMode(Thing thing, String name, ActionContext actionContext){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		if("INSERT".equals(value)){
			return DropMode.INSERT;
		}else if("INSERT_COLS".equals(value)){
			return DropMode.INSERT_COLS;
		}else if("INSERT_ROWS".equals(value)){
			return DropMode.INSERT_ROWS;
		}else if("ON".equals(value)){
			return DropMode.ON;
		}else if("ON_OR_INSERT".equals(value)){
			return DropMode.ON_OR_INSERT;
		}else if("ON_OR_INSERT_COLS".equals(value)){
			return DropMode.ON_OR_INSERT_COLS;
		}else if("ON_OR_INSERT_ROWS".equals(value)){
			return DropMode.ON_OR_INSERT_ROWS;
		}else if("USE_SELECTION".equals(value)){
			return DropMode.USE_SELECTION;
		}else{
			return null;
		}
	}
}