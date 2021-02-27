package xworker.app.view.swt.data.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.editor.EditorModifyListener;

public class EditDataObjectsDialog {
	private static final String TAG = EditDataObjectsDialog.class.getName();
	
	@SuppressWarnings("unchecked")
	public static void saveAll(ActionContext actionContext){
		List<Boolean> statusArray = actionContext.getObject("statusArray");
		int currentIndex  = (Integer) actionContext.get("currentIndex");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing form = actionContext.getObject("form");
		Map<Integer, DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		Thing dataStore = actionContext.getObject("dataStore");
		Label modifyLabel = actionContext.getObject("modifyLabel");
		Shell shell = actionContext.getObject("shell");
		
		int count = 0;
		try{
		    int index = 0;
		    for(boolean status : statusArray){
		        if(status){
		            //保存
		            Object values = null;
		            if(currentIndex == index){
		                values = form.doAction("getDataObject", actionContext);
		            }else{
		                values = unsavedDataObjects.get(index);
		            }
		            DataObject dataObject = dataObjects.get(index);
		            dataObject.putAll((Map<String, Object>) values);
		            dataStore.doAction("update", parentContext, UtilMap.toMap("record", dataObject));
		            dataObjects.set(index, dataObject);
		            unsavedDataObjects.remove(index);
		            count++;
		        }
		        
		        index++;
		    }
		    
		    modifyLabel.setText(" ");
		    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		    box.setText(shell.getText());
		    if(count > 0){
		        box.setMessage("保存成功，已保存" + count + "条记录。");
		    }else{
		        box.setMessage("没有修改的记录要保存。");
		    }
		    box.open();
		}catch(Exception e){
			Executor.error(TAG, "save all error", e);
		    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		    box.setText(shell.getText());
		    if(count > 0){
		        box.setMessage("保存失败，已保存" + count + "条记录，失败信息：" + e.getMessage());
		    }else{
		        box.setMessage("保存失败，失败信息：" + e.getMessage());
		    }
		    box.open();
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused"})
	public static void okButtonAction(ActionContext actionContext){
		List<Boolean> statusArray = actionContext.getObject("statusArray");
		int currentIndex  = (Integer) actionContext.get("currentIndex");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing form = actionContext.getObject("form");
		Map<Integer, DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		Map<Integer, DataObject> modifyedDataObjects = actionContext.getObject("modifyedDataObjects");
		Thing dataStore = actionContext.getObject("dataStore");
		//Label modifyLabel = actionContext.getObject("modifyLabel");
		Shell shell = actionContext.getObject("shell");
		DataObject dataObject = actionContext.getObject("dataObject");
		
		int count = 0;
		try{
		    int index = 0;
		    for(boolean status : statusArray){
		        if(status){
		            //保存
		            Object values = null;
		            if(currentIndex == index){
		                values = form.doAction("getDataObject", actionContext);
		                modifyedDataObjects.put(index, dataObject);
		            }else{
		                values = unsavedDataObjects.get(index);
		            }
		            DataObject dataObject1 = dataObjects.get(index);
		            dataObject.putAll((Map<String, Object>) values);
		            dataStore.doAction("update", parentContext, UtilMap.toMap("record", dataObject1));
		            unsavedDataObjects.remove(index);
		        }
		        
		        index++;
		    }
		    
		    //返回所有曾经修改过的记录
		    List<DataObject> result = new ArrayList<DataObject>();
		    index = 0;
		    for(boolean status : statusArray){
		        if(modifyedDataObjects.get(index) != null){
		            result.add(modifyedDataObjects.get(index));
		        }
		        index++;
		    }
		    actionContext.getScope(0).put("result", result);
		    
		    shell.dispose();   
		}catch(Exception e){
		    Executor.error(TAG, "ok button error", e);
		    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		    box.setText(shell.getText());
		    if(count > 0){
		        box.setMessage("保存失败，已保存" + count + "条记录，失败信息：" + e.getMessage());
		    }else{
		        box.setMessage("保存失败，失败信息：" + e.getMessage());
		    }
		    box.open();
		}
	}
	
	public static void modify(ActionContext actionContext){
		List<Boolean> statusArray = actionContext.getObject("statusArray");
		int currentIndex  = (Integer) actionContext.get("currentIndex");
		//ActionContext parentContext = actionContext.getObject("parentContext");
		//Thing form = actionContext.getObject("form");
		//List<DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		//List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		//List<DataObject> modifyedDataObjects = actionContext.getObject("modifyedDataObjects");
		//Thing dataStore = actionContext.getObject("dataStore");
		Label modifyLabel = actionContext.getObject("modifyLabel");
		//Shell shell = actionContext.getObject("shell");
		//DataObject dataObject = actionContext.getObject("dataObject");
		
		modifyLabel.setText("***");
		statusArray.set(currentIndex, true);
		Executor.info(TAG, "modify currentIndex=" + currentIndex + "=" + statusArray.get(currentIndex));
	}
	
	public static void init(ActionContext actionContext){
		//int currentIndex  = (Integer) actionContext.get("currentIndex");
		World world = World.getInstance();
		//ActionContext parentContext = actionContext.getObject("parentContext");
		Thing form = actionContext.getObject("form");
		//List<DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		//List<DataObject> modifyedDataObjects = actionContext.getObject("modifyedDataObjects");
		Thing dataStore = actionContext.getObject("dataStore");
		//Label modifyLabel = actionContext.getObject("modifyLabel");
		Shell shell = actionContext.getObject("shell");
		//DataObject dataObject = actionContext.getObject("dataObject");
		ActionContainer modifyActions = actionContext.getObject("modifyActions");
		Thing pageDataStore = actionContext.getObject("pageDataStore");
		
		actionContext.getScope(0).put("modifyListener",  new EditorModifyListener(modifyActions));
		List<Boolean> statusArray = new ArrayList<Boolean>(); //记录每条记录是否被修改
		for(@SuppressWarnings("unused") DataObject db : dataObjects){
		    statusArray.add(false);
		}
		actionContext.getScope(0).put("statusArray", statusArray);
		actionContext.getScope(0).put("modifyedDataObjects", new HashMap<Integer, DataObject>()); //记录变更的数据对象
		actionContext.getScope(0).put("unsavedDataObjects", new HashMap<Integer, DataObject>()); //记录变更的数据对象
		actionContext.getScope(0).put("currentIndex", -1); //记录变更的数据对象

		//设置分页的数据对象
		Thing listDataObject = new Thing("xworker.dataObject.java.ListDataObject");
		listDataObject.put("listData", "dataObjects");
		listDataObject.put("extends", ((Thing) dataStore.get("dataObject")).getMetadata().getPath());
		pageDataStore.doAction("setDataObject", actionContext, UtilMap.toMap("dataObject", listDataObject));

		//设置表单的导航监听
		Thing listener = world.getThing("xworker.app.view.swt.data.dialogs.EditDataObjectsDialog/@ListDataStoreListener");
		pageDataStore.doAction("addListener", actionContext, UtilMap.toMap("listener", listener));
		pageDataStore.doAction("load", actionContext);

		form.doAction("setDataObject", actionContext, "dataObject", dataStore.get("dataObject"));
		shell.pack();
	}
	
	public static void onLoaded(ActionContext actionContext){
		//int currentIndex  = (Integer) actionContext.get("currentIndex");
		//World world = World.getInstance();
		List<Boolean> statusArray = actionContext.getObject("statusArray");
		//ActionContext parentContext = actionContext.getObject("parentContext");
		Thing form = actionContext.getObject("form");
		HashMap<Integer, DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		//List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		//HashMap<Integer, DataObject> modifyedDataObjects = actionContext.getObject("modifyedDataObjects");
		//Thing dataStore = actionContext.getObject("dataStore");
		Label modifyLabel = actionContext.getObject("modifyLabel");
		//Shell shell = actionContext.getObject("shell");
		//DataObject dataObject = actionContext.getObject("dataObject");
		//ActionContainer modifyActions = actionContext.getObject("modifyActions");
		Thing pageDataStore = actionContext.getObject("pageDataStore");
		EditorModifyListener modifyListener = actionContext.getObject("modifyListener");
		Thing store = actionContext.getObject("store");
		//int currentIndex  = (Integer) actionContext.get("currentIndex");
		
		try{
		    modifyListener.setEnable(false);
		    PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
		    if(unsavedDataObjects.get(pageInfo.getStart()) != null){
		        form.doAction("setValues", actionContext, "values", unsavedDataObjects.get(pageInfo.getStart()));
		    }else{
		    	List<DataObject> records = store.getObject("records");
		        if(records != null && records.size() > 0){
		            form.doAction("setValues", actionContext, "values", records.get(0));
		        }
		    }
		    		    		 
		    PageInfo ppifno = PageInfo.getPageInfo(pageDataStore.get("pageInfo"));
		    if(statusArray.get(ppifno.getStart())){
		        modifyLabel.setText("***");
		    }else{
		        modifyLabel.setText(" ");
		    }
		    
		    actionContext.put("currentIndex", pageInfo.getStart());
		}finally{
		    modifyListener.setEnable(true);
		}
	}
	
	public static void beforeLoad(ActionContext actionContext){
		//int currentIndex  = (Integer) actionContext.get("currentIndex");
		//World world = World.getInstance();
		Event event = actionContext.getObject("event");
		List<Boolean> statusArray = actionContext.getObject("statusArray");
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing form = actionContext.getObject("form");
		HashMap<Integer, DataObject> unsavedDataObjects = actionContext.getObject("unsavedDataObjects");
		List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		HashMap<Integer, DataObject> modifyedDataObjects = actionContext.getObject("modifyedDataObjects");
		Thing dataStore = actionContext.getObject("dataStore");
		Label modifyLabel = actionContext.getObject("modifyLabel");
		Shell shell = actionContext.getObject("shell");
		//DataObject dataObject = actionContext.getObject("dataObject");
		//ActionContainer modifyActions = actionContext.getObject("modifyActions");
		//Thing pageDataStore = actionContext.getObject("pageDataStore");
		//EditorModifyListener modifyListener = actionContext.getObject("modifyListener");
		Thing store = actionContext.getObject("store");
		int currentIndex  = (Integer) actionContext.get("currentIndex");
		PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
		Button autoSaveButton = actionContext.getObject("autoSaveButton");
		
		if(currentIndex != pageInfo.getStart()){
		    Executor.info(TAG, "status=" + currentIndex + "=" + statusArray.get(currentIndex));
		    if(statusArray.get(currentIndex)){
		        DataObject values = (DataObject) form.doAction("getValues", actionContext);
		        modifyedDataObjects.put(currentIndex, values);
		        unsavedDataObjects.put(currentIndex, values);
		        if(autoSaveButton.getSelection()){
		            //自动保存已经修改的
		            try{                
		                DataObject dataObject = dataObjects.get(currentIndex);
		                dataObject.putAll(values);
		                dataStore.doAction("update", parentContext, "record", dataObject);                
		                dataObjects.set(currentIndex,  dataObject);
		                statusArray.set(currentIndex, false);
		                unsavedDataObjects.remove(currentIndex);
		                Executor.info(TAG, "updated");
		            }catch(Exception e){
		                Executor.error(TAG, "update data error", e);
		                MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		                box.setText(shell.getText());
		                box.setMessage("" + e.getMessage());
		                box.open();
		                
		                event.doit = false;
		            }
		        }
		    }
		}else{
		    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		    box.setText(shell.getText());
		    box.setMessage("重新设置表单的数据将放弃当前记录的修改，要继续吗？");
		    if(SWT.OK == box.open()){
		        DataObject dataObject = dataObjects.get(currentIndex);
		        form.doAction("setValues", actionContext, "values", dataObject);
		        statusArray.set(currentIndex, false);
		        unsavedDataObjects.remove(currentIndex);
		        modifyLabel.setText(" ");
		    }
		}
		return;
	}
}
