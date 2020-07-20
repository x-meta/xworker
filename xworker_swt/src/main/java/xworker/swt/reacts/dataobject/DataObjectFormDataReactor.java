package xworker.swt.reacts.dataobject;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.view.swt.widgets.form.DataObjectForm;
import xworker.app.view.swt.widgets.form.DataObjectFormListener;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;
import xworker.swt.reacts.WidgetDataReactor;
import xworker.swt.reacts.xworker.ThingFormDataReactor;

public class DataObjectFormDataReactor extends WidgetDataReactor implements DataObjectFormListener {
	DataObjectForm form;
	DataObject dataObject;
	
	public DataObjectFormDataReactor(Widget widget, DataObjectForm form, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);
		this.form = form;
		this.form.addListener(this);
		this.dataObject = form.getDataObject();
	}

	@Override
	public void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		List<DataObject> dataObjects = DataReactorUtils.toDataObjectList(datas);
		if(dataObjects.size() > 0) {
			form.setDataObject(dataObjects.get(0));
		}
	}

	@Override
	public void widgetDoOnUnselected(DataReactorContext context) {
		form.setDataObject((DataObject) null); 
	}

	@Override
	public void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
	}

	@Override
	public void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
	}

	@Override
	public void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
	}

	@Override
	public void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		List<DataObject> dataObjects = DataReactorUtils.toDataObjectList(datas);
		if(dataObjects.size() > 0) {
			form.setDataObject(dataObjects.get(0));
		}
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing form = self.doAction("getDataObjectForm", actionContext);
		if(form == null) {
			form = actionContext.getObject("dataObjectForm");
		}
		
		if(form != null) {
			DataObjectForm dataObjectForm = DataObjectForm.getDataObjectForm(form);
			if(dataObjectForm != null) {
				DataObjectFormDataReactor reactor = new DataObjectFormDataReactor(dataObjectForm.getControl(), dataObjectForm, self, actionContext);
				actionContext.g().put(self.getMetadata().getName(), reactor);
			}
		}
	}

	@Override
	public void onSetDataObject(DataObjectForm dataObjectForm, DataObject dataObject) {
		this.dataObject = dataObject;
		this.fireSelected(DataReactorUtils.toObjectList(dataObject), getContext());
	}

	@Override
	public void onMidified(DataObjectForm dataObjectForm) {
		Control control = form.getControl();
		if(control.isDisposed() == false) {
			control.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						DataObject data = form.getDataObject();
						DataObjectFormDataReactor.this.fireUpdated(DataReactorUtils.toObjectList(data), getContext());
					}catch(Exception e) {
						Executor.error(DataObjectFormDataReactor.class.getName(), 
								"Invoke modified error, path=" + getSelf().getMetadata().getPath(), e);
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	/**
	 * 从DataObjectForm上取数据对象并触发updated事件。
	 */
	public void fireUpdated() {
		if(form != null) {
			onMidified(form);
		}
	}
}
