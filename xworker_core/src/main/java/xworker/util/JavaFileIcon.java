package xworker.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

public class JavaFileIcon implements IFileIcon{
	static {
		UtilFileIcon.setFileIcon(new JavaFileIcon());
	}

	@Override
	public boolean saveFileIcon(File file, File iconFile) {
		//获取图片
		FileSystemView view = FileSystemView.getFileSystemView();
		//ShellFolder shellFolder = ShellFolder.getShellFolder(file);
		//Image iconImage = shellFolder.getIcon(isBigIcon);
		ImageIcon iconImage = (ImageIcon) view.getSystemIcon(file);
		if(iconImage != null){
			//ImageIcon bigIcon = new ImageIcon(iconImage);
			BufferedImage br = new BufferedImage(iconImage.getIconWidth(), iconImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gc = br.createGraphics();

			gc.drawImage(iconImage.getImage(), 0, 0,iconImage.getIconWidth(), iconImage.getIconHeight(), new Color(0,0,0,0), null);
			try {
				ImageIO.write(br, "gif", iconFile);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		
		return false;
	}

}
