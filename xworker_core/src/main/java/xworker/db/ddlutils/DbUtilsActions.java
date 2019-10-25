package xworker.db.ddlutils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.sql.DataSource;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.xml.XmlUtils;

public class DbUtilsActions {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataSource dataSource = (DataSource) self.doAction("getDataSource", actionContext);
		if(dataSource == null){
			throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
		}
		Database database = (Database) self.doAction("getDatabase", actionContext);
		if(database == null){
			throw new ActionException("database is null, path=" + self.getMetadata().getPath());
		}
		
		Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);
		platform.createTables(database, self.getBoolean("dropTablesFirst"), self.getBoolean("continueOnError"));
	}
	
	public static void alter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataSource dataSource = (DataSource) self.doAction("getDataSource", actionContext);
		if(dataSource == null){
			throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
		}
		Database database = (Database) self.doAction("getDatabase", actionContext);
		if(database == null){
			throw new ActionException("database is null, path=" + self.getMetadata().getPath());
		}
		
		Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);
		platform.alterTables(database, self.getBoolean("continueOnError"));
	}
	
	public static Object getDataSource(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String dataSource = self.getStringBlankAsNull("dataSource");
		if(dataSource != null){
			Thing ds = World.getInstance().getThing(dataSource);
			if(ds != null){
				return ds.doAction("getDataSource", actionContext);
			}
		}
		
		return null;
	}
	
	public static Object getDatabase(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String database = self.getStringBlankAsNull("database");
		if(database != null){
			Thing databaseThing = World.getInstance().getThing(database);
			if(databaseThing != null){
				String xml = XmlUtils.encodeToXml(databaseThing);
				xml = "<!DOCTYPE database SYSTEM \"http://db.apache.org/torque/dtd/database.dtd\">\r\n" + xml;
				return new DatabaseIO().read(new InputStreamReader(new ByteArrayInputStream(xml.getBytes())));
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
}
