package main.socket;

import java.util.ArrayList;
import java.util.StringTokenizer;

import main.ServerApplication;
import main.collectInterface.Account;
import main.domain.Hospital;
import main.domain.Patient;
import main.domain.Reservation;
import main.service.ServerService;

public class SocketHandler {
	ServerService service = ServerApplication.service;

	public String apiMapping(String requestData) {

		String responseData = "";
		StringTokenizer tokenizer = new StringTokenizer(requestData, "\n");
		String requestHeader = tokenizer.nextToken();

		String[] headerPieces = requestHeader.split(" ");

		switch (headerPieces[0]) {
		case "GET":
			responseData = mappingTypeGet(headerPieces[1], tokenizer);
			break;
		case "POST":
			responseData = mappingTypePost(headerPieces[1], tokenizer);
			break;
		case "PUT":
			responseData = mappingTypePut(headerPieces[1], tokenizer);
			break;
		case "DELETE":
			responseData = mappingTypeDelete(headerPieces[1], tokenizer);
			break;
		}

		return responseData;
	}

	private String mappingTypeGet(String uri, StringTokenizer tokenizer) {

		String responseData = null;

		// request Self Info
		if (uri.indexOf("/auth/me") == 0) {

			responseData = responseSelfInfo(tokenizer);
		}

		else if (uri.indexOf("/auth/logout") == 0) {
			responseData = logout(tokenizer);
		}

		else if (uri.indexOf("/auth/leave") == 0) {
			responseData = leave(tokenizer);
		}

		// request hospital list
		else if (uri.indexOf("/hospitals/list/") == 0) {

			int pageNum = Integer.parseInt(uri.split("/")[3]);

			responseData = responseHospitalList(pageNum, tokenizer);
		}

		// request hospital info
		else if (uri.indexOf("/hospitals/info/") == 0) {

			String hospitalId = uri.split("/")[3];

			responseData = responseHospitalInfo(hospitalId, tokenizer);
		}

		// request reservation List from patient
		else if (uri.indexOf("/patients/reservations/list/") == 0) {

			int pageNum = Integer.parseInt(uri.split("/")[4]);

			responseData = responseReservationListToPatient(pageNum, tokenizer);
		}

		// request recent reservation info about hospital from patient
		else if (uri.indexOf("/patients/reservations/recent/") == 0) {

			String hospitalId = uri.split("/")[4];

			responseData = responseRecentReservationInfo(hospitalId, tokenizer);
		}

		// request reservation info from patient
		else if (uri.indexOf("/patients/reservations/info/") == 0) {

			String hospitalId = uri.split("/")[4];

			responseData = responseReservationInfo(hospitalId, tokenizer);
		}

		// request reservation List of specific patient from hospital
		else if (uri.indexOf("/hospitals/reservations/list/") == 0) {

			String patientId = uri.split("/")[4];

			responseData = responseReservationListOfPatientTohospital(patientId, tokenizer);
		}

		// request reservation List from hospital
		else if (uri.indexOf("/hospitals/reservations/list") == 0) {

			responseData = responseReservationListToHospital(tokenizer);
		}

		// request patient info from hospital
		else if (uri.indexOf("/hospitals/patients/info/") == 0) {
			String patientId = uri.split("/")[4];

			responseData = responsePatientInfo(patientId, tokenizer);

		}

		return responseData;
	}

	private String mappingTypePost(String uri, StringTokenizer tokenizer) {

		String responseData = null;

		// login
		if (uri.indexOf("/auth/login") == 0) {
			responseData = login(tokenizer);
		}

		// Join
		else if (uri.indexOf("/auth/join") == 0) {
			responseData = join(tokenizer);
		}

		// make a reservation
		else if (uri.indexOf("/patients/reservations/") == 0) {
			String hospitalId = uri.split("/")[3];

			responseData = makeReservation(hospitalId, tokenizer);
		}

		// process a reservation
		else if (uri.indexOf("/hospitals/reservations/") == 0) {
			String patientId = uri.split("/")[3];

			responseData = processReservation(patientId, tokenizer);
		}

		return responseData;
	}

	private String mappingTypePut(String uri, StringTokenizer tokenizer) {

		String responseData = null;

		// Modify Self Info
		if (uri.indexOf("/auth/me") == 0) {
			responseData = modifySelfInfo(tokenizer);
		}

		return responseData;
	}

	private String mappingTypeDelete(String uri, StringTokenizer tokenizer) {

		String responseData = null;

		// Delete Reservation By Patient
		if (uri.indexOf("/patients/reservations/") == 0) {

			String objectId = uri.split("/")[3];

			responseData = deleteReservation(objectId, tokenizer);
		}

		// Delete Reservation By Hospital
		else if (uri.indexOf("/hospitals/reservations/") == 0) {

			String objectId = uri.split("/")[3];

			responseData = deleteReservation(objectId, tokenizer);
		}

		return responseData;
	}

	private String responseSelfInfo(StringTokenizer tokenizer) {

		Account user = service.getManageable(tokenizer);

		if (user == null)
			return "Get Self Info Failed";

		String responseData = "";
		responseData += "Get Self Info Success\n";
		responseData += user.getData();

		return responseData;
	}

