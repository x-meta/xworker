package xworker.swt.xworker.codeassist.objectassists;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xworker.lang.executor.Executor;
import xworker.swt.xworker.CodeAssitContent;

public class SqlAssistorCache {
private static final String TAG = SqlAssistorCache.class.getName();
	
	List<CodeAssitContent> variables = new ArrayList<CodeAssitContent>();
	
	long initTime = 0;
	
	public void init(Connection con) {
		//10秒刷新一次
		if(System.currentTimeMillis() - initTime < 10000) {
			return;
		}
		
		initTime = System.currentTimeMillis();
		try {
			if(con == null || con.isClosed()) {
				return;
			}
			
			variables.clear();
			
			Map<String, CodeAssitContent> context = new HashMap<String, CodeAssitContent>();
			
			List<String> tables = new ArrayList<String>();
			DatabaseMetaData metadata = con.getMetaData();
			ResultSet trs = metadata.getTables(null, metadata.getUserName(), null, new String[] {"TABLE"});
			while(trs.next()) {
				tables.add(trs.getString(3));
			}
			trs.close();
			//System.out.println(metadata.getCatalogTerm());
			ResultSet rs = metadata.getColumns(null, metadata.getUserName(), null, null);
			try {
				while(rs.next()) {
					String tableCat = rs.getString(1);
					String tableSchema = rs.getString(2);
					String tableName = rs.getString(3);
					String columnName = rs.getString(4);
					
					boolean have = false;
					for(String table : tables) {
						if(table.equals(tableName)) {
							have = true;
							break;
						}
					}
					if(!have) {
						continue;
					}
					add(tableCat, context);					
					add(tableSchema, context);
					add(tableName, context);
					add(columnName, context);
				}
			}finally {
				rs.close();
			}
		}catch(Exception e) {
			Executor.info(TAG, "Init connection variables error", e);
		}
	}
	
	private void add(String name, Map<String, CodeAssitContent> context) {
		if(name == null) {
			return;
		}
		name = name.trim();
		if("".equals(name)) {
			return;
		}
		if(context.get(name) != null) {
			return;
		}
		
		CodeAssitContent var = new CodeAssitContent(name, name , null);
		variables.add(var);
		context.put(name, var);
	}

	public List<CodeAssitContent> getVariables() {
		return variables;
	}
}
