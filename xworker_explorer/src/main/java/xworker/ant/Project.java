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
package xworker.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.listener.BigProjectLogger;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class Project {
	//private static Logger log = LoggerFactory.getLogger(Project.class);
	
	/**
	 * 运行服务器。
	 * @throws IOException 
	 */
	public static void run(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		String antFilePath = World.getInstance().getPath() + "/work/ant/" + self.getMetadata().getCategory().getName().replace('.', '/') + "/" + self.getMetadata().getName() + ".xml";
		
		//是否需要重新生成ANT文件
		File antFile = new File(antFilePath);
		//每次都重新生成ant配置，考虑到像xworker的配置每次都有可能会变化，并且XWorker生成的脚本可能采用的是绝对路径，2013-04-11张玉祥
		if(true){//!antFile.exists() || antFile.lastModified() < self.getMetadata().getLastModified()){
			String antXml = (String) self.doAction("toString", actionContext);
			antFile.getParentFile().mkdirs();
			FileOutputStream fout = new FileOutputStream(antFile);
			fout.write(antXml.getBytes());
			fout.close();
		}
		
		org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
		BigProjectLogger listener = new BigProjectLogger();
		listener.setErrorPrintStream(System.err);
		listener.setOutputPrintStream(System.out);
		listener.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);
		project.addBuildListener(listener);
		
		String target = (String) actionContext.get("anttarget");
		if(target == null || "".equals(target)){
			target = project.getDefaultTarget();
		}
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		helper.parse(project, antFile);
		project.executeTarget(target);
		project.fireBuildFinished(null);		
	}
}