package xworker.swt.actions;

import org.eclipse.swt.custom.StyledText;
import org.xmeta.ActionContext;

public class AppendTextExample {
	public static void run(ActionContext actionContext){
		StyledText text = (StyledText) actionContext.get("text");
		text.append("\nHello World!");
	}
}
