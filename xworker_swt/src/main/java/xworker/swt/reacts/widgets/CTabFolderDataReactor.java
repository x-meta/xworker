package xworker.swt.reacts.widgets;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.app.DataEditorProvider;
import xworker.swt.app.IEditor;
import xworker.swt.app.editorContainers.CTabFolderEditorContainer;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class CTabFolderDataReactor extends WidgetDataReactor{
	private static final String TAG = CTabFolderDataReactor.class.getName();
	
	CTabFolder cTabFolder;
	CTabFolderEditorContainer editorContainer;
	
	public CTabFolderDataReactor(CTabFolder cTabFolder, Thing self, ActionContext actionContext) {
		super(cTabFolder, self, actionContext);
		
		this.cTabFolder = cTabFolder;
		this.editorContainer = self.doAction("getEditorContainer", actionContext);
		if(this.editorContainer == null) {
			this.editorContainer = new CTabFolderEditorContainer(cTabFolder, actionContext);
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
		CTabFolder combo = self.doAction("getBindTo", actionContext);
		if(combo != null) {
			CTabFolderDataReactor reactor = new CTabFolderDataReactor(combo, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			Executor.warn(TAG, "CTabFolder is null, can not create CTabFolderDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
