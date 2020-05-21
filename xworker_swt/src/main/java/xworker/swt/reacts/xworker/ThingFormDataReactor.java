package xworker.swt.reacts.xworker;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;
import xworker.swt.reacts.WidgetDataReactor;
import xworker.swt.xworker.ThingForm;
import xworker.swt.xworker.ThingFormListener;

public class ThingFormDataReactor extends WidgetDataReactor implements ThingFormListener {
	ThingForm form;
		
	public ThingFormDataReactor(ThingForm form, Thing self, ActionContext actionContext) {
		super(form.getControl(), self, actionContext);
		this.form = form;
		this.form.addListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		
		if(datas != null && datas.size() > 0) {
			for(Object obj : datas) {
				if(obj instanceof Thing) {
					if(self.getBoolean("descriptor")) {
						form.setDescriptor((Thing) obj);
					}else {
						form.setThing((Thing) obj);
					}
					return;
				}else if(obj instanceof String) {
					Thing thing = World.getInstance().getThing((String) obj);
					if(thing != null) {
						if(self.getBoolean("descriptor")) {
							form.setDescriptor(thing);
						}else {
							form.setThing(thing);
						}
						return;	
					}
				}else if(obj instanceof Map) {
					Map<String, Object> values = (Map<String, Object>) obj;
					form.setValues(values);
				}
			}
		}
		
		form.setThing(null);
	}

	@Override
	public void widgetDoOnUnselected(DataReactorContext context) {		
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
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing form = self.doAction("getBindTo", actionContext);
		if(form == null) {
			form = actionContext.getObject("dataObjectForm");
		}
		
		if(form != null) {
			ThingForm thingForm = ThingForm.getThingForm(form);
			if(thingForm != null) {
				ThingFormDataReactor reactor = new ThingFormDataReactor(thingForm, self, actionContext);
				actionContext.g().put(self.getMetadata().getName(), reactor);
			}
		}
	}

	@Override
	public void modified(ThingForm thingForm) {
		Control control = thingForm.getControl();
		if(control.isDisposed() == false) {
			control.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						Object values = thingForm.getValues();
						ThingFormDataReactor.this.fireUpdated(DataReactorUtils.toObjectList(values), getContext());
					}catch(Exception e) {
						Executor.error(ThingFormDataReactor.class.getName(), 
								"Invoke modified error, path=" + getSelf().getMetadata().getPath(), e);
						e.printStackTrace();
					}
				}
			});
		}
		
	}

	@Override
	public void defaultSelection(ThingForm thingForm) {
		Object values = thingForm.getValues();
		this.fireSelected(DataReactorUtils.toObjectList(values), null);
	}

}
