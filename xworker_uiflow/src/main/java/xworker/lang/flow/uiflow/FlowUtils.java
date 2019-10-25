package xworker.lang.flow.uiflow;

import org.xmeta.Thing;
import org.xmeta.World;

public class FlowUtils {
	public static Thing getNextFlowNode(Thing flowNode, String name){
		Thing next = getNextFlowNodeByName(flowNode, name);
		if(next != null){
			return next;
		}
		
		String nextNode = flowNode.getStringBlankAsNull("nextNode");
		if(nextNode != null){
			return getFlowNode(flowNode, nextNode);
		}else{
			return null;
		}
	}
	
	public static Thing getNextFlowNodeByName(Thing flowNode, String name){
		for(Thing con : flowNode.getChilds("Connection")){
			String cname = con.getMetadata().getName();
			if(cname.equals(name)){				
				return getFlowNode(flowNode, con.getStringBlankAsNull("nodeRef"));
			}
		}
		
		return null;
	}
	
	public static Thing getFlowNode(Thing flowNode, String path){
		if(path.startsWith("/@")){
			path = flowNode.getRoot().getMetadata().getPath() + path; 
		}
		
		return World.getInstance().getThing(path);
	}
		
	/**
	 * 保存流程事物，如果nextNode或nodeRef的属性是子事物，那么过滤掉根路径。
	 * 
	 * @param flowThing
	 */
	public static void saveFlow(Thing flowThing){
		Thing root = flowThing.getRoot();
		//String rootPath = root.getMetadata().getPath();
		//trim(root, rootPath);
		root.save();
	}
	
	@SuppressWarnings("unused")
	private static void trim(Thing thing, String rootPath){
		String nextNode = thing.getStringBlankAsNull("nextNode");
		if(nextNode != null && nextNode.startsWith(rootPath)){
			nextNode = nextNode.substring(rootPath.length(), nextNode.length());
			thing.put("nextNode", nextNode);
		}
		
		String nodeRef = thing.getStringBlankAsNull("nodeRef");
		if(nodeRef != null && nodeRef.startsWith(rootPath)){
			nodeRef = nodeRef.substring(nodeRef.length(), nextNode.length());
			thing.put("nodeRef", nodeRef);
		}
		
		for(Thing child : thing.getChilds()){
			trim(child, rootPath);
		}
	}
}
