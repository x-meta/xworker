package xworker.swt.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class VerifyEventToInputStream implements VerifyKeyListener, IAction{
	private static Logger logger = LoggerFactory.getLogger(VerifyEventToInputStream.class);
	
	StyledText text;
	PipedInputStream in = null;
	PipedOutputStream out = null;
	boolean doit = false;
	
	public VerifyEventToInputStream() {
	
	}
	
	public void init(StyledText text, boolean doit) throws IOException {
		this.text = text;
		out = new PipedOutputStream();
		in = new PipedInputStream(out);
		this.doit = doit;
	}

	public InputStream getInputStream(){
		return in;
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		try {
			if(event.text != null){
				out.write(event.text.getBytes());
			}else if(event.character != -1){
				byte[] b = new byte[2];
				b[0] = (byte) ((event.character & 0xFF00) >> 8);
				b[1] = (byte) (event.character & 0xFF); 
				
				if(b[0] == 0){
					out.write(b[1]);
					if(b[1] == 13){
						out.write(10);
					}
				}else{
					out.write(b);
				}
			}
			
			out.flush();
			event.doit = doit;
		} catch (IOException e) {
			logger.warn("Remove VerifyKeyListener, exception: " + e.getMessage());
			text.removeVerifyKeyListener(this);
		}
	}

	@Override
	public Object run(Thing thing, ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		StyledText text = self.doAction("getStyledText", actionContext);
		
		try {
			this.init(text, doit);
		} catch (IOException e) {
			throw new ActionException("init error", e);
		}		
		text.addVerifyKeyListener(this);
		return this;
	}
	
}
