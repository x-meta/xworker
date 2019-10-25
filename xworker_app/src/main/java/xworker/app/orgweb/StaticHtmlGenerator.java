package xworker.app.orgweb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.util.UtilTemplate;

public class StaticHtmlGenerator {
	public static final String indexDo = "do?sc=xworker.app.orgweb.web.Index&id=";
	
	public String webRoot;
	public String fileRoot;
	public int rootId;
	public String rootFileName;
	Map<String, Date> context = new HashMap<String, Date>();
	Map<String, String> urls = new HashMap<String, String>();
	
	public StaticHtmlGenerator(String webRoot, String fileRoot, int rootId, String rootFileName){
		this.webRoot = webRoot;
		this.fileRoot = fileRoot;
		this.rootId = rootId;
		this.rootFileName = rootFileName;
	}
	
	public void generateHtmls() throws Throwable{
		ContentTreeManager.loadCache();
		Map<Integer, ContentTree> cache = ContentTreeManager.getCache();
		
		//找到项目并按照项目生成
		for(Integer key : cache.keySet()){
			ContentTree contentTree  = cache.get(key);
			if(contentTree.parentId == 0 && contentTree.refId != 0){
				ContentTree index = cache.get(contentTree.refId);
				if(index != null){
					rootId = index.id;
					generateIndexHtml(index);
				}
			}
		}
		//ContentTree contentTree = cache.get(rootId) ;
		//generateIndexHtml(contentTree);
		
		/*
		for(Integer key : cache.keySet()){
			ContentTree contentTree = cache.get(key) ;
			if(contentTree == null){
				continue;
			}
			
			generateIndexHtml(contentTree);
		}*/
		
		generateSiteMap();
	}
	
	private String getContentTreeIndexPath(ContentTree contentTree){
		if(rootId == contentTree.id){			
			return getRootPath(contentTree) + rootFileName;
		}else{
			return getContentTreeDir(contentTree) + "/" + contentTree.id + ".html";
		}
	}
	
	private String getContentTreeDir(ContentTree contentTree){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
		if(contentTree.id == 0){
			return sf.format(new Date());
		}
		
		String root = getRootPath(contentTree);		
		return root + sf.format(contentTree.createDate);
	}
	
	private String getRootPath(ContentTree contentTree){
		ContentTree prj = contentTree.getProject();
		if(prj != null){
			//使用target作为输出目录
			String target = prj.target;
			if("\\".equals(target) || "/".equals(target) || "".equals(target) || target == null || target.startsWith("_")){
				return  "";
			}else{
				return target + "/";
			}
		}else{
			return "";
		}
	}
	
	private boolean isGenerated(String url, Date date){
		if(context.get(url) == null){
			context.put(url, date);
			return false;
		}else{
			return true;
		}
	}
	
	public void generateSiteMap() throws Throwable{
		//ContentTreeManager.loadCache();
		ActionContext ac = new ActionContext();
		ac.put("loc", webRoot);
		
		List<Map<String, String>> htmls = new ArrayList<Map<String, String>>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		for(String key : context.keySet()){
			Date d = context.get(key);
			if(d == null){
				System.out.println("date is null: " + key);
				continue;
			}
			String date = sf.format(context.get(key));			
			Map<String, String> html = new HashMap<String, String>();
			html.put("url", webRoot + "/" + urls.get(key));
			html.put("date", date);
			htmls.add(html);
		}
		ac.put("htmls", htmls);
		
		FileWriter sitemapWriter = new FileWriter(new File(fileRoot + "/sitemap.xml"));
		FileWriter rssmapWriter = new FileWriter(new File(fileRoot + "/rssmap.xml"));
		UtilTemplate.process(ac, "xworker/app/orgweb/web/sitemap.ftl", "freemarker", sitemapWriter);
		System.out.println("sitemap.xml generated");
		UtilTemplate.process(ac, "xworker/app/orgweb/web/rssmap.ftl", "freemarker", rssmapWriter);
		System.out.println("rssmap.xml generated");
		sitemapWriter.close();
		rssmapWriter.close();
	}
	
