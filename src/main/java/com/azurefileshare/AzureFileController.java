package com.azurefileshare;

import java.io.InputStream;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;

@RestController
public class AzureFileController {

	@Autowired
	private static final Logger logger = LogManager.getLogger(AzureFileController.class);
	String fileName;

	@GetMapping("/upload")
	public Boolean login() {

		logger.info("FILE UPLOAD");

		final String storageConnectionString = "<accesskey>";

		try {
			LocalDateTime currentTime = LocalDateTime.now();
			LocalDate date = currentTime.toLocalDate();
			String strDisplayName = "";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm.ss");
			LocalTime today = LocalTime.now();
			String timeString = today.format(formatter);
			strDisplayName = date + "_" + timeString + "_" + strDisplayName;
			System.out.println("strDisplayName************" + strDisplayName);
			fileName = strDisplayName + "sample" + ".pdf";
			ShareClient shareClient = new ShareClientBuilder().connectionString(storageConnectionString)
					.shareName("ordermanagement").buildClient();
			Boolean shareClientexist = shareClient.exists();
			if (shareClientexist.equals(false)) {
				System.out.println("shareClient already not exist creating...");
				shareClient.create();
			} else {
				System.out.println("shareClient already exist");
			}

			ShareDirectoryClient dirClient = new ShareFileClientBuilder().connectionString(storageConnectionString)
					.shareName("ordermanagement").resourcePath("").buildDirectoryClient();
			ShareFileClient fileClient = dirClient.getFileClient(fileName);
			Boolean fileClientexist = fileClient.exists();
			if (fileClientexist.equals(false)) {
				System.out.println("fileClient already not exist creating...");
				fileClient.create(4096);
				fileClient.uploadFromFile("D:\\fileupload\\2020-12-23_18.51.41_ftp.pdf");
			} else {
				System.out.println("fileClient already exist");
			}

			ShareDirectoryClient dirClient01 = new ShareFileClientBuilder().connectionString(storageConnectionString)
					.shareName("ordermanagement").resourcePath("MME").buildDirectoryClient();
			Boolean directoryexist = dirClient01.exists();
			if (directoryexist.equals(false)) {
				System.out.println("dirClient01 already not exist creating...");
				dirClient01.create();
			} else {
				System.out.println("dirClient01 already exist");
			}

			ShareFileClient fileClient01 = dirClient01.getFileClient(fileName);
			Boolean fileClientexist01 = fileClient01.exists();
			if (fileClientexist01.equals(false)) {
				System.out.println("fileClient01 already not exist creating...");
				fileClient01.create(4096);
				fileClient01.uploadFromFile("D:\\fileupload\\2020-12-23_18.51.41_ftp.pdf");
			} else {
				System.out.println("fileClient01 already exist");
			}
			logger.info("shareClient uploadFromFile");
			return true;
		} catch (Exception e) {
			System.out.println("createFileShare exception: " + e.getMessage());
			return false;
		}
	}

	@GetMapping("/download")
	public Boolean download() {
		logger.info("FILE DOWNLOAD");
		byte[] data = null;
	//	String fileName = "2020-12-23_18.51.41_ftp.pdf";
		String destPath = "D:\\filedownload\\" + fileName;
		final String storageConnectionString = "<accesskeystring>";
		String SASToken = "?sv=2019-12-12&ss=bfqt&srt=o&sp=rwdlacupx&se=2022-08-25T22:41:41Z&st=2020-12-23T14:41:41Z&spr=https&sig=jkjjlFDEqfEK%2FggkyyYEeAGHinK4Ho3j%2BlmHCxVtm6k%3D";
// String ordermanagement;
		ShareDirectoryClient dirClient = new ShareFileClientBuilder().connectionString(storageConnectionString)
				.shareName("ordermanagement").resourcePath("").buildDirectoryClient();
		ShareFileClient fileClient = dirClient.getFileClient(fileName);
		String fileURL = fileClient.getFileUrl() + SASToken;
		try {
			InputStream streamDownloadedFile = new URL(fileURL).openStream();
			data = IOUtils.toByteArray(streamDownloadedFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 fileClient.downloadToFile(destPath);
		return true;
	}

	@GetMapping("/delete")
	public Boolean delete() {
		logger.info("FILE DELETE");
	//	String fileName = "MME\\2020-12-23_18.51.41_ftp.pdf";

		final String storageConnectionString = "<accesskeystring>";
// String ordermanagement;
		ShareDirectoryClient dirClient = new ShareFileClientBuilder().connectionString(storageConnectionString)
				.shareName("ordermanagement").resourcePath("").buildDirectoryClient();
		ShareFileClient fileClient = dirClient.getFileClient(fileName);
		fileClient.delete();
		return true;
	}
}