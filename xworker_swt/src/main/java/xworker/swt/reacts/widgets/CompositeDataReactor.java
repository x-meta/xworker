package xworker.swt.reacts.widgets;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.app.DataEditorProvider;
import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;
import xworker.swt.app.editorContainers.CompositeEditorContainer;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class CompositeDataReactor extends WidgetDataReactor implements Listener{
	private static final String TAG = CompositeDataReactor.class.getName();
	public static final String TYPE_EDITOR = "editor";
	public static final String TYPE_CHECK = "check";
	public static final String TYPE_RADIO = "radio";
	
	Composite composite;
	IEditorContainer editorContainer;
	String type;
	
	public CompositeDataReactor(Composite composite, Thing self, ActionContext actionContext) {
		super(composite, self, actionContext);
		
		type = self.doAction("getType", actionContext);
		if(type == null) {
			type = TYPE_EDITOR;
		}
		this.composite = composite;
		if(TYPE_EDITOR.equals(type)) {
			this.editorContainer = self.doAction("getEditorContainer", actionContext);
			if(this.editorContainer == null) {
				this.editorContainer = new CompositeEditorContainer(composite, actionContext);
			}
		}
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(TYPE_EDITOR.equals(type)) {
			if(datas.size() > 0) {
				Object data = datas.get(0);
				
				//创建打打开编辑器的参数
				Map<String, Object> params = DataEditorProvider.createDataParams(data, actionContext);
				if(params != null) {
					Thing editor = (Thing) params.get(IEditor.EDITOR_THING);
					if(editor != null) {					
						//打开编辑器
						String id = (String) params.get(IEditor.EDITOR_ID);
						editorContainer.openEditor(id, editor, params);
					}
				}
			}
		}else {
			for(Control child : composite.getChildren()) {
				if(child instanceof Button) {
					Button button = (Button) child;
					Object data = button.getData();
					boolean have = false;
					for(Object d : datas) {
						if(data.equals(d)) {
							have = true;
							button.setSelection(true);
						}
					}
					if(!have) {
						button.setSelection(false);
					}
				}
			}
		}
	}
	
	private void initComposite() {
		if(TYPE_EDITOR.equals(type)) {
			return;
		}
		
		for(Control child : composite.getChildren()) {
			child.dispose();
		}
		
		for(Object data : datas) {
			Button button = new Button(composite, TYPE_CHECK.equals(type) ? SWT.CHECK : SWT.RADIO);
			button.setText(String.valueOf(data));
			button.setData(data);
			button.addListener(SWT.Selection, this);
			
			if(data instanceof DataObject && ((DataObject) data).isChecked()) {
				button.setSelection(true);
			}
		}
		
		composite.layout();
		if(composite.getParent() != null) {
			composite.getParent().layout();
		}
	}
	
	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		if(TYPE_EDITOR.equals(type)) {
			return;
		}
		
		for(Control child : composite.getChildren()) {
			if(child instanceof Button) {
				Object data = child.getData();
				boolean have = false;
				for(Object obj : datas) {
					if(data != null && data.equals(obj)) {
						((Button) child).setSelection(true);
						have = true;
						break;
					}
				}
				
				if(!have) {
					((Button) child).setSelection(false);
				}
			}
		}
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		initComposite();
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		initComposite();
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		initComposite();
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		initComposite();
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			CompositeDataReactor reactor = new CompositeDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			Executor.warn(TAG, "Composite is null, can not create CompositeDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

	@Override
	public void handleEvent(Event arg0) {
	}


}
