package xworker.libdgx.tools.textureAtlasEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;

import xworker.dataObject.DataObject;
import xworker.swt.graphics.ImageUtils;
import xworker.swt.util.ResizeUtil;

public class Region {
	public static final int MIN_WIDTH = 10;
	public static final int MIN_HEIGHT = 10;
	String id;
	String name;
	String rotate = "false";
	String xy = "0,0";
	String size = MIN_WIDTH + "," + MIN_HEIGHT;
	String splits;
	String pads;
	String orig = "0,0";
	String offset = "0,0";
	String index = "-1";
	boolean selected = false;	
	boolean changed = false;
	Page page;
	Image regionImage;
	
	public Region(Page page){
		this.page = page;
	}
	
	public void dispose(){
		if(regionImage != null){
			regionImage.dispose();
		}
	}
	
	public void load(BufferedReader br) throws IOException{
		this.rotate = TextureAtlasInfo.readValue(br.readLine());
		this.xy = TextureAtlasInfo.readValue(br.readLine());
		this.size = TextureAtlasInfo.readValue(br.readLine());
		String line = TextureAtlasInfo.readValue(br.readLine());
		if(line.split("[,]").length == 4){
			this.splits = line;
			
			line = TextureAtlasInfo.readValue(br.readLine());
			if(line.split("[,]").length == 4){
				this.pads = line;
			}else{
				this.orig = line;
			}
		}else{
			this.orig = line;
		}
		
		this.offset = TextureAtlasInfo.readValue(br.readLine());
		this.index = TextureAtlasInfo.readValue(br.readLine());
	}
	
	public void write(PrintWriter pw){
		pw.println(name);
		pw.println("  rotate: " + rotate);
		pw.println("  xy: " + xy);
		pw.println("  size: " + size);
		if(splits != null && !"".equals(splits)){
			pw.println("  splits: " + splits);
			
			if(pads != null && !"".equals(pads)){
				pw.println("  pads: " + pads);
			}
		}
		pw.println("  orig: " + orig);
		pw.println("  offset: " + offset);
		pw.println("  index: " + index);
	}
	
	public void exportToDir(File dir){
		if(regionImage != null){
			String fileName = name;
			if(index != null && !"".equals(index)){
				fileName = fileName + "_" + index;
			}
			fileName = fileName + ".png";
			
			File output = new File(dir, fileName);
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[]{regionImage.getImageData()};
			loader.save(output.getAbsolutePath(), SWT.IMAGE_PNG);
		}
	}
	
	public void initImage(Device device){		
		if(regionImage != null){
			regionImage.dispose();
			regionImage = null;
		}
		
		int x, y ,width, height;
		
		if(xy != null && !"".equals(xy)){
			String s[] = xy.split("[,]");
			if(s.length != 2){
				return;
			}
			
			x = Integer.parseInt(s[0].trim());
			y = Integer.parseInt(s[1].trim());
			
			if(size != null && !"".equals(size)){
				s = size.split("[,]");
				if(s.length == 2){
					width = Integer.parseInt(s[0].trim());
					height = Integer.parseInt(s[1].trim());
										
					if(width <=0 || height <=0 ){
						return;
					}
					
					if("true".equals(this.getRotate())){
						regionImage = ImageUtils.rotate(page.image, x, y, width, height);
					}else{
						regionImage = ImageUtils.clip(page.image, x, y, width, height);
						/*
						ImageData data = regionImage.getImageData();
						data.palette = page.image.getImageData().palette;
						data.transparentPixel = page.image.getImageData().transparentPixel;
						data.alpha = page.image.getImageData().alpha;
						data.type = page.image.getImageData().type;
						gc = new GC(regionImage);
						gc.drawImage(page.image, x, y, width, height, 0, 0, width, height);
						*/											
					}
				}
			}
		}
	}
	
	/**
	 * 播放动画化时的播放。
	 * 
	 * @param gc
	 */
	public void drawImage(GC gc){
		if(regionImage != null){					
			if(offset != null && !"".equals(offset)){
				String[] s = offset.split("[,]");
				if(s.length == 2){
					int offsetX = Integer.parseInt(s[0].trim());
					int offsetY = Integer.parseInt(s[1].trim());
					
					gc.drawImage(regionImage, offsetX, offsetY);
				}
			}else{
				gc.drawImage(regionImage, 0, 0);
			}
		}

	}
		
	public void drawRegion(GC gc){
		int x, y ,width, height;
		
		if(xy != null && !"".equals(xy)){
			String s[] = xy.split("[,]");
			if(s.length != 2){
				return;
			}
			
			x = Integer.parseInt(s[0].trim());
			y = Integer.parseInt(s[1].trim());
			
			if(size != null && !"".equals(size)){
				s = size.split("[,]");
				if(s.length == 2){
					width = Integer.parseInt(s[0].trim());
					height = Integer.parseInt(s[1].trim());
					
					gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
					gc.drawRectangle(x, y, width, height);
					if(this.isSelected()){
						int alpha = gc.getAlpha();
						try{
							gc.setAlpha(128);
							gc.fillRectangle(x, y, width, height);
							
							//画缩放
							ResizeUtil.draw(gc, x, y, width, height);
						}finally{
							gc.setAlpha(alpha);
						}
						
						
					}
				}
			}
		}
	}
	
