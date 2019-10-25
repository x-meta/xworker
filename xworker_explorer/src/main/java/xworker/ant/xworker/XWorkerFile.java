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
package xworker.ant.xworker;

import java.io.File;

/**
 * 保存目录和文件信息的实体，可以用于生成 ant的fileset的信息。
 * 
 * @author Administrator
 *
 */
public class XWorkerFile {
	public static String WORLD = "world";
	public static String WEBROOT ="webroot";
	public static String ABSOLUTE = "absolute";
	/** 配置名称 */
	public String name;
	
	/** 文件 */
	public File file;
	
	/** 目标路径  */
	public String targetPath;
	
	/** 要排除的文件列表 */
	public String excludes;
	
	/** 库类型 */
	public String repository;
	
	/**
	 * 返回要拷贝的目的目录。
	 * 
	 * @param dir
	 * @param rootDir
	 * @return
	 */
	public String getPath(String dir, String rootDir){
		String path = rootDir;
		String targetPath_ = targetPath;
		if(dir != null && !"".equals(dir)){
			path = path + "/" + dir + "/";
		}
		if(targetPath_ == null || "".equals(targetPath_)){
			targetPath_ = path;
		}else{
			targetPath_ = path + "/" + targetPath_;
		}

		return targetPath_;
	}
}