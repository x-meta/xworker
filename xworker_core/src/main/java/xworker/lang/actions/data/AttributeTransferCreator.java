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

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class AttributeTransferCreator {
	//private static Logger log = LoggerFactory.getLogger(AttributeTransferCreator.class);
	private static final String TAG = AttributeTransferCreator.class.getName();

	public static Object run(ActionContext actionContext) throws OgnlException, ParseException {
		Thing self = (Thing) actionContext.get("self");

		// 源数据
		String sourceName = self.getString("sourceName");
		if (sourceName == null || "".equals(sourceName)) {
			Executor.info(TAG, "AttributeTransfer: source is null, name="
					+ self.getMetadata().getName() + ",sourceVarName="
					+ self.getString("sourceVarName"));
			return null;
		}
		
		Object sourceValue = OgnlUtil.getValue(sourceName, actionContext);
		String pattern = self.getString("pattern");
		String patternAction = self.getString("patternAction");
		String targetType = self.getString("targetType");
		String patternType = self.getString("patternType");		
		Object targetValue = UtilData.transfer(sourceValue, targetType, pattern, patternType, patternAction);
		
		// 保存变量
		String targetVarName = self.getString("targetVarName");
		if (targetVarName != null && !"".equals(targetVarName)) {
			Bindings bindings = (Bindings) self.doAction("getVarScope",
					actionContext);
			if (bindings != null) {
				bindings.put(targetVarName, targetValue);
			}
		}

		return targetValue;
	}
}