package main.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.collectInterface.Manager;
import main.domain.Hospital;

public class HospitalManager implements Manager {
	public HashMap<String, Hospital> hospitalMap = new HashMap<>();

	@Override
	public void readAll(String dirPath) {

		Hospital hospital = null;
		String hospitalId;

		File parentPath = new File(dirPath);
		for (File folder : parentPath.listFiles()) {
			hospitalId = folder.getName();
			hospital = new Hospital();

			Scanner scanner = openFile(folder.getPath() + "\\info.txt");
			hospital.read(scanner);
			scanner.close();

			hospitalMap.put(hospitalId, hospital);

		}
	}

	public Hospital searchWithId(String hospitalId) {
		return hospitalMap.get(hospitalId);
	}

	public ArrayList<Hospital> searchWithKeywords(StringTokenizer tokenizer) {

		String address = null;
		String careType = null;
		String state = null;
		String keywords = null;

		if (tokenizer.hasMoreTokens()) {
			address = tokenizer.nextToken();
		}

		if (tokenizer.hasMoreTokens()) {
			careType = tokenizer.nextToken();
		}

		if (tokenizer.hasMoreTokens()) {
			state = tokenizer.nextToken();
		}

		if (tokenizer.hasMoreTokens()) {
			keywords = tokenizer.nextToken();
		}

		ArrayList<Hospital> hospitalList = new ArrayList<>();
		Hospital hospital = null;

		for (String hospitalId : hospitalMap.keySet()) {
			hospital = hospitalMap.get(hospitalId);
			if (hospital.matches(address, careType, state, keywords)) {
				hospitalList.add(hospital);
			}
		}

		return hospitalList;
	}

	public void remove(Hospital hospital) {
		hospitalMap.remove(hospital.getId());
	}

}
