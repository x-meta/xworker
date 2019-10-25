package xworker.swt.nebula;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.swt.design.Designer;
import xworker.swt.model.Model;
import xworker.swt.model.ModelManager;
import xworker.swt.util.SwtUtils;

public class CDateTimeActions {
	static {
		ModelManager.regist(CDateTime.class, new Model() {

			@Override
			public void setValue(Object control, Object value, String viewPattern, String editPattern) {
				CDateTime dateTime = (CDateTime) control;
				if(value == null) {
					return;
				}
				
				Date date = UtilData.getDate(value, new Date(), editPattern);
				if(date != null) {
					dateTime.setSelection(date);
				}
			}

			@Override
			public Object getValue(Object control, String type, String pattern) {
				CDateTime dateTime = (CDateTime) control;
				
				return dateTime.getSelection();
			}
		});
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		style = style | getStyle(self, "DROP_DOWN");
		style = style | getStyle(self, "SIMPLE");
		style = style | getStyle(self, "SPINNER");
		style = style | getStyle(self, "TAB_FIELDS");
		style = style | getStyle(self, "COMPACT");
		style = style | getStyle(self, "HORIZONTAL");
		
		style = style | getStyle(self, "DATE", "DATE_");
		style = style | getStyle(self, "TIME", "TIME_");
		style = style | getStyle(self, "CLOCK", "CLOCK_");
		style = style | getStyle(self, "BUTTON", "BUTTON_");
		style = style | getStyle(self, "BUTTON_ALIGN", "BUTTON_");
	
		Composite parent = actionContext.getObject("parent");
		CDateTime dateTime = new CDateTime(parent, style);
		
		dateTime.setEditable(self.getBoolean("editable"));
		String nullText = self.getStringBlankAsNull("nullText​");
		if(nullText != null) {
			dateTime.setNullText(nullText);
		}
		String pattern = self.getStringBlankAsNull("pattern");
		if(pattern != null) {
			dateTime.setPattern(pattern);
		}
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", dateTime);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", dateTime);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), dateTime);
		Designer.attach(dateTime, self.getMetadata().getPath(), actionContext);
		return dateTime;
	}
	
	public static int getStyle(Thing self,  String styleName) {
		if(self.getBoolean(styleName)) {
			if("DROP_DOWN".equals(styleName)) {
				return CDT.DROP_DOWN;
			}else if("SIMPLE".equals(styleName)) {
				return CDT.SIMPLE;
			}else if("SPINNER".equals(styleName)) {
				return CDT.SPINNER;
			}else if("TAB_FIELDS".equals(styleName)) {
				return CDT.TAB_FIELDS;
			}else if("COMPACT".equals(styleName)) {
				return CDT.COMPACT;
			}else if("HORIZONTAL".equals(styleName)) {
				return CDT.HORIZONTAL;
			}
		}
		
		return SWT.NONE;
	}
	
	public static int getStyle(Thing self, String name, String prefix) {
		String str = self.getStringBlankAsNull(name);
		if(str != null) {
			String styleName = prefix + str;
			if("DATE_SHORT".equals(styleName)) {
				return CDT.DATE_SHORT;
			}else if("DATE_MEDIUM".equals(styleName)) {
				return CDT.DATE_MEDIUM;
			}else if("DATE_LONG".equals(styleName)) {
				return CDT.DATE_LONG;
			}else if("TIME_SHORT".equals(styleName)) {
				return CDT.DROP_DOWN;
			}else if("TIME_MEDIUM".equals(styleName)) {
				return CDT.DROP_DOWN;
			}else if("CLOCK_12_HOUR".equals(styleName)) {
				return CDT.CLOCK_12_HOUR;
			}else if("CLOCK_24_HOUR".equals(styleName)) {
				return CDT.CLOCK_24_HOUR;
			}else if("CLOCK_DISCRETE".equals(styleName)) {
				return CDT.CLOCK_DISCRETE;
			}else if("BUTTON_RIGHT".equals(styleName)) {
				return CDT.BUTTON_LEFT;
			}else if("BUTTON_AUTO".equals(styleName)) {
				return CDT.BUTTON_AUTO;
			}else if("BUTTON_RIGHT".equals(styleName)) {
				return CDT.BUTTON_RIGHT;
			}
		}
		
		return SWT.NONE;
	}
}
