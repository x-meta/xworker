package xworker.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import xworker.swt.custom.StyledTextProxy;

public class SwtTextUtils {
	public static Point getCaretLocation(Object obj) {
		if(obj instanceof Text) {
			if(SwtUtils.isRWT() == false) {
				return ((Text) obj).getCaretLocation();
			}else {
				//RWT的版本太低，不支持getCaretLocation的方法
				return null;
			}
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getCaretLocation(obj);
		}
		
		return null;
	}
	
	public static boolean getWordWrap(Object obj) {
		if(obj instanceof Text) {
			return false;
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getWordWrap(obj);
		}else {
			return false;
		}
	}
	
	public static String getText(Object obj) {
		if(obj instanceof Text) {
			return ((Text) obj).getText();
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getText(obj);
		}
		return obj.toString();
	}
	
	public static String getText(Object obj, int start, int end) {
		if(obj instanceof Text) {
			return ((Text) obj).getText(start, end);
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getText(obj, start, end);
		}
		return obj.toString();
	}
	
	public static void setText(Object obj, String text) {
		if(obj instanceof Text) {
			((Text) obj).setText(text);
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.setText(obj, text);
		}
	}
	
	public static void insert(Object obj, String text) {
		if(obj instanceof Text) {
			((Text) obj).insert(text);
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.insert(obj, text);
		}
	}
	
	public static void setCaretOffset(Object obj, int offset) {
		if(obj instanceof Text) {
			//((Text) obj).setca
			((Text) obj).setSelection(offset);
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.setCaretOffset(obj, offset);
		}
	}
	
	public static int getCaretOffset(Object obj) {
		if(obj instanceof Text) {
			return ((Text) obj).getCaretPosition();
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getCaretOffset(obj);
		}else {
			return 0;
		}
	}
	
	public static int getTopIndex(Object obj) {
		if(obj instanceof Text) {
			return ((Text) obj).getTopIndex();
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getTopIndex(obj);
		}else {
			return 0;
		}
	
	}
	
	public static void setTopIndex(Object obj , int index) {
		if(obj instanceof Text) {
			if(SWT.getVersion() > 4000) {
				((Text) obj).setTopIndex(index);
			}
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.setTopIndex(obj, index);
		}
	}
	
	public static void append(Object obj, String text) {
		if(obj instanceof Text) {
			((Text) obj).append(text);
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.append(obj, text);
		}
	}
	
	public static int getOffsetAtLocation(Object obj, Point point) {
		if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getOffsetAtLocation(obj, point);
		}else {
			return -1;
		}
	}
	
	public static void setSelection(Object obj, int start, int end) {
		if(obj instanceof Text) {
			Text text = (Text) obj;
			if(SwtUtils.isRWT() == false) {
				//RAP版本还未实现该方法
				text.setSelection(start, end);
			}
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.setSelection(obj, start, end);
		}
	}
	
	public static void showSelection(Object obj) {
		if(obj instanceof Text) {
			Text text = (Text) obj;
			if(SwtUtils.isRWT() == false) {
				text.showSelection();
			}
		}else if(StyledTextProxy.isStyledText(obj)) {
			StyledTextProxy.showSelection(obj);
		}
	}
	
	public static Point getSelection(Object obj) {
		if(obj instanceof Text) {
			Text text = (Text) obj;
			return text.getSelection();
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getSelection(obj);
		}
		
		return null;
	}
	
	public static int getLineAtOffset(Object obj, int offset) {
		if(obj instanceof Text) {
			Text text = (Text) obj;
			String content = text.getText();
			String[] lines = content.split("[\n]");
			int toffset = 0;
			for(int i=0; i<lines.length; i++) {
				int end = toffset + lines[i].length();
				if(offset >= toffset && offset <= end) {
					return i;
				}
				toffset = end;
			}
			return -1;
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getLineAtOffset(obj, offset);
			
		}else {
			return -1;
		}
	}
	
	public static int getOffsetAtLine(Object obj, int lineIndex) {		
		if(obj instanceof Text) {
			Text text = (Text) obj;
			String content = text.getText();
			String[] lines = content.split("[\n]");
			int offset = 0;
			for(int i=0; i<lineIndex; i++) {
				if(i >= lines.length) {
					break;
				}
				
				offset += lines[i].length();
			}
			return offset;
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getOffsetAtLine(obj, lineIndex);
			
		}else {
			return -1;
		}
	}
	
	public static int getLineCount(Object obj) {
		if(obj instanceof Text) {
			Text text = (Text) obj;
			String t = text.getText();
			return t.split("[\n]").length;
			//return text.getLineCount();
		}else if(StyledTextProxy.isStyledText(obj)) {
			return StyledTextProxy.getLineCount(obj);			
		}else {
			return -1;
		}
	}
	
	/**
	 * 显示文档的最底部。
	 * 
	 * @param obj
	 */
	public static void scrollToBottom(Object obj) {
		if(obj instanceof Text) {
			//Text text = (Text) obj;
			//text.setTopIndex(text.getLineCount() - 1);
		}
		else if(StyledTextProxy.isStyledText(obj)) {
			int lineCount = getLineCount(obj);
			setTopIndex(obj, lineCount - 1);			
			setCaretOffset(obj, getText(obj).length());
		}
	}
}
