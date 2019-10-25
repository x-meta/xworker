package xworker.html.module;

public class ModuleRequire {
	String module;
	String version;
	
	public ModuleRequire(String module, String version) {
		this.module = module;
		this.version = version;
	}
	
	public boolean equals(String module, String version) {
		if(this.module.equals(module)) {
			if(this.version != null) {
				return this.version.equals(version);
			}else {
				return version == null;
			}
		}
		
		return false;
	}

	public String getModule() {
		return module;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "ModuleRequire [module=" + module + ", version=" + version + "]";
	}
	
	
	
}
