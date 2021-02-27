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
package xworker.swt.design;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.command.interactive.InteractiveListener;
import xworker.command.interactive.InteractiveUI;
import xworker.lang.executor.Executor;
import xworker.util.XWorkerUtils;

/**
 * 为一个Control增加一个MouseListener，监听鼠标中键的事件。
 * 
 * @author zyx
 *
 */
public class DesignListener implements PaintListener, MouseListener, MouseTrackListener, DisposeListener, KeyListener, DragSourceListener, DropTargetListener{
	private static final String TAG = DesignListener.class.getName();
	
	Control control;
	DesignDialog designDialog = new DesignDialog();
	
	public Menu getPopMenu(Shell shell, Control c, String thingPath, boolean isAttribute, ActionContext actionContext){
		DesignPopMenuListener popSelectionListener = new DesignPopMenuListener(thingPath, actionContext, c, isAttribute);
		
		final Menu popMenu = new Menu(c.getShell(), SWT.POP_UP);
		//popMenu.addDisposeListener(popSelectionListener);
		popMenu.setData(popSelectionListener);
		
		createPopMenuItem(popMenu, popSelectionListener, actionContext);
		
		return popMenu;

	}
	
	private void createPopMenuItem(Menu popMenu,DesignPopMenuListener popSelectionListener, ActionContext actionContext){
		Thing popThing = World.getInstance().getThing("xworker.ide.worldexplorer.swt.designer.DesignerPopMenu");
		ActionContext ac = new ActionContext();
		ac.put("parent", popMenu);
		ac.put("thingPath", popSelectionListener.thingPath);
		ac.put("context", popSelectionListener.actionContext);
		ac.put("control", popSelectionListener.control);
		for(Thing child : popThing.getChilds()){
			child.doAction("create", ac);
		}
		
		//如果是通过事物产生的界面，取消刷新控件的菜单
		if(popSelectionListener.isAttribute){
			if(ac.get("sep1") != null){
				((Widget) ac.get("sep1")).dispose();
			}
			if(ac.get("refreshParentItem") != null){
				((Widget) ac.get("refreshParentItem")).dispose();
			}
			if(ac.get("refreshChildItem") != null){
				((Widget) ac.get("refreshChildItem")).dispose();
			}
		}
		/*
		MenuItem openItem = new MenuItem(popMenu, SWT.PUSH);
		openItem.setText(UtilString.getString("res:res.w_exp:openThing:打开事物", actionContext));
		openItem.setData("open");
		openItem.addSelectionListener(popSelectionListener);
		
		MenuItem sItem = new MenuItem(popMenu, SWT.SEPARATOR);
		sItem.setText("");
		
		MenuItem refreshItem = new MenuItem(popMenu, SWT.PUSH);
		refreshItem.setText(UtilString.getString("res:res.w_exp:refreshControl:刷新控件", actionContext));
		refreshItem.setData("refresh");
		refreshItem.addSelectionListener(popSelectionListener);
		
		MenuItem refreshParentItem = new MenuItem(popMenu, SWT.PUSH);
		refreshParentItem.setText(UtilString.getString("res:res.w_exp:refreshParentControl:刷新父控件", actionContext));
		refreshParentItem.setData("refreshParent");
		refreshParentItem.addSelectionListener(popSelectionListener);
		
		MenuItem refreshChildItem = new MenuItem(popMenu, SWT.PUSH);
		refreshChildItem.setText(UtilString.getString("res:res.w_exp:refreshChildControl:刷新子控件", actionContext));
		refreshChildItem.setData("refreshChilds");
		refreshChildItem.addSelectionListener(popSelectionListener);
		
		MenuItem sItem1 = new MenuItem(popMenu, SWT.SEPARATOR);
		sItem1.setText("");
		
		MenuItem cancelItem = new MenuItem(popMenu, SWT.PUSH);
		cancelItem.setText(UtilString.getString("res:res.w_exp:cancel:取消", actionContext));
		cancelItem.setData("cancel");
		cancelItem.addSelectionListener(popSelectionListener);
		*/
	}
	
