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
package xworker.lang.actions.log;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.util.UtilTemplate;

public class LoggerActionsLogTemplateCreator {
	/**
	 * 应该把打印的消息使用模板生成。
	 * @throws TemplateException 
	 * @throws IOException 
	 */
    public static void run(ActionContext actionContext) throws OgnlException, IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
		// 异常
		Throwable exception = null;
		String exceptionVarName = self.getString("exceptionVarName");
		if (exceptionVarName != null && !"".equals(exceptionVarName)) {
			exception = (Throwable) OgnlUtil.getValue(exceptionVarName,
					actionContext);
		}

		// 日志
		Logger logger = LoggerFactory.getLogger("thing:"
				+ self.getMetadata().getPath());

		// 打印日志
		String level = self.getString("level");
		String message = self.getString("message");
		if (message == null) {
			message = "";
		}else{
			message = UtilTemplate.processThingAttributeToString(self, "message", actionContext);
		}
		if ("debug".equals(level)) {
			if (exception != null) {
				logger.debug(message, exception);
			} else {
				logger.debug(message);
			}
		} else if ("info".equals(level)) {
			if (exception != null) {
				logger.info(message, exception);
			} else {
				logger.info(message);
			}
		} else if ("warn".equals(level)) {
			if (exception != null) {
				logger.warn(message, exception);
			} else {
				logger.warn(message);
			}
		} else if ("error".equals(level)) {
			if (exception != null) {
				logger.error(message, exception);
			} else {
				logger.error(message);
			}
		} else if ("fatal".equals(level)) {
			if (exception != null) {
				logger.error(message, exception);
			} else {
				logger.error(message);
			}
		} else if (exception != null) {
			logger.info(message, exception);
		} else {
			logger.info(message);
		}

    }

}