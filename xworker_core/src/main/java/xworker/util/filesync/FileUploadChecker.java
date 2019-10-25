package xworker.util.filesync;

public interface FileUploadChecker {
	/**
	 * 检查用户是否合法。
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean checkUser(String user, String password);
}
