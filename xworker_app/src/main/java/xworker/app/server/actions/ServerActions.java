package xworker.app.server.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DbUtil;

public class ServerActions {
	private static Logger logger = LoggerFactory.getLogger(ServerActions.class);
	
	/**
	 * 根据分组标识返回服务器列表。
	 * 
	 * @param groupIds
	 * @param actionContext
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> getServersByGroupId(String groupIds, ActionContext actionContext) throws NumberFormatException, SQLException{
		String gs[] = groupIds.split("[,]");
		String in = "";
		for(int i=0; i<gs.length; i++){
			if(i < gs.length - 1){
				in = in + "?,";
			}else{
				in = in + "?";
			}
		}
		
		String sql = "select * from tblServer where id in (select serverId from tblservergroupitems where serverGroupId in (" + in + "))";
		
		//获取数据库连接
		World world = World.getInstance();
		Thing server = world.getThing("xworker.app.server.dataobjects.Server");
		Thing dataSource = world.getThing(server.getString("dataSource"));
		Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			pst = con.prepareStatement(sql);
			for(int i=0; i<gs.length; i++){
				pst.setLong(i + 1, Long.parseLong(gs[i]));
			}
			
			rs = pst.executeQuery();
			
			return (List<DataObject>) server.doAction("loadFromResultSet", actionContext, UtilMap.toMap("resultSet", rs));
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
		
	}
	
	/**
	 * 获取服务器列表。
	 * 
	 * @param actionContext
	 * @return
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 */
	public static List<DataObject> getServers(ActionContext actionContext) throws NumberFormatException, SQLException{
		Thing self = (Thing) actionContext.get("self");
		
		String serverId = self.getStringBlankAsNull("serverId");
		String serverGroupId = self.getStringBlankAsNull("serverGroupId");
		
		DataObject obj = new DataObject("xworker.app.server.dataobjects.Server");
		List<DataObject> servers = null;
		if(serverId == null && serverGroupId == null){
			//默认取所有的服务器			
			servers = obj.query(UtilMap.toMap("system", "linux", "status", 1), actionContext);
		}else{
			//通过标识获取服务器列表
			if(serverId != null){
				servers = obj.query(UtilMap.toMap("id", serverId), actionContext);
			}
			
			//通过分组获取服务器列表
			if(serverGroupId != null){
				List<DataObject> ss = getServersByGroupId(serverGroupId, actionContext);
				
				if(servers != null){
					servers.addAll(ss);
				}else{
					servers = ss;
				}
			}			
		}		
		
		//过滤掉重复的服务器，或不符合条件的服务器
		if(servers == null){
			return null;
		}
		
		Map<Long, DataObject> context = new HashMap<Long, DataObject>();
		for(int i=0;i<servers.size(); i++){
			DataObject server = servers.get(i);
			Long id = server.getLong("id");
			if(context.get(id) != null){
				//重复的服务器，移除
				servers.remove(i);
				i--;
				continue;
			}else{
				context.put(id, server);
				if(server.getInt("status") == 1 && "linux".equals(server.getString("system")) &&
						server.getString("adminIP") != null && server.getString("adminUserName") != null){					
				}else{
					//不符合条件，移除
					servers.remove(i);
					i--;
				}
				
			}
		}
		return servers;
	}
	
	public static String getCommand(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getString("command");
	}
	
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//是否调试
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		try{
			//服务器列表
			List<DataObject> servers = (List<DataObject>) self.doAction("getServers", actionContext);
			if(servers == null || servers.size() == 0){
				logger.warn("No server need to run ssh, thing=" + self.getMetadata().getPath());
				return;
			}
			if(debug){
				logger.info("Server count: " + servers.size());
				for(DataObject server : servers){
					logger.info("    serverId=" + server.get("id") + ", serverName=" + server.get("name"));
				}
			}
			
			//命令
			String command = (String) self.doAction("getCommand", actionContext);
			if(command == null || "".equals(command)){
				logger.warn("No ssh command need to run, thing=" + self.getMetadata().getPath());
				return;
			}
			if(debug){
				logger.info("command= " + command);
			}
			
			Thing serverActions = World.getInstance().getThing("xworker.app.server.actions.ServerActions");
			boolean finshOnException = self.getBoolean("finshOnException");
			for(DataObject server : servers){
				try{
					if(debug){
						logger.info("before execute, serverId=" + server.get("id") + ", serverName=" + server.get("name"));
					}
					String result = (String) serverActions.doAction("executeSSH", actionContext, 
							UtilMap.toMap("server", server, "command", command));
					
					if(debug){
						logger.info("before onSuccess, serverId=" + server.get("id") + ", serverName=" + server.get("name") + ", result=" + result);
					}
					self.doAction("onSuccess", actionContext, UtilMap.toMap("server", server, "command", command, "result", result));
				}catch(Exception e){
					if(debug){
						logger.info("before onException, serverId=" + server.get("id") + ", serverName=" + server.get("name"));
						e.printStackTrace();
					}
					self.doAction("onException", actionContext, UtilMap.toMap("server", server, "command", command, "exception", e));
					
					if(finshOnException){
						break;
					}
				}
				
			}
		}finally{
			if(debug){
				logger.info("before onFinish");				
			}
			self.doAction("onFinish", actionContext);
		}
	}
	
	public static void onSuccess(ActionContext actionContext){
		DataObject server = (DataObject) actionContext.get("server");
		logger.info("serverId=" + server.getLong("id") + ", serverName=" 
				+ server.getString("name") + ", Execute result:\n" + actionContext.get("result"));
	}
	
	public static void onException(ActionContext actionContext){
		DataObject server = (DataObject) actionContext.get("server");
		logger.info("serverId=" + server.getLong("id") + ", serverName=" 
				+ server.getString("name") + ", Execute error:\n", (Exception) actionContext.get("exception"));
	}
	
	public static void onFinish(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		logger.info("Execute finished, thing=" + self.getMetadata().getPath());
	}
}
