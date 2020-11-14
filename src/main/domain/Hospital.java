package main.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;


public class Hospital implements Manageable {
    private String hospitalId;
    private String password;
    private String hospitalName;
    private String phoneNumber;
    private String address;
    private String[] careTypeList;
    private CareTime[] careTimeList = new CareTime[7];

    public void read(BufferedReader hospitalReader) throws IOException {
        hospitalId = hospitalReader.readLine().trim();
        password = hospitalReader.readLine().trim();
        hospitalName = hospitalReader.readLine().trim();
        phoneNumber = hospitalReader.readLine().trim();
        address = hospitalReader.readLine().trim();
        careTypeList = hospitalReader.readLine().trim().split(" ");
        
        for (int i=0; i<7; i++) {
        	careTimeList[i] = new CareTime();
        	careTimeList[i].read(hospitalReader);
        }
        
    }
    
	public boolean modifyPassword(String passwdFrom, String passwdTo) {
		if (equalsPassword(passwdFrom)) {
			this.password = passwdTo;
			return true;
		}
		
		return false;
	}
    
	public boolean matches(String keyword) {
		if (hospitalName.contains(keyword))
			return true;

		return false;
	}

	public boolean matches(String[] keywordList) {
		for (String keyword: keywordList)
			if (this.matches(keyword))
				return true;

		return false;
	}
    
    public boolean equalsPassword(String password) {
    	if (this.password.equalsIgnoreCase(password))
			return true;
		
    	return false;
    }

    public String getStateNow() {
    	int state = isOpenNow();
    	
    	switch(state) {
    	case 0:
    		return "Open";
    	case 1: // 휴무
    	case 2: // 영업시간 X
    		return "Close";
    	case 3:
    		return "Lunch";
    	default:
    		return "비정상";
    	}
    }
    
    // 0:영업중 1:휴무 2:준비중 3:식사중 -1:비정상
    public int isOpenNow() {
    	// LocalDate nowDate = LocalDate.now();
    	// LocalTime nowTime = LocalTime.now();
    	LocalDate nowDate = LocalDate.parse("2020-11-11");
    	LocalTime nowTime = LocalTime.parse("17:40");

    	String dayOfWeek = nowDate.format(DateTimeFormatter.ofPattern("E요일"));
    	
    	for (CareTime careTime: careTimeList) {
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
    
    
    
    
    
    // Temporary Method -> Drop after GUI linked
    public void print() {
    	String padding = "           ";
    	System.out.printf("[%s]\t이름: %s / phone: %s [%s]", hospitalId, hospitalName, phoneNumber, getStateNow());
    	System.out.println();
    	
    	System.out.printf(padding + "진료과목 ");
    	for (String careType: careTypeList)
    		System.out.print(careType + " ");
    	System.out.println();

    	System.out.println();
    }

    // Temporary Method -> Drop after GUI linked
    public void printDetail() {
    	String padding = "           ";
    	System.out.printf("[%s]\t이름: %s / phone: %s", hospitalId, hospitalName, phoneNumber);
    	System.out.println();
    	
    	System.out.printf(padding + "주소: %s", address);
    	System.out.println();
    	
    	System.out.printf(padding + "진료과목 ");
    	for (String careType: careTypeList)
    		System.out.print(careType + " ");
    	System.out.println();
    	
    	System.out.println(padding + "진료시간");
    	for (CareTime careTime: careTimeList) {
    		System.out.print(padding);
    		careTime.print();
    	}
    	
    	System.out.println();
    }
    
    public String getData() {
    	String data = "";
        data += hospitalId + "\n";
        data += hospitalName + "\n";
        data += phoneNumber + "\n";
        data += address + "\n";
        
        for (String careType: careTypeList)
        	data += careType + " ";
        data += "\n";
        
        for (CareTime careTime: careTimeList) {
        	data += careTime.toString();
        	data += "\n";
        }

        return data;
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
    	
    	void read(BufferedReader careTimeReader) throws IOException {
    		rawData = careTimeReader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(rawData," ");
        	DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("HH:mm");

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
    	
    	void print() {
    		String data = dayOfWeek + " ";

			data += "진료시간: ";
    		if (hasCareTime) {
	    		data += startCareTime.toString();
	    		data += "~";
	    		data += endCareTime.toString();
	    		data += "  ";
	    	
	    		data += "점심시간: ";
	    		if (hasLunchTime) {
		    		data += startLunchTime.toString();
		    		data += "~";
		    		data += endLunchTime.toString();
	    		}
	    		else {
	    			data += "진료 진행";
	    		}
    		}
    		else {
    			data += "진료 안함";
    		}
    		
    		System.out.println(data);
    	}
    	
    	public String toString() {
    		return rawData;
    	}
    }
    
}
