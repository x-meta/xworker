package xworker.swt.xwidgets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolTip;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.graphics.ImageUtils;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;

/**
 * 图标选择器，可选择一些小的图片，像QQ选择表情那样。
 * 
 * @author Administrator
 *
 */
public class IconSelector extends Canvas implements PaintListener, MouseListener, MouseMoveListener, DisposeListener, KeyListener{
	private static String DATA_ICONSELECTOR = "__iconSelector__data__";
	/** 图标的列个数 */
	int column;
	/** 线的厚度 */
	int lineThick = 1;
	/** 图片和边框的距离 */
	int gap = 5;
	/** 图片单元格 */
	IconInfo[][] cells = null;
	List<IconInfo> icons = null;
	/** 已选择的单元格行 */
	int selectedRow;
	/** 已选择的单元格列 */
	int selectedCol;
	/** 鼠标位置所在的单元格行 */
	int currentRow;
	/** 鼠标位置所在的单元格列 */
	int currentCol;
	/** 画布的大小 */
	Point canvasSize = null;
	/** 工具提示 */
	ToolTip toolTip = null;
	int iconMaxWidth;
	int iconMaxHeight;
	
	public IconSelector(Composite parent, int style){
		super(parent, style | SWT.DOUBLE_BUFFERED);
		
		toolTip = new ToolTip(parent.getShell(), SWT.NONE);
		
		this.addPaintListener(this);
		this.addMouseListener(this);
		if(!SwtUtils.isRWT()) {
			this.addMouseMoveListener(this);
		}
		this.addDisposeListener(this);
		this.addKeyListener(this);
	}

	public void setColumn(int column){	
		this.column = column;		
	}
	
	public void setMapIcons(List<Map<String, Object>> iconList){
		List<IconInfo> icons = new ArrayList<IconInfo>();
		for(Map<String, Object> iconCfg : iconList){
			IconInfo iconInfo = new IconInfo();
			iconInfo.iconPath = (String) iconCfg.get("iconPath");
			iconInfo.toolTip = (String) iconCfg.get("toolTip");
			iconInfo.name = (String) iconCfg.get("name");
			icons.add(iconInfo);
		}
		
		setIcons(icons);
	}
	
	/**
	 * 设置图标所在的目录。
	 * 
	 * @param directory 目录
	 * @param fileDirectoryPrefix 目录前缀
	 */
	public void setIcons(File directory, String fileDirectoryPrefix){
		List<IconInfo> iconList = new ArrayList<IconInfo>();
		if(directory.exists()) {
			for(File file : directory.listFiles()){
				if(file.isFile() && ImageUtils.isSupportImageFile(file)){
					IconInfo info =new IconInfo();
					info.iconPath = file.getAbsolutePath();
					info.toolTip = file.getName();
					if(fileDirectoryPrefix != null){
						info.name = fileDirectoryPrefix + file.getName();
					}else{
						info.name = file.getName();
					}
					
					iconList.add(info);
				}
			}
		}
		
		setIcons(iconList);
	}
	
	protected void initColumns(int width){
		if(icons == null){
			return;
		}
		
		int column  = 1;
		if(this.column < 0){
			int maxCellWidth = 0;
			for(IconInfo icon : icons){
				if(icon.width > maxCellWidth){
					maxCellWidth = icon.width;
				}
			}
						
			while(column * maxCellWidth < width){
				column++;
			}
		}else{
			column = this.column;
		}		
		
		initCells(column);	
	}
	
	protected void initCells(int column){
		int rows = (icons.size() / column) + ((icons.size() % column != 0) ? 1 : 0);
		cells = new IconInfo[rows][column];
		for(int i=0; i<icons.size(); i++){
			IconInfo cell = icons.get(i);
		    int row = i / column;
			int col = i % column;
			cells[row][col] = cell; 
		}
		//没有的单元格也初始化一下
		for(int i =0; i<cells.length; i++){
			for(int n = 0; n<column; n++){
				if(cells[i][n] == null){
					cells[i][n] = new IconInfo();
				}
			}
		}
	
		//设置行和列的最大值
		for(int i =0; i<cells.length; i++){
			//行的高度
			int maxHeight = 0;
			for(int n = 0; n<column; n++){
				if(maxHeight < cells[i][n].height){
					maxHeight = cells[i][n].height;
				}
			}
			
			for(int n=0; n<column; n++){
				cells[i][n].height = maxHeight;
			}
		}
		for(int i =0; i<column; i++){
			//列的宽度
			int maxWidth = 0;
			for(int n = 0; n<cells.length; n++){
				if(maxWidth < cells[n][i].width){
					maxWidth = cells[n][i].width;
				}
			}
			
			for(int n=0; n<cells.length; n++){
				cells[n][i].width = maxWidth;
			}
		}
		
		//初始化单元格的x,y以及canvas的高度和宽度
		int width = lineThick;
		int height = lineThick;
		for(int row=0; row<cells.length; row++){
			for(int cel=0; cel<cells[row].length; cel++){
				if(row == 0){
					cells[row][cel].x = width;
					width = width + cells[row][cel].width ;					
				}else{
					cells[row][cel].x = cells[0][cel].x;
				}
				if(cel == 0){
					cells[row][cel].y = height;
					height = height + cells[row][cel].height;					
				}else{
					cells[row][cel].y = cells[row][0].y;
				}
			}
		}
		
		canvasSize = new Point(width, height + 1);
		this.setSize(canvasSize);
		if(this.getParent() instanceof ScrolledComposite){
			ScrolledComposite parent = (ScrolledComposite) this.getParent();
			parent.setMinSize(canvasSize);
		}
	}
	
