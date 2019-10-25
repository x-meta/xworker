package xworker.app.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class UserActions {
	public static Map<String, Object> userLogin(ActionContext actionContext) throws SQLException, NoSuchAlgorithmException{
		Thing self = actionContext.getObject("self");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//获取数据源
			Thing dataSource = self.doAction("getDataSource", actionContext);
			con = dataSource.doAction("getConnection", actionContext);
			
			String loginName = self.doAction("getLoginName", actionContext);
			String password = self.doAction("getPassword", actionContext);
			
			pst = con.prepareStatement("select tid, name, password, loginName, randomKey, email,role from tbluser where loginName=?");
			pst.setString(1, loginName);
			rs = pst.executeQuery();
			if(rs.next()){
				//检查密码是否正确
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				
				if(!getMd5(password, randomKey).equals(dbPassword)){
					return null;
				}
				
				Map<String, Object> user = new HashMap<String, Object>();
				user.put("id", rs.getLong("tid"));
				user.put("name", rs.getString("name"));
				user.put("loginName", rs.getString("loginName"));
				user.put("email", rs.getString("email"));
				user.put("role", rs.getInt("role"));
				return user;
			}else{				
				return null;
			}
		}finally{			
			if(rs != null){
				rs.close();
			}
			
			if(pst != null){
				pst.close();
			}
			
			if(con != null) {
				con.close();
			}
		}
	}
	
	public static String getMd5(String password, String randomKey) throws NoSuchAlgorithmException{
		String md5 = getMd5(password);
		return getMd5(md5 + randomKey);
	}
	
	public static String getMd5(String source) throws NoSuchAlgorithmException{
		MessageDigest s = MessageDigest.getInstance("MD5");
		s.update(source.getBytes());		
		byte[] bytes = s.digest();
		return UtilString.toHexString(bytes);
	}
}
