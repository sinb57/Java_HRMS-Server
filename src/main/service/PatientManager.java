package main.service;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Manager;
import main.domain.Patient;

public class PatientManager implements Manager {
	public HashMap<String, Patient> patientMap = new HashMap<>();
	
	@Override
	public void readAll(String dirPath) {

		Scanner scanner = null;
		Patient patient = null;
    	String patientId = null;
    	
        File parentPath = new File(dirPath);
    	for (File folder : parentPath.listFiles()) {
        	patientId = folder.getName();
        	patient = new Patient();

            scanner = openFile(folder.getPath() + "\\info.txt");  
        	patient.read(scanner);
        	scanner.close();
        	
            patientMap.put(patientId, patient);
        }
	}
	
	public void read(StringTokenizer tokenizer) {
		Patient patient = new Patient();
		patient.read(tokenizer);
		patientMap.put(patient.getId(), patient);
		return;
	}
	
	public Patient searchWithId(String patientId) {		
		return patientMap.get(patientId);
	}

	public void remove(Patient patient) {
		patientMap.remove(patient.getId());
	}


}
