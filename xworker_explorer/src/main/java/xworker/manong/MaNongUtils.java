package xworker.manong;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MaNongUtils {
	private static Logger logger = LoggerFactory.getLogger(MaNongUtils.class);
	
	/**
	 * 根据用户名和密码返回用户信息。
	 * 
	 * @param userName
	 * @param password
	 * @param con
	 * @param actionContext
	 * @return
	 */
	public static Map<String, Object> getUserInfo(String userName, String password, Connection con, ActionContext actionContext){
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nickName = null;
		try{
			//检查密码是否正确
			pst = con.prepareStatement("select name, password, nickname, randomKey, email from tbluser where name=?");
			pst.setString(1, userName);
			rs = pst.executeQuery();
			if(rs.next()){				
				String dbPassword = rs.getString("password");
				String randomKey = rs.getString("randomKey");
				nickName = rs.getString("nickname");
				
				if(!MaNongServer.getMd5(password, randomKey).equals(dbPassword)){
					return null;
				}
				
				Map<String, Object> user = new HashMap<String, Object>();
				user.put("name", rs.getString("name"));
				user.put("nickname", nickName);
				user.put("email", rs.getString("email"));
				return user;
			}
			
			return null;
		}catch(Exception e){
			logger.error("Get user info error", e);
			return null;
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
	
	public static void showMessage(String title, int status, String message,  Shell parent){
		int icon = SWT.ICON_INFORMATION;
		if(status == 1){
			icon = SWT.ICON_WARNING;
		}else if(status == 2){
			icon = SWT.ICON_ERROR;
		}else{
			icon = SWT.ICON_INFORMATION;
		}
		
		if(message == null){
			message = "服务器返回值事物的message字段的为空，服务器端没有设置消息！";
		}
		
		showMessage(parent, icon, title, message);
	}
	
	public static void showMessage(String title, Thing thing, Shell parent){
		if(thing == null){
			showMessage(parent, SWT.ICON_WARNING, title, "服务器返回的事物为null！");
			return;
		}
		
		int status = thing.getInt("status");
		int icon = SWT.ICON_INFORMATION;
		if(status == 1){
			icon = SWT.ICON_WARNING;
		}else if(status == 2){
			icon = SWT.ICON_ERROR;
		}else{
			icon = SWT.ICON_INFORMATION;
		}
		
		String message = thing.getStringBlankAsNull("message");
		if(message == null){
			message = "服务器返回值事物的message字段的为空，服务器端没有设置消息！";
		}
		
		showMessage(parent, icon, title, message);
	}
	
	public static void showMessage(Shell parent, int icon, String title, String message){
		MessageBox box = new MessageBox(parent, icon | SWT.OK);
		box.setText(title);
		box.setMessage(message);
		box.open();
	}
	
	/**
	 * 取第一个硬件网卡的地址。
	 * 
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getMacAddress() throws SocketException, UnknownHostException{
		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	    while (en.hasMoreElements()) {
	    	NetworkInterface ni = en.nextElement();
		    	if(ni.getName().toLowerCase().indexOf("eth") != -1){
		    	byte[] hd = ni.getHardwareAddress();
		    	if(hd != null && hd.length != 0){
		    		return new String(Hex.encodeHex(hd));
		    	}
	    	}
	    }
	    
	    return "";
	}

}
