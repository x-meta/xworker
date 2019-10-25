package xworker.manong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.UtilTemplate;
import xworker.util.XWorkerUtils;

public class MaNongJavaScriptFunction extends BrowserFunction{

	public MaNongJavaScriptFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		String action = arguments[0].toString();
		String projectId = arguments[1].toString();
		if("check".equals(action)){
			String majorVersionStr = arguments[2].toString();
			String minorVersionStr = arguments[3].toString();
			int majorVersion = Integer.parseInt(majorVersionStr);
			int minorVersion = Integer.parseInt(minorVersionStr);
			
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("projectId", projectId);
			context.put("majorVersion", String .valueOf(majorVersion));
			context.put("minorVersion", String.valueOf(minorVersionStr));
			context.put("arguments", arguments);			
			
			Thing project = World.getInstance().getThing(projectId);
			if(project != null){				
				context.put("project", project);
				project = project.getRoot();
				putLinks(project, context);
				//理论上服务器上的版本应该总是最新的
				if(project.getInt("majorVersion") != majorVersion || project.getInt("minorVersion") != minorVersion){
					context.put("command", "update");
				}else{
					context.put("command", "noupdate");					
				}
			}else{
				context.put("command", "download");				
			}
			
			try {
				String html = UtilTemplate.process(context, "/xworker/manong/web/project_link.ftl", "freemarker");
				//System.out.println(html);
				return html;
			} catch (Throwable e) {
				e.printStackTrace();
				return "";
			}
		}else if("download".equals(action)){
			//到根项目
			int index = projectId.indexOf("/@");
			if(index >= 0){
				projectId = projectId.substring(0, index);
			}
			
			MaNongClient.download(projectId);
			
			return "版本已是最新";
		}else if("links".equals(action)){
			Thing project = World.getInstance().getThing(projectId);
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("projectId", projectId);
			context.put("historygo", "true");
			if(project != null){				
				context.put("majorVersion", project.getString("majorVersion"));
				context.put("minorVersion",  project.getString("minorVersionStr"));
				context.put("arguments", arguments);	
				context.put("project", project);
				project = project.getRoot();
				putLinks(project, context);
				context.put("command", "noupdate");
				
			}else{
				String args[] = new String[6];
				args[4]  = "functions";
				args[5] = projectId;
				context.put("arguments", args);
				context.put("majorVersion", "0");
				context.put("minorVersion",  "0");
				context.put("command", "download");				
			}
			
			try {
				String html = UtilTemplate.process(context, "/xworker/manong/web/project_link.ftl", "freemarker");
				//System.out.println(html);
				return html;
			} catch (Throwable e) {
				e.printStackTrace();
				return "";
			}
		}else{
			return "未知的命令";
		}				
	}
	
	public static void putLinks(Thing project, Map<String, Object> context){
		List<Map<String, Object>> lks = new ArrayList<Map<String, Object>>();
		Thing links = project.getThing("Links@0");
		if(links != null){
			for(Thing link : links.getChilds()){
				Map<String, Object> l = new HashMap<String, Object>();
				l.put("label", link.getMetadata().getLabel());
				
				if("url".equals(link.getString("type"))){
					l.put("href", link.getString("path"));
				}else if("localURL".equals(link.getString("type"))){
					l.put("href", XWorkerUtils.getWebUrl() + link.getString("path"));
				}else{
					l.put("href", "javascript:xw_invoke('" + link.getString("type") + ":" + link.getString("path") + "')");
				}
				lks.add(l);
			}
		}
		
		context.put("links", lks);
	}
	public static String getChildProjectString(Thing project, String nbsp){
		Thing config = World.getInstance().getThing("_local.manong.MaNongConfig");
		String str = null;		
		for(Thing child : project.getChilds("SubProject")){
			if(str == null){
				str = nbsp;
			}else{
				str = "<br/>" + nbsp;
			}
			
			str = str + "<a href=\"" + config.getString("serverUrl") + "do?sc=xworker.manong.web.ViewProject&projectId=" + child.getMetadata().getPath() + "\">" + child.getMetadata().getLabel() + "</a>";
			
			String childStr = getChildProjectString(child, nbsp + "&nbsp;&nbsp;&nbsp;&nbsp;");
			if(childStr != null){
				str = str + "<br/>" + childStr;
			}
		}
		
		return str;
		
	}

}
