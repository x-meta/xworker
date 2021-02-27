package xworker.com.google.proto;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.googlecode.protobuf.format.JsonFormat;

public class ProtobufMessageFactory {
	Thing thing;
	ActionContext actionContext;
	
	Map<String, Method> builders = new HashMap<String, Method>();	
	Map<String, Method> liteBuilders = new HashMap<String, Method>();
	
    /**
     * Creates a new instance.
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    public ProtobufMessageFactory(Thing thing, ActionContext actionContext) throws NoSuchMethodException, SecurityException {
    	this.thing = thing;
    	this.actionContext = actionContext;
    	
    	Class<?>[] classes = thing.doAction("getClasses", actionContext);
    	if(classes != null) {
    		init(classes);
    	}
    }
    
    public  ProtobufMessageFactory(Class<?>[] classes, Thing thing, ActionContext actionContext) throws NoSuchMethodException, SecurityException {
    	this.thing = thing;
    	this.actionContext = actionContext;
    	
    	if(classes != null) {
    		init(classes);
    	}
    }

    public Object decode(String messageType, String json) throws Exception {
    	Object objBuilder = newBuilder(messageType);
    	JsonFormat format = new JsonFormat();
    	ByteArrayInputStream bin = new ByteArrayInputStream(json.getBytes());
    	if(objBuilder instanceof com.google.protobuf.AbstractMessage.Builder<?>) {
    		com.google.protobuf.AbstractMessage.Builder<?> builder = (com.google.protobuf.AbstractMessage.Builder<?>) objBuilder;
    		format.merge(bin, builder);    		
    		return builder.build();
    	}else if(objBuilder instanceof com.google.protobuf.AbstractMessageLite.Builder<?,?>) {
    		throw new ActionException("JsonFormat not support MessageLite, builder=" + objBuilder);
    		//com.google.protobuf.AbstractMessageLite.Builder<?,?> builder = (com.google.protobuf.AbstractMessageLite.Builder<?,?>) objBuilder;
    		//format.merge(bin, builder);    		
    		//return builder.build();
    	}else {
    		throw new ActionException("Unkown builder type, builder=" + objBuilder);
    	}
    }
    
    public Object decoce(String messageType, byte[] array, int offset, int length) throws Exception {
    	Object objBuilder = newBuilder(messageType);
    	
    	if(objBuilder instanceof com.google.protobuf.AbstractMessage.Builder<?>) {
    		com.google.protobuf.AbstractMessage.Builder<?> builder = (com.google.protobuf.AbstractMessage.Builder<?>) objBuilder;
    		builder.mergeFrom(array, offset, length);
    		return builder.build();
    	}else if(objBuilder instanceof com.google.protobuf.AbstractMessageLite.Builder<?,?>) {
    		com.google.protobuf.AbstractMessageLite.Builder<?,?> builder = (com.google.protobuf.AbstractMessageLite.Builder<?,?>) objBuilder;
    		builder.mergeFrom(array, offset, length);
    		return builder.build();
    	}else {
    		throw new ActionException("Unkown builder type, builder=" + objBuilder);
    	}
    }
    
    public Object newBuilder(String messageType) throws Exception{
    	Method build = builders.get(messageType);
    	if(build == null) {
    		build = liteBuilders.get(messageType);
    	}
    	if(build == null) {
    		Class<?> cls = Class.forName(messageType);
    		if(isMessage(cls)) {
				build = cls.getMethod("newBuilder", new Class<?>[] {});
				builders.put(cls.getName(), build);
				builders.put(cls.getSimpleName(), build);
			}else if(isMessageLite(cls)) {
				build = cls.getMethod("newBuilder", new Class<?>[] {});
				liteBuilders.put(cls.getName(), build);
				liteBuilders.put(cls.getSimpleName(), build);
			}
    	}
    	
    	if(build != null) {
    		return build.invoke(null, new Object[] {});
    	}else {
    		return null;
    	}
    }
    
	public static boolean isMessage(Class<?> cls) {		
		Class<AbstractMessage> messageClass = AbstractMessage.class;
		if(cls == null) {
			return false;
		}
		
		if(cls.getName().equals(messageClass.getName())) {
			return true;
		}
		
		return isMessage(cls.getSuperclass());
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isMessageLite(Class<?> cls) {		
		Class<AbstractMessageLite> messageClass = AbstractMessageLite.class;
		if(cls == null) {
			return false;
		}
		
		if(cls.getName().equals(messageClass.getName())) {
			return true;
		}
		
		return isMessage(cls.getSuperclass());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void init(Class<?>[] classes) throws NoSuchMethodException, SecurityException {
    	for(Class cls : classes) {
    		for(Class messageCls : cls.getDeclaredClasses()) {
    			try {
	    			if(isMessage(messageCls)) {
	    				Method build = messageCls.getMethod("newBuilder", new Class<?>[] {});
	    				builders.put(messageCls.getName(), build);
	    				builders.put(messageCls.getSimpleName(), build);
	    			}else if(isMessageLite(messageCls)) {
	    				Method build = messageCls.getMethod("newBuilder", new Class<?>[] {});
	    				liteBuilders.put(messageCls.getName(), build);
	    				liteBuilders.put(messageCls.getSimpleName(), build);
	    			}
    			}catch(Exception e) {
    				
    			}
    			//if(messageCls.is)
    		}
    	}
    }
}
