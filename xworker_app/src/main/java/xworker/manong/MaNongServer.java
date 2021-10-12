package xworker.manong;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class MaNongServer {
	public static Thing registUser(String userName, String password, String nickName, String email, Connection con) throws SQLException, NoSuchAlgorithmException{
		Thing res = new Thing("xworker.manong.web.ResponseThing");
		if(userName == null || password == null || nickName == null ||
				"".equals(userName) || "".equals(password) || "".equals(nickName)){
			res.set("status", "2");
			res.set("message", "用户名、密码和昵称不能为空！");
			return res;
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement("select name, password, nickname, randomKey, email from tbluser where name=?");
			pst.setString(1, userName);
			rs = pst.executeQuery();
			if(rs.next()){
				//检查密码是否正确
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				
				if(!getMd5(password, randomKey).equals(dbPassword)){
					res.set("status", "3");
					res.set("message", "用户名密码错误！");
					return res;
				}
				
				//更新用户信息
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("update tbluser set nickname=?,email=? where name=?");
				pst.setString(1, nickName);
				pst.setString(2, email);
				pst.setString(3, userName);
				pst.executeUpdate();
				
				res.set("status", "1");
				res.set("message", "用户信息更新成功！");
				return res;
			}else{
				//插入注册信息
				rs.close();
				pst.close();
				
				Random r = new Random();
				String randomKey = "" + (1000 + r.nextInt(10000000));
				pst = con.prepareStatement("insert into tbluser(name, password, nickname, randomKey, email, createDate)" +
						" values(?, ? ,?, ?, ?, ?)");
				pst.setString(1, userName);
				pst.setString(2, getMd5(password, randomKey));
				pst.setString(3, nickName);
				pst.setString(4, randomKey);
				pst.setString(5, email);
				pst.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
				pst.execute();
				
				res.set("status", "1");
				res.set("message", "新用户注册成功！");
				return res;
			}
		}finally{
			if(rs != null){
				rs.close();
			}
			
			if(pst != null){
				pst.close();
			}
		}
	}
	
	/**
	 * 用户注册。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static Thing registUser(ActionContext actionContext) throws SQLException, NoSuchAlgorithmException{
		Connection con = (Connection) actionContext.get("con");
		Thing user = (Thing) actionContext.get("thing");
		String userName = user.getStringBlankAsNull("userName");
		String password = user.getStringBlankAsNull("password");
		String nickName = user.getStringBlankAsNull("nickName");
		String email = user.getStringBlankAsNull("email");
		
		return registUser(userName, password, nickName, email, con);
	}
	
	/**
	 * 用户登录，如果成功返回用户信息，用户不存在或用户名密码错误返回null。
	 * 
	 * 返回的用户信息里有name, nickname, email。
	 * 
	 * @param name
	 * @param password
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 */
	public static Map<String, Object> login(String name, String password, Connection con) throws SQLException, NoSuchAlgorithmException{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement("select tid, name, password, nickname, randomKey, email,role from tbluser where loginName=?");
			pst.setString(1, name);
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
				user.put("nickname", rs.getString("nickname"));
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
		}
	}
	
	/**
	 * 用户修改密码。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static Thing changePassword(ActionContext actionContext) throws SQLException, NoSuchAlgorithmException{
		Connection con = (Connection) actionContext.get("con");
		Thing user = (Thing) actionContext.get("thing");
		String userName = user.getStringBlankAsNull("userName");
		String password = user.getStringBlankAsNull("password");
		String newPassword = user.getStringBlankAsNull("newPassword");
		
		Thing res = new Thing("xworker.manong.web.ResponseThing");
		if(userName == null || password == null || newPassword == null){
			res.set("status", "2");
			res.set("message", "用户名、密码和新密码不能为空！");
			return res;
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement("select name, password, nickname, randomKey, email from tbluser where name=?");
			pst.setString(1, userName);
			rs = pst.executeQuery();
			if(rs.next()){
				//检查密码是否正确
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				
				if(!getMd5(password, randomKey).equals(dbPassword)){
					res.set("status", "3");
					res.set("message", "用户名密码错误！");
					return res;
				}
				
				//更新用户信息
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("update tbluser set password=? where name=?");
				pst.setString(1, getMd5(newPassword, randomKey));
				pst.setString(2, userName);
				pst.executeUpdate();
				
				res.set("status", "1");
				res.set("message", "新密码更新成功！");
				return res;
			}else{				
				res.set("status", "1");
				res.set("message", "用户不存在，无法更新密码！");
				return res;
			}
		}finally{
			if(rs != null){
				rs.close();
			}
			
			if(pst != null){
				pst.close();
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
	
	/**
	 * 更新项目的下载次数。
	 * 
	 * @param actionContext
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void download(ActionContext actionContext) throws SQLException, IOException{
		Connection con = (Connection) actionContext.get("con");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		String projectId = request.getParameter("projectId");
		
		PreparedStatement pst = null;
		try{
			pst = con.prepareStatement("update tblmanongproject set downCount=downCount+1 where projectId=?");
			pst.setString(1, projectId);
			pst.executeUpdate();
		}finally{
			if(pst != null){
				pst.close();
			}
		}
		
		response.sendRedirect("http://www.xworker.org/" + projectId.replace('.', '/') + ".zip");
	}
	
	/**
	 * 查看项目。
	 * 
	 * @param actionContext
	 * @throws SQLException
	 */
	public static String viewProject(ActionContext actionContext) throws SQLException{
		Connection con = (Connection) actionContext.get("con");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		String projectId = request.getParameter("projectId");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement("update tblmanongproject set viewCount=viewCount+1 where projectId=?");
			pst.setString(1, projectId);
			pst.executeUpdate();
			pst.close();
					
			pst = con.prepareStatement("select * from tblmanongproject where projectId=?");
			pst.setString(1, projectId);
			rs = pst.executeQuery();
			if(rs.next()){
				MaNongProjectInfo info = new MaNongProjectInfo();
				info.createDate = rs.getTimestamp("createDate");
				info.description = rs.getString("description");
				info.downCount = rs.getInt("downCount");
				info.fileZip = rs.getInt("fileZip");
				info.label = rs.getString("label");
				info.majorVersion = rs.getInt("majorVersion");
				info.minorVersion = rs.getInt("minorVersion");
				info.name = rs.getString("name");
				info.projectId = rs.getString("projectId");
				info.summary = rs.getString("summary");
				info.updateDate = rs.getTimestamp("updateDate");
				info.viewCount = rs.getInt("viewCount");
				info.userName = rs.getString("userName");
				info.nickName = rs.getString("nickName");
				actionContext.getScope(0).put("project", info);
			}
		}finally{
			if(rs != null){
				rs.close();
			}
			
			if(pst != null){
				pst.close();
			}
		}
		
		return "success";
	}
	
	/**
	 * WEB搜索项目的动作。
	 * 
	 * @param actionContext
	 * @return
	 * @throws SQLException 
	 */
	public static String searchProjects(ActionContext actionContext) throws SQLException{
		Connection con = (Connection) actionContext.get("con");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		String keys = request.getParameter("keys");

		String pageStr = request.getParameter("page");
		int page = 0;
		try{
			page = Integer.parseInt(pageStr);
		}catch(Exception e){			
		}
		
		MaNongProjectPageInfo pageInfo = searchProjects(con, keys, page, 10);
		actionContext.getScope(0).put("pageInfo", pageInfo);
		actionContext.getScope(0).put("keys", keys);
		
		return "success";
	}
	
	/**
	 * 根据关键字超找项目。
	 * 
	 * @param con
	 * @param page
	 * @throws SQLException 
	 */
	public static MaNongProjectPageInfo searchProjects(Connection con, String keys, int page, int pageSize) throws SQLException{
		if(keys == null){
			keys = "";
		}
		keys = keys.trim();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//查询总数
			String sql = "SELECT count(*) cnt FROM tblmanongproject";
			if(!"".equals(keys)){
				int keyCount = 0;
				for(String key : keys.split("[ ]")){ //根据空格区分
					key = key.trim();
					if(keyCount > 0){
						sql = sql + " and ";
					}else{
						sql = sql + " where ";
					}
					
					keyCount++;
					sql = sql +  "projectId in (select distinct projectId from tblmanongprojectkeys where keyWord=?)";
				}
				pst = con.prepareStatement(sql);
				int index = 1;
				for(String key : keys.split("[ ]")){
					pst.setString(index, key.trim().toLowerCase());
					index++;
				}
			}else{
				pst = con.prepareStatement(sql);
			}
			rs = pst.executeQuery();
			rs.next();
			int totalCount = rs.getInt("cnt");
			
			//查询具体记录数
			sql = "SELECT * FROM tblmanongproject";
			if(!"".equals(keys)){
				int keyCount = 0;
				for(String key : keys.split("[ ]")){ //根据空格区分
					key = key.trim();
					if(keyCount > 0){
						sql = sql + " and ";
					}else{
						sql = sql + " where ";
					}
					
					keyCount++;
					sql = sql +  "projectId in (select distinct projectId from tblmanongprojectkeys where keyWord=?)";
				}
				
				rs.close();
				pst.close();
				
				sql = sql + " order by downCount desc, viewCount desc limit ?,?";
				pst = con.prepareStatement(sql);
				int index = 1;
				for(String key : keys.split("[ ]")){
					pst.setString(index, key.trim().toLowerCase());
					index++;
				}
				pst.setInt(index, page * pageSize);
				pst.setInt(index + 1, pageSize);
			}else{
				rs.close();
				pst.close();
				
				sql = sql + " order by downCount desc, viewCount desc limit ?,?";
				pst = con.prepareStatement(sql);
				pst.setInt(1, page * pageSize);
				pst.setInt(2, pageSize);
			}
			List<MaNongProjectInfo> datas = new ArrayList<MaNongProjectInfo>();
			rs = pst.executeQuery();
			while(rs.next()){
				MaNongProjectInfo info = new MaNongProjectInfo();
				info.createDate = rs.getTimestamp("createDate");
				info.description = rs.getString("description");
				info.downCount = rs.getInt("downCount");
				info.fileZip = rs.getInt("fileZip");
				info.label = rs.getString("label");
				info.majorVersion = rs.getInt("majorVersion");
				info.minorVersion = rs.getInt("minorVersion");
				info.name = rs.getString("name");
				info.projectId = rs.getString("projectId");
				info.summary = rs.getString("summary");
				info.updateDate = rs.getTimestamp("updateDate");
				info.viewCount = rs.getInt("viewCount");
				info.userName = rs.getString("userName");
				info.nickName = rs.getString("nickName");
				info.rootProjectId = rs.getString("rootProjectId");
				datas.add(info);
			}
			
			MaNongProjectPageInfo pageInfo = new MaNongProjectPageInfo();
			pageInfo.datas = datas;
			pageInfo.page = page;
			pageInfo.pageSize = pageSize;
			pageInfo.totalCount = totalCount;
			pageInfo.keys = keys;
			
			return pageInfo;
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 删除项目。
	 * 
	 * @param actionContext
	 */
	public static void delete(ActionContext actionContext){
		HttpServlet servlet = (HttpServlet) actionContext.get("servlet");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String projectId = request.getParameter("projectId");

		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//用户名必须和目录符合
			String userPrjPath = "_share." + userName + ".";
			if(!projectId.startsWith(userPrjPath)){
				sendResponse(response, 2, "当前用户只能上传" + userPrjPath + "*的码农项目！");
				return;
			}
			
			//检查密码是否正确
			pst = con.prepareStatement("select name, password, nickname, randomKey, email from tbluser where name=?");
			pst.setString(1, userName);
			rs = pst.executeQuery();
			if(rs.next()){				
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				
				if(!MaNongServer.getMd5(password, randomKey).equals(dbPassword)){
					sendResponse(response, 2, "用户名密码错误！");
					return;
				}
			}
			rs.close();
			pst.close();
			
			//保存项目文件
			File outFile = getFilePath(servlet, projectId);
			if(outFile.exists()){
				outFile.delete();
			}
			
			//删除项目信息
			pst = con.prepareStatement("delete from tblmaNongproject where projectId=?");
			pst.setString(1, projectId);
			pst.execute();
			pst.close();
			
			//删除关键字
			pst = con.prepareStatement("delete from tblmanongprojectKeys where projectId=?");
			pst.setString(1, projectId);
			pst.execute();
			pst.close();		
			
			sendResponse(response, 1, "项目" + projectId + "已从服务器删除。");
		}catch(Exception e){
			try {
				sendResponse(response, 2, "码农项目上传文件错误： " + e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 向客户端发送回复。
	 * 
	 * @param response
	 * @param status
	 * @param message
	 * @throws IOException 
	 */
	public static void sendResponse(HttpServletResponse response, int status, String message) throws IOException{
		String res = status + "|" + message;
		response.setContentType("text/plain; charset=utf-8");
		response.getOutputStream().write(res.getBytes("utf-8"));
	}
	
	/**
	 * 根据项目标识获取要保存的项目文件。
	 * 
	 * @param projectId
	 * @return
	 */
	public static File getFilePath(HttpServlet servlet, String projectId){
		String filePath = servlet.getServletContext().getRealPath("/") + "/" + projectId.replace('.', '/') + ".zip";
		return new File(filePath);
	}
}
