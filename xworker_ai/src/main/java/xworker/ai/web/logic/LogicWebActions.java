package xworker.ai.web.logic;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;

import xworker.dataObject.utils.DbUtil;

public class LogicWebActions {
	private static Logger logger = LoggerFactory.getLogger(LogicWebActions.class);
	
	public static void getKb(ActionContext actionContext) throws SQLException, IOException{
		Connection con = (Connection) actionContext.get("con");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		response.setContentType("text/plain; charset=utf-8");
		PrintWriter writer= response.getWriter();
		if(request.getParameter("id") == null){
			writer.println("-1|parameter id is null");
			return;
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{						
			int kbId = Integer.parseInt(request.getParameter("id"));
			pst = con.prepareStatement("select type from tblAiKnowledgeBase where tid=?");
			pst.setInt(1, kbId);
			rs = pst.executeQuery();
			if(rs.next()){
				int type = rs.getInt("type");
				if(type == 1){
					//命题逻辑
					writer.println("" + type);
				}else if(type == 2){
					//一阶逻辑
					writer.println("" + type);
				}else{
					writer.println("-1|Unknown knowdegeBase type, type=" + type + ", kbId=" + kbId);
					return;
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select type, sentence from tblAiKnowledgeBaseSentence where kbId=?");
			pst.setInt(1, kbId);
			rs = pst.executeQuery();
			while(rs.next()){
				String type = rs.getString("type");
				String sentence = rs.getString("sentence");
				writer.println(type + "|" + sentence);
			}
		}catch(Exception e){
			writer.println("-1|" + e.getMessage());
			logger.error("get logic knowledgeBase error", e);
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
		}
	}
}
