package xworker.java.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilJava;

import ognl.OgnlException;
import xworker.util.UtilData;

public class MailActions {
	private static Logger logger = LoggerFactory.getLogger(MailActions.class);
	
	public static Session createSession(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Properties p = new Properties();
		putPropertiesValue(self, p, "mail.smtp.user", "user");
		putPropertiesValue(self, p, "mail.smtp.host", "host");
		putPropertiesValue(self, p, "mail.smtp.port", "port");
		putPropertiesValue(self, p, "mail.smtp.connectiontimeout", "connectiontimeout");
		putPropertiesValue(self, p, "mail.smtp.timeout", "timeout");
		putPropertiesValue(self, p, "mail.smtp.writetimeout", "writetimeout");
		putPropertiesValue(self, p, "mail.smtp.from", "from");
		putPropertiesValue(self, p, "mail.smtp.localhost", "localhost");
		putPropertiesValue(self, p, "mail.smtp.localaddress", "localaddress");
		putPropertiesValue(self, p, "mail.smtp.localport", "localport");
		putPropertiesValue(self, p, "mail.smtp.ehlo", "ehlo");
		putPropertiesValue(self, p, "mail.smtp.auth", "auth");
		putPropertiesValue(self, p, "mail.smtp.auth.mechanisms", "authMchanisms");
		putPropertiesValue(self, p, "mail.smtp.auth.login.disable", "authLoginDisable");
		putPropertiesValue(self, p, "mail.smtp.auth.plain.disable", "authPlainDisable");
		putPropertiesValue(self, p, "mail.smtp.auth.digest-md5.disable", "authDigest-md5Disable");
		putPropertiesValue(self, p, "mail.smtp.auth.ntlm.disable", "authNtlmDisable");
		putPropertiesValue(self, p, "mail.smtp.auth.ntlm.domain", "authNtlmDomain");
		putPropertiesValue(self, p, "mail.smtp.auth.ntlm.flags", "authNtlmFlags");
		putPropertiesValue(self, p, "mail.smtp.submitter", "submitter");
		putPropertiesValue(self, p, "mail.smtp.dsn.notify", "dsnNotify");
		putPropertiesValue(self, p, "mail.smtp.dsn.ret", "dsnRet");
		putPropertiesValue(self, p, "mail.smtp.allow8bitmime", "allow8bitmime");
		putPropertiesValue(self, p, "mail.smtp.sendpartial", "sendpartial");
		putPropertiesValue(self, p, "mail.smtp.sasl.enable", "sasEnable");
		putPropertiesValue(self, p, "mail.smtp.sasl.mechanisms", "saslMechanisms");
		putPropertiesValue(self, p, "mail.smtp.sasl.authorizationid", "saslAuthorizationid");
		putPropertiesValue(self, p, "mail.smtp.sasl.realm", "saslRealm");
		putPropertiesValue(self, p, "mail.smtp.quitwait", "quitwait");
		putPropertiesValue(self, p, "mail.smtp.reportsuccess", "reportsuccess");
		putPropertiesValue(self, p, "mail.smtp.socketFactory", "socketFactory");
		putPropertiesValue(self, p, "mail.smtp.socketFactory.class", "socketFactoryClass");
		putPropertiesValue(self, p, "mail.smtp.socketFactory.fallback", "socketFactoryFallback");
		putPropertiesValue(self, p, "mail.smtp.socketFactory.port", "socketFactoryPort");
		putPropertiesValue(self, p, "mail.smtp.ssl.enable", "sslEnable");
		putPropertiesValue(self, p, "mail.smtp.ssl.checkserveridentity", "sslCheckserveridentity");
		putPropertiesValue(self, p, "mail.smtp.ssl.trust", "sslTrust");
		putPropertiesValue(self, p, "mail.smtp.ssl.socketFactory", "sslSocketFactory");
		putPropertiesValue(self, p, "mail.smtp.ssl.socketFactory.class", "sslSocketFactoryClass");
		putPropertiesValue(self, p, "mail.smtp.ssl.socketFactory.port", "sslSocketFactoryPort");
		putPropertiesValue(self, p, "mail.smtp.ssl.protocols", "sslProtocols");
		putPropertiesValue(self, p, "mail.smtp.ssl.ciphersuites", "sslCiphersuites");
		putPropertiesValue(self, p, "mail.smtp.starttls.enable", "starttlsEnable");
		putPropertiesValue(self, p, "mail.smtp.starttls.required", "starttlsRequired");
		putPropertiesValue(self, p, "mail.smtp.socks.host", "socksHost");
		putPropertiesValue(self, p, "mail.smtp.socks.port", "socksPort");
		putPropertiesValue(self, p, "mail.smtp.mailextension", "mailextension");
		putPropertiesValue(self, p, "mail.smtp.userset", "userset");
		putPropertiesValue(self, p, "mail.smtp.noop.strict", "noopStrict");
		

		MailAuthenticator auth = null;
		if("true".equals(p.get("mail.smtp.auth"))){
			auth = new MailAuthenticator(self.getString("authUser"), self.getString("authPassword")); 
		}
		Session session = Session.getInstance(p, auth);
		if(self.getBoolean("debug")) {
			session.setDebug(true);
		}
		
		return session;
	}
	
	public static Object getMailSession(ActionContext actionContext){
		Thing sessionThing = (Thing) actionContext.get("sessionThing");
		return sessionThing.doAction("create", actionContext);
	}
	
