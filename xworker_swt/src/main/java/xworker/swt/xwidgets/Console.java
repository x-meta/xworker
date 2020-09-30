package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ExceptionUtil;

import xworker.swt.util.SwtTextUtils;

public class Console {
	Control text;
	boolean autoScroll = true;
	int maxLength;	

	public Console(Control text, int maxLength, boolean autoScroll) {
		this.text = text;
		this.maxLength = maxLength;
		this.autoScroll = autoScroll;
	}

	public boolean isAutoScroll() {
		return autoScroll;
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;		
		autoScroll();
	}

	private void autoScroll() {
		if (autoScroll) {
			text.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						SwtTextUtils.scrollToBottom(text);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}
	}
	
	private void checkMaxLengh() {
		if(maxLength > 0 && SwtTextUtils.getText(text).length() > maxLength) {
			int length = 10240;
			if(length > maxLength) {
				length = maxLength;
			}
			
			SwtTextUtils.setSelection(text, 0, length);
			SwtTextUtils.clearSelection(text);
		}
	}
	
	public void append(final String str) {
		if(str != null && !"".equals(str)) {
			text.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						SwtTextUtils.append(text, str);
						SwtTextUtils.append(text, "\n");
						checkMaxLengh();
						if(autoScroll) {
							SwtTextUtils.scrollToBottom(text);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	public void append(Object object) {
		append(String.valueOf(object));
	}
	
	public void append(Throwable t) {
		append(ExceptionUtil.toString(t));
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int maxLength = self.doAction("getMaxLength", actionContext);
		boolean autoScroll = self.doAction("isAutoScroll", actionContext);
		
		Console console = new Console(actionContext.getObject("parent"), maxLength, autoScroll);
		actionContext.g().put(self.getMetadata().getName(), console);
	}
}
