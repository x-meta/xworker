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
package xworker.swt.util;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * UtilSwt是新的Swt工具类，用于逐渐替换SwtUtils。
 * 
 * @author zyx
 *
 */
public class UtilSwt {
	private static float scaling = 1;
	static{
		Thing config = World.getInstance().getThing("_local.xworker.config.GlobalConfig");
		if(config != null){
			scaling = config.getFloat("swt_scaling");
			if(scaling < 0.5 && scaling > 0){
				scaling = 0.5f;
			}
		}
	}
	
	public static float getScaling(){
		return scaling;
	}
	
	public static void setScaling(float scaling){
		UtilSwt.scaling = scaling;
	}
	
	/**
	 * 获取缩放的的数值。
	 * 
	 * @param self
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(Thing self, String name, int defaultValue){
		if(self.getStringBlankAsNull(name) == null){
			return defaultValue;
		}else{
			int value = self.getInt(name);
			if(value <= 0){
				return value;
			}else{
				value = (int) ((value * 100 * scaling) / 100);
				return value;
			}
		}
	}
	
	public static int getInt(int value){
		if(value <= 0){
			return value;
		}else{
			return (int) ((value * 100 * scaling) / 100);
		}
	}
	
	public static Point createPoint(String pointStr){
		if(pointStr == null){
			return null;
		}
		
		String strs[] = pointStr.split("[,]");
		try{
			int x = Integer.parseInt(strs[0]);
			int y = Integer.parseInt(strs[1]);			
			
			return new Point(x, y);
		}catch(Exception e){
			return null;
		}
	}
	
	public static Rectangle createRectangle(String rectangleStr){
		if(rectangleStr == null){
			return null;
		}
		
		String strs[] = rectangleStr.split("[,]");
		try{
			int x = getInt(Integer.parseInt(strs[0]));
			int y = getInt(Integer.parseInt(strs[1]));
			int width = getInt(Integer.parseInt(strs[2]));
			int height = getInt(Integer.parseInt(strs[3]));
			
			return new Rectangle(x, y, width, height);
		}catch(Exception e){
			return null;
		}
	}
	
	public static void setForeground(Control control, String colorStr, ActionContext actionContext){
		if(control == null){
			return;
		}
		
		Color color = createColor(control, colorStr, actionContext);
		if(color != null){
			control.setForeground(color);
		}
	}
	
	/**
	 * 设置控件的颜色。
	 * @param control
	 * @param colorStr
	 * @param actionContext
	 */
	public static void setBackground(Control control, String colorStr, ActionContext actionContext){
		if(control == null){
			return;
		}
		
		Color color = createColor(control, colorStr, actionContext);
		if(color != null){
			control.setBackground(color);
		}
	}
	
	/**
	 * 创建一个颜色。
	 * 
	 * 颜色代码如果用引号包围，那么是颜色常量，会创建一个颜色，此时如果控件存在那么对控件加入销毁监听，控件销毁时颜色销毁。
	 * 
	 * 如果控件不存在且创建了新的颜色，调用者程序负责销毁颜色。
	 * 
	 * @param parent
	 * @param color
	 * @param actionContext
	 * @return
	 */
	public static Color createColor(Control parent, String color, ActionContext actionContext){
		if(color == null) return null;
		
		String rgbStr = color;
		int rgb[] = parseRGB(rgbStr);
		if(rgb != null) {
			Color c = new Color(Display.getCurrent(), rgb[0], rgb[1], rgb[2]);
			if(parent != null){
				new UtilSwt.AttachedDisposeListener(parent, c);
			}
			
			return c;
		}else {
			Object c = actionContext.get(color);
			if(c != null && c instanceof Color){
				return (Color) c;
			}else{
				return SwtUtils.getColor(color);
			}
		}
				
	}
	
