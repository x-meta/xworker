package xworker.javafx.util;

import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import xworker.lang.executor.Executor;
import xworker.util.UtilFileIcon;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.chrono.*;
import java.util.Map;
import java.util.WeakHashMap;

public class JavaFXUtils {
	private static final String TAG = JavaFXUtils.class.getName();
	private static final Map<String, Image> imageCahe = new WeakHashMap<>();

	public static void showThingDesc(Thing thing, WebView webView){
		if(thing == null || webView == null){
			return;
		}

		webView.getEngine().loadContent(XWorkerUtils.getThingDesc(thing));
	}

	public static Color getColor(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof Color){
			return (Color) obj;
		}else if(obj instanceof  String){
			return Color.valueOf((String) obj);
		}else{
			return null;
		}
	}

	public static Duration getDuration(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof  Duration){
			return (Duration) obj;
		}else if(obj instanceof Double){
			return new Duration((Double) obj);
		}else if(obj instanceof  String){
			return new Duration(Double.parseDouble((String) obj));
		}else{
			return null;
		}
	}

	public static Media getMedia(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof Media){
			return (Media) obj;
		}else if(obj instanceof  String){
			return new Media((String) obj);
		}else{
			return null;
		}
	}

	public static Image getImage(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof Image){
			return (Image) obj;
		}else if(obj instanceof  String){
			return getImage((String) obj);
		}else if(obj instanceof InputStream){
			return new Image((InputStream) obj);
		}else{
			return null;
		}
	}

	/**
	 * 返回一个模型
	 * @param thing
	 * @return
	 */
	public static Image getImage(Thing thing){
		String icon = thing.getStringBlankAsNull("icon");
		if(icon == null){
			for(Thing descriptor : thing.getAllDescriptors()){
				//println descriptor.getMetadata().getPath();
				icon = descriptor.getString("icon");
				//println icon;
				if(icon != null && !"".equals(icon)){
					break;
				}
			}
		}
		if(icon != null){
			return JavaFXUtils.getImage(icon);
		}else{
			return null;
		}
	}

	public static Image getImage(String imagePath){
		Image image = imageCahe.get(imagePath);
		if(image != null){
			return image;
		}

		try {
			image = new Image(imagePath);
		}catch(Exception e){
			try {
				InputStream in = World.getInstance().getResourceAsStream(imagePath);
				image = new Image(in);
			}catch(Exception ee){
				Executor.warn(TAG, "Can not create image, path=" + imagePath);
				return null;
			}
		}

		imageCahe.put(imagePath, image);
		return image;
	}

	public static Image getImage(File file) {
		if(file.isDirectory()){
			return getImage("icons/folder.png");
		}

		try {
			String icon = UtilFileIcon.getFileIcon(file, false);
			return getImage(icon);
		} catch (IOException e) {
			return getImage("icons/page_white.png");
		}

	}
		
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Thing thing, String name, ActionContext actionContext) {
		String value  = thing.getStringBlankAsNull(name);
		if(value != null) {
			return (T) UtilData.getData(value, actionContext);
		}else{
			return null;
		}
	}

	public static Insets getInsets(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof Insets){
			return (Insets) obj;
 		}
		if(!(obj instanceof String)){
			return null;
		}
		String value = (String) obj;
		if(value == null || "".equals(value.trim())){
			return null;
		}

		String[] strs = value.split("[,]");
		if(strs.length == 1) {
			return new Insets(Double.parseDouble(strs[0]));
		}else{
			return new Insets(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]), Double.parseDouble(strs[3]));
		}
	}

	public static String getString(Thing thing, String name, ActionContext actionContext){
		try {
			return xworker.util.UtilData.getString(thing, name, actionContext);
		}catch(Exception e){
			Executor.warn(TAG, "Get string '" + name + "' exception, path=" + thing.getMetadata().getPath(), e);
			return thing.getString(name);
		}
	}

	public static Font getFont(String value, ActionContext actionContext) {
		if(value == null) {
			return null;
		}
		
		if(value.indexOf(":") != -1) {
			Object obj = UtilData.getData(value, actionContext);
			if(obj instanceof Font) {
				return (Font) obj;
			}else if(obj instanceof String) {
				value = (String) obj;
			}else {
				return null;
			}
		}
		
		try {
			int index = value.indexOf("|");
			if(index !=-1) {
				
				String name = value.substring(0, index);
				double size = Double.parseDouble(value.substring(index + 1, value.length()));
				return new Font(name, size);
				
			}else {
				double size = Double.parseDouble(value);
				return new Font(size);
			}
		
		}catch(Exception e) {
			Executor.warn(TAG, "Parse font error, value=" + value, e);
		}
		
		return null;
	}
	
	public static Chronology getChronology(String value) {
		if("HijrahChronology".equals(value)) {
			return HijrahChronology.INSTANCE;
		}else if("IsoChronology".equals(value)) {
			return IsoChronology.INSTANCE;
		}else if("JapaneseChronology".equals(value)) {
			return JapaneseChronology.INSTANCE;
		}else if("MinguoChronology".equals(value)) {
			return MinguoChronology.INSTANCE;
		}else if("ThaiBuddhistChronology".equals(value)) {
			return ThaiBuddhistChronology.INSTANCE;
		}
		
		return null;
	}
	
	public static SceneAntialiasing getSceneAntialiasing(String value) {
		if("BALANCED".equals(value)) {
			return SceneAntialiasing.BALANCED;
		}else if("DISABLED".equals(value)) {
			return SceneAntialiasing.DISABLED;
		}else {
			return null;
		}
	}
	
	public static Point3D getPoint3D(String value) {
		if(value == null || "".equals(value)) {
			return null;
		}
		if("X_AXIS".equals(value)){
			return Rotate.X_AXIS;
		}else if("Y_AXIS".equals(value)){
			return Rotate.Y_AXIS;
		}else if("Z_AXIS".equals(value)){
			return Rotate.Z_AXIS;
		}
		
		double x, y, z = 0;
		String vs[] = value.split("[,]");
		x = Double.parseDouble(vs[0]);
		y = Double.parseDouble(vs[1]);
		z = Double.parseDouble(vs[2]);
		
		return new Point3D(x, y, z);
	}
	
	public static Cursor getCursor(String name) {
		if ("CLOSED_HAND".equals(name)) {
			return Cursor.CLOSED_HAND;
		}
		if ("CROSSHAIR".equals(name)) {
			return Cursor.CROSSHAIR;
		}
		if ("DEFAULT".equals(name)) {
			return Cursor.DEFAULT;
		}
		if ("DISAPPEAR".equals(name)) {
			return Cursor.DISAPPEAR;
		}
		if ("E_RESIZE".equals(name)) {
			return Cursor.E_RESIZE;
		}
		if ("H_RESIZE".equals(name)) {
			return Cursor.H_RESIZE;
		}
		if ("HAND".equals(name)) {
			return Cursor.HAND;
		}
		if ("MOVE".equals(name)) {
			return Cursor.MOVE;
		}
		if ("N_RESIZE".equals(name)) {
			return Cursor.N_RESIZE;
		}
		if ("NE_RESIZE".equals(name)) {
			return Cursor.NE_RESIZE;
		}
		if ("NONE".equals(name)) {
			return Cursor.NONE;
		}
		if ("NW_RESIZE".equals(name)) {
			return Cursor.NW_RESIZE;
		}
		if ("OPEN_HAND".equals(name)) {
			return Cursor.OPEN_HAND;
		}
		if ("S_RESIZE".equals(name)) {
			return Cursor.S_RESIZE;
		}
		if ("SE_RESIZE".equals(name)) {
			return Cursor.SE_RESIZE;
		}
		if ("SW_RESIZE".equals(name)) {
			return Cursor.SW_RESIZE;
		}
		if ("TEXT".equals(name)) {
			return Cursor.TEXT;
		}
		if ("V_RESIZE".equals(name)) {
			return Cursor.V_RESIZE;
		}
		if ("W_RESIZE".equals(name)) {
			return Cursor.W_RESIZE;
		}
		if ("WAIT".equals(name)) {
			return Cursor.WAIT;
		}
		
		return null;
	}
	
	public static KeyCombination getKeyCombination(String name) {
		return KeyCombination.valueOf(name);
	}

	public static Rectangle2D getRectangle2D(Thing thing, String name, ActionContext actionContext){
		Object obj = getObject(thing, name, actionContext);
		if(obj instanceof Rectangle2D){
			return (Rectangle2D) obj;
		}
		if(!(obj instanceof String)){
			return null;
		}
		String value = (String) obj;
		if(value == null || "".equals(value.trim())){
			return null;
		}

		String[] strs = value.split("[,]");
		return new Rectangle2D(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]), Double.parseDouble(strs[3]));
	}

	public static Node getGraphic(Thing thing, String name, ActionContext actionContext){
		Object graphic = JavaFXUtils.getObject(thing, name, actionContext);
		if(graphic instanceof Node){
			return (Node) graphic;
		}else if(graphic instanceof  String){
			try{
				return new ImageView((String) graphic);
			}catch(Exception e){
				try {
					InputStream in = World.getInstance().getResourceAsStream((String) graphic);
					if (in != null) {
						Image image = new Image(in);
						in.close();
						return new ImageView(image);
					}
				}catch(Exception ee){
				}
				Executor.warn(TAG, "Create graphic from string  as imageview error, path=" + thing.getMetadata().getPath(), e);
			}
		}else if(graphic instanceof InputStream){
			try{
				Image image = new Image((InputStream) graphic);
				return new ImageView(image);
			}catch(Exception e){
				Executor.warn(TAG, "Create graphic from inputstream as imageview error, path=" + thing.getMetadata().getPath(), e);
			}
		}

		Executor.warn(TAG, "Can not careate graphic,name=" + name + ", path=\" + thing.getMetadata().getPath()");
		return null;
	}
}
