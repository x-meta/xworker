package xworker.swt.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * 在屏幕上的矩形范围内画出缩放的图形和缩放的相关功能。
 * 
 * @author Administrator
 *
 */
public class ResizeUtil {
	public static final String TOP = "top";
	public static final String TOP_LEFT = "top_left";
	public static final String TOP_RIGHT = "top_right";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String BOTTOM = "bottom";
	public static final String BOTTOM_LEFT = "bottom_left";
	public static final String BOTTOM_RIGHT = "bottom_right";
	public static final String MOVE = "move";
	
	public static void draw(GC gc, int x, int y , int width, int height){
	
		Color oldColor = gc.getBackground();
		Color color = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		gc.setBackground(color);
		Map<String, Rectangle> recs = getResizeRetangles(x, y, width, height);
		Rectangle rect = recs.get(BOTTOM);
		gc.fillRectangle(rect);
		
		rect = recs.get(BOTTOM_LEFT);
		gc.fillRectangle(rect);
		
		rect = recs.get(BOTTOM_RIGHT);
		gc.fillRectangle(rect);
		
		rect = recs.get(LEFT);
		gc.fillRectangle(rect);
		
		rect = recs.get(RIGHT);
		gc.fillRectangle(rect);
		
		rect = recs.get(TOP);
		gc.fillRectangle(rect);
		
		rect = recs.get(TOP_LEFT);
		gc.fillRectangle(rect);
		
		rect = recs.get(TOP_RIGHT);
		gc.fillRectangle(rect);
		
		gc.setBackground(oldColor);
	}
	
	/**
	 * 获取鼠标的光标形状。
	 * 
	 * @param display
	 * @param x 鼠标的x坐标
	 * @param y 鼠标的y坐标
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getCursorType(Display display, int x, int y, int x1, int y1, int width, int height){
		Map<String, Rectangle> recs = getResizeRetangles(x1, y1, width, height);

		//System.out.println("x=" + x + ",y=" +y + ", rects=" + recs);
		Rectangle rect = recs.get(LEFT);
		if(isInRectangle(x, y, rect)){
			return LEFT;
		}
		
		rect = recs.get(RIGHT);
		if(isInRectangle(x, y, rect)){
			return RIGHT;
		}
		
		rect = recs.get(TOP);
		if(isInRectangle(x, y, rect)){
			return TOP;
		}
		
		 rect = recs.get(BOTTOM);
		if(isInRectangle(x, y, rect)){
			return BOTTOM;
		}
		
		rect = recs.get(BOTTOM_LEFT);
		if(isInRectangle(x, y, rect)){
			return BOTTOM_LEFT;
		}
		
		rect = recs.get(BOTTOM_RIGHT);
		if(isInRectangle(x, y, rect)){
			return BOTTOM_RIGHT;
		}
		
		rect = recs.get(TOP_LEFT);
		if(isInRectangle(x, y, rect)){
			return TOP_LEFT;
		}
		
		rect = recs.get(TOP_RIGHT);
		if(isInRectangle(x, y, rect)){
			return TOP_RIGHT;
		}
		
		return null;		
	}
	
	public static Cursor getCursor(Display display, String type){
		if(BOTTOM.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZES);
		}
		
		if(BOTTOM_LEFT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZESW);
		}
		
		if(BOTTOM_RIGHT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZESE);
		}
		
		if(LEFT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZEW);
		}
		
		if(RIGHT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZEE);
		}
		
		if(TOP.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZEN);
		}
		
		if(TOP_LEFT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZENW);
		}
		
		if(TOP_RIGHT.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZENE);
		}
		
		if(MOVE.equals(type)){
			return display.getSystemCursor(SWT.CURSOR_SIZEALL);
		}
		return null;		
	}
	
	public static boolean isInRectangle(int x, int y, Rectangle rect){
		if(getPintAtLineSide(x, y, rect.x, rect.y, rect.x + rect.width, rect.y) > 0){
			return false;
		}
		
		if(getPintAtLineSide(x, y, rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height) > 0){
			return false;
		}
		
		if(getPintAtLineSide(x, y, rect.x + rect.width, rect.y + rect.height, rect.x, rect.y + rect.height) > 0){
			return false;
		}
		
		if(getPintAtLineSide(x, y, rect.x, rect.y + rect.height, rect.x, rect.y) > 0){
			return false;
		}
		
		return true;
	}
	
	public static boolean isInTrangle(int x, int y , int x1, int y1, int x2, int y2, int x3, int y3){
		if(getPintAtLineSide(x, y, x1, y1, x2, y2) > 0){
			return false;
		}
		
		if(getPintAtLineSide(x, y, x2, y2, x3, y3) > 0){
			return false;
		}
		
		if(getPintAtLineSide(x, y, x3, y3, x1, y1) > 0){
			return false;
		}
		
		return true;
	}
	
	public static Map<String, Rectangle> getResizeRetangles(int x, int y , int width, int height){
		if(width < 3) {
			width  = 3;
		}
		if(height < 3) {
			height = 3;
		}
		int h = 6;

		if(h > (width / 3)){
			h = width / 3;
		}
		if(h > (height / 3)){
			h = height / 3;
		}
		int hf = h / 2;
				
		Map<String, Rectangle> recs = new HashMap<String, Rectangle>();
		recs.put(BOTTOM, new Rectangle(x + width / 2 - hf, y + height - h , h, h));
		
		recs.put(BOTTOM_LEFT, new Rectangle(x, y + height - h, h, h));
		
		recs.put(BOTTOM_RIGHT, new Rectangle(x + width - h, y + height - h, h, h));
		
		recs.put(LEFT, new Rectangle(x, y + height/2 - hf, h, h));
		
		recs.put(RIGHT, new Rectangle(x + width - h, y + height /2 - hf, h, h));
		
		recs.put(TOP, new Rectangle(x + width / 2 - hf, y, h, h));
		
		recs.put(TOP_LEFT, new Rectangle(x, y, h, h));
		
		recs.put(TOP_RIGHT, new Rectangle(x + width - h, y, h, h));
		
		return recs;
		
	}
	
	/**
	 * 判断点x,y在直线x1,y1 x2,y2的那一侧。返回值&gt;0左侧，0直线上，&lt;0右侧。
	 * 
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int getPintAtLineSide(int x, int y, int x1, int y1, int x2, int y2){
		 int A=y2-y1; 
		 int B=x1-x2; 
		 int C=x2*y1-x1*y2;
		 
		 return A*x+B*y+C;
	}
}
