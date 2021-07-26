package xworker.db.ddlutils;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ddlutils.DdlUtilsException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.platform.sqlite.SqLitePlatform;
import org.xmeta.*;

import org.xml.sax.SAXException;
import xworker.xml.XmlUtils;

public class DbUtilsActions {
	static{
		init();
	}
	private static void init(){
		PlatformFactory.registerPlatform(SqLitePlatform.DATABASENAME, SqLitePlatform.class);
	}

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
		
		Platform platform = getPlatform(self, dataSource);
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

		Platform platform = getPlatform(self, dataSource);
		platform.alterTables(database, self.getBoolean("continueOnError"));
	}

	public static void drop(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataSource dataSource = (DataSource) self.doAction("getDataSource", actionContext);
		if(dataSource == null){
			throw new ActionException("jdbc dataSource is null, path=" + self.getMetadata().getPath());
		}
		Database database = (Database) self.doAction("getDatabase", actionContext);
		if(database == null){
			throw new ActionException("database is null, path=" + self.getMetadata().getPath());
		}

		Platform platform = getPlatform(self, dataSource);
		platform.dropTables(database, self.getBoolean("continueOnError"));
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

	public static Database getSelfDatabase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String xml = XmlUtils.encodeToXml(self);
		xml = "<!DOCTYPE database SYSTEM \"http://db.apache.org/torque/dtd/database.dtd\">\r\n" + xml;
		//System.out.println(xml);
		return new DatabaseIO().read(new InputStreamReader(new ByteArrayInputStream(xml.getBytes())));
	}

	public static Platform getPlatform(Thing self, DataSource dataSource){
		try {
			return PlatformFactory.createNewPlatformInstance(dataSource);
		}catch(Exception e){
			Connection con = null;
			try {
				con = dataSource.getConnection();
				DatabaseMetaData metaData = con.getMetaData();
				String jdbcDriver = metaData.getDriverName();
				String url = metaData.getURL();
				if(SqLitePlatform.JDBC_DRIVER.equals(jdbcDriver) || url.startsWith("jdbc:sqlite:")){
					//是sqlite
					Platform pl =  PlatformFactory.createNewPlatformInstance(SqLitePlatform.DATABASENAME);
					pl.setDataSource(dataSource);
					return pl;
				}else{
					throw e;
				}
			}catch(Exception ee){
				throw new ActionException("Create database platform error, path=" + self.getMetadata().getPath(), ee);
			}finally {
				if(con != null){
					try {
						con.close();
					}catch (Exception eee){
					}
				}
			}

		}
	}

	public static Platform getPlatform(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Thing dataSource = self.doAction("getDataSource", actionContext);
		if(dataSource == null){
			throw new ActionException("DataSource is null, thing=" + self.getMetadata().getPath());
		}

		DataSource dataSource1 = dataSource.doAction("getDataSource", actionContext);
		return getPlatform(self, dataSource1);
	}

	public static void alterTables(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		platform.alterTables(database, actionContext.getBoolean("continueOnError"));
	}

	public static void createTables(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		platform.createTables(database, actionContext.getBoolean("dropTablesFirst"), actionContext.getBoolean("continueOnError"));
	}

	public static void dropTables(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		platform.dropTables(database, actionContext.getBoolean("continueOnError"));
	}

	public static String getAlterTablesSql(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		return platform.getAlterTablesSql(database);
	}

	public static String getDropTablesSql(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		return platform.getDropTablesSql(database, false);
	}

	public static String getCreateTablesSql(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Database database = self.doAction("getDatabase", actionContext);
		Platform platform = self.doAction("getPlatform", actionContext);

		return platform.getCreateTablesSql(database, false, false);
	}

	public static void shutdownDatabase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Platform platform = self.doAction("getPlatform", actionContext);
		platform.shutdownDatabase();
	}

	public static String toDatabaseXML(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String xml = XmlUtils.encodeToXml(self);
		xml = "<!DOCTYPE database SYSTEM \"http://db.apache.org/torque/dtd/database.dtd\">\r\n" + xml;
		return xml;
	}

	public static Database readModelFromDatabase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Platform platform = self.doAction("getPlatform", actionContext);
		return platform.readModelFromDatabase(null);
	}

	public static String readModelXMLFromDatabase(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		Platform platform = self.doAction("getPlatform", actionContext);
		Database database = platform.readModelFromDatabase(null);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bout);
		new DatabaseIO().write(database, writer);

		try {
			writer.flush();
			return bout.toString("utf-8");
		}catch(Exception e){
			return null;
		}
	}

	public static Thing readModelToThing(ActionContext actionContext) throws ParserConfigurationException, IOException, SAXException {
		Thing self = actionContext.getObject("self");

		String xml = self.doAction("readModelXMLFromDatabase", actionContext);
		//删除dtd的引用，有时候没网络大避开
		int index1 = xml.indexOf("<!DOCTYPE");
		int index2 = xml.indexOf("<database", index1);
		xml = xml.substring(0, index1) + "\n" + xml.substring(index2, xml.length());
		Thing thing = new Thing("xworker.db.ddlutils.database");
		thing.parseXML(xml);

		return thing;
	}
}
