package xworker.util.deploy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.xmeta.Thing;
import org.xmeta.World;

public class ModuleResource {
	Thing thing;
	
	File file;
	
	String path = null;
	
	Pattern pattern = null;
	
	/** true是类库，false为资源 */
	boolean isLib;
	
	String scope;
	
	String os;
	
	public ModuleResource(ModuleResource source) {
		this.thing = source.thing;
		this.pattern = source.pattern;
		this.isLib = source.isLib;
		this.scope = source.scope;
		this.os = source.os;
	}
	
	public ModuleResource(Thing thing, String pathRegex, boolean isLib) {
		this.thing = thing;
		this.isLib = isLib;
		this.scope = thing.getString("scope");
		this.os = thing.getStringBlankAsNull("os");
		
		if(pathRegex != null) {
			pattern = Pattern.compile(pathRegex);
		}
	}
		
	public boolean acceptScope(String scope) {
		if(scope == null || "".equals(scope) || "max".equals(scope)) {
			return true;
		}
		
		if(this.scope == null || "".equals(this.scope)) {
			return true;
		}
		
		if(this.scope.equals(scope)) {
			return true;
		}else if("normal".equals(scope) && "min".equals(this.scope)) {
			return true;
		}else {
			return false;
		}
	}
	
	public ModuleResource accept(File file) throws IOException {
		String filePath = file.getCanonicalPath();
		if(pattern != null && pattern.matcher(filePath).matches()) {
			File xworkerDir = new File(World.getInstance().getPath());
			String xworkerPath = xworkerDir.getCanonicalPath();
			
			String path = this.path;
			if(filePath.startsWith(xworkerPath)) {
				path =  filePath.substring(xworkerPath.length(), filePath.length());
			}
			ModuleResource mr = new ModuleResource(this);
			mr.path = path;
			mr.file = file;
			return mr;
		}else {
			return null;
		}
		
	}

	/**
	 * 资源文件。
	 * 
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	public boolean equals(File file) {
		return this.file.equals(file);
	}

	/**
	 * 相对于XWorker根目录的路径。
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	public boolean isLib() {
		return isLib;
	}

	public String getOs() {
		return os;
	}
	
	
}
