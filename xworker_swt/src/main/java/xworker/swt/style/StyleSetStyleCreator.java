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
package xworker.swt.style;

import org.eclipse.swt.custom.CTabItem;
//import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.util.UtilSwt;

@SuppressWarnings("deprecation")
public class StyleSetStyleCreator {
    public static void create(ActionContext actionContext){
    	Thing StyleManager = (Thing) actionContext.get("StyleManager");
    	Thing self = (Thing) actionContext.get("self");
    	if(StyleManager != null && self != null){
    		StyleManager.put(self.getString("name"), self);
    	}
    }
    
    public static void createResource(ActionContext actionContext){
    	create(actionContext);
    }

	public static void apply(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Object widget = actionContext.get("widget");
        if(widget instanceof Control){
            self.doAction("applyControl", actionContext);
        }else if(widget instanceof CoolItem){
            self.doAction("applyCoolItem", actionContext);
        }else if(widget instanceof CTabItem){
            self.doAction("applyCTabItem", actionContext);
        }else if(widget instanceof ExpandItem){
            self.doAction("applyExpandItem", actionContext);
        }else if(widget instanceof MenuItem){
            self.doAction("applyMenuItem", actionContext);
        }else if(widget instanceof TabItem){
            self.doAction("applyTabItem", actionContext);
        }else if(widget instanceof TableColumn){
            self.doAction("applyTableColumn", actionContext);
        }else if(widget instanceof TableItem){
            self.doAction("applyTableItem", actionContext);
        //}else if(widget instanceof TableTreeItem){
        //    self.doAction("applyTableTreeItem", actionContext);
        }else if(widget instanceof ToolItem){
            self.doAction("applyToolItem", actionContext);
        }else if(widget instanceof TrayItem){
            self.doAction("applyTrayItem", actionContext);
        }else if(widget instanceof TreeColumn){
            self.doAction("applyTreeColumn", actionContext);
        }else if(widget instanceof TreeItem){
            self.doAction("applyTreeItem", actionContext);
        }else if(widget instanceof Item){
            self.doAction("applyItem", actionContext);
        }
    }

    public static void applyControl(ActionContext actionContext){
        Control control = (Control) actionContext.get("widget");
        Thing self = (Thing) actionContext.get("self");
        if(control == null || control.isDisposed()){
        	return;
        }
        
        //图
        //def image = createResource(self.getString("image"), 
        //        "xworker.swt.graphics.Image", "imageFile", actionContext);
        //if(image != null){
        //    control.setImage(image);
        //}
        
        //背景颜色
        Color background = (Color) createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(background != null){
            control.setBackground(background);
        }
        
        //背景图
        Image backgroundImage = (Image) createResource(self.getString("backgroundImage"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(backgroundImage != null){
            control.setBackgroundImage(backgroundImage);
        }
        
        //区域
        Rectangle rectangle = (Rectangle) UtilSwt.createRectangle(self.getString("bounds"));
        if(rectangle != null) control.setBounds(rectangle);
        
        //capture
        String capture = self.getString("capture");
        try {
        	if(capture != null && !"".equals(capture)) control.setCapture(self.getBoolean("capture"));
        }catch(Throwable e) {        	
        	//Eclipse RAP不支持该方法
        }
        
        //图标
        Cursor cursor = (Cursor) createResource(self.getString("cursor"), 
                "xworker.swt.graphics.Cursor", "style", actionContext);
        if(cursor != null){
            control.setCursor(cursor);
        }
        
        //是否激活
        String enabled = self.getString("enabled");
        if(enabled != null && !"".equals(enabled)) control.setEnabled(self.getBoolean("enabled"));
        
        //字体
        Font font = (Font) createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            control.setFont(font);
        }
        
        //字体颜色
        Color foreground = (Color) createResource(self.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            control.setForeground(foreground);
        }
        
        //LayoutData
        Object layoutData = actionContext.get(self.getString("layoutData"));
        if(layoutData != null) control.setLayoutData(layoutData);
        
        //点
        Point point = UtilSwt.createPoint(self.getString("bounds"));
        if(point != null) control.setLocation(point);
        
        //菜单
        Menu menu = (Menu) actionContext.get(self.getString("menu"));
        if(menu instanceof Menu) control.setMenu(menu);
        
        //父
        String sparent = self.getString("parent");
        if(sparent != null && !"".equals(sparent)){
            Object p = (Object) actionContext.get(sparent);
            try{
                if(p instanceof Composite) control.setParent((Composite) p);
            }catch(Exception e){
            }    
        }    
        
        //if(self.redraw != null && self.redraw != "") control.setRedraw(self.getBoolean("redraw"));
        
        //区域
        Object region = actionContext.get(self.getString("region"));
        if(region instanceof Region){
            control.setRegion((Region) region);
        }else if(region == null){
            Rectangle regionRect = UtilSwt.createRectangle(self.getString("region"));
            if(regionRect != null){
            	Region rregion = new Region();
                rregion.add(regionRect);
                control.setRegion(rregion);
            }
        }
        
        //大小
        Point sizePoint = UtilSwt.createPoint(self.getString("size"));
        if(sizePoint != null) control.setSize(sizePoint);
        
        //pack
        if(self.getBoolean("pack")) {
        	control.pack();
        }
        
        //Location
        Point locationPoint = UtilSwt.createPoint(self.getString("location"));
        if(locationPoint != null) control.setLocation(locationPoint);               
        
        //提示语
        String toolTipText = self.getString("toolTipText");
        if(toolTipText != null && !"".equals(toolTipText)){
            control.setToolTipText(UtilString.getString(toolTipText, actionContext));
        }
        
        //是否可见
        String visible = self.getString("visible");
        if(visible != null && !"".equals(visible) && !self.getBoolean("visible")) {
            control.setVisible(false);
        }
    }

    public static void applyCoolItem(ActionContext actionContext){
        CoolItem control = (CoolItem) actionContext.get("widget");
        Thing self = (Thing) actionContext.get("self");
        
        //大小
        Point sizePoint = UtilSwt.createPoint(self.getString("size"));
        if(sizePoint != null) control.setSize(sizePoint);
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
    }

    public static void applyCTabItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	CTabItem control = (CTabItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //字体
        Font font = (Font) createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            control.setFont(font);
        }
    }

