package main.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Hospital implements Manageable {
    private String hospitalId;
    private String password;
    private String name;
    private String phoneNumber;
    private String address;
    private ArrayList<LocalDateTime> careTimes;

    public void read(Scanner scan) {
        try {
            hospitalId = scan.next();
            password = scan.next();
            name = scan.next();
            phoneNumber = scan.next();
            address = scan.next();
        } catch (Exception e) {
            //
        }
    }
}
