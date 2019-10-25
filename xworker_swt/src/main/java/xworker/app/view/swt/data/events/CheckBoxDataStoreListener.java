/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.app.view.swt.data.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.swt.form.FormModifyListener;

public class CheckBoxDataStoreListener {
	private static Logger log = LoggerFactory.getLogger(CheckBoxDataStoreListener.class);
	
	public static void onReconfig(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");		
		//final World world = World.getInstance();
		
		SwtStoreUtils.runSync(store, composite.getDisplay(), new Runnable(){
			public void run(){
			    try{
			    	DSSelectionListener.bind(composite, store, actionContext);
			    	
			        //删除所有数据
			        for(Control children : composite.getChildren()){
			            children.dispose();
			        }
			        
			        //重新组建表格的列
			        Thing dataObject = (Thing) store.get("dataObject");
			        if(dataObject == null){
			            return;
			        }
			        
			        //初始化数据，如果存在
			        Boolean dataLoaded = store.getBoolean("dataLoaded");
			        if(dataLoaded != null && dataLoaded == true){
			            self.doAction("onLoaded", actionContext, UtilMap.toMap(new Object[]{"store", store}));
			        }
			        composite.layout();
			        composite.getParent().layout();
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener onReconfig error", t);
			    }
			}
		});
	}
	
	public static void onLoaded(final ActionContext actionContext){
		onLoaded(actionContext, false);
	}
	
