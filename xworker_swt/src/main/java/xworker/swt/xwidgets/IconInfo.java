package xworker.swt.xwidgets;

import org.eclipse.swt.graphics.Image;

/**
 * 图标信息。
 * 
 * @author Administrator
 *
 */
public class IconInfo {
	public String iconPath;
	public String toolTip;
	public String name;
	
	public IconInfo(){
	
	}
	
	public IconInfo(String name, String iconPath, String toolTip){
		this.name = name;
		this.iconPath = iconPath;
		this.toolTip = toolTip;
	}
	
	int x;
	int y;
	int width;
	int height;
	Image image;
	boolean needDispose = false;
}
