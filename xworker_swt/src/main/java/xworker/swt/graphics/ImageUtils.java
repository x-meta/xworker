package xworker.swt.graphics;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class ImageUtils {
	public static String[] supportImageFiles = new String[]{"bmp", "ico", "jpg", "jpeg", "gif", "png", "tiff"};
	/**
	 * 剪切一段图片。
	 * 
	 * @param src
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image clip(Image src, int x, int y , int width, int height){
		ImageData srcData = src.getImageData();
		ImageData dstData = new ImageData(width, height, srcData.depth, srcData.palette);		
		dstData.transparentPixel = srcData.transparentPixel;
		dstData.alpha = srcData.alpha;
		
		for(int sx=0; sx<width; sx++){
			for(int sy=0; sy < height; sy++){
				int alpha = srcData.getAlpha(sx + x, sy + y);				
				dstData.setAlpha(sx, sy, alpha);
				
				int pixelValue = srcData.getPixel(sx + x,  sy + y);
				dstData.setPixel(sx, sy, pixelValue);
			}
		}
		
		return new Image(src.getDevice(), dstData);
	}
	
	/**
	 * 逆时针旋转90度。
	 * 
	 * @param src
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image rotate(Image src, int x, int y , int width, int height){
		ImageData srcData = src.getImageData();
		ImageData dstData = new ImageData(height, width, srcData.depth, srcData.palette);		
		dstData.transparentPixel = srcData.transparentPixel;
		dstData.alpha = srcData.alpha;
		
		for(int sx=0; sx<width; sx++){
			for(int sy=0; sy < height; sy++){
				int alpha = srcData.getAlpha(sx + x, sy + y);				
				dstData.setAlpha(sy, sx, alpha);
				
				int pixelValue = srcData.getPixel(sx + x,  sy + y);
				dstData.setPixel(sy, sx, pixelValue);
			}
		}
		
		return new Image(src.getDevice(), dstData);
	}
	
	/**
	 * 根据文件名来判断是否是系统支持的格式。
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isSupportImageFile(File file){
		String fileName = file.getName();
		int lastIndex = fileName.lastIndexOf(".");
		if(lastIndex != -1){
			String ext = fileName.substring(lastIndex + 1, fileName.length());
			ext = ext.toLowerCase();
			for(String s : supportImageFiles){
				if(s.equals(ext)){
					return true;
				}
			}
		}
		
		return false;
	}
}
