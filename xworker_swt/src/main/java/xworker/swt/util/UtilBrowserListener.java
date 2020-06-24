package xworker.swt.util;

public interface UtilBrowserListener {
	/**
	 * 处理浏览器传来的事件，如果返回true表示UtilBrowser不再处理了，如果false使用UtilBrowser的默认处理方法。
	 * 
	 * @param message
	 * @return
	 */
	public boolean handleBrowserMessage(String message) ;
}
