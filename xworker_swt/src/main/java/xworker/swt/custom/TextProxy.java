package xworker.swt.custom;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TextProxy {
	Object text;
	
	public TextProxy(Object text) {
		this.text = text;
	}
	
	public Point getSelection() {
		if(text instanceof Text) {
			return ((Text) text).getSelection();
		}
		
		return StyledTextProxy.getSelection(text);
	}
	
	public void showSelection() {
		if(text instanceof Text) {
			((Text) text).showSelection();
		}
		StyledTextProxy.showSelection(text);
	}
	
	public void setCaretOffset(int offset) {
		if(text instanceof Text) {
		}else {
			StyledTextProxy.setCaretOffset(text, offset);
		}
	}

	public int getOffsetAtLocation(Point point) {
		if(text instanceof Text) {
			return 0;
		}else {
			return StyledTextProxy.getOffsetAtLocation(text, point);
		}
	}
	
	public void setSelection(int start, int end) {
		if(text instanceof Text) {
			((Text) text).setSelection(start, end);			
		}else {
			StyledTextProxy.setSelection(text, start, end);
		}
	}
	
	public int getLineCount() {
		return StyledTextProxy.getLineCount(text);
	}
	
	public void addModifyListener(ModifyListener listener) {
		StyledTextProxy.addModifyListener(text, listener);
	}
	
	public int getLineAtOffset(int offset) {
		return StyledTextProxy.getLineAtOffset(text, offset);
	}
	
	public int getOffsetAtLine(int lineIndex) {
		return StyledTextProxy.getOffsetAtLine(text, lineIndex);
	}
	
	public void setText(String str) {
		StyledTextProxy.setText(text, str);
	}
	
	public void append(String str) {
		StyledTextProxy.append(text, str);
	}
	
	public String getText() {
		return StyledTextProxy.getText(text);
	}
	
	public boolean isFocusControl() {
		return StyledTextProxy.isFocusControl(text);
	}
	
	public void initCodeAssistor(Thing thing, ActionContext actionContext) {
		StyledTextProxy.initCodeAssistor(text, thing, actionContext);
	}
	
	public boolean isDisposed() {
		return StyledTextProxy.isDisposed(text);
	}
}
