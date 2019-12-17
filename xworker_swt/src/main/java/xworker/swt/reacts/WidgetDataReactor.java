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
	ThreadLocal<DataReactorContext> contextLocal = new ThreadLocal<DataReactorContext>();
	
	public WidgetDataReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		this.widget = widget;
		this.bindObject = widget;
	}

	public WidgetDataReactor(Widget widget, Thing self, ActionContext actionContext, boolean createChilds) {
		super(self, actionContext, createChilds);
		
		this.widget = widget;
		this.bindObject = widget;
	}
	
	public DataReactorContext getContext() {
		return contextLocal.get();
	}
	
	@Override
	protected final void doOnSelected(final List<Object> datas, final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnSelected(datas, context);
					}catch(Exception e) {
						logger.warn("Execute doOnSelected error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
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

	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {	
		if(self.getBoolean("monitorStatus")) {
			if(datas == null || datas.size() == 0) {
				this.setWidgetEnabled(false);
			}else {
				this.setWidgetEnabled(true);
			}
		}
	}
	
	@Override
	protected final void doOnUnselected(final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnUnselected(context);
					}catch(Exception e) {
						logger.warn("Execute doOnUnselected error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnUnselected(DataReactorContext context) {
		if(self.getBoolean("monitorStatus")) {
			this.setWidgetEnabled(false);
		}
	}

	@Override
	protected final void doOnAdded(final int index, final List<Object> datas, final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnAdded(index, datas, context);
					}catch(Exception e) {
						logger.warn("Execute doOnAdded error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		
	}

	@Override
	protected final void doOnRemoved(final List<Object> datas, final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnRemoved(datas, context);
					}catch(Exception e) {
						logger.warn("Execute doOnRemoved error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		
	}

	@Override
	protected final void doOnUpdated(final List<Object> datas, final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnUpdated(datas, context);
					}catch(Exception e) {
						logger.warn("Execute doOnUpdated error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
					}
				}
			});			
		}
	}
	
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		
	}

	@Override
	protected final void doOnLoaded(final List<Object> datas, final DataReactorContext context) {
		if(widget != null && widget.isDisposed() == false) {
			widget.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						contextLocal.set(context);
						widgetDoOnLoaded(datas, context);
					}catch(Exception e) {
						logger.warn("Execute doOnLoaded error, thing=" + getSelf().getMetadata().getPath(), e);
					}finally {
						contextLocal.set(null);
					}
				}
			});
			
		}
	}
	
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		
	}

	public Widget getWidget() {
		return widget;
	}
	
	

}
