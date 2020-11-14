package main.socket;

import java.util.ArrayList;
import java.util.StringTokenizer;

import main.ServerApplication;
import main.domain.Hospital;
import main.domain.Manageable;
import main.service.ServerService;

public class SocketHandler {
	ServerService service = ServerApplication.service;

	public String apiMapping(String requestData) {

		String responseData = "";

		StringTokenizer tokenizer = new StringTokenizer(requestData, "\n"); 
		
		String requestHeader = tokenizer.nextToken();
		
		String[] headerPieces = requestHeader.split(" ");
		
		switch(headerPieces[0]) {
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
		
		// request hospital list
		else if (uri.indexOf("/hospitals/list") == 0) {
			
			int pageNum = Integer.parseInt(uri.split("/")[3]);
			
			responseData = responseHospitalList(pageNum, tokenizer);
		}
		
		// request hospital info
		else if (uri.indexOf("/hospitals/info") == 0) {
			
			String hospitalId = uri.split("/")[3];
			
			responseData = responseHospitalInfo(hospitalId, tokenizer);
		}
		
		return responseData;
	}

	private String mappingTypePost(String uri, StringTokenizer tokenizer) {

		String responseData = null;

		// login
		if (uri.indexOf("/auth/login") == 0) {
			responseData = login(tokenizer);
		}
		
		// make a reservation
		else if (uri.indexOf("/patients/reservations") == 0) {
			String hospitalId = uri.split("/")[4];

			responseData = makeReservation(hospitalId, tokenizer);
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

		// Delete Reservation
		if (uri.indexOf("/hospitals/reservations") == 0) {
			
			String patientId = uri.split("/")[4];
			
			responseData = deleteReservation(patientId, tokenizer);
		}
		
		return responseData;
	}


	private String responseSelfInfo(StringTokenizer tokenizer) {
		
		Manageable user = service.getInfo(tokenizer);
		
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
		for (Hospital hospital: hospitalList)
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

	
	private String login(StringTokenizer tokenizer) {
		String cookie = service.login(tokenizer);
		if (cookie == null) {
			return "Login Failed";
		}
		
		Manageable user = service.getManagebleInfo(cookie);

		String responseData = "";
		responseData += "Login Successed\n";
		responseData += cookie + "\n";
		responseData += user.getData();
		
		return responseData;
	}
	

	private String makeReservation(String hospitalId, StringTokenizer tokenizer) {
		
		if (service.makeReservation(hospitalId, tokenizer))
			return "Make Reservation Successed";
		
		return "Make Reservation Failed";
	}
	
	
	private String modifySelfInfo(StringTokenizer tokenizer) {
		
		if (service.modifySelfInfo(tokenizer))
			return "Modify Self Password Successed";
		
		return "Modify Self Password Failed";
	}
	
	
	
	private String deleteReservation(String patientId, StringTokenizer tokenizer) {

		if (service.deleteReservation(patientId, tokenizer))
			return "Delete Reservation Successed";
		
		return "Delete Reservation Failed";
	}
}
