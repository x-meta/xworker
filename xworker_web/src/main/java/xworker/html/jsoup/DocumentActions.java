package xworker.html.jsoup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class DocumentActions {
	public static Document parse(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		String parameterType = self.getStringBlankAsNull("parameterType");
		if(parameterType == null){
			throw new ActionException("JSoup: parameterType is null, path=" + self.getMetadata().getPath());
		}
		
		if("file_charsetName".equals(parameterType)){
			File file = (File) self.doAction("getFile", actionContext);
			String charsetName = (String) self.doAction("getCharsetName", actionContext);
			return Jsoup.parse(file, charsetName);
		}else if("file_charsetName_baseUri".equals(parameterType)){
			File file = (File) self.doAction("getFile", actionContext);
			String charsetName = (String) self.doAction("getCharsetName", actionContext);
			String baseUri = (String) self.doAction("getBaseUri", actionContext);
			return Jsoup.parse(file, charsetName, baseUri);
		}else if("inputstream_charsetName_baseUri".equals(parameterType)){
			InputStream in = (InputStream) self.doAction("getInputstream", actionContext);
			String charsetName = (String) self.doAction("getCharsetName", actionContext);
			String baseUri = (String) self.doAction("getBaseUri", actionContext);
			return Jsoup.parse(in, charsetName, baseUri);
		}else if("inputstream_charsetName_baseUri_parser".equals(parameterType)){
			InputStream in = (InputStream) self.doAction("getInputstream", actionContext);
			String charsetName = (String) self.doAction("getCharsetName", actionContext);
			String baseUri = (String) self.doAction("getBaseUri", actionContext);
			Parser parser = (Parser) self.doAction("getParser", actionContext);
			return Jsoup.parse(in, charsetName, baseUri, parser);
		}else if("html".equals(parameterType)){
			String html = (String) self.doAction("getHtml", actionContext);
			return Jsoup.parse(html);
		}else if("html_baseUri".equals(parameterType)){
			String html = (String) self.doAction("getHtml", actionContext);
			String baseUri = (String) self.doAction("getBaseUri", actionContext);
			return Jsoup.parse(html, baseUri);
		}else if("html_baseUri_parser".equals(parameterType)){
			String html = (String) self.doAction("getHtml", actionContext);
			String baseUri = (String) self.doAction("getBaseUri", actionContext);
			Parser parser = (Parser) self.doAction("getParser", actionContext);
			return Jsoup.parse(html, baseUri, parser);
		}else if("url_timeoutMillis".equals(parameterType)){
			String url = UtilData.getString(self.doAction("getUrl", actionContext), null);
			int timeoutMillis = UtilData.getInt(self.doAction("getTimeoutMillis", actionContext), 3000);
			return Jsoup.parse(new URL(url), timeoutMillis);
		}else{
			throw new ActionException("JSoup: unkonow parameterType '" + parameterType + "', path=" + self.getMetadata().getPath());
		}
	}
	
	public static Elements select(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Element element = (Element) self.doAction("getElement", actionContext);
		String query = (String) self.doAction("query", actionContext);
		return element.select(query);
	}
	
	public static Document parseFile(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		String charsetName = self.doAction("getChatsetName", actionContext);
		String baseUri = self.doAction("getBaseUri", actionContext);
		
		if(baseUri != null && charsetName != null) {
			return Jsoup.parse(file, charsetName, baseUri);
		}else if(charsetName != null) {
			return Jsoup.parse(file, charsetName);
		}else if(baseUri != null) {
			return Jsoup.parse(file, "utf-8", baseUri);					
		}else {
			return Jsoup.parse(file, "utf-8");
		}
	}
	
	public static Document parseInputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		InputStream in = self.doAction("getInputStream", actionContext);
		String charsetName = self.doAction("getChatsetName", actionContext);
		String baseUri = self.doAction("getBaseUri", actionContext);
		
		if(baseUri != null && charsetName != null) {
			return Jsoup.parse(in, charsetName, baseUri);
		}else if(charsetName != null) {
			return Jsoup.parse(in, "utf-8", charsetName);
		}else if(baseUri != null) {
			return Jsoup.parse(in, "utf-8", baseUri);					
		}else {
			return Jsoup.parse(in, "utf-8", "/");
		}
	}
	
	public static Document parseHtml(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String baseUri = self.doAction("getBaseUri", actionContext);
		String html = self.doAction("getHtml", actionContext);
		if(baseUri != null) {
			return Jsoup.parse(html, baseUri);
		}else {
			return Jsoup.parse(html);
		}
	}
	
	public static Document parseURL(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		URL url = self.doAction("getURL", actionContext);
		long timeoutMillis = self.doAction("getTimeoutMillis", actionContext);
		
		return Jsoup.parse(url, (int) timeoutMillis);
	}
	
	public static Document parseBodyFragment(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String baseUri = self.doAction("getBaseUri", actionContext);
		String html = self.doAction("getHtml", actionContext);
		if(baseUri != null) {
			return Jsoup.parse(html, baseUri);
		}else {
			return Jsoup.parse(html);
		}
	}
	
	public static void iteratorElements(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Element element = self.doAction("getElement", actionContext);
		iteratorElements(self, element, actionContext);
	}
	
	public static void iteratorElements(Thing self, Element element, ActionContext actionContext) {		
		self.doAction("onElement", actionContext, "element", element, "document", element.ownerDocument());
		
		//遍历子节点
		for(Element childElement : element.children()) {
			iteratorElements(self, childElement, actionContext);
		}
	}
	
	public static void iteratorNodes(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Element element = self.doAction("getElement", actionContext);
		iteratorNodes(self, element, actionContext);
	}
	
	public static void iteratorNodes(Thing self, Node node, ActionContext actionContext) {		
		self.doAction("onNode", actionContext, "node", node, "document", node.ownerDocument());
		
		//遍历子节点
		for(Node childElement : node.childNodes()) {
			iteratorNodes(self, childElement, actionContext);
		}
	}
}
