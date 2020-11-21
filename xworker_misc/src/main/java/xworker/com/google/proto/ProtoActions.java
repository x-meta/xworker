package xworker.com.google.proto;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

public class ProtoActions {
	@SuppressWarnings("resource")
	public static ProtobufMessageFactory getMessageFactory(final ActionContext actionContext) throws NoSuchMethodException, SecurityException, MalformedURLException, ClassNotFoundException {
		final Thing self = actionContext.getObject("self");
		
		String key = "__messageFactory__";			
		ProtobufMessageFactory factory = self.getData(key);
		if(factory == null) {
			String className = self.getStringBlankAsNull("java_package");
			if(className != null) {
				className = className + "." + self.getStringBlankAsNull("java_outer_classname");
			} else {
				className = self.getStringBlankAsNull("java_outer_classname");
			}
			URL[] urls = new URL[] {
					new File(World.getInstance().getPath(), "work/actionClasses/" + self.getMetadata().getThingManager().getName()).toURI().toURL()
					
			};
			ClassLoader loader = new URLClassLoader(urls, World.getInstance().getClassLoader());
			Class<?>[] cls = new Class<?>[] {loader.loadClass(className)};
			factory = new  ProtobufMessageFactory(cls, self, actionContext);					
			self.setData(key, factory);
		}
		
		return factory;
	}
	
	public static Object decodeBytes(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		String name = actionContext.getObject("name");
		byte[] message = actionContext.getObject("message");
		Integer offset = actionContext.getObject("offset");
		Integer length = actionContext.getObject("length");
		
		ProtobufMessageFactory factory = self.doAction("getMessageFactory", actionContext);
		if(factory != null) {
			if(offset != null && length != null) {
				return factory.decoce(name, message, offset, length);	
			} else {
				return factory.decoce(name, message, 0, message.length);
			}
		}else {
			throw new ActionException("ProtobufMessageFactory is null, path=" + self.getMetadata().getPath());
		}
	}
	
	public static Object decodeJson(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		String name = actionContext.getObject("name");
		String message = actionContext.getObject("message");
		
		ProtobufMessageFactory factory = self.doAction("getMessageFactory", actionContext);
		if(factory != null) {
			return factory.decode(name, message);
		}else {
			throw new ActionException("ProtobufMessageFactory is null, path=" + self.getMetadata().getPath());
		}
	}
	
	public static Object newBuilder(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		String name = actionContext.getObject("name");
		
		ProtobufMessageFactory factory = self.doAction("getMessageFactory", actionContext);
		return factory.newBuilder(name);
	}
}
