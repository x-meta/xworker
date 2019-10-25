package xworker.swt.nebula;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.dialog.ChoiceItem;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.notifier.Notifier;
import org.eclipse.nebula.widgets.opal.notifier.NotifierColorsFactory.NotifierTheme;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class NebulaActions {
	public static void notify(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String title = self.doAction("getTitle", actionContext);
		String text = self.doAction("getText", actionContext);
		Image image = self.doAction("getImage", actionContext);
		NotifierTheme theme = self.doAction("getTheme", actionContext);
		
		if(title != null && text != null && image != null && theme != null) {
			Notifier.notify(image, title, text, theme);
		}else if(title != null && text != null && image != null) {
			Notifier.notify(image, title, text);
		}else if(title != null && text != null && theme != null) {
			Notifier.notify(title, text, theme);
		}else {
			Notifier.notify(title, text);
		}
	}
	
	public static NotifierTheme getNotifierTheme(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String theme = self.getStringBlankAsNull("theme");
		if("BLUE_THEME".equals(theme)) {
			return NotifierTheme.BLUE_THEME;
		}else if("GRAY_THEME".equals(theme)) {
			return NotifierTheme.GRAY_THEME;
		}else if("YELLOW_THEME".equals(theme)) {
			return NotifierTheme.YELLOW_THEME;
		}else {
			return null;
		}		
	}
	
	public static String ask(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		String defaultValue = self.doAction("getDefaultValue", actionContext);
		String text = self.doAction("getText", actionContext);
		
		String result = null;
		if(shell != null) {
			result = Dialog.ask(shell, title, text, defaultValue);
		}else {
			result = Dialog.ask(title, text, defaultValue);
		}
		
		self.doAction("closed", actionContext, "result", result);
		return result;
	}
	
	public static int choice(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		int defaultSelection = self.doAction("getDefaultSelection", actionContext);
		String text = self.doAction("getText", actionContext);
		ChoiceItem[] items = self.doAction("getItems", actionContext);
		int result = 0;
		if(shell != null) {
			result = Dialog.choice(shell, title, text, defaultSelection, items);
		}else {
			result = Dialog.choice(title, text, defaultSelection, items);
		}
		
		self.doAction("closed", actionContext, "result", result);
		return result;
	}
	
	public static ChoiceItem[] getChoiceItems(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		List<ChoiceItem> items = new ArrayList<ChoiceItem>();
		for(Thing item : self.getChilds("ChoiceItem")) {
			String instruction = item.getString("instruction", null, actionContext);
			if(instruction == null) {
				continue;
			}
			
			String text = item.getString("text", null, actionContext);
			if(text == null) {
				items.add(new ChoiceItem(instruction));
			}else {
				items.add(new ChoiceItem(instruction, text));
			}
		}
		
		ChoiceItem[] array = new ChoiceItem[items.size()];
		items.toArray(array);
		
		return array;
	}
	
	public static void error(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		String errorMessage = self.doAction("getErrorMessage", actionContext);
		
		if(shell != null) {
			Dialog.error(shell, title, errorMessage);
		}else {
			Dialog.error(title, errorMessage);
		}
	}
	
	public static void inform(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		String text = self.doAction("getText", actionContext);
		
		if(shell != null) {
			Dialog.inform(shell, title, text);
		}else {
			Dialog.inform(title, text);
		}
	}
	
	public static boolean isConfirmed(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		String text = self.doAction("getText", actionContext);
		int timer = self.doAction("getTimer", actionContext);
		
		boolean result = false;
		if(timer <= 0) {
			if(shell != null) {
				result = Dialog.isConfirmed(shell, title, text);
			}else {
				result =  Dialog.isConfirmed(title, text);
			}
		}else {
			if(shell != null) {
				result= Dialog.isConfirmed(shell, title, text, timer);
			}else {
				result = Dialog.isConfirmed(title, text, timer);
			}
		}
		
		self.doAction("closed", actionContext, "result", result);
		return result;
	}
	
	public static int radioChoice(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Shell shell = self.doAction("getShell", actionContext);
		String title = self.doAction("getTitle", actionContext);
		int defaultSelection = self.doAction("getDefaultSelection", actionContext);
		String text = self.doAction("getText", actionContext);
		String[] items = self.doAction("getChoices", actionContext);
		int result = 0;
		if(shell != null) {
			result = Dialog.radioChoice(shell, title, text, defaultSelection, items);
		}else {
			result = Dialog.radioChoice(title, text, defaultSelection, items);
		}
		
		self.doAction("closed", actionContext, "result", result);
		return result;
	}
	
	public static void showException(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Throwable exception = self.doAction("getException", actionContext);
		if(exception != null) {
			Dialog.showException(exception);
		}
	}
}
