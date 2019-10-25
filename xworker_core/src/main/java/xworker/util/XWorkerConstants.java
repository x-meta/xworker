package xworker.util;

public class XWorkerConstants {
	/**
	 * XMeta引擎的类库的前缀，用于从xworker/libs目录下搜寻相关类库。
	 */
	public static final String[] XMETA_LIBS = new String[]{"org.xmeta.engine", "slf4j-log4j", "slf4j-api", "ognl", "log4j-", "javassist"};
	
	/**
	 * Web的类库，用于打包到war中，用于从xworker/lib目录下搜寻。
	 */
	public static final String[] WEB_FORWAR_LIBS = new String[]{"xworker_web_forwar"};
}
