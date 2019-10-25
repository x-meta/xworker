/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class MultiPartRequest implements HttpServletRequest {
	HttpServletRequest request;
	Map<String, Object> parmeters = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public MultiPartRequest(HttpServletRequest request, FileItemFactory factory, ServletRequestContext context) throws FileUploadException{
		this.request = request;
				
		ServletFileUpload upload = new ServletFileUpload(factory);

		List items = upload.parseRequest(request);
		for(Iterator iter = items.iterator(); iter.hasNext();){
			FileItem item = (FileItem) iter.next();
		
			if(item.isFormField()){
				String name = item.getFieldName();
				String value = item.getString();				
				parmeters.put(name, value);
			}else{
				String name = item.getFieldName();
				parmeters.put(name, item);
			}
		}
	}

	public FileItem getFileItem(String name){
		Object value = parmeters.get(name);
		if(value instanceof FileItem){
			return (FileItem) value;
		}else{
			return null;
		}
	}
	
	public String getAuthType() {
		return request.getAuthType();
	}

	public Cookie[] getCookies() {
		return request.getCookies();
	}

	public long getDateHeader(String arg0) {
		return request.getDateHeader(arg0);
	}

	public String getHeader(String arg0) {
		return request.getHeader(arg0);
	}

	public Enumeration getHeaders(String arg0) {
		return request.getHeaders(arg0);
	}

	public Enumeration getHeaderNames() {
		return request.getHeaderNames();
	}

	public int getIntHeader(String arg0) {
		return request.getIntHeader(arg0);
	}

	public String getMethod() {
		return request.getMethod();
	}

	public String getPathInfo() {
		return request.getPathInfo();
	}

	public String getPathTranslated() {
		return request.getPathTranslated();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getQueryString() {
		return request.getQueryString();
	}

	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	public boolean isUserInRole(String arg0) {
		return request.isUserInRole(arg0);
	}

	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
	}

	public String getRequestURI() {
		return request.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}

	public String getServletPath() {
		return request.getServletPath();
	}

	public HttpSession getSession(boolean arg0) {
		return request.getSession(arg0);
	}

	public HttpSession getSession() {
		return request.getSession();
	}

	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return request.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromURL() {
		return request.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	public boolean isRequestedSessionIdFromUrl() {
		return request.isRequestedSessionIdFromUrl();
	}

	public Object getAttribute(String arg0) {
		return request.getAttribute(arg0);
	}

	public Enumeration getAttributeNames() {
		return request.getAttributeNames();
	}

	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		request.setCharacterEncoding(arg0);
	}

	public int getContentLength() {
		return request.getContentLength();
	}

	public String getContentType() {
		return request.getContentType();
	}

	public ServletInputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	public String getParameter(String arg0) {
		Object obj = parmeters.get(arg0);
		if(obj instanceof String){
			return (String) obj;
		}else{
			return null;
		}		
	}

	public Enumeration getParameterNames() {			
		final Iterator keys = parmeters.keySet().iterator();
		return new Enumeration(){

			public boolean hasMoreElements() {				
				return keys.hasNext();
			}

			public Object nextElement() {
				return keys.next();
			}
			
		};
	}

	public String[] getParameterValues(String arg0) {
		String value = getParameter(arg0);
		
		if(value != null){
			return value.split(";");
		}else{
			return null;
		}		
	}

	public Map getParameterMap() {
		return parmeters;
	}

	public String getProtocol() {
		return request.getProtocol();
	}

	public String getScheme() {
		return request.getScheme();
	}

	public String getServerName() {
		return request.getServerName();
	}

	public int getServerPort() {
		return request.getServerPort();
	}

	public BufferedReader getReader() throws IOException {
		return request.getReader();
	}

	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	public void setAttribute(String arg0, Object arg1) {
		request.setAttribute(arg0, arg1);
		
	}

	public void removeAttribute(String arg0) {
		request.removeAttribute(arg0);
		
	}

	public Locale getLocale() {
		return request.getLocale();
	}

	public Enumeration getLocales() {
		return request.getLocales();
	}

	public boolean isSecure() {
		return request.isSecure();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		return request.getRequestDispatcher(arg0);
	}

	@SuppressWarnings("deprecation")
	public String getRealPath(String arg0) {
		return request.getRealPath(arg0);
	}

	public int getRemotePort() {
		return request.getRemotePort();
	}

	public String getLocalName() {
		return request.getLocalName();
	}

	public String getLocalAddr() {
		return request.getLocalAddr();
	}

	public int getLocalPort() {
		return request.getLocalPort();
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

}