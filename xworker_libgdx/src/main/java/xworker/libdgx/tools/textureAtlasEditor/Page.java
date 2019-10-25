package xworker.libdgx.tools.textureAtlasEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;

import xworker.dataObject.DataObject;

public class Page {
	int id = 0;
	String fileName;
	String format;
	String minFilter;
	String maxFilter;
	String repeat;
	
	List<Region> regions = new ArrayList<Region>();
	Region currentRegion;
	Image image;
	
	File packFile;
	boolean changed = false;
	TextureAtlasInfo info;
	int maxWidth;
	int maxHeight;
		
	public Page(File packFile, TextureAtlasInfo info){
		this.packFile = packFile;
		this.info = info;
	}
	
	public void update(Map<String, String> values){
		updateValue(values, "format");
		updateValue(values, "minFilter");
		updateValue(values, "maxFilter");
		updateValue(values, "repeat");
		
		changed = true;
		info.changed();
	}
	
	public void updateValue(Map<String, String> values, String name){
		String v = values.get(name);
		if("format".equals(name)){
			format = v;
		}if("minFilter".equals(name)){
			minFilter = v;
		}if("maxFilter".equals(name)){
			maxFilter = v;
		}if("repeat".equals(name)){
			repeat = v;
		}
	}
	
	public void load(BufferedReader br) throws IOException{
		format = TextureAtlasInfo.readValue(br.readLine());
		
		String[] vs = TextureAtlasInfo.readValues(br.readLine());
		minFilter = vs[0];
		maxFilter = vs[1];
		repeat = TextureAtlasInfo.readValue(br.readLine());
		
		String line = null;
		while((line = br.readLine()) != null){
			line = line.trim();
			if("".equals(line)){
				return;
			}
		
			Region region = addRegion(line);
			region.load(br);
			
			if(currentRegion == null){
				currentRegion = region;
				currentRegion.setSelected(true);
			}
		}
	}
	
	public void resize(int width, int height){
		
	}
	
	public Region getRegionAt(int x, int y){
		for(Region region : regions){
			if(region.isContains(x, y)){
				return region;
			}
		}
		
		return null;
	}
	
	public boolean selectByXY(int x, int y, ActionContext actionContext){
		Region selectedRegion = null;
		for(Region region : regions){
			if(region.isContains(x, y)){
				selectedRegion = region;
			}
		}
		
		if(selectedRegion != null && selectedRegion != currentRegion){
			setCurrentRegion(selectedRegion);
		}else if(selectedRegion == null && currentRegion != null){
			currentRegion.setSelected(false);
			currentRegion = null;
		}
		
		//表格也跟着选择
		Table table = (Table) actionContext.get("packTable");
		if(currentRegion == null){
			table.deselectAll();
			
			return false;
		}else{
			tableSelectCurrentRegion(table);
			
			return true;
		}
	}
	
	public void tableSelectCurrentRegion(Table table){
		for(int index=0; index<table.getItemCount(); index++){
			TableItem item = table.getItem(index);
			DataObject data = (DataObject) item.getData();
			String name = data.getString("id");
			if(currentRegion.getId().equals(name)){
				table.select(index);
				break;
			}
		}
	}
	
	public void dispose(){
		if(image != null && !image.isDisposed()){
			image.dispose();
			image = null;
		}
		
		for(Region region : regions){
			region.dispose();
		}
	}
	
	public void setCurrentRegion(Region region){
		if(currentRegion != null){
			currentRegion.setSelected(false);
		}
		
		currentRegion = region;
		currentRegion.setSelected(true);
	}
	
	public void write(PrintWriter pw){
		File packFile = info.getPackFile();
		File imgFile = getImageFile();
		
		String pf = packFile.getParentFile().getAbsolutePath();
		String imf = imgFile.getAbsolutePath();
		if(imf.startsWith(pf)){
			imf = imf.substring(pf.length(), imf.length());
		}
		imf = imf.replace('\\', '/');
		if(imf.startsWith("/")){
			imf = imf.substring(1, imf.length());
		}
		
		pw.println(imf);
		pw.println("format: " + format);
		pw.println("filter: " + minFilter + "," + maxFilter);
		pw.println("repeat: " + repeat);
		
		for(Region region : regions){
			region.write(pw);
		}
	}
	
	public boolean isChanged(){
		if(changed){
			return true;
		}else{
			for(Region region : regions){
				if(region.isChanged()){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setChanged(boolean changed){
		this.changed = changed;
		
		for(Region region : regions){
			region.setChanged(changed);
		}
	}
	
	public Region addRegion(Region region){
		id++;
		
		region.id = String.valueOf(id);
		regions.add(region);
		
		changed = true;
		return region;
	}
	
	public Region addRegion(String name){
		Region region = new Region(this);
		region.setName(name);
		
		return addRegion(region);
	}
	
	public void removeRegion(String id){
		for(int i=0; i<regions.size(); i++){
			if(regions.get(i).getId().equals(id)){
				regions.remove(i);
				changed = true;
				return;
			}
		}
	}
	
	public File getImageFile(){
		File imageFile = null;
		if(packFile != null){				
			imageFile = new File(packFile.getParentFile(), fileName);
			if(!imageFile.exists()){
				imageFile = new File(fileName);
			}
		}else{
			imageFile = new File(fileName);
		}
		
		return imageFile;
	}
	
	/**
	 * 当Page设置为当前Page时的初始化。
	 * 
	 * @param actionContext
	 */
	public void init(ActionContext actionContext){
		ScrolledComposite scrolledComposite = (ScrolledComposite) actionContext.get("scrolledComposite");
		Canvas imageCanvas = (Canvas) actionContext.get("imageCanvas");
		
		if(image == null || image.isDisposed()){
			File imageFile = getImageFile();
			image = new Image(scrolledComposite.getDisplay(), imageFile.getAbsolutePath());
		}
		
		Rectangle rect = image.getBounds();
		imageCanvas.setSize(rect.width, rect.height);
		scrolledComposite.setMinSize(rect.width, rect.height);
		
		maxWidth = rect.width;
		maxHeight = rect.height;
		//scrolledComposite.update();
		//scrolledComposite.layout();
	}
	
	public void initRegionImage(ActionContext actionContext){
		Canvas imageCanvas = (Canvas) actionContext.get("imageCanvas");
		for(Region region : regions){
			region.initImage(imageCanvas.getDisplay());
		}
	}
	
	/**
	 * 导出分割图到指定的目录下。
	 * 
	 * @param display
	 * @param dir
	 */
	public void exportSplitImageToDir(Display display, File dir){
		for(Region region : regions){
			region.initImage(display);
			
			region.exportToDir(dir);
		}
	}
	
	public void draw(GC gc, Canvas canvas){
		Display display = canvas.getDisplay();
		
		if(image == null || image.isDisposed()){
			File imageFile = null;
			if(packFile != null){				
				imageFile = new File(packFile.getParentFile(), fileName);
				if(!imageFile.exists()){
					imageFile = new File(fileName);
				}
			}else{
				imageFile = new File(fileName);
			}
			image = new Image(display, imageFile.getAbsolutePath());
		}
		gc.drawImage(image, 0, 0);
				
		//画各个区域
		for(Region region : regions){
			region.drawRegion(gc);
		}
	}
	
	public List<Region> getRegions(){
		return regions;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMinFilter() {
		return minFilter;
	}

	public void setMinFilter(String minFilter) {
		this.minFilter = minFilter;
	}

	public String getMaxFilter() {
		return maxFilter;
	}

	public void setMaxFilter(String magFilter) {
		this.maxFilter = magFilter;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}
}
