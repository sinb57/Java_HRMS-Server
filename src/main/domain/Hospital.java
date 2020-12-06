package main.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Account;

public class Hospital implements Account {
	private String hospitalId;
	private String password;
	private String hospitalName;
	private String phoneNumber;
	private String address;
	private HashMap<String, String[]> careTypeMap = new HashMap<>();
	private CareTime[] careTimeList = new CareTime[7];
	private ArrayList<Reservation> reservationList = new ArrayList<>();

	public void read(Scanner scanner) {
		hospitalId = scanner.nextLine().trim();
		password = scanner.nextLine().trim();
		hospitalName = scanner.nextLine().trim();
		phoneNumber = scanner.nextLine().trim();
		address = scanner.nextLine().trim();

		String[] careTypeList = scanner.nextLine().split(" ");

		for (String careType : careTypeList) {
			String[] docterList = scanner.nextLine().trim().split(" ");
			careTypeMap.put(careType, docterList);
		}

		for (int i = 0; i < 7; i++) {
			careTimeList[i] = new CareTime();
			careTimeList[i].read(scanner);
		}
	}

	public ArrayList<Reservation> searchReservationList(StringTokenizer tokenizer) {
		String state = tokenizer.nextToken().trim();
		String[] dateList = tokenizer.nextToken().trim().split(" ");
		String searchType = null;
		String[] keywordList = null;

		if (tokenizer.hasMoreTokens())
			searchType = tokenizer.nextToken().trim();
		if (tokenizer.hasMoreTokens())
			keywordList = tokenizer.nextToken().trim().split(" ");

		ArrayList<Reservation> tmpList = new ArrayList<>();
		for (Reservation reservation : reservationList) {
			if (state.equals("예약중") && reservation.matchesState(state) == false)
				continue;

			if (reservation.belongToPeriod(dateList[0], dateList[1])) {
				if (keywordList == null || keywordList.length == 0) {
					tmpList.add(reservation);
					continue;
				}

				switch (searchType) {
				case "진료의사":
					if (reservation.matchesDocter(keywordList))
						tmpList.add(reservation);
					break;

				case "진료과목":
					if (reservation.matchesCareType(keywordList))
						tmpList.add(reservation);
					break;

				case "이름":
					if (reservation.matchesPatientName(keywordList))
						tmpList.add(reservation);
					break;

				case "번호":
					if (reservation.matchesPatientPhoneNumber(keywordList))
						tmpList.add(reservation);
					break;

				}
			}
		}

		Collections.sort(tmpList);

		if (state.equals("예약중"))
			Collections.reverse(tmpList);

		return tmpList;
	}

	public void addReservation(Reservation reservation) {
		reservationList.add(reservation);
	}

	public int getSizeOfReservationList(String reservationDate, String reservationTime) {
		int sizeOfReservationList = 0;
		for (Reservation reservation : reservationList) {
			if (reservation.matchesDateTime(reservationDate, reservationTime)) {
				sizeOfReservationList++;
			}
		}
		return sizeOfReservationList;
	}

	public Reservation searchReservation(String patientId, StringTokenizer tokenizer) {
		String reservationDate = tokenizer.nextToken();
		String reservationTime = tokenizer.nextToken();

		for (Reservation reservation : reservationList)
			if (reservation.matches(patientId, reservationDate, reservationTime, false))
				return reservation;

		return null;
	}

	public boolean modifyPassword(String passwdFrom, String passwdTo) {
		if (equalsPassword(passwdFrom)) {
			this.password = passwdTo;
			return true;
		}

		return false;
	}

	public boolean matches(String address, String careType, String state, String keywords) {

		// 주소 확인
		if (address != null) {
			if (this.address.indexOf(address) != 0)
				return false;
		}

		// 진료과목 확인
		if (careType.equals("전체") == false) {
			if (containsCareType(careType) == false)
				return false;
		}

		// 영업중 확인
		if (state.equals("전체") == false) {
			if (getStateNow().equals(state) == false)
				return false;
		}

		// 병원명 확인
		if (keywords != null) {
			String[] keywordList = keywords.split(" ");
			if (searchHospitalName(keywordList) == false)
				return false;
		}

		return true;
	}

