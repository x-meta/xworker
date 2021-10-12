package xworker.lang.flow.uiflow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilThing;

import xworker.lang.flow.uiflow.widgets.ResizeableGraphNode;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

public class UiFlowActions {
	public static GraphNode createZestGraphNode(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Graph graph = actionContext.getObject("graph");
		UiFlow uiFlow = actionContext.getObject("uiFlow");
		
		//创建节点
		GraphNode node = new ResizeableGraphNode(graph, ZestStyles.NODES_CACHE_LABEL, self.getMetadata().getLabel());
		node.setText(self.getMetadata().getLabel());
		Image icon = XWorkerTreeUtil.getIcon(self, uiFlow.mainComposite, actionContext);
		if(icon != null){
			node.setImage(icon);
		}
		node.setData(self);
		return node;
	}
	
	public static void createZestGraphConnection(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Graph graph = actionContext.getObject("graph");
		Map<String, GraphNode> graphNodes = actionContext.getObject("graphNodes");
		Thing nextNode = UtilThing.getSelfQuoteThing(self, "nextNode");
		GraphNode srcNode = graphNodes.get(self.getMetadata().getPath());
		GraphNode targetNode = null;
		if(nextNode != null){
			targetNode = graphNodes.get(nextNode.getMetadata().getPath());
		}		
		
		if(srcNode != null && targetNode != null){
			GraphConnection con = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, srcNode, targetNode);
			con.setData("source", self);
			con.setData("target", nextNode);
			con.setData("isRootNode", true);
			con.setData(self);
		}
		
