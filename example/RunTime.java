package example;

import java.io.IOException;

public class RunTime {
	public static void main(String[] args) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec("notepad.exe E:\\javatest\\Ima.java");
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.destroy();
		rt.exit(0);
		System.exit(0);
	}

}
