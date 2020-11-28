package main.collectInterface;

import java.io.File;
import java.util.Scanner;

public interface Manager {
	
	void readAll(String fileName);
		
    default Scanner openFile(String filePath) {
    	Scanner scanner = null;
    	try {

    		scanner = new Scanner(new File(filePath));
    	} catch (Exception e) {
    		System.out.printf("파일 오픈 실패: %s\n", filePath);
    		System.exit(0);
    	}
    	return scanner;
    }
}
