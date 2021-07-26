package xworker.net.jsch;


import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSWT;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.xmeta.util.ExceptionUtil;
import xworker.swt.design.Designer;
import xworker.swt.util.UtilBrowser;
import xworker.task.TaskManager;

public class JCTerm {
	private static Logger logger = LoggerFactory.getLogger(JCTerm.class);
	
	private JCTermSWT jcTerm;
	
	private TermConnection connection;
	//是否在结束时自动关闭Session
	private boolean closeSession = false;
	private Session session;
	private Thing sessionThing;
	private ActionContext actionContext;
	private ScheduledFuture<?> future;
	private Composite mainComposite;
	private ScrolledComposite scrolledComposite;

	public JCTerm(Composite parent, int style, ActionContext actionContext){
		/*
		Frame frame = SWT_AWT.new_Frame(parent);
		JRootPane root=new JRootPane();
        frame.add(root);
        jcTerm=new JCTermSwing();
        root.getContentPane().add((JPanel)jcTerm);
        */
		this.actionContext = actionContext;
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		scrolledComposite = new ScrolledComposite(mainComposite, SWT.V_SCROLL);
		jcTerm = new JCTermSWT(scrolledComposite);
		scrolledComposite.setContent(jcTerm);
		scrolledComposite.setMinHeight(6000);
		jcTerm.setSize(scrolledComposite.getClientArea().width, 6000);

		mainComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mainComposite.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						resize();
					}
				});
			}
		});

		future = TaskManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(jcTerm.isDisposed()){
					future.cancel(false);
				}else {
					jcTerm.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							/*
							try{
								Point currentPosition = jcTerm.getCursorLocation();
								Point origin = scrolledComposite.getOrigin();

								Point rectangle = mainComposite.getParent().getSize();
								int bottomY = origin.y + rectangle.y;
								if (currentPosition.y < origin.y) {
									scrolledComposite.setOrigin(0, currentPosition.y - rectangle.y);
								} else if (currentPosition.y > bottomY) {
									scrolledComposite.setOrigin(0, currentPosition.y - rectangle.y);

								}
							}catch(Exception e){
								e.printStackTrace();
							}*/
						}
					});

				}
			}
		}, 0 , 500, TimeUnit.MILLISECONDS);
		/*
		jcTerm.addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				P
			}
		});*/

		jcTerm.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				stop();
			}			
		});

		jcTerm.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR){
					if((connection == null || connection.isClosed()) && sessionThing != null){
						try {
							start(sessionThing);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		});


		mainComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				mainComposite.layout();
				mainComposite.getParent().layout();
				scrolledComposite.layout();
				jcTerm.update();
				jcTerm.redraw();
			}
		});
	}

	public void resize(){
		jcTerm.setSize(scrolledComposite.getClientArea().width, 6000);
		scrolledComposite.setMinSize(jcTerm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public Composite getMainComposite(){
		return mainComposite;
	}

	public void setSize(Point size){
		jcTerm.setSize(size);
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public Session getSession(){
		return session;
	}
	
	private void closeConnection(){
		try{
			if(this.connection != null){
				this.connection.close();
			}
		}catch(Exception e){
			logger.error("close connection error", e);
		}
	}
	
	private void closeSession(){
		try{
			if(this.session != null && this.closeSession){
				this.session.disconnect();
			}
		}catch(Exception e){
			logger.error("close session error", e);
		}
	}
	
	/**
	 * 通过SessionThing启动一个连接，在结束或启动一个新连接时会自动关闭会话。
	 * 
	 * @param sessionThing 会话事物
	 * @throws JSchException
	 * @throws IOException
	 */
	public void start(Thing sessionThing) throws JSchException, IOException{
		this.sessionThing = sessionThing;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Session session = sessionThing.doAction("create", actionContext);
					if (session != null) {
						start(session, true);
					}
				}catch(final Exception e){
					jcTerm.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								String stackTrace = ExceptionUtil.toString(e);
								Color oldColor = jcTerm.getForeground();
								Color newColor = new Color(jcTerm.getDisplay(), 248,248, 255);
								jcTerm.setForeground(newColor);
								int y = 0 + jcTerm.getCharHeight();
								for(String line : stackTrace.split("[\n]")) {
									if(line.startsWith("\t")){
										line = "    " + line;
									}
									jcTerm.drawString(line, 0, y);
									y = y + jcTerm.getCharHeight() + 3;
								}
								jcTerm.setForeground(oldColor);
								newColor.dispose();
								jcTerm.update();
							}catch(Exception e){
							}
						}
					});
				}
			}
		}).start();

	}

	public void sendCommand(String command){
		jcTerm.sendCommand(command);
	}
		
	/**
	 * 启动一个连接。
	 * 
	 * @param session 会话
	 * @param closeSession 是否在结束时自动关闭会话
	 * @throws JSchException
	 * @throws IOException
	 */
	public void start(Session session, boolean closeSession) throws JSchException, IOException{
		//先关闭原有的连接和会话
		if(session != this.session) {
			closeConnection();
			closeSession();
		}
				
		this.session = session;
		this.closeSession = closeSession;

		new Thread(new Runnable(){
			public void run(){
				try {
					connection = new TermConnection(jcTerm, session);
					jcTerm.start(connection);
				}catch(final Exception e){
					jcTerm.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								String stackTrace = ExceptionUtil.toString(e);
								Color oldColor = jcTerm.getForeground();
								Color newColor = new Color(jcTerm.getDisplay(), 248,248, 255);
								jcTerm.setForeground(newColor);
								int y = 0 + jcTerm.getCharHeight();
								for(String line : stackTrace.split("[\r\n]")) {
									if(line.startsWith("\t")){
										line = "    " + line;
									}
									jcTerm.drawString(line, 0, y);
									y = y + jcTerm.getCharHeight() + 3;
								}
								jcTerm.setForeground(oldColor);
								newColor.dispose();
								jcTerm.update();
							}catch(Exception e){
							}
						}
					});

				}
			}
		}).start();		
	}
	
	public void stop(){
		closeConnection();
		closeSession();
	}

	public JCTermSWT getJcTerm() {
		return jcTerm;
	}

	public static JCTermSWT create(ActionContext actionContext) throws JSchException, IOException{
		Thing self = (Thing) actionContext.get("self");
				
		//创建JCTerm
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		Composite parent = (Composite) actionContext.get("parent");
		JCTerm jcTerm = new JCTerm(parent, style, actionContext);

		//创建子节点
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", jcTerm.mainComposite);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//启动会话
		Session session = (Session) self.doAction("getSession", actionContext);
		if(session != null){
			jcTerm.start(session, self.getBoolean("closeSession", true));
		}
		
		Designer.attach(jcTerm.jcTerm, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), jcTerm);
		return jcTerm.jcTerm;
	}
	
	public static Session getSession(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//从sessionThing获取
		String sessionThing = self.getStringBlankAsNull("sessionThing");
		if(sessionThing != null){
			Thing thing = World.getInstance().getThing(sessionThing);
			if(thing != null){
				return (Session) thing.doAction("create", actionContext);
			}
		}
		
		//从定义的子节点获取
		Thing sessions = self.getThing("Sessions@0");
		if(sessions != null && sessions.getChilds().size() > 0){
			Thing thing = sessions.getChilds().get(0);
			return (Session) thing.doAction("create", actionContext);
		}
		
		return null;
	}
}
