package com.sokolowski.maciej.application.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.sokolowski.maciej.application.constants.Consts;

public class FileHandler {

	/**
	 * Method to retrieve a file from provided URL
	 * 
	 * @param url String url from which user wants to retrieve a file
	 * @param savePath path where to save a file
	 * @param filename name of the file to save
	 */
	public void retrieveFileFromUrl(String url, String savePath, String filename) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);

		// TODO implement filename based on date to prevent downloading file every time
		// call is made
		File file = new File(savePath + filename);
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response != null && response.getStatusLine() != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == Status.OK.getStatusCode()) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try (FileOutputStream outstream = new FileOutputStream(file)) {
							entity.writeTo(outstream);
						}
					}
				}
			}
			httpClient.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used to unzip files
	 * 
	 * @param pathToZipFile path to zip file that needs to be unzipped as String
	 * @return list of filenames unzipped
	 */
	public List<String> unzipFile(String pathToZipFile) {

		// Returns list of all filenames in zip file to be able to easily find/iterate
		// through all of them
		List<String> listOfFilenames = new ArrayList<String>();
		File destDir = new File(Consts.GENERAL_PATH_TO_FILES);
		byte[] buffer = new byte[1024];
		ZipInputStream zis;
		try {
			zis = new ZipInputStream(new FileInputStream(pathToZipFile));

			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				listOfFilenames.add(zipEntry.getName());
				File newFile = newFile(destDir, zipEntry);
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfFilenames;
	}
	
	/**
	 * Creation of the file based on destination directory and zip entry
	 * 
	 * @param pathToZipFile directory path to where zip file is
	 * @param zipEntry 
	 * @return newly created File
	 */

	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
