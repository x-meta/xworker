package xworker.net.jsch;


import java.io.IOException;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
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

import xworker.swt.design.Designer;

public class JCTerm {
	private static Logger logger = LoggerFactory.getLogger(JCTerm.class);
	
	private JCTermSWT jcTerm;
	
	private Connection connection;
	//是否在结束时自动关闭Session
	private boolean closeSession = false;
	private Session session;
	private ActionContext actionContext;
		
	public JCTerm(Composite parent, int style, ActionContext actionContext){
		/*
		Frame frame = SWT_AWT.new_Frame(parent);
		JRootPane root=new JRootPane();
        frame.add(root);
        jcTerm=new JCTermSwing();
        root.getContentPane().add((JPanel)jcTerm);
        */
		this.actionContext = actionContext;
        jcTerm = new JCTermSWT(parent);
		jcTerm.setSize(new Point(1024, 768));
		
		jcTerm.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				stop();
			}			
		});
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
		Session session = (Session) sessionThing.doAction("create", actionContext);
		if(session != null){
			this.start(session, true);
		}
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
		closeConnection();
		closeSession();
				
		this.session = session;
		this.closeSession = closeSession;
		
		connection = new TermConnection(jcTerm, session);
		new Thread(new Runnable(){
			public void run(){
				jcTerm.start(connection);
			}
		}).start();		
	}
	
	public void stop(){
		closeConnection();
		closeSession();
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
			bindings.put("parent", jcTerm.jcTerm);
			
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
