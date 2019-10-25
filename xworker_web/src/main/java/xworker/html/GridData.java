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
package xworker.html;

import java.util.HashMap;
import java.util.Map;

public class GridData {
	public int colspan;
	public int rowspan;
	public Object userData;
	
	Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public void set(String name, Object data){
		dataMap.put(name, data);
	}
	
	public Object get(String name){
		if("userData".equals(name)){
			return userData;
		}
		if("rowspan".equals(name)){
			return rowspan;
		}
		if("colspan".equals(name)){
			return colspan;
		}
		return dataMap.get(name);
	}
	
	public Object getUesrData(){
		return userData;
	}
}