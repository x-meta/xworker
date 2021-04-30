package xworker.ide;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ChangeAllMavenProjectsParent {
    public static void main(String[] args){
        try{
            File dir = new File(".");
            for(File child : dir.listFiles()){
                File pomFile = new File(child, "pom.xml");
                if(pomFile.exists()){
                    String xml = FileUtils.readFileToString(pomFile, "utf-8");
                    int index1 = xml.indexOf("<artifactId>xworker_parent</artifactId>");
                    if(index1 != -1){
                        index1 = xml.indexOf("<version>", index1) + "<version>".length();
                        int index2 = xml.indexOf("</version>", index1);
                        System.out.println(pomFile.getAbsolutePath() + " : " + (xml.substring(index1, index2)));
                        xml = xml.substring(0, index1) + "1.0.1-SNAPSHOT" + xml.substring(index2, xml.length());
                        System.out.println(xml);
                        FileUtils.write(pomFile, xml, "utf-8");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
