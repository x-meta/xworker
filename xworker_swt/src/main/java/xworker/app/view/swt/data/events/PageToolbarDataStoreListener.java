package xworker.app.view.swt.data.events;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class PageToolbarDataStoreListener {
	private static final String TAG = PageToolbarDataStoreListener.class.getName();
	
	public static void onReconfig(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Thing store = (Thing) self.get("store");
		//final Object modifyListener = actionContext.get("modifyListener");
		//final Integer index = (Integer) actionContext.get("index");
		//final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		//final Thing config = (Thing) store.get("config");
		//final Thing dataObject = (Thing) store.get("dataObject");
		
		Thing pageToolbar = (Thing) self.get("pageToolbar");
		ActionContext ac = (ActionContext) pageToolbar.get("context");
		ToolItem firtPageItem = (ToolItem) ac.get("firtPageItem");
		
		firtPageItem.getDisplay().asyncExec(new Runnable(){
			public void run(){
				 try{
			        //初始化数据，如果存在
			        self.doAction("onLoaded", actionContext);
			    }catch(Throwable t){
			        Executor.error(TAG, "PageToolbarDataStoreListener onReconfig error, store=" + store.getMetadata().getPath(), t);
			    }  
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void onLoaded(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Thing store = (Thing) self.get("store");
		//final Object modifyListener = actionContext.get("modifyListener");
		//final Integer index = (Integer) actionContext.get("index");
		final List<DataObject> records = (List<DataObject>) store.get("records");
		//final Thing config = (Thing) store.get("config");
		//final Thing dataObject = (Thing) store.get("dataObject");
		
		final Thing pageToolbar = (Thing) self.get("pageToolbar");
		final ActionContext ac = (ActionContext) pageToolbar.get("context");
		ToolItem firtPageItem = (ToolItem) ac.get("firtPageItem");
		
		if(firtPageItem.isDisposed()){
			return;
		}
		
		firtPageItem.getDisplay().asyncExec(new Runnable(){
			public void run(){
				try{
					//ToolItem beforePageItem = (ToolItem) ac.get("beforePageItem");
					ToolItem afterPageItem = (ToolItem) ac.get("afterPageItem");		
					ToolItem firtPageItem = (ToolItem) ac.get("firtPageItem");
					ToolItem prePgaeItem = (ToolItem) ac.get("prePgaeItem");
					ToolItem pageTextItem = (ToolItem) ac.get("pageTextItem");
					ToolItem nextPageItem = (ToolItem) ac.get("nextPageItem");
					ToolItem lastPageItem = (ToolItem) ac.get("lastPageItem");
					ToolItem refresItem = (ToolItem) ac.get("refresItem");
					//Label beforePageLabel = (Label) ac.get("beforePageLabel");
					Label afterPageLabel = (Label) ac.get("afterPageLabel");
					Label infoLabel = (Label) ac.get("infoLabel");
					Text pageText = (Text) ac.get("pageText");
					Composite pagingComposite = (Composite) ac.get("pagingComposite");					
					
			        if(store != null){
			            //self.store = store;
			            int currentPage = 0;
			            int totalPage = 0;
			            int start = 0;
			            int totalCount = 0;    
			            int offset = 0;
			            int limit = 0;
			                			            
			            if(store.get("pageInfo") != null){
			                PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
			                if(pageInfo.getTotalCount() == 0){
			                    totalCount = records != null ? records.size() : 0; 
			                }else{
			                    totalCount = (int) pageInfo.getTotalCount();
			                }
			                start = (int) pageInfo.getStart();        
			                limit = (int) pageInfo.getLimit();
			                if(limit > 0){
			                    totalPage = totalCount / limit + (totalCount % limit > 0 ? 1 : 0);
			                    currentPage = start / limit + 1;
			                }else{
			                    totalPage = 1;
			                    currentPage = 1;
			                }        
			                offset = start + (records != null ? (records.size() -1) : 0);        
			            }else{
			                totalCount = records != null ? records.size() : 0; 
			                start = 0;
			                limit = 0;
			                totalPage = 1;
			                currentPage = 1;
			                offset = totalCount;
			            }
			                
			            if(currentPage > 1){
			                firtPageItem.setEnabled(true);
			                prePgaeItem.setEnabled(true);
			            }else{
			                firtPageItem.setEnabled(false);
			                prePgaeItem.setEnabled(false);
			            }    
			            if(totalPage > 1){ 
			                pageTextItem.setEnabled(true);
			                pageText.setText("" + currentPage);
			            }else{
			                pageTextItem.setEnabled(false);
			                pageText.setText("1");
			            }
			            if(currentPage < totalPage){
			                nextPageItem.setEnabled(true);
			                lastPageItem.setEnabled(true);
			            }else{
			                nextPageItem.setEnabled(false);
			                lastPageItem.setEnabled(false);
			            }    
			            refresItem.setEnabled(true);
			            
			            String afterPageText = pageToolbar.doAction("getAfterPageText", actionContext);
			            if(afterPageText != null && !"".equals(afterPageText)){
			                afterPageLabel.setText(afterPageText.replace("{0}", "" + totalPage));
			                afterPageLabel.pack();
			                afterPageItem.setWidth(afterPageLabel.getSize().x + 5);
			            }
			            if(totalCount == 0 ){
			            	String emptyMsg = pageToolbar.doAction("getEmptyMsg", actionContext);
			                if(emptyMsg != null && pageToolbar.getBoolean("displayInfo")){
			                    infoLabel.setText(emptyMsg);
			                    pagingComposite.layout();
			                }else{
			                    infoLabel.setText("");
			                }
			            }else{        
			            	String displayMsg = pageToolbar.doAction("getDisplayMsg", actionContext);
			                if(displayMsg != null && pageToolbar.getBoolean("displayInfo")){
			                    String msg = displayMsg.replace("{0}", "" + (start + 1));
			                    msg = msg.replace("{1}", "" + (offset + 1));
			                    msg = msg.replace("{2}", "" + totalCount);
			                    infoLabel.setText(msg);
			                    pagingComposite.layout();
			                }else{
			                    infoLabel.setText("");
			                }
			            }
			            pageToolbar.put("currentPage", currentPage);
			            pageToolbar.put("totalPage", totalPage);
			            pageToolbar.put("start", start - 1);
			            pageToolbar.put("totalCount", totalCount);
			            pageToolbar.put("offset", offset);
			            pageToolbar.put("limit", limit);
			        
			        }//else{
			        	/*
			            pageToolbar.put("currentPage", 0);
			            pageToolbar.put("totalPage", 0);
			            pageToolbar.put("start", 0);
			            pageToolbar.put("totalCount", 0);
			            pageToolbar.put("offset", 0);
			            pageToolbar.put("limit", 0);
			        
			            //没有数据仓库
			            firtPageItem.setEnabled(false);
			            prePgaeItem.setEnabled(false);
			            pageTextItem.setEnabled(false);
			            nextPageItem.setEnabled(false);
			            lastPageItem.setEnabled(false);
			            refresItem.setEnabled(false);
			            pageText.setText("1");
			            
			            String beforePageText = pageToolbar.getStringBlankAsNull("pageToolbar");
			            if(beforePageText != null){
			                beforePageLabel.setText(beforePageText);
			                beforePageLabel.pack();
			                beforePageItem.setWidth(beforePageLabel.getSize().x + 5);
			            }
			            String afterPageText = pageToolbar.getStringBlankAsNull("afterPageText");
			            if(afterPageText != null){
			                afterPageLabel.setText(afterPageText.replace("{0}", "0"));
			                afterPageLabel.pack();
			                afterPageItem.setWidth(afterPageLabel.getSize().x + 5);
			            }
			            String emptyMsg = pageToolbar.getStringBlankAsNull("emptyMsg");
			            if(emptyMsg != null){
			                infoLabel.setText(emptyMsg);
			            }
			            */
			        //}			        
			    }catch(Throwable t){
			        Executor.error(TAG, "PageToolbarDataStoreListener onLoaded error, store=" + store.getMetadata().getPath(), t);
			    }    
			}
		});
	}
}
