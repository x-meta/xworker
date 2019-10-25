package xworker.swt.xwidgets;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

import xworker.io.SystemIoRedirector;
import xworker.io.SystemIoRedirectorListener;
import xworker.swt.functions.AutoScroll;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;

public class SystemConsoleRWT implements SystemIoRedirectorListener, DisposeListener, AutoScroll{
	Text textText;
	boolean autoScroll = true;

	public SystemConsoleRWT(Object parent) {
		textText = (Text) parent;
		textText.addDisposeListener(this);
		
		SystemIoRedirector.addListener(this);
		
		SwtUtils.regist(textText, this, AutoScroll.class);
	}
	
	public Display getDisplay() {
		return textText.getDisplay();
	}

	@Override
	public void readLine(String line) {		
	}
	
	public boolean isAutoScroll() {
		return autoScroll;
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
		
		if(this.autoScroll) {
			SwtTextUtils.scrollToBottom(textText);
			//textText.setSelection(textText.getCharCount());
       	 	//textText.showSelection();
		}
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
		                    	 SwtTextUtils.scrollToBottom(textText);
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
		new SystemConsoleRWT(actionContext.getObject("parent"));
	}

}
