package xworker.swt.util.clipscreen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ClipSelector {
	int startX;
	int startY;
	int endX;
	int endY;	
	private Rectangle dragRecs[] = new Rectangle[8];
	int dragRecSize = 7;
	int dragRecOffset = dragRecSize / 2;
	
	public void init(int startX, int startY, int endX, int endY){
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		
		int x1 = (startX + endX) / 2;
        int y1 = (startY +endY ) / 2;
        
        dragRecs[0]=new Rectangle(startX-dragRecOffset,startY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[1]=new Rectangle(x1-dragRecOffset,startY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[2]=new Rectangle(endX-dragRecOffset,startY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[3]=new Rectangle(endX-dragRecOffset,y1-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[4]=new Rectangle(endX-dragRecOffset,endY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[5]=new Rectangle(x1-dragRecOffset,endY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[6]=new Rectangle(startX-dragRecOffset,endY-dragRecOffset,dragRecSize,dragRecSize);
        dragRecs[7]=new Rectangle(startX-dragRecOffset,y1-dragRecOffset,dragRecSize,dragRecSize);
	}
	
	public void paint(GC gc){
		if(startX == 0 && startY == 0 && endX == 0 && endY == 0){
			return;
		}
		
		gc.drawLine(startX, startY, endX, startY);
		gc.drawLine(startX, startY, startX, endY);
		gc.drawLine(startX, endY, endX, endY);
		gc.drawLine(endX, startY, endX, endY);
		
		for(Rectangle rect : dragRecs){
			gc.fillRectangle(rect);
		}
	}
	
	public boolean isSelected(){
		return startX != 0 || endX != 0 || startY != 0 || endY != 0;
	}
	
	public void unselected(){
		startX = endX = startY = endY = 0;
	}
	
	public boolean contains(int x, int y){
		return x >= startX && x <= endX && y >= startY && y <= endY;
	}
	
	public int getDragType(int x, int y){
		if(!contains(x, y)){
			return 0;
		}
		
		for(int i=0; i<dragRecs.length; i++){
			Rectangle rect = dragRecs[i];
			if(rect.contains(x, y)){
				switch(i){
				case 0:
					return ClipScreen.DRAG_LEFT | ClipScreen.DRAG_TOP;
				case 1:
					return ClipScreen.DRAG_TOP;
				case 2:
					return ClipScreen.DRAG_TOP | ClipScreen.DRAG_RIGHT;
				case 3:
					return ClipScreen.DRAG_RIGHT;
				case 4:
					return ClipScreen.DRAG_RIGHT | ClipScreen.DRAG_BOTTOM;
				case 5:
					return ClipScreen.DRAG_BOTTOM;
				case 6:
					return ClipScreen.DRAG_BOTTOM | ClipScreen.DRAG_LEFT;
				case 7:
					return ClipScreen.DRAG_LEFT;
				}
			}
		}
		
		return ClipScreen.DRAG_ALL;
	}
	
	public Cursor getCursor(Display display, int x, int y){
		if(!contains(x, y)){
			return null;
		}
		
		for(int i=0; i<dragRecs.length; i++){
			Rectangle rect = dragRecs[i];
			if(rect.contains(x, y)){
				switch(i){
				case 0:
					return display.getSystemCursor(SWT.CURSOR_SIZENW);
				case 1:
					return display.getSystemCursor(SWT.CURSOR_SIZEN);
				case 2:
					return display.getSystemCursor(SWT.CURSOR_SIZENE);
				case 3:
					return display.getSystemCursor(SWT.CURSOR_SIZEE);
				case 4:
					return display.getSystemCursor(SWT.CURSOR_SIZESE);
				case 5:
					return display.getSystemCursor(SWT.CURSOR_SIZES);
				case 6:
					return display.getSystemCursor(SWT.CURSOR_SIZESW);
				case 7:
					return display.getSystemCursor(SWT.CURSOR_SIZEW);
				}
			}
		}
		
		return display.getSystemCursor(SWT.CURSOR_HAND);
	}
}
