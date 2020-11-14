package main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import main.domain.Hospital;
import main.domain.Manageable;
import main.domain.Patient;

public class ServerService {
	
    public static HashMap<String, Patient> patientMap = new HashMap<>();
    public static HashMap<String, Hospital> hospitalMap = new HashMap<>();
    private HashMap<String, Manageable> cookieMap = new HashMap<>();
    
    public ServerService() {

    	try {
	    	readAllPatient("../storage/Account/patients/");
	    	readAllHospital("../storage/Account/hospitals/");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return;
    	}

    }
    
    public void printAllPatient() {
    	System.out.println();
    	for (String patientId: patientMap.keySet())
    		patientMap.get(patientId).print();
    	System.out.println();
    }
    
    public void printAllHospital() {
    	System.out.println();
    	for (String hospitalId: hospitalMap.keySet())
    		hospitalMap.get(hospitalId).print();
    	System.out.println();
    }
    

    private void readAllPatient(String dirPath) throws IOException {

    	String fileName;
    	String patientId;
    	Patient patient;
    	
        File patientDir = new File(dirPath);
    	for (File file : patientDir.listFiles()) {
            if (file.isFile()) {
            	fileName = file.getName();
            	patientId = fileName.substring(0, fileName.length()-4);
            	patient = new Patient();
            	
                BufferedReader patientReader = readFile(dirPath + fileName);
            	patient.read(patientReader);
                patientReader.close();

                patientMap.put(patientId, patient);
            }
        }
    }

    private void readAllHospital(String dirPath) throws IOException {

    	String fileName;
    	String hospitalId;
    	Hospital hospital;
    	
    	File hospitalDir = new File(dirPath);
        for (File file : hospitalDir.listFiles()) {
            if (file.isFile()) {
            	fileName = file.getName();
            	hospitalId = fileName.substring(0, fileName.length()-4);
            	hospital = new Hospital();
            	
                BufferedReader patientReader = readFile(dirPath + fileName);
                hospital.read(patientReader);
                patientReader.close();

                hospitalMap.put(hospitalId, hospital);
            }
        }
    }
        
    private BufferedReader readFile(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }


    
	public String login(StringTokenizer requestData) {
		
		String userId = requestData.nextToken();
		String userPw = requestData.nextToken();
		String encryptedPassword = "";
		
		try {
			encryptedPassword = getEncryptedPassword(userPw);
		} catch (Exception e) {
			return null;
		}
		
		for (String patientId : patientMap.keySet()) {
			if (patientId.equals(userId)) {
				Patient patient = patientMap.get(patientId);
				if (patient.equalsPassword(encryptedPassword)) {
					String cookie = makeCookie();
					cookieMap.put(cookie, patient);
					return cookie;
				}
			}
		}
		
		for (String hospitalId : hospitalMap.keySet()) {
			if (hospitalId.equals(userId)) {
				Hospital hospital = hospitalMap.get(hospitalId);
				if (hospital.equalsPassword(userPw)) {
					String cookie = makeCookie();
					cookieMap.put(cookie, hospital);
					return cookie;
				}
			}
		}
		
		return null;
	}
	
	
	public Manageable getInfo(StringTokenizer tokenizer) {
		String cookie = tokenizer.nextToken();
		
		return cookieMap.get(cookie);
	}

