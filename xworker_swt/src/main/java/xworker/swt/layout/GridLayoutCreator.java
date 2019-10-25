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
package xworker.swt.layout;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilSwt;

public class GridLayoutCreator {
	public static Object create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");

		GridLayout layout = new GridLayout();
		int numColumns = self.getInt("numColumns", 1);
		if (numColumns != 1) {
			layout.numColumns = numColumns;
		}
		if (self.getBoolean("makeColumnsEqualWidth")) {
			layout.makeColumnsEqualWidth = true;
		}
		int marginWidth = self.getInt("marginWidth", 5);
		if (marginWidth != 5)
			layout.marginWidth = UtilSwt.getInt(marginWidth);
		int marginHeight = self.getInt("marginHeight", 5);
		if (marginHeight != 5)
			layout.marginHeight = UtilSwt.getInt(marginHeight);
		int marginLeft = self.getInt("marginLeft", 0);
		if (marginLeft != 0)
			layout.marginLeft = UtilSwt.getInt(marginLeft);
		int marginTop = self.getInt("marginTop", 0);
		if (marginTop != 0)
			layout.marginTop = UtilSwt.getInt(marginTop);
		int marginRight = self.getInt("marginRight", 0);
		if (marginRight != 0)
			layout.marginRight = UtilSwt.getInt(marginRight);
		int marginBottom = self.getInt("marginBottom", 0);
		if (marginBottom != 0)
			layout.marginBottom = UtilSwt.getInt(marginBottom);
		int horizontalSpacing = self.getInt("horizontalSpacing", 5);
		if (horizontalSpacing != 5)
			layout.horizontalSpacing = UtilSwt.getInt(horizontalSpacing);
		int verticalSpacing = self.getInt("verticalSpacing", 5);
		if (verticalSpacing != 5)
			layout.verticalSpacing = UtilSwt.getInt(verticalSpacing);

		Composite parent = (Composite) actionContext.get("parent");
		parent.setLayout(layout);
		actionContext.getScope(0).put(self.getString("name"), layout);
		return layout;
	}

}