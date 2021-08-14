package xworker.lang.flow.uiflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.ThrowableStackTraceText;
import xworker.util.XWorkerUtils;

public class UiFlow extends AbstractUiFlow{
	private static final String TAG = UiFlow.class.getName();
	/** 运行节点时的动作 */
	public static final String NODE_ACTION = "flowRun";
	/** 调用动作是的动作 */
	public static final String ACTION_ACTION = "doAction";
	
	ActionContext flowContext;
	Composite mainComposite;
	
	/** Zest的GraphNode的缓存 */
	Map<String, GraphNode> graphNodes = new HashMap<String, GraphNode>();
	GraphSelectionListener graphSelectionListener = new GraphSelectionListener(this);
	/** 最后执行的事物 */
	Thing lastNodeThing;
	/** 父流程 */
	UiFlow parent = null;
	/** 子流程 */
	UiFlow child = null;
	
	public UiFlow(Composite parent, Thing thing, ActionContext actionContext){
		super(thing, actionContext);
		
		this.flowContext = new ActionContext();
		this.flowContext.put("shellContext", actionContext);	
		this.flowContext.put("uiFlow", this);		
		this.actionContext.g().put("uiFlow", this);
		
		//创建界面
		this.actionContext.put("parent", parent);
		Thing compositeThing = World.getInstance().getThing("xworker.lang.flow.uiflow.UiFilowShell/@mainComposite");
		mainComposite = (Composite) compositeThing.doAction("create", actionContext);
		
		//创建zest流程图控件
		initZestGraph();
		
		//初始化流程图
		this.actionContext.g().put("uiFlow", null); //有子流程判断相同则不初始化
		initModelGraphs();
	}

	public UiFlow(Thing thing, UiFlow parent){
		super(thing, parent.actionContext);
		
		this.flowContext = new ActionContext();
		this.flowContext.put("shellContext", actionContext);	
		this.flowContext.put("uiFlow", this);
		this.parent = parent;
		this.mainComposite = parent.mainComposite;
		
		//初始化参数
		thing.doAction("intiParams", this.flowContext);

		parent.child = this;
	}
	
