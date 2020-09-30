package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ThingForm implements Listener, ModifyListener{
	private static Logger logger = LoggerFactory.getLogger(ThingForm.class);
	private static final String KEY = "__ThingForm__KEY__";
	
	/** 表单事物 */
	public Thing form;	
	Event lastEvent;
	
	List<ThingFormListener> listeners = new ArrayList<ThingFormListener>();
	
	public ThingForm() {		
	}
	
	public void addListener(ThingFormListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ThingFormListener listener) {
		listeners.remove(listener);
	}
	
	public void setDescriptor(Thing descriptor) {
		form.doAction("setDescriptor", getFormContext(), "descriptor", descriptor);
	}
	
	public void setThing(Thing thing) {
		form.doAction("setThing", getFormContext(), "thing", thing);
	}
	
	public Map<String, Object> getValues(){
		return form.doAction("getValues", getFormContext());
	}
	
	public void setValues(Map<String, Object> values) {
		form.doAction("setValues", getFormContext(), "values", values);
	}
	
	public void setPartialValues(Map<String, Object> values) {
		form.doAction("setPartialValues", getFormContext(), "values", values);
	}
	
	public boolean validate() {
		return UtilData.isTrue(form.doAction("validate", getFormContext()));
	}
	/**
	 * 返回表单所在的SWT的变量上下文。
	 * 
	 * @return
	 */
	public ActionContext getParentContext() {
		return (ActionContext) form.getData("parentActionContext");
	}
	
	/**
	 * 返回表单自己的变量上下文。
	 * 
	 * @return
	 */
	public ActionContext getFormContext() {
		return (ActionContext) form.getData("actionContext");
	}
	
	/**
	 * 返回表单原始的模型。
	 * 
	 * @return
	 */
	public Thing getFormThing() {
		return (Thing) form.getData("formThing");
	}
	
	public Control getControl() {
		return (Control) form.getData("parent");
	}

	/** 
	 * 监听表单的修改事件。
	 * 
	 * @param e
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		for(ThingFormListener listener : listeners) {
			listener.modified(this);
		}
	}

	/**
	 * 监听表单的默认选择事件。
	 * 
	 * @param event
	 */
	@Override
	public void handleEvent(Event event) {
		if(lastEvent == event) {
			return;
		}else {
			lastEvent = event;
		}
		
		Thing self = this.getFormThing();
		try {			
			for(ThingFormListener listener : listeners) {
				listener.defaultSelection(this);
			}
			
			self.doAction("defaultSelection", this.getParentContext(), "event", event, "thingForm", this);
		}catch(Exception e) {
			logger.warn("DefaultSelection exception, form=" + self.getMetadata().getPath(), e);
		}
	}
	
	public static ThingForm getThingForm(Thing form) {
		return (ThingForm) form.getData(KEY);
	}
	
	public void setForm(Thing form) {
		this.form = form;
		form.setData(KEY, this);
	}
}
