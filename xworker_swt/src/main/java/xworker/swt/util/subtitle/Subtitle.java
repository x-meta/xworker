package xworker.swt.util.subtitle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.swt.design.Designer;
import xworker.swt.util.ResourceManager;
import xworker.util.XWorkerUtils;

/**
 * 字幕。
 * 
 * @author Administrator
 *
 */
public class Subtitle implements PaintListener, Listener, DisposeListener, Runnable{
	List<SubtitleText> texts = new ArrayList<SubtitleText>();
	int index = 0;
	Font font;
	int lineSpacing = 10;
	Color color;
	Canvas canvas;
	Shell shell;
	Display display;
	Image transparentBackgroundImage = null;
	String fontStr;
	String colorStr;
	ActionContext ac = new ActionContext();
	long lastTime = 0;
	boolean exited = false;
	
	public Subtitle(String texts, String fontStr, String colorStr, int lineSpacing, boolean refresh){
		for(String t : texts.split("[\n]")){
			this.texts.add(new SubtitleText(t));
		}
		this.fontStr = fontStr;
		this.colorStr = colorStr;
		this.lineSpacing = lineSpacing;
		this.display = Designer.getExplorerDisplay();
		Designer.getExplorerDisplay().addFilter(SWT.KeyDown, this);
		
		initShell();
		
		if(refresh){
			new Thread(this, "subtitle").start();
		}
	}
	
	public void run(){
		
		//定时刷新
		while(exited == false){			
			try{
				Thread.sleep(1000);
				
				this.display.asyncExec(new Runnable(){
					public void run(){
						update();
					}
				});
				
			}catch(Exception e){	
				
			}
		}
		
		exit();
		
	}
	
	public void initShell(){
		if(shell != null && shell.isDisposed() == false){
			//shell.dispose();
			canvas.dispose();
			//shell.setAlpha(0);
			/*
			Point size = canvas.getSize();
			Image newImage = new Image(canvas.getDisplay(), size.x, size.y);
			GC gc = new GC(newImage);
			Point disPoint = canvas.toDisplay(canvas.getLocation());
			gc.copyArea(disPoint.x, disPoint.y, size.x, size.y, 0, 0);
			gc.dispose();
			GC cgc = new GC(canvas);
			cgc.drawImage(newImage, 0, 0);
			cgc.dispose();
			newImage.dispose();
			*/
			shell.setVisible(false);
			
			shell.update();
			
		}else{
		
			shell = new Shell((Shell) XWorkerUtils.getIDEShell(), SWT.NO_BACKGROUND | SWT.NO_TRIM 
					| SWT.NO_FOCUS | SWT.TOP | SWT.TOOL);
			shell.setLayout(new FillLayout());
			shell.addDisposeListener(this);
			
			ac.put("parent", shell);
			if(fontStr != null && !"".equals(fontStr)){
				font = ResourceManager.createFont(shell, fontStr, ac);			
			}
			if(colorStr != null && !"".equals(colorStr)){
				color = ResourceManager.createColor(shell, colorStr, ac);
			}
			
			
		}
				
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		canvas.addPaintListener(this);
		shell.setAlpha(255);
		shell.layout();
		//canvas.redraw();
		
		shell.setVisible(true);
		//XWorkerUtils.getIDEShell().setFocus();
	}
	
	public void exit(){
		exited = true;
		final Subtitle subtitle = this;
		Designer.getExplorerDisplay().asyncExec(new Runnable(){
			public void run(){
				Designer.getExplorerDisplay().removeFilter(SWT.KeyDown, subtitle);
				if(!shell.isDisposed()){
					shell.setVisible(false);
					shell.dispose();
				}
			}
		});
	}
	
	public void pre(){
		if(index <= 0){
			return;
		}
		
		index--;
		update();
	}
	
	public void next(){
		index ++;
		if(index >= texts.size()){
			//已到最后，退出
			exit();
		}
		
		update();
	}
	