	private String responseHospitalList(int pageNum, StringTokenizer tokenizer) {

		ArrayList<Hospital> hospitalList = service.searchHospitalList(pageNum, tokenizer);

		if (hospitalList == null || hospitalList.size() == 0)
			return "Get Hospital List Failed";

		String responseData = "";
		responseData += "Get Hospital List Success\n";
		for (Hospital hospital : hospitalList)
			responseData += hospital.getData();

		return responseData;
	}

	private String responseHospitalInfo(String hospitalId, StringTokenizer tokenizer) {

		Hospital hospital = service.getHospitalInfo(hospitalId, tokenizer);

		if (hospital == null)
			return "Get Hospital Info Failed";

		String responseData = "";
		responseData += "Get Hospital Info Success\n";
		responseData += hospital.getData();

		return responseData;
	}

	private String responseReservationListToPatient(int pageNum, StringTokenizer tokenizer) {

		ArrayList<Reservation> reservationList = service.getReservationListForPatient(pageNum, tokenizer);

		if (reservationList == null || reservationList.size() == 0)
			return "Get Reservation List Failed";

		String responseData = "";
		responseData += "Get Reservation List Success\n";
		for (Reservation reservation : reservationList)
			responseData += reservation.getData();

		return responseData;
	}

	private String responseReservationListToHospital(StringTokenizer tokenizer) {

		ArrayList<Reservation> reservationList = service.getReservationListForHospital(tokenizer);

		if (reservationList == null || reservationList.size() == 0)
			return "Get Reservation List Failed";

		String responseData = "";
		responseData += "Get Reservation List Success\n";
		for (Reservation reservation : reservationList)
			responseData += reservation.getData();

		return responseData;
	}

	private String responseReservationListOfPatientTohospital(String patientId, StringTokenizer tokenizer) {

		ArrayList<Reservation> reservationList = service.getReservationListOfPatientForHospital(patientId, tokenizer);

		if (reservationList == null || reservationList.size() == 0)
			return "Get Reservation List Failed";

		String responseData = "";
		responseData += "Get Reservation List Success\n";
		for (Reservation reservation : reservationList)
			responseData += reservation.getData();

		return responseData;
	}

	private String responsePatientInfo(String patientId, StringTokenizer tokenizer) {

		Patient patient = service.getPatientInfo(patientId, tokenizer);

		if (patient == null)
			return "Get Patient Info Failed";

		String responseData = "";
		responseData += "Get Patient Info Success\n";
		responseData += patient.getData();

		return responseData;
	}

	private String responseRecentReservationInfo(String hospitalId, StringTokenizer tokenizer) {

		Reservation reservation = service.getRecentReservationInfo(hospitalId, tokenizer);

		if (reservation == null)
			return "Get Recent Reservation Info Failed";

		String responseData = "";
		responseData += "Get Recent Reservation Info Success\n";
		responseData += reservation.getData();

		return responseData;
	}

	private String responseReservationInfo(String hospitalId, StringTokenizer tokenizer) {

		Reservation reservation = service.getReservationInfo(hospitalId, tokenizer);

		if (reservation == null)
			return "Get Reservation Info Failed";

		String responseData = "";
		responseData += "Get Reservation Info Success\n";
		responseData += reservation.getData();

		return responseData;
	}

	private String login(StringTokenizer tokenizer) {
		String cookie = service.login(tokenizer);
		if (cookie == null) {
			return "Login Failed";
		}

		Account user = service.getManageable(cookie);

		if (user == null)
			return "Login Failed";

		String responseData = "";
		responseData += "Login Successed\n";
		responseData += cookie + "\n";
		responseData += user.getData();

		return responseData;
	}

	private String logout(StringTokenizer tokenizer) {

		if (service.logout(tokenizer) == false) {
			return "Logout Failed";
		}

		String responseData = "";
		responseData += "Lougout Successed\n";

		return responseData;
	}

	private String join(StringTokenizer tokenizer) {

		if (service.join(tokenizer) == false)
			return "Join Failed";

		String responseData = "";
		responseData += "Join Successed";

		return responseData;
	}

	private String leave(StringTokenizer tokenizer) {
		if (service.leave(tokenizer) == false) {
			return "Leave Failed";
		}

		String responseData = "";
		responseData += "Leave Successed\n";

		return responseData;
	}

	private String makeReservation(String hospitalId, StringTokenizer tokenizer) {

		if (service.makeReservation(hospitalId, tokenizer))
			return "Make Reservation Successed";

		return "Make Reservation Failed";
	}

	private String processReservation(String patientId, StringTokenizer tokenizer) {

		if (service.processReservaiton(patientId, tokenizer))
			return "Process Reservation Successed";

		return "Process Reservation Failed";
	}

	private String modifySelfInfo(StringTokenizer tokenizer) {

		if (service.modifySelfInfo(tokenizer))
			return "Modify Self Password Successed";

		return "Modify Self Password Failed";
	}

	private String deleteReservation(String objectId, StringTokenizer tokenizer) {

		if (service.cancelReservation(objectId, tokenizer))
			return "Delete Reservation Successed";

		return "Delete Reservation Failed";
	}
}
