package xworker.lang.flow.uiflow;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface IFlow {
	public void nodeFinished(Thing node, String nextConnectionName);
	
	public void log(String message);
	
	public void log(Throwable e);
	
	public Object get(String key);
	
	public void set(String key , Object value);
	
	public ActionContext runComposite(Thing flowNode, Thing composite);
	
	public ActionContext runComposite(Thing flowNode, Thing composite, Map<String, Object> params);
	
	public Object runAction(Thing action);
	
	public void start(Thing nextNode);
	
	public void start();
	
	public void end();
	
	public Composite getMainComposite();
	
	public void startChildFlow(Thing childFlow);	
	
	public IFlow getParent();
	
	public Thing getThing();
	
	public void set(Thing flowNode, String key , Object value);
	
	public Object get(Thing flowNode, String key);
	
	public void save();
	
}
