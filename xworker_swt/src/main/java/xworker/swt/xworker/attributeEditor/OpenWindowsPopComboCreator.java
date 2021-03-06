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
package xworker.swt.xworker.attributeEditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class OpenWindowsPopComboCreator {
    public static Object create(ActionContext actionContext){
        Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.openWins.DataObjectAttrOpenWindow/@thingShell");
        Object newShell = shellThing.doAction("create", actionContext, 
                UtilMap.toParams(new Object[]{"descriptorPath", "xworker.swt.xworker.PopCombo",
                "split","&"}));
        
        return newShell;
    }

}