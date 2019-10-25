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
package xworker.lang.text;

import java.util.Random;

import org.xmeta.ActionContext;

public class PasswordActions {
    public static Object createPassword(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Random r = new Random();
        char[] chars = new char[]{'!', '#', '%', '&', '(', ')', '*', '+', ',', '-', '.','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        int l = 16;// + r.nextInt(5);
        String pwd = "";
        for(int i=0;i<l; i++){
            pwd = pwd + chars[r.nextInt(chars.length)];
        }
        if(pwd.indexOf("\"") != -1 || pwd.indexOf("$") != -1 || pwd.indexOf("'") !=-1){
            System.out.println("un ok");
        }
        
         return pwd;
    }

}