package xworker.app.xworker;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader{

	public RemoteClassLoader(ClassLoader parent, URL[] urls) {
		super(urls, parent);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try{
			return super.findClass(name);
		}catch(ClassNotFoundException e){
			try {
				File file = DeployHttpClientManager.getClass(name);
				if(!file.getName().endsWith(".class")){
					this.addURL(file.toURI().toURL());
				}
				return super.findClass(name);
			} catch (Exception ee) {
				throw e;
			} 		
		}
	}

	@Override
	public URL findResource(String name) {
		URL url = super.findResource(name);
		if(url == null){
			try {
				return DeployHttpClientManager.getResource(name);
			} catch (Exception e) {
				return null;
			} 
		}else{
			return url;
		}
	}

}
