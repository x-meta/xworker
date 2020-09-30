package xworker.swt.custom;

import java.lang.reflect.Method;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

/**
 * 为解决RWT模式下报错。
 * 
 * @author zyx
 *
 */
public class StyledTextProxy {
	//private static Logger logger = LoggerFactory.getLogger(StyledTextProxy.class);
	
	private static final String NAME = "org.eclipse.swt.custom.StyledText";
	private static Class<?> styledTextCls = null;
	private static Method addModifyListener = null;
	private static Method setText = null;
	private static Method getText = null;
	private static Method getTextSN = null;
	private static Method isFocusControl = null;
	private static Method getOffsetAtLine = null;
	private static Method getLineCount = null;
	private static Method getWordWrap = null;
	private static Method setSelection = null;
	private static Method clearSelection = null;
	private static Method showSelection = null;
	private static Method getSelection = null;
	private static Method getLineAtOffset = null;
	private static Method append = null;
	private static Method getOffsetAtLocation = null;
	private static Method getCaret = null;
	private static Method setCaretOffset = null;
	private static Method getCaretOffset = null;
	private static Method getTopIndex = null;
	private static Method setTopIndex = null;
	private static Method insert = null;
	private static Method addListener = null;
	
	private static Class<?> codeAssistorCls = null;
	private static Method codeAssistorAttachStyledText= null;
	private static Method codeAssistorAttachText= null;
	
	public static boolean isStyledText(Object obj) {
		return obj.getClass().getName().equals(NAME);
	}
	
	private static Method getMethod(String name, Class<?> ... parameterTypes) {
		try {
			if(styledTextCls == null) {
				styledTextCls = Class.forName(NAME);
			}
			
			return styledTextCls.getMethod(name, parameterTypes);
		}catch(Throwable t) {
			if(t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}else {
				throw new ActionException("Get StyledText method" + name + " error", t);
			}			
		}
	}	
	
	public static boolean getWordWrap(Object obj) {
		if(getWordWrap == null) {
			getWordWrap = getMethod("getWordWrap");
		}
		if(getWordWrap != null) {
			try {
				return (Boolean) getWordWrap.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getWordWrap error", t);
				}	
			}
		}
		
