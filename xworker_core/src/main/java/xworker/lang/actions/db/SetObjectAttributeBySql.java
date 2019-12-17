package xworker.lang.actions.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.util.DbUtils;
import xworker.util.JavaUtils;

public class SetObjectAttributeBySql {
	private static final String TAG =  SetObjectAttributeBySql.class.getName();
	public static Object run(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		
		Object object = self.doAction("getObject", actionContext);
		if(object == null) {
			Executor.info(TAG, "Object is null, not set attriutes, thing=" + self.getMetadata().getPath());
			return null;
		}
		
		Thing dataSource = self.doAction("getDataSource", actionContext);
		if(dataSource == null) {
			Executor.info(TAG, "DataSource is null, not set attriutes, thing=" + self.getMetadata().getPath());
			return object;
		}
	
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = dataSource.doAction("getConnection", actionContext);
			if(con != null) {
				executeSql(self, object, con, actionContext);
				
				for(Thing sqlThing : self.getChilds("Sql")) {
					executeSql(sqlThing, object, con, actionContext);
				}
			}else {
				Executor.info(TAG, "Connection is null, not set attriutes, thing=" + self.getMetadata().getPath());
			}
			
			
			return object;
		}finally {
			if(rs != null) {
				rs.close();
			}
			
			if(pst != null) {
				pst.close();
			}
			
			if(con != null) {
				con.close();
			}
		}		
	}
	
	public static void executeSql(Thing thing, Object object,  Connection con, ActionContext actionContext) throws SQLException {
		
		String sql = thing.doAction("getSql", actionContext);
		Object[] params = thing.doAction("getParams", actionContext);
		if(sql == null || "".equals(sql)) {
			Executor.info(TAG, "Sql is null, not set attributes thing=" +thing.getMetadata().getPath());
			return;
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql);
			if(params != null) {
				DbUtils.setParameters(pst, params);
			}
			rs = pst.executeQuery();
			if(rs.next()) {
				Map<String, Object> mapping = thing.doAction("getAttributeMapping", actionContext);
				Map<String, Object> values = DbUtils.getValues(rs);
				for(String name : values.keySet()) {
					Object value = values.get(name);
					Object mapName = mapping.get(name);
					if(mapName instanceof String) {
						JavaUtils.setAttribute(object, (String) mapName, value);
					}else{
						JavaUtils.setAttribute(object, name, value);
					}
				}
			}			
		}finally {
			if(rs != null) {
				rs.close();
			}
			
			if(pst != null) {
				pst.close();
			}
		}		
	}
}
