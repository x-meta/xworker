package xworker.swt.custom;

import java.lang.reflect.Method;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.widgets.TextCreator;

public class StyledTextCreator1 {
	private static Method creator = null;
	private static Method createCodeAssistor = null;
	private static Method createColorer = null;
	private static Method createTextEditor = null;

	public static Object create(ActionContext actionContext) {
		if (SwtUtils.isRWT()) {
			// RWT环境不支持StyledTtext
			Thing self = actionContext.getObject("self");
			self.set("style", "MULTI"); // 使用多行
			return TextCreator.create(actionContext);
		} else {
			try {
				if (creator == null) {
					Class<?> cls = Class.forName("xworker.swt.custom.StyledTextCreator");
					creator = cls.getDeclaredMethod("create", ActionContext.class);
				}

				return creator.invoke(null, actionContext);
			} catch (Throwable t) {
				if (t instanceof ActionException) {
					throw (ActionException) t;
				} else {
					throw new ActionException(t);
				}
			}
		}
	}
	
	public static Object createCodeAssistor(ActionContext actionContext) {
		if (SwtUtils.isRWT()) {
			return null;
		} else {
			try {
				if (createCodeAssistor == null) {
					Class<?> cls = Class.forName("xworker.swt.custom.StyledTextCreator");
					createCodeAssistor = cls.getDeclaredMethod("createCodeAssistor", ActionContext.class);
				}

				return createCodeAssistor.invoke(null, actionContext);
			} catch (Throwable t) {
				if (t instanceof ActionException) {
					throw (ActionException) t;
				} else {
					throw new ActionException(t);
				}
			}
		}
	}
	
	public static Object createColorer(ActionContext actionContext) {
		if (SwtUtils.isRWT()) {
			return null;
		} else {
			try {
				if (createColorer == null) {
					Class<?> cls = Class.forName("xworker.swt.custom.StyledTextCreator");
					createColorer = cls.getDeclaredMethod("createColorer", ActionContext.class);
				}

				return createColorer.invoke(null, actionContext);
			} catch (Throwable t) {
				if (t instanceof ActionException) {
					throw (ActionException) t;
				} else {
					throw new ActionException(t);
				}
			}
		}
	}
	
	public static Object createTextEditor(ActionContext actionContext) {
		if (SwtUtils.isRWT()) {
			return null;
		} else {
			try {
				if (createTextEditor == null) {
					Class<?> cls = Class.forName("xworker.swt.custom.StyledTextCreator");
					createTextEditor = cls.getDeclaredMethod("createTextEditor", ActionContext.class);
				}

				return createTextEditor.invoke(null, actionContext);
			} catch (Throwable t) {
				if (t instanceof ActionException) {
					throw (ActionException) t;
				} else {
					throw new ActionException(t);
				}
			}
		}
	}
}