	/**
	 * 设置图标的文件列表。
	 * 
	 * @param iconList
	 */
	public void setIcons(List<IconInfo> iconList){
		if(icons != null){
			//释放旧的资源
			this.getDisplay().asyncExec(new Runnable(){
				public void run(){
					disposeIcons();
				}
			});
		}
		ActionContext ac = new ActionContext();
		ac.put("parent", this);
		
		for(int i=0; i<iconList.size(); i++){
			IconInfo cell = iconList.get(i);
			//使用ResourceManager创建这样资源的释放绑定到当前的canvas上了
			try{
				Image image = (Image) ResourceManager.createIamge(cell.iconPath, ac);
				if(image != null){
					int width = image.getImageData().width;
					int height = image.getImageData().height;
					boolean resize = false;
					if(width > iconMaxWidth){
						width = iconMaxWidth;
						resize = true;
					}
					if(height > iconMaxHeight){
						height = iconMaxHeight;
						resize = true;
					}
					if(resize){
						Image newImage = new Image(image.getDevice(), image.getImageData().scaledTo(UtilSwt.getInt(width), UtilSwt.getInt(height)));						
						image = newImage;
						cell.needDispose = true;//需要手工释放资源
					}
					cell.image = image;
					cell.width = image.getImageData().width + 2 * gap + lineThick;
					cell.height = image.getImageData().height + 2 * gap + lineThick;
				}
			}catch(Exception e){
				//System.out.println("IconSelector load error, file=" + iconFile + e);
			}
		}
		this.icons = iconList;
		
		initColumns(this.getClientArea().x);		
	}
	
	@Override
	public void paintControl(PaintEvent event) {
		GC gc = event.gc;
		Display display = this.getDisplay();
	
		String designData = (String) this.getParent().getData(Designer.DATA_DESING_SELECTED);		
		if(designData != null && "true".equals(designData)){
			gc.setBackground(this.getParent().getBackground());
		}else{
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		}
		gc.fillRectangle(0, 0, this.getSize().x,this.getSize().y);
		gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
				
		if(cells == null){
			gc.drawText("No icon seted!", 1, 1);
			return;
		}
		
		for(int row=0; row<cells.length; row++){			
			for(int cel=0; cel<cells[row].length; cel++){
				
			}
		}
		//画边框
		gc.drawLine(1, 1, 1, canvasSize.y); //最左边竖线
		gc.drawLine(1, 1, canvasSize.x, 1); //最上面横线
		for(int row=0; row<cells.length; row++){			
			for(int col=0; col<cells[row].length; col++){
				IconInfo cell = cells[row][col];
				if(row == 0){
					//画竖线
					int lineX = cells[row][col].x + cells[row][col].width;
					gc.drawLine(lineX, 1, lineX, canvasSize.y);
				}
				if(col == 0){
					//画横线
					int lineY = cells[row][col].y + cells[row][col].height;
					gc.drawLine(1, lineY, canvasSize.x, lineY);
				}
				
				if(cell != null && cell.image != null){		
					if(row == selectedRow && col == selectedCol){
						gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
						gc.fillRectangle(cell.x, cell.y, cell.width, cell.height);
						gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					}else if(row == currentRow && col == currentCol){
						gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
						gc.fillRectangle(cell.x, cell.y, cell.width, cell.height);
						gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					}
					gc.drawImage(cell.image, cell.x + gap, cell.y + gap);
				}
			}
		}
		
		//designData = (String) this.getParent().getData(Designer.DATA_DESING_SELECTED);		
		if(designData != null && "true".equals(designData)){
			Designer.paintSelected(this, gc);
		}		
	}
	
