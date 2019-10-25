package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilAction;
import xworker.util.XWorkerUtils;

public class ThrowableStackTraceStyledText {
	public static void setThrowable(ActionContext actionContext){
		StyledText text = (StyledText) actionContext.get("text");
		Throwable throwable = (Throwable) actionContext.get("throwable");
		SwtTextUtils.setText(text, "");
		
		printStackTrace(throwable, text);
	}
	
	public static void printStackTrace(Throwable t, Object text){
		printStackTrace(t, (StyledText) text);
	}
	
	public static void printStackTrace(Throwable t, StyledText text){
		SwtTextUtils.append(text, t.toString());
		SwtTextUtils.append(text, "\n");
		for(StackTraceElement st : t.getStackTrace()){			
			SwtTextUtils.append(text, "\tat ");
			int start = SwtTextUtils.getText(text).length();
			String line = st.getClassName() + "." + st.getMethodName() + "(";
						
			line = line + st.getFileName() + ":" + st.getLineNumber();
			SwtTextUtils.append(text, line);
			
			String className = st.getClassName();
			className = className.split("[\\$]")[0];
            Thing scriptObject = UtilAction.getActionThing(className);
            if(scriptObject != null && !SwtUtils.isRWT()){
				Color colorBlue = text.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE );
	            StyleRange range = new StyleRange(start, line.length(), colorBlue, null);
	            range.underline = true;
	            range.data = st;
	            text.setStyleRange(range);
            }
            
            SwtTextUtils.append(text, ")\n");
		}

		Throwable cause = t.getCause();
		if(cause != null){
			printStackTrace(cause, text);
		}
	}
	
	public static void mouseMove(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		Point p = new Point(event.x, event.y);
		StyledText consoleText = (StyledText) event.widget;
		try{
		    int offset =  SwtTextUtils.getOffsetAtLocation(consoleText, p);
		    StyleRange range = consoleText.getStyleRangeAtOffset(offset);

		    if(range != null && range.underline == true){
		        //是类    
		        Cursor cursor = consoleText.getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		        Cursor oldCursor = consoleText.getCursor();
		        if(oldCursor != cursor){
		            consoleText.setData("oldCursor", oldCursor);
		        }
		        consoleText.setCursor(cursor);
		    }else{
		        Cursor cur = (Cursor) consoleText.getData("oldCursor");
		        if(cur != null){
		            consoleText.setCursor(cur);
		            consoleText.setData("oldCursor", null);
		        }
		    }
		}catch(Exception e){
			Cursor cur = (Cursor) consoleText.getData("oldCursor");
		    if(cur != null){
		        consoleText.setCursor(cur);
		        consoleText.setData("oldCursor", null);
		    }
		}
	}
	
	public static void mouseDown(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		StyledText consoleText = (StyledText) event.widget;
		
		if(event.button == 1){
		    Point p = new Point(event.x, event.y);
		    try{
		        int offset = -1;
		        StyleRange range = null;
		        try{
		            offset = consoleText.getOffsetAtLocation(p);
		            range = consoleText.getStyleRangeAtOffset(offset);
		        }catch(Exception e){
		        }
		  
		        if(range != null && range.underline == true){
		        	StackTraceElement st = (StackTraceElement) range.data;
		            String className = st.getClassName();
		            //println className;
		            className = className.split("[\\$]")[0];
		            Thing scriptObject = UtilAction.getActionThing(className);
		            if(scriptObject != null){
		            	int line = st.getLineNumber();
		            	XWorkerUtils.ideOpenThingAndSelectCodeLine(scriptObject, "code", line - 4);
		            }else{
		            	/*
		                //查看是否是在源文件目录下的对象
		                String fileName = className.replace('.', '/');
		                fileName = "src/xworker/" + fileName + ".java";
		                File f = new File(fileName);
		                //log.info(f.getAbsolutePath());
		                if(!f.exists()){
		                     fileName = "src/core/" + fileName + ".java";  
		                     f = new File(fileName);
		                }
		                
		                if(f.exists()){
		                     actions.doAction("openTextFile", ["file":f]);
		                }else{
		                     
		                }*/
		            }
		        }
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		}
	}
}
