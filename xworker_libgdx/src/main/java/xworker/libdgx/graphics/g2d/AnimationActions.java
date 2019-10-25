package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import ognl.OgnlException;

public class AnimationActions {
	@SuppressWarnings("unchecked")
	public static Animation create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Array<TextureRegion> rs = new Array<TextureRegion>();
		Object regions = UtilData.getObject(self, "textregions", actionContext);
		int arrayStart = self.getInt("arrayStart", 0, actionContext);
		int arrayEnd = self.getInt("arrayEnd", -1, actionContext);
		int arrayIndex = self.getInt("arrayIndex", 0, actionContext);
		
		if(regions instanceof Array){
			Array<TextureRegion> aregions = (Array<TextureRegion>) regions;
			if(arrayEnd != -1){
				for(int i=arrayStart; i<=arrayEnd; i++){
					rs.add(aregions.get(i));
				}
			}else{
				rs = aregions;
			}
		}else if(regions instanceof TextureRegion[]){
			TextureRegion[] aregions = (TextureRegion[]) regions;
			for(int i=arrayStart; i<aregions.length; i++){
				if(arrayEnd == -1 || i <= arrayEnd){
					rs.add(aregions[i]);
				}
			}
		}else if(regions  instanceof TextureRegion[][]){
			TextureRegion[][] aregions = (TextureRegion[][]) regions;
			for(int i=arrayStart; i<aregions[arrayIndex].length; i++){
				if(arrayEnd == -1 || i <= arrayEnd){
					rs.add(aregions[arrayIndex][i]);
				}
			}
		}else if(regions instanceof TextureRegion){
			rs.add((TextureRegion) regions);
		}
		
		String playMode = self.getStringBlankAsNull("playMode");
		PlayMode pm = PlayMode.NORMAL;
		if("LOOP".equals(playMode)){
			pm = PlayMode.LOOP;
		}else if("LOOP_PINGPONG".equals(playMode)){
			pm = PlayMode.LOOP_PINGPONG;
		}else if("LOOP_RANDOM".equals(playMode)){
			pm = PlayMode.LOOP_RANDOM;
		}else if("LOOP_REVERSED".equals(playMode)){
			pm = PlayMode.LOOP_REVERSED;
		}else if("NORMAL".equals(playMode)){
			pm = PlayMode.NORMAL;
		}else if("REVERSED".equals(playMode)){
			pm = PlayMode.REVERSED;
		}
	
		Animation an = new Animation(self.getFloat("frameDuration", 0f, actionContext), rs, pm);
		actionContext.getScope(0).put(self.getMetadata().getName(), an);
		return an;
	}
}
