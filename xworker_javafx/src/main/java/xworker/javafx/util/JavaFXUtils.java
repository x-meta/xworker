package xworker.javafx.util;

import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import xworker.lang.executor.Executor;

public class JavaFXUtils {
	private static final String TAG = JavaFXUtils.class.getName();
		
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Thing thing, String name, ActionContext actionContext) {
		return (T) UtilData.getData(thing.getString(name), actionContext);
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
}
