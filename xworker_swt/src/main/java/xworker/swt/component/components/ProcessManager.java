package xworker.swt.component.components;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import freemarker.template.TemplateException;
import xworker.lang.system.IProcessManager;
import xworker.service.ServiceManager;
import xworker.swt.functions.AutoScroll;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;

public class ProcessManager {
	private static List<ProcessConsole> getConsoles(ActionContext actionContext){
		List<ProcessConsole> consoles = actionContext.getObject("consoles");
		if(consoles == null) {
			consoles = new ArrayList<ProcessConsole>();
			actionContext.g().put("consoles", consoles);
		}
		
		return consoles;
	}
	
	public static void setProcess(ActionContext actionContext) {
		String  name = actionContext.getObject("name");
		Process process = actionContext.getObject("process");
			
		List<ProcessConsole> consoles = getConsoles(actionContext);
		for(ProcessConsole c : consoles) {
			if(c.process == process) {
				//相同进程已经添加了
				return;				
			}
		}
	
		//创建进程控制台
		ProcessConsole processConsole = new ProcessConsole(name, process);
		consoles.add(processConsole);
		
		processConsole.create(actionContext);
	}
	
	public static void teminate(ActionContext actionContext) {
		Object currentConsole = actionContext.getObject("currentConsole");
		if(currentConsole instanceof ProcessConsole) {
			ProcessConsole pc = (ProcessConsole) currentConsole;
			pc.process.destroy();
			
			checkToolItemsStatus(pc, actionContext);
		}
	}
	
	public static void clearConsole(ActionContext actionContext) {
		Object currentConsole = actionContext.getObject("currentConsole");
		if(currentConsole == null) {
			currentConsole = actionContext.getObject("systemConsole");
		}
		
		if(currentConsole instanceof ProcessConsole) {
			ProcessConsole console = (ProcessConsole) currentConsole;
			Widget text = console.actions.getActionContext().getObject("text");
			SwtTextUtils.setText(text, "");
		}else {			
			SwtTextUtils.setText(currentConsole, "");
		}
	}
	
	public static void scrollLock(ActionContext actionContext) {
		Object currentConsole = actionContext.getObject("currentConsole");
		if(currentConsole == null) {
			currentConsole = actionContext.getObject("systemConsole");
		}
		
		Event event = actionContext.getObject("event");
		ToolItem item = (ToolItem) event.widget;
		
		AutoScroll autoScroll = null;
		if(currentConsole instanceof ProcessConsole) {
			ProcessConsole console = (ProcessConsole) currentConsole;
			Widget text = console.actions.getActionContext().getObject("text");
			autoScroll = SwtUtils.getRegist(text, AutoScroll.class);
		}else {
			autoScroll = SwtUtils.getRegist((Widget) currentConsole, AutoScroll.class);
		}
		
		if(autoScroll != null) {
			if(autoScroll.isAutoScroll()) {
				autoScroll.setAutoScroll(false);
				item.setSelection(true);				
			}else {
				autoScroll.setAutoScroll(true);
				item.setSelection(false);
			}
		}
	}
	
	public static void remove(ActionContext actionContext) {
		Object currentConsole = actionContext.getObject("currentConsole");
		if(currentConsole instanceof ProcessConsole) {
			List<ProcessConsole> consoles = getConsoles(actionContext);
			int index = consoles.indexOf(currentConsole);
			consoles.remove(index);
			if(index < consoles.size()) {
				showConsole(consoles.get(index), actionContext);
			}else {
				Control systemConsole = actionContext.getObject("systemConsole");
				if(systemConsole != null && !systemConsole.isDisposed()) {
					showConsole(systemConsole, actionContext);
				}else {
					showConsole(actionContext.get("noConsoleLabel"), actionContext);
				}
			}
		}
	}
	