	public static void onLoaded(final ActionContext actionContext, final boolean isRadio){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");
		final FormModifyListener modifyListener = (FormModifyListener) actionContext.get("modifyListener");
		//final World world = World.getInstance();
		
		SwtStoreUtils.runAsync(store, composite.getDisplay(), new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
				try{
					DSSelectionListener.clear(composite);
					
			        //删除所有数据
			        for(Control children : composite.getChildren()){
			            children.dispose();
			        }
			        
					List<DataObject> records = (List<DataObject>) store.get("records");
			        if(records != null){
			            List<DataObject> rs = new ArrayList<DataObject>();
			            Thing config = (Thing) store.get("config");
			            String labelField = config.getStringBlankAsNull("labelField");
			            if(labelField == null){
			            	Thing dataObject = (Thing) store.get("dataObject");
			            	labelField = dataObject.getStringBlankAsNull("displayName");
			            }
			           		            
			            boolean lisEnable = false; 
			            if(modifyListener != null){
			            	lisEnable = modifyListener.isEnable();
				            modifyListener.setEnable(false);
			            }
			            try{
			            	Thing attribute = (Thing) actionContext.get("attribute");
			            	
			            	if(isRadio && attribute != null && attribute.getBoolean("optional")){
			                    Button radioButton1 = new Button(composite, SWT.RADIO);
			                    radioButton1.setText("");
			                    radioButton1.setData("");
			                }  
			            	
				            for(DataObject record : records){			               
				                //button
				            	createButton(labelField, isRadio, modifyListener, composite, record, actionContext);
				               
				                rs.add(record);
				            }
			            }finally{
			            	if(modifyListener != null){
			            		modifyListener.setEnable(lisEnable);
			            	}
				        }
			            composite.setData("_store_records", rs);
			            self.doAction("setSelection", actionContext);
			        
			            composite.layout();
			            composite.getParent().layout();
			        }
			        
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener onLoaded error", t);
			    }
			}
		});
	}
	
	private static Button createButton(String labelField, boolean isRadio, FormModifyListener modifyListener, Composite composite, DataObject record, ActionContext actionContext) {
		 String text = "no label feild";
         if(labelField != null ){
             Object value = record.get(labelField);
         	text = value == null ? "" : String.valueOf(value);			                    
         }
         
		 Button radioButton1 = new Button(composite, isRadio ? SWT.RADIO : SWT.CHECK);
         radioButton1.setText(text);
         
         //value
         radioButton1.setData("record", record);
         Object[][] keys = record.getKeyAndDatas();
         if(keys != null && keys.length > 0){
             radioButton1.setData(keys[0][1]);
         }
         
         if(actionContext.get("modifyListener") !=null){
             radioButton1.addSelectionListener(modifyListener.getSelectionListener());
         }
         
         DSSelectionListener listener = DSSelectionListener.getListener(composite);
         if(listener != null) {
        	 radioButton1.addListener(SWT.Selection, listener);
         }
         
         return radioButton1;
	}
	
	public static void beforeLoad(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");
		//final World world = World.getInstance();
		SwtStoreUtils.runAsync(store, composite.getDisplay(), new Runnable(){
			public void run(){
				try{
			        //删除所有数据
			        for(Control children : composite.getChildren()){
			            children.dispose();
			        }
			        
			        Label label = new Label(composite, SWT.NONE);
			        label.setText("loading...");
			        
			        composite.layout();
			        composite.getParent().layout();
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener beforeLoad error", t);
			    }
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void setSelection(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) self.get("store");
		//final World world = World.getInstance();
		DSSelectionListener selectionListener = DSSelectionListener.getListener(composite);				
		try {
			selectionListener.setEnabled(false);
			selectionListener.clear();
			
			//获取要选择的值
			Object value = null;
			if(actionContext.get("record") != null){
			    value = actionContext.get("record");
			}else if(actionContext.get("id") != null){
			    value = actionContext.get("id");
			    
			}
			if(value == null){
			    value = composite.getData("value");
			}else{
			    self.put("value", value);
			}
	
			//选择值
			Boolean dataLoaded = store.getBoolean("dataLoaded");
			if(value != null && dataLoaded != null && dataLoaded == true){
			    List<DataObject> records = (List<DataObject>) store.get("records");
			    int index = 0;
			    if(records != null){
			        int i = 0;			        
			        for(DataObject record : records){
			            if(value == record){
			                 index = i;
			                 ((Button) composite.getChildren()[index]).setSelection(true);
			                 break;
			            }
			            
			            List<String> vkeys = new ArrayList<String>();
			            if(value instanceof String){
			            	for(String v : ((String) value).split("[,]")){
			            		vkeys.add(v);
			            	}
			            }else{
			                vkeys.add(String.valueOf(value));
			            }
			            for(String key : vkeys){
			                if(value instanceof DataObject){
			                    Object[][] keys = ((DataObject) value).getKeyAndDatas();
			                    if(keys != null && keys.length > 0){
			                        key = String.valueOf(keys[0][1]);
			                    }
			                }
			                String recordKey = null;
			                Object[][] keys = record.getKeyAndDatas();
			                if(keys != null && keys.length > 0){
			                    recordKey = String.valueOf(keys[0][1]);
			                }
			                		 
			                if(key != null && key.equals(recordKey)){
			                    index = i;
			                    ((Button) composite.getChildren()[index]).setSelection(true);
			                    break;
			                }
			            }
			            i++;
			        }
			    }
			}		
		}finally {
			selectionListener.setEnabled(true);
		}
	}
	
	public static void onInsert(final ActionContext actionContext){
		onInsert(actionContext, false);
	}
	
	@SuppressWarnings("unchecked")
	public static void onInsert(final ActionContext actionContext, final boolean isRadio){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");
		//final World world = World.getInstance();
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		final FormModifyListener modifyListener = (FormModifyListener) actionContext.get("modifyListener");
		
		SwtStoreUtils.runAsync(store, composite.getDisplay(), new Runnable(){
			public void run(){
				try{
			        List<DataObject> rs = (List<DataObject>) composite.getData("_store_records");    
			        //int i = (Integer) actionContext.get("index");
			        Thing config = (Thing) store.get("config");
			        String labelField = config.getStringBlankAsNull("labelField");
		            if(labelField == null){
		            	Thing dataObject = (Thing) store.get("dataObject");
		            	labelField = dataObject.getStringBlankAsNull("displayName");
		            }
		           
		            boolean lisEnable = false;
		            if(modifyListener != null){
		            	lisEnable = modifyListener.isEnable();
			            modifyListener.setEnable(false);
		            }
		            try{
				        for(DataObject record : records){
				        	//过滤重复
				        	boolean have = false;
				        	for(Control child : composite.getChildren()) {
				        		if(child.getData("record") == record) {
				        			have = true;
				        			break;
				        		}
				        	}
				        	if(have) {
				        		continue;
				        	}
				        	
				        	createButton(labelField, isRadio, modifyListener, composite, record, actionContext);
				            rs.add(record);
				            //i++;
				        }
		            }finally{
		            	if(modifyListener != null){
		            		modifyListener.setEnable(lisEnable);
		            	}
		            }
			        
			        composite.layout();
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener onInsert error", t);
			    }
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void onUpdate(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");
		//final World world = World.getInstance();
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		
		SwtStoreUtils.runAsync(store, composite.getDisplay(), new Runnable(){
			public void run(){
				try{
			        List<DataObject> rs = (List<DataObject>) composite.getData("_store_records");
			        Thing config = (Thing) store.get("config");
			        String labelField = config.getStringBlankAsNull("labelField");
		            if(labelField == null){
		            	Thing dataObject = (Thing) store.get("dataObject");
		            	labelField = dataObject.getStringBlankAsNull("displayName");
		            }
			        for(DataObject record : records){
			            for(int i=0; i<rs.size(); i++){
			                if(rs.get(i) == record){			                   
			                	String text = "no label feild";
					            if(labelField != null){
					            	Object value = record.get(labelField);
					            	if(value == null){
					            		text = "";
					            	}else{
					            		text = String.valueOf(value);
					            	}			
					            }
			                    
			                    ((Button) composite.getChildren()[i]).setText(text);
			                    break;
			                }
			            }
			        }
			        
			        composite.layout();
			        composite.getParent().layout();
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener onUpdate error", t);
			    }
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void onRemove(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Composite composite = (Composite) self.get("composite");
		final Thing store = (Thing) actionContext.get("store");
		//final World world = World.getInstance();
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		
		SwtStoreUtils.runAsync(store, composite.getDisplay(), new Runnable(){
			public void run(){
				try{
			        List<DataObject> rs = (List<DataObject>) composite.getData("_store_records");
			        for(DataObject record : records){
			            for(int i=0; i<rs.size(); i++){
			                if(rs.get(i) == record && i < composite.getChildren().length){
			                    composite.getChildren()[i].dispose();
			                    rs.remove(i);
			                    break;
			                }
			            }
			        }
			        
			        composite.layout();
			        composite.getParent().layout();
			    }catch(Throwable t){
			        log.error("CheckBoxDataStoreListener onRemove error", t);
			    }    
			}
		});
	}	
}