	public ActionContext getFlowActionContext(){
		return flowContext;
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	public IFlow getParent(){
		return parent;
	}
	
	public List<UiFlow> getUiFLows(){
		UiFlow root = this;
		while(root.parent != null){
			root = root.parent;
		}
		
		List<UiFlow> list = new ArrayList<UiFlow>();
		while(root != null){
			list.add(root);
			root = root.child;
		}
		
		return list;
	}
	
	
	/**
	 * 启动子流程。
	 * 
	 * @param childFlow
	 */
	public void startChildFlow(Thing childFlow){		
		UiFlow child = new UiFlow(childFlow, this);
		child.initModelGraphs();
		if(childFlow.getBoolean("startOnLoad")){
			child.start();
		}
	}
	
	public void initZestGraph(){
		Graph graph = actionContext.getObject("graph");
		graph.addSelectionListener(graphSelectionListener);
		graph.addDisposeListener(new GraphDisposeListener(this));
		
		//节点树可以拖拽
		if(SwtUtils.isRWT() == false) {
			ActionContainer nodeRegistSelector = actionContext.getObject("nodeRegistSelector");
			Tree thingTree = nodeRegistSelector.getActionContext().getObject("thingTree");
			Thing dragSource = World.getInstance().getThing("xworker.lang.flow.uiflow.UiFilowShell/@DragSource");
			nodeRegistSelector.getActionContext().peek().put("parent", thingTree);
			dragSource.doAction("create", nodeRegistSelector.getActionContext());
		}
		new GraphDropTarget(this, graph, actionContext); 
	}
	
	public void saveNodePositions(){
		//保存所有节点的位置，当前没有考虑节点是其它事物的情况，如果有那么需要考虑
		for(String key : graphNodes.keySet()){
			GraphNode node = graphNodes.get(key);
			Thing thing = World.getInstance().getThing(key);
			if(thing != null && node != null){
				thing.put("x", String.valueOf(node.getLocation().x));
				thing.put("y", String.valueOf(node.getLocation().y));
			}
		}
		
		//保存事物
		this.save();
		//thing.save();
	}
	
	/**
	 * 结束当前流程。
	 */
	public void end(){
		if(parent != null){
			//初始化到父节点的参数
			thing.doAction("setReturnValues", flowContext);
			
			//返回值
			String returnString = (String) thing.doAction("getRetunString", actionContext);
			
			//初始化父节点的流程图
			parent.initModelGraphs();
			
			//调用父节点的节点结束
			parent.nodeFinished(thing, returnString);
		}else{
			return;
		}
	}
	
	public void initModelGraphs(){		
		UiFlow oldFlow = actionContext.getObject("uiFlow");
		/*
		if(oldFlow == this){
			return;
		}*/
		Graph graph = actionContext.getObject("graph");
		graph.setSelection(null);
		
		//模拟一个假的选择事件
		Event ev = new Event();
		ev.widget = graph;
		SelectionEvent event = new SelectionEvent(ev);
		graphSelectionListener.widgetSelected(event);
		
		//保存原有流程的节点位置
		if(oldFlow != null){
			oldFlow.saveNodePositions();
		}
		
		//清空原有的节点
		List<GraphNode> forRemove = new ArrayList<GraphNode>();
		for(Object node : graph.getNodes()){
			forRemove.add((GraphNode) node);
		}
		List<GraphConnection> forRemoveC = new ArrayList<GraphConnection>();
		for(Object con : graph.getConnections()){
			forRemoveC.add((GraphConnection) con);
		}
		for(GraphConnection c : forRemoveC){
			try{
				c.dispose();
			}catch(Exception e){				
			}
		}
		for(GraphNode g : forRemove){
			try{
				g.dispose();
			}catch(Exception e){				
			}
		}
		actionContext.g().put("uiFlow", this);
		if(thing == null) {
			return;
		}
		
		log("初始化流程图，flow=" + thing.getMetadata().getPath());
		//它下面的节点都当作是流程节点，只是需要实现createZestGraphNode方法的可能才是真流程节点
		for(Thing child : thing.getChilds()){
			GraphNode node = (GraphNode) child.doAction("createZestGraphNode", actionContext,  "graph", graph, "uiFlow", this);			
			if(node != null){
				//放入缓存
				graphNodes.put(child.getMetadata().getPath(), node);
			}
		}
		
		//初始化连接
		for(Thing child : thing.getChilds()){
			child.doAction("createZestGraphConnection", actionContext,  "graph", graph, "graphNodes", graphNodes);			
		}
				
		//初始化布局和位置，目前看还是不要布局的好
		//graph.applyLayout();
		
		for(Thing child : thing.getChilds()){
			GraphNode node = graphNodes.get(child.getMetadata().getPath());
			if(node != null && child.getStringBlankAsNull("x") != null && child.getStringBlankAsNull("y") != null){
				node.setLocation(child.getDouble("x"), child.getDouble("y"));
			}
		}	
	}
	
	public Composite getMainComposite(){
		return mainComposite;
	}
	
	/**
	 * 节点结束，一般是节点自己调用，包含nextNode的名字。
	 * 
	 * @param node 要结束的节点
	 * @param nextConnectionName 下级连接的名字，可以为null
	 */
	public void nodeFinished(Thing node, String nextConnectionName){
		//首先查找是否有对应的子节点
		Thing nextNode = null;
		for(Thing conn : node.getChilds("Connection")){
			if(nextConnectionName != null && !"".equals(nextConnectionName)){
				if(conn.getMetadata().getName().equals(nextConnectionName)){
					nextNode = World.getInstance().getThing(conn.getString("nodeRef"));
					break;
				}
			}else{
				//使用默认
				if(conn.getMetadata().getName().equals("default")){
					nextNode = World.getInstance().getThing(conn.getString("nodeRef"));
					break;
				}
			}
		}
		
		if(nextNode == null){
			nextNode = World.getInstance().getThing(node.getString("nextNode"));
		}
		
		nodeFinishedWithNextNode(node, nextNode);
	}
	
	private void nodeFinishedWithNextNode(Thing node, Thing nextNode){
		log("节点完成：" + node.getMetadata().getLabel() + ", path=" + node.getMetadata().getPath());
		if(nextNode == null){
			nextNode = World.getInstance().getThing(node.getString("nextNode"));
		}
		
		start(nextNode);
	}
	
	public void log(String message){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Control logText = (Control) actionContext.getObject("logText");
		if(logText != null && !logText.isDisposed()){
			SwtTextUtils.append(logText, sf.format(new Date()) + ":" + message + "\n");
			SwtTextUtils.setSelection(logText, 0, SwtTextUtils.getText(logText).length());
			SwtTextUtils.showSelection(logText);
		}else{
			Executor.info(TAG, sf.format(new Date()) + ":" + message );
		}
	}
	
	public void log(Throwable e){
		Executor.info(TAG, "error", e);
		Control logText = (Control) actionContext.getObject("logText");
		if(logText != null && !logText.isDisposed()){
			if(logText instanceof Text) {
				ThrowableStackTraceText.printStackTrace(e, (Text) logText);
			}else {
				ThrowableStackTraceText.printStackTraceToStyledText(e, logText);
			}
			
			CTabFolder tabFolder = actionContext.getObject("tabFolder");
			CTabItem logTabItem = actionContext.getObject("logTabItem");
			tabFolder.setSelection(logTabItem);
		}
	}
	
	/**
	 * 返回流程的定义事物。
	 * 
	 * @return
	 */
	public Thing getThing(){
		return thing;
	}
	
	/**
	 * 在Outline上显示事物的文档，如果outlineBrowser存在的话。
	 * @param thing
	 */
	public void showOutlineDoc(Thing thing){
		if(actionContext.get("outlineBrowser") != null){
			Browser outlineBrowser = actionContext.getObject("outlineBrowser");
			outlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
			
			Browser descOutlineBrowser = actionContext.getObject("descOutlineBrowser");
			descOutlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing.getDescriptor()));
		}
	}
	
	public Object get(String key){
		return flowContext.g().get(key);
	}
	
	public void set(String key , Object value){
		flowContext.g().put(key, value);
	}
	
	public ActionContext runComposite(Thing flowNode, Thing composite){
		return runComposite(flowNode, composite, null);	
	}
		
	public ActionContext runComposite(Thing flowNode, Thing composite, Map<String, Object> params){
		Composite contentComposite = actionContext.getObject("contentComposite");
		for(Control child : contentComposite.getChildren()){
			child.dispose();
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", contentComposite);
		ac.put("uiFlow", this);
		ac.put("parentContext", flowContext);
		ac.put("flowNode", flowNode);
		ac.put("nodeThing", flowNode);
		if(params != null){
			ac.putAll(params);
		}
		
		composite.doAction("create", ac);
		contentComposite.layout();
		
		CTabFolder tabFolder = actionContext.getObject("tabFolder");
		CTabItem contentItem = actionContext.getObject("contentItem");
		tabFolder.setSelection(contentItem);
		return ac;
	}
	
	public Object runAction(Thing action){
		Thing composite = World.getInstance().getThing("xworker.lang.flow.uiflow.UiFilowShell/@blankComposite");
		runComposite(action, composite);
				
		//执行动作		
		return action.doAction(UiFlow.ACTION_ACTION, flowContext, "flowNode", action, "uiFlow", this);		
	}
	
	public Thing getStartNode(){
		if(thing == null) {
			return null;
		}
		
		for(Thing node : thing.getChilds()){
			if("Start".equals(node.getThingName())){
				return node;
			}
		}
		
		if(thing.getChilds().size() > 0){
			return thing.getChilds().get(0);
		}
		
		return null;
	}
	
	public void start(Thing nextNode){
		/*
		String nextFlowNode = (String) thing.doAction("getNextNode", actionContext);
		if(nextFlowNode != null){
			//首先通过getNextNode方法获取节点
			nextNode = World.getInstance().getThing(nextFlowNode);
		}*/
		
		if(nextNode == null){
			//没有后续节点，不执行了。
			log("没有可以执行的节点。");
			return;
			/*
			//最后看起始
			if(thing.getChilds().size() > 0){
				nextNode = thing.getChilds().get(0);
			}
			*/
		}				
		
		log("节点执行：" + nextNode.getMetadata().getLabel() + ", path=" + nextNode.getMetadata().getPath());
		//设置节点背景色为原背景色
		if(lastNodeThing != null){
			GraphNode gnode = graphNodes.get(lastNodeThing.getMetadata().getPath());
			if(gnode != null && !gnode.isDisposed()){
				Color oldBackground = (Color) gnode.getData("oldBackground");
				if(oldBackground != null){
					gnode.setBackgroundColor(oldBackground);
				}
			}
		}
		 
		//设置当前节点的颜色
		lastNodeThing = nextNode;
		GraphNode gnode = graphNodes.get(nextNode.getMetadata().getPath());
		if(gnode != null){			
			Color oldBackground = (Color) gnode.getData("oldBackground");
			if(oldBackground == null){
				gnode.setData("oldBackground", gnode.getBackgroundColor());
			}
			gnode.setBackgroundColor(mainComposite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		}
		
		//显示文档
		showOutlineDoc(nextNode);
		
		//执行节点
		try{
			nextNode.doAction(UiFlow.NODE_ACTION, flowContext, "flowNode", nextNode);
		}catch(Exception e){
			log(e);
		}
	}
	
	public void start(){
		start(getStartNode());
	}
	
	public void removeNode(Thing nodeThing){
		if(nodeThing == null){
			return;
		}
		
		GraphNode node = graphNodes.get(nodeThing.getMetadata().getPath());
		if(node != null){
			node.dispose();
		}
				
		removeConnection(nodeThing, true);
	}
	
	public void recreateConnection(Thing nodeThing){
		if(nodeThing == null){
			return;
		}
		
		//先移除原先的连接
		removeConnection(nodeThing, false);
		GraphNode node = graphNodes.get(nodeThing.getMetadata().getPath());
		if(node != null){
			Graph graph = actionContext.getObject("graph");
			nodeThing.doAction("createZestGraphConnection", actionContext,  "graph", graph, "graphNodes", graphNodes);		
		}
	}
	
	public void removeConnection(Thing nodeThing, boolean all){
        if(nodeThing == null){
        	return;
        }
                
        Graph graph = actionContext.getObject("graph");
        //移除所有相关连接
        List<GraphConnection> list = new ArrayList<GraphConnection>();
        for(Object conObj : graph.getConnections()){
        	GraphConnection con = (GraphConnection) conObj;
            if(con.getData("source") == nodeThing || (all && con.getData("target") == nodeThing)){
                list.add(con);
                continue;
            }
            
            for(Thing child : nodeThing.getChilds()){
            	if(con.getData("source") == child || (all && con.getData("target") == nodeThing)){
                    list.add(con);
                    continue;
                }
            }
        }
        for(GraphConnection con : list){
            con.dispose();
        }
	}
	
	public void setLinkStart(){
		Graph graph = actionContext.getObject("graph");
		if(graph.getData("oldCursor") == null){
			graph.setData("oldCursor", graph.getCursor());
		}
		graph.setCursor(graph.getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
		actionContext.g().put("selectionAction", "setLink");
	}
	
	public void setLinkEnd(){
		Graph graph = actionContext.getObject("graph");
		Cursor oldCursor = (Cursor) graph.getData("oldCursor");
		if(oldCursor != null){
			graph.setCursor(oldCursor);
		}else{
			graph.setCursor(graph.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		}
		graph.setData("oldCursor", null);
		actionContext.g().put("selectionAction", null);
	}
}
