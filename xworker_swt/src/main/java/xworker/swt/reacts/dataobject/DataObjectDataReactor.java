package xworker.swt.reacts.dataobject;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;
import xworker.swt.util.DelayExecutor;

public class DataObjectDataReactor extends DataReactor implements DataObjectListener{
	DataObject dataObject;
	DelayExecutor delayExecutor = new DelayExecutor(Display.getCurrent(), 500) {

		@Override
		public void doTask() {	
			DataObjectDataReactor.this.fireUpdated(DataReactorUtils.toObjectList(dataObject), null);
		}
		
	};
	public DataObjectDataReactor(DataObject dataObject, Thing self, ActionContext actionContext) {
		super(self, actionContext);

		Widget parent = this.getParentWidget(actionContext);
				
		this.dataObject = dataObject;
		this.dataObject.addListener(this);
		
		if(parent != null && parent.isDisposed() == false) {
			//销毁时从数据对象上移除监听器
			parent.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					try {
						DataObjectDataReactor.this.dataObject.removeListener(DataObjectDataReactor.this);
					}catch(Exception ee) {						
					}
				}				
			});
		}
		
		//this.fireLoaded(DataReactorUtils.toObjectList(dataObject));
		this.datas = this.toList(dataObject);
	}

	@Override
	public void changed(DataObject dataObject) {
		delayExecutor.execute();
	}
	
	@Override
	public void fireLoaded(DataReactorContext context) {
		this.fireSelected(datas, context);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObject dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject != null) {
			DataObjectDataReactor dr = new DataObjectDataReactor(dataObject, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), dr);
			return dr;
		}
		
		return null;
	}
}
