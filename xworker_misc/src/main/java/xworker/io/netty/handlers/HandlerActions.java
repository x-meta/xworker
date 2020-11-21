package xworker.io.netty.handlers;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class HandlerActions {
	private static final String TAG = HandlerActions.class.getName();
	public static ChannelTrafficShapingHandler createChannelTrafficShapingHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
	
		long writeLimit = self.doAction("getWriteLimit", actionContext);
		long readLimit  = self.doAction("getReadLimit", actionContext);
		long checkInterval  = self.doAction("getCheckInterval", actionContext);
		long maxTime  = self.doAction("getMaxTime", actionContext);
		
		if(maxTime > 0) {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit, checkInterval, maxTime);
		}else if(checkInterval > 0) {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit, checkInterval);
		}else {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit);
		}
	}
	
	public static GlobalTrafficShapingHandler createGlobalTrafficShapingHandler(ActionContext actionContext) {
		//Thing self = actionContext.getObject("self");
	
		Executor.warn(TAG, "Can not create GlobalTrafficShapingHandler, haven't implemented!");
		return null;
		/* 还未实现，因为GlobalTrafficShapingHandler的构造函数需要EventExecutor或ScheduledExecutorService
		 * 这两个对象还没有确定通过什么方式传入或获取。
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		
		long writeLimit = self.doAction("getWriteLimit", actionContext);
		long readLimit  = self.doAction("getReadLimit", actionContext);
		long checkInterval  = self.doAction("getCheckInterval", actionContext);
		long maxTime  = self.doAction("getMaxTime", actionContext);
		
		EventLoopGroup group = bootstrap.
		
		if(maxTime > 0) {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit, checkInterval, maxTime);
		}else if(checkInterval > 0) {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit, checkInterval);
		}else {
			return new ChannelTrafficShapingHandler(writeLimit, readLimit);
		}*/
	}
	
	public static Base64Decoder createBase64Decoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String dialect = self.doAction("getDialect", actionContext);
		if("ORDERED".equals(dialect)) {
			return new Base64Decoder(Base64Dialect.ORDERED);
		}else if("STANDARD".equals(dialect)) {
			return new Base64Decoder(Base64Dialect.STANDARD);
		}else if("URL_SAFE".equals(dialect)) {
			return new Base64Decoder(Base64Dialect.URL_SAFE);
		} else {
			return new Base64Decoder();
		}
	}
	
	public static Base64Encoder createBase64Encoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Boolean breakLines = self.doAction("isBreakLines", actionContext);
		if(breakLines != null) {
			String dialect = self.doAction("getDialect", actionContext);
			if("ORDERED".equals(dialect)) {
				return new Base64Encoder(breakLines, Base64Dialect.ORDERED);
			}else if("STANDARD".equals(dialect)) {
				return new Base64Encoder(breakLines, Base64Dialect.STANDARD);
			}else if("URL_SAFE".equals(dialect)) {
				return new Base64Encoder(breakLines, Base64Dialect.URL_SAFE);
			} else {
				return new Base64Encoder(breakLines);
			}
		}else {
			return new Base64Encoder();
		}
	}
	
	public static ByteArrayDecoder createByteArrayDecoder(ActionContext actionContext) {
		return new ByteArrayDecoder();
	}
	
	public static ByteArrayEncoder createByteArrayEncoder(ActionContext actionContext) {
		return new ByteArrayEncoder();
	}
	
	public static DelimiterBasedFrameDecoder createDelimiterBasedFrameDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ByteBuf[] delimiters = self.doAction("getDelimiters", actionContext);
		if(delimiters == null) {
			throw new ActionException("delimiters is null, action=" + self.getMetadata().getPath());
		}
		
		int maxFrameLength = self.doAction("getMaxFrameLength", actionContext);
		Boolean stripDelimiter = self.doAction("getStripDelimiter", actionContext);
		Boolean failFast = self.doAction("getFailFast", actionContext);
		if(stripDelimiter != null && failFast != null) {
			return new DelimiterBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast, delimiters);
		}else if(stripDelimiter != null) {
			return new DelimiterBasedFrameDecoder(maxFrameLength, stripDelimiter, delimiters);
		}else {
			return new DelimiterBasedFrameDecoder(maxFrameLength, delimiters);
		}
		
	}
	
	public static ByteBuf[] getDelimiters(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String delimiters = self.getStringBlankAsNull("delimiters");
		if(delimiters == null) {
			return null;
		}
		
		if(delimiters.equals("lineDelimiter")) {
			return Delimiters.lineDelimiter();
		}else if("nulDelimiter".equals(delimiters)) {
			return Delimiters.nulDelimiter();
		}else {
			return new ByteBuf[] {Unpooled.wrappedBuffer(delimiters.getBytes())};
		}
	}
	
	public static FixedLengthFrameDecoder createFixedLengthFrameDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int frameLength = self.doAction("createFrameLength", actionContext);
		
		return new FixedLengthFrameDecoder(frameLength);
	}
	
	public static HttpClientCodec createHttpClientCodec(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxInitialLineLength = self.doAction("getMaxInitialLineLength", actionContext);
		int maxHeaderSize = self.doAction("getMaxHeaderSize", actionContext);
		int maxChunkSize = self.doAction("getMaxChunkSize", actionContext);
		Boolean failOnMissingResponse = self.doAction("isFailOnMissingResponse", actionContext);
		Boolean validateHeaders = self.doAction("isValidateHeaders", actionContext);
		
		if(failOnMissingResponse != null && validateHeaders != null) {
			return new HttpClientCodec(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders);
		}else if(failOnMissingResponse != null) {
			return new HttpClientCodec(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse);
		}else {
			return new HttpClientCodec(maxInitialLineLength, maxHeaderSize, maxChunkSize);
		}
	}
	
	public static HttpContentCompressor createHttpContentCompressor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int compressionLevel = self.doAction("getCompressionLevel", actionContext);
		int windowBits = self.doAction("getWindowBits", actionContext);
		int memLevel = self.doAction("getMemLevel", actionContext);
		
		return new HttpContentCompressor(compressionLevel, windowBits, memLevel);
	}
	
	public static HttpRequestDecoder createHttpRequestDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxInitialLineLength = self.doAction("getMaxInitialLineLength", actionContext);
		int maxHeaderSize = self.doAction("getMaxHeaderSize", actionContext);
		int maxChunkSize = self.doAction("getMaxChunkSize", actionContext);
		Boolean validateHeaders = self.doAction("isValidateHeaders", actionContext);
		
		if(validateHeaders != null) {
			return new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
		}else {
			return new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize);
		}
	}
	
	public static HttpRequestEncoder createHttpRequestEncoder(ActionContext actionContext) {
		return new HttpRequestEncoder();
	}
	
	public static HttpResponseDecoder createHttpResponseDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxInitialLineLength = self.doAction("getMaxInitialLineLength", actionContext);
		int maxHeaderSize = self.doAction("getMaxHeaderSize", actionContext);
		int maxChunkSize = self.doAction("getMaxChunkSize", actionContext);
		Boolean validateHeaders = self.doAction("isValidateHeaders", actionContext);
		
		if(validateHeaders != null) {
			return new HttpResponseDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
		}else {
			return new HttpResponseDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize);
		}
	}
	
	public static HttpResponseEncoder createHttpResponseEncoder(ActionContext actionContext) {
		return new HttpResponseEncoder();
	}
	
	public static HttpServerCodec createHttpServerCodec(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxInitialLineLength = self.doAction("getMaxInitialLineLength", actionContext);
		int maxHeaderSize = self.doAction("getMaxHeaderSize", actionContext);
		int maxChunkSize = self.doAction("getMaxChunkSize", actionContext);
		Boolean validateHeaders = self.doAction("isValidateHeaders", actionContext);
		
		if(validateHeaders != null) {
			return new HttpServerCodec(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
		}else {
			return new HttpServerCodec(maxInitialLineLength, maxHeaderSize, maxChunkSize);
		}
	}
	
	public static ChannelHandler[] createIdleStateHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int readerIdleTimeSeconds = self.doAction("getReaderIdleTimeSeconds", actionContext);
		int writerIdleTimeSeconds = self.doAction("getWriterIdleTimeSeconds", actionContext);
		int allIdleTimeSeconds = self.doAction("getAllIdleTimeSeconds", actionContext);
		
		IdleStateHandler idleHandler = new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		if(UtilData.isTrue(self.doAction("isEvent", actionContext))) {
			IdleThingHandler thingHandler = new IdleThingHandler(self, actionContext);
			
			return new ChannelHandler[] {idleHandler, thingHandler};
		}else {
			return new ChannelHandler[] {idleHandler};
		}
	}
	
	public static LengthFieldBasedFrameDecoder createLengthFieldBasedFrameDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String byteOrder = self.doAction("getByteOrder", actionContext);
		int maxFrameLength = self.doAction("getMaxFrameLength", actionContext);
		int lengthFieldOffset = self.doAction("getLengthFieldOffset", actionContext);
		int lengthFieldLength = self.doAction("getLengthFieldLength", actionContext);
		int lengthAdjustment = self.doAction("getLengthAdjustment", actionContext);
		int initialBytesToStrip = self.doAction("getInitialBytesToStrip", actionContext);
		Boolean failFast = self.doAction("isFailFast", actionContext);
		
		if("BIG_ENDIAN".equals(byteOrder)) {
			return new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, maxFrameLength, lengthFieldOffset, 
					lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast == null ? false : failFast);
		}else if("LITTLE_ENDIAN".equals(byteOrder)) {
			return new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, maxFrameLength, lengthFieldOffset, 
					lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast == null ? false : failFast);
		}else if(failFast != null) {
			return new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, 
					lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
		}else {
			return new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, 
					lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		}
	}
	
	public static LengthFieldPrepender createLengthFieldPrepender(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int lengthFieldLength = self.doAction("getLengthFieldLength", actionContext);
		int lengthAdjustment = self.doAction("getLengthAdjustment", actionContext);
		Boolean lengthIncludesLengthFieldLength = self.doAction("isLengthIncludesLengthFieldLength", actionContext);
		
		if(lengthIncludesLengthFieldLength != null) {
			return new LengthFieldPrepender(lengthFieldLength, lengthAdjustment, lengthIncludesLengthFieldLength); 
		}else {
			return new LengthFieldPrepender(lengthFieldLength, lengthAdjustment);
		}
	}
	
	public static StringDecoder createStringDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String charset = self.doAction("getCharset", actionContext);
		if(charset != null && !"".equals(charset)) {
			return new StringDecoder(Charset.forName(charset));
		}else {
			return new StringDecoder();
		}
	}
	
	public static StringEncoder createStringEncoder(ActionContext actionContext) {
		
		Thing self = actionContext.getObject("self");
		
		String charset = self.doAction("getCharset", actionContext);
		if(charset != null && !"".equals(charset)) {
			return new StringEncoder(Charset.forName(charset));
		}else {
			return new StringEncoder();
		}
	}
	
	public static LoggingHandler createLoggingHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String loggerName = self.doAction("getLoggerName", actionContext);
		LogLevel level = LogLevel.DEBUG;
		String l = self.doAction("getLevel", actionContext);
		if("TRACE".equals(l)) {
			level = LogLevel.TRACE;
		} else if("DEBUG".equals(l)) {
			level = LogLevel.DEBUG;
		} else if("INFO".equals(l)) {
			level = LogLevel.INFO;
		} else if("WARN".equals(l)) {
			level = LogLevel.WARN;
		} else if("ERROR".equals(l)) {
			level = LogLevel.ERROR;
		} 
		
		if(loggerName == null || "".equals(loggerName)) {
			return new LoggingHandler(level);
		}else {
			return new LoggingHandler(loggerName, level);
		}
	}
	
	public static MqttDecoder createMqttDecoder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxBytesInMessage = self.doAction("getMaxBytesInMessage", actionContext);
		if(maxBytesInMessage > 0) {
			return new MqttDecoder(maxBytesInMessage);
		}else {
			return new MqttDecoder();
		}
	}
	
	public static MqttEncoder createMqttEncoder(ActionContext actionContext) {
		return MqttEncoder.INSTANCE;
	}
	
	public static HttpObjectAggregator createHttpObjectAggregator(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int maxContentLength = self.doAction("getMaxContentLength", actionContext);
		Boolean closeOnExpectationFailed = self.doAction("isCloseOnExpectationFailed", actionContext);
		
		if(closeOnExpectationFailed != null) {
			return new HttpObjectAggregator(maxContentLength, closeOnExpectationFailed);
		}else {
			return new HttpObjectAggregator(maxContentLength);
		}
	}
	
	public static ChunkedWriteHandler createChunkedWriteHandler(ActionContext actionContext) {
		//Thing self = actionContext.getObject("self");
		
		return new ChunkedWriteHandler();
	}
}