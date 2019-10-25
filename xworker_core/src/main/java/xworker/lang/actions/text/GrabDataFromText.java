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
package xworker.lang.actions.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class GrabDataFromText {
	/**
	 * 从文本截取字符串。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static Object run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//是否返回字符串
		boolean returnList = self.getBoolean("returnList");
		
		//文本
		Object strObj = OgnlUtil.getValue(self, "textExpression", actionContext);
		if(strObj == null){
			return returnNull(returnList);
		}
		String text = String.valueOf(strObj);
		
	    //预定位
		int index = 0;
		Thing preIndex = self.getThing("PreIndex@0");
		if(preIndex != null){
			for(Thing stringIndex : preIndex.getChilds()){
				index = index(text, stringIndex, index, false);
				if(index == -1){
					returnNull(returnList);
				}
			}
		}
		
		//搜索词
		Thing fields = self.getThing("Fields@0");
		if(fields != null){
			List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
			boolean first = true;
			while(true){
				//多条数据的遍历中的循环
				if(!first){
					Thing lpreIndex = self.getThing("ListPreIndex@0");
					if(lpreIndex != null){
						for(Thing stringIndex : lpreIndex.getChilds()){
							index = index(text, stringIndex, index, false);
							if(index == -1){
								break;
							}
						}
					}
				}				
				if(index == -1){
					break;
				}
				
				first = false;
				Map<String, String> data = new HashMap<String, String>();
				for(Thing field : fields.getChilds()){
					preIndex = field.getThing("PreIndex@0");
					if(preIndex != null){
						for(Thing stringIndex : preIndex.getChilds()){
							index = index(text, stringIndex, index, true);
							if(index == -1){
								returnNull(returnList);
							}
						}
					}
					
					int index2 = index;
					Thing sufIndex = field.getThing("SuffixStringIndex@0");
					if(sufIndex != null){
						index2 = index(text, sufIndex, index, false);
						if(index2 == -1){
							index = -1;
							break;
						}
					}
					
					String value = text.substring(index, index2);
					index = index2;
					data.put(field.getString("name"), value);
					
				}
				
				datas.add(data);
				
				if(!returnList){
					//如果只返回一条记录
					break;
				}
				
				if(index == -1){
					break;
				}
			}
			
			if(returnList){
				return datas;
			}else if(datas.size() > 0){
				return datas.get(0);
			}
		}
		
		return returnNull(returnList);
	}
	
	public static int index(String text, Thing stringIndex, int index, boolean addLength){
		String strIndex = stringIndex.getString("index");
		if(strIndex != null && !"".equals(strIndex)){
			return Integer.parseInt(strIndex);
		}
		
		String key = stringIndex.getString("string");
		if(key != null && !"".equals(index)){
			if(key.startsWith("'") && key.endsWith("'")){
				key = key.substring(1, key.length() - 1);
			}
			
			return text.indexOf(key, index) +  (addLength ? key.length() : 0);
		}else{
			return index;
		}
	}
	
	public static Object returnNull(boolean returnList){
		if(returnList){
			return Collections.emptyList();
		}else{
			return null;
		}
	}
}