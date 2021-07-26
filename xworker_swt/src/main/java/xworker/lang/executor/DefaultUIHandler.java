package xworker.lang.executor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.swt.widgets.CompositeCreator;
import xworker.util.StringUtils;
import xworker.util.XWorkerUtils;

public class DefaultUIHandler implements UIHandler{
	private static final String TAG = DefaultUIHandler.class.getName();

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
				SwtUtils.setThingDesc(request.request.getThing(), DefaultUIHandler.this.browser);
			}
			
		});
	}
	
	@Override
	public void handleUIRequest(final xworker.lang.executor.Request request) {
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
		xworker.lang.executor.Request request;
		ActionContext ac;
		int count = 1;
		String time = null;
		ExecutorService executorService;
		
		TableItem item;
		Composite composite;
		
		public Request(Thread thread, xworker.lang.executor.Request request, ActionContext actionContext, ExecutorService executorService) {
			this.thread = thread;
			this.request = request;
			
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss-SSS");
			this.executorService = executorService;
			time = sf.format(new Date());
		}
		
		public boolean equals(Thread thread, Thing thing, ActionContext actionContext) {
			boolean unique = thing.getBoolean("unique");
			if(unique && thing == this.request.getThing()) {
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
				try {
					Thing prototype = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.QuestPrototype/@contentComposite");
					composite = prototype.doAction("create", request.getActionContext(), "parent", parent);
					Composite contentComposite = request.getActionContext().getObject("contentComposite");
					Composite buttonComposite = request.getActionContext().getObject("buttonComposite");

					//创建请求自身的界面
					request.create(contentComposite, "swt");

					//创建按钮
					Button okButton = new Button(buttonComposite, SWT.NONE);
					okButton.setText(StringUtils.getString("lang:d=确定&en=Ok", request.getActionContext()));
					okButton.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							ok();
						}
					});

					for(Thing buttons : request.getThing().getChilds("Buttons")){
						for(Thing buttonThing : buttons.getChilds()){
							Button button = new Button(buttonComposite, SWT.NONE);
							button.setText(buttonThing.getMetadata().getLabel());
							button.addListener(SWT.Selection, new Listener() {
								@Override
								public void handleEvent(Event event) {
									buttonThing.doAction("doAction", request.getActionContext());
								}
							});
						}
					}

					Button cancelButton = new Button(buttonComposite, SWT.NONE);
					cancelButton.setText(StringUtils.getString("lang:d=取消&en=Cancel", request.getActionContext()));
					cancelButton.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							cancel();
						}
					});
				}catch(Exception e){
					Executor.error(TAG, "Create composite error", e);
				}
			}
			
			return composite;
		}
		
		public void ok() {
			try {
				Executor.push(request.getExecutorService());

				Object result = request.doAction("ok");
				if(result instanceof Boolean && !(Boolean) result){
					return;
				}else{
					request.finish();
				}
			}finally {
				Executor.pop();
			}
		}
		
		public void cancel() {
			try {
				Executor.push(executorService);
				Object result = request.doAction("cancel");
				if(result instanceof Boolean && !(Boolean) result){
					return;
				}else{
					request.finish();
				}
			}finally {
				Executor.pop();
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
	public void removeRequest(final xworker.lang.executor.Request request) {
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

	@Override
	public List<xworker.lang.executor.Request> getRequests() {
		List<xworker.lang.executor.Request> requests = new ArrayList<xworker.lang.executor.Request>();
		if(table != null && table.isDisposed() == false) {
			for(TableItem item : table.getItems()) {
				Request request = (Request) item.getData();
				if(request != null) {
					requests.add(request.request);
				}
			}
		}

		return requests;
	}
}
