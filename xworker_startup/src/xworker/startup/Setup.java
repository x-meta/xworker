
package xworker.startup;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 设置XWorker。
 * 
 * @author zyx
 *
 */
public class Setup {
	
	public static void write(String fileName, String[] lines, boolean overwrite) throws IOException{
		File file = new File(fileName);
		if(!file.exists() || overwrite){
			file.getParentFile().mkdirs();
			
			FileOutputStream fout = new FileOutputStream(file);
			write(lines, fout);
			fout.close();
		}
	}
	
	public static void setExecutable(File file){
		file.setExecutable(true);
	}
	
	public static void write(String[] lines, OutputStream out) throws IOException{
		for(String line : lines){
			out.write(line.getBytes());
			out.write("\n".getBytes());
		}
	}
	
	public static void setup() throws IOException{
		String OS = null;
		try{
			OS = System.getenv("OS").toLowerCase();
		}catch(Exception e){
			OS = System.getProperty("os.name").toLowerCase();
		}
		
		OS = OS.toLowerCase();
		System.out.println("System is " + OS);
		setup(OS);
	}
	
	public static void setup(String OS) throws IOException{
		File dbdir = new File("./databases/sqlite/");
		if(dbdir.exists() == false){
			dbdir.mkdirs();
		}
		
		if(OS.indexOf("windows") != -1){
			setupWindows();
		}else if(OS.indexOf("linux") != -1){
			setupLinux();
		}else if(OS.indexOf("mac os x") != -1){
			setupMaxOsX();
		}else{
			System.out.println("unkonw system, can not set up xworker");
		}
	}
	
	public static void setupMaxOsX() throws IOException{
		File dir = new File(".");
		String home = dir.getAbsolutePath();
		home = home.substring(0, home.length() - 1);
		
		//写入update.sh
		write("./update.sh", new String[]{
				"#!/bin/sh",
				"cd " + home,
				"java -classpath \"" + home + "xworker.jar\" xworker.filesync.FileSync $1",
				"./upidx.sh"
		}, true);
				
		write("./dmlforlink.sh", new String[]{
				"#!/bin/sh",
				"",
				home + "dml.sh \"$@\""
		}, true);
		
		write("./dml-rapforlink.sh", new String[]{
				"#!/bin/sh",
				"",
				home + "dml-rap.sh \"$@\""
		}, true);
		
		//写入setup.sh，进行文件关联等		
		write("./setupenv.sh", new String[]{		
				"sudo rm -f /usr/bin/dml.sh",
				"sudo ln -s " + home + "dmlforlink.sh /usr/bin/dml.sh",			
				"sudo rm -f /usr/bin/dml-rap.sh",
				"sudo ln -s " + home + "dml-rapforlink.sh /usr/bin/dml-rap.sh"
		}, true);

		//先写入一个临时的文件
		write("./dml.sh", new String[]{					
				"java -version"				
		}, false);
		write("./upidx.sh", new String[]{					
				"java -version"				
		}, false);
		write("./thingexplorer.sh", new String[]{					
				"java -version"				
		}, false);
		write("./dml2xsd.sh", new String[] {
				"dml.sh xworker.tools.GenerateXsd run $1 $2"
		}, false);
		String os = Startup.getOS();
		String arc = Startup.getPROCESSOR_ARCHITECTURE();
		write("./dml.conf.sh", new String[]{
				"JAVA_OPTS=\"$JAVA_OPTS -Djava.library.path=$XWORKER_HOME/os/library/" 
					+ os + "_" + arc + "/;$XWORKER_HOME/os/library/" + os + "/;$PATH\""
		}, true);
		setExecutable(new File("./update.sh"));
		setExecutable(new File("./dml.sh"));
		setExecutable(new File("./setupenv.sh"));
		setExecutable(new File("./upidx.sh"));
		setExecutable(new File("./dmlforlink.sh"));
		setExecutable(new File("./thingexplorer.sh"));
		setExecutable(new File("./dml-rap.sh"));
		setExecutable(new File("./dml-rapforlink.sh"));
		setExecutable(new File("./dml2xsd.sh"));
		setExecutable(new File("./dml.conf.sh"));
	}
	