	public void setSelection(int row, int col){
		this.selectedRow = row;
		this.selectedCol = col;
	}
	
	public Image getSelectedImage(){
		return null;
	}
	
	public int getSelectionIndex(){
		if(selectedRow == -1 || selectedCol == -1){
			return -1;
		}
		return selectedRow * selectedCol;
	}

	public IconInfo getSelection(){
		if(selectedRow == -1 || selectedCol == -1){
			return null;
		}else if(cells !=  null){
			return cells[selectedRow][selectedCol];
		}else{
			return null;
		}
	}
	
	protected IconInfo getMouseMoveSelection(){
		if(currentRow == -1 || currentCol == -1){
			return null;
		}else if(cells !=  null){
			return cells[currentRow][currentCol];
		}else{
			return null;
		}	
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent event) {
		if(SwtUtils.isRWT()) {
			//RWT下没有MouseMove，因此是在点击时选择
			selectIconAt(event.x, event.y);
		}
		IconInfo iconInfo = getMouseMoveSelection();
		if(event.button == 1 && iconInfo != null){
			selectedRow = currentRow;
			selectedCol = currentCol;
			
			Event selectionEvent = new Event();
			selectionEvent.display = this.getDisplay();
			selectionEvent.widget = this;			
			selectionEvent.x = event.x;
			selectionEvent.y = event.y;
			selectionEvent.data = iconInfo;
			
			notifyListeners(SWT.DefaultSelection, selectionEvent);
		}
	}

	@Override
	public void mouseDown(MouseEvent event) {
		if(SwtUtils.isRWT()) {
			//RWT下没有MouseMove，因此是在点击时选择
			selectIconAt(event.x, event.y);
		}
		
		IconInfo iconInfo = getMouseMoveSelection();
		if(event.button == 1 && iconInfo != null){
			selectedRow = currentRow;
			selectedCol = currentCol;
			Event selectionEvent = new Event();
			selectionEvent.display = this.getDisplay();
			selectionEvent.widget = this;			
			selectionEvent.x = event.x;
			selectionEvent.y = event.y;
			selectionEvent.data = iconInfo;
			
			notifyListeners(SWT.Selection, selectionEvent);
		}		
	}

	@Override
	public void mouseUp(MouseEvent event) {
	}
	