	public String getHrefFilePath(Element link, String href, ContentTree parent) throws Exception{
		int index = href.indexOf("?");
		Map<String, String> params = UtilString.getParams(href.substring(index + 1, href.length()));
		String sc = params.get("sc");
		if(sc == null || "".equals(sc)){
			return null;
		}
		
		if("xworker.app.orgweb.web.Index".equals(sc)){
			ContentTree contentTree = ContentTreeManager.getContentTree(Integer.parseInt(params.get("id")));
			if(contentTree != null){
				generateIndexHtml(contentTree);
				String url = getContentTreeIndexPath(contentTree);
				return url;
			}else{
				return null;
			}
		}else if("xworker.app.orgweb.web.IndexSearch".equals(sc)){
			return null;
			/*
			String id = params.get("id");
			String page = params.get("page");
			if(page == null || "".equals(page)){
				page = "1";
			}
			
			ContentTree contentTree = ContentTreeManager.getContentTree(Integer.parseInt(id));
			if(contentTree == null){
				return null;
			}
			SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
			String filePath = sf.format(contentTree.createDate) + "/" + contentTree.id + "_" + page + ".html";
			
			if(!isGenerated(filePath, contentTree.createDate)){
				System.out.println("starting save " + href + " to " + filePath);
				saveUrlToFile(href, filePath, contentTree);
				System.out.println("finished save " + href + " to " + filePath);
			}
			
			return filePath;			*/
		}else if("xworker.app.orgweb.web.IndexSearchView".equals(sc)){
			return null;
			/*
			String contentId = params.get("contentId");
			String id = params.get("id");
			
			ContentTree contentTree = ContentTreeManager.getContentTree(Integer.parseInt(contentId));
			SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
			String filePath = sf.format(contentTree.createDate) + "/" + contentTree.id + "_" + id + ".html";
			if(!isGenerated(filePath, contentTree.createDate)){
				System.out.println("starting save " + href + " to " + filePath);
				saveUrlToFile(href, filePath, contentTree);
				System.out.println("finished save " + href + " to " + filePath);
			}
			
			return filePath;			*/
		}else{
			return null;
		}
	}	

	public String getHtmlByUrl(String url) throws MalformedURLException, IOException{
		System.out.println("get url content: " + url);
		return IOUtils.toString(new URL(url));
	}
	
	public String urlToRelate(String url, ContentTree contentTree){
		if(url.toLowerCase().startsWith("http://")){
			return url;
		}
		
		String root = getRootPath(contentTree);
		if(contentTree.id == rootId){
			if(url.startsWith("./")){
				if("".equals(root)){
					return url;
				}else{
					return "." + url;
				}
			}else if(url.startsWith("/")){
				if("".equals(root)){
					return "." + url;
				}else{
					return ".." + url;
				}
			}else{
				return url;
			}			
		}else{		
			String rurl = null;
			if(url.startsWith("./")){
				rurl =  "../../." + url;
			}else if(url.startsWith("/")){
				rurl = "../../.." + url; 
			}else{
				rurl = "../../../" + url;
			}			
			if(!"".equals(root)){
				rurl = "../" + rurl;
			} 
			
			return rurl;
		}
	}
	
	public void saveUrlToFile(String url, String filePath, ContentTree contentTree) throws Exception{
		String html = getHtmlByUrl(url);
		//替换连接
		Document doc = Jsoup.parse(html);
		Elements els = doc.select("a");
		for(int i=0; i<els.size(); i++){			
			Element link = els.get(i);
			String href = link.attr("href");
			Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");    	
			String aurl =  globalConfig.getString("webUrl") + href;
			
			String hrefFilePath = getHrefFilePath(link, aurl, contentTree);
			if(hrefFilePath != null){	
				if(contentTree.id == rootId){
					hrefFilePath = "./" + hrefFilePath;
				}else{								
					//hrefFilePath = "../../../../" + hrefFilePath;
				}
				link.attr("href", urlToRelate(hrefFilePath, contentTree));
			}else if(href.startsWith("do?sc=")){
				link.attr("href", urlToRelate(href, contentTree));//"/" + href);
			}
		}
		
		//img为绝对目录
		els = doc.select("img");
		for(int i=0; i<els.size(); i++){			
			Element link = els.get(i);
			String src = link.attr("src");
			if(src != null && !"".equals(src)){
				link.attr("src", urlToRelate(src, contentTree));
			}
			if(link.attr("data-gif") != null){
				link.attr("data-gif", urlToRelate(link.attr("data-gif"), contentTree));
			}			
		}
		
		els = doc.select("script");
		for(int i=0; i<els.size(); i++){			
			Element link = els.get(i);
			String src = link.attr("src");
			if(src != null && !"".equals(src)){
				link.attr("src", urlToRelate(src, contentTree));
			}
			
		}
		
		els = doc.select("link");
		for(int i=0; i<els.size(); i++){			
			Element link = els.get(i);
			String src = link.attr("href");
			link.attr("href", urlToRelate(src, contentTree));			
		}
		
		//重新生成HTML
		html = doc.outerHtml();
		File htmlFile = new File(fileRoot + "/" + filePath);
		if(!htmlFile.exists()){
			htmlFile.getParentFile().mkdirs();
		}
		urls.put(url, filePath);
		FileOutputStream fout = new FileOutputStream(htmlFile);
		try{
			fout.write(html.getBytes());
		}finally{
			fout.close();
		}
		
	}
	
	private void generateIndexHtml(ContentTree contentTree) throws Exception{
		if(contentTree == null){
			return;
		}
		
		String filePath = getContentTreeIndexPath(contentTree);		
		Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");    	
		String url = globalConfig.getString("webUrl") + "do?sc=xworker.app.orgweb.web.Index&id=" + contentTree.id;
		
		if(isGenerated(url, contentTree.updateDate)){
			return;
		}
		
		System.out.println("starting save " + url + " to " + filePath);
		saveUrlToFile(url, filePath, contentTree);
		System.out.println("finished save " + url + " to " + filePath);
	}
}
