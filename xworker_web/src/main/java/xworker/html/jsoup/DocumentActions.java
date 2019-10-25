package xworker.html.jsoup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
}
