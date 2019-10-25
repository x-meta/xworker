package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CLabelActions {
	public static CLabel create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("WRAP".equals(selfStyle)){
			style |= SWT.WRAP;
		}else if("SEPARATOR".equals(selfStyle)){
			style |= SWT.SEPARATOR;
		}
		
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}
		
		String shadow = self.getString("shadow");
		if("SHADOW_IN".equals(shadow)){
			style |= SWT.SHADOW_IN;
		}else if("SHADOW_OUT".equals(shadow)){
			style |= SWT.SHADOW_OUT;
		}else if("SHADOW_NONE".equals(shadow)){
			style |= SWT.SHADOW_NONE;
		}		
		    
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		String alignment = self.getString("alignment");
		if("LEFT".equals(alignment)){
			style |= SWT.LEFT;
		}else if("CENTER".equals(alignment)){
			style |= SWT.CENTER;
		}else if("RIGHT".equals(alignment)){
			style |= SWT.RIGHT;
		}
		
		Composite parent = (Composite) actionContext.get("parent");    
		CLabel label = new CLabel(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", label);
		try{
		    self.doAction("super.init", actionContext);
		}finally{
		    actionContext.pop();
		}
				
		Image image = SwtUtils.createImage(label, self.getString("image"), actionContext);
		//Object image = actionContext.get(self.getString("image"));
		if(image != null && image instanceof Image)
		    label.setImage((Image) image);
		
		label.setText(UtilString.getString(self.getString("text"), actionContext));
		label.setToolTipText(UtilString.getString(self.getString("toolTipText"), actionContext));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), label);
		actionContext.peek().put("parent", label);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(label, self.getMetadata().getPath(), actionContext);
		return label;       
	}
}
