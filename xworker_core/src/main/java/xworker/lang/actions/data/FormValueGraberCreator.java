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
package xworker.lang.actions.data;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class FormValueGraberCreator {
	//private static Logger log = LoggerFactory.getLogger(FormValueGraberCreator.class);
	private static final String TAG = FormValueGraberCreator.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws OgnlException, ParseException{
        Thing self = (Thing) actionContext.get("self");
        
        //数据源
        Object sourceData = null;
        String sourceType = self.getString("sourceType");
        String sourceName = self.getString("sourceName");
        if("model".equals(sourceType)){
            Thing model = (Thing) OgnlUtil.getValue(sourceName, actionContext);
            if(model != null){
                ActionContext modelContext = actionContext;
                String modelActionContext = self.getString("modelActionContext");
                if(modelActionContext != null && !"".equals(modelActionContext)){
                    modelContext = (ActionContext) OgnlUtil.getValue(modelActionContext, actionContext);
                }
                if(modelContext == null){
                    modelContext = actionContext;
                }
                sourceData = model.doAction("getValue", modelContext);
            }
        }else{
            Thing form = (Thing) OgnlUtil.getValue(sourceName, actionContext);
            if(form != null){
                sourceData = form.doAction("getValues", actionContext);
            }
        }
        
        if(sourceData == null){
            Executor.info(TAG, "FormValueGraber: source is null, source=" + sourceName);
            return null;
        }
        
        //抓取数据
        Map<String, Object> targetData = new HashMap<String, Object>();
        for(Thing child : (List<Thing>) self.get("AttributeTransfer@")){
            Object sourceValue = OgnlUtil.getValue(child.getString("sourceVarName"), sourceData);
            Object targetValue = DataTransferCreator.transferAttribute(child, actionContext, sourceValue, child.getString("targetType"), child.getString("patternAction"), child.getString("pattern"));
            OgnlUtil.setValue(child.getString("targetVarName"), targetData, targetValue);
        }
        
        //返回数据
        String varName = self.getString("varName");
        if(varName != null && !"".equals(varName)){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            //log.info("bindigns is null " + (bindings == null));
            if(bindings != null){
                bindings.put(varName, targetData);
            }
        }
        
        return targetData;        
    }

}