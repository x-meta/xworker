package xworker.dataObject.swt.bind;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.swt.bind.control.BackgroundImage;
import xworker.dataObject.swt.bind.control.BackgroundItem;
import xworker.dataObject.swt.bind.control.CursorItem;
import xworker.dataObject.swt.bind.control.EnabledItem;
import xworker.dataObject.swt.bind.control.FontItem;
import xworker.dataObject.swt.bind.control.ForegroundItem;
import xworker.dataObject.swt.bind.control.ToolTipTextItem;
import xworker.dataObject.swt.bind.control.TouchEnabledItem;
import xworker.dataObject.swt.bind.control.VisibleItem;
import xworker.dataObject.swt.bind.reflect.AlignmentItem;
import xworker.dataObject.swt.bind.reflect.AppendItem;
import xworker.dataObject.swt.bind.reflect.BackgroundModeItem;
import xworker.dataObject.swt.bind.reflect.DoubleClickEnabledItem;
import xworker.dataObject.swt.bind.reflect.EditableItem;
import xworker.dataObject.swt.bind.reflect.GrayedItem;
import xworker.dataObject.swt.bind.reflect.ImageItem;
import xworker.dataObject.swt.bind.reflect.IncrementItem;
import xworker.dataObject.swt.bind.reflect.MaximumItem;
import xworker.dataObject.swt.bind.reflect.MinimumItem;
import xworker.dataObject.swt.bind.reflect.OrientationItem;
import xworker.dataObject.swt.bind.reflect.PageIncrementItem;
import xworker.dataObject.swt.bind.reflect.SelectionBooleanItem;
import xworker.dataObject.swt.bind.reflect.SelectionItem;
import xworker.dataObject.swt.bind.reflect.StateItem;
import xworker.dataObject.swt.bind.reflect.TextItem;
import xworker.dataObject.swt.bind.reflect.ThumbItem;
import xworker.dataObject.swt.bind.reflect.TopIndexItem;
import xworker.lang.executor.Executor;

public class BindItemFactory {
	private static final String TAG = "BindItemFactory";
	private static Map<String, ItemInfo> items = new HashMap<String, ItemInfo>();
	
	static {
		regist("backgroundImage", "xworker.dataObject.swt.bind.control.BackgroundImage", BackgroundImage.class);
		regist("background", "xworker.dataObject.swt.bind.control.Background", BackgroundItem.class);
		regist("cursor", "xworker.dataObject.swt.bind.control.Cursor", CursorItem.class);
		regist("enabled", "xworker.dataObject.swt.bind.control.Enabled", EnabledItem.class);
		regist("font", "xworker.dataObject.swt.bind.control.Font", FontItem.class);
		regist("foreground", "xworker.dataObject.swt.bind.control.Foreground", ForegroundItem.class);
		regist("toolTip", "xworker.dataObject.swt.bind.control.ToolTipText", ToolTipTextItem.class);
		regist("touchEnabled", "xworker.dataObject.swt.bind.control.TouchEnabled", TouchEnabledItem.class);
		regist("visible", "xworker.dataObject.swt.bind.control.Visible", VisibleItem.class);
		regist("alignment", "xworker.dataObject.swt.bind.reflect.Alignment", AlignmentItem.class);
		regist("append", "xworker.dataObject.swt.bind.reflect.Append", AppendItem.class);
		regist("backgroundMode", "xworker.dataObject.swt.bind.reflect.BackgroundMode", BackgroundModeItem.class);
		regist("doubleClickEnabled", "xworker.dataObject.swt.bind.reflect.DoubleClickEnabled", DoubleClickEnabledItem.class);
		regist("editable", "xworker.dataObject.swt.bind.reflect.Editable", EditableItem.class);
		regist("grayed", "xworker.dataObject.swt.bind.reflect.Grayed", GrayedItem.class);
		regist("image", "xworker.dataObject.swt.bind.reflect.Image", ImageItem.class);
		regist("increment", "xworker.dataObject.swt.bind.reflect.Increment", IncrementItem.class);
		regist("maximum", "xworker.dataObject.swt.bind.reflect.Maximum", MaximumItem.class);
		regist("minimum", "xworker.dataObject.swt.bind.reflect.Minimum", MinimumItem.class);
		regist("orientation", "xworker.dataObject.swt.bind.reflect.Orientation", OrientationItem.class);
		regist("pareIncrement", "xworker.dataObject.swt.bind.reflect.PageIncrement", PageIncrementItem.class);
		regist("selection", "xworker.dataObject.swt.bind.reflect.Selection", SelectionItem.class);
		regist("selectionBoolean", "xworker.dataObject.swt.bind.reflect.SelectionBoolean", SelectionBooleanItem.class);
		regist("state", "xworker.dataObject.swt.bind.reflect.State", StateItem.class);
		regist("text", "xworker.dataObject.swt.bind.reflect.Text", TextItem.class);
		regist("thumb", "xworker.dataObject.swt.bind.reflect.Thumb", ThumbItem.class);
		regist("topIndex", "xworker.dataObject.swt.bind.reflect.TopIndex", TopIndexItem.class);
	}
	
	protected static Map<String, ItemInfo> getItemInfos(){
		return items;
	}
	
	/**
	 * 注册一个BindItem，可以通过名字和参数来创建它。
	 * 
	 * @param name
	 * @param thingPath
	 * @param className
	 */
	public static void regist(String name, String thingPath, Class<?> className) {
		items.put(name, new ItemInfo(thingPath, className));
	}
	
	/**
	 * 根据名字和参数创建一个绑定到Widget上的绑定条目。
	 * 
	 * @param name
	 * @param params
	 * @param widget
	 * @param actionContext
	 * @return
	 */
	public static BinderItem create(String name, Map<String, String> params, Widget widget, ActionContext actionContext) {
		ItemInfo itemInfo = items.get(name);
		if(itemInfo != null) {
			return itemInfo.create(params, widget, actionContext);
		}else {
			Executor.warn(TAG, "BindItem type=" + name + " not registed!");
			return null;
		}
	}
		
	static class ItemInfo{
		String thingPath;
		Class<?> className;
		
		public ItemInfo(String thingPath, Class<?> className) {
			this.thingPath = thingPath;
			this.className = className;
		}
		
		public BinderItem create(Map<String, String> params, Widget widget, ActionContext actionContext) {
			Thing thing = new Thing(thingPath);
			if(params != null) {
				for(String key : params.keySet()) {
					thing.put(key, params.get(key));
				}
			}
			
			if(widget != null) {
				try {
					Constructor<?> cons = className.getConstructor(new Class[] {Thing.class, Widget.class, ActionContext.class});
					if(cons != null) {
						return (BinderItem) cons.newInstance(thing, widget, actionContext);
					}
				}catch(Exception e) {
					Executor.info(TAG, "Create BinderItem exception, class=" + className.getName() + ", " + e.getMessage());
				}
			}
			
			Executor.warn(TAG, "Can not create BinderItem, class=" + className.getName());
			return null;
		}
	}
}
