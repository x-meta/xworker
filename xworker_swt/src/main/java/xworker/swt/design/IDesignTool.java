package xworker.swt.design;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Thing;

public interface IDesignTool<T extends Composite> {
	public Control insertAboveOrBelow(T parent, Control control, Thing thing, int actionType);
	
	public Control replace(T parent, Control control, Thing thing);
	
	public Control update(T parent, Control control);
	
	public Control remove(T parent, Control control);	
	
	public Control insert(T parent, Thing thing);
	
	public ItemInfo getItemIndex(T parent, Control control);
}
