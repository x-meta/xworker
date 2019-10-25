package xworker.ide.utils;


public class ThingZestUtils {
	/*
	@SuppressWarnings("unchecked")
	public static void showThingAtGraph(Graph graph, Thing thing, ActionContext actionContext){		
		//销毁先前的，如果存在
		List<GraphItem> data = (List<GraphItem>) graph.getData();
		List<GraphItem> oldItems = data;
		if(oldItems != null){
			for(GraphItem item : oldItems){
				try{
					item.dispose();
				}catch(Exception e){
					
				}
			}
		}
		
		//预估大小
		Dimension size = new Dimension(1, 1);
		calDimension(thing ,size, 1);
		graph.setPreferredSize(size.width * 80, size.height * 50);
		
		List<GraphItem> items = new ArrayList<GraphItem>();		
		GraphNode root = createNode(graph, graph, thing, items, actionContext);
		createChildNodes(graph, root, thing, items,  actionContext);
		
		graph.setData(items);
		graph.applyLayout();
	}
	
	public static void createChildNodes(Graph graph, GraphNode parentNode, Thing thing, List<GraphItem> items, ActionContext actionContext){
		for(Thing child : thing.getChilds()){
			//创建子节点
			GraphNode childNode = createNode(graph, graph, child, items, actionContext);
			items.add(childNode);
			
			//创建连接
			items.add(new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, parentNode, childNode));
			
			//创建子子节点
			createChildNodes(graph, childNode, child, items, actionContext);
		}
	}
	
	public static GraphNode createNode(Graph graph, IContainer graphModel, Thing thing, List<GraphItem> items,  ActionContext actionContext){
		String label =  thing.getMetadata().getLabel();
		GraphNode node = new GraphNode(graphModel, ZestStyles.NODES_NO_LAYOUT_RESIZE, label);
		node.setCacheLabel(true);
		Dimension size = node.getSize();
		node.setSize(label.length() * 10, size.height);
		
		Image icon = XWorkerTreeUtil.getIcon(thing, graph, actionContext);
		if(icon != null){
			node.setImage(icon);
		}
		
		items.add(node);
		return node;
	}
	
	public static void calDimension(Thing thing, Dimension size, int level){		
		if(size.height < level){
			size.height = level;
		}
		
		int myLevel = level + 1;
		for(Thing child : thing.getChilds()){			
			size.width = size.width + 1;
			calDimension(child, size, myLevel);
		}
	}
	*/
}
