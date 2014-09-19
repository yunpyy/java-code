package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * 
 * Java关于文件和文件夹的操作
 *
 */
public class FileOperation {
	public static void main(String[] args) {
		FileOperation fileOperation = new FileOperation();
		fileOperation.copyFile();
	}
	
	//复制文件
	public void copyFile(){
		int bytesum = 0;
		int byteread = 0;
		File oldfile = new File("d:\\test.txt");
		try {
			if(oldfile.exists()){
				FileInputStream inStream = new FileInputStream(oldfile);
				FileOutputStream outStream = new FileOutputStream(new File("d:\\test2.txt"));
				byte[] buffer = new byte[5120];
				int length;
				while((byteread = inStream.read(buffer)) != -1){
					bytesum += byteread;
					System.out.println(bytesum);
					outStream.write(buffer, 0, byteread);
				}
				inStream.close();
				outStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	//复制文件夹
	public void copyFolder(){
		LinkedList<String> folderList = new LinkedList<String>();
		folderList.add("d:\\test");
		LinkedList<String> folderList2 = new LinkedList<String>();
//		folderList2.add("d:\\test2" + "d:\\test".substring("d:\\test".lastIndexOf("\\")));
		folderList2.add("d:\\test2");
		while(folderList.size() > 0){
			(new File(folderList2.peek())).mkdirs();
			File folders = new File(folderList.peek());
			String[] file = folders.list();
			File temp = null;
			try {
				for (int i = 0; i < file.length; i++) {
					if(folderList.peek().endsWith(File.separator)){
						temp = new File(folderList.peek() + File.separator + file[i]);
					}else {
						temp = new File(folderList.peek() + File.separator + file[i]);
					}
					if(temp.isFile()){
						FileInputStream input = new FileInputStream(temp);
						FileOutputStream output = new FileOutputStream(folderList2.peek() + File.separator + (temp.getName()).toString());
						byte[] b = new byte[5120];
						int len;
						while((len = input.read(b)) != -1){
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					if(temp.isDirectory()){
						for(File f : temp.listFiles()){
							if(f.isDirectory()){
								folderList.add(f.getPath());
								folderList2.add(folderList2.peek() + File.separator + f.getName());
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("复制整个文件夹内容操作出错");
				e.printStackTrace();
			}
			folderList.removeFirst();
			folderList2.removeFirst();
		}
	}
	
	//写入文件属性
	public void writeFileAtt(){
		File f = new File("d:\\test.txt");
		try {
			Boolean b = f.setReadOnly();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//读取文件属性
	public void readFileAtt(){
		File f = new File("d:\\test.txt");
		if(f.exists()){
			System.out.println(f.getName() + "的属性如下: 文件长度为:" + f.length());
			System.out.println(f.isFile() ? "是文件" : "不是文件");
			System.out.println(f.isDirectory() ? "是目录" : "不是目录");
			System.out.println(f.canRead() ? "可读取" : "不可读取");
			System.out.println(f.canWrite() ? "不是隐藏文件" : "是隐藏文件");
			System.out.println("文件夹的最后修改日期为:" + new Date(f.lastModified()));
		}
	}
	
	//写入随机文件
	public void writeRanFile(){
		try {
			RandomAccessFile logFile = new RandomAccessFile("d:\\test.txt", "rw");
			long lg = logFile.length();
			logFile.seek(100);
			logFile.writeByte(200);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//写入文件
	public void writeFile(){
		try {
			FileWriter fw = new FileWriter("d:\\test.txt");
			fw.write("写入测试文本");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.out.println("写入文件操作出错");
			e.printStackTrace();
		}
	}
	
	//读取文件
	public void readFile() {
		try {
			FileReader fr = new FileReader("d://test.txt");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null){
				System.out.println(line);
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			System.out.println("读取文件操作出错");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("读取文件操作出错");
			e.printStackTrace();
		}
	}
	
	//清空文件夹
	public void emptyFolder(){
		File delFileFolder = new File("d:\\test");
		try {
			if(!delFileFolder.exists()){
				delFileFolder.mkdir();
			}else {
				File[] files = delFileFolder.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
		} catch (Exception e) {
			System.out.println("清空文件夹操作出错");
			e.printStackTrace();
		}
	}
	
	//删除一个文件夹下的所有文件夹
	public void delInnerFolder(){
		File delfile = new File("d:\\test");
		File[] files = delfile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				files[i].delete();
			}
		}
	}
	                             
	
	//删除文件夹
	public void delFolder(){
		File delFolderPath = new File("d:\\test");
		try {
			delFolderPath.delete();
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}
	
	//删除文件
	public void delFile(){
		File myDelFile = new File("d:\\test.txt");
		try {
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}
	
	//创建文件
	public void createFile(){
		File myFilePath = new File("d:\\test.txt");
		try {
			if(!myFilePath.exists()){
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			myFile.println("写入一行文本信息");
			resultFile.close();
		} catch (Exception e) {
			System.out.println("新建文件操作出错");
			e.printStackTrace();
		}
	}
	
	//创建文件夹
	public void createFolder(){
		File myFolderPath = new File("d:\\test");
		try {
			if(!myFolderPath.exists()){
				myFolderPath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}
}
