package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

import xworker.io.SystemIoRedirector;
import xworker.io.SystemIoRedirectorListener;
import xworker.swt.functions.AutoScroll;
import xworker.swt.util.SwtUtils;

public class SystemConsole implements SystemIoRedirectorListener, DisposeListener, AutoScroll{
	//RWT模式下没有StyledText，是使用Text代替的
	StyledText styledText;
	Text textText;
	boolean autoScroll = true;

	public SystemConsole(Object parent) {
		if(parent instanceof StyledText) {
			styledText = (StyledText) parent;
			styledText.addDisposeListener(this);
		}else {
			textText = (Text) parent;
			textText.addDisposeListener(this);
		}
				
		SystemIoRedirector.addListener(this);
		SwtUtils.regist(styledText, this, AutoScroll.class);
	}
	
	public boolean isAutoScroll() {
		return autoScroll;
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
		
		if(autoScroll) {
            int offset = styledText.getText().length();
            styledText.setCaretOffset(offset);
            styledText.setSelection(offset, offset);
            styledText.showSelection();
        }
	}

	public Display getDisplay() {
		if(styledText != null) {
			return styledText.getDisplay();
		}else {
			return textText.getDisplay();
		}
	}
	
	public void setStyled(int lineIndex) {
		String line = styledText.getLine(lineIndex);
		int lineOffset = styledText.getOffsetAtLine(lineIndex);
		if(line.startsWith("\tat ")){         
            //判断是Exception的情况            
            String clss = line.substring(4, line.indexOf("("));
            clss = clss.substring(0, clss.lastIndexOf("."));
                      
            //println clss;     
            //为提高速度，始终显示                           
            //def scriptObj = UtilAction.getActionThing(clss);
            //if(scriptObj != null){
            if(true){
                //println clss;
                try{
                    int start = 4;
                    int length = clss.length();
                    Color colorBlue = getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE );
                    StyleRange range = new StyleRange(lineOffset + start, length, colorBlue, null);
                    range.underline = true;
                
                    styledText.setStyleRange(range);
                }catch(Exception eeeee){
                }
            }
            
            return;
        }
        
        int leftIndex = line.indexOf("(");
        int rightIndex = line.indexOf(")");
        int commerIndex = line.indexOf(":", leftIndex);
        if(leftIndex != -1 && rightIndex != -1 && (commerIndex > leftIndex && commerIndex < rightIndex)){
            //是Log4j的输出格式
            String clss = line.substring(leftIndex + 1, commerIndex); 
            //println clss;
            //def scriptObj =  UtilAction.getActionThing(clss);
            //if(scriptObj != null){
            if(true){
                //println clss;
                try{
                    int start = leftIndex + 1;
                    int length = clss.length();
                    Color colorBlue = getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE );
                    StyleRange range = new StyleRange(lineOffset + start, length, colorBlue, null);
                    range.underline = true;
                    styledText.setStyleRange(range);
                }catch(Exception eeeee){
                }
            }
        }       
	}
	
	@Override
	public void readLine(String line) {		
	}
	
	@Override
	public void read(final String text) {
		try {
			Display display = getDisplay();
			if(display == null || display.isDisposed()) {
				return;
			}
			
			display.asyncExec(new Runnable() {
				public void run() {				 
					try {
		                 if(SwtUtils.isRWT()){
		                	 textText.append(text);
		                     if(textText.getCharCount() > 10000){
		                    	 textText.setSelection(0,1000);
		                    	 textText.clearSelection();
		                     }
		                     
		                     if(autoScroll) {
		                    	 textText.setSelection(textText.getCharCount());
		                    	 textText.showSelection();
		                     }
		                 }else{
		                	 styledText.append(text);
		                     if(styledText.getLineCount() > 3000){
		                         //输出显示最多3000行
		                         int offset = styledText.getOffsetAtLine(500);
		                         styledText.replaceTextRange(0, offset, "");
		                     }
		                     
		                     if(autoScroll) {
			                     int offset = styledText.getText().length();
			                     styledText.setCaretOffset(offset);
			                     styledText.setSelection(offset, offset);
			                     styledText.showSelection();
		                     }
		                 }
					}catch(Exception e) {
						
					}
				}
			});
		}catch(Exception e) {
			
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		SystemIoRedirector.removeListener(this);
	}

	public static void create(ActionContext actionContext) {
		new SystemConsole(actionContext.getObject("parent"));
	}
}
