package xworker.lang.executor;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.xmeta.ActionContext;

import xworker.swt.util.DelayExecutor;

public class StyledTextLogViewer extends DelayExecutor implements LogViewer{
	boolean autoScroll = true;
	
	StyledText styledText;
	StringBuilder logStringBuilder = new StringBuilder();
	
	public StyledTextLogViewer(StyledText text) {
		super(text.getDisplay(), 200);
		
		this.styledText = text;
	}
	
	@Override
	public void log(byte level, String msg) {
		logStringBuilder.append(msg);
		logStringBuilder.append("\n");
		if(logStringBuilder.length() > 1024 * 1024) {
			//超过一定长度，删除
			logStringBuilder.delete(0, 1024 * 20);
		}
		
		this.execute();
		/*
		 styledText.append(msg);
		 styledText.append("\n");
         if(styledText.getLineCount() > 3000){
             //输出显示最多3000行
             int offset = styledText.getOffsetAtLine(500);
             styledText.replaceTextRange(0, offset, "");
         }
         
         showSelection();
         */
	}
	
	public void showSelection() {
		if(autoScroll) {
            int offset = styledText.getText().length();
            styledText.setCaretOffset(offset);
            styledText.setSelection(offset, offset);
            styledText.showSelection();
        }
	}

	@Override
	public boolean isAutoScroll() {
		return autoScroll;
	}

	@Override
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

	public static StyledTextLogViewer create(ActionContext actionContext) {
		StyledText text = actionContext.getObject("logText");
		return new StyledTextLogViewer(text);
	}

	@Override
	public void print(Object message) {
		styledText.append(String.valueOf(message));
		showSelection();
	}

	@Override
	public void println(Object message) {
		styledText.append(String.valueOf(message));
		styledText.append("\n");
		showSelection();
	}

	@Override
	public void errPrint(Object message) {
		styledText.append(String.valueOf(message));
		showSelection();
	}

	@Override
	public void errPrintln(Object message) {
		styledText.append(String.valueOf(message));
		styledText.append("\n");
		showSelection();
	}

	@Override
	public Display getDisplay() {		
		if(styledText == null || styledText.isDisposed()) {
			return null;
		}
		
		return styledText.getDisplay();
	}

	@Override
	public void doTask() {
		styledText.setText(logStringBuilder.toString());
		showSelection();
	}

}
