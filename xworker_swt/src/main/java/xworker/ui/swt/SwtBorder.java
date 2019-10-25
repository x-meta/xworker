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
package xworker.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import xworker.swt.util.SwtUtils;

public class SwtBorder implements PaintListener, Listener {
	public static void attach(Control control){
		if(control == null) {
			return;
		}
		if(SwtUtils.isRWT() == false) {
			control.addPaintListener(new SwtBorder());
		}else{			
			control.addListener(SWT.Paint, new SwtBorder());
		}
	}
	
	public void paintControl(PaintEvent event) {
		Control control = (Control) event.widget;
		Rectangle rect = new Rectangle(0, 0,
				control.getSize().x, control.getSize().y);
		
		GC gc = event.gc;
		//System.out.println(event.count);
		Color color = gc.getForeground();
		gc.setForeground(event.display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		gc.drawLine(rect.x, rect.y, rect.width, rect.y);
		gc.drawLine(rect.x, rect.y, rect.x, rect.height);
		gc.setForeground(event.display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		gc.drawLine(rect.x, rect.height-1, rect.width, rect.height-1);
		gc.drawLine(rect.width-1, rect.y, rect.width-1, rect.height);
		gc.setForeground(color);
	}

	@Override
	public void handleEvent(Event event) {
		Control control = (Control) event.widget;
		Rectangle rect = new Rectangle(0, 0,
				control.getSize().x, control.getSize().y);
		
		GC gc = event.gc;
		//System.out.println(event.count);
		Color color = gc.getForeground();
		gc.setForeground(event.display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		gc.drawLine(rect.x, rect.y, rect.width, rect.y);
		gc.drawLine(rect.x, rect.y, rect.x, rect.height);
		gc.setForeground(event.display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		gc.drawLine(rect.x, rect.height-1, rect.width, rect.height-1);
		gc.drawLine(rect.width-1, rect.y, rect.width-1, rect.height);
		gc.setForeground(color);
		
	}
}