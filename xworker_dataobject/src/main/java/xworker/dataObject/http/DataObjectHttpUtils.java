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
package xworker.dataObject.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataObjectHttpUtils {
	/**
	 * 通过给定的DataObject从httpRequest中分析参数。
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object parseHttpRequestData(ActionContext actionContext){        
        DataObject theData = (DataObject) actionContext.get("theData");
        Thing self = (Thing) actionContext.get("self");
        if(theData == null){
            theData = new DataObject(self);
        }
        
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        Map paramMap = request.getParameterMap();
        for(Thing attribute : self.getChilds("attribute")){
        	String name = attribute.getString("name");
            if(paramMap.containsKey(name)){
                theData.put(name, request.getParameter(name));
            }else if("truefalse".equals(attribute.getString("inputtype"))){
                //checkbox特殊处理
                theData.put(name, "false");
            }
        }
        
        return theData;
    }
}