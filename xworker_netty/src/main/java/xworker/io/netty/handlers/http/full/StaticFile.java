/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * 
 * 拷贝修改自：https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/http/file/HttpStaticFileServerHandler.java
 */
package xworker.io.netty.handlers.http.full;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.HEAD;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class StaticFile {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    public static Map<String, String> contentTypes = new HashMap<String, String>();
    static{
        contentTypes.put(".*","application/octet-stream");
        contentTypes.put("0.001","application/x-001");
        contentTypes.put("0.323","text/h323");
        contentTypes.put("0.907","drawing/907");
        contentTypes.put(".acp","audio/x-mei-aac");
        contentTypes.put(".aif","audio/aiff");
        contentTypes.put(".aiff","audio/aiff");
        contentTypes.put(".asa","text/asa");
        contentTypes.put(".asp","text/asp");
        contentTypes.put(".au","audio/basic");
        contentTypes.put(".awf","application/vnd.adobe.workflow");
        contentTypes.put(".bmp","application/x-bmp");
        contentTypes.put(".c4t","application/x-c4t");
        contentTypes.put(".cal","application/x-cals");
        contentTypes.put(".cdf","application/x-netcdf");
        contentTypes.put(".cel","application/x-cel");
        contentTypes.put(".cg4","application/x-g4");
        contentTypes.put(".cit","application/x-cit");
        contentTypes.put(".cml","text/xml");
        contentTypes.put(".cmx","application/x-cmx");
        contentTypes.put(".crl","application/pkix-crl");
        contentTypes.put(".csi","application/x-csi");
        contentTypes.put(".cut","application/x-cut");
        contentTypes.put(".dbm","application/x-dbm");
        contentTypes.put(".dcd","text/xml");
        contentTypes.put(".der","application/x-x509-ca-cert");
        contentTypes.put(".dib","application/x-dib");
        contentTypes.put(".doc","application/msword");
        contentTypes.put(".drw","application/x-drw");
        contentTypes.put(".dwf","Model/vnd.dwf");
        contentTypes.put(".dwg","application/x-dwg");
        contentTypes.put(".dxf","application/x-dxf");
        contentTypes.put(".emf","application/x-emf");
        contentTypes.put(".ent","text/xml");
        contentTypes.put(".eps","application/x-ps");
        contentTypes.put(".etd","application/x-ebx");
        contentTypes.put(".fax","image/fax");
        contentTypes.put(".fif","application/fractals");
        contentTypes.put(".frm","application/x-frm");
        contentTypes.put(".gbr","application/x-gbr");
        contentTypes.put(".gif","image/gif");
        contentTypes.put(".gp4","application/x-gp4");
        contentTypes.put(".hmr","application/x-hmr");
        contentTypes.put(".hpl","application/x-hpl");
        contentTypes.put(".hrf","application/x-hrf");
        contentTypes.put(".htc","text/x-component");
        contentTypes.put(".html","text/html");
        contentTypes.put(".htx","text/html");
        contentTypes.put(".ico","image/x-icon");
        contentTypes.put(".iff","application/x-iff");
        contentTypes.put(".igs","application/x-igs");
        contentTypes.put(".img","application/x-img");
        contentTypes.put(".isp","application/x-internet-signup");
        contentTypes.put(".java","java/*");
        contentTypes.put(".jpe","image/jpeg");
        contentTypes.put(".jpeg","image/jpeg");
        contentTypes.put(".jpg","application/x-jpg");
        contentTypes.put(".jsp","text/html");
        contentTypes.put(".lar","application/x-laplayer-reg");
        contentTypes.put(".lavs","audio/x-liquid-secure");
        contentTypes.put(".lmsff","audio/x-la-lms");
        contentTypes.put(".ltr","application/x-ltr");
        contentTypes.put(".m2v","video/x-mpeg");
        contentTypes.put(".m4e","video/mpeg4");
        contentTypes.put(".man","application/x-troff-man");
        contentTypes.put(".mdb","application/msaccess");
        contentTypes.put(".mfp","application/x-shockwave-flash");
        contentTypes.put(".mhtml","message/rfc822");
        contentTypes.put(".mid","audio/mid");
        contentTypes.put(".mil","application/x-mil");
        contentTypes.put(".mnd","audio/x-musicnet-download");
        contentTypes.put(".mocha","application/x-javascript");
        contentTypes.put(".mp1","audio/mp1");
        contentTypes.put(".mp2v","video/mpeg");
        contentTypes.put(".mp4","video/mpeg4");
        contentTypes.put(".mpd","application/vnd.ms-project");
        contentTypes.put(".mpeg","video/mpg");
        contentTypes.put(".mpga","audio/rn-mpeg");
        contentTypes.put(".mps","video/x-mpeg");
        contentTypes.put(".mpv","video/mpg");
        contentTypes.put(".mpw","application/vnd.ms-project");
        contentTypes.put(".mtx","text/xml");
        contentTypes.put(".net","image/pnetvue");
        contentTypes.put(".nws","message/rfc822");
        contentTypes.put(".out","application/x-out");
        contentTypes.put(".p12","application/x-pkcs12");
        contentTypes.put(".p7c","application/pkcs7-mime");
        contentTypes.put(".p7r","application/x-pkcs7-certreqresp");
        contentTypes.put(".pc5","application/x-pc5");
        contentTypes.put(".pcl","application/x-pcl");
        contentTypes.put(".pdf","application/pdf");
        contentTypes.put(".pdx","application/vnd.adobe.pdx");
        contentTypes.put(".pgl","application/x-pgl");
        contentTypes.put(".pko","application/vnd.ms-pki.pko");
        contentTypes.put(".plg","text/html");
        contentTypes.put(".plt","application/x-plt");
        contentTypes.put(".png","application/x-png");
        contentTypes.put(".ppa","application/vnd.ms-powerpoint");
        contentTypes.put(".pps","application/vnd.ms-powerpoint");
        contentTypes.put(".ppt","application/x-ppt");
        contentTypes.put(".prf","application/pics-rules");
        contentTypes.put(".prt","application/x-prt");
        contentTypes.put(".ps","application/postscript");
        contentTypes.put(".pwz","application/vnd.ms-powerpoint");
        contentTypes.put(".ra","audio/vnd.rn-realaudio");
        contentTypes.put(".ras","application/x-ras");
        contentTypes.put(".rdf","text/xml");
        contentTypes.put(".red","application/x-red");
        contentTypes.put(".rjs","application/vnd.rn-realsystem-rjs");
        contentTypes.put(".rlc","application/x-rlc");
        contentTypes.put(".rm","application/vnd.rn-realmedia");
        contentTypes.put(".rmi","audio/mid");
        contentTypes.put(".rmm","audio/x-pn-realaudio");
        contentTypes.put(".rms","application/vnd.rn-realmedia-secure");
        contentTypes.put(".rmx","application/vnd.rn-realsystem-rmx");
        contentTypes.put(".rp","image/vnd.rn-realpix");
        contentTypes.put(".rsml","application/vnd.rn-rsml");
        contentTypes.put(".rtf","application/msword");
        contentTypes.put(".rv","video/vnd.rn-realvideo");
        contentTypes.put(".sat","application/x-sat");
        contentTypes.put(".sdw","application/x-sdw");
        contentTypes.put(".slb","application/x-slb");
        contentTypes.put(".slk","drawing/x-slk");
        contentTypes.put(".smil","application/smil");
        contentTypes.put(".snd","audio/basic");
        contentTypes.put(".sor","text/plain");
        contentTypes.put(".spl","application/futuresplash");
        contentTypes.put(".ssm","application/streamingmedia");
        contentTypes.put(".stl","application/vnd.ms-pki.stl");
        contentTypes.put(".sty","application/x-sty");
        contentTypes.put(".swf","application/x-shockwave-flash");
        contentTypes.put(".tg4","application/x-tg4");
        contentTypes.put(".tif","image/tiff");
        contentTypes.put(".tiff","image/tiff");
        contentTypes.put(".top","drawing/x-top");
        contentTypes.put(".tsd","text/xml");
        contentTypes.put(".uin","application/x-icq");
        contentTypes.put(".vcf","text/x-vcard");
        contentTypes.put(".vdx","application/vnd.visio");
        contentTypes.put(".vpg","application/x-vpeg005");
        contentTypes.put(".vsd","application/x-vsd");
        contentTypes.put(".vst","application/vnd.visio");
        contentTypes.put(".vsw","application/vnd.visio");
        contentTypes.put(".vtx","application/vnd.visio");
        contentTypes.put(".wav","audio/wav");
        contentTypes.put(".wb1","application/x-wb1");
        contentTypes.put(".wb3","application/x-wb3");
        contentTypes.put(".wiz","application/msword");
        contentTypes.put(".wk4","application/x-wk4");
        contentTypes.put(".wks","application/x-wks");
        contentTypes.put(".wma","audio/x-ms-wma");
        contentTypes.put(".wmf","application/x-wmf");
        contentTypes.put(".wmv","video/x-ms-wmv");
        contentTypes.put(".wmz","application/x-ms-wmz");
        contentTypes.put(".wpd","application/x-wpd");
        contentTypes.put(".wpl","application/vnd.ms-wpl");
        contentTypes.put(".wr1","application/x-wr1");
        contentTypes.put(".wrk","application/x-wrk");
        contentTypes.put(".ws2","application/x-ws");
        contentTypes.put(".wsdl","text/xml");
        contentTypes.put(".xdp","application/vnd.adobe.xdp");
        contentTypes.put(".xfd","application/vnd.adobe.xfd");
        contentTypes.put(".xhtml","text/html");
        contentTypes.put(".xls","application/x-xls");
        contentTypes.put(".xml","text/xml");
        contentTypes.put(".xq","text/xml");
        contentTypes.put(".xquery","text/xml");
        contentTypes.put(".xsl","text/xml");
        contentTypes.put(".xwd","application/x-xwd");
        contentTypes.put(".sis","application/vnd.symbian.install");
        contentTypes.put(".x_t","application/x-x_t");
        contentTypes.put(".apk","application/vnd.android.package-archive");
        contentTypes.put(".tif","image/tiff");
        contentTypes.put("0.301","application/x-301");
        contentTypes.put("0.906","application/x-906");
        contentTypes.put(".a11","application/x-a11");
        contentTypes.put(".ai","application/postscript");
        contentTypes.put(".aifc","audio/aiff");
        contentTypes.put(".anv","application/x-anv");
        contentTypes.put(".asf","video/x-ms-asf");
        contentTypes.put(".asx","video/x-ms-asf");
        contentTypes.put(".avi","video/avi");
        contentTypes.put(".biz","text/xml");
        contentTypes.put(".bot","application/x-bot");
        contentTypes.put(".c90","application/x-c90");
        contentTypes.put(".cat","application/vnd.ms-pki.seccat");
        contentTypes.put(".cdr","application/x-cdr");
        contentTypes.put(".cer","application/x-x509-ca-cert");
        contentTypes.put(".cgm","application/x-cgm");
        contentTypes.put(".class","java/*");
        contentTypes.put(".cmp","application/x-cmp");
        contentTypes.put(".cot","application/x-cot");
        contentTypes.put(".crt","application/x-x509-ca-cert");
        contentTypes.put(".css","text/css");
        contentTypes.put(".dbf","application/x-dbf");
        contentTypes.put(".dbx","application/x-dbx");
        contentTypes.put(".dcx","application/x-dcx");
        contentTypes.put(".dgn","application/x-dgn");
        contentTypes.put(".dll","application/x-msdownload");
        contentTypes.put(".dot","application/msword");
        contentTypes.put(".dtd","text/xml");
        contentTypes.put(".dwf","application/x-dwf");
        contentTypes.put(".dxb","application/x-dxb");
        contentTypes.put(".edn","application/vnd.adobe.edn");
        contentTypes.put(".eml","message/rfc822");
        contentTypes.put(".epi","application/x-epi");
        contentTypes.put(".eps","application/postscript");
        contentTypes.put(".exe","application/x-msdownload");
        contentTypes.put(".fdf","application/vnd.fdf");
        contentTypes.put(".fo","text/xml");
        contentTypes.put(".g4","application/x-g4");
        contentTypes.put(".","application/x-");
        contentTypes.put(".gl2","application/x-gl2");
        contentTypes.put(".hgl","application/x-hgl");
        contentTypes.put(".hpg","application/x-hpgl");
        contentTypes.put(".hqx","application/mac-binhex40");
        contentTypes.put(".hta","application/hta");
        contentTypes.put(".htm","text/html");
        contentTypes.put(".htt","text/webviewhtml");
        contentTypes.put(".icb","application/x-icb");
        contentTypes.put(".ico","application/x-ico");
        contentTypes.put(".ig4","application/x-g4");
        contentTypes.put(".iii","application/x-iphone");
        contentTypes.put(".ins","application/x-internet-signup");
        contentTypes.put(".IVF","video/x-ivf");
        contentTypes.put(".jfif","image/jpeg");
        contentTypes.put(".jpe","application/x-jpe");
        contentTypes.put(".jpg","image/jpeg");
        contentTypes.put(".js","application/x-javascript");
        contentTypes.put(".la1","audio/x-liquid-file");
        contentTypes.put(".latex","application/x-latex");
        contentTypes.put(".lbm","application/x-lbm");
        contentTypes.put(".ls","application/x-javascript");
        contentTypes.put(".m1v","video/x-mpeg");
        contentTypes.put(".m3u","audio/mpegurl");
        contentTypes.put(".mac","application/x-mac");
        contentTypes.put(".math","text/xml");
        contentTypes.put(".mdb","application/x-mdb");
        contentTypes.put(".mht","message/rfc822");
        contentTypes.put(".mi","application/x-mi");
        contentTypes.put(".midi","audio/mid");
        contentTypes.put(".mml","text/xml");
        contentTypes.put(".mns","audio/x-musicnet-stream");
        contentTypes.put(".movie","video/x-sgi-movie");
        contentTypes.put(".mp2","audio/mp2");
        contentTypes.put(".mp3","audio/mp3");
        contentTypes.put(".mpa","video/x-mpg");
        contentTypes.put(".mpe","video/x-mpeg");
        contentTypes.put(".mpg","video/mpg");
        contentTypes.put(".mpp","application/vnd.ms-project");
        contentTypes.put(".mpt","application/vnd.ms-project");
        contentTypes.put(".mpv2","video/mpeg");
        contentTypes.put(".mpx","application/vnd.ms-project");
        contentTypes.put(".mxp","application/x-mmxp");
        contentTypes.put(".nrf","application/x-nrf");
        contentTypes.put(".odc","text/x-ms-odc");
        contentTypes.put(".p10","application/pkcs10");
        contentTypes.put(".p7b","application/x-pkcs7-certificates");
        contentTypes.put(".p7m","application/pkcs7-mime");
        contentTypes.put(".p7s","application/pkcs7-signature");
        contentTypes.put(".pci","application/x-pci");
        contentTypes.put(".pcx","application/x-pcx");
        contentTypes.put(".pdf","application/pdf");
        contentTypes.put(".pfx","application/x-pkcs12");
        contentTypes.put(".pic","application/x-pic");
        contentTypes.put(".pl","application/x-perl");
        contentTypes.put(".pls","audio/scpls");
        contentTypes.put(".png","image/png");
        contentTypes.put(".pot","application/vnd.ms-powerpoint");
        contentTypes.put(".ppm","application/x-ppm");
        contentTypes.put(".ppt","application/vnd.ms-powerpoint");
        contentTypes.put(".pr","application/x-pr");
        contentTypes.put(".prn","application/x-prn");
        contentTypes.put(".ps","application/x-ps");
        contentTypes.put(".ptn","application/x-ptn");
        contentTypes.put(".r3t","text/vnd.rn-realtext3d");
        contentTypes.put(".ram","audio/x-pn-realaudio");
        contentTypes.put(".rat","application/rat-file");
        contentTypes.put(".rec","application/vnd.rn-recording");
        contentTypes.put(".rgb","application/x-rgb");
        contentTypes.put(".rjt","application/vnd.rn-realsystem-rjt");
        contentTypes.put(".rle","application/x-rle");
        contentTypes.put(".rmf","application/vnd.adobe.rmf");
        contentTypes.put(".rmj","application/vnd.rn-realsystem-rmj");
        contentTypes.put(".rmp","application/vnd.rn-rn_music_package");
        contentTypes.put(".rmvb","application/vnd.rn-realmedia-vbr");
        contentTypes.put(".rnx","application/vnd.rn-realplayer");
        contentTypes.put(".rpm","audio/x-pn-realaudio-plugin");
        contentTypes.put(".rt","text/vnd.rn-realtext");
        contentTypes.put(".rtf","application/x-rtf");
        contentTypes.put(".sam","application/x-sam");
        contentTypes.put(".sdp","application/sdp");
        contentTypes.put(".sit","application/x-stuffit");
        contentTypes.put(".sld","application/x-sld");
        contentTypes.put(".smi","application/smil");
        contentTypes.put(".smk","application/x-smk");
        contentTypes.put(".sol","text/plain");
        contentTypes.put(".spc","application/x-pkcs7-certificates");
        contentTypes.put(".spp","text/xml");
        contentTypes.put(".sst","application/vnd.ms-pki.certstore");
        contentTypes.put(".stm","text/html");
        contentTypes.put(".svg","text/xml");
        contentTypes.put(".tdf","application/x-tdf");
        contentTypes.put(".tga","application/x-tga");
        contentTypes.put(".tif","application/x-tif");
        contentTypes.put(".tld","text/xml");
        contentTypes.put(".torrent","application/x-bittorrent");
        contentTypes.put(".txt","text/plain");
        contentTypes.put(".uls","text/iuls");
        contentTypes.put(".vda","application/x-vda");
        contentTypes.put(".vml","text/xml");
        contentTypes.put(".vsd","application/vnd.visio");
        contentTypes.put(".vss","application/vnd.visio");
        contentTypes.put(".vst","application/x-vst");
        contentTypes.put(".vsx","application/vnd.visio");
        contentTypes.put(".vxml","text/xml");
        contentTypes.put(".wax","audio/x-ms-wax");
        contentTypes.put(".wb2","application/x-wb2");
        contentTypes.put(".wbmp","image/vnd.wap.wbmp");
        contentTypes.put(".wk3","application/x-wk3");
        contentTypes.put(".wkq","application/x-wkq");
        contentTypes.put(".wm","video/x-ms-wm");
        contentTypes.put(".wmd","application/x-ms-wmd");
        contentTypes.put(".wml","text/vnd.wap.wml");
        contentTypes.put(".wmx","video/x-ms-wmx");
        contentTypes.put(".wp6","application/x-wp6");
        contentTypes.put(".wpg","application/x-wpg");
        contentTypes.put(".wq1","application/x-wq1");
        contentTypes.put(".wri","application/x-wri");
        contentTypes.put(".ws","application/x-ws");
        contentTypes.put(".wsc","text/scriptlet");
        contentTypes.put(".wvx","video/x-ms-wvx");
        contentTypes.put(".xdr","text/xml");
        contentTypes.put(".xfdf","application/vnd.adobe.xfdf");
        contentTypes.put(".xls","application/vnd.ms-excel");
        contentTypes.put(".xlw","application/x-xlw");
        contentTypes.put(".xpl","audio/scpls");
        contentTypes.put(".xql","text/xml");
        contentTypes.put(".xsd","text/xml");
        contentTypes.put(".xslt","text/xml");
        contentTypes.put(".x_b","application/x-x_b");
        contentTypes.put(".sisx","application/vnd.symbian.install");
        contentTypes.put(".ipa","application/vnd.iphone");
        contentTypes.put(".xap","application/x-silverlight-app");
    }

    public static FullHttpResponse doRequest(ChannelHandlerContext ctx, FullHttpRequest request, File file) throws IOException, ParseException {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        String uri = request.uri();

        if (file == null || file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND);
        }

        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                return sendListing(ctx, file, uri);
            } else {
                return sendRedirect(ctx, uri + '/');
            }
        }

        if (!file.isFile()) {
            return sendError(ctx, FORBIDDEN);
        }

        // Cache Validation
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                return sendNotModified(ctx);
            }
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            return sendError(ctx, NOT_FOUND);
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        setDateAndCacheHeaders(response, file);

        if (!keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        //下载起始位置
        long since=0;
        //下载结束位置
        long until = fileLength - 1;


        //获取Range，下载范围
        String range = request.headers().get("range");
        boolean hasRange = true;
        if(range != null){
            //剖解range
            range = range.split("=")[1];
            String[] rs=range.split("-");
            try {
                since = Long.parseLong(rs[0]);
            }catch(Exception e) {
                since = 0;
            }
            if(rs.length > 1){
                try {
                    until=Long.parseLong(rs[1]);
                }catch(Exception e) {
                }
            }
        }else{
            hasRange = false;
        }
        if(until >= fileLength) {
            until = fileLength - 1;
        }

        String browser=request.headers().get("user-agent");
        if((until - since + 1) == fileLength) {
            //下载全部时，不需要设置206
            hasRange = false;
        }
        if(browser != null && browser.contains("MSIE")) {
            //200 响应头，不支持断点续传
            since = 0;
            until = fileLength - 1;
            hasRange = false;
        }

        if(hasRange){
            response.setStatus(HttpResponseStatus.PARTIAL_CONTENT);
        }

        //String cd = "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), "utf-8");
        //response.headers().set("Content-Disposition", cd);
        response.headers().set("Content-Length", "" + (until - since + 1));
        response.headers().set("Accept-Ranges", "bytes");
        if(hasRange) {
            response.headers().set("Content-Range", "bytes " + since + "-" + until + "/" + fileLength);
        }
        response.headers().set("Server", "Netty");

        // Write the initial line and the header.
        ctx.writeAndFlush(response);

        ChannelFuture lastContentFuture;
        if(GET.equals(request.method())) {
            // Write the content.
            ChannelFuture sendFileFuture;

            sendFileFuture =
                    ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, since, until - since + 1, 8192)),
                            ctx.newProgressivePromise());
            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            lastContentFuture = sendFileFuture;
            /*
            if (ctx.pipeline().get(SslHandler.class) == null) {
                sendFileFuture =
                        ctx.write(new DefaultFileRegion(raf.getChannel(), since, until -since + 1), ctx.newProgressivePromise());
                // Write the end marker.
                lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                sendFileFuture =
                        ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, since, until - since + 1, 8192)),
                                ctx.newProgressivePromise());
                // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                lastContentFuture = sendFileFuture;
            }*/

            /*
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                    if (total < 0) { // total unknown
                        System.err.println(future.channel() + " Transfer progress: " + progress);
                    } else {
                        System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) {
                    System.err.println(future.channel() + " Transfer complete.");
                }
            });*/
        }else{
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }

        // Decide whether to close the connection or not.
        if (!keepAlive) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        return null;
    }

    public static FullHttpResponse doRequest(ActionContext actionContext) throws IOException, ParseException {
    	Thing self = actionContext.getObject("self");
    	ChannelHandlerContext ctx = actionContext.getObject("ctx");
    	FullHttpRequest request = actionContext.getObject("request");

    	//System.out.println(request);
        if (!request.decoderResult().isSuccess()) {
            return sendError(ctx, BAD_REQUEST);
        }

        if (!GET.equals(request.method()) && !HEAD.equals(request.method())) {
            return sendError(ctx, METHOD_NOT_ALLOWED);
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        String uri = request.uri();
        String path = sanitizeUri(uri);
        if (path == null) {
            return sendError(ctx, FORBIDDEN);
        }

        String contextPath = self.doAction("getContextPath", actionContext);
        //过滤参数
        int index = path.indexOf("?");
        if(index > 0) {
        	path = path.substring(0, index);
        }
        //过滤contextPath
        if(contextPath != null) {
        	path.substring(contextPath.length(), path.length());
        }
        
        File rootFile = self.doAction("getFileRoot", actionContext);
        if(rootFile == null) {
        	return sendError(ctx, NOT_FOUND);
        }

        File file = new File(rootFile, path);
        
        //自行处理回复的消息了
        return doRequest(ctx, request, file);
    }

    public static FullHttpResponse exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            return sendError(ctx, INTERNAL_SERVER_ERROR);
        }
        
        return null;
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private static String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        return uri;
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

    private static FullHttpResponse sendListing(ChannelHandlerContext ctx, File dir, String dirPath) {
        StringBuilder buf = new StringBuilder()
            .append("<!DOCTYPE html>\r\n")
            .append("<html><head><meta charset='utf-8' /><title>")
            .append("Listing of: ")
            .append(dirPath)
            .append("</title></head><body>\r\n")

            .append("<h3>Listing of: ")
            .append(dirPath)
            .append("</h3>\r\n")

            .append("<ul>")
            .append("<li><a href=\"../\">..</a></li>\r\n");

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f: files) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }

                String name = f.getName();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }

                buf.append("<li><a href=\"")
                .append(name)
                .append("\">")
                .append(name)
                .append("</a></li>\r\n");
            }
        }

        buf.append("</ul></body></html>\r\n");

        ByteBuf buffer = ctx.alloc().buffer(buf.length());
        buffer.writeCharSequence(buf.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        return response;
    }

    private static FullHttpResponse sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);

        return response;
    }

    private static FullHttpResponse sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        return response;
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    private static FullHttpResponse sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        setDateHeader(response);

        return response;
    }


    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param fileToCache
     *            file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        if(file != null){
            String name = file.getName().toLowerCase();
            int index = name.lastIndexOf(".");
            String type = "application/octet-stream";
            if(index != -1){
                String ctype = contentTypes.get(name.substring(index, name.length()));
                if(ctype != null){
                    type = ctype;
                }
            }
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, type);
        }
    }
}
