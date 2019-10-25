package xworker.game.cocos2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ResourceActions {
	public static String toJavaScript(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String js = "";
		for(Thing images : self.getChilds("Images")){
			for(Thing image : images.getChilds()){
				js = js + "\r\nvar " + image.getMetadata().getName() + " = \"" + image.getString("resPath") + "\";";
			}
		}
		for(Thing audios : self.getChilds("Audios")){
			for(Thing audio : audios.getChilds()){
				js = js + "\r\nvar " + audio.getMetadata().getName() + " = \"" + audio.getString("resPath") + "\";";
			}
		}
		for(Thing animations : self.getChilds("Animations")){
			for(Thing animation : animations.getChilds()){
				js = js + "\r\nvar " + animation.getMetadata().getName() + " = \"" + animation.getString("resPath") + "\";";
			}
		}
		js = js + "\r\n";
		
		for(Thing resourceGroups : self.getChilds("ResourceGroup")){
			js = js + "\r\nvar " + resourceGroups.getMetadata().getName() + " = [";
			for(int i=0; i<resourceGroups.getChilds().size(); i++){
				if( i != 0){
					js = js + ",";
				}
				js = js + "\r\n";
				Thing res = resourceGroups.getChilds().get(i);
				js = js + "    {src:" + res.getMetadata().getName() + "}";
			}
			js = js + "\r\n];";
		}
		
		return js;
	}
}