	public static void setupLinux() throws IOException{
		File dir = new File(".");
		String home = dir.getAbsolutePath();
		home = home.substring(0, home.length() - 1);
		
		//写入update.sh
		write("./update.sh", new String[]{
				"#!/bin/sh",
				"cd " + home,
				"java -classpath \"" + home + "xworker.jar\" xworker.filesync.FileSync $1",
				"./upidx.sh"
		}, true);
		
		//写入文件类型
		write("./config/xworker-dml.xml", new String[]{
				"<?xml version=\"1.0\"?>",
				"<mime-info xmlns='http://www.freedesktop.org/standards/shared-mime-info'>",
				"  <mime-type type=\"text/dml\">",
				"    <comment>DynamicModel</comment>",
				"    <glob pattern=\"*.dml\"/>",
				"  </mime-type>",
				"</mime-info>"
		}, true);
		
		write("./config/xworker.desktop", new String[]{
				"[Desktop Entry]",
				"Encoding=UTF-8",
				"Version=1.0",
				"Name=XWorker",
				"GenericName=XWorker",
				"Comment=Dyanimc Model runtime!",
				"Exec=" + home + "dml.sh",
				"Terminal=true",
				"Type=Application",
				"MimeType=text/dml",
				"Hidden=true",
				"Categories=Development"
		}, true);
		
		write("./dmlforlink.sh", new String[]{
				"#!/bin/sh",
				"",
				home + "dml.sh \"$@\""
		}, true);
		
		write("./dml-rapforlink.sh", new String[]{
				"#!/bin/sh",
				"",
				home + "dml-rap.sh \"$@\""
		}, true);
		
		//写入setup.sh，进行文件关联等		
		write("./setupenv.sh", new String[]{		
				"sudo rm -f /usr/bin/dml.sh",
				"sudo ln -s " + home + "dmlforlink.sh /usr/bin/dml.sh",
				"sudo rm -f /usr/bin/dml-rap.sh",
				"sudo ln -s " + home + "dml-rapforlink.sh /usr/bin/dml-rap.sh",
				//"echo \"export XMETA_HOME=" + home + "\" >> /etc/profile",
				//"source /etc/profile",
				"sudo xdg-mime install --mode system ./config/xworker-dml.xml",
				"sudo cp ./config/xworker.desktop  /usr/share/applications/",
				"xdg-mime default xworker.desktop text/dml"				
		}, true);

		//先写入一个临时的文件
		write("./dml.sh", new String[]{					
				"java -version"				
		}, false);
		write("./upidx.sh", new String[]{					
				"java -version"				
		}, false);
		write("./thingexplorer.sh", new String[]{					
				"java -version"				
		}, false);
		
		String os = Startup.getOS();
		String arc = Startup.getPROCESSOR_ARCHITECTURE();
		write("./dml.conf.sh", new String[]{
				"JAVA_OPTS=\"$JAVA_OPTS -Djava.library.path=$XWORKER_HOME/os/library/" 
					+ os + "_" + arc + "/;$XWORKER_HOME/os/library/" + os + "/;$PATH\""
		}, true);
					
		setExecutable(new File("./update.sh"));
		setExecutable(new File("./dml.sh"));
		setExecutable(new File("./setupenv.sh"));
		setExecutable(new File("./upidx.sh"));
		setExecutable(new File("./dmlforlink.sh"));
		setExecutable(new File("./thingexplorer.sh"));
		setExecutable(new File("./dml-rap.sh"));
		setExecutable(new File("./dml-rapforlink.sh"));
		setExecutable(new File("./dml.conf.sh"));
		
		//Desktop.getDesktop().open(new File("update.sh"));
		//Desktop.getDesktop().open(new File("setup.sh"));
	}
	
	public static void setupWindows() throws IOException{
		File dir = new File(".");
		String home = dir.getAbsolutePath();
		home = home.substring(0, home.length() - 1);
		
		//检查update.cmd是否存在
		write("./update.cmd", new String[]{
				"cd /d " + home + "\r",
				"java -classpath \"xworker.jar\" xworker.filesync.FileSync %1\r",
				"upidx.cmd"
		}, true);
		
		//创建ThngExpl
		//写入文件关联cmd		
		write("./setupenv.cmd", new String[]{				
		    "echo set file association\r",		    
			"assoc .dml=DynamicModel\r",
			"assoc .dmw=DynamicModelWindow\r",
			"ftype DynamicModel=" + home + "dml.cmd %%1 %%*\r",
			"ftype DynamicModelWindow=" + home + "dmw.cmd %%1 %%*\r",
			"\r",
		    "echo set XMETA_HOME\r",
		    "SetX  XMETA_HOME " + home + " /M\r",			
			"echo If Access is denied. Please run it under administrator!\r",
			"pause"
		}, true);
		
		String os = Startup.getOS();
		String arc = Startup.getPROCESSOR_ARCHITECTURE();
		write("./dml.conf.cmd", new String[]{
				"set JAVA_OPTS=%JAVA_OPTS% -Djava.library.path=\"%XWORKER_HOME%os\\library\\" 
					+ os + "_" + arc + "\\;%XWORKER_HOME%os\\library\\" + os + "\\;%PATH%\""
		}, true);
		
		//启动update.cmd
		Desktop.getDesktop().open(new File("update.cmd"));
	}
	
	public static void main(String args[]){
		try{
			Startup.initOS();
			
			setup();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
