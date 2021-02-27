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
package xworker.html.extjs.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Method，Event等。
 * 
 * @author Administrator
 *
 */
public class ExtjsMethod {
	/** 方法名称 */
	public String name;
	/** 方法的文档 */
	public String doc;
	/** 方法参数 */
	public List<ExtjsParam> params = new ArrayList<ExtjsParam>();
	/** 定义该方法的源对象  */
	public String source;
	
	@Override
	public String toString() {
		return "ExtjsMethod [name=" + name + ", source=" + source + ", params="
				+ params + "]";
	}
	
	public String getArgs(){
		String args = "";
		for(ExtjsParam param : params){
			if(!args.equals("")){
				args = args + ",";
			}
			
			args = args + param.name;
		}
		
		return args;
	}
}