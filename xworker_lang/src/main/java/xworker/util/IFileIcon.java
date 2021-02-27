package xworker.util;

import java.io.File;

public interface IFileIcon {
	/**
	 *　获取文件file的图标并保存到iconFile中。
	 * 
	 * @param file
	 * @param iconFile
	 * @return 成功返回true
	 */
	public boolean saveFileIcon(File file, File iconFile);
}