    public static void applyExpandItem(ActionContext actionContext){
    	ExpandItem control = (ExpandItem) actionContext.get("widget");
    	Thing self = (Thing) actionContext.get("self");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
    }

    public static void applyMenuItem(ActionContext actionContext){
    	MenuItem control = (MenuItem) actionContext.get("widget");
    	Thing self = (Thing) actionContext.get("self");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //是否激活
        String enabled = self.getString("enabled");
        if(enabled != null && !"".equals(enabled)) control.setEnabled(self.getBoolean("enabled"));
    }

    public static void applyTabItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TabItem control = (TabItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
    }

    public static void applyTableColumn(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TableColumn control = (TableColumn) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
    }

    public static void applyTableItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TableItem control = (TableItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //背景颜色
        Color background = (Color) createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(background != null){
            control.setBackground(background);
        }
        
        //字体
        Font font = (Font) createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            control.setFont(font);
        }
        
        //字体颜色
        Color foreground = (Color) createResource(self.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            control.setForeground(foreground);
        }
    }

    /*
	public static void applyTableTreeItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TableTreeItem control = (TableTreeItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //背景颜色
        Color background = (Color) createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(background != null){
            control.setBackground(background);
        }
        
        //字体
        Font font = (Font) createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            control.setFont(font);
        }
        
        //字体颜色
        Color foreground = (Color) createResource(self.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            control.setForeground(foreground);
        }
    }*/

    public static void applyToolItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	ToolItem control = (ToolItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //是否激活
        String enabled = self.getString("enabled");
        if(enabled != null && !"".equals(enabled)) control.setEnabled(self.getBoolean("enabled"));
        
        //提示语
        String toolTipText= self.getString("toolTipText");
        if(toolTipText != null && !"".equals(toolTipText)){
            control.setToolTipText(UtilString.getString(toolTipText, actionContext));
        }
    }

    public static void applyTrayItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TrayItem control = (TrayItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //提示语
        String toolTipText = self.getString("toolTipText");
        if(toolTipText != null && !"".endsWith(toolTipText)){
            control.setToolTipText(UtilString.getString(toolTipText, actionContext));
        }
        
        //是否可见
        String visible = self.getString("visible");
        if(visible != null && !"".equals(visible) && !self.getBoolean("visible")) {
            control.setVisible(false);
        }
        //log.info(control.getVisible());
    }

    public static void applyTreeColumn(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TreeColumn control = (TreeColumn) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //提示语
        String toolTipText = self.getString("toolTipText");
        if(toolTipText != null && !"".equals(toolTipText)){
            control.setToolTipText(UtilString.getString(toolTipText, actionContext));
        }
    }

    public static void applyTreeItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	TreeItem control = (TreeItem) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
        
        //背景颜色
        Color background = (Color) createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(background != null){
            control.setBackground(background);
        }
        
        //字体
        Font font = (Font) createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            control.setFont(font);
        }
        
        //字体颜色
        Color foreground = (Color) createResource(self.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            control.setForeground(foreground);
        }
    }

    public static void applyItem(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Item control = (Item) actionContext.get("widget");
        
        //图
        Image image = (Image) createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            control.setImage(image);
        }
    }

    public static Object createResource(String value, String descriptor, String attributeName, ActionContext actionContext){
        if(value == null || value.equals("")){
            return null;
        }
        
        Object obj = actionContext.get(value);
        if(obj != null){
        	return obj;
        }
        
        Thing resThing = new Thing(descriptor);
        resThing.set(attributeName, value);
        return resThing.doAction("create", actionContext);      
    }

}