	public ArrayList<Hospital> searchHospitalList(int pageNum, StringTokenizer tokenizer) {

		ArrayList<Hospital> hospitalList = new ArrayList<>();
		Hospital hospital = null;
		String cookie = "";
		boolean isSelectedOnlyOpened = false;
		
		if (tokenizer.hasMoreTokens()) {
			cookie = tokenizer.nextToken();
			if (cookieMap.containsKey(cookie) == false)
				return null;
		}
		
		if (tokenizer.hasMoreTokens()) {
			if (tokenizer.nextToken().equals("true"))
				isSelectedOnlyOpened = true;
		}
		
		// keyword가 있으면
		if (tokenizer.hasMoreTokens()) {
				
			String keywords = tokenizer.nextToken();
			String[] keywordList = keywords.split(" ");

			for (String hospitalId: hospitalMap.keySet()) {
				if (hospitalList.size() > pageNum*5)
					break;
				
				hospital = hospitalMap.get(hospitalId);

				if (isSelectedOnlyOpened) {
					if (hospital.isOpenNow() != 0)
						continue;
				}
				
				if (hospital.matches(keywordList))
					hospitalList.add(hospital);
			}
		}
		// keyword가 없으면
		else {
			for (String hospitalId: hospitalMap.keySet()) {
				hospital = hospitalMap.get(hospitalId);

				if (isSelectedOnlyOpened) {
					if (hospital.isOpenNow() != 0)
						continue;
				}

				hospitalList.add(hospital);
				if (hospitalList.size() > pageNum*5-1)
					break;
			}
		}
		
		for (int i=0; i<(pageNum-1)*5; i++)
			hospitalList.remove(0);
		
		return hospitalList;
	}
	
	public Hospital getHospitalInfo(String hospitalId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			
			if (cookieMap.containsKey(cookie) == false)
				return null;
			
			Hospital hospital = hospitalMap.get(hospitalId);
			
			return hospital;
		}
		return null;
	}
	
	public boolean makeReservation(String hospitalId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			
			Manageable user = cookieMap.get(cookie);
			
			if (user == null)
				return false;
			
			String className = user.getClass().toString();
			
			if (className.contains("Patient")) {
				// Make a Reservation
				
				return true;
			}
		}
		return false;
	}
	

	public boolean modifySelfInfo(StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = "";
			String passwdFrom = "";
			String passwdTo = "";
			
			if (tokenizer.hasMoreElements())
				cookie= tokenizer.nextToken();
			else
				return false;
			
			if (tokenizer.hasMoreElements())
				passwdFrom = tokenizer.nextToken();
			else
				return false;
			
			if (tokenizer.hasMoreElements())
				passwdTo = tokenizer.nextToken();
			else
				return false;
			
			Manageable user = cookieMap.get(cookie);
			
			if (user == null)
				return false;
			
			String encryptedPassword = "";
			
			try {
				encryptedPassword = getEncryptedPassword(passwdFrom);
			} catch (Exception e) {
				return false;
			}
			
			
			if (user.modifyPassword(encryptedPassword, passwdTo))
				return true;
			
		}
		return false;
	}

	public boolean deleteReservation(String patientId, StringTokenizer tokenizer) {
		if (tokenizer.hasMoreTokens()) {
			String cookie = tokenizer.nextToken();
			
			Manageable user = cookieMap.get(cookie);
		
			if (user == null)
				return false;
			
			String className = user.getClass().toString();
			
			if (className.contains("Hospital")) {
				// Drop the Reservation of Specific patient
				
				return true;
			}
		}
		return false;
	}

	
	private String makeCookie() {

		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		
		String cookie = "";
		
		do {
			cookie = "";
			for (int i=0; i<10; i++) {
				int index = rand.nextInt(3);

				switch(index) {
					case 0:
						cookie += (char)(rand.nextInt(26) + 65);
						break;
					case 1:
						cookie += (char)(rand.nextInt(26) + 97);
						break;
					case 2:
						cookie += (rand.nextInt(10));
						break;
				}
			}
		} while (cookieMap.containsKey(cookie));
		
		return cookie;
	}
	
	
	public String getEncryptedPassword(String password) throws Exception {
	     
		String MD5 = ""; 

		MessageDigest md = MessageDigest.getInstance("MD5"); 
		md.update(password.getBytes()); 
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer(); 
		for(int i = 0 ; i < byteData.length ; i++){
			sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
		}
		MD5 = sb.toString();

		return MD5;

	}
	
	public Manageable getManagebleInfo(String cookie) {
		return cookieMap.get(cookie);
	}

}
