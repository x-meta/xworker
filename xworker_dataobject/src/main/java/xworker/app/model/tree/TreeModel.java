package xworker.app.model.tree;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TreeModel {

	public static final String ROOT_ID = "__TreeNodeRootId__";
	
	/** 存储原始数据的Key，在原始数据被转成Map类型的TreeModel数据时 */
	public static final String Source = "_source";

	Thing thing;
	ActionContext actionContext;
	boolean rootVisible;
	boolean bindToParent;
	boolean loadBackground;
	boolean delayLoad;
	List<String> parentControls;
	TreeModelItem rootItem;
	List<TreeModelListener> listeners = new ArrayList<>();
	Object bindObjet;

	public TreeModel(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;

		rootVisible = thing.doAction("isRootVisible", actionContext);
		bindToParent = thing.doAction("isBindToParent", actionContext);
		parentControls = thing.doAction("getParentControls", actionContext);
		loadBackground = thing.doAction("isLoadBackground", actionContext);
		delayLoad = thing.doAction("isDelayLoad", actionContext);
	}

	/**
	 * 从已加载的节点中寻找指定ID的节点。
	 *
	 * @param id 节点标识
	 * @return 节点，不存在返回null
	 */
	public TreeModelItem findItem(String id){
		if(id == null || id.isEmpty()){
			return null;
		}

		rootItem = getRoot();
		return findItem(rootItem, id);
	}

	private TreeModelItem findItem(TreeModelItem item, String id){
		if(id.equals(item.getId())){
			return item;
		}

		if(item.isChildInited()) {
			for (TreeModelItem childItem : item.getItems()) {
				TreeModelItem findedItem = findItem(childItem, id);
				if(findedItem != null){
					return findedItem;
				}
			}
		}

		return null;
	}

	public Object getBindObjet() {
		return bindObjet;
	}

	public void setBindObjet(Object bindObjet) {
		this.bindObjet = bindObjet;
	}

	public void addListener(TreeModelListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
	}

	public void removeListener(TreeModelListener listener){
		listeners.remove(listener);
	}

	public void fireItemUpdated(TreeModelItem item){
		for(TreeModelListener listener : listeners){
			listener.itemUpdated(item);
		}
	}

	public void fireItemRemoved(TreeModelItem item){
		for(TreeModelListener listener : listeners){
			listener.itemRemoved(item);
		}
	}

	public void fireItemAdded(TreeModelItem parentTreeModelItem, TreeModelItem treeModelItem, int index){
		for(TreeModelListener listener : listeners){
			listener.itemAdded(parentTreeModelItem, treeModelItem, index);
		}
	}

	public void fireItemRefreshed(TreeModelItem item){
		for(TreeModelListener listener : listeners){
			listener.itemRefreshed(item);
		}
	}

	public TreeModelItem getRoot(){
		if(rootItem != null){
			return rootItem;
		}
		rootItem = thing.doAction("getRoot", actionContext, "treeModel", this, "parentItem", null);
		List<TreeModelItem> items = new ArrayList<>();
		items.add(rootItem);
		thing.doAction("initItems", actionContext, "treeModel", this, "parentItem", null, "items", items);

		return rootItem;
	}

	public void loadChilds(final TreeModelItem parentItem, final Callback<List<TreeModelItem>, Void> callback) {
		if (loadBackground) {
			new Thread(() -> TreeModel.this.doLoadChilds(parentItem, callback)).start();
		} else {
			doLoadChilds(parentItem, callback);
		}
	}

	public List<TreeModelItem> doLoadChilds(TreeModelItem parentItem, Callback<List<TreeModelItem>, Void> callback){
		List<TreeModelItem> items;
		if(parentItem.isChildInited()){
			items = parentItem.getItems();
		}else {
			items = thing.doAction("getChilds", actionContext,
					"treeModel", this, "parentItem", parentItem);
			parentItem.setItems(items);
			thing.doAction("initItems", actionContext, "treeModel", this, "parentItem", parentItem, "items", items);

		}
		if(callback != null) {
			callback.call(items);
		}

		return items;
	}

	/**
	 * 通过标识返回对应的条目。
	 *
	 * @param id　条目标识
	 * @return　对应的条目，不存在返回null
	 */
	public List<TreeModelItem> getItemById(String id){
		return thing.doAction("getItemById", actionContext, "treeModel", this, "parentItem", null, "id", id);
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public TreeModelItem getRootItem() {
		return rootItem;
	}

	public boolean isBindToParent() {
		return bindToParent;
	}

	public List<String> getParentControls() {
		return parentControls;
	}

	public boolean isRootVisible() {
		return rootVisible;
	}

	public boolean isLoadBackground() {
		return loadBackground;
	}

	public boolean isDelayLoad() {
		return delayLoad;
	}

	public static TreeModel create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Thing treeModel = self;
		while(true) {
			Thing ref = self.doAction("getTreeModelRef", actionContext);
			if(ref != null){
				treeModel = ref;
			}else{
				break;
			}
		}

		return new TreeModel(treeModel, actionContext);
	}
}
