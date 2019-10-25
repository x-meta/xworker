package xworker.ide;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XWorkerXMPUtils;

public class TestXMP {
	public static void main(String args[]){
		try{
			byte[] bytes = FileUtils.readFileToByteArray(new File("e:/temp/IMG_20160501_151728.vr.jpg"));
			List<XMPMeta> metas = XWorkerXMPUtils.parse(bytes);
			
			XMPMeta meta = metas.get(1);
			String bs  = meta.getPropertyString("http://ns.google.com/photos/1.0/image/", "Data");
			//bs = XWorkerXMPUtils.trim(bs);
			FileOutputStream fout = new FileOutputStream(new File("e:/temp/vr.jpg"));
			fout.write(bs.getBytes());
			//fout.write(bs.getBytes());
			fout.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
