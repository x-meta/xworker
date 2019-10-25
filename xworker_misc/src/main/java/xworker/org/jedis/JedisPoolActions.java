package xworker.org.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import redis.clients.jedis.JedisPool;

public class JedisPoolActions {
	private static final String KEY = "__JedisPoolActions__";
	
	public static JedisPool getJedisPool(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingEntry thingEntry = self.getStaticData(KEY);
		if(thingEntry == null) {
			thingEntry = new ThingEntry(self);
			self.setStaticData(KEY, thingEntry);
		}
		
		if(thingEntry.isChanged() || thingEntry.getData() == null) {
			//如果之前就有先关闭
			JedisPool pool = (JedisPool) thingEntry.getData();
			if(pool != null) {
				pool.close();
			}
			
			String host = self.doAction("getHost", actionContext);
			int port = self.doAction("getPort", actionContext);
			int timeout = self.doAction("getTimeout", actionContext);
			String password = self.doAction("getPassword", actionContext);
			int database = self.doAction("getDatabase", actionContext);
			
			GenericObjectPoolConfig  config = new GenericObjectPoolConfig ();
			pool = new JedisPool(config, host, port, timeout, password, database);
			thingEntry.setData(pool);
			
			return pool;
		}else {
			return (JedisPool) thingEntry.getData();
		}
	}
}
