package xworker.org.apache.kafka;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesActions {
	public static Class<?> getSerdeClass(String name) {
		if("ByteArraySerde".equals(name)) {
			return Serdes.ByteArray().getClass();
		}else if("ByteBufferSerde".equals(name)) {
			return Serdes.ByteBuffer().getClass();
		}else if("BytesSerde".equals(name)) {
			return Serdes.Bytes().getClass();
		}else if("DoubleSerde".equals(name)) {
			return Serdes.Double().getClass();
		}else if("FloatSerde".equals(name)) {
			return Serdes.Float().getClass();
		}else if("IntegerSerde".equals(name)) {
			return Serdes.Integer().getClass();
		}else if("LongSerde".equals(name)) {
			return Serdes.Long().getClass();
		}else if("ShortSerde".equals(name)) {
			return Serdes.Short().getClass();
		}else if("UUIDSerde".equals(name)) {
			return Serdes.UUID().getClass();
		}else if("VoidSerde".equals(name)) {
			return Serdes.Void().getClass();
		}else {
			return Serdes.String().getClass();
		}
	}
	
	public static Serde<?> getSerde(String name) {
		if("ByteArraySerde".equals(name)) {
			return Serdes.ByteArray();
		}else if("ByteBufferSerde".equals(name)) {
			return Serdes.ByteBuffer();
		}else if("BytesSerde".equals(name)) {
			return Serdes.Bytes();
		}else if("DoubleSerde".equals(name)) {
			return Serdes.Double();
		}else if("FloatSerde".equals(name)) {
			return Serdes.Float();
		}else if("IntegerSerde".equals(name)) {
			return Serdes.Integer();
		}else if("LongSerde".equals(name)) {
			return Serdes.Long();
		}else if("ShortSerde".equals(name)) {
			return Serdes.Short();
		}else if("UUIDSerde".equals(name)) {
			return Serdes.UUID();
		}else if("VoidSerde".equals(name)) {
			return Serdes.Void();
		}else {
			return Serdes.String();
		}
	}
}
