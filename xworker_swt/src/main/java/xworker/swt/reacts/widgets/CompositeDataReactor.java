package xworker.swt.reacts.widgets;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.app.DataEditorProvider;
import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;
import xworker.swt.app.editorContainers.CompositeEditorContainer;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class CompositeDataReactor extends WidgetDataReactor{
	private static Logger logger = LoggerFactory.getLogger(CompositeDataReactor.class);
	
	Composite composite;
	IEditorContainer editorContainer;
	
	public CompositeDataReactor(Composite composite, Thing self, ActionContext actionContext) {
		super(composite, self, actionContext);
		
		this.composite = composite;
		this.editorContainer = self.doAction("getEditorContainer", actionContext);
		if(this.editorContainer == null) {
			this.editorContainer = new CompositeEditorContainer(composite, actionContext);
		}
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
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
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			CompositeDataReactor reactor = new CompositeDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("Composite is null, can not create CompositeDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}


}
