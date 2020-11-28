package main.service;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Manager;
import main.domain.Patient;
import main.domain.Reservation;

public class ReservationManager implements Manager {
	private String dirPath = null;

	@Override
	public void readAll(String dirPath) {
		this.dirPath = dirPath;

        File parentPath = new File(dirPath);
    	for (File folder : parentPath.listFiles()) {
    		String filePath = folder.getPath() + "\\info.txt"; 
    		File reservationFile = new File(filePath);
    		if (reservationFile.exists()) {
	        	Scanner scanner = openFile(filePath);
	        	while (scanner.hasNext()) {
		        	Reservation reservation = new Reservation();
		        	reservation.read(scanner);
	        	}
	        	scanner.close();
    		}
        }
	}
	
	public boolean addReservation(Patient patient, String hospitalId, StringTokenizer tokenizer) {
    	
    	Reservation reservation = new Reservation();

    	return reservation.read(patient.getId(), hospitalId, tokenizer);
	}
	
	public void delReservationByPatient(String patientId, String hospitalId, StringTokenizer tokenizer) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		patient.cancelReservation("예약취소", hospitalId, tokenizer);
		
	}
	public void delReservationByHospital(String hospitalId, String patientId, StringTokenizer tokenizer) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		patient.cancelReservation("예약취소됨", hospitalId, tokenizer);
		
	}
	

}