	public static void removeAll(ActionContext actionContext) {
		Object currentConsole = actionContext.getObject("currentConsole");
		int index = -1;
		List<ProcessConsole> consoles = getConsoles(actionContext);
		
		if(currentConsole instanceof ProcessConsole) {
			ProcessConsole console = (ProcessConsole) currentConsole;
			if(console.isAlive() == false) {
				index = consoles.indexOf(currentConsole);
			}	
		}
		
		for(int i=0; i<consoles.size(); i++) {
			if(consoles.get(i).isAlive() == false) {
				consoles.remove(i);
				i--;
			}
		}
		
		if(index != -1) {
			if(index < consoles.size()) {
				showConsole(consoles.get(index), actionContext);
			}else {
				Control systemConsole = actionContext.getObject("systemConsole");
				if(systemConsole != null && !systemConsole.isDisposed()) {
					showConsole(systemConsole, actionContext);
				}else {
					showConsole(actionContext.get("noConsoleLabel"), actionContext);
				}
			}
		}
	}

	public static void init(ActionContext actionContext) {
		IProcessManager service = ServiceManager.getService(IProcessManager.class); 
		if(service == null || service.isDisposed()) {
			ProcessManagerImpl impl = new ProcessManagerImpl(actionContext);
			ServiceManager.regist(impl, IProcessManager.class);
		}
	}
	
	public static void showConsole(Object console, ActionContext actionContext) {
		StackLayout layout = actionContext.getObject("contentStackLayout");
		Composite contentComposite = actionContext.getObject("contentComposite");
		Label titleLabel = actionContext.getObject("titleLabel");
		Control systemConsole = actionContext.getObject("systemConsole");
		
		AutoScroll autoScroll = null;
		if(console == systemConsole) {
			layout.topControl = systemConsole;
			try {
				titleLabel.setText(UtilData.getString("lang:d=系统控制台&en=System Console", actionContext));
			}catch(Exception e) {				
			}
			autoScroll = SwtUtils.getRegist((Widget) console, AutoScroll.class);
		}else if(console instanceof ProcessConsole){
			ProcessConsole pc = (ProcessConsole) console;
			layout.topControl = pc.control;
			titleLabel.setText(pc.name);
			
			Widget text = pc.actions.getActionContext().getObject("text");
			autoScroll = SwtUtils.getRegist(text, AutoScroll.class);
		}else if(console instanceof Control){
			layout.topControl = (Control) console;
		}
		
		contentComposite.layout();
		
		checkToolItemsStatus(console, actionContext);
		ToolItem scrollLockToolItem = actionContext.getObject("scrollLockToolItem");
		if(autoScroll != null) {		
			if(autoScroll.isAutoScroll()) {
				scrollLockToolItem.setSelection(false);
			}else {
				scrollLockToolItem.setSelection(true);
			}
		}else {
			scrollLockToolItem.setSelection(false);
		}
		
	}
	
	public static void switchConsole(ActionContext actionContext) throws IOException, TemplateException {
		List<ProcessConsole> consoles = getConsoles(actionContext);
		Control systemConsole = actionContext.getObject("systemConsole");
		
		Event event = actionContext.getObject("event");
		if((event.detail & SWT.ARROW) == SWT.ARROW) {
			//点击下拉部分，显示菜单
			int index = 1;
			ToolItem switchItem = actionContext.getObject("switchItem");
			ToolBar parent = switchItem.getParent();
			Menu menu = parent.getMenu();
			if(menu != null) {
				menu.dispose();
			}
			
			menu = new Menu(parent);			
			if(systemConsole != null && systemConsole.isDisposed() == false) {
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setData(systemConsole);
				item.setText(index + " " + UtilData.getString("lang:d=系统控制台&en=System Console", actionContext));
				item.addListener(SWT.Selection, new MenuListener(item, actionContext));
				index++;
			}
						
			for(ProcessConsole console : consoles) {
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setData(console);
				item.setText(index + " " + console.name);
				item.addListener(SWT.Selection, new MenuListener(item, actionContext));
				index++;
			}
			
			SwtUtils.showMenuByWidget(switchItem, menu);
		}else {
			//切换
			if(consoles.size() == 0) {
				return;
			}
			
			StackLayout layout = actionContext.getObject("contentStackLayout");
			if(layout.topControl == systemConsole) {
				showConsole(consoles.get(0), actionContext);				
			}else {
				for(int i=0; i<consoles.size(); i++) {
					if(layout.topControl == consoles.get(i).control) {
						if(i < consoles.size() - 1) {			
							showConsole(consoles.get( i + 1), actionContext);
						}else {
							showConsole(systemConsole, actionContext);
						}
						break;
					}
				}
			}
		}
	}
	
