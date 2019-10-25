package xworker.swt.xwidgets.clock;

import java.util.Date;

import org.eclipse.swt.graphics.GC;

public interface ClockDrawer {
	/**
	 * 画时钟。
	 * 
	 * @param date
	 * @param gc
	 */
	public void drawClock(Date date, float x, float y, float width, float height, GC gc);
	
	public void setShowAmPm(Boolean showAmPm);
	
	public void setShowArea(Boolean showArea);
	
	public void setShowHour(Boolean showHour);
	
	public void setShowMinute(Boolean showMinute);
	
	public void setShowSecond(Boolean showSecond);
}