	public static Cursor createCursor(Control parent, String cursorStr, ActionContext actionContext){
		if(cursorStr == null) return null;
		
		String curStr = cursorStr;
		if(cursorStr.startsWith("\"") && cursorStr.endsWith("\"")){
			curStr = cursorStr.substring(1, cursorStr.length() - 1);			
		}else{
			Object curObj = actionContext.get(cursorStr);
			if(curObj instanceof Cursor){
				return (Cursor) curObj;
			}
		}
		
		curStr = curStr.toUpperCase();
		if("CURSOR_APPSTARTING".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_APPSTARTING);
		}else if("CURSOR_ARROW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
		}else if("CURSOR_CROSS".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_CROSS);
		}else if("CURSOR_HAND".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		}else if("CURSOR_HELP".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_HELP);
		}else if("CURSOR_IBEAM".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM);
		}else if("CURSOR_NO".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_NO);
		}else if("CURSOR_SIZEALL".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL);
		}else if("CURSOR_SIZEE".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZEE);
		}else if("CURSOR_SIZEN".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZEN);
		}else if("CURSOR_SIZENE".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZENE);
		}else if("CURSOR_SIZENESW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZENESW);
		}else if("CURSOR_SIZENS".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZENS);
		}else if("CURSOR_SIZENW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZENW);
		}else if("CURSOR_SIZENWSE".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZENWSE);
		}else if("CURSOR_SIZES".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZES);
		}else if("CURSOR_SIZESE".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZESE);
		}else if("CURSOR_SIZESW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZESW);
		}else if("CURSOR_SIZEW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZEW);
		}else if("CURSOR_SIZEWE".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_SIZEWE);
		}else if("CURSOR_UPARROW".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_UPARROW);
		}else if("CURSOR_WAIT".equals(curStr)){
			return parent.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
		}
		
		return null;
	}
	
	public static void setFont(Control control, String fontStr, ActionContext actionContext){
		if(control == null || control.isDisposed()){
			return;
		}
		
		Font font = createFont(control, fontStr, actionContext);
		if(font != null){
			control.setFont(font);
		}
	}
	
	public static Image createImage(Control parent, String imageName, ActionContext actionContext){
		if(imageName == null) return null;
		
		String imageFile = imageName;
		if(imageName.startsWith("\"")){
			//常量
			imageFile = imageName.substring(1, imageFile.length() - 1);
		}else{
			Object img = actionContext.get(imageName);
			if(img instanceof Image){
				return (Image) img;
			}
		}
		
		Image image = null;
		File file = new File(imageFile);
		if(file.exists() && file.isFile()){
			image = new Image(null, imageFile);			
		}else if(imageFile.indexOf(":") != -1){
			imageFile = imageFile.replace(':', '/');
			imageFile = World.getInstance().getPath() + "/" + imageFile;
			
			image = new Image(null, imageFile);
		}

		if(parent != null && image != null){
			new UtilSwt.AttachedDisposeListener(parent, image);
		}
		
		return image;
	}
	
	public static Font createFont(Control parent, String fontStr, ActionContext actionContext){
		if(fontStr == null) return null;
		
		if(fontStr.startsWith("\"") && fontStr.endsWith("\"")){
			fontStr = fontStr.substring(1, fontStr.length() - 1);
			String fs[] = fontStr.split("[|]");
			String name = "";
			int height = -1;
			int style = SWT.NORMAL;
			
			if(fs.length >= 1){
				name = fs[0];
			}
			
			if(fs.length >= 2){
				height = Integer.parseInt(fs[1]);
			}
			
			if(fs.length >= 3){
				style = Integer.parseInt(fs[2]);
			}
			
			Font f = new Font(Display.getCurrent(), name, height, style);
			if(parent != null){
				new UtilSwt.AttachedDisposeListener(parent, f);
			}		
			
			return f;
		}else{
			Object f = actionContext.get(fontStr);
			if(f instanceof Font){
				return (Font) f;
			}else{
				return null;
			}
		}
	}	
	
	public static FontData parseFontData(String fontStr){
		if(fontStr == null) return null;
		
		if(fontStr.startsWith("\"") && fontStr.endsWith("\"")){
			fontStr = fontStr.substring(1, fontStr.length() - 1);
			String fs[] = fontStr.split("[|]");
			String name = "";
			int height = -1;
			int style = SWT.NORMAL;
			
			if(fs.length >= 1){
				name = fs[0];
			}
			
			if(fs.length >= 2){
				height = Integer.parseInt(fs[1]);
			}
			
			if(fs.length >= 3){
				style = Integer.parseInt(fs[2]);
			}
			
			return new FontData(name, height, style);
		}else{
			return null;
		}
	}
	
	public static RGB parseFontRGB(String fontStr){
		if(fontStr == null) return null;
		
		if(fontStr.startsWith("\"") && fontStr.endsWith("\"")){
			fontStr = fontStr.substring(1, fontStr.length() - 1);
			String fs[] = fontStr.split("[|]");
			
			if(fs.length >= 4){
				String rgbStr = fs[3];
				int[] rgb = parseRGB(rgbStr);
				return new RGB(rgb[0], rgb[1], rgb[2]);
			}else{
				return null;
			}			
		}else{
			return null;
		}
	}
	
	/**
	 * 粘贴到父组件上随父组件一起销毁而销毁的监听。
	 * 
	 * @author zyx
	 *
	 */
	public static class AttachedDisposeListener implements DisposeListener{
		Widget parent;
		Widget attachedWidget;
		Resource resource;
		
		public AttachedDisposeListener(Widget parent, Widget attachedWidget){
			this.parent = parent;
			this.attachedWidget = attachedWidget;
			
			this.parent.addDisposeListener(this);
		}

		public AttachedDisposeListener(Widget parent, Resource resource){
			this.parent = parent;
			this.resource = resource;
			
			this.parent.addDisposeListener(this);
		}
		
		public void widgetDisposed(DisposeEvent event) {
			if(attachedWidget != null){
				attachedWidget.dispose();
			}
			
			if(resource != null){
				resource.dispose();
			}
		}
	}
	
	/**
	 * 分析RGB颜色字符串，比如#FFCCFF。
	 * 
	 * @param rgb
	 * @return
	 */
	public static int[] parseRGB(String rgb){
		if(rgb == null) return null;
		
		String temp = rgb;
		if(rgb.startsWith("\"")){
			temp = rgb.substring(1, rgb.length() - 1);
		}
		if(temp.startsWith("#")){
			temp = temp.substring(1, temp.length());
		}
		
		if(temp.length() < 6){
			return null;
		}
		
		try{
			String r = temp.substring(0, 2);
			String g = temp.substring(2, 4);
			String b = temp.substring(4, 6);
			int rr[] = new int[3];
			rr[0] = Integer.parseInt(r, 16);
			rr[1] = Integer.parseInt(g, 16);
			rr[2] = Integer.parseInt(b, 16);
		
			return rr;
		}catch(Exception e){
			return null;
		}
	}
	
	public static String RGBToString(RGB rgb){
		String red = Integer.toHexString(rgb.red).toUpperCase();	
		String green = Integer.toHexString(rgb.green).toUpperCase();
		String blue = Integer.toHexString(rgb.blue).toUpperCase();
		
		if(red.length() == 1) red = "0" + red;
		if(green.length() == 1) green = "0" + green;
		if(blue.length() == 1) blue = "0" + blue;
		
		return "#" + red + green + blue;
	}
}