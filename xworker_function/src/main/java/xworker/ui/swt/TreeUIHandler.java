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
package xworker.ui.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.util.XWorkerTreeUtil;
import xworker.ui.UIRequest;

/**
 * 一个把事物显示在Tree上的UIHandler。
 * 
 * @author Administrator
 *
 */
public class TreeUIHandler extends AbstractSWTUIHandler implements DisposeListener{
	//private static Logger logger = LoggerFactory.getLogger(TreeUIHandler.class);
	
	Tree root;
	Object parent;
	
	Map<String, TreeItem> cache = new HashMap<String, TreeItem>();
	
	/**
	 * <p>构造函数，parent可以是Tree或TreeItem。</p>
	 * 
	 * 创建时即默认把自己注册到UIManager。
	 * 
	 * @param parent
	 */
	public TreeUIHandler(Thing thing, String uiHandlerId_, Tree root, Object parent, boolean regist, boolean selectionListener, ActionContext actionContext){
		super(thing, uiHandlerId_, (Widget) parent, regist, actionContext);
		
		this.root = root;
		this.parent = parent;

		//添加事件处理
		if(selectionListener){
			root.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					TreeItem item = (TreeItem) event.item;
					Object data = item.getData();
					if(data instanceof UIRequest){
						UIRequest request = (UIRequest) data;
						request.getThing().doAction("defaultSelected", request.getActionContext(), UtilMap.toMap(new Object[]{"evnet", event}));
					}
				}
	
				@Override
				public void widgetSelected(SelectionEvent event) {
					TreeItem item = (TreeItem) event.item;
					Object data = item.getData();
					if(data instanceof UIRequest){
						UIRequest request = (UIRequest) data;
						request.getThing().doAction("selected", request.getActionContext(), UtilMap.toMap(new Object[]{"evnet", event}));
					}
				}			
			});
		}
	}
	
	/**
	 * 返回树节点路径的标识。
	 * 
	 * @param request
	 * @return
	 */
	private String getPathId(Thing thing){
		return getPath(thing);
		/*
		Thing thing = request.thingEntry.getThing();
		if(thing == null){			
			return null;
		}
		
		String path = thing.getString("group");
		if(path == null){
			return request.id;
		}else{
			return path + "." + request.id;
		}*/
	}
	
	/**
	 * 获取树节点的路径。
	 * 
	 * @param request
	 * @return
	 */
	private String getPath(Thing thing){
		String path = thing.getStringBlankAsNull("group");
		if(path == null){
			return thing.getMetadata().getLabel();
		}else{
			return path + "." + thing.getMetadata().getLabel();
		}
	}
	
	private void updateTreeItem(TreeItem item, UIRequest request, String path){
		String[] ps = path.split("[.]");		
		item.setData(request);
		XWorkerTreeUtil.initItem(item, thing, request.getActionContext());
		item.setText(ps[ps.length - 1]);
	}
	
	private TreeItem createTreeItem(String path, UIRequest request){
		Object parentItem = parent;
		String[] ps = path.split("[.]");
		String currentPath = null;
		for(int i=0; i<ps.length; i++){
			if(i == ps.length -1){
				//已到达叶子节点，创建
				TreeItem item = null;
				if(parentItem instanceof Tree){
					Tree rootTree = (Tree) parentItem;
					item = new TreeItem(rootTree, SWT.NONE);
				}else if(parentItem instanceof TreeItem){
					TreeItem rootTreeItem = (TreeItem) parentItem;
					item = new TreeItem(rootTreeItem, SWT.NONE);
				}
												
				item.setData(request);
				XWorkerTreeUtil.initItem(item, thing, request.getActionContext());
				item.setText(ps[i]);
				cache.put(getPathId(request.getThing()), item);
				
				item.addDisposeListener(this);
				item.setData("__treeUIHandler__path__cache__", getPathId(request.getThing()));
				
				return item;
			}else{
				//查看父节点是否存在，如果不存在则创建
				if(currentPath == null){
					currentPath = ps[i];
				}else{
					currentPath = currentPath + "." + ps[i];
				}
				
				TreeItem item = cache.get(currentPath);
				if(item == null){
					if(parentItem instanceof Tree){
						Tree rootTree = (Tree) parentItem;
						item = new TreeItem(rootTree, SWT.NONE);
					}else if(parentItem instanceof TreeItem){
						TreeItem rootTreeItem = (TreeItem) parentItem;
						item = new TreeItem(rootTreeItem, SWT.NONE);
					}
					
					item.setText(ps[i]);
					item.setData(currentPath);
					cache.put(currentPath, item);
					
					item.addDisposeListener(this);
					item.setData("__treeUIHandler__path__cache__", currentPath);
				}
				
				parentItem = item;
			}
		}
		
		return null;
	}

	/**
	 * 在SWT事物模型中创建的方法。
	 * 
	 * @param actionContext
	 */
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Object parent = actionContext.get("parent");
		
		boolean regist = self.getBoolean("registToUIManager");
		boolean selectionListener = self.getBoolean("selectionListener");
		Tree root = parent instanceof Tree ? (Tree) parent : ((TreeItem) parent).getParent();
		TreeUIHandler uiHandler = new TreeUIHandler(self, uiHandlerId, root, parent, regist, selectionListener, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		//避免垃圾产生，从缓存中移除生成的TreeItem当TreeItem执行dispose的时候
		String path = (String) event.widget.getData("__treeUIHandler__path__cache__");
		if(path != null){
			cache.remove(path);
		}
	}

	@Override
	protected void doRequeestUI(UIRequest request,
			ActionContext actionContext) {
		Thing thing = request.getThing();
		String pathId = getPathId(thing);
		String path = getPath(thing);
		
		TreeItem item = cache.get(pathId);
		if(item != null){
			//树节点已经存在了，主要是相同标识的路径已经存在，此时更新树节点
			updateTreeItem(item, request, path);
		}else{		
			//创建树节点
			item = createTreeItem(path, request);
		}
		
		this.requestCallback(request, UtilMap.toParams(new Object[]{"item", item}));
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		//结束则删除节点
		String pathId = this.getPathId(thing);
		if(pathId != null){
			TreeItem item = cache.get(pathId);
			if(item != null){
				cache.remove(pathId);
				item.dispose();
			}
		}
		
		//回调
		this.finishCallback(request, null);		
	}
}