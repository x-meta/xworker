package xworker.org.apache.maven;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import java.io.File;

public class MavenActions {
    public static Object copyPomDependenciesToDir(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String cmdarray = self.doAction("getMvnCommand", actionContext) +
                "\ndependency:copy-dependencies\n-f\n" +
                self.doAction("getPomFile", actionContext) +
                "\n-DoutputDirectory=" +
                self.doAction("getOutputDirectory", actionContext);

        //println cmdarray;
        Object timeout = self.doAction("getTimeout", actionContext);
        Object sync = self.doAction("isSync", actionContext);

        Action exec = actionContext.getObject("exec");
        return exec.run(actionContext, "cmdarray", cmdarray, "timeout", timeout, "sync", sync);
    }

    public static Object copyDependenciesToDir(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //依赖
        String dependencies = self.doAction("getDependencies", actionContext);

        //输出目录
        File outputDir = self.doAction("getOutputDir", actionContext);
        if(outputDir.exists() == false){
            outputDir.mkdirs();
        }

        //是否拷贝源码
        Object copySource = self.doAction("isCopySource", actionContext);

        Action shortenPath = actionContext.getObject("shortenPath");
        String path = shortenPath.run(actionContext, "thing", self);
        path = path.replace('.', '/');
        World world = World.getInstance();
        path = world.getPath() + "/work/maven/" + path + ".pom";
        File pomOutFile = new File(path);
        pomOutFile.getParentFile().mkdirs();

        //生成pom
        Action toPom = actionContext.getObject("toPom");
        toPom.run(actionContext, "dependencies", dependencies, "thing", self,
                "output", pomOutFile.getAbsolutePath(), "copySource", copySource,
                "libOutout", outputDir.getAbsolutePath());

        //执行pom
        //def mvnPath = self.doAction("getMvnPath", actionContext);
        //def cmd = mvnPath + "\npackage \n-f \n" + pomOutFile.getAbsolutePath();

        String cmd = self.doAction("getMvnCommand", actionContext) +
                "\ndependency:copy-dependencies\n-f\n" +
                pomOutFile.getAbsolutePath() +
                "\n-DoutputDirectory=" +
                outputDir.getAbsolutePath();

        Action exec = actionContext.getObject("exec");
        return exec.run(actionContext, "cmd", cmd);
    }
}
