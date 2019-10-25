package xworker.swt.nebula;

import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;

public class PromptSupportActions {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Control parent = actionContext.getObject("parent");
		String promptText = self.getString("promptText", null, actionContext);
		if(promptText == null) {
			return;
		}
		
		Color promptForeground = UtilSwt.createColor(parent, self.getString("promptForeground"), actionContext);
		Color promptBackground = UtilSwt.createColor(parent, self.getString("promptBackground"), actionContext);
		PromptSupport.init(promptText, promptForeground, promptBackground, parent);
		
		int style = SwtUtils.getSWT(self.getString("ITALIC"));
		style = style | SwtUtils.getSWT(self.getString("BOLD"));
		PromptSupport.setFontStyle(style, parent);		
	}
}