	private boolean containsCareType(String keyword) {
		for (String careType : careTypeMap.keySet()) {
			if (careType.equals(keyword))
				return true;
		}
		return false;
	}

	private boolean searchHospitalName(String[] keywordList) {
		for (String keyword : keywordList) {
			if (hospitalName.contains(keyword))
				return true;
		}
		return false;
	}

	public boolean equalsPassword(String password) {
		if (this.password.equalsIgnoreCase(password))
			return true;

		return false;
	}

	public String getStateNow() {
		int state = isOpenNow();

		switch (state) {
		case 0:
			return "영업중";
		case 1:
			return "휴무";
		case 2:
			return "영업마감";
		case 3:
			return "식사중";
		default:
			return "비정상";
		}
	}

	// 0:영업중 1:휴무 2:준비중 3:식사중 -1:비정상
	public int isOpenNow() {
		LocalDate nowDate = LocalDate.now();
		LocalTime nowTime = LocalTime.now();

		String dayOfWeek = nowDate.format(DateTimeFormatter.ofPattern("E요일"));

		for (CareTime careTime : careTimeList) {
			if (careTime.equalsDate(dayOfWeek)) {
				if (careTime.isClosedDay())
					return 1;
				if (careTime.isCareTimeNow(nowTime)) {
					if (careTime.isLunchTimeNow(nowTime))
						return 3;
					return 0;
				}
				return 2;
			}
		}

		return -1;
	}

	public String getData() {
		String data = "";
		data += hospitalId + "\n";
		data += hospitalName + "\n";
		data += phoneNumber + "\n";
		data += address + "\n";

		for (String careType : careTypeMap.keySet())
			data += careType + " ";
		data += "\n";

		for (String careType : careTypeMap.keySet()) {
			String[] docterList = careTypeMap.get(careType);
			for (String docterName : docterList)
				data += docterName + " ";
			data += "\n";
		}

		for (CareTime careTime : careTimeList) {
			data += careTime.toString();
			data += "\n";
		}

		return data;
	}

	@Override
	public String getId() {
		return hospitalId;
	}

	class CareTime {
		private String dayOfWeek;
		private LocalTime startCareTime;
		private LocalTime endCareTime;
		private LocalTime startLunchTime;
		private LocalTime endLunchTime;
		private boolean hasCareTime;
		private boolean hasLunchTime;
		private String rawData;

		void read(Scanner scanner) {
			rawData = scanner.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(rawData, " ");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

			dayOfWeek = tokenizer.nextToken();

			if (tokenizer.nextToken().equals("x")) {
				hasCareTime = false;
				return;
			}

			hasCareTime = true;

			startCareTime = LocalTime.parse(tokenizer.nextToken(), formatter);
			endCareTime = LocalTime.parse(tokenizer.nextToken(), formatter);

			if (tokenizer.nextToken().equals("x")) {
				hasLunchTime = false;
				return;
			}

			hasLunchTime = true;

			startLunchTime = LocalTime.parse(tokenizer.nextToken(), formatter);
			endLunchTime = LocalTime.parse(tokenizer.nextToken(), formatter);

		}

		boolean equalsDate(String dayOfWeek) {
			if (this.dayOfWeek.equals(dayOfWeek))
				return true;
			return false;
		}

		boolean isClosedDay() {
			if (hasCareTime)
				return false;
			return true;
		}

		boolean isCareTimeNow(LocalTime now) {
			if (hasCareTime) {
				if (now.isBefore(startCareTime))
					return false;
				if (now.isAfter(endCareTime))
					return false;
				return true;
			}
			return false;
		}

		boolean isLunchTimeNow(LocalTime now) {
			if (hasLunchTime) {
				if (now.isBefore(startLunchTime))
					return false;
				if (now.isAfter(endLunchTime))
					return false;
				return true;
			}
			return false;
		}

		public String toString() {
			return rawData;
		}
	}

}
