package com.adobe.xmp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xworker.util.UtilData;
import xworker.util.UtilData.Index;

public class XWorkerXMPUtils {
	public static List<XMPMeta> parse(byte[] bytes) throws IOException, XMPException{
		Index index = new Index();
		List<XMPMeta> metas = new ArrayList<XMPMeta>();
		while(index.index < bytes.length){
			byte[] bs = UtilData.getBytes(bytes, index, "<x:xmpmeta".getBytes(), "</x:xmpmeta>".getBytes());
			if(bs != null && bs.length != 0){
				//System.out.println(new String(bs));
				//return metas;
				ByteArrayInputStream bin = new ByteArrayInputStream(bs);
				metas.add(XMPMetaFactory.parse(bin));
			}else{
				break;
			}
		}
		
		return metas;
	}
	
	/**
	 * 失败，trim出来的东西不正确现在，不知道为什么base64的xmp数据不正确。
	 * @param str
	 * @return
	 */
	public static String trim(String str){
		byte[] bytes = str.getBytes();
		int index = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		boolean trim = false;
		byte[] startMark = new byte[]{(byte) 0xC3, (byte) 0xBF, (byte) 0xC3, (byte) 0xA1};
		//int count = 0;
		while(index < bytes.length){
			if(trim){
				if(bytes[index] == (byte) 0xC3){
					index = index + 2;
				    trim = false;					
				}else{
					index++;
				}
			}else if(UtilData.isMark(bytes, index, startMark)){
				trim = true;
				byte[] b = new byte[150];
				System.arraycopy(bytes, index, b, 0, b.length);
				System.out.println(new String(b));
				for(int i = 0; i<150; i++){
					System.out.println(i + "=" + new String(new byte[]{ bytes[index + i]}) + ":" + bytes[index + i]);
				}
				index++;
			}else{
				bout.write(bytes[index]);
				index++;
			}
		}
		
		return new String(bout.toByteArray());
	}
}
