package main.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Account;

public class Patient implements Account {
    private String patientId;
    private String password;
    private String patientName;
    private String phoneNumber;
	private ArrayList<Reservation> reservationList = new ArrayList<>();
	
    public void read(Scanner scanner) {
        patientId = scanner.nextLine().trim();
        password = scanner.nextLine().trim();
        patientName = scanner.nextLine().trim();
        phoneNumber = scanner.nextLine().trim();
    }
    
    public void read(StringTokenizer tokenizer) {
        patientId = tokenizer.nextToken().trim();
        password = tokenizer.nextToken().trim();
        patientName = tokenizer.nextToken().trim();
        phoneNumber = tokenizer.nextToken().trim();
    }
    
    public void addReservation(Reservation reservation) {
    	reservationList.add(reservation);
    }
    
    public void cancelReservation(String reservationState, String hospitalId, StringTokenizer tokenizer) {
		Reservation reservation = searchReservation(hospitalId, tokenizer);
		reservation.updateReservationState(reservationState);
    }
    
    public Reservation searchReservation(String hospitalId, StringTokenizer tokenizer) {
		String reservationDate = tokenizer.nextToken();
		String reservationTime = tokenizer.nextToken();
		
		for (Reservation reservation : reservationList)
			if (reservation.matches(hospitalId, reservationDate, reservationTime))
				return reservation;

		return null;
    }
    
    public void sortReservationList() {
    	ArrayList<Reservation> tmpList1 = new ArrayList<>();
    	ArrayList<Reservation> tmpList2 = new ArrayList<>();
    	
    	for (Reservation reservation : reservationList) {
    		if (reservation.matchesState("¿µ¾÷Áß"))
    			tmpList1.add(reservation);
    		else
    			tmpList2.add(reservation);
    	}
    	
    	Collections.sort(tmpList1);
    	Collections.sort(tmpList2);
    	
    	reservationList.clear();
    	reservationList.addAll(tmpList1);
    	reservationList.addAll(tmpList2);
    }
    
	public boolean modifyPassword(String passwdFrom, String passwdTo) {
		if (equalsPassword(passwdFrom)) {
			this.password = passwdTo;
			return true;
		}
		return false;
	}

    
    public boolean equalsPassword(String password) {
    	if (this.password.equalsIgnoreCase(password))
			return true;
		
    	return false;
    }
    
    
    public String getData() {
    	String data = "";
        data += patientId + "\n";
        data += patientName + "\n";
        data += phoneNumber + "\n";
        return data;
    }
    
    public String getId() {
    	return patientId;
    }
    
    public ArrayList<Reservation> getReservationList() {
    	sortReservationList();
    	return reservationList;
    }
    

    public String getReservationDatas() {
    	sortReservationList();
    	
    	String data = "";
    	for (Reservation reservation: reservationList) {
    		data += reservation.getData();
    	}
    	return data;
    }
}