	/**
	 * 通过事物创建图标选择器。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		    
		Composite parent = (Composite) actionContext.get("parent");
		
		ScrolledComposite composite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		composite.setExpandHorizontal(true);
		composite.setExpandVertical(true);
		composite.addControlListener(new ControlListener(){
			@Override
			public void controlMoved(ControlEvent arg0) {
			}

			@Override
			public void controlResized(ControlEvent event) {
				IconSelector is = (IconSelector) event.widget.getData(DATA_ICONSELECTOR);
				ScrolledComposite parent = (ScrolledComposite) event.widget;				
				is.initColumns(parent.getClientArea().width - 20);
				//is.initColumns(parent.getClientArea().width);
			}
			
		});
		
		IconSelector iconSelector = new IconSelector(composite, style);
		iconSelector.setColumn((Integer) self.doAction("getColumn", actionContext));
		iconSelector.setIconMaxWidth(UtilSwt.getInt((Integer) self.doAction("getIconMaxWidth", actionContext)));
		iconSelector.setIconMaxHeight(UtilSwt.getInt((Integer) self.doAction("getIconMaxHeight", actionContext)));
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", iconSelector);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), iconSelector);		
		for(Thing child : self.getAllChilds()){
			if(child.isThingByName("listeners") || child.isThingByName("Event")){
				actionContext.peek().put("parent", iconSelector);
			}else{
				actionContext.peek().put("parent", composite);
			}
				
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		//设置图标
		File directory = self.doAction("getIconDirectory", actionContext);
		String prefix = self.doAction("getIconDirectoryPrefix", actionContext);
		if(directory != null){
			iconSelector.setIcons(directory, prefix);
		}
		composite.setContent(iconSelector);
		Designer.pushCreator(self);
		try{
			iconSelector.setData(Designer.DATA_DESIGN_TARGET, composite);
			Designer.attach(iconSelector, self.getMetadata().getPath(), actionContext);			
		}finally{
			Designer.popCreator();
		}
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData(DATA_ICONSELECTOR, iconSelector);
		return composite;        
	}

	private void selectIconAt(int x, int y) {
		int newSelectedRow = -1;
		int newSelectedCol = -1;
		
		if(cells == null){
			return;
		}
		
		for(int row = 0; row < cells.length; row ++){
			for(int col = 0; col < cells[row].length; col++){
				IconInfo cell = cells[row][col];
				if(cell.x <= x && cell.x + cell.width >= x && cell.y <= y && y <= cell.y + cell.height){
					newSelectedRow = row;
					newSelectedCol = col;
					break;
				}
			}
		}
		
		if(newSelectedRow != currentRow || newSelectedCol != currentCol){
			currentRow = newSelectedRow;
			currentCol = newSelectedCol;		
			
			IconInfo iconInfo = this.getSelection();
			if(iconInfo != null){
				if(iconInfo.toolTip != null){
					toolTip.setMessage(iconInfo.toolTip);
					toolTip.setText("");
					toolTip.setLocation(this.toDisplay(x + 15, y + 15));
					toolTip.setVisible(true);
				}
			}else{
				toolTip.setVisible(false);
			}
			
			redraw();
		}else{
			IconInfo iconInfo = this.getSelection();
			if(iconInfo != null){
				toolTip.setLocation(this.toDisplay(x + 15, y + 15));
				toolTip.setVisible(true);
			}
		}
	}
	
	@Override
	public void mouseMove(MouseEvent event) {
		int newSelectedRow = -1;
		int newSelectedCol = -1;
		
		if(cells == null){
			return;
		}
		
		for(int row = 0; row < cells.length; row ++){
			for(int col = 0; col < cells[row].length; col++){
				IconInfo cell = cells[row][col];
				if(cell.x <= event.x && cell.x + cell.width >= event.x && cell.y <= event.y && event.y <= cell.y + cell.height){
					newSelectedRow = row;
					newSelectedCol = col;
					break;
				}
			}
		}
		
		toolTip.setVisible(false);
		if(newSelectedRow != currentRow || newSelectedCol != currentCol){
			currentRow = newSelectedRow;
			currentCol = newSelectedCol;
			
			IconInfo iconInfo = this.getSelection();
			if(iconInfo != null){
				if(iconInfo.toolTip != null){
					toolTip.setMessage(iconInfo.toolTip);
					toolTip.setText("");
					toolTip.setLocation(this.toDisplay(event.x + 15, event.y + 15));
					toolTip.setVisible(true);
				}
			}else{
				toolTip.setVisible(false);
			}
			redraw();
		}else{
			IconInfo iconInfo = this.getSelection();
			if(iconInfo != null){
				toolTip.setLocation(this.toDisplay(event.x + 15, event.y + 15));
				toolTip.setVisible(true);
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		//System.out.println("icon disposed");
		if(toolTip != null && !toolTip.isDisposed()){
			toolTip.dispose();
		}
		
		disposeIcons();
	}

	private void disposeIcons(){
		if(icons != null){
			for(IconInfo icon : icons){
				if(icon.needDispose && icon.image != null){
					icon.image.dispose();
				}
			}
		}
	}
	public int getIconMaxWidth() {
		return iconMaxWidth;
	}

	public void setIconMaxWidth(int iconMaxWidth) {
		this.iconMaxWidth = iconMaxWidth;
	}

	public int getIconMaxHeight() {
		return iconMaxHeight;
	}

	public void setIconMaxHeight(int iconMaxHeight) {
		this.iconMaxHeight = iconMaxHeight;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		boolean redraw = false;
		if(selectedCol < 0){
			selectedCol = 0;
		}
		if(selectedRow < 0){
			selectedRow = 0;
		}
		if(event.keyCode == SWT.ARROW_LEFT){
			if(selectedCol > 0){
				selectedCol--;
				redraw = true;
			}
			redraw = true;
		}else if(event.keyCode == SWT.ARROW_RIGHT){
			if(selectedCol < cells[0].length - 1 && cells[selectedRow][selectedCol + 1].image != null){				
				selectedCol++;
				redraw = true;
			}
			redraw = true;
		}else if(event.keyCode == SWT.ARROW_DOWN){
			if(selectedRow < cells.length - 1  && cells[selectedRow + 1][selectedCol].image != null){
				selectedRow ++;
				redraw = true;
			}
		}else if(event.keyCode == SWT.ARROW_UP){
			if(selectedRow > 0){
				selectedRow --;
				redraw = true;
			}
		}
		
		if(redraw){
			/*
			int x = 0; 
			int y = 0;
			for(int i=0; i<selectedRow; i++){
				y += cells[i][0].height;
			}
			for(int i=0; i<selectedCol; i++){
				x += cells[0][i].width;
			}
			ScrolledComposite sc = (ScrolledComposite) this.getParent();
			System.out.println(sc.getClientArea());
			System.out.println("x=" + x + ", y=" + y);
			System.out.println(sc.toControl(x, y));
			
			sc.setOrigin(x, y);
			*/
			this.redraw();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
	}
}
