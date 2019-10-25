package xworker.app.view.swt.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.PageInfo;

public class PagingToolbar {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Control parent = (Control) actionContext.get("parent");
		World world = World.getInstance();		

		//创建PageToolbar
		ActionContext ac = new ActionContext();
		ac.put("parent", parent);
		ac.put("parentActionContext", actionContext);
		Thing compositeThing = world.getThing("xworker.app.view.swt.widgets.PagingToolbar/@pagingComposite");
		Composite composite = (Composite) compositeThing.doAction("create", ac);
		ToolItem beforePageItem = (ToolItem) ac.get("beforePageItem");
		ToolItem afterPageItem = (ToolItem) ac.get("afterPageItem");		
		Label beforePageLabel = (Label) ac.get("beforePageLabel");
		Label afterPageLabel = (Label) ac.get("afterPageLabel");
		Label infoLabel = (Label) ac.get("infoLabel");
		
		//设置文本
		setToolTip(self, "firstText", ac.get("firtPageItem"), (String) self.doAction("getFirstText", actionContext));
		setToolTip(self, "prevText", ac.get("prePgaeItem"), (String) self.doAction("getPrevText", actionContext));
		setToolTip(self, "nextText", ac.get("nextPageItem"), (String) self.doAction("getNextText", actionContext));
		setToolTip(self, "lastText", ac.get("lastPageItem"), (String) self.doAction("getLastText", actionContext));
		setToolTip(self, "refreshText", ac.get("refresItem"), (String) self.doAction("getRefreshText", actionContext));
		String beforePageText = self.doAction("getBeforePageText", actionContext);
		if(beforePageText != null){
		    beforePageLabel.setText(beforePageText);
		    beforePageLabel.pack();
		    beforePageItem.setWidth(beforePageLabel.getSize().x + 5);
		}
		String afterPageTextStr = self.doAction("getAfterPageText", actionContext);
		if(afterPageTextStr != null){
		    afterPageLabel.setText(afterPageTextStr.replace("{0}", "0"));
		    afterPageLabel.pack();
		    afterPageItem.setWidth(afterPageLabel.getSize().x + 5);
		}
		String emptyMsg = self.doAction("getEmptyMsg", actionContext);
		if(emptyMsg != null && self.getBoolean("displayInfo")){
		    infoLabel.setText(emptyMsg);
		}

		Bindings bindings = ac.push(null);
		bindings.put("parent", composite);
		try{
		    for(Thing child : self.getChilds()){
		        child.doAction("create", ac);
		    }
		}finally{
		    ac.pop();
		}

		//创建PageToolbar事物
		Thing pageToolbar = new Thing("xworker.app.view.swt.widgets.PagingToolbar");
		pageToolbar.getAttributes().putAll(self.getAttributes());
		pageToolbar.set("extends", self.getMetadata().getPath());
		pageToolbar.set("context", ac);
		ac.put("pageToolbar", pageToolbar);

		//和DataStore绑定
		String storeStr = self.getStringBlankAsNull("store");
		if(storeStr != null){
		    Object store = actionContext.get(storeStr);
		    if(store != null){
		        pageToolbar.put("store", store);
		        pageToolbar.doAction("setStore", ac, UtilMap.toMap("store", store));
		    }
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), pageToolbar);
		return composite;
	}
	
	public static void setToolTip(Thing pageToolbar, String name, Object item, String tip){
	    //String tip = pageToolbar.getString(name);
	    if(tip != null && tip != ""){
	    	if(item instanceof Control){
	    		((Control) item).setToolTipText(tip);
	    	}else if(item instanceof ToolItem){
	    		((ToolItem) item).setToolTipText(tip);
	    	}
	    }
	}
	
	public static void setStore(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing store = (Thing) actionContext.get("store");
		
		//添加一个表格数据仓库监听
		Thing pageToolbar = self;
		Thing listener = new Thing("xworker.app.view.swt.data.events.PageToolbarDataStoreListener");
		listener.put("pageToolbar", pageToolbar);
		listener.put("store", store);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));

		//加入到监听器中
		pageToolbar.put("store",store);
		store.doAction("addListener", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		pageToolbar.set("storeListener", listener);
	}
	
	public static void handleEvent(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		Thing pageToolbar = (Thing) actionContext.get("pageToolbar");
		ActionContext parentActionContext = (ActionContext) actionContext.get("parentActionContext");
		ToolItem refresItem = (ToolItem) actionContext.get("refresItem");
		ToolItem firtPageItem = (ToolItem) actionContext.get("firtPageItem");
		ToolItem prePgaeItem = (ToolItem) actionContext.get("prePgaeItem");
		ToolItem nextPageItem = (ToolItem) actionContext.get("nextPageItem");
		ToolItem lastPageItem = (ToolItem) actionContext.get("lastPageItem");
		Text pageText = (Text) actionContext.get("pageText");		
		
		Thing store = (Thing) pageToolbar.get("store");
		if(event.widget == refresItem){
		    store.doAction("reload", parentActionContext);
		}

		if(store == null || store.get("pageInfo") == null){
		    return;
		}

		PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
		if(event.widget == firtPageItem){
		    pageInfo.setStart(0);
		    store.doAction("reload", parentActionContext);
		}else if(event.widget == prePgaeItem){
		    int page = pageToolbar.getInt("currentPage") - 1;
		    if(page < 1){
		        page = 1;
		    }
		    int start = (page - 1) * pageToolbar.getInt("limit");
		    if(start <= 0){
		        start = 0;
		    }
		    pageInfo.setStart(start);
		    store.doAction("reload", parentActionContext);
		}else if(event.widget == pageText){
		    int page = Integer.parseInt(pageText.getText());
		    if(page < 1){
		        page = 1;
		    }
		    int start = (page - 1) * pageToolbar.getInt("limit");
		    if(start <= 0){
		        start = 0;
		    }
		    pageInfo.setStart(start);
		    store.doAction("reload", parentActionContext);
		}else if(event.widget == nextPageItem){
		    int page = pageToolbar.getInt("currentPage") + 1;
		    if(page < 1){
		        page = 1;
		    }
		    int start = (page - 1) * pageToolbar.getInt("limit");
		    if(start <= 0){
		        start = 0;
		    }
		    pageInfo.setStart(start);
		    store.doAction("reload", parentActionContext);
		}else if(event.widget == lastPageItem){
		    int page = pageToolbar.getInt("totalPage");
		    if(page < 1){
		        page = 1;
		    }
		    int start = (page - 1) * pageToolbar.getInt("limit");
		    if(start <= 0){
		        start = 0;
		    }
		    pageInfo.setStart(start);
		    store.doAction("reload", parentActionContext);
		}
	}
}