	public void update(){
		initShell();
		
		if(canvas != null && !canvas.isDisposed()){
			canvas.redraw();
		}
	}
	
	
	@Override
	public void paintControl(PaintEvent e) {
		String text = null;
		if(index < texts.size()){
			text = texts.get(index).text;
		}
		if(text == null){
			return;
		}
		
		
		GC gc = e.gc;
		if(font != null){
			gc.setFont(font);
		}

		if(color != null){
			gc.setForeground(color);
		}

		//把字符串分行
		List<String> lines = new ArrayList<String>();
		int shellWidth = 0;
		int shellHeight = 0;		
		String t = text.replaceAll("\\\\n", "\n");
		for(String l : t.split("[\\n]")){
			l = l.trim();
			if(l.equals("")){
				continue;
			}else{		
				
				while(true){
					int index = l.length();
					while(gc.textExtent(l.substring(0, index)).x > 800){
						index--;
					}
					if(l.lastIndexOf(" ", index) > 0 && index < l.length()){
						index = l.lastIndexOf(" ", index);
					}
	
					String ltr = l.substring(0, index);
					Point textSize = gc.textExtent(ltr);
					lines.add(ltr);
					if(shellWidth < textSize.x){
						shellWidth = textSize.x;
					}
					shellHeight = shellHeight + textSize.y + lineSpacing;
					if(index == l.length()){
						break;
					}else{
						l = l.substring(index, l.length());
					}
				}
			}
		}
		
		shell.setSize(shellWidth + 50, shellHeight + 50);
		Shell explorerShell = (Shell) XWorkerUtils.getIDEShell();
		Rectangle explorerRec = explorerShell.getClientArea();
		Point explorerLoc = explorerShell.getLocation();
		int shellLocX = explorerLoc.x  + (explorerRec.width - shellWidth) / 2;
		int shellLocY = explorerLoc.y + (explorerRec.height - shellHeight) / 2;
		shell.setLocation(shellLocX, shellLocY + (int) (explorerRec.height * 0.45));
		
		Rectangle rec = canvas.getClientArea();
		if(transparentBackgroundImage != null){
			transparentBackgroundImage.dispose();
		}
		
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
	    Color black = display.getSystemColor(SWT.COLOR_BLACK);
	    PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });
	    final ImageData sourceData = new ImageData(rec.width, rec.height, 1, palette);
	    sourceData.transparentPixel = 0;
	    transparentBackgroundImage = new Image(shell.getDisplay(), sourceData);
	    gc.drawImage(transparentBackgroundImage, 0, 0);
	    
	    gc.setAlpha(255);
		int top = 0;
		Color borderColor = display.getSystemColor(SWT.COLOR_DARK_GREEN);
		for(String l : lines){
			Point textSize = gc.textExtent(l);
			int left = (shellWidth - textSize.x) / 2;
			Path path = new Path(gc.getDevice());
			path.addString(l, left, top, gc.getFont());

			gc.setAntialias(SWT.ON);
			gc.setBackground(borderColor);
			gc.fillPath(path);
			gc.setForeground(color);
	        gc.drawPath(path);
	        //gc.setForeground(black);
	        //
	        //gc.setForeground(color);
            path.dispose();  
			//gc.drawText(l, left, top, true);
			
			top = top + lineSpacing + textSize.y;
		}
	}
	
	static class SubtitleText{
		String text;
		int delay = 0;
		
		public  SubtitleText(String text){
			String l[] = text.split("[|]");
			if(l.length > 1){
				delay = Integer.parseInt(l[0]);
				this.text = l[1];
			}else{
				delay = 0;
				this.text = l[0];
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		//System.out.println(event.keyCode );
		if(event.stateMask == SWT.CTRL && (event.keyCode == 'x' || event.keyCode == 'X' || event.keyCode == 'q' || event.keyCode == 'Q')){
			//下一步
			if(System.currentTimeMillis() -  lastTime < 1000){
				return;
			}
			
			next();
			lastTime = System.currentTimeMillis();
			event.doit = false;
		}else if(event.stateMask == SWT.CTRL && (event.keyCode == 'z' || event.keyCode == 'z' || event.keyCode == 'e' || event.keyCode == 'E')){
			//退出
			if(System.currentTimeMillis() -  lastTime < 1000){
				return;
			}
			exit();
			lastTime = System.currentTimeMillis();
			event.doit = false;
		}else if(event.stateMask == SWT.CTRL && (event.keyCode == 'c' || event.keyCode == 'C' || event.keyCode == 'w' || event.keyCode == 'W')){
			//上一步
			pre();
			event.doit = false;
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {		
		if(transparentBackgroundImage != null){
			transparentBackgroundImage.dispose();
		}
		if(font != null){
			font.dispose();
		}
		if(color != null){
			color.dispose();
		}
	}
}
