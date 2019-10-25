package xworker.lang.executor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				String url = XWorkerUtils.getThingDescUrl(request.thing);
				DefaultUIHandler.this.browser.setUrl(url);
			}
			
		});
	}
	
	@Override
	public void handleUIRequest(final Thing thing, final ActionContext actionContext) {
		if(table.isDisposed()) {
			return;
		}
		final Thread thread = Thread.currentThread();
		//要转移的变量
		Map<String, Object> vs = thing.doAction("getVariables", actionContext);
		if(vs == null) {
			vs = new HashMap<String, Object>();
		}
		final Map<String, Object> variables = vs;
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					for(TableItem item : table.getItems()) {
						Request request = (Request) item.getData();
						if(request.equals(thread, thing, actionContext)) {
							request.addCount();
							return;
						}
					}
					
					Request request = new Request(thread, thing, variables, actionContext, 
							(ExecutorService) DefaultUIHandler.this.actionContext.getObject("executorService"));
					request.createTabeItem(table);
				}catch(Exception e) {					
				}
			}
		});
	}

	static class Request{
		Thread thread;
		Thing thing;
		ActionContext actionContext;
		ActionContext ac;
		int count = 1;
		String time = null;
		ExecutorService executorService;
		Map<String, Object> variables;
		
		TableItem item;
		Composite composite;
		
		public Request(Thread thread, Thing thing, Map<String, Object> variables, ActionContext actionContext, ExecutorService executorService) {
			this.thread = thread;
			this.thing = thing;
			this.actionContext = actionContext;
			this.variables = variables;
			
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss-SSS");
			this.executorService = executorService;
			time = sf.format(new Date());
		}
		
		public boolean equals(Thread thread, Thing thing, ActionContext actionContext) {
			if(thread == this.thread && thing == this.thing && actionContext == this.actionContext) {
				return true;
			}else {
				return false;
			}
		}
		
		public void createTabeItem(Table parent) {
			if(item == null) {
				item = new TableItem(parent, SWT.NONE, 0);
				String[] text = new String[] {time, thing.getMetadata().getLabel(), thread.getName(), };
				item.setText(text);
				item.setData(this);
			}
		}
		
		public void addCount() {
			count++;
			String[] text = new String[] {time,  thing.getMetadata().getLabel() + "(" + count + ")", thread.getName()};
			item.setText(text);
		}
		
		public Composite getComposite(Composite parent) {
			if(composite == null) {
				ac = new ActionContext();
				ac.putAll(variables);
				ac.put("parent", parent);
				ac.put("parentContext", actionContext);
				ac.put("executorService", executorService);
				ac.put("request", thing);
				ac.put("requestContext", actionContext);				
				
				composite = thing.doAction("createSWT", ac);
			}
			
			return composite;
		}
		
		public void ok() {
			try {
				Executor.push(executorService);
				
				Object result = null;
				if(ac != null) {
					ActionContainer actions = ac.getObject("actions");
					if(actions != null) {
						result = actions.doAction("getResult", ac);
					}
				}
				
				variables.put("result", result);
				thing.doAction("ok", actionContext, variables);
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
				thing.doAction("cancel", actionContext, variables);
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
		Composite composite = actionContext.getObject("requestComposite");
		
		return new DefaultUIHandler(table, browser, composite, actionContext);
	}

	@Override
	public Thread getThread() {
		return table.getDisplay().getThread();
	}

	@Override
	public void removeRequest(final Thing request, final ActionContext actionContext) {
		if(table.isDisposed()) {
			return;
		}
		
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					List<TableItem> removeList = new ArrayList<TableItem>();
					for(TableItem item : table.getItems()) {
						Request req = (Request) item.getData();
						if(req.thing == request) {
							removeList.add(item);
						}
					}
					
					for(TableItem item : removeList) {
						item.dispose();
					}
				}catch(Exception e) {
					
				}
			}
		});
	}
}
