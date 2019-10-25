package xworker.app.model.tree;

import java.util.ArrayList;
import java.util.List;

public class SimpleTreeNode {
	String text;
	String icon;
	String id;
	Object data;
	List<SimpleTreeNode> childs;
	boolean singleClickExpand;
	SimpleTreeNode parent;
	
	public SimpleTreeNode(){		
	}
	
	public SimpleTreeNode(String id, String text, String icon, Object data){
		this.id = id;
		this.text = text;
		this.icon = icon;
		this.data = data;
	}

	public Object get(String key){
		return null;
	}
	
	public boolean isLeaf(){
		return childs == null || childs.size() == 0;
	}
	
	public void addChild(SimpleTreeNode node){
		if(childs == null){
			childs = new ArrayList<SimpleTreeNode>();
		}
		
		childs.add(node);
		node.setParent(this);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public List<SimpleTreeNode> getChilds() {
		return childs;
	}

	public void setChilds(List<SimpleTreeNode> childs) {
		this.childs = childs;
	}
	
	public Object getModelId(){
		return null;
	}

	public SimpleTreeNode getParent() {
		return parent;
	}

	public void setParent(SimpleTreeNode parent) {
		this.parent = parent;
	}

	public boolean isSingleClickExpand() {
		return singleClickExpand;
	}

	public void setSingleClickExpand(boolean singleClickExpand) {
		this.singleClickExpand = singleClickExpand;
	}

}
