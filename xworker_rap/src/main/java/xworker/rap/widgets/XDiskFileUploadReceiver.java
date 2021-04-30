package xworker.rap.widgets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadReceiver;

/**
 * Modified form org.eclipse.rap.fileupload.DiskFileUploadReceiver.
 * 
 * @author zyx
 *
 */
public class XDiskFileUploadReceiver extends FileUploadReceiver {

	private static final String DEFAULT_TARGET_FILE_NAME = "upload.tmp";
	private static final String TEMP_DIRECTORY_PREFIX = "fileupload_";

	private final List<File> targetFiles;
	private final Map<File, String> contentTypes;
	private final Map<String, File> detialTargetFiles;
	private File dir;

	public XDiskFileUploadReceiver(File dir) {
		this.dir = dir;
		targetFiles = new ArrayList<>();
		contentTypes = new HashMap<File, String>();
		detialTargetFiles = new HashMap<String, File>();
	}

	@Override
	public void receive(InputStream dataStream, FileDetails details) throws IOException {
		File targetFile = createTargetFile(details);
		targetFiles.add(targetFile);
		contentTypes.put(targetFile, details.getContentType());
		detialTargetFiles.put(details.getFileName(), targetFile);
		
		FileOutputStream outputStream = new FileOutputStream(targetFile);
		try {
			copy(dataStream, outputStream);
		} finally {
			outputStream.close();
		}		
	}

	public String getContentType(File uploadedFile) {
		return contentTypes.get(uploadedFile);
	}

	public File[] getTargetFiles() {
		return targetFiles.toArray(new File[0]);
	}

	public File getTargetFile(FileDetails fileDetial) {
		return detialTargetFiles.get(fileDetial.getFileName());
	}
	
	protected File createTargetFile(FileDetails details) throws IOException {
		String fileName = DEFAULT_TARGET_FILE_NAME;
		if (details != null && details.getFileName() != null) {
			fileName = details.getFileName();
		}
		File result = new File(dir == null ? createTempDirectory() : dir, fileName);
		result.createNewFile();
		return result;
	}


	private static File createTempDirectory() throws IOException {
		File result = File.createTempFile(TEMP_DIRECTORY_PREFIX, "");
		result.delete();
		if (result.mkdir()) {
			result.deleteOnExit();
		} else {
			throw new IOException("Unable to create temp directory: " + result.getAbsolutePath());
		}
		return result;
	}

	private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[8192];
		boolean finished = false;
		while (!finished) {
			int bytesRead = inputStream.read(buffer);
			if (bytesRead != -1) {
				outputStream.write(buffer, 0, bytesRead);
			} else {
				finished = true;
			}
		}
	}

}
