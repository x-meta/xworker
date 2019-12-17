package xworker.swt.xwidgets.dataitems;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.app.IEditor;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class OpenEditorDataItem  extends DataItem{
	//要打开的编辑器的参数
	Map<String, Object> data = new HashMap<String, Object>();
	
	public OpenEditorDataItem(DataItemContainer dataItemContainer, DataItem parentItem, boolean createChilds,
			Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, createChilds, thing, actionContext);
		
		data.put(IEditor.EDITOR_ID, thing.doAction("getId", actionContext));
		data.put(IEditor.EDITOR_THING, thing.doAction("getEditor", actionContext));
		Map<String, Object> params = thing.doAction("getParams", actionContext);
		if(params != null) {
			data.putAll(params);
		}
	}

	@Override
	public Object getData() {
		return data;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new OpenEditorDataItem(dataItemContainer, parentItem, true, self, actionContext);
	}
}
