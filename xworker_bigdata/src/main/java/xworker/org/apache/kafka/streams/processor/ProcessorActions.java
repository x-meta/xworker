package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.Topology.AutoOffsetReset;
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp;
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.processor.UsePartitionTimeOnInvalidTimestamp;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

public class ProcessorActions {
	public static TimestampExtractor getTimestampExtractor(String name) {
		if("FailOnInvalidTimestamp".equals(name)) {
			return new FailOnInvalidTimestamp();
		}else if("LogAndSkipOnInvalidTimestamp".equals(name)) {
			return new LogAndSkipOnInvalidTimestamp();
		}else if("UsePartitionTimeOnInvalidTimestamp".equals(name)) {
			return new UsePartitionTimeOnInvalidTimestamp();
		}else if("WallclockTimestampExtractor".equals(name)) {
			return new WallclockTimestampExtractor();
		}else {
			return new LogAndSkipOnInvalidTimestamp();
		}
	}
	
	public static AutoOffsetReset getResetPolicy(String name) {
		if("EARLIEST".equals(name)) {
			return AutoOffsetReset.EARLIEST;
		}else {
			return AutoOffsetReset.LATEST;
		}
	}
}
