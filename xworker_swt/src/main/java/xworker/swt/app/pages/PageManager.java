package xworker.swt.app.pages;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PageManager {
	Page rootPage;
	PageManagerListener listener;
	
	public PageManager() {
		
	}
	
	private Page createPage(String id, Thing thing, ActionContext actionContext) {
		return new Page(this, id, thing, actionContext);
	}

	public Page getRootPage() {
		return rootPage;
	}

	public void setRootPage(String id, Thing thing, ActionContext actionContext) {
		if(this.rootPage != null) {
			if(rootPage.getId().equals(id) && rootPage.getThing() == thing) {
				//重复不需要设置
				return;
			}
			
			this.rootPage.dispose();			
		}
		
		this.rootPage = createPage(id, thing, actionContext);
				
		fireChanged();
	}
	
	public void fireChanged() {
		if(this.listener != null) {
			this.listener.changed(this);
		}
	}

	public PageManagerListener getListener() {
		return listener;
	}

	public void setListener(PageManagerListener listener) {
		this.listener = listener;
	}
	
	public boolean setNextPage(String parentId, String id, Thing thing, ActionContext actionContext) {
		if(parentId == null || rootPage == null) {
			this.setRootPage(id, thing, actionContext);
			return true;
		} else {			
			Page current = rootPage;
			while(current != null) {
				if(current.getId().equals(parentId)) {
					Page next = current.getNext();
					if(next != null && next.getId().equals(id) && next.getThing() == thing) {
						//已经存在了不需要重复设置
						return true;
					}
					
					Page page = this.createPage(id, thing, actionContext);
					current.setNext(page);
					return true;
				}else {
					current = current.getNext();
				}
			}
		}		
		
		return false;
	}	
}
