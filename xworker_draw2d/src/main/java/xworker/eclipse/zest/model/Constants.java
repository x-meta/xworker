package xworker.eclipse.zest.model;

import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.ZestStyles;

public class Constants {
	public static int getNodeStyle(String name){
		int style = SWT.NONE;
		
		if(name == null || "".equals(name)){
			return style;
		}
		
		for(String n : name.split("[,]")){
			if(n.equals("NODES_CACHE_LABEL")){
				style = style | ZestStyles.NODES_CACHE_LABEL;
			}else if(n.equals("NODES_FISHEYE")){
				style = style | ZestStyles.NODES_FISHEYE;
			}else if(n.equals("NODES_HIDE_TEXT")){
				style = style | ZestStyles.NODES_HIDE_TEXT;
			}else if(n.equals("NODES_NO_ANIMATION")){
				style = style | ZestStyles.NODES_NO_ANIMATION;
			}else if(n.equals("NODES_NO_FISHEYE_ANIMATION")){
				style = style | ZestStyles.NODES_NO_FISHEYE_ANIMATION;
			}else if(n.equals("NODES_NO_LAYOUT_ANIMATION")){
				style = style | ZestStyles.NODES_NO_LAYOUT_ANIMATION;
			}else if(n.equals("NODES_NO_LAYOUT_RESIZE")){
				style = style | ZestStyles.NODES_NO_LAYOUT_RESIZE;
			}else if(n.equals("NONE")){
				style = style | ZestStyles.NONE;
			}
		}
		
		return style;
	}

}
