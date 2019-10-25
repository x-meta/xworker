package xworker.com.google.zxing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class ZXingPainListener implements PaintListener, Listener{
	Thing thing;
	ActionContext actionContext;
	Canvas canvas;
	boolean localMessage = false;
	String message = null;
	Image image = null;
	
	public ZXingPainListener(Canvas canvas, Thing thing, ActionContext actionContext){
		this.canvas = canvas;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public Canvas getCanvas(){
		return canvas;
	}
	
	public Thing getThing(){
		return thing;
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	public void setMessage(String message){
		if(message != null){
			this.message = message;
			localMessage = true;
		}else{
			localMessage = false;
		}
		
		canvas.redraw();
	}
	
	public void redraw(){
		String message = this.message;
		if(!localMessage){
			message = (String) thing.doAction("getMessage", actionContext);
		}
		if(message == null){
			return;
		}
		
		if(image != null && !image.isDisposed()) {
			image.dispose();
		}
		
		BarcodeFormat barcodeFormat = ZXingActions.getBarcodeFormat((String) thing.doAction("getBarcodeFormat", actionContext));
		
		MultiFormatWriter encoder = new MultiFormatWriter();
		Rectangle rec = canvas.getClientArea();
		try {
			System.out.println(message + ":" + barcodeFormat);
			BitMatrix bitMatrix = encoder.encode(message, barcodeFormat, rec.width, rec.height);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", bout);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			
			image = new Image(canvas.getDisplay(), bin);
			canvas.setBackgroundImage(image);
			bin.close();
			bout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		canvas.redraw();
	}
	
	@Override
	public void paintControl(PaintEvent event) {
		String message = this.message;
		if(!localMessage){
			message = (String) thing.doAction("getMessage", actionContext);
		}
		if(message == null){
			return;
		}
		
		BarcodeFormat barcodeFormat = ZXingActions.getBarcodeFormat((String) thing.doAction("getBarcodeFormat", actionContext));
		
		MultiFormatWriter encoder = new MultiFormatWriter();
		Rectangle rec = canvas.getClientArea();
		try {
			BitMatrix bitMatrix = encoder.encode(message, barcodeFormat, rec.width, rec.height);
			//event.gc.setForeground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			//event.gc.fillRectangle(rec);
			event.gc.setForeground(canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			for(int w = 0; w<bitMatrix.getWidth(); w++){
				for(int h = 0; h<bitMatrix.getHeight(); h++){
					if(bitMatrix.get(w, h)){
						event.gc.drawPoint(w, h);
					}
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleEvent(Event event) {
		redraw();
	}

}
