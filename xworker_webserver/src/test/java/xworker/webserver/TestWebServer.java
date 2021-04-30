package xworker.webserver;

import org.xmeta.World;

public class TestWebServer {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            xworker.webserver.XWorkerWebServer.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