		return false;
	}
	
	public static Point getSelection(Object obj) {
		if(getSelection == null) {
			getSelection = getMethod("getSelection");
		}
		if(getSelection != null) {
			try {
				return (Point) getSelection.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getSelection error", t);
				}	
			}
		}
		
		return null;
	}
	
	public static void showSelection(Object obj) {
		if(showSelection == null) {
			showSelection = getMethod("showSelection");
		}
		if(showSelection != null) {
			try {
				showSelection.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method showSelection error", t);
				}				
			}
		}		
	}
	
	public static void setCaretOffset(Object obj, int offset) {
		if(setCaretOffset == null) {
			setCaretOffset = getMethod("setCaretOffset", int.class);
		}
		if(setCaretOffset != null) {
			try {
				setCaretOffset.invoke(obj, offset);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method setCaretOffset error", t);
				}				
			}
		}		
	}
	
	public static Point getCaretLocation(Object obj) {
		if(getCaret == null) {
			getCaret = getMethod("getCaret");
		}
		if(getCaret != null) {
			try {
				Object caret = getCaret.invoke(obj);
				Method getLocation = caret.getClass().getMethod("getLocation", new Class<?>[]{});
				return (Point) getLocation.invoke(caret);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getCaretLocation error", t);
				}				
			}
		}else {
			return null;
		}
	}
	
	public static int getCaretOffset(Object obj) {
		if(getCaretOffset == null) {
			getCaretOffset = getMethod("getCaretOffset");
		}
		if(getCaretOffset != null) {
			try {
				return (Integer) getCaretOffset.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getCaretOffset error", t);
				}				
			}
		}else {
			return 0;
		}
	}
	
		
	public static int getOffsetAtLocation(Object obj, Point point) {
		if(getOffsetAtLocation == null) {
			getOffsetAtLocation = getMethod("getOffsetAtLocation", Point.class);
		}
		if(getOffsetAtLocation != null) {
			try {
				getOffsetAtLocation.invoke(obj, point);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getOffsetAtLocation error", t);
				}					
			}
		}
		
		return -1;
	}
	
	public static void setSelection(Object obj, int start, int end) {
		if(setSelection == null) {
			setSelection = getMethod("setSelection", int.class, int.class);
		}
		if(setSelection != null) {
			try {
				setSelection.invoke(obj, start, end);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method setSelection error", t);
				}
			}
		}		
	}
	
	public static void clearSelection(Object obj) {
		if(clearSelection == null) {
			clearSelection = getMethod("clearSelection", int.class, int.class);
		}
		if(clearSelection != null) {
			try {
				clearSelection.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method clearSelection error", t);
				}
			}
		}		
	}
	
	public static int getLineCount(Object obj) {
		if(getLineCount == null) {
			getLineCount = getMethod("getLineCount");
		}
		if(getLineCount != null) {
			try {
				return (Integer) getLineCount.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getLineCount error", t);
				}
			}
		}
		
		return -1;
	}
	
	public static int getTopIndex(Object obj) {
		if(getTopIndex == null) {
			getTopIndex = getMethod("getTopIndex");
		}
		if(getTopIndex != null) {
			try {
				return (Integer) getTopIndex.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getTopIndex error", t);
				}
			}
		}
		
		return 0;
	}
	
	public static void setTopIndex(Object obj, int index) {
		if(setTopIndex == null) {
			setTopIndex = getMethod("setTopIndex", int.class);
		}
		if(setTopIndex != null) {
			try {
				setTopIndex.invoke(obj, index);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method setTopIndex error", t);
				}
			}
		}
	}
	
	public static void addModifyListener(Object obj, ModifyListener listener) {
		if(addModifyListener == null) {
			addModifyListener = getMethod("addModifyListener", ModifyListener.class);
		}
		if(addModifyListener != null) {
			try {
				addModifyListener.invoke(obj, listener);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method addModifyListener error", t);
				}
			}
		}
	}
		
	public static int getLineAtOffset(Object obj, int offset) {
		if(getLineAtOffset == null) {
			getLineAtOffset = getMethod("getLineAtOffset", int.class);
		}
		if(getLineAtOffset != null) {
			try {
				return (Integer) getLineAtOffset.invoke(obj, offset);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getLineAtOffset error", t);
				}
			}
		}
		return -1;
	}
	
	public static void addListener(Object obj, int type, Listener listener) {
		if(addListener == null) {
			addListener = getMethod("addListener", int.class, Listener.class);
		}
		if(addListener != null) {
			try {
			 addListener.invoke(obj, type, listener);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getLineAtOffset error", t);
				}
			}
		}
	}
	
	
	public static int getOffsetAtLine(Object obj, int lineIndex) {
		if(getOffsetAtLine == null) {
			getOffsetAtLine = getMethod("getOffsetAtLine", int.class);
		}
		if(getOffsetAtLine != null) {
			try {
				return (Integer) getOffsetAtLine.invoke(obj, lineIndex);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getOffsetAtLine error", t);
				}
			}
		}
		return -1;
	}
	
	public static void setText(Object obj, String text) {
		if(setText == null) {
			setText = getMethod("setText", String.class);
		}
		if(setText != null) {
			try {
				setText.invoke(obj, text);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method setText error", t);
				}
			}
		}
	}
	
	public static void insert(Object obj, String text) {
		if(insert == null) {
			insert = getMethod("insert", String.class);
		}
		if(insert != null) {
			try {
				insert.invoke(obj, text);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method insert error", t);
				}
			}
		}
	}
	
	public static void append(Object obj, String text) {
		if(append == null) {
			append = getMethod("append", String.class);
		}
		if(append != null) {
			try {
				append.invoke(obj, text);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method append error", t);
				}
			}
		}
	}
	
	public static String getText(Object obj) {
		if(getText == null) {
			getText = getMethod("getText");
		}
		if(getText != null) {
			try {
				return (String) getText.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method setText error", t);
				}
			}
		}
		
		return null;
	}
	
	public static String getText(Object obj, int start ,int end) {
		if(getTextSN == null) {
			getTextSN = getMethod("getText", int.class, int.class);
		}
		if(getTextSN != null) {
			try {
				return (String) getTextSN.invoke(obj, new Object[] {start, end});
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method getText error", t);
				}
			}
		}
		
		return null;
	}
	
	public static boolean isFocusControl(Object obj) {
		if(isFocusControl == null) {
			isFocusControl = getMethod("isFocusControl");
		}
		if(isFocusControl != null) {
			try {
				return (Boolean) isFocusControl.invoke(obj);
			} catch (Throwable t) {
				if(t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}else {
					throw new ActionException("Invoke StyledText method isFocusControl error", t);
				}
			}
		}
		
		return false;
	}
	
	public static boolean isDisposed(Object obj) {
		Widget widget = (Widget) obj;
		return widget.isDisposed();
	}
	
	private static Method getCodeAssistorMethod(String name, Class<?> ... parameterTypes) {
		try {
			if(codeAssistorCls == null) {
				codeAssistorCls = Class.forName("xworker.swt.xworker.CodeAssistor");
			}
			
			return codeAssistorCls.getMethod(name, parameterTypes);
		}catch(Throwable t) {
			if(t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}else {
				throw new ActionException("Get StyledText method xworker.swt.xworker.CodeAssistor error", t);
			}			
		}
	}
	
	public static void initCodeAssistor(Object obj, Thing thing, ActionContext actionContext) {
		try {
			if(isStyledText(obj)) {
				if(codeAssistorAttachStyledText == null) {
					codeAssistorAttachStyledText = getCodeAssistorMethod("attach", Thing.class, styledTextCls, ActionContext.class);					
				}
				codeAssistorAttachStyledText.invoke(obj, thing, obj, actionContext);
			}else if(obj instanceof Text) {				
				if(codeAssistorAttachText == null) {
					codeAssistorAttachText = getCodeAssistorMethod("attach", Thing.class, Text.class, ActionContext.class);					
				}
				codeAssistorAttachText.invoke(obj, thing, obj, actionContext);
			}			
		}catch(Throwable t) {
			if(t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}else {
				throw new ActionException("Get CodeAssistor method attach error", t);
			}	
		}
	}
}


