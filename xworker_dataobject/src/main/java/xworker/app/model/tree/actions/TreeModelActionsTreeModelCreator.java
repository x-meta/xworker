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
package xworker.app.model.tree.actions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class TreeModelActionsTreeModelCreator {
	private static Logger log = LoggerFactory
			.getLogger(TreeModelActionsTreeModelCreator.class);

	public static void run(ActionContext actionContext) throws OgnlException {
		Thing self = (Thing) actionContext.get("self");

		Thing treeModel = (Thing) OgnlUtil.getValue(
				self.getString("treeModelName"), actionContext);
		if (treeModel != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			Object node = OgnlUtil.getValue(self.getString("nodeDataName"),
					actionContext);
			// log.info("TreeModel: node=" + node);
			if ("Nodes".equals(self.getString("nodeDataType"))) {
				params.put("nodes", node);
				params.put("node", null);
			} else {
				params.put("nodes", null);
				params.put("node", node);
			}
			// log.info("method=" + self.method);
			String method = self.getString("method");
			String nodeDataType = self.getString("nodeDataType");
			if ("insert".equals(method)) {
				Object parentId = OgnlUtil.getValue(self.getString("parentId"),
						actionContext);
				params.put("parentId", parentId);
				if ("Nodes".equals(nodeDataType)) {
					treeModel.doAction("insertNodes", actionContext, params);
				} else {
					treeModel.doAction("insertNode", actionContext, params);
				}
			} else if ("update".equals(method)) {
				if ("Nodes".equals(nodeDataType)) {
					treeModel.doAction("updateNodes", actionContext, params);
				} else {
					treeModel.doAction("updateNode", actionContext, params);
				}
			} else if ("delete".equals(method)) {
				if ("Nodes".equals(nodeDataType)) {
					treeModel.doAction("removeNodes", actionContext, params);
				} else {
					treeModel.doAction("removeNode", actionContext, params);
				}
			} else {
				log.info("TreeModelAtions: not support method, name=" + method);
			}
		} else {
			log.info("TreeModelActions: treeModel is null, name="
					+ self.getString("treeModelName"));
		}
	}

}