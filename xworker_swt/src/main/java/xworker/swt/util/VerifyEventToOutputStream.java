package xworker.swt.util;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class VerifyEventToOutputStream implements VerifyKeyListener, IAction{
	private static Logger logger = LoggerFactory.getLogger(VerifyEventToOutputStream.class);
	
	StyledText text;
	OutputStream out = null;
	boolean doit = false;
	
	public VerifyEventToOutputStream() {
		
	}
	public void init(StyledText text, boolean doit, OutputStream out){
		this.text = text;
		this.out = out;
		this.doit = doit;
	}
	
	@Override
	public void verifyKey(VerifyEvent event) {
		if(doit == false) {
			final ControlEditor editor = new ControlEditor (text);
			final Text inputText = new Text(text, SWT.BORDER);
			if(event.text != null) {
				inputText.setText(event.text);
			}
			inputText.addListener(SWT.DefaultSelection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					try {
						out.write(inputText.getText().getBytes());
						out.write(13);
						out.write(10);		
						out.flush();
					}catch(Exception e) {					
					}
					inputText.dispose();
					editor.dispose();
				}			
			});
			inputText.addListener(SWT.FocusOut, new Listener() {
				@Override
				public void handleEvent(Event event) {
					inputText.dispose();
					editor.dispose();
				}
			});
			editor.horizontalAlignment = SWT.CENTER;
			editor.verticalAlignment = SWT.BOTTOM;
			editor.grabHorizontal = false;
			editor.grabVertical = false;
			Point size = inputText.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			editor.minimumWidth = size.x + 50;
			editor.minimumHeight = size.y;
			editor.setEditor (inputText);
			inputText.forceFocus();
			event.doit = false;
		}else {
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
	}
	@Override
	public Object run(Thing thing, ActionContext actionContext) {
		StyledText text = thing.doAction("getStyledText", actionContext);
		OutputStream out = thing.doAction("getOutputStream", actionContext);
		this.init(text, thing.getBoolean("eventDoit"), out);
		text.addVerifyKeyListener(this);
		return this;
	}
	

}
