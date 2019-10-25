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
package xworker.java.actions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;

import ognl.OgnlException;

public class SocketActions {
	/**
	 * 创建Socket。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static Object createSocket(ActionContext actionContext) throws OgnlException, UnknownHostException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Object host;
		int port;
		
		//创建socket
		if(self.getBoolean("fromVar")){
			host = OgnlUtil.getValue(self, "host", actionContext);
			port = (Integer) OgnlUtil.getValue(self, "port", actionContext);
		}else{
			host = self.getString("host");
			port = self.getInt("port");
		}
		
		Socket socket = null;
		if(host instanceof String){
			socket = new Socket((String) host, port);
		}else{
			socket = new Socket((InetAddress) host, port);
		}
		
		//保存变量
		String socketVarName = self.getStringBlankAsNull("socketVarName");
		String inputStreamVarName = self.getStringBlankAsNull("inputStreamVarName");
		String outputStreamVarName = self.getStringBlankAsNull("outputStreamVarName");
		if(socketVarName != null){
			UtilAction.putVarByActioScope(self, socketVarName, socket, actionContext);
		}
		if(inputStreamVarName != null){
			UtilAction.putVarByActioScope(self, inputStreamVarName, socket.getInputStream(), actionContext);
		}
		if(outputStreamVarName != null){
			UtilAction.putVarByActioScope(self, outputStreamVarName, socket.getOutputStream(), actionContext);
		}
		
		//返回值
		return socket;
	}
}