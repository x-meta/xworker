package xworker.webserver;

import org.apache.commons.io.FileUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;
import xworker.util.GlobalConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class XWorkerWebServer {
    private static final String TAG = XWorkerWebServer.class.getName();

    public static void checkResources() throws IOException {
        File webXml = new File(World.getInstance().getPath() + "/webroot/WEB-INF/web.xml");
        if (webXml.exists()) {
            return;
        } else {
            InputStream resourceIn = World.getInstance().getResourceAsStream("xworker_webroot.zip");
            if (resourceIn == null) {
                throw new ActionException("Can not found xworker_webroot.zip");
            }

            ZipInputStream zin = new ZipInputStream(resourceIn);
            ZipEntry entry = null;
            String webroot = World.getInstance().getPath() + "/webroot/";
            while ((entry = zin.getNextEntry()) != null) {
                if(entry.isDirectory()){
                    continue;
                }

                File outFile = new File(webroot + entry.getName());
                if (outFile.exists() == false) {
                    outFile.getParentFile().mkdirs();
                }

                FileOutputStream fout = new FileOutputStream(outFile);
                byte[] bytes = new byte[4096];
                int length = -1;
                while ((length = zin.read(bytes)) != -1) {
                    fout.write(bytes, 0, length);
                }
                fout.close();

                zin.closeEntry();
            }
            zin.close();
            resourceIn.close();
        }
    }

    public static void run() throws IOException {
        checkResources();

        int httpPort = 9001;

        for (int i = 0; i < 300; i++) {
            Executor.info(TAG, "Check port availabe, port=" + httpPort);
            if (isPortAvailable(httpPort)) {
                Thing webServer = World.getInstance().getThing("xworker.webserver.WebServer");
                String webroot = World.getInstance().getPath() + "/webroot/";
                webServer.doAction("start", new ActionContext(), "port", httpPort, "webroot", webroot);
                Executor.info(TAG, "XWorker webserver start at " + httpPort);
                GlobalConfig.setWebUrl("http://localhost:" + httpPort + "/");
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