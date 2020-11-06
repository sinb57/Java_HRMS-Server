package main.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Patient implements Manageable{
    private String patientId;
    private String password;
    private String name;
    private String phoneNumber;
    private String address;

    public void read(Scanner scan) {
        try {
            patientId = scan.next();
            password = scan.next();
            name = scan.next();
            phoneNumber = scan.next();
            address = scan.next();
        } catch (Exception e) {
            //
        }
    }

}

