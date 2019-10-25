package xworker.org.jedis;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xworker.lang.context.ActionListener;

public class JedisPoolContext {
	public static void doInit(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");		
		ActionListener listener = actionContext.getObject("listener");
		
		Thing poolThing = self.doAction("getJedisPool", actionContext);
		if(poolThing != null) {
			Jedis jedis = null;
			ActionListener inheritContext = listener.getActionListener(poolThing);
			if(inheritContext != null) {
				jedis = (Jedis) inheritContext.getFromMe("jedis");
			}else {
				//获取Jedis
				JedisPool jedisPool = poolThing.doAction("getJedisPool", actionContext) ;
				jedis = jedisPool.getResource();
				
				//保存动作监听器（动作上下文)的变量
				listener.putToMe("jedisPool", jedisPool);				
			}
			listener.putToMe("jedis", jedis);
			
			//保存jedis到本监听器中
			String name = self.getMetadata().getName();
			listener.putToAc(name, jedis);
		}
	}
	
	public static void doSuccess(ActionContext actionContext) {
		//释放有本动作上下文创建的jedis
		ActionListener listener = actionContext.getObject("listener");
		JedisPool jedisPool = (JedisPool) listener.getFromMe("jedisPool");
		Jedis jedis = (Jedis) listener.getFromMe("jedis");
		if(jedisPool != null && jedis != null) {
			//如果jedisPool不为null表示是本上下文创建的
			jedis.close();
		}
	}
	
	public static void doException(ActionContext actionContext) {
		doSuccess(actionContext);
	}
}
