package xworker.webserver;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;
import xworker.util.GlobalConfig;
import xworker.util.XWorkerUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class XWorkerWebServer {
    private static final String TAG = XWorkerWebServer.class.getName();


    public static void run() throws IOException {
        if(XWorkerUtils.hasWebServer()){
            //WebServer已经启动了
            return;
        }

        int httpPort = 9001;

        for (int i = 0; i < 300; i++) {
            Executor.info(TAG, "Check port availabe, port=" + httpPort);
            if (isPortAvailable(httpPort)) {
                Thing webServer = World.getInstance().getThing("xworker.webserver.WebServer");
                String webroot = World.getInstance().getPath() + "/webroot/";
                webServer.doAction("start", new ActionContext(), "port", httpPort, "webroot", webroot);
                Executor.info(TAG, "XWorker webserver start at " + httpPort);
                if(webServer.getBoolean("ssl")) {
                    GlobalConfig.setWebUrl("https://localhost:" + httpPort + "/");
                }else{
                    GlobalConfig.setWebUrl("http://localhost:" + httpPort + "/");
                }
                GlobalConfig.setHttpPort(httpPort);
                return;
            }else{
                httpPort ++;
            }
        }
    }

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    public static boolean isPortAvailable(int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}