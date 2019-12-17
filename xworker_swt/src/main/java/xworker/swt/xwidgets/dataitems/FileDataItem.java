package xworker.swt.xwidgets.dataitems;

import java.io.File;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;
import xworker.util.UtilFileIcon;

public class FileDataItem extends DataItem{
	File file;
	boolean dynamic;
	boolean includeSubFiles;
	boolean recursion;
	boolean inited = false;
	
	public FileDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
		
		file = thing.doAction("getFile", actionContext);
		dynamic = thing.getBoolean("dynamic");
		includeSubFiles = thing.getBoolean("includeSubFiles");
		recursion = thing.getBoolean("recursion");
		
		if(includeSubFiles && dynamic == false) {
			initChilds();
		}
	}
	
	public FileDataItem(DataItemContainer dataItemContainer, DataItem parentItem, File file, boolean dynamic, boolean includeSubFiles, boolean recursion, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
		
		this.file = file;
		this.dynamic = dynamic;
		this.includeSubFiles = includeSubFiles;
		this.recursion = recursion;
		
		if(includeSubFiles && dynamic == false) {
			initChilds();
		}
	}
	
	private void initChilds() {
		if(inited == false && file != null && file.isDirectory()) {
			inited = true;
			for(File childFile : file.listFiles()) {
				FileDataItem childItem = new FileDataItem(dataItemContainer, parentItem, childFile, dynamic, includeSubFiles & recursion, recursion, thing, actionContext);
				childs.add(childItem);
			}
		}
	}

	@Override
	public Image getIcon(Control control) {
		if(file != null) {
			try {
				String icon = UtilFileIcon.getFileIcon(file, false);
				return SwtUtils.createImage(control, icon, actionContext);
			}catch(Exception e) {				
			}
		}
		
		return super.getIcon(control);
	}

	@Override
	public String getLabel() {
		if(file != null) {
			return file.getName();
		}
		
		return super.getLabel();
	}

	@Override
	public boolean isDynamic() {
		return dynamic;
	}

	@Override
	public Object getData() {
		return file;
	}

	@Override
	public List<DataItem> getChilds() {
		initChilds();
		
		return childs;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new FileDataItem(dataItemContainer, parentItem, self, actionContext);
	}
}
