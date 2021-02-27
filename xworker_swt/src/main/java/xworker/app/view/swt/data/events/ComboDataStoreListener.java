package xworker.app.view.swt.data.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.app.view.swt.data.DataStore;
import xworker.app.view.swt.data.DataStoreUtils;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.form.SetableModifyListener;

public class ComboDataStoreListener {
	private static final String TAG = ComboDataStoreListener.class.getName();
	
	public static void onReconfig(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) actionContext.get("store");
				if(ccombo == null || ccombo.isDisposed()) {
			return;
		}
		final Object modifyListener = actionContext.get("modifyListener");		
		
		ccombo.getDisplay().asyncExec(new Runnable(){
			public void run(){
				boolean modifyStatus = true;
			    if(modifyListener != null && modifyListener instanceof SetableModifyListener){
			        //此时编辑表单不算修改
			        modifyStatus = ((SetableModifyListener) modifyListener).isEnable();
			        ((SetableModifyListener) modifyListener).setEnable(false);
			    }
			    try{
			    	DSSelectionListener.bind(ccombo, store, actionContext);
			    	
			        ccombo.setData("store", store);
			        
			        //删除所有数据
			        removeAll(ccombo);
			        
			        
			        //重新组建表格的列
			        Thing dataObject = (Thing) store.get("dataObject");
			        if(dataObject == null){
			            return;
			        }
			        
			        //初始化数据，如果存在
			        if(store.getBoolean("dataLoaded") == true){
			            self.doAction("onLoaded", actionContext, UtilMap.toMap("store", store));
			        }
			    }catch(Throwable t){
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener onReconfig error, store=" + store.getMetadata().getPath(), t);
			    	}
			    }finally{
		            if(modifyListener != null && modifyListener instanceof SetableModifyListener){
		                ((SetableModifyListener) modifyListener).setEnable(modifyStatus);
		            }
		        }    
			}
		});
	}
	
	public static void setText(Control ccombo, String text){
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).setText(text);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).setText(text);
        }
	}
	
	public static void select(Control ccombo, int index){
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).select(index);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).select(index);
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).select(index);
        }

	}
	
	public static void removeAll(Control ccombo){
		if(ccombo == null || ccombo.isDisposed()) {
			return;
		}
		
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).removeAll();
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).removeAll();
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).removeAll();
        }

	}
	
	public static void add(Control ccombo, Object value){
		String str = value == null ? "" : String.valueOf(value);
		
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).add(str);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).add(str);
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).add(str);
        }

	}
	
	public static void remove(Control ccombo, int index){
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).remove(index);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).remove(index);
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).remove(index);
        }

	}
	
	public static void add(Control ccombo, Object value, int index){
		String str = value == null ? "" : String.valueOf(value);
		
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).add(str, index);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).add(str, index);
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).add(str, index);
        }

	}
	
	public static void setItem(Control ccombo, Object value, int index){
		String str = value == null ? "" : String.valueOf(value);
		
		if(ccombo instanceof CCombo){
        	((CCombo) ccombo).setItem(index, str);
        }else if(ccombo instanceof Combo){
        	((Combo) ccombo).setItem(index, str);
        }else if(ccombo instanceof org.eclipse.swt.widgets.List){
        	((org.eclipse.swt.widgets.List) ccombo).setItem(index, str);
        }

	}
	
	public static void onLoaded(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) actionContext.get("store");
		final Object modifyListener = actionContext.get("modifyListener");		

		ccombo.getDisplay().asyncExec(new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
			    try{
			    	DSSelectionListener.clear(ccombo);
			        boolean modifyStatus = true;
			        if(modifyListener != null && modifyListener instanceof SetableModifyListener){
			            //此时编辑表单不算修改
			            modifyStatus = ((SetableModifyListener) modifyListener).isEnable();
			            ((SetableModifyListener) modifyListener).setEnable(false);
			        }
			        
			        try{
			        	 //删除所有数据
			        	removeAll(ccombo);
				        if(ccombo instanceof CCombo){
				        	((CCombo) ccombo).removeAll();
				        }else if(ccombo instanceof Combo){
				        	((Combo) ccombo).removeAll();
				        }
			            
			            //log.info("ComboStore loaded");
				        List<DataObject> records = (List<DataObject>) store.get("records");
			            if(records != null){
			                List<Object> rs = new ArrayList<Object>();    
			                if(ccombo.getData("attribute") != null && ((Thing) ccombo.getData("attribute")).getBoolean("optional")){           
			                    rs.add("");
			                    add(ccombo, "");
			                }
			                for(DataObject record : records){
			                    String text = DataStoreUtils.getLabel(store, record);
			                    add(ccombo, text);
			                    
			                    rs.add(record);
			                }
			                
			                ccombo.setData(DataStore.STORE_RECORDS, rs);
			                ccombo.setData(rs);
			            }
			        }finally{
			            if(modifyListener != null && modifyListener instanceof SetableModifyListener){
			                ((SetableModifyListener) modifyListener).setEnable(modifyStatus);
			            }
			        }
			        
			        self.doAction("setSelection", actionContext);
			    }catch(Throwable t) {
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener onLoaded error, store=" + store.getMetadata().getPath(), t);
			    	}
			    }    
			}
		});
	}
	
	public static void beforeLoad(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		//final Thing store = (Thing) actionContext.get("store");
		final Object modifyListener = actionContext.get("modifyListener");	

		ccombo.getDisplay().asyncExec(new Runnable(){
			public void run(){
				boolean modifyStatus = true;
			    if(modifyListener != null && modifyListener instanceof SetableModifyListener){
			        //此时编辑表单不算修改
			        modifyStatus = ((SetableModifyListener) modifyListener).isEnable();
			        ((SetableModifyListener) modifyListener).setEnable(false);
			    }
			    try{
			        //先清空数据
			        removeAll(ccombo);
			        
			        add(ccombo, "loading...");
			    }catch(Throwable t){
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener beforeLoad error", t);
			    	}
			    }finally{
		            if(modifyListener != null && modifyListener instanceof SetableModifyListener){
		                ((SetableModifyListener) modifyListener).setEnable(modifyStatus);
		            }
		        }    
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void setSelection(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) self.get("store");
		final Object modifyListener = actionContext.get("modifyListener");
		
		//获取要选择的值
		Object value = null;
		if(actionContext.get("record") != null){
		    value = actionContext.get("record");
		}else if(actionContext.get("id") != null){
		    value = actionContext.get("id") ;
		    
		}
		if(value == null){
		    value = self.get("value");
		}else{
		    self.put("value", value);
		}

		//log.info("setSelection=" + value);
		boolean modifyStatus = true;
		if(modifyListener != null && modifyListener instanceof SetableModifyListener){
		    //此时不算修改
		    modifyStatus = ((SetableModifyListener) modifyListener).isEnable();
		    ((SetableModifyListener) modifyListener).setEnable(false);
		}
		    
		try{
		    //选择值
		    boolean seted = false;
		    if(value != null && UtilData.isTrue(store.get("dataLoaded"))){
		        List<DataObject> records = (List<DataObject>) store.get("records");
		        int index = 0;
		        if(records != null){
		            int i = 0;
		            for(DataObject record : records){
		                if(value == record){
		                     index = i;
		                     break;
		                }
		                
		                Object key = value;
		                if(value instanceof DataObject){
		                    Object[][] keys = ((DataObject) value).getKeyAndDatas();
		                    if(keys != null && keys.length > 0){
		                        key = keys[0][1];
		                    }
		                }
		                Object recordKey = null;
		                Object[][] keys = record.getKeyAndDatas();
		                if(keys != null && keys.length > 0){
		                    recordKey = keys[0][1];
		                }
		                if(key == recordKey || (key != null && String.valueOf(key).equals(String.valueOf(recordKey)))){
		                    index = i;
		                    if(ccombo.getData("attribute") != null && ((Thing) ccombo.getData("attribute")).getBoolean("optional")){
		                        select(ccombo, index + 1);
		                    }else{
		                        select(ccombo, index);
		                    }
		                    return;
		                }
		                i++;
		            }
		        }
		        
		        if(!seted && value != null && value instanceof String){
		            if((ccombo.getStyle() & SWT.READ_ONLY)  == 0){
		                setText(ccombo, (String) value);
		            }
		        }
		    }
		}finally{
		    if(modifyListener != null && modifyListener instanceof SetableModifyListener){
		        ((SetableModifyListener) modifyListener).setEnable(modifyStatus);
		    }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void onInsert(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) self.get("store");
		//final Object modifyListener = actionContext.get("modifyListener");
		final Integer index = (Integer) actionContext.get("index");
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		final Thing config = (Thing) store.get("config");
		final Thing dataObject = (Thing) store.get("dataObject");

		ccombo.getDisplay().asyncExec(new Runnable(){
			public void run(){
			    try{
			        List<Object> rs = (List<Object>) ccombo.getData(DataStore.STORE_RECORDS);    
			        int i = index;
			        for(DataObject record : records){
			        	//过滤重复
			        	boolean have = false;
			        	for(Object r : rs) {
			        		if(r == record) {
			        			have = true;
			        			break;
			        		}
			        	}
			        	if(have) {
			        		continue;
			        	}
			        	
			            String labelField = config.getStringBlankAsNull("labelField");
			            if(labelField == null || labelField == ""){
			                labelField = dataObject.getString("displayName");
			            }
			            String text = "no label feild";
			            if(labelField != null && labelField != ""){
			                text = record.getString(labelField);
			                if(text == null || text == ""){
			                    text = "";
			                }else{
			                    text = String.valueOf(text);
			                }
			            }
			            
			            add(ccombo, text, i);
			            
			            rs.add(i, record);
			            i++;
			        }
			    }catch(Throwable t){
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener onInsert error， store=" + store.getMetadata().getPath(), t);
			    	}
			    }    
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void onUpdate(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) self.get("store");
		//final Object modifyListener = actionContext.get("modifyListener");
		//final Integer index = (Integer) actionContext.get("index");
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		final Thing config = (Thing) store.get("config");
		final Thing dataObject = (Thing) store.get("dataObject");
		
		ccombo.getDisplay().asyncExec(new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
			    try{
			        List<Object> rs = (List<Object>) ccombo.getData(DataStore.STORE_RECORDS);
			        for(DataObject record : records){
			        	for(int i=0; i<rs.size(); i++){
			                if(rs.get(i) == record){
			                    String labelField = config.getStringBlankAsNull("labelField");
			                    if(labelField == null || labelField == ""){
			                        labelField = dataObject.getString("displayName");
			                    }
			                    String text = "no label feild";
			                    if(labelField != null && labelField != ""){
			                        text = record.getString(labelField);
			                        if(text == null || text == ""){
			                            text = "";
			                        }else{
			                            text = String.valueOf(text);
			                        }
			                    }
			                    
			                    setItem(ccombo, text, i);
			                    break;
			                }
			            }
			        }
			    }catch(Throwable t){
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener onUpdate error, store=" + store.getMetadata().getPath(), t);
			    	}
			    }    
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void onRemove(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Control ccombo = (Control) self.get("combo");
		final Thing store = (Thing) self.get("store");
		//final Object modifyListener = actionContext.get("modifyListener");
		//final Integer index = (Integer) actionContext.get("index");
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		//final Thing config = (Thing) store.get("config");
		//final Thing dataObject = (Thing) store.get("dataObject");
		
		ccombo.getDisplay().asyncExec(new Runnable(){
			public void run(){
				try{
			        List<Object> rs = (List<Object>) ccombo.getData(DataStore.STORE_RECORDS);
			        DSSelectionListener.removeRecords(ccombo, records);
			        for(DataObject record : records){
			            for(int i=0; i<rs.size(); i++){
			                if(rs.get(i) == record){
			                    remove(ccombo, i);
			                    rs.remove(i);
			                    break;
			                }
			            }
			        }
			    }catch(Throwable t){
			    	if(!ccombo.isDisposed()) {
			    		Executor.error(TAG, "CombotoreListener onRemove error, store=" + store.getMetadata().getPath(), t);
			    	}
			    }    
			}
		});
	}
}
