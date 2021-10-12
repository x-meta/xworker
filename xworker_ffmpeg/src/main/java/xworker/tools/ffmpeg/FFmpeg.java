package xworker.tools.ffmpeg;

import xworker.lang.util.Config;

public class FFmpeg {
    public static String getFFmpegPath(){
        return Config.getConfig("FFmpegConfig", "xworker.tools.ffmpeg.path");
    }

    public static void setFFmpegPath(String path){
        Config.setConfig("FFmpegConfig", "xworker.tools.ffmpeg.path", path);
    }
}
