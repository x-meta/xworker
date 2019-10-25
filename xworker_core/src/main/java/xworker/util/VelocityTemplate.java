package xworker.util;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.World;

public class VelocityTemplate {
	private static Logger log = LoggerFactory.getLogger(VelocityTemplate.class);
	static Map<String, VelocityEngine> velocityHandlers = new HashMap<String, VelocityEngine>();
	
	public static VelocityEngine stringEngine;
	
	static{
		//初始化velocity的StringResourceLoader
		Properties p = new Properties();
		p.put("resource.loader", "string");
		
		p.put("string.resource.loader.description", "Velocity String Resource Loader");
		p.put("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
		
		try {
			stringEngine = new VelocityEngine();
			stringEngine.init(p);
		} catch (Exception e) {
			log.error("init velocity", e);				 
		}
	}
	
	public static void putStringResource(String templateName, String templateCode){
		StringResourceLoader.getRepository().putStringResource(templateName, templateCode);		
	}
	
	public static void process(String templateConfigName, boolean isStringResource, String templateName, String encoding, Object object, Writer outputWriter) throws IOException{
		VelocityEngine engine = (VelocityEngine) velocityHandlers.get(templateConfigName);
		if(engine == null){
			Properties p = new Properties();
			p.put("resource.loader", "file");
			p.put("file.resource.loader.description", "Velocity File Resource Loader");
			p.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			p.put("file.resource.loader.path", World.getInstance().getPath());
			p.put("file.resource.loader.cache", "true");
			p.put("file.resource.loader.modificationCheckInterval", "0");
			
			engine = new VelocityEngine();
			engine.init(p);			
			velocityHandlers.put("projectName", engine);
		}
		
		org.apache.velocity.Template template = null;
		if(isStringResource){
			template = stringEngine.getTemplate(templateName);
		}else{
			if(encoding != null){
				template = engine.getTemplate(templateName, encoding);
			}else{
				template = engine.getTemplate(templateName);
			}
		}
		
		template.merge((org.apache.velocity.VelocityContext) object, outputWriter);
		outputWriter.flush();
	}
}
