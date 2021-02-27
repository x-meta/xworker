package xworker.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardUtils {
	public static void setSystemClipboard(String refContent) {
		// 设置为static是为了直接使用，不用new一个该类的实例即可直接使用,即定义的: 类名.方法名
		String vc = refContent.trim();
		StringSelection ss = new StringSelection(vc);

		Clipboard sysClb = null;
		sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
		sysClb.setContents(ss, null);

		// Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,
		// null); //跟上面三行代码效果相同
	}

	public static String getSystemClipboard() {// 获取系统剪切板的文本内容[如果系统剪切板复制的内容是文本]
		Clipboard sysClb = null;
		sysClb = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = sysClb.getContents(null);
		// Transferable t =
		// Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		// //跟上面三行代码一样
		try {
			if (null != t && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch (UnsupportedFlavorException e) {
			// System.out.println("Error tip: "+e.getMessage());
		} catch (IOException e) {
		} // System.out.println("Error tip: "+e.getMessage());
		return null;
	}

	public static void setImageClipboard(Image image) {
		ImageSelection imgSel = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(imgSel, null);
	}

	public static class ImageSelection implements Transferable {
		private Image image;

		public ImageSelection(Image image) {
			this.image = image;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
		}
	}

	public static Image getImageClipboard() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);
		try {
			if (null != t && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				Image image = (Image) t.getTransferData(DataFlavor.imageFlavor);
				return image;
			}
		} catch (UnsupportedFlavorException e) {
			// System.out.println("Error tip: "+e.getMessage());
		} catch (IOException e) {
			// System.out.println("Error tip: "+e.getMessage());
		}
		return null;
	}
}
