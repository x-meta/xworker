package xworker.libdgx.tools.textureAtlasEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.ResizeUtil;

public class TextureAtlasInfo {
	public static final int DRAG_MOVE = 1;	
	public static final int DRAG_RESIZE_LEFT = 2;
	public static final int DRAG_RESIZE_RIGHT = 4;
	public static final int DRAG_RESIZE_TOP = 8;
	public static final int DRAG_RESIZE_BOTTOM = 16;
	public static final int DRAG_CREATE = 32;
	
	List<Page> pages = new ArrayList<Page>();
	Page currentPage;
	File packFile;
	int dragStartX;
	int dragStartY;
	boolean dragging = false;
	int dragType;
	boolean changed = false;
	Shell shell;
	int imageWidth = 100;
	int imageHeight = 100;
	
	public TextureAtlasInfo(){		
		packFile = null;
	}
	
	public TextureAtlasInfo(File packFile){
		this.packFile = packFile;
	}
	
	public void setShell(Shell shell){
		this.shell = shell;
	}
	
	public void changed(){
		if(shell != null){
			String title = "TextureAtlas编辑器-";
			if(packFile == null){
				title = title + "新文件";
			}else{
				title = title + packFile.getAbsolutePath();
			}
			if(isChanged()){
				title = title + "*";
			}
			shell.setText(title);
		}
	}
	
	public boolean isNew(){
		return packFile == null;
	}
	
	public void save() throws IOException{
		save(packFile);
		setChanged(false);
		
		this.changed();
	}
	
	public void setChanged(boolean changed){
		this.changed = changed;
		
		for(Page page : pages){
			page.setChanged(changed);
		}
	}
	public void setCurrentPage(Page page){
		if(currentPage != null && currentPage != page){
			currentPage.dispose();
		}
		
		currentPage = page;
	}
	
	public void draw(Canvas canvas, GC gc){
		//先填充画面
		Color oldColor = gc.getBackground();
		gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(canvas.getClientArea());
		gc.setBackground(oldColor);
		
		if(currentPage != null){
			currentPage.draw(gc, canvas);
		}
	}
	
	public void handleCanvasEvent(Event event, ActionContext actionContext){
		if(currentPage == null){
			return;
		}
		
		switch(event.type){
		case SWT.MouseDown:
			if(event.button == 1){
				dragging = true;
			}
			
			if((dragType & TextureAtlasInfo.DRAG_CREATE) == TextureAtlasInfo.DRAG_CREATE){
				if(currentPage.selectByXY(event.x, event.y, actionContext) == false){
					Button deleteRegionButton = (Button) actionContext.get("deleteRegionButton");
					deleteRegionButton.setEnabled(false);
				}else{
					Button deleteRegionButton = (Button) actionContext.get("deleteRegionButton");
					deleteRegionButton.setEnabled(true);
				}
			}
			dragStartX = event.x;
			dragStartY = event.y;
			break;
		case SWT.MouseUp:
			dragging = false;
			break;
		case SWT.MouseMove:
			if(currentPage == null){
				return;
			}
			if(dragging){
				int width = event.x - dragStartX;
				int height = event.y - dragStartY;
								
				if((dragType & TextureAtlasInfo.DRAG_CREATE) == TextureAtlasInfo.DRAG_CREATE){
					if(dragStartX > currentPage.maxWidth || dragStartY > currentPage.maxHeight){
						dragType = 0;
						dragging = false;
						return;
					}
					
					//如果是新建，那么新建立一个
					Region region = currentPage.addRegion("newRegion");
					region.name = region.name + region.id;
					region.setXy(dragStartX + "," + dragStartY);
					region.setSize(width + "," + height);
					
					dragType = TextureAtlasInfo.DRAG_RESIZE_RIGHT | TextureAtlasInfo.DRAG_RESIZE_BOTTOM;
					((Control) event.widget).setCursor(event.display.getSystemCursor(SWT.CURSOR_SIZESE));
					
					currentPage.setCurrentRegion(region);
					
					//重新加载Store
					Thing regionDataStore = (Thing) actionContext.get("regionDataStore");
					regionDataStore.doAction("load", actionContext);
					
					//表格选中新的条目
					Table table = (Table) actionContext.get("packTable");
					currentPage.tableSelectCurrentRegion(table);
					
					//重新绘制屏幕
					Canvas imageCanvas = (Canvas) actionContext.get("imageCanvas");
					imageCanvas.redraw();
				}else{
					//当前区域处理事件
					currentPage.currentRegion.resize(dragType, width, height, actionContext);
					
					//重新绘制屏幕
					Canvas imageCanvas = (Canvas) actionContext.get("imageCanvas");
					imageCanvas.redraw();
				}
				
				dragStartX = event.x;
				dragStartY = event.y;
			}else{	
				Region region = null;
				if(currentPage.currentRegion != null && currentPage.currentRegion.isContains(event.x, event.y)){
					region = currentPage.currentRegion;
				}else{
					region = currentPage.getRegionAt(event.x, event.y);
				}
				if(region != null  && region == currentPage.currentRegion){
					//如果鼠标在当前选中的page上，那么
					String cursorType = region.getCursorTtoe(event.display, event.x, event.y);
					Cursor cursor = ResizeUtil.getCursor(event.display, cursorType);
					if(cursor == null){
						cursor = event.display.getSystemCursor(SWT.CURSOR_SIZEALL);				
						
						dragType = TextureAtlasInfo.DRAG_MOVE;
					}else{
						if(ResizeUtil.BOTTOM.equals(cursorType)){
							dragType = DRAG_RESIZE_BOTTOM;
						}
						
						if(ResizeUtil.BOTTOM_LEFT.equals(cursorType)){
							dragType = DRAG_RESIZE_BOTTOM | DRAG_RESIZE_LEFT;
						}
						
						if(ResizeUtil.BOTTOM_RIGHT.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_BOTTOM | TextureAtlasInfo.DRAG_RESIZE_RIGHT;
						}
						
						if(ResizeUtil.LEFT.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_LEFT;
						}
						
						if(ResizeUtil.RIGHT.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_RIGHT;
						}
						
						if(ResizeUtil.TOP.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_TOP;
						}
						
						if(ResizeUtil.TOP_LEFT.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_TOP | TextureAtlasInfo.DRAG_RESIZE_LEFT;
						}
						
						if(ResizeUtil.TOP_RIGHT.equals(cursorType)){
							dragType = TextureAtlasInfo.DRAG_RESIZE_TOP | TextureAtlasInfo.DRAG_RESIZE_RIGHT;
						}						
					}
					((Control) event.widget).setCursor(cursor);
				}else{
					((Control) event.widget).setCursor(event.display.getSystemCursor(SWT.CURSOR_ARROW));
					
					dragType = TextureAtlasInfo.DRAG_CREATE;
				}
			}
		}
		
		((Control) event.widget).redraw();
	}
	
