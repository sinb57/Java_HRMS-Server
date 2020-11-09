package main.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Patient implements Manageable{
    private String patientId;
    private String password;
    private String patientName;
    private String phoneNumber;

    public void read(StringTokenizer dataTokenizer) {
        patientId = dataTokenizer.nextToken();
        password = dataTokenizer.nextToken();
        patientName = dataTokenizer.nextToken();
        phoneNumber = dataTokenizer.nextToken();
    }

}

