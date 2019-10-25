package xworker.swt.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Thing;
import org.xmeta.codes.XmlCoder;

public class ThingTransfer extends ByteArrayTransfer {
	private static Logger logger = LoggerFactory.getLogger(ThingTransfer.class);
	
	private static String TYPENAME = "Thing";
	private static int TYPEID = registerType(TYPENAME);
	private static ThingTransfer instance = new ThingTransfer();
		
	private ThingTransfer() {
	}
	
	public static ThingTransfer getInstance(){
		return instance;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof Thing))
			return;

		if (isSupportedType(transferData)) {
			Thing things = (Thing) object;
			try {
				// write data to a byte array and then ask super to convert to
				// pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				Thing thing = (Thing) things;
				String xml = XmlCoder.encodeToString(thing);
				byte[] bytes = xml.getBytes("utf-8");
				writeOut.write(bytes);

				byte[] buffer = out.toByteArray();
				writeOut.close();

				super.javaToNative(buffer, transferData);
			} catch (IOException e) {
				logger.error("javaToNative error", e);
			}
		}
	}

	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				Thing thing = new Thing();
				XmlCoder.parse(thing, in);
				
				return thing;
			} catch (Exception ex) {
				logger.error("nativeToJava error", ex);
				return null;
			}
		}

		return null;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPENAME };
	}

}
