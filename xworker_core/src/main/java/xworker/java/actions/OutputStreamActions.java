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
import java.io.OutputStream;
import java.nio.charset.Charset;

import ognl.OgnlException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

public class OutputStreamActions {
	/**
	 * 向一个输出流里写入字符串。
	 * 
	 * @param actionContext
	 * @throws OgnlException 
	 * @throws IOException 
	 */
	public static void writeString(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		OutputStream out = (OutputStream) OgnlUtil.getValue(self, "outputStreamVarName", actionContext);
		String str = (String) OgnlUtil.getValue(self, "stringVarName", actionContext);
		String charset = self.getStringBlankAsNull("charset");
		
		if(out != null && str != null){
			if(charset != null){
				out.write(str.getBytes(Charset.forName(charset)));
			}else{
				out.write(str.getBytes());
			}
			
			out.flush();
		}
	}
}