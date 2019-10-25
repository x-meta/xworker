package xworker.swt.util;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

public class TextProxy {
	Object text = null;
	
	public TextProxy(Object text) {
		this.text = text;
	}
	
	public void setText(String str) {
		if(text instanceof Text) {
			((Text) text).setText(str);
		}else if(text instanceof StyledText) {
			((StyledText) text).setText(str);
		}
	}
	
	public String getText() {
		if(text instanceof Text) {
			return ((Text) text).getText();
		}else if(text instanceof StyledText) {
			return ((StyledText) text).getText();
		}
		
		return null;
	}
}
