package xworker.swt.custom.textutils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Display;

public class StyledTextLineNumber implements LineStyleListener, ModifyListener {
	public static void attach(StyledText styledText){
		StyledTextLineNumber sl = new StyledTextLineNumber();
		styledText.addLineStyleListener(sl);
		styledText.addModifyListener(sl);
	}
	
	@Override
	public void lineGetStyle(LineStyleEvent event) {
		StyledText styledText = (StyledText) event.widget;
		
		// Using ST.BULLET_NUMBER sometimes results in weird alignment.
		// event.bulletIndex = styledText.getLineAtOffset(event.lineOffset);
		StyleRange styleRange = new StyleRange();
		styleRange.foreground = Display.getCurrent().getSystemColor(
				SWT.COLOR_GRAY);
		int maxLine = styledText.getLineCount();
		int bulletLength = Integer.toString(maxLine).length();
		// Width of number character is half the height in monospaced font, add
		// 1 character width for right padding.
		int bulletWidth = (bulletLength + 1) * styledText.getLineHeight() / 2;
		styleRange.metrics = new GlyphMetrics(0, 0, bulletWidth);
		event.bullet = new Bullet(ST.BULLET_TEXT, styleRange);
		// getLineAtOffset() returns a zero-based line index.
		int bulletLine = styledText.getLineAtOffset(event.lineOffset) + 1;
		event.bullet.text = String.format("%" + bulletLength + "s", bulletLine);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		StyledText styledText = (StyledText) e.widget;
		
		// For line number redrawing.
		styledText.redraw();
	}
}
