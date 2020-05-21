package xworker.swt.app.pages;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class PagesComposite implements PageManagerListener{
	PageManager pageManager = null;
	Thing thing;
	ActionContext actionContext;
	
	//PagesComposite的根
	Control rootControl;
	Composite pathComposite;
	Composite contentComposite;
	StackLayout contentLayout;
	
	public PagesComposite(Composite parent, Thing thing, ActionContext parentContext) {
		pageManager = new PageManager();
		pageManager.setListener(this);
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(thing, parentContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.app.pages.prototypes.PagesComposite/@mainComposite"));
		rootControl = cc.create();
		
		this.actionContext = cc.getNewActionContext();
		pathComposite = actionContext.getObject("pathComposite");
		contentComposite = actionContext.getObject("contentComposite");
		contentLayout = actionContext.getObject("contentCompositeStackLayout");
	}

	@Override
	public void changed(PageManager pageManager) {
		//清空pathComposite，全部重建
		for(Control control : pathComposite.getChildren()) {
			control.dispose();
		}
		
		//重新创建
		Page rootPage = pageManager.getRootPage();
		if(rootPage != null) {
			createPagePath(rootPage);
		}
	}
	
	private void createPagePath(Page page) {
		Link link = new Link(pathComposite, SWT.NONE);
		link.setText("<a>" + page.getLabel() + "</a>");
		link.setData(page);
		link.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					Page page = (Page) event.widget.getData();
					
					Control pageControl = page.getControl(contentComposite);
					if(pageControl != null && pageControl.isDisposed() == false) {
						contentLayout.topControl = pageControl;
						contentComposite.layout();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		if(page.getNext() != null) {
			Label split = new Label(pathComposite, SWT.NONE);
			split.setText("/");
			split.setAlignment(SWT.CENTER);
			RowData rowData = new RowData();
			rowData.width = 20;
			split.setLayoutData(rowData);
			
			createPagePath(page.getNext());
		}
		
		pathComposite.layout();
	}
	public Control getControl() {
		return rootControl;
	}
	
	public void setNextPage(String parentId, String id, Thing thing, ActionContext actionContext) {
		pageManager.setNextPage(parentId, id, thing, actionContext);
	}
	
	public void setRootPage(String id, Thing thing, ActionContext actionContext) {
		pageManager.setRootPage(id, thing, actionContext);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite parent = actionContext.getObject("parent");
		PagesComposite pagesComposite = new PagesComposite(parent, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), pagesComposite);
		return pagesComposite.getControl();
				
	}
}
