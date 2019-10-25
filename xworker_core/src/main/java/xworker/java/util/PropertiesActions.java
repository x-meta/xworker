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
		String propertiesFile1 = self.getStringBlankAsNull("propertiesFile1");
		String propertiesFile2 = self.getStringBlankAsNull("propertiesFile2");
		String propertiesFile3 = self.getStringBlankAsNull("propertiesFile3");
		
		Properties p = new Properties();
		boolean loadAll = "all".equals(strategy);
		if(!loadAll && loadProperties(p, propertiesFile1)){
			return p;
		}
		if(!loadAll && loadProperties(p, propertiesFile2)){
			return p;
		}
		if(!loadAll && loadProperties(p, propertiesFile3)){
			return p;
		}
		
		return p;
	}
	
	private static boolean loadProperties(Properties p, String fileName){
		if(fileName == null || "".equals(fileName)){
			return false;
		}
		
		File file = new File(fileName);
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