	public String getCursorTtoe(Display display, int x1, int y1){
		int x, y ,width, height;
		
		if(xy != null && !"".equals(xy)){
			String s[] = xy.split("[,]");
			if(s.length != 2){
				return null;
			}
			x = Integer.parseInt(s[0].trim());
			y = Integer.parseInt(s[1].trim());
			
			if(size != null && !"".equals(size)){
				s = size.split("[,]");
				if(s.length != 2){
					return null;
				}
				width = Integer.parseInt(s[0].trim());
				height = Integer.parseInt(s[1].trim());
				
				 return ResizeUtil.getCursorType(display, x1, y1, x, y, width, height);
			}
		}
		
		return null;
	}
	
	
	public boolean isContains(int xx, int yy){
		int x, y ,width, height;
		
		if(xy != null && !"".equals(xy)){
			String s[] = xy.split("[,]");
			if(s.length != 2){
				return false;
			}
			x = Integer.parseInt(s[0].trim());
			y = Integer.parseInt(s[1].trim());
			
			if(size != null && !"".equals(size)){
				s = size.split("[,]");
				if(s.length != 2){
					return false;
				}
				width = Integer.parseInt(s[0].trim());
				height = Integer.parseInt(s[1].trim());
				
				if(xx >= x && xx <= x + width && yy >= y && yy <= y + height){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isChanged(){
		return changed;
	}
	
	public void setChanged(boolean changed){
		this.changed = changed;
	}
	
	public void resize(int dragType, int widthx, int heightx, ActionContext actionContext){
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		
		if(xy != null && !"".equals(xy)){
			String s[] = xy.split("[,]");
			if(s.length != 2){
				x = 0;
				y = 0;
			}else{
				x = Integer.parseInt(s[0].trim());
				y = Integer.parseInt(s[1].trim());
			}
		}
		if(size != null && !"".equals(size)){
			String[] s = size.split("[,]");
			if(s.length != 2){
				width = 18;
				height = 18;
			}else{
				width = Integer.parseInt(s[0].trim());
				height = Integer.parseInt(s[1].trim());
			}
		
		}
		
		if((dragType & TextureAtlasInfo.DRAG_MOVE) == TextureAtlasInfo.DRAG_MOVE){
			x = x + widthx;
			y = y + heightx;
			
			if(x < 0){
				x = 0;
			}
			if(y < 0){
				y = 0;
			}
		}
		if((dragType & TextureAtlasInfo.DRAG_RESIZE_LEFT) == TextureAtlasInfo.DRAG_RESIZE_LEFT){
			if(x + widthx < 0){
				widthx = -x;
			}
			if(width - widthx < MIN_WIDTH){
				widthx = width - MIN_WIDTH;
			}
			x = x + widthx;
			width = width - widthx;				
		}
		
		if((dragType & TextureAtlasInfo.DRAG_RESIZE_RIGHT) == TextureAtlasInfo.DRAG_RESIZE_RIGHT){
			width = width + widthx;
			if(width < MIN_WIDTH){
				width = MIN_WIDTH;
			}
			
			if(x + width > page.maxWidth){
				width = page.maxWidth - x;
			}
		}
		
		if((dragType & TextureAtlasInfo.DRAG_RESIZE_TOP) == TextureAtlasInfo.DRAG_RESIZE_TOP){
			if(y + heightx < 0){
				heightx = -y;
			}
			if(height - heightx < MIN_HEIGHT){
				heightx = height - MIN_HEIGHT;
			}
			y = y + heightx;
			height = height - heightx;		
		}
		if((dragType & TextureAtlasInfo.DRAG_RESIZE_BOTTOM) == TextureAtlasInfo.DRAG_RESIZE_BOTTOM){
			height = height + heightx;
			if(height < MIN_HEIGHT){
				height = MIN_HEIGHT;
			}			
			
			if(y + height > page.maxHeight){
				height = page.maxHeight - y;
			}
		}
		
		xy = x + "," + y;
		size = width + "," + height;
		this.orig = size;
		
		Table table = (Table) actionContext.get("packTable");
		for(int index=0; index<table.getItemCount(); index++){
			TableItem item = table.getItem(index);
			DataObject data = (DataObject) item.getData();
			String name = data.getString("id");
			if(getId().equals(name)){
				item.setText(3, xy);
				item.setText(4, size);
				item.setText(7, orig);
				break;
			}
		}
		
		page.info.changed();
		changed = true;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		changed = true;
		this.name = name;
		page.info.changed();
	}
	
	public String getRotate() {
		return rotate;
	}
	
	public void setRotate(String rotate) {
		changed = true;
		this.rotate = rotate;
		page.info.changed();
	}
	
	public String getXy() {
		return xy;
	}
	
	public void setXy(String xy) {
		changed = true;
		this.xy = xy;
		page.info.changed();
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		changed = true;
		this.size = size;
		page.info.changed();
	}
	
	public String getSplits() {
		return splits;
	}
	
	public void setSplits(String splits) {
		changed = true;
		this.splits = splits;
		page.info.changed();
	}
	
	public String getPads() {
		return pads;
	}
	
	public void setPads(String pads) {
		changed = true;
		this.pads = pads;
		page.info.changed();
	}
	
	public String getOrig() {
		return orig;
	}
	
	public void setOrig(String orig) {
		changed = true;
		this.orig = orig;
		page.info.changed();
	}
	
	public String getOffset() {
		return offset;
	}
	
	public void setOffset(String offset) {
		changed = true;
		this.offset = offset;
		page.info.changed();
	}
	
	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		changed = true;
		this.index = index;
		
		page.info.changed();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
