package xworker.net.jsch;

import java.io.InputStream;
import java.io.OutputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Shell {
	public static Object run(ActionContext actionContext) throws JSchException{
		Thing self = (Thing) actionContext.get("self");
		Session session = (Session) self.doAction("getSession", actionContext);
		InputStream in = (InputStream) self.doAction("getInputStream", actionContext);
		OutputStream out = (OutputStream) self.doAction("getOutputStream", actionContext);
	    Channel channel=session.openChannel("shell");
	    channel.setInputStream(in);
	    channel.setOutputStream(out);
	    channel.connect(10000);
	    return channel;
	}
	
	public static Object getInputStream(ActionContext actionContext) throws JSchException{
		Thing self = (Thing) actionContext.get("self");
		String inputStream = self.getStringBlankAsNull("inputStream");
		if(inputStream != null){
			return (InputStream) actionContext.get(inputStream);
		}else{
			return System.in;
		}
	}
	
	public static Object getOutputStream(ActionContext actionContext) throws JSchException{
		Thing self = (Thing) actionContext.get("self");
		String outputStream = self.getStringBlankAsNull("outputStream");
		if(outputStream != null){
			return (OutputStream) actionContext.get(outputStream);
		}else{
			return System.out;
		}
	}
}
