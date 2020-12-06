package main.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import main.collectInterface.Account;
import main.domain.Hospital;
import main.domain.Patient;
import main.domain.Reservation;

public class ServerService {

	public static PatientManager patientManager = new PatientManager();
	public static HospitalManager hospitalManager = new HospitalManager();
	public static ReservationManager reservationManager = new ReservationManager();

	private HashMap<String, Account> cookieMap = new HashMap<>();

	public ServerService() {

		String relativePath = "../storage/Account/";

		try {
			patientManager.readAll(relativePath + "patients/");
			hospitalManager.readAll(relativePath + "hospitals/");
			reservationManager.readAll(relativePath + "reservations/");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	public String login(StringTokenizer tokenizer) {

		String userId = tokenizer.nextToken();
		String userPw = tokenizer.nextToken();

		try {
			userPw = getEncryptedPassword(userPw);
		} catch (Exception e) {
			return null;
		}

		Account user = null;

		user = patientManager.searchWithId(userId);
		if (user == null)
			user = hospitalManager.searchWithId(userId);
		if (user == null)
			return null;

		if (user.equalsPassword(userPw)) {
			String cookie = makeCookie();
			cookieMap.put(cookie, user);
			return cookie;
		}

		return null;
	}

	public boolean logout(StringTokenizer tokenizer) {
		String cookie = tokenizer.nextToken();

		cookieMap.remove(cookie);

		return true;
	}

	public boolean join(StringTokenizer tokenizer) {

		try {
			patientManager.read(tokenizer);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean leave(StringTokenizer tokenizer) {
		String cookie = tokenizer.nextToken();

		Account user = cookieMap.get(cookie);

		if (user instanceof Patient)
			patientManager.remove((Patient) user);
		else
			hospitalManager.remove((Hospital) user);

		cookieMap.remove(cookie);

		return true;
	}

	public boolean modifySelfInfo(StringTokenizer tokenizer) {

		if (tokenizer.hasMoreTokens()) {
			String cookie = "";
			String passwdFrom = "";
			String passwdTo = "";

			try {
				cookie = tokenizer.nextToken();
				passwdFrom = tokenizer.nextToken();
				passwdTo = tokenizer.nextToken();

				Account user = cookieMap.get(cookie);

				if (user == null)
					return false;

				String encryptedPassword = getEncryptedPassword(passwdFrom);
				String encryptedPassword2 = getEncryptedPassword(passwdTo);

				if (user.modifyPassword(encryptedPassword, encryptedPassword2))
					return true;

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public Account getManageable(StringTokenizer tokenizer) {
		String cookie = tokenizer.nextToken();

		return getManageable(cookie);
	}

	public Account getManageable(String cookie) {
		return cookieMap.get(cookie);
	}

	public ArrayList<Hospital> searchHospitalList(int pageNum, StringTokenizer tokenizer) {

		ArrayList<Hospital> searchedList = new ArrayList<>();
		String cookie = "";

		if (tokenizer.hasMoreTokens()) {
			cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			searchedList = hospitalManager.searchWithKeywords(tokenizer);

			if (searchedList.size() < (pageNum - 1) * 4) {
				return null;
			} else if (pageNum * 4 < searchedList.size()) {
				return new ArrayList<>(searchedList.subList((pageNum - 1) * 4, pageNum * 4));
			} else {
				return new ArrayList<>(searchedList.subList((pageNum - 1) * 4, searchedList.size()));
			}

		}
		return null;
	}

	public Hospital getHospitalInfo(String hospitalId, StringTokenizer tokenizer) {

		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			return hospitalManager.searchWithId(hospitalId);
		}
		return null;
	}

	public boolean makeReservation(String hospitalId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();

			Account user = cookieMap.get(cookie);

			if (user == null || user instanceof Hospital)
				return false;

			return reservationManager.addReservation((Patient) user, hospitalId, tokenizer);
		}
		return false;
	}

	public boolean processReservaiton(String patientId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();

			Hospital hospital = (Hospital) cookieMap.get(cookie);

			reservationManager.processReservation(hospital, patientId, tokenizer);

			return true;
		}
		return false;
	}

	public boolean cancelReservation(String objectId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();

			Account user = cookieMap.get(cookie);

			if (user == null)
				return false;

			String userId = user.getId();

			if (user instanceof Patient) {
				reservationManager.cancelReservationByPatient(userId, objectId, tokenizer);
				return true;
			} else {
				reservationManager.cancelReservationByHospital(userId, objectId, tokenizer);
				return true;
			}
		}
		return false;
	}

	public ArrayList<Reservation> getReservationListForPatient(int pageNum, StringTokenizer tokenizer) {

		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Patient patient = (Patient) cookieMap.get(cookie);

			ArrayList<Reservation> reservationList = patient.getReservationList();

			if (reservationList.size() > pageNum * 4) {
				return new ArrayList<>(reservationList.subList((pageNum - 1) * 4, pageNum * 4));
			} else if (reservationList.size() < (pageNum - 1) * 4) {
				return null;
			} else {
				return new ArrayList<>(reservationList.subList((pageNum - 1) * 4, reservationList.size() - 1));
			}
		}
		return null;
	}

	public ArrayList<Reservation> getReservationListForHospital(StringTokenizer tokenizer) {

		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Hospital hospital = (Hospital) cookieMap.get(cookie);

			ArrayList<Reservation> reservationList = hospital.searchReservationList(tokenizer);

			return reservationList;
		}
		return null;
	}

	public ArrayList<Reservation> getReservationListOfPatientForHospital(String patientId, StringTokenizer tokenizer) {

		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Hospital hospital = (Hospital) cookieMap.get(cookie);

			Patient patient = patientManager.searchWithId(patientId);

			ArrayList<Reservation> reservationList = patient.getReservationList(hospital.getId());

			return reservationList;
		}
		return null;

	}

	public Patient getPatientInfo(String patientId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Hospital hospital = (Hospital) cookieMap.get(cookie);
			
			return patientManager.searchWithId(patientId);
		}
		return null;
	}

	public Reservation getRecentReservationInfo(String hospitalId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Patient patient = (Patient) cookieMap.get(cookie);

			return patient.searchRecentReservation(hospitalId);
		}
		return null;
	}

	public Reservation getReservationInfo(String hospitalId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;

			Patient patient = (Patient) cookieMap.get(cookie);

			return patient.searchReservation(hospitalId, tokenizer);
		}
		return null;
	}

	private String makeCookie() {

		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());

		String cookie = "";

		do {
			cookie = "";
			for (int i = 0; i < 10; i++) {
				int index = rand.nextInt(3);

				switch (index) {
				case 0:
					cookie += (char) (rand.nextInt(26) + 65);
					break;
				case 1:
					cookie += (char) (rand.nextInt(26) + 97);
					break;
				case 2:
					cookie += (rand.nextInt(10));
					break;
				}
			}
		} while (cookieMap.containsKey(cookie));

		return cookie;
	}

	private String getEncryptedPassword(String password) throws Exception {

		String MD5 = "";

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		MD5 = sb.toString();

		return MD5;

	}

}