		//如If、Switch第有子节点的连接的
		for(Thing child : self.getChilds("Connection")){
			nextNode = UtilThing.getSelfQuoteThing(child, "nodeRef");
			String nodeRef = nextNode == null ? null : nextNode.getMetadata().getPath();
			if(nodeRef != null){
				targetNode = graphNodes.get(nodeRef);
				if(srcNode != null && targetNode != null){
					GraphConnection con =  null;
					String type = child.getStringBlankAsNull("type");
					if("data".equals(type)){
						con = new GraphConnection(graph, ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED, targetNode, srcNode);
						con.setLineColor(graph.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
						//con.setLineStyle(GraphConnection.)
					}else{
						con = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, srcNode, targetNode);
					}
					con.setText(child.getMetadata().getLabel());
					con.setData("source", self);
					con.setData("sourceChild", child);
					con.setData("target", nextNode);
					con.setData("isRootNode", false);
					con.setData(child);
				}
			}
		}
	}	
	

	public static void dragStart(ActionContext actionContext){
		//找到树节点对应的事物
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		Object data = tree.getSelection()[0].getData();
		if(!(data instanceof Thing)){
			event.doit = false;
		}
	}
	
	public static void dragSetData(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		Object data = tree.getSelection()[0].getData();
		Thing thing = (Thing) data;
		event.data = thing.getMetadata().getPath();		
	}

	public static void dragEnd(ActionContext actionContext){
	
	}
	
	public static List<Map<String, String>> parseParams(String str){
		if(str != null){
			List<Map<String, String>> cons = new ArrayList<Map<String, String>>();
			for(String line : str.split("[,]")){
				line = line.trim();
				if("".equals(line)){
					continue;
				}
								
				String[] ls = line.split("[:]");
				String name = ls[0].trim();
				if("".equals(name)){
					continue;
				}
				String label = name;
				if(ls.length > 1){
					label = ls[1].trim();					
				}
				
				Map<String, String> data = new HashMap<String, String>();
				data.put("name", name);
				data.put("label", label);
				cons.add(data);
			}
			
			return cons;
		}else{
			return null;
		}
	}
	
	public static Object getConnectionStarts(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String connections = self.getStringBlankAsNull("connections");
		return parseParams(connections);
	}
	
	public static void updateConnection(ActionContext actionContext){
		UiFlow uiFlow = actionContext.getObject("uiFlow");
		Thing self = actionContext.getObject("self");
		
		Thing target = actionContext.getObject("target");
		if(actionContext.get("linkStartInfo") == null){
			//不是多选连接，只有节点本身的连接
			self.put("nextNode", target.getMetadata().getPath());
			self.save();
		}else{
			Thing connection = null;
			Map<String, String> linkStartInfo = actionContext.getObject("linkStartInfo");
		    for(Thing con : self.getChilds("Connection")){
		    	String name = con.getMetadata().getName();
		    	if(name.equals(linkStartInfo.get("name"))){
		    		connection = con;
		    		break;
		    	}		        
		    }
		    if(connection == null){
		    	connection = new Thing("xworker.lang.flow.uiflow.Connection");
		    	connection.put("name", linkStartInfo.get("name"));
		    	self.addChild(connection);
		    }
		    connection.put("label", linkStartInfo.get("label"));
		    connection.put("type", linkStartInfo.get("type"));
		    connection.put("nodeRef", target.getMetadata().getPath());
		    self.save();
		}
		
		//重建连接
		uiFlow.recreateConnection(self);			
	}
	
	/**
	 * 各种脚本类型的节点运行。
	 * 
	 * @param actionContext
	 */
	public static void scriptFlowRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		//把UI清空
		if(uiFlow instanceof UiFlow){
			Composite contentComposite = actionContext.getObject("contentComposite");
			if(contentComposite != null){
				for(Control child : contentComposite.getChildren()){
					child.dispose();
				}
			}
		}

		//执行动作
		Object result = self.getAction().run(actionContext);
		
		//保存值，如果属性varForSave不为空的话，由于是动作，也可以通过动作设置属性
		self.doAction("saveValue", actionContext, "value", result);
				
		//如果自动结束执行下级节点		
		if(self.getBoolean("autoFinished")){
			uiFlow.nodeFinished(self, String.valueOf(result));
		}
	}
	
	public static ActionFlow runAsAction(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		if(!XWorkerUtils.hasWebServer()){
			try {
				ThingUtils.startRegistThingCache();

				Class<?> webServer = Class.forName("xworker.webserver.XWorkerWebServer");
				Method run = webServer.getMethod("run");
				run.invoke(null);
			}catch(Exception ignored){
			}
		}
		
		String runMethod = self.getStringBlankAsNull("runMethod");
		if("shell".equals(runMethod)){
			Thing shellThing = World.getInstance().getThing("xworker.lang.flow.uiflow.CompositeFlowShell");
			actionContext.peek().put("flowThing", self);
			actionContext.peek().put("type", "simple");
			
			shellThing.doAction("run", actionContext);
		}else if("shellWithOutline".equals(runMethod)){
			Thing shellThing = World.getInstance().getThing("xworker.lang.flow.uiflow.DesignFlowShell");
			actionContext.peek().put("flowThing", self);
			actionContext.peek().put("type", "simple");
			
			shellThing.doAction("run", actionContext);
		}else if("design".equals(runMethod)){
			Thing shellThing = World.getInstance().getThing("xworker.lang.flow.uiflow.DesignFlowShell");
			actionContext.peek().put("flowThing", self);
			actionContext.peek().put("type", "editor");
			
			shellThing.doAction("run", actionContext);
		}else{
			ActionFlow af = new ActionFlow(self, actionContext);
			af.start();
			return af;
		}
		
		return null;
	}
	
	public static void nodeFinished(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Thing flowNode = actionContext.getObject("flowNode");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		String nextName = (String) self.doAction("getNextName", actionContext);
		uiFlow.nodeFinished(flowNode, nextName);
	}
	
	public static void saveValue(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		//变量名
		String name = (String) self.doAction("getVarForSave", actionContext);
		if(name != null){
			actionContext.g().put(name, actionContext.get("value"));			
		}
		
	}
	
	/**
	 * 用于快速制作界面节点类型的。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void compositeFlowRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing desc = self.getDescriptor();
		
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		//获取面板
		Thing composite = (Thing) desc.doAction("getComposite", actionContext);
		if(composite == null){
			throw new ActionException("Composite is null, node=" + self.getMetadata().getPath());
		}
		
		ActionContext ac = uiFlow.runComposite(self, composite);
		
		//创建按钮
		World world = World.getInstance();
		Composite parent = (Composite) ac.get(composite.getMetadata().getName());
		ac.peek().put("parent", parent);
		
		Thing buttonComposite = world.getThing("xworker.lang.flow.uiflow.nodes.base.prototypes.CompositeWithButton/@47221");
		Composite bc = (Composite) buttonComposite.doAction("create", ac);
		ac.peek().put("parent", bc);
		
		Thing buttonPrototype = world.getThing("xworker.lang.flow.uiflow.nodes.base.prototypes.CompositeWithButton/@4725");
		List<Map<String, String>> cons = (List<Map<String, String>>) self.doAction("getConnectionStarts", actionContext);
		if(cons != null){
			for(Map<String, String> con : cons){
				Button button = (Button) buttonPrototype.doAction("create", ac);
				button.setText(con.get("label"));
				button.setData(con);
				ac.g().put(con.get("name") + "Button", button);
			}
			
			parent.layout();
		}
	}
}
