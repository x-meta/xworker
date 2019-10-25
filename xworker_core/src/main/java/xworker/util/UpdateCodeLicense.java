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
package xworker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 更新所有代码的版权申明。
 * 
 * @author Administrator
 *
 */
public class UpdateCodeLicense {
	public static void updateLicence(File file) throws IOException{
		if(file.isDirectory()){
			for(File childFile : file.listFiles()){
				updateLicence(childFile);
			}
		}else{
			String name = file.getName();
			if(name.toLowerCase().endsWith(".java")){
				String code = getLicenseText();
				FileInputStream fin = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin, "utf-8"));
				String line = null;
				boolean start = false;
				while((line = br.readLine()) != null){
					if(start){
						code = code + "\r\n" + line;
					}else{
						if(line.startsWith("package") || line.startsWith("import")){
							start = true;
							code = code + "\r\n" + line;
						}
					}	
				}
				fin.close();
				
				FileOutputStream fout = new FileOutputStream(file);
				fout.write(code.getBytes("utf-8"));
				fout.close();
				
				System.out.println("updated " + file.getAbsolutePath());
			}
		}
	}
	
	public static String getLicenseText(){
		return "/*******************************************************************************\r\n" + 
               "* Copyright 2007-2013 See AUTHORS file.\r\n" + 
	               " * \r\n" + 
	               "* Licensed under the Apache License, Version 2.0 (the \"License\");\r\n" + 
	               "* you may not use this file except in compliance with the License.\r\n" + 
	               "* You may obtain a copy of the License at\r\n" + 
	               "* \r\n" + 
	               "*   http://www.apache.org/licenses/LICENSE-2.0\r\n" + 
	               "* \r\n" + 
	               "* Unless required by applicable law or agreed to in writing, software\r\n" + 
	               "* distributed under the License is distributed on an \"AS IS\" BASIS,\r\n" + 
	               "* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\r\n" + 
	               "* See the License for the specific language governing permissions and\r\n" + 
	               "* limitations under the License.\r\n" + 
	               "******************************************************************************/";		
	}
	
	public static void main(String args[]){
		try{
			File file = new File("../../x-meta/org.xmeta.engine/src/");
			updateLicence(file);
			
			file = new File("src");
			updateLicence(file);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}