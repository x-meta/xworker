package xworker.net.jsch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;
import xworker.util.StringUtils;

public class Exec {
	private static Logger logger = LoggerFactory.getLogger(Exec.class);
	
	public static Session getSession(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String sessionVar = self.getStringBlankAsNull("sessionVar");
		if(sessionVar != null){
			return (Session) OgnlUtil.getValue(self, "sessionVar", actionContext);
		}
		
		String sessionPath= UtilString.getString(self, "sessionPath", actionContext);
		if(sessionPath != null){
			Thing thing = World.getInstance().getThing(sessionPath);
			if(thing != null){
				return (Session) thing.doAction("create", actionContext);
			}
		}
		
		Thing thing = self.getThing("Session@0");
		if(thing != null){
			return (Session) thing.doAction("create", actionContext);
		}
		
		thing = self.getThing("AppSession@0");
		if(thing != null){
			return (Session) thing.doAction("create", actionContext);
		}
		
		return null;
	}
	
	/**
	 * 相关事物定义的session是否需要关闭，如果从变量得到的session不需要关闭，其他情况都需要关闭。
	 * 
	 * @param self
	 * @return
	 */
	public static boolean isSessionNeedClose(Thing self){
		String sessionVar = self.getStringBlankAsNull("sessionVar");
		if(sessionVar != null && !self.getBoolean("closeSession")){
			return false;
		}else{
			return true;
		}
	}
	
	public static String getCommand(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		return StringUtils.getString(self, "command", actionContext);
	}
	
	public static Object run(ActionContext actionContext) throws JSchException, OgnlException, IOException, InterruptedException{
		Thing self = (Thing) actionContext.get("self");
		boolean runBackground = self.getBoolean("runBackground");
		
		Session session = (Session) self.doAction("getSession", actionContext);
		if(session == null){
			throw new ActionException("Jsch Session can not be null, path=" + self.getMetadata().getPath());
		}
		String command = (String) self.doAction("getCommand", actionContext);
		if(command == null){
			throw new ActionException("Command can not be null, path=" + self.getMetadata().getPath());
		}
		Channel channel = session.openChannel("exec");
	    ((ChannelExec)channel).setCommand(command.replace("\r", "").trim());
	    
	    channel.setInputStream(null);
	    self.doAction("initIo", actionContext, UtilMap.toMap(new Object[]{"session", session, "channel", channel}));	    
	    InputStream in = channel.getInputStream();
	    channel.connect();
	    		
	    try{
	    	OutputStream outputStream = null;
	    	String outputStreamStr = self.getStringBlankAsNull("outputStream");
			if(outputStreamStr != null){
				outputStream = (OutputStream) OgnlUtil.getValue(self, "outputStream", actionContext);
			}
					
			UserTask task = UserTaskManager.createTask(self, false);
			task.start();
			task.setDetail("session=" + session + "<br/>command=" + command);
			if(runBackground){				
				ExecTask exec = new ExecTask(self, task, session, channel, outputStream, actionContext, self.getBoolean("closeSession"), in);
				new Thread(exec).start();
				return task;
			}else{
				try {
					return self.doAction("afterConnected", actionContext, UtilMap.toMap(new Object[]{"session", session, 
							"channel", channel, "outputStream", outputStream, "in", in, "task", task}));
				}finally {
					task.finished();
				}
			}
	    }finally{
	    	if(!runBackground){
		        channel.disconnect();
		        if(self.getBoolean("closeSession")){
		    	    session.disconnect();
		        }
	    	}
	    }
	}
	
	static class ExecTask implements Runnable{
		UserTask task;
		Thing self;
		OutputStream outputStream;
		Channel channel;
		Session session;
		ActionContext actionContext;
		boolean closeSession = false;
		InputStream in;
		
		public ExecTask(Thing self, UserTask task, Session session, Channel channel, OutputStream outputStream, ActionContext actionContext, boolean closeSession, InputStream in){
			this.self = self;
			this.task = task;
			this.outputStream = outputStream;
			this.channel = channel;
			this.session = session;
			this.actionContext = actionContext;
			this.closeSession = closeSession;
			this.in = in;
		}
		
		public void run(){
			try{
				self.doAction("afterConnected", actionContext, UtilMap.toMap(new Object[]{
						"session", session, "channel", channel, "outputStream", outputStream, "task", task, "in", in}));
			}catch(Exception e){
				logger.error("exec error, path=" + self.getMetadata().getPath(), e);
			}finally{
				task.finished();
				channel.disconnect();
				if(closeSession){
					session.disconnect();
				}				
			}
		}
	}
	
	public static void initIo(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		//Session session = (Session) actionContext.get("session");
		Channel channel = (Channel) actionContext.get("channel");
		String errStream = self.getStringBlankAsNull("errStream");
		if(errStream != null){
			((ChannelExec)channel).setErrStream((OutputStream) OgnlUtil.getValue(self, "errStream", actionContext), true);			 
		}else{
			 ((ChannelExec)channel).setErrStream(System.err, true);
		}
	}
	
	public static Object afterConnected(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		// Session session = (Session) actionContext.get("session");
		Channel channel = (Channel) actionContext.get("channel");
		InputStream in = (InputStream) actionContext.get("in");
		OutputStream out = (OutputStream) actionContext.get("outputStream");
		UserTask task = (UserTask) actionContext.get("task");
		
		boolean returnString = false;
		if(out == null){
			out = new ByteArrayOutputStream();
			returnString = true;
		}
		
		byte[] tmp = new byte[1024];
		while (true) {			
			while(in.available()>0){
				int i = in.read(tmp, 0, 1024);
				if (i <= 0)
					break;
							
				out.write(tmp, 0, i);
				out.flush();
			}
			if (channel.isClosed()) {
				if (in.available() > 0){
					continue;
				}
				
				//logger.info("channel closed, exitStatus=" + channel.getExitStatus() + ", thing=" + self.getMetadata().getPath());
				break;
			}
			
			if(task != null && task.getStatus() == UserTask.CANCEL){
				task.terminated();
				break;
			}
			try{Thread.sleep(100);}catch(Exception ee){}
		}

		if(returnString){
			return new String(((ByteArrayOutputStream) out).toByteArray());
		}else{
			return null;
		}
	}
}
