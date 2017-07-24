package com.sticker.basic.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class FileUtils {
	static int byteLength = 1024;

	// byte数组到图片到硬盘上
	public static void byte2file(byte[] data, String path) {
		// if(data.length<3||path.equals("")) return;//判断输入的byte是否为空
		try {

			String strPath = URLDecoder.decode(path, "utf-8");
			File file = new File(strPath);
			File fileParent = file.getParentFile();
			if (!fileParent.exists()) {
				fileParent.mkdirs();
			}
			file.createNewFile();

			FileImageOutputStream imageOutput = new FileImageOutputStream(file);// 打开输入流
			imageOutput.write(data, 0, data.length);// 将byte写入硬盘
			imageOutput.close();
			System.out.println("Make file success,Please find file in " + strPath);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * 读取一个文件到字符串里.
	 * 
	 * @param sFileName
	 *            文件名
	 * @param sEncode
	 *            String
	 * @return 文件内容
	 */
	public static String readTextFile(String sFileName, String sEncode) {
		StringBuffer sbStr = new StringBuffer();

		try {
			File ff = new File(sFileName);
			InputStreamReader read = new InputStreamReader(new FileInputStream(ff), sEncode);
			BufferedReader ins = new BufferedReader(read);

			String dataLine = "";
			while (null != (dataLine = ins.readLine())) {
				sbStr.append(dataLine);
			}

			ins.close();
			return sbStr.toString();
		} catch (Exception e) {
			System.out.println("read Text File Error");
			return "error";
		}

	}

	/*
	 * 生成随机文件名
	 */
	public static String generateRandomFilename(String symbol) {
		SimpleDateFormat simpleDateFormat;

		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

		Date date = new Date();

		String str = simpleDateFormat.format(date);

		Random random = new Random();

		int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

		return symbol + rannum + str;// 当前时间
	}

	/**
	 * 读取ZIP文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void readZipFile(String file, Handler<JsonArray> handler) {
		ZipFile zf;
		try {
			zf = new ZipFile(file);
			InputStream in = new BufferedInputStream(new FileInputStream(file));//
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze;
			JsonArray result = new JsonArray();
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {

				} else {
					System.err.println("file - " + ze.getName() + " : " + ze.getSize() + " bytes");
					long size = ze.getSize();
					if (size > 0) {

						BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));

						String line;
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
						br.close();
					}
				}
			}
			zin.closeEntry();
			handler.handle(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.handle(null);
		}
	}

	/**
	 * 读取zip文件中制定文件的内容
	 * 
	 * @param zipFile
	 *            目标zip文件对象
	 * @param readFileName
	 *            目标读取文件名字
	 * @return 文件内容
	 * @throws ZipException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static String getZipFileContent(File zipFile, String readFileName) throws ZipException, IOException {
		StringBuilder content = new StringBuilder();
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		ZipEntry ze;
		// 枚举zip文件内的文件/
		while (entries.hasMoreElements()) {
			ze = entries.nextElement();
			// 读取目标对象
			if (ze.getName().equals(readFileName)) {
				Scanner scanner = new Scanner(zip.getInputStream(ze));
				while (scanner.hasNextLine()) {
					content.append(scanner.nextLine());
				}
				scanner.close();
			}
		}
		zip.close();

		return content.toString();
	}

	/**
	 * 解压zip文件
	 * 
	 * @param zipFile
	 *            要解压的zip文件对象
	 * @param unzipFilePath
	 *            解压目的绝对路径
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Future<JsonArray> unzip(File zipFile, String unzipFilePath) throws IOException {
		Future<JsonArray> future = Future.future();
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
		ZipEntry ze;
		String unzipEntryPath;
		String unzipEntryDirPath;
		int index;
		File unzipEntryDir;
		BufferedOutputStream bos;
		BufferedInputStream bis;
		byte[] data = new byte[byteLength];

		// 创建解压后的文件夹
		File unzipDir = new File(unzipFilePath);
		if (!unzipDir.exists() || !unzipDir.isDirectory()) {
			unzipDir.mkdir();
		}
		JsonArray result = new JsonArray();
		// 解压
		while (entries.hasMoreElements()) {
			// 获取下一个解压文件
			ze = (ZipEntry) entries.nextElement();
			if (ze.isDirectory()) {
				String ss=ze.getName();
			} else {
				unzipEntryPath = unzipFilePath + File.separator + ze.getName();
				index = unzipEntryPath.lastIndexOf(File.separator);
				// 获取解压文件上层目录
				if (index != -1) {
					unzipEntryDirPath = unzipEntryPath.substring(0, index);
				} else {
					unzipEntryDirPath = "";
				}
				// 创建解压文件上层目录
				unzipEntryDir = new File(unzipEntryPath);
				File fileParent = unzipEntryDir.getParentFile();
				if (!fileParent.exists()) {
					fileParent.mkdirs();
				}
//				unzipEntryDir = new File(unzipEntryPath);
//				if (!unzipEntryDir.exists() || !unzipEntryDir.isDirectory()) {
//					unzipEntryDir.mkdir();
//				}
				// 写出解压文件
				bos = new BufferedOutputStream(new FileOutputStream(unzipEntryPath));
				bis = new BufferedInputStream(zip.getInputStream(ze));
				while (bis.read(data, 0, byteLength) != -1) {
					bos.write(data);
				}
				bis.close();
				bos.flush();
				bos.close();
				if(unzipEntryPath.contains(".shp")){
				result.add(unzipEntryPath);
				}
			}
		}
		zip.close();
		future.complete(result);
		return future;
	}

}
