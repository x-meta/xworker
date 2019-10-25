package xworker.manong;

import org.xmeta.Thing;
import org.xmeta.World;

public class MaNongI18n {
	public static String getMessage(String id){
		Thing i18n = World.getInstance().getThing("xworker.manong.MaNongI18n/@" + id);
		if(i18n == null){
			throw new MaNongException("事物xworker.manong.MaNongI18n/@" + id + "不存在！");
		}

		return i18n.getMetadata().getLabel();
	}
}
