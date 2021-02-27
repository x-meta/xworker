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
package xworker.swt.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.World;

import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

/**
 * ToolTipListener，打开提示的监听器。
 * 
 * 2013-07-09监听器设置为每个线程一个，期望不再出现显示不了的问题。
 * 
 * @author Administrator
 *
 */
public class LabelToolTipListener implements MouseTrackListener, MouseListener{
	static List<LabelToolTipListener> listeners = new ArrayList<LabelToolTipListener>();
	static long lastCheckTime = 0;
 
	public static LabelToolTipListener getInstance(){
		return getInstance(Thread.currentThread());
	}
	
	public synchronized static LabelToolTipListener getInstance(Thread thread){
		Display display = Display.findDisplay(thread);
		if(display == null){
			return null;
		}
				
		if(System.currentTimeMillis() - lastCheckTime > 10000){
			//每10秒检测一次不要的线程
			for(int i=0; i<listeners.size(); i++){
				LabelToolTipListener listener = listeners.get(i);
				if(listener.getDisplay().isDisposed()){
					listeners.remove(i);
					i--;
				}
			}
		}

		//查找listener
		for(int i=0; i<listeners.size(); i++){
			LabelToolTipListener listener = listeners.get(i);
			if(listener.getDisplay() == display){
				return listener;
			}
		}
		
		//创建新的listener
		LabelToolTipListener listener = new LabelToolTipListener(display);
		listeners.add(listener);			
		
		return listener;
	}
	
	protected Control lastControl;
	protected Object utilBrowser;
	protected LabelToolTipOpenThread thread;
	protected Display display;
	
	public void setUtilBrowser(Object utilBrowser){
		this.utilBrowser = utilBrowser;
	}
	
	public LabelToolTipListener(Display display){
		this.display = display;
		if(display != null){
			display.addFilter(SWT.KeyUp, new Listener(){
				public void handleEvent(Event event) {
					if(lastControl != null && event.keyCode == SWT.F2){
						openToolTipBrowser();
					}
				}				
			});
		}
	}
		
	public Display getDisplay(){
		return display;
	}
	public void openToolTipBrowser(){
		ActionContext actionContext = new ActionContext();
		actionContext.put("parent", lastControl.getShell());
		actionContext.put("message", lastControl.getData("toolTip"));
		actionContext.put("utilBrowser", utilBrowser);
		
		Shell toolTipShell = (Shell) lastControl.getData("toolTipShell");
		Shell toolTipBrowser = (Shell) PoolableControlFactory.borrowControl(lastControl.getShell(), "xworker.ide.worldexplorer.swt.util.ToolTipBrowser/@shell", actionContext);
		
		if(SwtUtils.isRWT()) {
			SwtUtils.setShellRelateLocation(toolTipBrowser, lastControl.toDisplay(lastControl.getLocation()),
					lastControl.getSize());
		}else {
			//toolTipShell.open();
			if(toolTipShell == null){
				return;
			}
			
			Point point = toolTipShell.getLocation();
			//System.out.println(point);
			toolTipBrowser.setLocation(point);
		}
		
		Browser browser = actionContext.getObject("browser");
		String url = XWorkerUtils.getWebControlUrl(World.getInstance().getThing("xworker.ide.worldexplorer.swt.util.HtmlToolTipWeb"));
		String content = (String) lastControl.getData("toolTip");
		url = url + "&overflow=false&message=" + content;
		browser.setUrl(url);
		/*
		ActionContainer actions = (ActionContainer) toolTipBrowser.getData("actions");
		Map<String, Object> param = new HashMap<String, Object>();
		
		if(content != null && content.startsWith("thing=")) {
			String thingPath = content.substring(6, content.length());
			Thing attribute = World.getInstance().getThing(thingPath);
			content = "<b><u><a href=\"" + XWorkerUtils.getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" 
			    	+ thingPath + "\">"+ attribute.getMetadata().getName() + "</a></u></b><br/><br/>";
		}
		param.put("message", content);
		actions.doAction("setContent", param);*/
		toolTipBrowser.setVisible(true);
		toolTipBrowser.setFocus();
		closeToolTip(lastControl);
		lastControl = null;
	}
	
	public void mouseEnter(MouseEvent e) {
		Control control = (Control) e.getSource();
		lastControl = control;
		
		thread = new LabelToolTipOpenThread();
		thread.control = control;
		thread.parent = control.getShell();
		Point point = control.toDisplay(e.x + 10, e.y + 10);
		thread.x = point.x;
		thread.y = point.y;
		thread.listsner = this;
		thread.start();
		control.setData("labelToolTipThread", thread);
	}

	public void closeToolTip(Control control){				
		Shell toolTipShell = (Shell) control.getData("toolTipShell");
		if(toolTipShell != null && !toolTipShell.isDisposed()){
			control.setData("toolTipShell", null);
			toolTipShell.setVisible(false);		
			//toolTipShell.dispose();
			PoolableControlFactory.returnControl(control.getShell(), "xworker.ide.worldexplorer.swt.util.HtmlToolTip/@shell", toolTipShell);
		}
	}
	
	public void mouseExit(MouseEvent e) {
		lastControl = null;
		thread.cancel = true;
		
		Control control = (Control) e.getSource();	
		closeToolTip(control);
		//if(toolTipShell != null)
		//	toolTipShell.dispose();		
	}

	public void mouseHover(MouseEvent e) {		
	}

	@Override
	public void mouseDoubleClick(MouseEvent event) {		
		if(SwtUtils.isRWT() && event.widget instanceof Control){
			lastControl = (Control) event.widget;
			this.openToolTipBrowser();
		}
	}

	@Override
	public void mouseDown(MouseEvent event) {
		if(event.button == 3 && event.widget instanceof Control){
			lastControl = (Control) event.widget;
			this.openToolTipBrowser();
		}
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
	}

}