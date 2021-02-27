package xworker.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class MysqlActions {
	private static final String TAG = MysqlActions.class.getName();
	
	public static void createPartitionByDate(ActionContext actionContext) throws ParseException, SQLException {
		Thing self = actionContext.getObject("self");
		Connection con = self.doAction("getConnection", actionContext);
		
		if(con == null) {
			Executor.warn(TAG, "Can not create partition, connection is null, thing={}", self.getMetadata().getPath());
			return;
		}
		
		String tableName = self.doAction("getTableName", actionContext);
		if(tableName == null || "".equals(tableName)) {
			Executor.warn(TAG, "Can not create partition, talbe is null, thing={}", self.getMetadata().getPath());
			return;
		}
		
		String columnName = self.doAction("getDateColumn", actionContext);
		
		Date date = new Date();
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");		
		
		String startDate = self.doAction("getStartDate", actionContext);
		if(startDate != null) {
			date = sf1.parse(startDate);
		}
		Integer count = self.doAction("getCount", actionContext);
		if(count == null) {
			count = 30;
		}				
		for(int i=0; i<count; i++) {
			createPartitionByDate(con, tableName, columnName, date);
			
			date = new Date(date.getTime() + 24 * 3600 * 1000);
		}
	}
	
	private static void createPartitionByDate(Connection con, String tableName, String columnName , Date date) throws SQLException {
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");	
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");
		
		String partition = "p" + sf2.format(date);
		String schema = con.getSchema();
		String sql = " SELECT REPLACE(partition_name,'p','')  "
				+ "FROM INFORMATION_SCHEMA.PARTITIONS " + 
				"     WHERE table_name=? and partition_name=?";
		Executor.debug(TAG, sql);
		PreparedStatement pst = con.prepareStatement(sql);
		ResultSet rs = null;
		try {
			pst.setString(1, tableName);
			pst.setString(2, partition);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				Executor.debug(TAG, "Partition exists, do not create, schema={}, table={}, column={}, partition={}", schema, tableName, columnName, partition);
				return;
			}else {
				rs.close();
				pst.close();
								
				Executor.debug(TAG, "Partition not exists, create it, schema={}, table={}, column={}, partition={}", schema, tableName, columnName, partition);
				
				sql = "ALTER TABLE " + tableName + " ADD PARTITION "
						+ "(PARTITION p" + sf2.format(date) + " VALUES LESS THAN (TO_DAYS('" + sf1.format(date) + "')))";
				Executor.debug(TAG, sql);
				pst = con.prepareStatement(sql);
				 
				pst.execute();
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
