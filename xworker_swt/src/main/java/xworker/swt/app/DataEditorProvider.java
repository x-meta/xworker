package xworker.swt.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class DataEditorProvider {
	private static List<DataEditor> dataEditors = new ArrayList<DataEditor>();
	
	/**
	 * 给定数据，返回能够打开该数据编辑的参数，如果没有适合的编辑器，则返回null。
	 * 
	 * @param data
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createDataParams(Object data, ActionContext actionContext){
		if(data instanceof Map) {
			return (Map<String, Object>) data;
		}
		
		if(actionContext == null) {
			actionContext = new ActionContext();
		}
		
		//遍历获取参数
		for(int i=0; i<dataEditors.size(); i++) {
			DataEditor dataEditor = dataEditors.get(i);
			Map<String, Object> params = dataEditor.createDataParams(data, actionContext);
			if(params != null) {
				return params;
			}
		}
		
		//没有找到支持编辑该数据的，初始化编辑器
		initDataEditors();
		
		//再重新查找一边
		for(int i=0; i<dataEditors.size(); i++) {
			DataEditor dataEditor = dataEditors.get(i);
			Map<String, Object> params = dataEditor.createDataParams(data, actionContext);
			if(params != null) {
				return params;
			}
		}
		
		//没有支持的编辑器
		return null;
	}
	
	/**
	 * 初始化编辑器列表，可以重复初始化。
	 */
	public synchronized static void initDataEditors() {
		for(DataEditor dataEditor : dataEditors) {
			//用于后面检验编辑器是否已经删除了
			dataEditor.exists = false;			
		}
		
		//添加所有编辑器
		Thing registorThing = World.getInstance().getThing("xworker.swt.app.Editors");
		List<Thing> editors = XWorkerUtils.searchRegistThings(registorThing, "child", null, new ActionContext());
		for(Thing editor : editors) {
			boolean have = false;
			for(DataEditor dataEditor : dataEditors) {
				if(dataEditor.thing == editor) {
					dataEditor.exists = true;
					dataEditor.priority = editor.getInt("priority");
					have = true;
					break;
				}
			}
			
			if(!have) {
				DataEditor dataEditor = new DataEditor(editor);
				dataEditors.add(dataEditor);
			}
		}
		
		//移除已经删除的编辑器
		for(int i=0; i<dataEditors.size(); i++) {
			if(dataEditors.get(i).exists = false) {
				dataEditors.remove(i);
				i--;
			}
		}
		
		Collections.sort(dataEditors);
	}
	
	public static class DataEditor implements Comparable<DataEditor>{
		Thing thing;
		int priority;
		boolean exists = true;
		
		public DataEditor(Thing thing) {
			this.thing = thing;
			this.priority = thing.getInt("priority");
		}

		@Override
		public int compareTo(DataEditor o) {
			if(priority < o.priority) {
				return -1;
			}else if(priority == o.priority) {
				return 0;
			}else {
				return 1;
			}
		}
		
		public Map<String, Object> createDataParams(Object data, ActionContext actionContext){
			return thing.doAction("createDataParams", actionContext, "data", data);
		}
	}
}
