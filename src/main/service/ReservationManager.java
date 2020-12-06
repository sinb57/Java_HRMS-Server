package main.service;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Manager;
import main.domain.Hospital;
import main.domain.Patient;
import main.domain.Reservation;

public class ReservationManager implements Manager {

	@Override
	public void readAll(String dirPath) {
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

		String reservationDate = tokenizer.nextToken().trim();
		String reservationTime = tokenizer.nextToken().trim();

		Hospital hospital = ServerService.hospitalManager.searchWithId(hospitalId);
		int sizeOfReservationList = hospital.getSizeOfReservationList(reservationDate, reservationTime);

		if (sizeOfReservationList < 4) {
			reservation.read(patient.getId(), hospitalId, reservationDate, reservationTime, tokenizer);

			return true;
		}
		return false;
	}

	public void processReservation(Hospital hospital, String patientId, StringTokenizer tokenizer) {

		Reservation reservation = hospital.searchReservation(patientId, tokenizer);

		reservation.updateToBeTreated(tokenizer);

	}

	public void cancelReservationByPatient(String patientId, String hospitalId, StringTokenizer tokenizer) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		patient.cancelReservation("예약취소", hospitalId, tokenizer);

	}

	public void cancelReservationByHospital(String hospitalId, String patientId, StringTokenizer tokenizer) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		patient.cancelReservation("진료취소", hospitalId, tokenizer);

	}

}
