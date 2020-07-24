package xworker.ide.utils.translate;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ide.index.entities.Languages;
import xworker.io.SystemIoRedirector;

/**
 * 翻译模型相关的工具。
 * 
 * @author zhangyuxiang
 *
 */
public class TranslateUtils {
	static ActionContext actionContext = new ActionContext();
	static long lastQueryTime = 0;
	
	/** 翻译的动作 */
	static Action translate = null;
	
	public static void init() {
		translate = World.getInstance().getAction("xworker.ide.worldexplorer.actions.TranslateActions/@Translate");
	}
	
	/**
	 * 翻译模型的描述文档。
	 * @param thing
	 * @param to
	 */
	public static void translateDescription(Thing thing, String to) {
		String name = to + "_description";
		if(thing.getStringBlankAsNull(name) == null) {
			//没有设置相关语言
			String content = thing.getStringBlankAsNull("description");
			if(content != null) {
				Languages l = Languages.getLanguage(thing, "description", to);
				if(l == null || l.getLastmodify() != thing.getMetadata().getLastModified()) {	
					Document document = Jsoup.parseBodyFragment(content);
				}
				
			}
		}		
	}
	
	public static void translateNode(Node node) {
		if(node instanceof TextNode) {
			String text = ((TextNode) node).text();
			if(text != null && !"".equals(text.trim())) {
				
			}
		}
	}
	
	/**
	 * 翻译模型的标签。
	 * 
	 * @param thing
	 * @param to
	 */
	public static void translateLabel(Thing thing, String to) {
		String name = to + "_label";
		if(thing.getStringBlankAsNull(name) == null) {
			//没有设置相关语言
			String label = thing.getStringBlankAsNull("label");
			if(label != null) {
				Languages l = Languages.getLanguage(thing, "label", to);
				if(l == null || l.getLastmodify() != thing.getMetadata().getLastModified()) {				
					String dest = translate(label, to);
					if(dest != null) {
						//保存
						if(l != null) {
							l.setContent(dest);
							l.setLastmodify(thing.getMetadata().getLastModified());
							l.update(new ActionContext());
						}else {
							l = new Languages();
							l.setAttribute("label");
							l.setContent(dest);
							l.setLang(to);
							l.setLastmodify(thing.getMetadata().getLastModified());
							l.setThing(thing.getMetadata().getPath());
							l.create(new ActionContext());
						}
					}
				}
			}
		}
	}
	
	public static String translate(String query, String lang) {
		long time = System.currentTimeMillis() - lastQueryTime; 
		if(time < 1000) {
			//免费账号，限制1秒最多一次访问
			try {
				Thread.sleep(1200 - time); //为保险，多睡眠200毫秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Map<String, Object> result = translate.run(actionContext, "query", query, "to", lang);
		return getTransResult(result);
	}
	/**
	 * 从百度翻译的结果里获取翻译的结果。
	 * 
	 * @param result
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTransResult(Map<String, Object> result) {
		if(result == null) {
			return null;
		}
		
		List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("trans_result");
		if(results != null && results.size() > 0) {
			return (String) results.get(0).get("dest");
		}else {
			return null;
		}
				
	}
	
	
	public static void main(String args[]) {
		try{				
			//初始化引擎
			World world = World.getInstance();			
			world.init("./xworker/");
			
			Thread.currentThread().setContextClassLoader(world.getClassLoader());
			SystemIoRedirector.init();
			
			TranslateUtils.init();
			Thing thing = world.getThing("xworker.lang.MetaDescriptor3");
			translateLabel(thing, "en");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
