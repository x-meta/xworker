package xworker.swt.xwidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.util.ResourceManager;
import xworker.swt.xworker.DelayAction;

public class ContentSelector{
	ActionContext actionContext;
	ActionContext selectorContext;
	DelayAction delayAction;
	Thing thing;
	Table table;
	List<SelectContent> contentsForSelect = new ArrayList<SelectContent>();
	
	public ContentSelector(Table table, Thing thing, ActionContext actionContext, ActionContext selectorContext){
		this.actionContext = actionContext;
		this.selectorContext = selectorContext;
		this.thing = thing;
		this.table = table;
		delayAction = new DelayAction(table.getDisplay(), 200);
	}
	
	public void setContents(List<SelectContent> contents){
		this.contentsForSelect = contents;
	}
	
	public List<SelectContent> query(String text){
		if(contentsForSelect == null){
			return Collections.emptyList();
		}
		
		String texts[] = text.split("[ ]");
		for(int i=0; i<texts.length; i++) {
			texts[i] = texts[i].trim();
		}
		
		List<SelectContent> list = new ArrayList<SelectContent>();
		for(SelectContent c : contentsForSelect){
			boolean ok = true;
			for(String txt : texts) {				
				if(c.value != null && c.value.indexOf(txt) != -1) {
					continue;
				}
				
				if(c.label != null && c.label.indexOf(txt) != -1){
					continue;
				}
				
				ok = false;
				break;
			}
			
			if(ok) {
				list.add(c);
			}
			/*
			if(c.value != null && c.value.indexOf(text) != -1){
				list.add(c);
				continue;
			}
			
			if(c.label != null && c.label.indexOf(text) != -1){
				list.add(c);
				continue;
			}*/
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void showContents(String filterText){		
		if(filterText == null){
			filterText = "";
		}
		
		table.removeAll();
		
		//查询数据
		List<SelectContent> contents = (List<SelectContent>) thing.doAction("query", actionContext, UtilMap.toMap("text", filterText, "selector", this));
		
		if(contents != null && contents.size() > 0){
			int count = 0;
			for(SelectContent c : contents){
				TableItem item = new TableItem(table, SWT.NONE);
				if(c.label != null){
					item.setText(new String[]{c.label});
				}else{
					item.setText(new String[]{c.value});
				}
				
				if(c.image != null && !"".equals(c.image)){
					actionContext.peek().put("parent", table);
					Image image = (Image) ResourceManager.createIamge(c.image, actionContext);
					if(image != null){
						item.setImage(image);
					}
					
				}
				item.setData(c);
				count++;
				
				if(count > 200) {
					break;
				}
			}
			
			if(contents.size() > 0){
				table.setSelection(0);
				Object value = contents.get(0).value;
				Object content = contents.get(0);
			    thing.doAction("onSelection", actionContext, UtilMap.toMap("value", value, "content", content));
			}			
						
			if(contents.size() == 1 && thing.getBoolean("autoSelected")){
				//自动触发选择事件
				Object value = contents.get(0).value;
				Object content = contents.get(0);

				thing.doAction("selected", actionContext, UtilMap.toMap("value", value, "content", content));
			}
		}
	}

	public void setDelayAction(Runnable run){
		delayAction.cancel();
		delayAction.setRunnable(run);
	}
	
	
	/**
	 * 事物的Create方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException
	 */
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		ac.put("thing", self);
		
		//创建面板
		Thing compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.ContentSelector/@Composite");
		Composite composite = null;
		Designer.pushCreator(self);
		try {
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally {
			Designer.popCreator();
		}
		
		//创建选择器
		Table table = (Table) ac.get("table");		
		ContentSelector assistor = new ContentSelector(table, self, actionContext, ac);		
		ac.put("assistor", assistor);
		
		//执行子节点的 create方法
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//返回值
		actionContext.getGlobalScope().put(self.getMetadata().getName(), ac.get("actions"));
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		
		return composite;
	}
	
	public static void setDelayAction(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		final Text text = (Text) event.widget;
		final ContentSelector assistor = (ContentSelector) actionContext.get("assistor");

		assistor.setDelayAction(new Runnable(){
			public void run(){
				try {
					if(text.isDisposed()) {
						return;
					}
					
					//过滤标点符号
					String str = text.getText();
					if(assistor.thing.getBoolean("filtePunct")){
						str = str.replaceAll("[\\pP‘’“”]", "");
					}
					assistor.showContents(str);
				}catch(Exception e) {
					Executor.warn(ContentSelector.class.getName(), "Search content error", e);
				}
			}		   
		});
	}
	
	public static void textDefaultSelection(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("thing");
		Text filterText = (Text) actionContext.get("filterText");
		Table table = (Table) actionContext.get("table");
		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
		
		TableItem[] items = table.getSelection();
		Object value = null;
		Object content = null;
		if(items == null || items.length == 0){
		    value = filterText.getText();
		}else{
		    value = ((SelectContent) items[0].getData()).value;
		    content = items[0].getData();
		}

		thing.doAction("selected", parentContext, UtilMap.toMap("value",  value, "content",  content));
	}

	public static void textKeyDown(ActionContext actionContext){
		Table table = (Table) actionContext.get("table");
		Event event = (Event) actionContext.get("event");
		
		int index = table.getSelectionIndex();

		if(event.keyCode == 16777218){
		    //Down
		    if(index >= 0){
		        index = index + 1;
		    }else{
		        index = 0;
		    }
		    
		    if(index > table.getItems().length - 1){
		        index = 0;
		    }
		    
		    event.doit = false;
		}else if(event.keyCode == 16777217){
		    //Up
		    if(index > 0){
		        index = index - 1;
		    }else{
		        index = table.getItems().length - 1;
		    }    
		    
		    event.doit = false;
		}

		if(table.getItems().length > 0){
			//table.select(index);
		    table.setSelection(index);
		    
			ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
			TableItem item = table.getSelection()[0];
			
			Object value = ((SelectContent)  item.getData()).value;
			Object content = item.getData();

			Thing thing = (Thing) actionContext.get("thing"); 
		    thing.doAction("onSelection", parentContext, UtilMap.toMap("value", value, "content", content));
		}
	}
	
	public static void tableDefaultSelection(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("thing");
		Event event = (Event) actionContext.get("event");
		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
		
		Object value = ((SelectContent) event.item.getData()).value;
		Object content = event.item.getData();

		thing.doAction("selected", parentContext, UtilMap.toMap("value", value, "content", content));
	}
	
	public static void tableSelection(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("thing");
		Event event = (Event) actionContext.get("event");
		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
		
		Object value = ((SelectContent) event.item.getData()).value;
		Object content = event.item.getData();

		thing.doAction("onSelection", parentContext, UtilMap.toMap("value", value, "content", content));
	}
	
	public static void setText(ActionContext actionContext){
		Text filterText = (Text) actionContext.get("filterText");
		String text = (String) actionContext.get("text");
		if(text != null){
		    filterText.setText(text);
		}

		filterText.setFocus();
	}
	
	public static void setFocus(ActionContext actionContext){
		Text filterText = (Text) actionContext.get("filterText");
		filterText.setFocus();
	}
	
	public static List<SelectContent> doQuery(ActionContext actionContext){
		String text = actionContext.getObject("text");		
		ContentSelector assistor = actionContext.getObject("selector");

		return assistor.query(text);
	}
}
