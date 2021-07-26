package xworker.lang.executor;

import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TextExecutorService {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		LogViewer logViewer = null;
		Object text = self.doAction("getText", actionContext);
		if(text instanceof Text) {
			logViewer = new TextLogViewer((Text) text);
		} else if(text != null) {
			logViewer = self.doAction("createStyledText", actionContext,"logText", text);
		}
		
		if(logViewer != null) {
			SwtExecutorService ss = new SwtExecutorService(logViewer, null);
			actionContext.g().put(self.getMetadata().getName(), ss);
		}
	}
}
