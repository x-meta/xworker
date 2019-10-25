package xworker.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.util.SwtUtils;

public class ToolTipActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Control parent = (Control) actionContext.get("parent");
		Shell shell = parent.getShell();
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(self.getBoolean("BALLOON")){
			style = style | SWT.BALLOON;
		}
		
		String icon = self.getString("ICON");
		if("ERROR".equals(icon)){
			style = style | SWT.ICON_ERROR;
		}else if("INFORMATION".equals(icon)){
			style = style | SWT.ICON_INFORMATION;
		}else if("WARNING".equals(icon)){
			style = style | SWT.ICON_WARNING;
		}
		
		ToolTip tip = new ToolTip(shell, style);
		String text = UtilString.getString(self, "text", actionContext);
		if(text != null){
			tip.setText(text);
		}
		String message = UtilString.getString(self, "message", actionContext);
		if(message != null){
			tip.setMessage(message);
		}
		
		tip.setVisible(self.getBoolean("visible"));
		tip.setAutoHide(self.getBoolean("autoHide"));
		
		actionContext.getScope(0).put(self.getMetadata().getName(), tip);
		return tip;
	}
}
