package xworker.dataObject.swt.reacts.widgets;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.reacts.DataObjectReactor;
import xworker.dataObject.swt.reacts.DataObjectReactorContext;
import xworker.dataObject.swt.reacts.ThingDataObjectReactor;

public class WidgetDataObjectReactor extends ThingDataObjectReactor{
	private static Logger logger = LoggerFactory.getLogger(WidgetDataObjectReactor.class);
	
	Widget widget;
	
	public WidgetDataObjectReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		this.widget = widget;
	}

	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		
	}
	@Override
	public final void doOnSelected(final List<DataObject> dataObjects) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnSelected(dataObjects);
						}catch(Exception e) {
							logger.warn("doOnSelected error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnSelected(dataObjects);
			}
		}
	}

	public void doWidgetOnUnselected() {
		
	}
	
	@Override
	public final void doOnUnselected() {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnUnselected();
						}catch(Exception e) {
							logger.warn("doOnUnselected error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnUnselected();
			}
		}
	}

	public void doWidgetOnAdded(int index, List<DataObject> dataObjects) {
		
	}
	
	@Override
	public final void doOnAdded(final int index, final List<DataObject> dataObjects) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnAdded(index, dataObjects);
						}catch(Exception e) {
							logger.warn("doWidgetOnAdded error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnAdded(index, dataObjects);
			}
		}
	}

	public void doWidgetOnRemoved(List<DataObject> dataObjects) {
		
	}
	
	public void setWidgetEnabled(boolean enabled) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget instanceof Control) {
				((Control) widget).setEnabled(enabled);
			}else if(widget instanceof MenuItem) {
				((MenuItem) widget).setEnabled(enabled);
			}else if(widget instanceof ToolItem) {
				((ToolItem) widget).setEnabled(enabled);
			}
		}
	}
	
	@Override
	public final void doOnRemoved(final List<DataObject> dataObjects) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnRemoved(dataObjects);
						}catch(Exception e) {
							logger.warn("doWidgetOnRemoved error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnRemoved(dataObjects);
			}
		}
	}

	public void doWidgetOnUpdated(List<DataObject> dataObjects) {
		
	}
	
	@Override
	public final void doOnUpdated(final List<DataObject> dataObjects) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnUpdated(dataObjects);
						}catch(Exception e) {
							logger.warn("doWidgetOnUpdated error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnUpdated(dataObjects);
			}
		}
	}

	public void doWidgetOnLoaded(List<DataObject> dataObjects) {
		
	}
	
	@Override
	public final void doOnLoaded(final List<DataObject> dataObjects) {
		if(widget != null && widget.isDisposed() == false) {
			if(widget.getDisplay().getThread() != Thread.currentThread()) {
				final DataObjectReactorContext context = DataObjectReactor.getContext();
				context.addCount();
				
				widget.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						DataObjectReactor.setContext(context);
						try {
							DataObjectReactor.setContext(context);
							
							WidgetDataObjectReactor.this.doWidgetOnLoaded(dataObjects);
						}catch(Exception e) {
							logger.warn("doWidgetOnLoaded error, thing=" + self.getMetadata().getPath(), e);
						}finally {
							context.removeCount();
						}
					}
				});
			}else {
				doWidgetOnLoaded(dataObjects);
			}
		}
	}
	
	
}
