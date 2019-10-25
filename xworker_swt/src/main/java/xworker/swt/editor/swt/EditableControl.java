/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.editor.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class EditableControl implements PaintListener, MouseListener, MouseTrackListener{
	public static int MOVE = 1;
	public static int RESIZE_LEFT = 2;
	public static int RESIZE_TOP = 4;
	public static int RESIZE_RIGHT = 8;
	public static int RESIZE_BOTTOM = 16;
	public static int RESIZE_LEFT_TOP = 32;
	public static int RESIZE_TOP_RIGHT = 64;
	public static int RESIZE_RIGHT_BOTTOM = 128;
	public static int RESIZE_BOTTOM_LEFT = 256;
	public static int RESIZE_EDIT = 512;
	
	EditableControlManager manager = null;
	Control control = null;
	
	//是否是选中状态
	boolean selected = false;
	//是否已经画过选中的状态
	boolean drawSelected = false;
	int x,y,width, hegith;
	int style;
	
	public EditableControl(EditableControlManager manager, Control control, int style){
		this.manager = manager;
		this.control = control;
		this.style = style;
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDown(MouseEvent event) {
		if(event.stateMask != SWT.CTRL){
			//如果没有按住CTRL键，那么其他选中的丢失选中焦点
			manager.unSelectAll();
		}
		
		//处于选中状态
		selected = true;
	}

	@Override
	public void mouseUp(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintControl(PaintEvent event) {
		GC gc = event.gc;
		if(this.drawSelected){
			//如果已经画过，判断位置是否已经变更
			return;
		}
		
		//System.out.println(event.count);
		Color color = gc.getForeground();
		gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
		
		//先画一个虚线边框代表已选中
		Point size = control.getSize();
		Rectangle rect = new Rectangle(0, 0, size.x, size.y);
		gc.setLineStyle(SWT.LINE_DASHDOTDOT);
		gc.drawRectangle(rect.x, rect.y, rect.width - 1, rect.height - 1);
					
		//画Resize
		gc.setLineStyle(SWT.LINE_SOLID);
		if((style | EditableControl.RESIZE_LEFT) == EditableControl.RESIZE_LEFT ){
			//左面可以缩放
		}
		
		gc.setForeground(color);		
	}

	@Override
	public void mouseEnter(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExit(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseHover(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}