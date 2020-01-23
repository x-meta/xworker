package xworker.lang.executor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.util.XWorkerUtils;

public class DefaultUIHandler implements UIHandler{
	Table table;
	Browser browser;
	Composite composite;
	ExecutorService executorService;
	ActionContext actionContext;
	
	public DefaultUIHandler(Table table, Browser browser, Composite composite, ActionContext actionContext) {
		this.table = table;
		this.browser = browser;
		this.composite = composite;
		this.actionContext = actionContext;
		
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Request request = (Request) event.item.getData();
				
				//请求界面
				Composite requestComposite = request.getComposite(DefaultUIHandler.this.composite);
				if(requestComposite != null) {
					StackLayout layout = (StackLayout) DefaultUIHandler.this.composite.getLayout();
					layout.topControl = requestComposite;
					DefaultUIHandler.this.composite.layout();
				}
				
				//文档
				String url = XWorkerUtils.getThingDescUrl(request.request.getThing());
				DefaultUIHandler.this.browser.setUrl(url);
			}
			
		});
	}
	
	@Override
	public void handleUIRequest(final ExecuteRequest request) {
		if(table.isDisposed()) {
			return;
		}
		final Thread thread = Thread.currentThread();
		//要转移的变量
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					for(TableItem item : table.getItems()) {
						Request req = (Request) item.getData();
						if(req.equals(thread, request.getThing(), actionContext)) {
							req.addCount();
							return;
						}
					}
					
					Request req = new Request(thread, request, actionContext, 
							(ExecutorService) DefaultUIHandler.this.executorService);
					req.createTabeItem(table);
				}catch(Exception e) {					
				}
			}
		});
	}

	static class Request{
		Thread thread;
		ExecuteRequest request;
		ActionContext ac;
		int count = 1;
		String time = null;
		ExecutorService executorService;
		
		TableItem item;
		Composite composite;
		
		public Request(Thread thread, ExecuteRequest request, ActionContext actionContext, ExecutorService executorService) {
			this.thread = thread;
			this.request = request;
			
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss-SSS");
			this.executorService = executorService;
			time = sf.format(new Date());
		}
		
		public boolean equals(Thread thread, Thing thing, ActionContext actionContext) {
			if(thread == this.thread && thing == this.request.getThing() 
					&& actionContext == this.request.getActionContext()) {
				return true;
			}else {
				return false;
			}
		}
		
		public void createTabeItem(Table parent) {
			if(item == null) {
				item = new TableItem(parent, SWT.NONE, 0);
				String[] text = new String[] {time, request.getThing().getMetadata().getLabel(), thread.getName(), };
				item.setText(text);
				item.setData(this);
			}
		}
		
		public void addCount() {
			count++;
			String[] text = new String[] {time,  request.getThing().getMetadata().getLabel() + "(" + count + ")", thread.getName()};
			item.setText(text);
		}
		
		public Composite getComposite(Composite parent) {
			if(composite == null) {
				ac = new ActionContext();
				ac.put("parent", parent);
				
				composite = (Composite) request.createSWT(parent, ac);
			}
			
			return composite;
		}
		
		public void ok() {
			try {
				Executor.push(request.getExecutorService());
				
				Object result = null;
				if(ac != null) {
					ActionContainer actions = ac.getObject("actions");
					if(actions != null) {
						result = actions.doAction("getResult", ac);
					}
				}
				
				request.doAction("ok", "result", result);
			}finally {
				Executor.pop();
				
				//销毁控件
				if(item != null) {
					item.dispose();
				}
				
				if(composite != null) {
					composite.dispose();
				}
			}
		}
		
		public void cancel() {
			try {
				Executor.push(executorService);
				request.doAction("cancel");
			}finally {
				Executor.pop();
				
				//销毁控件
				if(item != null) {
					item.dispose();
				}
				
				if(composite != null) {
					composite.dispose();
				}
			}
		}
	}
	
	public static void okButtonSelection(ActionContext actionContext) {
		Table table = actionContext.getObject("requestTable");
		if(table.getSelectionCount() <= 0) {
			return;
		}
		
		TableItem item = table.getSelection()[0];
		Request request = (Request) item.getData();
		request.ok();
	}
	
	public static void cancelButtonSelection(ActionContext actionContext) {
		Table table = actionContext.getObject("requestTable");
		if(table.getSelectionCount() <= 0) {
			return;
		}
		
		TableItem item = table.getSelection()[0];
		Request request = (Request) item.getData();
		request.cancel();
	}

	public static DefaultUIHandler create(ActionContext actionContext) {
		Table table = actionContext.getObject("requestTable");
		Browser browser = actionContext.getObject("browser");
		Composite composite = actionContext.getObject("contentComposite");
		
		return new DefaultUIHandler(table, browser, composite, actionContext);
	}

	@Override
	public Thread getThread() {
		return table.getDisplay().getThread();
	}

	@Override
	public void removeRequest(final ExecuteRequest request) {
		if(table.isDisposed()) {
			return;
		}
		
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					List<TableItem> removeList = new ArrayList<TableItem>();
					for(TableItem item : table.getItems()) {
						Request req = (Request) item.getData();
						if(req.request == request) {
							removeList.add(item);
						}
					}
					
					for(TableItem item : removeList) {
						Request request = (Request) item.getData();
						if(request.composite != null) {
							request.composite.dispose();
						}
						
						item.dispose();
					}
				}catch(Exception e) {
					
				}
			}
		});
	}

	@Override
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