	public static void sendMailAction(ActionContext actionContext) throws OgnlException, MessagingException{
		Thing self = (Thing) actionContext.get("self");
		Object to = self.doAction("getTo", actionContext);
		Object cc = self.doAction("getCc", actionContext);
		Object bcc = self.doAction("getBcc", actionContext);
		Object attachs = self.doAction("getAttachs", actionContext);
		String subject = self.doAction("getSubject", actionContext);
		String content = self.doAction("getContent", actionContext);
		String contentType = self.doAction("getContentType", actionContext);
		Thing sessionThing = self.doAction("getSessionThing", actionContext);
		Session session = (Session) sessionThing.doAction("create", actionContext);	
		sendMail(session, sessionThing.getString("from"), to, cc, bcc, attachs, subject, content, contentType);				
	}

	public static void sendMail(String sessionThingPath, Object to, String subject, String content, ActionContext actionContext) throws MessagingException{
		sendMail(sessionThingPath, to, null, null, null, subject, content, "text/html; charset=UTF-8", actionContext);		
	}
	
	public static void sendMail(String sessionThingPath,  Object to, Object cc, Object bcc, Object attachs, String subject, String content, String contentType, ActionContext actionContext) throws MessagingException{
		Thing sessionThing = World.getInstance().getThing(sessionThingPath);
		if(sessionThing != null){
			Session session = (Session) sessionThing.doAction("create", actionContext);			
			sendMail(session, sessionThing.getString("from"), to, cc, bcc, attachs, subject, content, contentType);
		}else{
			logger.info("send mail sessionThing not exists, path=" + sessionThingPath);
		}
	}	
	

	public static void sendMail(Session session, String from, Object to, Object cc, Object bcc, Object attachs, String subject, String content, String contentType) throws MessagingException{
		// 创建邮件消息
		MimeMessage message = new MimeMessage(session);
		
		//标题
		message.setSubject(subject == null ? "No subject" : subject);
		
		//是否有附件，附件暂时只支持文件
		if(attachs != null){
			MimeMultipart mimemultipart = new MimeMultipart();
			//添加文本正文
			MimeBodyPart textBodyPart = new MimeBodyPart(); 
			textBodyPart.setContent(content, contentType);
			mimemultipart.addBodyPart(textBodyPart);
			
			//添加附件
			Iterable<DataSource> ds = UtilJava.getIterable(attachs);
			for(DataSource dataSource : ds){
				MimeBodyPart messageBodyPart = new MimeBodyPart(); 
		        messageBodyPart.setDataHandler(new DataHandler(dataSource)); 
		        messageBodyPart.setFileName(dataSource.getName()); 
		        mimemultipart.addBodyPart(messageBodyPart);				
			}
			
			message.setContent(mimemultipart);
		}else{
			message.setContent(content, contentType);
		}
		
		//设置邮件发送者
		message.setFrom(new InternetAddress(from));
		
		//接受者
		for(Object toObj : UtilJava.getIterable(to)){
			if(toObj != null && !"".equals(toObj)) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toObj.toString()));
			}
		}
		
		//抄送
		for(Object ccObj : UtilJava.getIterable(cc)){
			if(ccObj != null && !"".equals(ccObj)) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccObj.toString()));
			}
		}
		
		//暗送
		for(Object bccObj : UtilJava.getIterable(bcc)){
			if(bccObj != null && !"".equals(bccObj)) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccObj.toString()));
			}
		}
		
		//设置发送时间
		message.setSentDate(new Date());
		
		//发送邮件
		Transport.send(message);
	}

	public static void sendMail(ActionContext actionContext) throws MessagingException{
		//FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Session session = (Session) actionContext.get("session");
		String from  = session.getProperty("mail.smtp.from");
		//String from = (String) actionContext.get("from");

		Object to = actionContext.get("to");
		Object cc = actionContext.get("cc");
		Object bcc = actionContext.get("bcc");
		String subject = (String) actionContext.get("subject");
		String content = (String) actionContext.get("content");
		String contentType = (String) actionContext.get("contentType");
		Object attachs = actionContext.get("attachs");
		if(contentType == null){
			contentType = "text/html; charset=UTF-8";
		}
		
		sendMail(session, from, to, cc, bcc, attachs, subject, content, contentType);
	}
	
	private static void putPropertiesValue(Thing self, Properties p, String name, String selfAttrName){
		String value = self.getStringBlankAsNull(selfAttrName);
		if(value != null){
			p.put(name, value);
		}
	}
		
	private static class MailAuthenticator extends Authenticator{
		private String user;
		private String password;
		
		public MailAuthenticator(String user, String password){
			this.user = user;
			this.password = password;
		}
		
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password);
		}
		
	}
	
	public static Object getAttachs(ActionContext actionContext) throws OgnlException, IOException {
		Thing self = actionContext.getObject("self");
		String attachs = self.getStringBlankAsNull("attachs");
		if(attachs != null) {
			return UtilData.getData(attachs, actionContext);
		}else {
			List<DataSource> ds = new ArrayList<DataSource>();
			for(Thing ats : self.getChilds("Attachs")) {
				for(Thing at : ats.getChilds()) {
					DataSource d = at.doAction("run", actionContext);
					if(d != null) {
						ds.add(d);
					}
				}
			}
			
			if(ds.size() > 0) {
				return ds;
			}else {
				return null;
			}
		}
	}
}
