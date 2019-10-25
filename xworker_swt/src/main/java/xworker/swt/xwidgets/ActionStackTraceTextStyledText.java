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
import org.xmeta.World;

import xworker.swt.util.SwtTextUtils;
import xworker.util.XWorkerUtils;

public class ActionStackTraceTextStyledText {
	public static void setStackTrace(ActionContext actionContext){
		//Control text = (Control) actionContext.get("text");
		StyledText text = (StyledText) actionContext.get("text");
		//text.setText("");
		SwtTextUtils.setText(text, "");
		
		String stackTrace = (String) actionContext.get("stackTrace");
		if(stackTrace == null){
			return;
		}
		
		for(String line : stackTrace.split("[\n]")){
			String l = line.trim();
			if(l.startsWith("caller:")){
				String ls[] = l.split("[,]");
				String ls1[] = ls[0].split("[:]");
				if(ls1.length == 3){
					SwtTextUtils.append(text, "\t");
					SwtTextUtils.append(text, ls1[0] + ":");
					SwtTextUtils.append(text, ls1[1] + ":");
					Thing thing = World.getInstance().getThing(ls1[2].trim());
					if(thing != null){
						int start = SwtTextUtils.getText(text).length();			
						String link = thing.getMetadata().getPath();
						SwtTextUtils.append(text, link);
						
						Color colorBlue = text.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE );
			            StyleRange range = new StyleRange(start, link.length(), colorBlue, null);
			            range.underline = true;
			            range.data = thing;
			            text.setStyleRange(range);
					}else{
						SwtTextUtils.append(text, ls1[2]);
					}
					
					for(int i=1;i<ls.length; i++){
						SwtTextUtils.append(text, "," + ls[i]);
					}
					SwtTextUtils.append(text, "\n");
				}else{
					SwtTextUtils.append(text, line + "\n");
				}
			}else{
				SwtTextUtils.append(text, line + "\n");
			}
		}
	}
	
	public static void mouseMove(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		Point p = new Point(event.x, event.y);
		StyledText consoleText = (StyledText) event.widget;
		try{
		    int offset = consoleText.getOffsetAtLocation(p);
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
		        	Thing thing = (Thing) range.data;
		        	if(thing != null){
		        		XWorkerUtils.ideOpenThing(thing);	            
		            }
		        }
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		}
	}
}
