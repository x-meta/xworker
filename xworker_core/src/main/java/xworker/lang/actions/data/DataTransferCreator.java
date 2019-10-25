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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class DataTransferCreator {
	private static Logger log = LoggerFactory
			.getLogger(DataTransferCreator.class);

	@SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext)
			throws ParseException, OgnlException {
		Thing self = (Thing) actionContext.get("self");

		String sourceVarName = self.getString("sourceVarName");
		Object sourceData = OgnlUtil.getValue(sourceVarName, actionContext);
		if (sourceData == null) {
			log.info("DataTransfer: source is null, source=" + sourceVarName);
			return null;
		}

		Object targetVarName = self.getString("targetVarName");
		Object targetData = OgnlUtil.getValue(targetVarName, actionContext);
		if (targetData == null) {
			log.info("DataTransfer: target is null, target=" + targetVarName);
			return null;
		}

		for (Thing child : (List<Thing>) self.get("AttributeTransfer@")) {
			Object sourceValue = OgnlUtil.getValue(
					child.getString("sourceVarName"), sourceData);
			Object targetValue = transferAttribute(self, actionContext,
					sourceValue, child.getString("targetType"), child
							.getString("patternAction"), child
							.getString("pattern"));
			OgnlUtil.setValue(child.getString("targetVarName"), targetData,
					targetValue);
		}
		return targetData;
	}

	public static Object transferAttribute(Thing self,
			ActionContext actionContext, Object sourceValue, Object targetType,
			String patternAction, String pattern) throws ParseException {
		if (pattern != null && sourceValue != null) {
			if (patternAction == "parse") {
				return UtilData.parse(String.valueOf(sourceValue), String
						.valueOf(targetType), pattern);
			} else {
				sourceValue = UtilData.format(sourceValue, pattern);
			}
		}

		// 数据转换
		Object targetValue = sourceValue;
		if (targetType != null && !"".equals(targetType)) {
			if ("byte".equals(targetType)) {
				targetValue = UtilData.getByte(sourceValue, (byte) 0);
			} else if ("short".equals(targetType)) {
				targetValue = UtilData.getShort(sourceValue, (short) 0);
			} else if ("int".equals(targetType)) {
				targetValue = (int) UtilData.getLong(sourceValue, 0);
			} else if ("long".equals(targetType)) {
				targetValue = UtilData.getLong(sourceValue, 0);
			} else if ("float".equals(targetType)) {
				targetValue = UtilData.getFloat(sourceValue, 0);
			} else if ("double".equals(targetType)) {
				targetValue = UtilData.getDouble(sourceValue, 0);
			} else if ("boolean".equals(targetType)) {
				targetValue = UtilData.getBoolean(sourceValue, false);
			} else if ("byte[]".equals(targetType)) {
				if (sourceValue instanceof String) {
					targetValue = UtilString
							.hexStringToByteArray((String) sourceValue);
				} else if (sourceValue instanceof byte[]) {
					targetValue = sourceValue;
				} else {
					targetValue = null;
				}
			} else if ("hex_byte[]".equals(targetType)) {
				if (sourceValue instanceof byte[]) {
					targetValue = UtilString.toHexString((byte[]) sourceValue);
				} else if (sourceValue instanceof String) {
					targetValue = sourceValue;
				} else {
					targetValue = null;
				}
			} else
				targetValue = sourceValue;
		}

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