package xworker.html.design;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.html.HtmlConstants;
import xworker.html.HtmlUtil;
import xworker.html.extjs.ExtJsCreator;
import xworker.util.JacksonFormator;

/**
 * 设计节点。
 * 
 * @author Administrator
 *
 */
public class DesignNode {
	public static final String resourceKey = "__designNode__resourceId__";
	
	/**
	 * 生成正常的界面生成时的html界面。
	 * 
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
	
		Map<String, Resource> resources = (Map<String, Resource>) actionContext.get(resourceKey);
		if(resources == null){
			resources = new HashMap<String, Resource>();
			actionContext.peek().put(resourceKey, resources);
		}
		
		if(actionContext.get("desingHttpDo") == null){
			//导入Extjs的=
			ExtJsCreator.importExtjs(actionContext);
		}
		
		String html = "<div xworker_design=\"true\" id=\"" + getId(self) + "\" style=\"" + getStyle(self) +
				"\" xworker_label=\"" + self.getMetadata().getLabel() +  
				"\" thing=\"" + self.getMetadata().getPath() + "\">";
		html = html + HtmlUtil.getChildsHtml(actionContext) + "</div>";		
		
		return html;
	}
	
	/**
	 * 动态刷新界面的方法。
	 * 
	 * @param actionContext
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static void httpDo(ActionContext actionContext) throws IOException, Exception{
		//Thing self = (Thing) actionContext.get("self");
		List<Map<String, String>> heads = new ArrayList<Map<String, String>>();
        List<Map<String, String>> bottoms = new ArrayList<Map<String, String>>();
        List<Thing> bottomThings = new ArrayList<Thing>();
        
        Thing self = (Thing) actionContext.get("self");
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        if("remove".equals(request.getParameter("action"))){
        	self.remove();
        	
        	Map<String, Object> result = new HashMap<String, Object>();
			result.put("success", true);
        	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			response.setContentType("text/json; charset=utf-8");
			response.getWriter().print(JacksonFormator.formatObject(result));
        }else{
	        String bodyAttributes = self.getString("bodyAttributes");
	        if(bodyAttributes == null) bodyAttributes = "";
		        
			Bindings bindings = actionContext.peek();
			bindings.put("heads", heads);
			bindings.put("bottoms", bottoms);
			bindings.put("bottomThings", bottomThings);
			bindings.put("bodyAttributes", bodyAttributes);
			bindings.put("desingHttpDo", true);
	        Thing javaScriptThing = new Thing("xworker.html.base.scripts.JavaScript");
	        javaScriptThing.put("useChildsCode", "true");
	        bindings.put(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING, javaScriptThing);
	        
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("success", true);
			String childHtml = HtmlUtil.getChildsHtml(actionContext); 
			if(childHtml == null){
				childHtml = "";
			}
			result.put("html", childHtml);
			if(heads.size() > 0){
				result.put("refresh", true);
			}else{
				result.put("refresh", false);
			}
			
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			response.setContentType("text/json; charset=utf-8");
			response.getWriter().print(JacksonFormator.formatObject(result));
        }
	}
	
	public static String getStyle(Thing self){
		String style = "";
		style += getStyle(self, "position");
		style += getStyle(self, "left");
		style += getStyle(self, "top");
		style += getStyle(self, "width");
		style += getStyle(self, "height");
		
		return style;
	}
	
	public static String getStyle(Thing self, String name){
		String value = self.getStringBlankAsNull(name);
		if(value != null){
			return "name=" + value + ";";
		}else{
			return "";
		}
	}
	
	/**
	 * 节点的标识。
	 * 
	 * @param self
	 * @return
	 */
	public static String getId(Thing self){
		return "design_node_" + self.getMetadata().getPath().hashCode();
	}
	
	/**
	 * 生成在设计是动态加载的HTML页面。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toDesignLoadHtml(ActionContext actionContext){
		return null;
	}
}
