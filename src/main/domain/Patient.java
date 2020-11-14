package main.domain;

import java.io.BufferedReader;
import java.io.IOException;

public class Patient implements Manageable {
    private String patientId;
    private String password;
    private String patientName;
    private String phoneNumber;
    private String address;

    public void read(BufferedReader patientReader) throws IOException {
        patientId = patientReader.readLine().trim();
        password = patientReader.readLine().trim();
        patientName = patientReader.readLine().trim();
        phoneNumber = patientReader.readLine().trim();
    }

    
	public boolean modifyPassword(String passwdFrom, String passwdTo) {
		if (equalsPassword(passwdFrom)) {
			this.password = passwdTo;
			return true;
		}
		
		return false;
	}

    
    public boolean equalsPassword(String password) {
    	if (this.password.equalsIgnoreCase(password))
			return true;
		
    	return false;
    }
    
    
    // Temporary Method -> Drop after GUI linked
    public void print() {
    	System.out.printf("[%s]\t이름: %s / 번호: %s", patientId, patientName, phoneNumber);
    	System.out.println();
    }
    
    public String getData() {
    	String data = "";
        data += patientId + "\n";
        data += patientName + "\n";
        data += phoneNumber + "\n";
        data += address + "\n";
        return data;
    }
}

