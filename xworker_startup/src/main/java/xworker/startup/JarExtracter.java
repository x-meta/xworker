package xworker.startup;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * 提取Jar中的xworker，并把它解压到当前目录下。
 * 
 * @author zyx
 *
 */
public class JarExtracter {
	public static void main(String args[]) {
		try {
			JarFile jarFile = new JarFile(JarRunner.getJarFileName());
			
			//获取要运行的事物
			Attributes attributes = jarFile.getManifest().getMainAttributes(); 
			String xworker_home = ".";
			String xworkerJar = attributes.getValue("Extract-Jar");
			System.out.println("xworker_home=" + xworker_home);
			System.out.println("xworkerJar=" + xworkerJar);
			File xworkerRoot = JarRunner.getXWorkerHome(xworker_home);		
			System.out.println("xworkerRoot=" + xworkerRoot);
			if(xworkerJar != null) {
				JarRunner.extractXWorker(xworkerRoot, jarFile, jarFile.getJarEntry(xworkerJar));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
