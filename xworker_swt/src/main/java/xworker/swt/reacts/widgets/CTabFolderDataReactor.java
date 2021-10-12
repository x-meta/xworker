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
import xworker.workbench.EditorParams;
import xworker.workbench.WorkbenchUtils;

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
		for(Object data : datas){
			//创建打打开编辑器的参数
			List<EditorParams<Object>> editorParamsList = WorkbenchUtils.getEditors("swt", data, actionContext);
			if (editorParamsList.size() > 0) {
				EditorParams<Object> editorParams = editorParamsList.get(0);
				editorContainer.openEditor(editorParams.getId(), editorParams.getEditor(), editorParams.getParams());
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
