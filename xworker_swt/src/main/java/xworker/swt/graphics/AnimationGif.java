package xworker.swt.graphics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;

import xworker.lang.executor.Executor;
import xworker.swt.util.SwtUtils;

public class AnimationGif {
	private static Timer timer =new Timer("SWT Animation Timer");
	private static final String TAG = AnimationGif.class.getName();
	
	public static void create(ActionContext actionContext){
		Control parent = (Control) actionContext.get("parent");
		Thing self = (Thing) actionContext.get("self");
		String gifPath = self.getStringBlankAsNull("gifPath");
		if(gifPath == null){
			return;
		}
		
		AnimationGif agif = createAnimationGif(parent, gifPath);
		if(agif != null){
			agif.run();
		}
	}
	
	public static AnimationGif createAnimationGif(Control control, String path){
		try{
	    	//如果是http的数据
	    	URL url = new URL(path);
	    	if(url.getProtocol() != null && url.getProtocol().startsWith("http")){
	    		URLConnection con = url.openConnection();
	    		try{
		    		con.connect();
	    			return create(control, con.getInputStream());
	    		}catch(Exception ee){
	    			Executor.warn(TAG, "Create AnimationGif from http error", ee);
	    		}
	    	}
	    }catch(Exception e){		    			    	
	    }
	    
        //直接从文件系统取
	    File file = new File(path);
	    
	    //从默认图片路径
	    String imageFilePath = null;
	    if(!file.exists() || !file.isFile()){
	        Thing globalConfig = World.getInstance().getThing(ThingRegistor.getPath("_xworker_globalConfig"));
	        if(globalConfig != null){
	        	imageFilePath = globalConfig.getString("imagePath") + "/" + imageFilePath;
	        	file = new File(imageFilePath);
	        }
	    }		    
	    
	    //从world根目录
	    World world = World.getInstance();
	    if(!file.exists() || !file.isFile()){
	        imageFilePath = world.getPath() + "/" + path;
	        file = new File(imageFilePath);
	    }
	    
	    //从world/webroot下取
	    if(!file.exists() || !file.isFile()){
	        imageFilePath = world.getPath() + "/webroot/" + path;
	        file = new File(imageFilePath);
	    }
	    
	    //从系统资源的变量上下文取
	    if(!file.exists() || !file.isFile()){
	    	try {
	    		if(!path.startsWith("/") || !path.startsWith("\\")){
	    			imageFilePath = "/" + path;
	    		}else{
	    			imageFilePath = path;
	    		}
				InputStream rin = world.getResourceAsStream(imageFilePath);
				try{
					if(rin != null){
						return create(control, rin);
					}
				}finally{
					if(rin != null){
						rin.close();
					}
				}
			} catch (IOException e) {					
			}
	    	
	    	return null;
	    }else{
	    	return create(control, imageFilePath);
	    }
	}
	
	private static AnimationGif create(Control control, InputStream in){
		ImageLoader loader = new ImageLoader();
		loader.load(in);
		if(loader.data == null || loader.data.length == 0){
			return null;
		}
		
		return new AnimationGif(control, loader);
	}
	
	private static AnimationGif create(Control control, String fileName){
		ImageLoader loader = new ImageLoader();
		loader.load(fileName);
		if(loader.data == null || loader.data.length == 0){
			return null;
		}
		return new AnimationGif(control, loader);
	}
	
	Control control;
	ImageLoader loader;
	Image image;
	GC gc;
	int imageNumber = -1;
	
	public AnimationGif(Control control, ImageLoader loader){
		this.control = control;
		this.loader = loader;
		image = new Image(control.getDisplay(), loader.data[0]);
	    
	    gc = new GC(image);
	    if(SwtUtils.isRWT()) {
	    	control.addListener(SWT.Paint, new Listener() {

				@Override
				public void handleEvent(Event event) {
					event.gc.drawImage(image, 0, 0);
				}
	    		
	    	});
	    }else {
			control.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent event) {
					event.gc.drawImage(image, 0, 0);
				}
			});
	    }
		
		this.control.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				if(image != null){
					image.dispose();
				}
				if(gc != null){
					gc.dispose();
				}
			}			
		});
	}
	
	public void run(){
		if(loader.data != null && loader.data.length > 0){
			if(imageNumber == -1){
				imageNumber++;
				timer.schedule(new AnimationGifTask(this), 0);				
			}else{
				long delay = loader.data[imageNumber].delayTime;
				imageNumber++;
				if(delay == 0){
					delay = 10;
				}
				
				if(imageNumber > loader.data.length - 1){
					imageNumber = 0;
				}
				timer.schedule(new AnimationGifTask(this), delay * 10);
			}
		}
	}
	
	static class AnimationGifTask extends TimerTask{
		AnimationGif agif;
		
		public AnimationGifTask(AnimationGif agif){
			this.agif = agif;
		}
		
		public void run(){
			try{
				if(agif.control.isDisposed()){
					return;
				}
				
				agif.control.getDisplay().asyncExec(new Runnable(){
					public void run(){
						if(agif.control.isDisposed()){
							return;
						}
						
						// Draw the new data onto the image
						ImageData nextFrameData = agif.loader.data[agif.imageNumber];
						Image frameImage = new Image(agif.control.getDisplay(), nextFrameData);
						agif.gc.drawImage(frameImage, nextFrameData.x, nextFrameData.y);
						frameImage.dispose();
						agif.control.redraw();
						agif.run();
					}
				});
			}catch(Exception e){
				
			}
		}
	}
}
