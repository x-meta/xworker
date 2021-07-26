package xworker.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RequestConfigActions {
    public static RequestConfig create(ActionContext actionContext) throws UnknownHostException {
        Thing self = actionContext.getObject("self");

        RequestConfig.Builder builder = RequestConfig.custom();
        if(self.valueExists("authenticationEnabled")){
            builder.setAuthenticationEnabled(self.getBoolean("authenticationEnabled"));
        }
        if(self.valueExists("circularRedirectsAllowed")){
            builder.setCircularRedirectsAllowed(self.getBoolean("circularRedirectsAllowed"));
        }
        if(self.valueExists("connectionRequestTimeout")){
            builder.setConnectionRequestTimeout(self.getInt("connectionRequestTimeout"));
        }
        if(self.valueExists("connectTimeout")){
            builder.setConnectTimeout(self.getInt("connectTimeout"));
        }
        if(self.valueExists("contentCompressionEnabled")){
            builder.setContentCompressionEnabled(self.getBoolean("contentCompressionEnabled"));
        }
        if(self.valueExists("cookieSpec")){
            builder.setCookieSpec(self.getString("cookieSpec"));
        }
        if(self.valueExists("expectContinueEnabled")){
            builder.setExpectContinueEnabled(self.getBoolean("expectContinueEnabled"));
        }
        if(self.valueExists("maxRedirects")){
            builder.setMaxRedirects(self.getInt("maxRedirects"));
        }
        if(self.valueExists("proxyPreferredAuthSchemes")){
            String proxyPreferredAuthSchemes = self.getString("proxyPreferredAuthSchemes");
            List<String> list = new ArrayList<>();
            for(String line : proxyPreferredAuthSchemes.split("[\n]")){
                list.add(line);
            }
            builder.setProxyPreferredAuthSchemes(list);
        }
        if(self.valueExists("redirectsEnabled")){
            builder.setRedirectsEnabled(self.getBoolean("redirectsEnabled"));
        }
        if(self.valueExists("relativeRedirectsAllowed")){
            builder.setRelativeRedirectsAllowed(self.getBoolean("relativeRedirectsAllowed"));
        }
        if(self.valueExists("socketTimeout")){
            builder.setSocketTimeout(self.getInt("socketTimeout"));
        }
        if(self.valueExists("proxyHost") && self.valueExists("proxyPort")){
            builder.setProxy(new HttpHost(self.getString("proxyHost"), self.getInt("proxyPort")));
        }
        if(self.valueExists("localAddress")){
            builder.setLocalAddress(InetAddress.getByName(self.getString("localAddress")));
        }
        return builder.build();
    }
}
