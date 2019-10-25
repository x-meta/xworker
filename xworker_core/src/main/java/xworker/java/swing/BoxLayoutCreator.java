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
package xworker.java.swing;

import javax.swing.BoxLayout;

public class BoxLayoutCreator {
	public static int getConstans(String name){
		if("X_AXIS".equals(name)){
			return BoxLayout.X_AXIS;
		}else if("Y_AXIS".equals(name)){
			return BoxLayout.Y_AXIS;
		}else if("LINE_AXIS".equals(name)){
			return BoxLayout.LINE_AXIS;
		}else if("PAGE_AXIS".equals(name)){
			return BoxLayout.PAGE_AXIS;
		}else{
			return -1;
		}
	}
}