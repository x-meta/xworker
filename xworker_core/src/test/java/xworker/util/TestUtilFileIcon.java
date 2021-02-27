package xworker.util;

import java.io.File;
import java.io.IOException;

public class TestUtilFileIcon {
	public static void main(String[] args) {
		try {
			UtilFileIcon.getFileIcon(new File("abc.java"), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
