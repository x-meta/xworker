package xworker.rap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.ui.session.SessionManager;

public class XWorkerEntryPoint extends AbstractEntryPoint {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static {
		//注册RAP的会话管理器
		RapSessionManager.regist();
	}
	
	private void showStartMessage(Composite parent, String message ) {
		parent.setLayout(new FillLayout());
		Label label = new Label(parent, SWT.NONE);
		label.setText( message);
	}

	/**
	public Shell createShell(Display display) {
		ActionContext actionContext = new ActionContext();
		actionContext.put("parent", display);
		
		
		Thing thing = World.getInstance().getThing("xworker.ide.worldExplorer.swt.SimpleExplorerRunner");
		return thing.doAction("create", actionContext);
	}*/
	
	public void createContents(Composite parent) {
		World world = World.getInstance();				
		//GlobalConfig的webUrl的配置在HttpServletDo中。
		
		//设置会话
		ActionContext actionContext = new ActionContext();
		RapSession session = (RapSession) SessionManager.getSession(actionContext);		
		session.setRootComposite(parent);
		session.setRootShell(parent.getShell());
		
		String appPath = this.getParameter("app");
		Thing app = world.getThing(appPath);
		if(app == null) {
			showStartMessage(parent, "App not exists, please set app parameter!");
			//for(ThingManager tm : world.getThingManagers()) {
			//	System.out.println(tm);
			//}
			return;
		}else if(!app.isThing("xworker.swt.rwt.RwtWidget")){
			showStartMessage(parent, "App is not a xworker.swt.rwt.RwtWidget! path=" + appPath);
			return;
		}else {			
			//先执行init方法			
			actionContext.put("rootComposite", parent);
			for(String name : this.getParameterNames()) {
				actionContext.put(name, this.getParameter(name));
			}
			//actionContext.putAll(RWT.getRequest().getParameterMap());
			actionContext.put("rootShell", parent.getShell());
			actionContext.put("httpRequest", RWT.getRequest());
			actionContext.put("httpSession", RWT.getUISession().getHttpSession());
			//RWT.getRequest().getH
			
			app.doAction("init", actionContext);
			
			//注册ServiceManager
			for(Thing serviceHandler : app.getChilds("ServiceHandler")) {
				registServiceHandler(serviceHandler);
			}
			
			Thing thing = null;
			//运行APP
			parent.setLayout(new FillLayout());
			
			String type = app.doAction("getType", actionContext);
			
			if("Composite".equals(type)) {
				thing = app.doAction("getComposite", actionContext);
			}else {
				thing = app.doAction("getShell", actionContext);
			}
			
			if(thing == null) {
				showStartMessage(parent, "App need define a Composite or Shell");
				
				return;
			}
			if("Composite".equals(type)) {
				actionContext.put("parent", parent);				
			}else {
				actionContext.put("parent", parent.getShell());
			}
			
			Object obj = thing.doAction("create", actionContext);
			if(obj instanceof Shell) {
				final Shell shell = (Shell) obj;
				
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						shell.forceActive();
						//shell.setMinimized(false);
						shell.setMinimized(false);
						shell.setVisible(true);			
						shell.open();
					}
				});
				//System.out.println("xxxxxxxxxxxxxxxxxxx: " + shell);
				
				//如果是Shell，当Shell最小化时就看不见了
				//Button shellButton = new Button(parent, SWT.PUSH);
				parent.addListener(SWT.MouseDoubleClick, new Listener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void handleEvent(Event event) {
						new ShellManager(parent.getShell());
					}
					
				});
			}
		}	
	}

	private void registServiceHandler(Thing thing) {
		String id = thing.getMetadata().getName();
		if(RWT.getServiceManager().getServiceHandlerUrl(id) == null) {
			RWT.getServiceManager().registerServiceHandler(id, new XWorkerServiceHandler(thing));
		}
	}
	
	/*
	public static void main(String[] args) {
		try {
			
			World world = World.getInstance();
			world.init(null);
			//启动WEB服务器
			Server server = new Server();
			
			Connector connector = new SelectChannelConnector();			
			connector.setPort(81);
			server.setConnectors(new Connector[] { connector });
	
			Map<String, Object> initParams = new HashMap<String, Object>();
			initParams.put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
			WebAppContext root = new WebAppContext("webroot", "/manager");			
			root.setResourceBase("./webroot");
			root.setClassLoader(Thread.currentThread().getContextClassLoader());
			root.setInitParams(initParams);
			
			server.addHandler(root);
			
			server.start();
			System.out.println("管理器WEB已启动, 端口: " + 81);			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/

}
