/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PreparedStatementContextActions {
	private static Logger log = LoggerFactory.getLogger(PreparedStatementContextActions.class);
	
	public static void init(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");

		ActionContext acContext = (ActionContext) actionContext.get("acContext");
		
		Connection con = (Connection) acContext.get(self.getString("connection"));
		if(con == null){
		    log.info("PreparedStatementContext: connection is null, name=" + self.getString("connection"));
		    return;
		}

		PreparedStatement pst = con.prepareStatement(self.getString("sql"));
		acContext.peek().put(self.getString("varName"), pst);
	}
	
	public static void success(ActionContext actionContext) throws SQLException{
		if(actionContext.get("pst") != null){
		    ((PreparedStatement) actionContext.get("pst")).close();  
		}
	}
}