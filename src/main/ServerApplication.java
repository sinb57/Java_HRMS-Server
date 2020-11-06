package main;

import main.domain.Hospital;
import main.domain.Manageable;
import main.domain.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerApplication {

    private Scanner scan = new Scanner(System.in);
    private List<Manageable> informationList = new ArrayList<>();

    private void run() {

    }

    private void readAll(String fileName) {
        Scanner fileIn = openFile(fileName);
        Manageable m = null;
        while (fileIn.hasNext()) {
            m.read(fileIn);
            informationList.add(m);
        }
    }

    private Scanner openFile(String fileName) {
        Scanner fileIn = null;
        try {
            fileIn = new Scanner(new File(fileName));
        } catch (IOException e) {
            System.out.println("파일 오픈 실패 " + fileName);
            System.exit(0);
        }
        return fileIn;
    }
}
