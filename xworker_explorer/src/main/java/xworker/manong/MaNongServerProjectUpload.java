package xworker.manong;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.codes.TxtCoder;

import xworker.dataObject.utils.DbUtil;
import xworker.http.MultiPartRequest;
import xworker.http.fileupload.FileuploadAction;

/**
 * 上传文件的服务器处理。
 * 
 * @author Administrator
 *
 */
public class MaNongServerProjectUpload implements FileuploadAction{
	private static Logger logger = LoggerFactory.getLogger(MaNongServerProjectUpload.class);
	
	/**
	 * 向客户端发送回复。
	 * 
	 * @param response
	 * @param status
	 * @param message
	 * @throws IOException 
	 */
	public void sendResponse(HttpServletResponse response, int status, String message) throws IOException{
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
	
	@Override
	public String doService(MultiPartRequest mrequest,
			HttpServletRequest request, HttpServletResponse response,
			ActionContext actionContext) throws ServletException {
		
		HttpServlet servlet = (HttpServlet) actionContext.get("servlet");
		
		FileItem fileItem = mrequest.getFileItem("file");
		String userName = mrequest.getParameter("userName");
		String password = mrequest.getParameter("password");
		//String name = mrequest.getParameter("name");
		//String label = mrequest.getParameter("label");
		String projectId = mrequest.getParameter("projectId");
		String majorVersion = mrequest.getParameter("majorVersion");
		String minorVersion = mrequest.getParameter("minorVersion");
		String summary = mrequest.getParameter("summary").trim();		
		summary = summary.replaceAll("<[^>]+>", "");
		//String description = mrequest.getParameter("description");
		//String keyWords = mrequest.getParameter("keyWords");
		FileItem projectItem = mrequest.getFileItem("project");
		Thing project = new Thing();
		try {
			TxtCoder.decode(project, new ByteArrayInputStream(projectItem.get()), true, 0);
			
			project.getMetadata().setPath(projectId);
			project.initChildPath();
		} catch (IOException e2) {
			throw new ServletException("decode project thing error", e2);
		}
		
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nickName = null;
		try{
			//用户名必须和目录符合
			String userPrjPath = "_share." + userName + ".";
			if(!projectId.startsWith(userPrjPath)){
				sendResponse(response, 2, "当前用户只能上传" + userPrjPath + "*的码农项目！");
				return "success";
			}
			
			//检查密码是否正确
			pst = con.prepareStatement("select name, password, nickname, randomKey, email from tbluser where name=?");
			pst.setString(1, userName);
			rs = pst.executeQuery();
			if(rs.next()){				
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				nickName = rs.getString("nickname");
				
				if(!MaNongServer.getMd5(password, randomKey).equals(dbPassword)){
					sendResponse(response, 2, "用户名密码错误！");
					return "success";
				}
			}
			rs.close();
			pst.close();
			
			//保存项目文件
			File outFile = getFilePath(servlet, projectId);
			if(!outFile.getParentFile().exists()){
				outFile.getParentFile().mkdirs();
			}
			FileOutputStream fout = new FileOutputStream(outFile);
			fout.write(fileItem.get());
			fout.close();
			
			//插入项目信息
			insertPorjectInfo(con, (int) outFile.length(), nickName, userName, majorVersion, minorVersion, project);
			
			sendResponse(response, 1, "项目上传更新成功");
		}catch(MaNongException me){
			try {
				sendResponse(response, 2, me.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			logger.error("码农项目上传文件错误", e);
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
		
		return "success";
	}
	
	public static void insertPorjectInfo(Connection con , int fileLength, String nickName, String userName, String majorVersion, String minorVersion, Thing project) throws SQLException{
		//保存项目信息
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			String projectId = project.getMetadata().getPath();
			String name = project.getMetadata().getName();
			String label = project.getMetadata().getLabel();
			String keyWords = project.getString("keyWords");
			String summary = project.getString("summary");
			String description = project.getString("description");					
			
			pst = con.prepareStatement("select * from tblmanongproject where projectId=?");
			pst.setString(1, projectId);
			rs = pst.executeQuery();
			if(rs.next()){
				int dbMajorVersion = rs.getInt("majorVersion");
				int dbMinorVersion = rs.getInt("minorVersion");
				double dbVersion = Double.parseDouble(String.valueOf(dbMajorVersion) + "." + String.valueOf(dbMinorVersion));
				double version = Double.parseDouble(majorVersion + "." + minorVersion);
				if(dbVersion > version){
					throw new MaNongException("当前版本比服务器版本低！不能上传！");
				}
				if(dbVersion == version){
					throw new MaNongException("当前版本和服务器版本相同！不能上传！");
				}
				//项目已存在，更新
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("update tblmanongproject set name=?, label=?, majorVersion=?," +
						" minorVersion=?, summary=?, description=?, fileZip=?, updateDate=?,nickName=?,userName=?, rootProjectId=? where projectId=?");
				pst.setString(1, name);
				pst.setString(2, label);
				pst.setInt(3, Integer.parseInt(majorVersion));
				pst.setInt(4, Integer.parseInt(minorVersion));
				pst.setString(5, summary);
				pst.setString(6, description);
				pst.setInt(7, fileLength);
				pst.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
				pst.setString(9, nickName);
				pst.setString(10, userName);
				pst.setString(11, project.getRoot().getMetadata().getPath());
				pst.setString(12, projectId);
				
				pst.executeUpdate();
				pst.close();
				
				//更新关键字列表
				pst = con.prepareStatement("delete from tblmanongprojectkeys where projectId=?");
				pst.setString(1, projectId);
				pst.execute();
				pst.close();
				
				insertKeyWords(con, projectId, keyWords);
			}else{
				//新项目，插入
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into tblmanongproject (name, label, majorVersion, minorVersion,summary, description, " +
						"fileZip, updateDate, createDate, projectId, downCount, viewCount, userName, nickName, rootProjectId) values(?, ?, ?, ?, ?, ?, ?, ?, ?,?, 0, 0,?,?, ?)");
				pst.setString(1, name);
				pst.setString(2, label);
				pst.setInt(3, Integer.parseInt(majorVersion));
				pst.setInt(4, Integer.parseInt(minorVersion));
				pst.setString(5, summary);
				pst.setString(6, description);
				pst.setInt(7, fileLength);
				pst.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
				pst.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
				pst.setString(10, projectId);
				pst.setString(11, userName);
				pst.setString(12, nickName);
				pst.setString(13, project.getRoot().getMetadata().getPath());
				pst.execute();
				pst.close();
				
				insertKeyWords(con, projectId, keyWords);
			}
			
			//插入子项目
			for(Thing subProject : project.getChilds("SubProject")){
				insertPorjectInfo(con, fileLength, nickName, userName, majorVersion, minorVersion, subProject);
			}
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
		}
	}

	public static void insertKeyWords(Connection con, String projectId, String keys) throws SQLException{
		PreparedStatement pst = null;
		try{
			//用于过滤重复的key
			Map<String, String> context = new HashMap<String, String>();
			
			pst = con.prepareStatement("insert into tblmanongprojectkeys(keyWord, projectId) values(?, ?)");
			for(String key : keys.split("[,]")){
				if(context.get(key.toLowerCase()) != null){
					continue;
				}
				context.put(key.toLowerCase(), key);
				pst.setString(1, key.trim().toLowerCase());
				pst.setString(2, projectId);
				pst.addBatch();
			}
			
			//项目路劲也作为key
			if(context.get(projectId) != null){
				context.put(projectId, projectId);
				pst.setString(1, projectId.toLowerCase());
				pst.setString(2, projectId);
				pst.addBatch();
			}
			
			//项目路径的分解也作为key
			for(String key : projectId.split("[.]")){
				for(String kk : key.split("[/@]")){
					if(context.get(kk.toLowerCase()) != null){
						continue;
					}
					context.put(kk.toLowerCase(), key);
					
					pst.setString(1, kk.trim().toLowerCase());
					pst.setString(2, projectId);
					pst.addBatch();
				}
			}
			pst.executeBatch();
		}finally{
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