	public boolean isResizeAble(Control control){
		boolean resizeAble = false;
		Object data = control.getLayoutData();
		if(data != null){
			if(data instanceof GridData){
				resizeAble = true;
			}else if(data instanceof RowData){
				resizeAble = true;
			}else if(data instanceof FormData){
				resizeAble = true;
			}
		}else{
			Composite parent = control.getParent();
			Layout layout = parent.getLayout();
			if(layout instanceof GridLayout){
				resizeAble = true;
			}else if(layout instanceof FormLayout){
				resizeAble = true;
			}else if(layout instanceof RowLayout){
				resizeAble = true;
			}
		}
		return resizeAble;
	}
	
	public void paintControl(PaintEvent event) {
		if(Designer.isEnabled() == false || XWorkerUtils.getIde() == null) return;
				
		Control control = (Control) event.widget;
		
		//画设计时的选中
		Designer.paintIfSelected(control, event.gc);
		/*
		String designData = (String) control.getData(Designer.DATA_DESING_SELECTED);
		if("true".equals(designData)){
			Rectangle rect = null;
			if(control instanceof Composite){
				rect = ((Composite) control).getClientArea();
			}else{
			    rect = new Rectangle(0, 0,
					control.getSize().x, control.getSize().y);
			}
			
			GC gc = event.gc;
			//System.out.println(event.count);
			
			//判断是否是可以拖拽大小
			//if(isResizeAble(control)){
			ResizeUtil.draw(gc, rect.x, rect.y, rect.width, rect.height);
			//}
			
			Color color = gc.getForeground();
			gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLUE));
			gc.setLineStyle(SWT.LINE_DASHDOTDOT);
			gc.setLineWidth(1);
			gc.drawRectangle(rect.x + 3, rect.y + 3, rect.width - 3, rect.height - 3);
			//gc.drawRectangle(rect.x, rect.y, 6, 6);					
			gc.setForeground(color);
		}*/
		
		//画标记
		if(Designer.TRUE.equals(control.getData(Designer.DATA_MARKED))){
			//会绘制标记和不绘制标记间隔进行，看起来像返回提示
			if(!Designer.TRUE.equals(control.getData(Designer.DATA_MARKED_PAINTED))){
				control.setData(Designer.DATA_MARKED_PAINTED, Designer.TRUE);
				
				Rectangle rect = new Rectangle(0, 0, control.getSize().x, control.getSize().y);
				
				GC gc = event.gc;
				//System.out.println(event.count);
				Color color = gc.getForeground();
				gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_GREEN));
				gc.setLineWidth(3);
				gc.setLineStyle(SWT.LINE_SOLID);
				
				gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 7, rect.height - 7);
				//gc.drawRectangle(rect.x, rect.y, 6, 6);					
				gc.setForeground(color);
			}else{
				control.setData(Designer.DATA_MARKED_PAINTED, Designer.FALSE);
			}
		}
	}

	public void mouseDoubleClick(MouseEvent event) {
		try{
			//鼠标中键的双击
			if(event.button == 2 && Designer.isEnabled()){
				Control newControl = (Control) event.widget;
				if(Designer.getThing(newControl) != null){
					setCurrentControL(newControl);
					
					if(designDialog.isOpened() == false){
						designDialog.show(newControl, null);
					}
				}							
			}
		}catch(Throwable t){
			Executor.error(TAG, "design control mouse dbClick", t);
		}
	}
	
	/**
	 * 显示弹出菜单。
	 * 	
	 * @param c
	 */
	public void showMenu(Control c){
		String thingPath = (String) c.getData("_designer_thingPath");
		ActionContext actionContext = (ActionContext) c.getData("_designer_actionContext");
		Boolean isAttribute = (Boolean) c.getData("_designer_isAttribute");
		
		Menu popMenu = getPopMenu(c.getShell(), c, thingPath, isAttribute, actionContext);				
		popMenu.setVisible(true);
	}

	public void mouseDown(MouseEvent event) {
		
	}

	public InteractiveUI getInteractiveUI(Control control){
		if(control == null || control.isDisposed()){
			return null;
		}
		
		InteractiveUI ui = (InteractiveUI) control.getData(InteractiveUI.DATA_KEY);
		if(ui != null && ui.isEnabled()){
			return ui;
		}else if(!(control instanceof Shell) && control.getParent() != null){
			return getInteractiveUI(control.getParent());
		}else{
			return null;
		}
	}
	
	protected void removeCurrentControl(){
		if(control != null && !control.isDisposed()){
			if(designDialog != null && designDialog.shell.isDisposed() == false && 
					control.getDisplay() == designDialog.shell.getDisplay()){
				final Control fcontrol = control;
				control.getDisplay().asyncExec(new Runnable(){
					public void run(){
						if(fcontrol.isDisposed() == false) {
							fcontrol.setData("_design_selected", "false");		
							restoreControlBackground(fcontrol);
						}
					}					
				});				
				control.getDisplay().asyncExec(new RedrawControl(control));	
			}else{
				try{
					final Control oldControl = control;
					oldControl.getDisplay().asyncExec(new Runnable(){
						public  void run(){
							if(oldControl.isDisposed() == false) {
								//使原来选择的控件选择边框消失
								oldControl.setData("_design_selected", "false");		
								restoreControlBackground(oldControl);
								oldControl.getDisplay().asyncExec(new RedrawControl(oldControl));
							}
						}
					});
					
				}catch(Throwable t1){							
				}
			}
			
			control = null;
		}
	}

	protected Control getDesignTarget(Control control){
		Control target = (Control) control.getData(Designer.DATA_DESIGN_TARGET);
		if(target != null){
		    return getDesignTarget(target);
		}else{
			return control;
		}
	}
	
	public void setCurrentControL(Control newControl) {
		setCurrentControL(newControl, null);
	}
	
	public void setCurrentControL(Control newControl, Thing tools){
		if(newControl == null || newControl.isDisposed()){
			removeCurrentControl();
			designDialog.close();
			return;
		}
		
		//不能选中设计器本身的组件，（虽然可以选择，但可能会打乱设计功能）
		if(newControl.getShell() == designDialog.shell) {
			return;
		}
		
		newControl = getDesignTarget(newControl);
		if(control == newControl){		
			designDialog.showIfOpened(control, tools);
			return;
		}
		removeCurrentControl();
		if(control == null && newControl == null){
			if(designDialog.isOpened()){
				designDialog.close();
			}	
			return;
		}
		
		//为新选择的控件增加边框
		control = newControl;
		control.setData(Designer.DATA_DESING_SELECTED, "true");		
		Color selectBackground = control.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		setControlBackground(control, selectBackground);
		//control.redraw();
		control.getDisplay().asyncExec(new RedrawControl(control));
		designDialog.showIfOpened(control, tools);
		//control.getShell().redraw();		
		//control.getShell().update();
	}
	
	private void setControlBackground(Control control, Color background){
		if(!(control instanceof SashForm || control instanceof Sash || control instanceof ExpandBar)
				&& control.getData(Designer.DATA_OLD_BACKGROUND) == null){
			Color oldBackground = control.getBackground();
			control.setData(Designer.DATA_OLD_BACKGROUND, oldBackground);
			control.setBackground(background);
		}
		
		if(control instanceof Composite){
			for(Control child : ((Composite) control).getChildren()){
				setControlBackground(child, background);
			}
		}
	}
	
	private void restoreControlBackground(Control control){
		if(control == null || control.isDisposed()){
			return;
		}
		
		if(!(control instanceof SashForm || control instanceof Sash || control instanceof ExpandBar)){
			Color oldBackground = (Color) control.getData(Designer.DATA_OLD_BACKGROUND);
			if(oldBackground != null){
				control.setBackground(oldBackground);
				control.setData(Designer.DATA_OLD_BACKGROUND, null);			
			}
		}
		
		if(control instanceof Composite){
			for(Control child : ((Composite) control).getChildren()){
				restoreControlBackground(child);
			}
		}
	}
	
	public void mouseUp(MouseEvent event) {
		try{
			Control newControl = (Control) event.widget;
			
			//鼠标中键，是设计，只有在设计器打开时才能使用
			if(event.button == 2 && Designer.isEnabled()){
				boolean setNewControl = false;
				if(control != null && !control.isDisposed() && control != newControl){
					setNewControl = true;
				}else if((event.stateMask & SWT.CTRL) == SWT.CTRL){
					if(control == newControl){
						removeCurrentControl();
						designDialog.close();
					}else{
						setNewControl = true;
					}					
				}
				
				if(setNewControl && Designer.getThing(newControl) != null){
					setCurrentControL(newControl);
					
					if(designDialog.isOpened() == false){
						designDialog.show(newControl, null);
					}
				}		
			}
			
			//交互组件
			InteractiveUI ui = getInteractiveUI(newControl);
			if(ui != null && ui.isEnabled()){
				List<InteractiveListener> listeners = Designer.getInteractiveListeners(ui.getListenerName());
				if(listeners != null){
					for(int i=0; i<listeners.size(); i++) {
						InteractiveListener interactiveListener = listeners.get(i);
						if(interactiveListener == null || interactiveListener.isDisposed()) {
							listeners.remove(i);
							i--;
						}else {
							interactiveListener.connected(ui);
						}
					}					
				}
			}
		}catch(Throwable t){
			Executor.error(TAG, "design control mouse click", t);
		}
	}

	@Override
	public void mouseEnter(MouseEvent event) {
		return;  //总是自动弹出编辑按钮有点讨厌，暂时先取消
		/*
		Widget widget = event.widget;
		widget.setData("_design_mouse_in", "ture");
		if(widget.getData("_design_icon_shell") != null){
			return;
		}
		
		new Thread(new OpenDesignShellRunnable(event, this)).start();
		*/
	}

	@Override
	public void mouseExit(MouseEvent event) {
		event.widget.setData("_design_mouse_in", null);
		final Widget widget = event.widget;
		final Shell shell = (Shell) event.widget.getData("_design_icon_shell");
		final Display display = event.display;
		if(shell != null){
			display.asyncExec(new CloseDesignShellRunnabel(display, widget, shell, 1000));			
		}
	}

	@Override
	public void mouseHover(MouseEvent event) {	
		/*
		Control control = (Control) event.widget;
		if(isSelected(control)){
			//是否可以缩放大小
			if(isResizeAble(control)){
				if(control.getData(Designer.DATA_RESIZEING) == null){
					Rectangle rec = control.getBounds();
					String cursorType = ResizeUtil.getCursorType(control.getDisplay(), event.x, event.y,
							rec.x, rec.y, rec.width, rec.height);
					if(cursorType != null){
						control.setData(Designer.DATA_RESIZE_TYPE, cursorType);
						control.setCursor(ResizeUtil.getCursor(control.getDisplay(), cursorType));
					}else if(control.getData(Designer.DATA_RESIZE_TYPE) != null){
						control.setData(Designer.DATA_RESIZE_TYPE, null);
						control.setCursor(control.getDisplay().getSystemCursor(SWT.DEFAULT));
					}
				}
			}
		}*/
	}
	
	public boolean isSelected(Control control){
		if(Designer.isEnabled() == false || Designer.getExplorerActions() == null) return false;
		
		//如果当前控件式正在被选择的
		String designData = (String) control.getData(Designer.DATA_DESING_SELECTED);
		if("true".equals(designData)){
			return true;
		}else{
			return false;
		}
	}
	
	static class RedrawControl implements Runnable{
		public Control control;
		public RedrawControl(Control control){
			this.control = control;
		}
		
		public void run(){
			if(!control.isDisposed()){
				control.getDisplay().update();
				control.update();
				control.redraw();
			}
		}
	}
	
	static class CloseDesignShellRunnabel implements Runnable{
		Display display;
		Widget widget;
		Shell shell;
		long delay;
		
		public CloseDesignShellRunnabel(Display display, Widget widget, Shell shell, long delay){
			this.display = display;
			this.widget = widget;
			this.shell = shell;
			this.delay = delay;
		}
		
		public void run(){
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(display == null || display.isDisposed()){
				return;
			}
			
			display.asyncExec(new Runnable(){
				public void run(){
					if(shell != null && !shell.isDisposed()){
						widget.setData("_design_icon_shell", null);
						shell.dispose();
					}
				}
			});
		}
	}
		
	static class OpenDesignShellRunnable implements Runnable{
		Display display;
		Widget widget;
		DesignListener listener;
		Point point;
		ActionContext ac;
		
		public OpenDesignShellRunnable(MouseEvent event, DesignListener listener){
			this.display = event.display;
			this.widget = event.widget;
			this.listener = listener;
			
			Control control = (Control) event.getSource();
			if(control == null || control.getParent() == null){
				return;
			}
			point = control.getParent().toDisplay(control.getLocation());
			point.y = point.y - 16;
			
			ac = new ActionContext();
			ac.getScope(0).put("parent", ((Control) widget).getShell());
			ac.getScope(0).put("listener", listener);
			ac.getScope(0).put("theControl", widget);
		}
		
		public void run(){
			try{
				Thread.sleep(1000);
				
				if(display == null || display.isDisposed()){
					return;
				}
				
				display.asyncExec(new Runnable(){
					public void run(){
						if(!widget.isDisposed() && widget.getData("_design_mouse_in") != null){
							
							Thing iconShellThing = World.getInstance().getThing("xworker.ide.worldexplorer.swt.designer.DesignIconShell");
							Shell iconShell = (Shell) iconShellThing.doAction("create", ac);
												
							if(iconShell == null || point == null){
								return;
							}
							
							iconShell.setLocation(point);
							iconShell.setVisible(true);
							
							widget.setData("_design_icon_shell", iconShell);
							
							//打开后最多存在5秒
							new Thread(new CloseDesignShellRunnabel(display, widget, iconShell, 5000)).start();
						}
					}
				});
				
			}catch(Exception e){
				Executor.error(TAG, "Designer icon thread error", e);
			}
		}
		
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		Designer.detach(event.widget);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		/*
		//如果输入了SHIFT + Alt + C，那么弹出命令窗口
		if(event.stateMask == (SWT.SHIFT | SWT.ALT) && (event.keyCode == 'C' || event.keyCode == 'c')){
			ActionContext ac = new ActionContext();
			
			Control control = (Control) event.widget;
			this.setCurrentControL(control);
			
			if(!Designer.isControlAttribute(control)){
				Thing commandDomain = World.getInstance().getThing("xworker.ide.worldexplorer.swt.designer.DesignerCommandDomain");
				ActionContext actionContext = Designer.getControlActionContext(control);
				ac.put("parent", control.getShell());				
				ac.put("control", control);
				
				Thing shellThing = World.getInstance().getThing("xworker.lang.command.CommandExecutor");
				Shell shell = (Shell) shellThing.doAction("create", ac);
				shell.setText(commandDomain.getMetadata().getLabel());
								
				CommandExecutor executor = new CommandExecutor(commandDomain, ac, actionContext);
				ac.put("executor", executor);
				executor.doSearch("");
				shell.open();
			}
		}*/
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	@Override
	public void drop(DropTargetEvent event) {
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}
}
