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
package xworker.dataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含校验结果的信息。
 * 
 * @author Administrator
 *
 */
public class ValidateResult {
	DataObject dataObject;
	List<FieldInfo> infos = new ArrayList<FieldInfo>();
	
	public ValidateResult(DataObject dataObject){
		this.dataObject = dataObject;
	}
	
	public boolean isOk(){
		return infos.size() == 0;
	}
	
	public DataObject getDataObject() {
		return dataObject;
	}

	public List<FieldInfo> getInfos() {
		return infos;
	}

	public void addInfo(String field, String message){
		FieldInfo info = new FieldInfo();
		info.field = field;
		info.message = message;
		infos.add(info);
	}
	
	public String toString(){
		String info = null;
		for(int i=0; i<infos.size(); i++){
			if(i == 0){
				info = infos.get(i).message;
			}else{
				info = info + "\n" + infos.get(i).message;
			}				
		}
		
		return info;
	}
	
	public static class FieldInfo{
		public String field;
		public String message;
	}
}