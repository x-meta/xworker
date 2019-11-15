package xworker.swt.reacts;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class WidgetDataReactor extends DataReactor{
	private static Logger logger = LoggerFactory.getLogger(WidgetDataReactor.class);
	
	Widget widget;
	
	public WidgetDataReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		this.widget = widget;
		this.bindObject = widget;
	}

	@Override
	protected final void doOnSelected(final List<Object> datas) {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();		
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnSelected(datas);
					}catch(Exception e) {
						logger.warn("Execute doOnSelected error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	
	public void setWidgetEnabled(boolean enabled) {
		Widget widget = this.getWidget();
		if(widget instanceof Control) {
			((Control) widget).setEnabled(enabled);
		}else if(widget instanceof MenuItem) {
			((MenuItem) widget).setEnabled(enabled);
		}else if(widget instanceof ToolItem) {
			((ToolItem) widget).setEnabled(enabled);
		}
	}

	protected void widgetDoOnSelected(List<Object> datas) {	
		if(self.getBoolean("monitorStatus")) {
			if(datas == null || datas.size() == 0) {
				this.setWidgetEnabled(false);
			}else {
				this.setWidgetEnabled(true);
			}
		}
	}
	
	@Override
	protected final void doOnUnselected() {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();		
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnUnselected();
					}catch(Exception e) {
						logger.warn("Execute doOnUnselected error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnUnselected() {
		if(self.getBoolean("monitorStatus")) {
			this.setWidgetEnabled(false);
		}
	}

	@Override
	protected final void doOnAdded(final int index, final List<Object> datas) {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();		
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnAdded(index, datas);
					}catch(Exception e) {
						logger.warn("Execute doOnAdded error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnAdded(int index, List<Object> datas) {
		
	}

	@Override
	protected final void doOnRemoved(final List<Object> datas) {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();		
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnRemoved(datas);
					}catch(Exception e) {
						logger.warn("Execute doOnRemoved error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnRemoved(List<Object> datas) {
		
	}

	@Override
	protected final void doOnUpdated(final List<Object> datas) {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();		
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnUpdated(datas);
					}catch(Exception e) {
						logger.warn("Execute doOnUpdated error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnUpdated(List<Object> datas) {
		
	}

	@Override
	protected final void doOnLoaded(final List<Object> datas) {
		if(widget != null && widget.isDisposed() == false) {
			final DataReactorContext context = WidgetDataReactor.getContext();
			context.addCount();			
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						WidgetDataReactor.setContext(context);
						widgetDoOnLoaded(datas);
					}catch(Exception e) {
						logger.warn("Execute doOnLoaded error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						context.removeCount();
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnLoaded(List<Object> datas) {
		
	}

	public Widget getWidget() {
		return widget;
	}
	
	

}
