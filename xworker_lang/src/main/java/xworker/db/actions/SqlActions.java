package xworker.db.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.db.sql.SQLConnection;
import xworker.lang.executor.Executor;
import xworker.util.DbUtils;

public class SqlActions {
	private static final String TAG  = SqlActions.class.getName();
	
	public static void executeSqls(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		if(sqlcon == null) {
			Executor.warn(TAG, "Can not execute sqls, connection is null, thing={}", self.getMetadata().getPath());
			return;
		}
		
		try {
			String sqls = self.doAction("getSqls", actionContext);
			Boolean ignoreException = self.doAction("isIgnoreException", actionContext);
			
			Connection con = sqlcon.getConnection();
			PreparedStatement pst = null;
			String sql = null;
			String[] lines = sqls.split("[\n]");
			for(int i=0; i<lines.length; i++) {
				String line = lines[i];
				line = line.trim();
				if("".equals(line) || line.startsWith("//") || line.startsWith("#")) {
					continue;
				}
				
				if(sql == null) {
					sql = line;
				}else {
					sql = sql + "\n" + line;
				}
				
				boolean end = false;
				if(sql.endsWith(";")) {
					end = true;
					sql = sql.substring(0, sql.length() - 1);
				}
				if(end || i == lines.length - 1) {
					try {
						Executor.debug(TAG, sql);
						pst = con.prepareStatement(sql);
						pst.execute();
					}catch(SQLException e) {
						if(ignoreException) {
							Executor.warn(TAG, "Execute sql error", e);
						}else {
							throw e;
						}
					}finally {
						sql = null;
						if(pst != null) {
							pst.close();
						}
						pst = null;
					}
				}
				
			}
		}finally {
			if(sqlcon != null) {
				sqlcon.close();
			}
		}
	}
	
	public static boolean isPrimaryKeyExists(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		try {
			String tableName = self.doAction("getTableName", actionContext);
			List<String> keys = self.doAction("getKeys", actionContext);
			
			ResultSet rs = sqlcon.getConnection().getMetaData().getPrimaryKeys(null, null, tableName);
			List<String> ks = new ArrayList<String>();
			while(rs.next()) {
				String name = rs.getString(4);
				if(name != null) {
					ks.add(name.toLowerCase());
				}
			}
			rs.close();
			
			for(String key : keys) {
				key = key.toLowerCase().trim();
				boolean have = false;
				for(String k : ks) {
					if(k.equals(key)) {
						have = true;
						break;
					}
				}
				
				if(!have) {
					return false;
				}
			}
			
			return true;
		}finally {
			sqlcon.close();
		}
	}
	
	public static boolean createPrimaryKeysIfNotExists(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		try {
			String tableName = self.doAction("getTableName", actionContext);
			List<String> keys = self.doAction("getKeys", actionContext);
			
			if(isPrimaryKeyExists(actionContext)) {				
				Executor.debug(TAG, "Primary keys exsits, tableName={}, keys={}", tableName, keys);
				return false;
			}else {
				Connection con = sqlcon.getConnection();
				Executor.debug(TAG, "Drop primary keys, tableName={}", tableName, keys);
				PreparedStatement pst = con.prepareStatement("alter table " + tableName + " drop primary key");
				 
				try {
					pst.execute();
					
					String sql = "ALTER TABLE " + tableName + " Add CONSTRAINT PK_" + tableName + " PRIMARY KEY (";
					for(int i=0 ;i<keys.size(); i++) {
						sql = sql + keys.get(i);
						if(i < keys.size() - 1) {
							sql = sql + ",";
						}
					}
					sql = sql + ")";
					Executor.debug(TAG, sql);
					pst = con.prepareStatement(sql);
					pst.execute();
				}finally {
					if(pst != null) {
						pst.close();
					}
				}
				
				return true;
			}
		}finally {
			sqlcon.close();
		}
	}
	
	public static Object query(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		try {
			String sql = self.doAction("getSql", actionContext);
			Object[] params = self.doAction("getParams", actionContext);
			
			Connection con = sqlcon.getConnection();
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = null;
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			try {
				if(params != null) {
					DbUtils.setParameters(pst, params);					
				}
				
				rs = pst.executeQuery();
				ResultSetMetaData rm = rs.getMetaData();
				while(rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					for(int i = 0; i<rm.getColumnCount(); i++) {
						String name = rm.getColumnName(i + 1);
						data.put(name, rs.getObject(name));
					}
					
					self.doAction("handleRow", actionContext, "row", data);
					datas.add(data);
				}
								
			}finally {
				if(rs != null) {
					rs.close();
				}
				
				if(pst != null) {
					pst.close();
				}
			}
			
			return datas;
		}finally {
			sqlcon.close();
		}
	}
	
	public static Object firstRow(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		try {
			String sql = self.doAction("getSql", actionContext);
			Object[] params = self.doAction("getParams", actionContext);
			
			Connection con = sqlcon.getConnection();
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = null;
			try {
				if(params != null) {
					DbUtils.setParameters(pst, params);					
				}
				
				rs = pst.executeQuery();
				ResultSetMetaData rm = rs.getMetaData();
				while(rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					for(int i = 0; i<rm.getColumnCount(); i++) {
						String name = rm.getColumnName(i + 1);
						data.put(name, rs.getObject(name));
					}
					
					self.doAction("handleRow", actionContext, "row", data);
					
					return data;
				}
								
			}finally {
				if(rs != null) {
					rs.close();
				}
				
				if(pst != null) {
					pst.close();
				}
			}
			
			return null;
		}finally {
			sqlcon.close();
		}
	}
	
	public static Object getQueryValue(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		SQLConnection sqlcon = self.doAction("getConnection", actionContext);
		try {
			String sql = self.doAction("getSql", actionContext);
			Object[] params = self.doAction("getParams", actionContext);
			String columnName = self.doAction("getColumnName", actionContext);
			
			Connection con = sqlcon.getConnection();
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = null;
			try {
				if(params != null) {
					DbUtils.setParameters(pst, params);					
				}
				
				rs = pst.executeQuery();
				while(rs.next()) {
					if(columnName != null && !"".equals(columnName)) {
						return rs.getObject(columnName);
					}else {
						return rs.getObject(1);
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
			
			return null;
		}finally {
			sqlcon.close();
		}
	}
}
