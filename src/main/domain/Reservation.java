package main.domain;

import java.util.Scanner;
import java.util.StringTokenizer;

import main.service.ServerService;

public class Reservation implements Comparable<Reservation> {
	private String reservationState;
	private String patientId;
	private String hospitalId;
	private String reservationDate;
	private String reservationTime;
	private String careType;
	private String[] symptomList; 

    
    public void read(Scanner scanner) {
    	reservationState = scanner.next();
    	patientId = scanner.next();
    	hospitalId = scanner.next();
    	scanner.nextLine();
    	
        reservationDate = scanner.next();
        reservationTime = scanner.next();
        scanner.nextLine();

        careType = scanner.next();
        symptomList = scanner.nextLine().trim().split(" ");

        if (scanner.hasNext())
        	scanner.nextLine();
    	
    	addToManageable();
    }
    
    public boolean read(String patientId, String hospitalId, StringTokenizer tokenizer) {
    	reservationDate = tokenizer.nextToken().trim();
    	reservationTime = tokenizer.nextToken().trim();

    	Hospital hospital = ServerService.hospitalManager.searchWithId(hospitalId);
    	int sizeOfReservationList = hospital.getSizeOfReservationList(reservationDate, reservationTime);
    	
    	if (sizeOfReservationList < 7) {
	    	this.patientId = patientId;
	    	this.hospitalId = hospitalId;
	    	reservationState = "예약중";
	        careType = tokenizer.nextToken().trim();
	    	symptomList = tokenizer.nextToken().trim().split(" ");
	    	
	    	addToManageable();
	    	return true;
    	}
    	
    	return false;
    }
    
    public void updateReservationState(String reservationState) {
    	this.reservationState = reservationState;
    }
    
    private void addToManageable() {
    	Patient patient = ServerService.patientManager.searchWithId(patientId);
    	patient.addReservation(this);
    	Hospital hospital = ServerService.hospitalManager.searchWithId(hospitalId);
    	hospital.addReservation(this);
    }
    
	@Override
	public int compareTo(Reservation other) {
		
		if (reservationState.equals("영업중")) {
	    	if (reservationDate.compareTo(other.getReservationDate()) > 0) {
	    		return 1;
	    	}
	    	else if (reservationDate.compareTo(other.getReservationDate()) < 0) {
	    		return -1;
	    	}
	    	
	    	if (reservationTime.compareTo(other.getReservationTime()) > 0) {
	    		return 1;
	    	}
	    	else if (reservationTime.compareTo(other.getReservationTime()) < 0) {
	    		return -1;
	    	}
		}
		else {
	    	if (reservationDate.compareTo(other.getReservationDate()) > 0) {
	    		return -1;
	    	}
	    	else if (reservationDate.compareTo(other.getReservationDate()) < 0) {
	    		return 1;
	    	}
	    	
	    	if (reservationTime.compareTo(other.getReservationTime()) > 0) {
	    		return -1;
	    	}
	    	else if (reservationTime.compareTo(other.getReservationTime()) < 0) {
	    		return 1;
	    	}
		}
    	return 0;
    }
	
	public boolean matches(String hospitalId, String reservationDate, String reservationTime) {
		if (this.hospitalId.equals(hospitalId) == false)
			return false;
		
		if (matchesDateTime(reservationDate, reservationTime) == false)
			return false;
		
		return true;
	}
	
	public boolean matchesDateTime(String reservationDate, String reservationTime) {
		if (this.reservationDate.equals(reservationDate) == false)
			return false;
		
		if (this.reservationTime.equals(reservationTime) == false)
			return false;
		return true;
	}
	
	public boolean matchesState(String state) {
		if (reservationState.equals(state))
			return true;
		return false;
	}
	
	public String getData() {
    	String data = "";
    	data += reservationState + " ";
    	data += patientId + " ";
    	data += hospitalId + "\n";

    	data += reservationDate + " ";
    	data += reservationTime + " ";

    	data += careType + "\n";
    	for (String symptom : symptomList)
    		data += symptom + " ";
    	data += "\n\n";

        return data;
	}
	
	public String getDataForSocket() {
    	String data = "";
    	data += reservationState + "\n";
    	data += patientId + "\n";
    	data += hospitalId + "\n";

    	data += reservationDate + "\n";
    	data += reservationTime + "\n";

    	data += careType + "\n";
    	for (String symptom : symptomList)
    		data += symptom + " ";
    	data += "\n";

        return data;
    }
    
    public String getReservationDate() {
    	return reservationDate;
    }
    
    public String getReservationTime() {
    	return reservationTime;
    }
    
    public String getReservationState() {
    	return reservationState;
    }
    

}