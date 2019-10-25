package xworker.db.jdbc.functions;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilJava;

import xworker.db.DbUtil;

public class JDBCFunctions {
	private final static byte TYPE_LIST = 0;
	private final static byte TYPE_MAP = 1;
	private final static byte TYPE_VALUE = 2;
	
	public static Object query(ActionContext actionContext) throws SQLException{
		return query(actionContext, TYPE_LIST);
	}
	
	public static Object queryMap(ActionContext actionContext) throws SQLException{
		return query(actionContext, TYPE_MAP);
	}
	
	public static Object queryValue(ActionContext actionContext) throws SQLException{
		return query(actionContext, TYPE_VALUE);
	}
	
	@SuppressWarnings("resource")
	private static Object query(ActionContext actionContext, byte type) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		
		//数据源
		Thing dataSourceThing = (Thing) actionContext.get("dataSourceThing");
		if(dataSourceThing == null){
			throw new ActionException("DataSourceThing is null, path=" + self.getMetadata().getPath());
		}
		
		//sql
		String sql = (String) actionContext.get("sql");
		
		//参数
		Iterable<Object> params = UtilJava.getIterable(actionContext.get("params"));
		Iterator<Object> iterator = params.iterator();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			con = (Connection) dataSourceThing.doAction("getConnection", actionContext);
			pst = con.prepareStatement(sql);
			setParameter(pst, iterator);
			rs = pst.executeQuery();
			List<Map<Object, Object>> datas = type == TYPE_LIST ? new ArrayList<Map<Object, Object>>() : null;
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			int count = 0; 
			while(rs.next()){
				Map<Object, Object> data = new HashMap<Object, Object>();
				for(int i=1; i<columnCount; i++){
					Object value = DbUtil.getValue(rs, i, metadata.getColumnType(i));
					String name = metadata.getColumnName(i);
					
					if(type == TYPE_VALUE){
						return value;
					}
					data.put(name, value);
					data.put(i, value);
				}
				
				if(type == TYPE_MAP){
					return data;
				}
				
				datas.add(data);
				
				count++;
				if(count >= 50000){
					break;
				}
			}
			
			if(type == TYPE_VALUE || type == TYPE_MAP){
				return null;
			}
			
			return datas;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}
	
	public static Object update(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		
		//数据源
		Thing dataSourceThing = (Thing) actionContext.get("dataSourceThing");
		if(dataSourceThing == null){
			throw new ActionException("DataSourceThing is null, path=" + self.getMetadata().getPath());
		}
		
		//sql
		String sql = (String) actionContext.get("sql");
		
		//参数
		Iterable<Object> params = UtilJava.getIterable(actionContext.get("params"));
		Iterator<Object> iterator = params.iterator();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			con = (Connection) dataSourceThing.doAction("getConnection", actionContext);
			pst = con.prepareStatement(sql);
			setParameter(pst, iterator);

			return pst.executeUpdate();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}
	
	private static void setParameter(PreparedStatement pst, Iterator<Object> iterator) throws SQLException{
		int index = 1;
		while(iterator.hasNext()){
			Object param = iterator.next();
			if(param instanceof String){
				pst.setString(index, (String) param);
			}else if(param instanceof Integer){
				pst.setInt(index, (Integer) param);
			}else if(param instanceof Long){
				pst.setLong(index, (Long) param);
			}else if(param instanceof Float){
				pst.setFloat(index, (Float) param);
			}else if(param instanceof Double){
				pst.setDouble(index, (Double) param);
			}else if(param instanceof Byte){
				pst.setByte(index, (Byte) param);
			}else if(param instanceof Integer){
				pst.setShort(index, (Short) param);
			}else if(param instanceof Boolean){
				pst.setBoolean(index, (Boolean) param);
			}else if(param instanceof Integer){
				pst.setInt(index, (Integer) param);
			}else if(param instanceof java.math.BigDecimal){
				pst.setBigDecimal(index, ((BigDecimal) param));
			}else if(param instanceof java.math.BigInteger){
				pst.setBigDecimal(index, new BigDecimal((BigInteger) param));
			}else if(param instanceof InputStream){
				pst.setBlob(index, (InputStream) param);
			}else if(param == null){
				pst.setString(index, null);
			}else{
				//默认都是string
				pst.setString(index, param.toString());
			}
			
			index++;
		}
	}
}