	public static void showProcess(ProcessConsole console, ActionContext actionContext) {
		showConsole(console, actionContext);		
		actionContext.g().put("currentConsole", console);		
	}
	
	private static void checkToolItemsStatus(Object obj, ActionContext actionContext) {
		actionContext.g().put("currentConsole", obj);	
		
		ToolItem terminateItem = actionContext.getObject("terminateItem");
		ToolItem removeItem = actionContext.getObject("removeItem");
		ToolItem removeAllItem = actionContext.getObject("removeAllItem");
		
		if(obj instanceof Control) {
			terminateItem.setEnabled(false);
			removeItem.setEnabled(false);
			
			List<ProcessConsole> consoles = getConsoles(actionContext);
			boolean r = false;
			for(ProcessConsole c : consoles) {
				if(c.isAlive() == false) {
					r = true;
					break;
				}
			}
			if(!r) {
				removeAllItem.setEnabled(false);
			}else {
				removeAllItem.setEnabled(true);
			}
		}else if(obj instanceof ProcessConsole){
			ProcessConsole console = (ProcessConsole) obj;
			
			if(console.isAlive()) {					
				removeItem.setEnabled(false);
				removeAllItem.setEnabled(false);
				terminateItem.setEnabled(true);
			}else {
				terminateItem.setEnabled(false);
				removeItem.setEnabled(true);
				removeAllItem.setEnabled(true);
			}
		}
	}
	
	static class ProcessConsole{
		String name;
		Process process;
		
		//ProcessConsole创建的动态容器
		ActionContainer actions;
		
		//ProcessConsole创建的control
		Control control;
		
		public ProcessConsole(String name, Process process) {
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(name == null) {
				name = process.toString();
			}
			this.name = name + " (" + sf.format(date) + ")";
			this.process = process;			
		}
		
		public void create(ActionContext actionContext) {
			Thing thing = new Thing("xworker.swt.xwidgets.ProcessConsole");
			thing.put("name", "processConsole");
			//thing.put("style", "seperate");
			thing.put("process", "var:process");
			thing.put("destroyOnDispose", "true");
			
			this.control = thing.doAction("create", actionContext, "process", process, "parent", actionContext.get("contentComposite"));
			actions = actionContext.getObject("processConsole");
		}
		
		public boolean isAlive() {
			return process != null && process.isAlive(); 
		}
	}
	
	static class MenuListener implements Listener{
		ActionContext actionContext;
		MenuItem item;
		
		public MenuListener(MenuItem item, ActionContext actionContext) {
			this.item = item;
			this.actionContext = actionContext;
		}
		
		@Override
		public void handleEvent(Event event) {
			showConsole(item.getData(), actionContext);
		}		
	}
	
	static class ProcessManagerImpl implements IProcessManager{
		ActionContext actionContext;
		
		public ProcessManagerImpl(ActionContext actionContext) {
			this.actionContext = actionContext;
			ServiceManager.regist(this, IProcessManager.class);
		}
		
		public boolean isDisposed() {
			if(actionContext == null) {
				return true;
			}
			
			Composite contentComposite = actionContext.getObject("contentComposite");
			if(contentComposite == null || contentComposite.isDisposed()) {
				return true;
			}else {
				return false;
			}
		}
		
		public void addProcess(String name, Process process) {
			if(isDisposed()) {
				return;				
			}
			
			List<ProcessConsole> consoles = getConsoles(actionContext);
			for(ProcessConsole c : consoles) {
				if(c.process == process) {
					//相同进程已经添加了
					return;				
				}
			}
			
			//创建进程控制台
			ProcessConsole processConsole = new ProcessConsole(name, process);			
			consoles.add(processConsole);
			
			processConsole.create(actionContext);
		}
	}
}
