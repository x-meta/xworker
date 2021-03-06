package xworker.javafx.scene.media;

import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class MediaPlayerActions {
    public static void init(MediaPlayer node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("audioSpectrumInterval")){
            node.setAudioSpectrumInterval(thing.getDouble("audioSpectrumInterval"));
        }
        if(thing.valueExists("audioSpectrumListener")){
            AudioSpectrumListener audioSpectrumListener = JavaFXUtils.getObject(thing, "audioSpectrumListener", actionContext);
            if(audioSpectrumListener != null) {
                node.setAudioSpectrumListener(audioSpectrumListener);
            }
        }
        if(thing.valueExists("audioSpectrumNumBands")){
            node.setAudioSpectrumNumBands(thing.getInt("audioSpectrumNumBands"));
        }
        if(thing.valueExists("audioSpectrumThreshold")){
            node.setAudioSpectrumThreshold(thing.getInt("audioSpectrumThreshold"));
        }
        if(thing.valueExists("autoPlay")){
            node.setAutoPlay(thing.getBoolean("autoPlay"));
        }
        if(thing.valueExists("balance")){
            node.setBalance(thing.getDouble("balance"));
        }
        if(thing.valueExists("cycleCount")){
            node.setCycleCount(thing.getInt("cycleCount"));
        }
        if(thing.valueExists("mute")){
            node.setMute(thing.getBoolean("mute"));
        }
        if(thing.valueExists("rate")){
            node.setRate(thing.getDouble("rate"));
        }
        if(thing.valueExists("startTime")){
            Duration startTime = JavaFXUtils.getObject(thing, "startTime", actionContext);
            if(startTime != null) {
                node.setStartTime(startTime);
            }
        }
        if(thing.valueExists("stopTime")){
            Duration stopTime = JavaFXUtils.getObject(thing, "stopTime", actionContext);
            if(stopTime != null) {
                node.setStopTime(stopTime);
            }
        }
        if(thing.valueExists("volume")){
            node.setVolume(thing.getDouble("volume"));
        }
    }

    public static MediaPlayer create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Media media = JavaFXUtils.getMedia(self, "media", actionContext);
        MediaPlayer mediaPlayer = null;
        if(media != null){
            mediaPlayer = new MediaPlayer(media);
            init(mediaPlayer, self, actionContext);
            actionContext.g().put(self.getMetadata().getName(), mediaPlayer);
        }

        return mediaPlayer;
    }
}
