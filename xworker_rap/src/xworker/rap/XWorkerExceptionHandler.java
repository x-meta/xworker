package xworker.rap;

import org.eclipse.rap.rwt.application.ExceptionHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class XWorkerExceptionHandler implements ExceptionHandler{

	@Override
	public void handleException(Throwable throwable) {
		Thing thing = World.getInstance().getThing("xworker.swt.rap.ExceptionHandler");
		ActionContext ac = new ActionContext();
		ac.put("parent", Display.getCurrent());
		Shell shell = thing.doAction("create", ac);
		ActionContainer actions = ac.getObject("viewer");
		actions.doAction("setThrowable", ac, "throwable", throwable);
		if(throwable.getMessage() != null) {
			shell.setText(throwable.getMessage());
		}
		shell.setVisible(true);
	}

}
