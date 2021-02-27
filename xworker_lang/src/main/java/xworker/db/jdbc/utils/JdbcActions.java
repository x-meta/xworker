package xworker.db.jdbc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;

public class JdbcActions {
	private static final String TAG = JdbcActions.class.getName();
	
	public static void deleteTableRowsInc(ActionContext actionContext) throws SQLException{
        //xworker.db.jdbc.utils.JdbcActions/@DeleteTableRowsInc/@actions/@run
		Thing self = actionContext.getObject("self");
        Thing datasource = self.doAction("getDatasource", actionContext);
        long currentId = self.getLong("currentId");
        long maxId = self.getLong("maxId");
        String table = self.getString("tableName");
        String idFiled = self.getString("idFiled");
        while(currentId < maxId){
        	Connection con = datasource.doAction("getConnection", actionContext);
            try{
                PreparedStatement pst = con.prepareStatement("delete from " + table + "  where " + idFiled + " < ?");
                pst.setLong(1, currentId);
                pst.execute();
                Executor.info(TAG, table + " " + idFiled + " < " + currentId + " has deleted");
                pst.close();
                
                self.set("currentId", currentId);
                self.save();
                            
                currentId = currentId + 10000;
            }catch(Exception e){
                Executor.error(TAG, "execute delete table error, action=" + self.getMetadata().getPath(), e);
            }finally{
            	if(con != null) {
            		con.close();
            	}
            }
        }
    }
    
    public static void initDatabase(ActionContext actionContext){
        //xworker.db.jdbc.utils.JdbcActions/@InitDatabase/@actions/@run
    	Thing self = actionContext.getObject("self");
    	
        //检查数据源是否存在
        String dataSource = self.doAction("getDataSource", actionContext);
        World world = World.getInstance();
        if(world.getThing(dataSource) == null){
            //创建dataSource
             Thing dataSourceThing = self.doAction("getDataSourceThing", actionContext);
             String thingManager = self.doAction("getThingManager", actionContext);
             
             Thing ds = dataSourceThing.detach();
             ds.getMetadata().setCoderType("dml_xml");//为了方便手工编辑
             ds.saveAs(thingManager, dataSource);
        }
        
        //ddl
        Thing ddl = self.doAction("getDdlThing", actionContext);
        if(ddl != null && UtilData.isTrue(self.doAction("isDbChanged", actionContext))){
            ddl.doAction("run", actionContext);
        }
    }
    
    public static boolean isDbChanged(ActionContext actionContext) throws SQLException{
        //xworker.db.jdbc.utils.JdbcActions/@InitDatabase/@actions/@isDbChanged
        //检查数据库是否已经更新
    	Thing self = actionContext.getObject("self");
        String dataSource = self.doAction("getDataSource", actionContext);
        String sql = self.doAction("getCheckSql", actionContext);
        World world = World.getInstance();
        Thing ds = world.getThing(dataSource);
        Connection con = ds.doAction("getConnection", actionContext);
        try{
        	
        	PreparedStatement pst = con.prepareStatement(sql);
            pst.execute();
            pst.close();
             
            return false;
        }catch(Exception e){
            return true;
        }finally{
        	if(con != null) {
        		con.close();
        	}
        }
    }
}
