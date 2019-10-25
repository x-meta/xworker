package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.util.JavaUtils;

/**
 * 可以显示堆栈信息的并且能够打开相应事物的文本框。
 * 
 * @author Administrator
 *
 */
public class ThrowableStackTraceText {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		Thing thing = null;
		if(SwtUtils.isRWT()) {
			thing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ThrowableStackTraceTextShell/@text1");
		}else {
			thing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ThrowableStackTraceTextShell/@text");
		}
		Control obj = null;
		Designer.pushCreator(self);
		try {
			obj = thing.doAction("create", ac);
			Designer.attachCreator(obj, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
		
		actionContext.peek().put("parent", obj);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), ac.get("actions"));
		return obj;
	}
	
	
	public static void setThrowable(ActionContext actionContext){
		Text text = (Text) actionContext.get("text");
		Throwable throwable = (Throwable) actionContext.get("throwable");
		SwtTextUtils.setText(text, "");
		
		printStackTrace(throwable, text);
	}
	
	public static void printStackTrace(Throwable t, Text text){
		SwtTextUtils.append(text, t.toString());
		SwtTextUtils.append(text, "\n");
		for(StackTraceElement st : t.getStackTrace()){			
			SwtTextUtils.append(text, "\tat ");
			//int start = SwtTextUtils.getText(text).length();
			String line = st.getClassName() + "." + st.getMethodName() + "(";
						
			line = line + st.getFileName() + ":" + st.getLineNumber();
			SwtTextUtils.append(text, line);
			
			String className = st.getClassName();
			className = className.split("[\\$]")[0];
            
            SwtTextUtils.append(text, ")\n");
		}

		Throwable cause = t.getCause();
		if(cause != null){
			printStackTrace(cause, text);
		}
	}
	
	public static void printStackTraceToStyledText(Throwable t, Object text) {
		JavaUtils.call("xworker.swt.xwidgets.ThrowableStackTraceStyledText", "printStackTrace",
				text, new Class<?>[] {Throwable.class, Object.class}, new Object[] {t, text});
	}
	
	public static void mouseMove(ActionContext actionContext){
		
	}
	
	public static void mouseDown(ActionContext actionContext){
		
	}
}
