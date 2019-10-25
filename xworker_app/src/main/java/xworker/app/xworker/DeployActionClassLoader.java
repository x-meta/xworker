package xworker.app.xworker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.xmeta.World;

public class DeployActionClassLoader extends URLClassLoader {
	List<File> thingManagerDirs = new ArrayList<File>();
	
	public DeployActionClassLoader() {	
		super(new URL[]{}, World.getInstance().getClassLoader());
				
	}
	
	public void init(){
		File dir = new File(World.getInstance().getPath() + "/work/actionClasses/");
		for(File child : dir.listFiles()){
			if(child.isDirectory()){
				boolean have = false;
				for(File f : thingManagerDirs){
					if(child.equals(f)){
						have = true;
						break;
					}
				}
				
				if(!have){
					thingManagerDirs.add(child);
					try {
						this.addURL(child.toURI().toURL());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	@Override
	public InputStream getResourceAsStream(String name) {
		init();
		return super.getResourceAsStream(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		init();
		return super.findClass(name);
	}

	@Override
	public URL findResource(String name) {
		init();
		return super.findResource(name);
	}

	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		init();
		return super.findResources(name);
	}

	@Override
	protected PermissionCollection getPermissions(CodeSource codesource) {
		init();
		return super.getPermissions(codesource);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		init();
		return super.loadClass(name);
	}

	@Override
	public URL getResource(String name) {
		init();
		return super.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		init();
		return super.getResources(name);
	}


	static DeployActionClassLoader loader = new DeployActionClassLoader();
	public static Class<?> getClass(String className) throws ClassNotFoundException{
		return loader.findClass(className);
	}
	
	public static DeployActionClassLoader getInstance(){
		return loader;
	}
}
