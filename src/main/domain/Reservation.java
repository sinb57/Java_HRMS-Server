package main.domain;

import java.util.StringTokenizer;

public class Reservation {
    private String patientId;
    private String hospitalId;
    private String reservationDate;
    private String reservationTime;
    private int waitingNum;

    public void read(StringTokenizer dataTokenizer) {
        patientId = dataTokenizer.nextToken();
        hospitalId = dataTokenizer.nextToken();
        reservationDate = dataTokenizer.nextToken();
        reservationTime = dataTokenizer.nextToken();
        waitingNum = -1;
    }
}