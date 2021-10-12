package xworker.manong;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.codes.TxtCoder;

import xworker.util.XWorkerUtils;

public class MaNongClient {
	private static HttpClient httpClient = new DefaultHttpClient();
	private static String userThingPath = "_local.manong.User";
	private static Logger logger = LoggerFactory.getLogger(MaNongClient.class);
	
	/**
	 * 返回服务器的根地址。
	 * 
	 * @return
	 */
	public static String getBaseUrl(){
		World world = World.getInstance();
		String configThingPath = "_local.manong.MaNongConfig";
		
		Thing config = world.getThing(configThingPath);
		if(config == null){
			config = new Thing("xworker.manong.MaNongConfig");
			config.initDefaultValue();			
			config.saveAs("_local", configThingPath);
		}
		
		return config.getString("serverUrl");
	}
	
	/**
	 * 注册用户信息。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void regist(ActionContext actionContext) throws IOException{
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		Shell shell = (Shell) explorerContext.get("shell");
		
		World world = World.getInstance();
		Thing userThing = world.getThing(userThingPath);
		if(userThing == null){
			MaNongUtils.showMessage(shell, SWT.ICON_ERROR, "注册用户信息", "用于保存用户信息的事物不存在，thingPath=_local.manong.User");
			return;
		}

		try{
			Thing thing = XWorkerUtils.httpThingSendRequest(getBaseUrl() + "do?sc=xworker.manong.web.UserRegist", "registUser", userThing, actionContext);
			MaNongUtils.showMessage("注册用户信息", thing, shell);
		}catch(Exception e){
			logger.error("码农项目注册用户信息失败", e);
			MaNongUtils.showMessage(shell, SWT.ICON_ERROR, "注册用户信息", "码农项目注册用户信息失败:" + e.getMessage());
		}
	}
	
	public static void checkShareProject() throws IOException {
		//检查_share项目是否存在
		File configProperties = new File(World.getInstance().getPath() + "/projects/_share/config.properties");
		if(configProperties.exists() == false) {
			configProperties.getParentFile().mkdirs();
			
			FileOutputStream ffout = new FileOutputStream(configProperties);
			ffout.write("link=".getBytes());
			ffout.close();
		}
		
		//加载_share项目的事物管理器
		World world = World.getInstance();
		if(world.getThingManager("_share") == null) {
			world.addFileThingManager("_share", configProperties.getParentFile(), true, false);
		}
	}
	
	/**
	 * 修改用户的密码信息。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void modifyPassword(ActionContext actionContext) throws IOException{
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		Shell shell = (Shell) explorerContext.get("shell");
		
		World world = World.getInstance();
		Thing userThing = world.getThing(userThingPath);
		if(userThing == null){
			MaNongUtils.showMessage(shell, SWT.ICON_ERROR, "修改密码", "用于保存用户信息的事物不存在，thingPath=_local.manong.User");
			return;
		}

		try{
			Thing thing = XWorkerUtils.httpThingSendRequest(getBaseUrl() + "do?sc=xworker.manong.web.UserChangePassword", "changePassword", userThing, actionContext);
			MaNongUtils.showMessage("修改密码", thing, shell);
			
			userThing.set("password", userThing.getString("newPassword"));
			userThing.set("newPassword", "");
			userThing.save();
		}catch(Exception e){
			logger.error("码农项目修改密码失败", e);
			MaNongUtils.showMessage(shell, SWT.ICON_ERROR, "修改密码", "码农项目修改密码失败:" + e.getMessage());
		}
	}
	
	/**
	 * 从服务器下载码农项目。
	 * 
	 * @param projectThingPath
	 */
	public static void download(String projectThingPath){
		String projectZipPath = projectThingPath.replace('.', '/') + ".zip";
		HttpGet httpGet = new HttpGet(getBaseUrl() + "/do?sc=xworker.manong.web.Download&projectId=" + projectThingPath);
		HttpResponse res= null;
		try{
			res = httpClient.execute(httpGet);
			if(res.getStatusLine().getStatusCode() == 200 && res.getEntity().isStreaming()){
				//保存压缩文件
				String fileName = World.getInstance().getPath() + "/work/manong/" + projectZipPath;
				byte[] bytes = new byte[1024];
				int length = -1;
				InputStream hin = res.getEntity().getContent();
				File outFile =  new File(fileName);
				if(!outFile.getParentFile().exists()){
					outFile.getParentFile().mkdirs();
				}
				FileOutputStream fout = new FileOutputStream(fileName);
				while((length = hin.read(bytes)) > 0){
					fout.write(bytes, 0, length);
				}
				fout.close();
				
				//解压文件
				ZipInputStream zin = new ZipInputStream(new FileInputStream(fileName));
				ZipEntry zipEntry = null;
				while((zipEntry = zin.getNextEntry()) != null){
					File pfile = new File(World.getInstance().getPath() + "/projects/" + zipEntry.getName());
					if(!pfile.getParentFile().exists()){
						pfile.getParentFile().mkdirs();
					}
					fout = new FileOutputStream(pfile);
					while((length = zin.read(bytes)) > 0){
						fout.write(bytes, 0, length);
					}
					fout.close();
				}
				zin.close();
				
				checkShareProject();
				
				//查看是否存在WEB资源
				int index = projectThingPath.lastIndexOf(".");
				if(index != -1){
					File pfile = new File(World.getInstance().getPath() + "/projects/_share/things/" + projectThingPath.substring(0, index).replace('.','/') + "/resources/");
					if(pfile.exists() && pfile.isDirectory()){
						String ss[] = projectThingPath.split("[.]");
						File target = new File(World.getInstance().getPath() + "/webroot/" + ss[0] + "/" + ss[1]);
						
						FileUtils.copyDirectory(pfile, target);
					}
				}
				XWorkerUtils.ideShowMessageBox("下载项目", "项目下载和安装成功！", SWT.ICON_INFORMATION | SWT.OK);
			}else{
				if(res.getStatusLine().getStatusCode() == 404){
					XWorkerUtils.ideShowMessageBox("下载项目", "服务器上项目不存在！", SWT.ICON_WARNING | SWT.OK);
				}else{
					XWorkerUtils.ideShowMessageBox("下载项目", "服务器返回不是文件！服务器返回的类型是：" + res.getEntity().getContentType().getValue() 
						+ "\n" + EntityUtils.toString(res.getEntity()), SWT.ICON_WARNING | SWT.OK);
				}
			}
		}catch(Exception e){
			logger.error("下载项目错误", e);
			XWorkerUtils.ideShowMessageBox("下载项目", "下载项目错误:" + e.getMessage(), SWT.ICON_ERROR | SWT.OK);
		}finally{
			try {
				if(res != null){
					EntityUtils.consume(res.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 从服务器下载码农项目。
	 * 
	 * @param actionContext
	 */
	public static void download(ActionContext actionContext){
		Thing project = (Thing) actionContext.get("currentThing");
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		Shell shell = (Shell) explorerContext.get("shell");
		
		MessageBox confirm = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
		confirm.setText("上传项目");
		confirm.setMessage("下载会覆盖当前项目，确认吗？");
		if(SWT.CANCEL == confirm.open()){
			return;
		}
		download(project.getMetadata().getPath());
	}
	
	/**
	 * 上传码农项目到服务器。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void upload(ActionContext actionContext) throws IOException{
		Thing project = (Thing) actionContext.get("currentThing");
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		Shell shell = (Shell) explorerContext.get("shell");
		
		MessageBox confirm = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
		confirm.setText("上传项目");
		confirm.setMessage("上传项目到码农的世界需要接受开源协议Apache2.0，确认吗？");
		if(SWT.CANCEL == confirm.open()){
			return;
		}
		
		World world = World.getInstance();
		Thing user = world.getThing(userThingPath);
		if(user == null){
			//需要先创建用户账户
			throw new MaNongException(MaNongI18n.getMessage("needUserInfo"));
		}
		
		String userName = user.getString("userName");
		String password = user.getString("password");
		
		//压缩项目文件
		File zipFile = MaNongProjectZipper.encodeToZip(project);
		
		//把项目打包一同发送
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		TxtCoder.encode(project, new PrintWriter(new OutputStreamWriter(bout)), new HashMap<Thing, String>());
		
		//上传项目文件
		HttpPost httpPost = new HttpPost(getBaseUrl() + "/do?sc=xworker.manong.web.ProjectUpload");
		MultipartEntity entity = new MultipartEntity();
		
		try{
			Integer.parseInt(project.getString("majorVersion"));
			Integer.parseInt(project.getString("minorVersion"));
		}catch(Exception e){
			MaNongUtils.showMessage(shell, 2, "上传项目", "最大版本号和最低版本号必须是数字！");
			return;
		}
		if(project.getStringBlankAsNull("name") == null){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目名称不能为空！");
			return;
		}
		if(project.getStringBlankAsNull("label") == null){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目标签不能为空！");
			return;
		}
		if(project.getStringBlankAsNull("keyWords") == null){
			MaNongUtils.showMessage(shell, 2, "上传项目", "关键字不能为空！");
			return;
		}
		if(project.getStringBlankAsNull("summary") == null){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目摘要不能为空！");
			return;
		}
		if(project.getStringBlankAsNull("description") == null){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目描述不能为空！");
			return;
		}
		String summary = project.getString("summary");
		summary = summary.replaceAll("<[^>]+>", "");
		if(summary != null && summary.getBytes().length > 512){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目摘要最大长度超过了512字节！");
			return;
		}
		String description = project.getString("description");
		if(description != null && description.getBytes().length > 20480){
			MaNongUtils.showMessage(shell, 2, "上传项目", "项目介绍最大长度超过了20480字节！");
			return;
		}
		
		FileBody fileBody = new FileBody(zipFile);		
		Charset charset = Charset.forName("utf-8");
		entity.addPart("file", fileBody);
		entity.addPart("userName", new StringBody(userName));
		entity.addPart("password", new StringBody(password));
		entity.addPart("projectId", new StringBody(project.getMetadata().getPath()));
		entity.addPart("name", new StringBody(project.getString("name"), charset));
		entity.addPart("label", new StringBody(project.getString("label"), charset));
		entity.addPart("keyWords", new StringBody(project.getString("keyWords"), charset));
		entity.addPart("majorVersion", new StringBody(project.getString("majorVersion")));
		entity.addPart("minorVersion", new StringBody(project.getString("minorVersion")));		
		entity.addPart("summary", new StringBody(summary, charset));
		entity.addPart("description", new StringBody(project.getString("description"), charset));		
		ByteArrayBody projectBody = new ByteArrayBody(bout.toByteArray(), "project");
		entity.addPart("project", projectBody);
		
		HttpResponse res= null;
		try{
			httpPost.setEntity(entity);
			res = httpClient.execute(httpPost);
			String result = EntityUtils.toString(res.getEntity(), "utf-8");
			if(result.indexOf("|") != -1){
				String rs[] = result.split("[|]");
				MaNongUtils.showMessage("上传项目", Integer.parseInt(rs[0]), rs[1], shell);
			}else{
				MaNongUtils.showMessage(shell, 1, "上传项目", "服务器返回未知格式消息：" + result);
			}
		}catch(Exception e){
			logger.error("上传项目错误", e);
			MaNongUtils.showMessage(shell, 2, "上传项目", "上传项目失败：" + e.getMessage());
		}finally{
			try {
				if(res != null){
					EntityUtils.consume(res.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除码农项目。
	 * 
	 * @param actionContext
	 * @throws UnsupportedEncodingException 
	 */
	public static void delete(ActionContext actionContext) throws UnsupportedEncodingException{
		Thing project = (Thing) actionContext.get("currentThing");
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		Shell shell = (Shell) explorerContext.get("shell");
		
		MessageBox confirm = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
		confirm.setText("删除项目");
		confirm.setMessage("确认要删除这个项目么？");
		if(SWT.CANCEL == confirm.open()){
			return;
		}
		
		World world = World.getInstance();
		Thing user = world.getThing(userThingPath);
		if(user == null){
			//需要先创建用户账户
			throw new MaNongException(MaNongI18n.getMessage("needUserInfo"));
		}
		
		String userName = user.getString("userName");
		String password = user.getString("password");
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userName", userName));
		nvps.add(new BasicNameValuePair("password", password));
		nvps.add(new BasicNameValuePair("projectId", project.getMetadata().getPath()));

		//上传项目文件
		HttpPost httpPost = new HttpPost(getBaseUrl() + "/do?sc=xworker.manong.web.ProjectDelete");
		
		HttpResponse res= null;
		try{
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

			res = httpClient.execute(httpPost);
			String result = EntityUtils.toString(res.getEntity(), "utf-8");
			if(result.indexOf("|") != -1){
				String rs[] = result.split("[|]");
				MaNongUtils.showMessage("删除项目", Integer.parseInt(rs[0]), rs[1], shell);
			}else{
				MaNongUtils.showMessage(shell, 1, "删除项目", "服务器返回未知格式消息：" + result);
			}
		}catch(Exception e){
			logger.error("删除项目错误", e);
			MaNongUtils.showMessage(shell, 2, "删除项目", "删除项目失败：" + e.getMessage());
		}finally{
			try {
				if(res != null){
					EntityUtils.consume(res.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
