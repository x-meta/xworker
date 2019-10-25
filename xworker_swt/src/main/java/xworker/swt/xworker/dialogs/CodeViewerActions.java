/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.swt.xworker.dialogs;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.swt.xworker.Colorer;

public class CodeViewerActions {
    public static void cancelButtonSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Shell shell = actionContext.getObject("shell");
        StyledText outputText = actionContext.getObject("outputText");
        
        Clipboard clipboard = new Clipboard(shell.getDisplay());
        String plainText = outputText.getText();
        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new String[]{plainText}, new Transfer[]{textTransfer});
        clipboard.dispose();
    }

    public static void closeButtonSelection(ActionContext actionContext){
    	Shell shell = actionContext.getObject("shell");
        shell.dispose();
    }

    public static void setCode(ActionContext actionContext){
    	Shell shell = actionContext.getObject("shell");
        StyledText outputText = actionContext.getObject("outputText");
        
        //设置着色
        Colorer.attach(outputText, (String) actionContext.get("codeType"), (String) actionContext.get("codeType"));
        
        
        if(actionContext.get("code") != null){
            outputText.setText((String) actionContext.get("code"));
        }
    }

}