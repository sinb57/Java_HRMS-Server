package main.domain;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.service.ServerService;

public class Reservation implements Comparable<Reservation> {
	private String reservationState;
	private String patientId;
	private String hospitalId;
	private String reservationDate;
	private String reservationTime;
	private String careTime;
	private String docterName;
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

		if (reservationState.equals("진료완료")) {
			careTime = scanner.next();
			docterName = scanner.next();
		}
		
		if (scanner.hasNext())
			scanner.nextLine();

		addToManageable();
	}

	public void read(String patientId, String hospitalId, String reservationDate, String reservationTime,
			StringTokenizer tokenizer) {
		this.patientId = patientId;
		this.hospitalId = hospitalId;
		this.reservationDate = reservationDate;
		this.reservationTime = reservationTime;

		reservationState = "예약중";
		careType = tokenizer.nextToken().trim();
		symptomList = tokenizer.nextToken().trim().split(" ");

		addToManageable();
	}

	public void updateReservationState(String reservationState) {
		this.reservationState = reservationState;
	}

	public void updateToBeTreated(StringTokenizer tokenizer) {
		careTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		docterName = tokenizer.nextToken();

		reservationState = "진료완료";
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
			} else if (reservationDate.compareTo(other.getReservationDate()) < 0) {
				return -1;
			}

			if (reservationTime.compareTo(other.getReservationTime()) > 0) {
				return 1;
			} else if (reservationTime.compareTo(other.getReservationTime()) < 0) {
				return -1;
			}
		} else {
			if (reservationDate.compareTo(other.getReservationDate()) > 0) {
				return -1;
			} else if (reservationDate.compareTo(other.getReservationDate()) < 0) {
				return 1;
			}

			if (reservationTime.compareTo(other.getReservationTime()) > 0) {
				return -1;
			} else if (reservationTime.compareTo(other.getReservationTime()) < 0) {
				return 1;
			}
		}
		return 0;
	}

	public boolean matches(String objectId, String reservationDate, String reservationTime, boolean isPatientDoing) {

		if (isPatientDoing) {
			if (matchesHospitalId(objectId) == false)
				return false;
		} else {
			if (matchesPatientId(objectId) == false)
				return false;
		}

		if (matchesDateTime(reservationDate, reservationTime) == false)
			return false;

		return true;
	}

	public boolean matchesHospitalId(String hospitalId) {
		if (this.hospitalId.equals(hospitalId))
			return true;
		return false;
	}

	public boolean matchesPatientId(String patientId) {
		if (this.patientId.equals(patientId))
			return true;
		return false;
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

	public boolean matchesDocter(String[] keywordList) {
		for (String keyword : keywordList) {
			if (docterName.contains(keyword))
				return true;
		}
		return false;
	}

	public boolean matchesCareType(String[] keywordList) {
		for (String keyword : keywordList) {
			if (careType.contains(keyword))
				return true;
		}
		return false;
	}

	public boolean matchesPatientName(String[] keywordList) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		for (String keyword : keywordList) {
			if (patient.getName().contains(keyword))
				return true;
		}
		return false;
	}

	public boolean matchesPatientPhoneNumber(String[] keywordList) {
		Patient patient = ServerService.patientManager.searchWithId(patientId);
		for (String keyword : keywordList) {
			if (patient.getPhoneNumber().contains(keyword))
				return true;
		}
		return false;
	}

	public boolean belongToPeriod(String dateFrom, String dateTo) {
		if (reservationDate.compareTo(dateFrom) < 0)
			return false;
		if (reservationDate.compareTo(dateTo) > 0)
			return false;
		return true;
	}

	public String getData() {
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

		if (reservationState.equals("진료완료")) {
			data += careTime + "\n";
			data += docterName + "\n";
		}

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