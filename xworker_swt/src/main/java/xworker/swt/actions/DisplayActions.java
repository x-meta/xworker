package xworker.swt.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.task.Task;

public class DisplayActions {
	private static final String TAG = DisplayActions.class.getName();
	
	public static void exec(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取display
		String widgetForDisplay = self.getStringBlankAsNull("widgetForDisplay");
		Display display = null;
		boolean dispose = false;
		if(widgetForDisplay != null){
			try{
				Widget widget = (Widget) OgnlUtil.getValue(widgetForDisplay, actionContext);//actionContext.get(widgetForDisplay);
				if(widget != null){
					if(widget.isDisposed()) {
						dispose = true;
					}else {
						display = widget.getDisplay();
					}
				}
			}catch(Exception e){
				display = Designer.getExplorerDisplay();
			}
		}else{
			display = Display.getCurrent();
		}
		
		if(dispose == true || display == null || display.isDisposed()){			
			if(self.getBoolean("cancelTaskOnWidgetDisposed")){
				try{
					Task task = (Task) actionContext.get("task");
					if(task != null){
						task.cancel(false);
						return;
					}					
				}catch(Exception e){
					Executor.warn(TAG, "DisplayExex end task error, path=" + self.getMetadata().getPath(), e);
				}
			}
			self.doAction("onDisplayError", actionContext);
			return;
		}
		
		String type = self.getString("type");
		String variables = self.getStringBlankAsNull("variables");
		Map<String, Object> vars = null;
		if(variables != null){
			vars = new HashMap<String, Object>();
			for(String var : variables.split("[,]")){
				vars.put(var, actionContext.get(var));
			}
		}
		
		Exec exec = new Exec();
		exec.variables = vars;
		exec.actionContext = actionContext;
		exec.thing = self;
		if("async".equals(type)){
			display.asyncExec(exec);
		}else{
			display.syncExec(exec);
		}
	}
	
	public static void onDisplayError(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Executor.warn(TAG, "Display is null or disposed!, action=" + self.getMetadata().getPath());
	}
	
	static class Exec implements Runnable{
		Map<String, Object> variables = null;
		ActionContext actionContext = null;
		Thing thing = null;
		
		@Override
		public void run() {
			try{
				if(variables != null){
					thing.doAction("doAction", actionContext, variables);
				}else{
					thing.doAction("doAction", actionContext);
				}
			}catch(Exception e){
				Executor.error(TAG, "DisplayExec: execute error happened, thing=" + thing.getMetadata().getPath(), e);
			}
		}
		
	}
}
