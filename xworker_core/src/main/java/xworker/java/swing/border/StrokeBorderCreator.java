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
package xworker.java.swing.border;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.lang.reflect.Constructor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;

public class StrokeBorderCreator {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object create(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		BasicStroke stroke = AwtCreator.createBasicStroke(self, "stroke", actionContext);
		Paint paint = AwtCreator.createPaint(self, "paint", actionContext);
		
		//jdk1.7才有，这里还在用jdk1.6，所以用反射来实现
		Class cls = Class.forName("javax.swing.border.StrokeBorder");
		if(cls != null){
			Object border = null;
			if(paint == null){
				Constructor cons = cls.getConstructor(new Class[]{BasicStroke.class});
				border = cons.newInstance(new Object[]{stroke});
			}else{
				Constructor cons = cls.getConstructor(new Class[]{BasicStroke.class, Paint.class});
				border = cons.newInstance(new Object[]{stroke, paint});
			}
			
			actionContext.getScope(0).put(self.getMetadata().getName(), border);
			
			return border;
		}else{
			return null;
		}
		
	}
}