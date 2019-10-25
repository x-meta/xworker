package xworker.dataObject.swt.bind;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public abstract class WidgetBinderItem extends BinderItem{
	protected Widget widget = null;
	
	public WidgetBinderItem(Thing thing,  Widget widget, ActionContext actionContext) {
		super(thing, actionContext);
		
		//添加组件的disposeListener
		this.widget = widget;
		if(widget != null) {
			this.widget.addDisposeListener(this);
		}
	}
	
	public boolean isDisposed() {
		return widget == null || widget.isDisposed();
	}
	
}
