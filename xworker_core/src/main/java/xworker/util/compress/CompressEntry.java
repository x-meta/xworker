package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * 和压缩工具相关的简单Entry。
 * 
 * @author zyx
 *
 */
public interface CompressEntry {
	/**
	 * 返回名称，如果返回null或空，表示改Entry是不需要加入到压缩中的，但不包括子Entrys。
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 向输出流中写入内容。
	 * @param output 要写入的输出流
	 */
	public void write(OutputStream output) throws IOException;
	
	/**
	 * 返回最后修改日期。
	 * 
	 * @return
	 */
	public long getLastModified();
	
	/**
	 * 返回Entry的大小。
	 * 
	 * @return
	 */
	public long getSize();
	
	/**
	 * 返回子Entry，可以为null。
	 * 
	 * @return
	 */
	public Iterator<CompressEntry> getChildsIterator() throws IOException;
	
	
	/** 
	 * 打开Entry。
	 */
	public void open() throws IOException;
	
	/**
	 * 关闭Entry。
	 */
	public void close() throws IOException;
	
	/**
	 * 返回是否是目录。
	 * @return
	 */
	public boolean isDirectory();
	
	/**
	 * 返回CRC32的校验值
	 * 
	 * @return
	 */
	public long getCRC32();
	
	/**
	 * 返回压缩方法，-1表示默认。
	 * 
	 * @return
	 */
	public int getMethod();
}
