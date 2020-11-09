package main.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Hospital implements Manageable {
    private String hospitalId;
    private String password;
    private String hospitalName;
    private String phoneNumber;
    private String address;
    private String boundary;
    private String careTimes;

    public void read(StringTokenizer dataTokenizer) {
        hospitalId = dataTokenizer.nextToken();
        hospitalName = dataTokenizer.nextToken();
        phoneNumber = dataTokenizer.nextToken();
        address = dataTokenizer.nextToken();
        careTimes = dataTokenizer.nextToken();;
    }
}