	public void dispose(){
		if(currentPage != null){
			currentPage.dispose();
		}
	}
	
	public void load(BufferedReader br) throws IOException{
		String line = null;
		
		while((line = br.readLine()) != null){
			line = line.trim();
			if(line.equals("")){
				continue;
			}
			
			Page page = addPage(line);
			page.load(br);
			
			if(currentPage == null){
				currentPage = page;
			}
		}
	}
	
	public void write(PrintWriter pw){
		for(Page page : pages){
			pw.println();
			page.write(pw);
		}
	}
	
	public static TextureAtlasInfo load(File file) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			
			TextureAtlasInfo info = new TextureAtlasInfo(file);

			info.load(br);
			
			info.setChanged(false);
			return info;
		}finally{
			fin.close();
		}
	}
	
	public void save(File file) throws IOException{
		FileOutputStream fout = new FileOutputStream(file);
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(fout));
			write(pw);
			pw.flush();
		}finally{
			fout.close();
		}
	}
	
	public List<Page> getPages(){
		return pages;
	}
	
	public Page addPage(Page page){
		pages.add(page);
		
		changed = true;
		this.changed();
		return page;
	}
	
	public Page addPage(String imageFile){
		Page page = new Page(packFile, this);
		page.setFileName(imageFile);
		
		return addPage(page);
	}
		
	public void setCurrentPage(String fileName){
		for(Page page : pages){
			if(page.getFileName().equals(fileName)){
				if(currentPage != page){
					this.setCurrentPage(page);
					return;
				}
			}
		}		
	}
	
	public void removeCurrentPage(){
		if(currentPage != null){
			for(int i=0; i<pages.size(); i++){
				if(currentPage == pages.get(i)){
					int index = i - 1;
					if(index < 0){
						index = i + 1;
					}
					if(index < pages.size()){
						setCurrentPage(pages.get(index));
						pages.remove(i);
						return;
					}
				}
			}
			
			currentPage.dispose();
		}
		
		changed = true;
	}
	
	public boolean isChanged(){
		if(changed){
			return true;
		}else{
			for(Page page : pages){
				if(page.isChanged()){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getCurrentPageIndex(){
		if(currentPage != null){
			for(int i=0; i<pages.size(); i++){
				if(currentPage == pages.get(i)){
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public static String[] readValues(String value){
		int index = value.indexOf(":");
		value = value.substring(index + 1, value.length());
		String[] vs = value.split("[,]");
		for(int i=0; i<vs.length; i++){
			vs[i] = vs[i].trim();
		}
		
		return vs;
	}
	
	public static String readValue(String value){
		int index = value.indexOf(":");
		if(index == -1){
			return value;
		}
		
		value = value.substring(index + 1, value.length());		
		
		return value.trim();
	}

	public Page getCurrentPage() {
		return currentPage;
	}

	public File getPackFile() {
		return packFile;
	}

	public void setPackFile(File packFile) {
		this.packFile = packFile;
	}
	
	public String toString(){
		String str = null;
		if(this.currentPage != null){
			if(currentPage.currentRegion != null){
				str = currentPage.currentRegion.name + "|" + currentPage.currentRegion.index;
			}
		}
		
		return str;
	}
}
