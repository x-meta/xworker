package xworker.lang.executor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

public class TextLogViewer implements LogViewer{
	Text text;
	boolean autoScroll = true;
	
	public TextLogViewer(Text text) {
		this.text = text;
	}
	
	public boolean isAutoScroll() {
		return autoScroll;
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

	@Override
	public void log(final byte level, final String msg) {
		if(text != null && !text.isDisposed()) {
			text.getDisplay().asyncExec(new Runnable(){
				public void run() {
					if(text.isDisposed() == false) {
						try {							
							text.append(msg);
							text.append("\n");
							
							if(text.getCharCount() > 10000){
								text.setSelection(0,1000);
								text.clearSelection();
		                     }
							
							showSelection();
						}catch(Exception e) {							
						}
					}
				}
			});
		}
	}
	
	public void showSelection() {
		if(TextLogViewer.this.autoScroll) {
			//滚动到末尾
			text.setSelection(text.getCharCount(), text.getCharCount());
			text.showSelection();
		}
	}
	
	public static TextLogViewer create(ActionContext actionContext) {
		Text text = actionContext.getObject("logText");
		return new TextLogViewer(text);
	}
	
	@Override
	public void print(Object message) {
		text.append(String.valueOf(message));
		showSelection();
	}

	@Override
	public void println(Object message) {
		text.append(String.valueOf(message));
		text.append("\n");
		showSelection();
	}

	@Override
	public void errPrint(Object message) {
		text.append(String.valueOf(message));
		showSelection();
	}

	@Override
	public void errPrintln(Object message) {
		text.append(String.valueOf(message));
		text.append("\n");
		showSelection();
	}

	@Override
	public Display getDisplay() {
		return text.getDisplay();
	}
}
