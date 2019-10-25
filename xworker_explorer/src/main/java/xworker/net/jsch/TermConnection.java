package xworker.net.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class TermConnection implements Connection{
	private Session session;
	private ChannelShell channel;
	private OutputStream out;
	private InputStream in;
	
	public TermConnection(Term term, Session session) throws JSchException, IOException{
		this.session = session;
		
		channel = (ChannelShell) session.openChannel("shell");
		out = channel.getOutputStream();
		in = channel.getInputStream();
		
	    channel.connect(10000);
	    
	    requestResize(term);
	}
	
	@Override
	public void close() {
		channel.disconnect();
		session.disconnect();
	}

	@Override
	public InputStream getInputStream() {
		return in;
	}

	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	@Override
	public void requestResize(Term term) {
		int c=term.getColumnCount();
        int r=term.getRowCount();
        channel.setPtySize(c, r, c*term.getCharWidth(), r*term.getCharHeight());        
	}

}
