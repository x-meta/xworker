package xworker.system;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface ISystem {
	public Thing getThing();
	
	public ActionContext getActionContext();
	
	public void doSystem();
	
	public void stop();
	
	public boolean isStoped();
}
