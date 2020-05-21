package xworker.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class PropertiesActions {
	public static Properties createProperties(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String strategy = self.getStringBlankAsNull("strategy");
		File propertiesFile1 = self.doAction("getPropertiesFile1", actionContext);
		File propertiesFile2 = self.doAction("getPropertiesFile2", actionContext);
		File propertiesFile3 = self.doAction("getPropertiesFile3", actionContext);
		
		Properties p = new Properties();
		boolean loadAll = "all".equals(strategy);
		if(loadProperties(p, propertiesFile1) && !loadAll){
			return p;
		}
		if(loadProperties(p, propertiesFile2) && !loadAll){
			return p;
		}
		if(loadProperties(p, propertiesFile2) && !loadAll){
			return p;
		}
		
		return p;
	}
	
	private static boolean loadProperties(Properties p, File file){
		if(file == null || file.exists() == false){
			return false;
		}
		
		if(file.exists()){
			FileInputStream fin = null;
			try{
				fin = new FileInputStream(file);
				p.load(fin);
				return true;
			}catch(Exception e){
				return false;
			}finally{
				if(fin != null){
					try {
						fin.close();
					} catch (IOException e) {
					}
				}
			}
		}else{
			return false;
		}
	}
	
	public static Object getProperty(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Properties p = (Properties) self.doAction("getProperties", actionContext);
		String propertyName = (String) self.doAction("getPropertyName", actionContext);
		if(p == null){
			throw new ActionException("Properties is null, path=" + self.getMetadata().getPath());
		}
		if(propertyName == null){
			throw new ActionException("PropertieName is null, path=" + self.getMetadata().getPath());
		}
		
		return p.get(propertyName);
	}
	
	public static Properties getProperties(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String propertiesVar = self.getStringBlankAsNull("propertiesVar");
		if(propertiesVar != null){
			return (Properties) OgnlUtil.getValue(self, "propertiesVar", actionContext);
		}else{
			String propertiesThing = self.getStringBlankAsNull("propertiesThing");
			if(propertiesThing != null){
				Thing thing = World.getInstance().getThing(propertiesThing);
				if(thing != null){
					return (Properties) thing.doAction("run"	, actionContext);
				}
			}
		}
		
		return null;
	}
	
	public static String getPropertyName(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getStringBlankAsNull("propertyName");
	}
}
