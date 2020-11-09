package main;

import main.domain.Hospital;
import main.domain.Manageable;
import main.domain.Patient;

import java.io.*;
import java.util.*;

public class ServerApplication {

    private Scanner scan = new Scanner(System.in);
    public static HashMap<String, Patient> patientList = new HashMap<>();
    public static List<Hospital> hospitalList = new ArrayList<>();
    public List<String> patientIdList = new ArrayList<>();
    public List<String> hospitalIdList = new ArrayList<>();

    private void run() throws IOException {
        readAllPatientId("storage/Account/patients");
        readAllHospitalId("storage/Account/hospitals");
        for (String patientId : patientIdList) {
            readAllPatient("storage/Account/patients/" + patientId + "/info.txt");
        }
        for (String hospitalId : hospitalIdList) {
            readAllPatient("storage/Account/hospitals/" + hospitalId + "/info.txt");
        }
    }

    private BufferedReader readAllFile(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }

    private void readAllPatientId(String folderName) {
        File patientFolder = new File(folderName);
        for (File file : patientFolder.listFiles()) {
            if (file.isDirectory()) {
                patientIdList.add(file.getName());
            }
        }
    }

    private void readAllPatient(String filePath) throws IOException {
        BufferedReader patientReader = readAllFile(filePath);
        String line = "";
        Patient patient = new Patient();
        while((line = patientReader.readLine()) != null) {
            patientList.put(line, patient);
        }
        patientReader.close();
    }

    private void readAllHospitalId(String fileName) {
        File hospitalFolder = new File(fileName);
        for (File file : hospitalFolder.listFiles()) {
            if (file.isDirectory()) {
                hospitalIdList.add(file.getName());
            }
        }
    }

    private void readAllHospital(String filePath) throws IOException {
        BufferedReader hospitalReader = readAllFile(filePath);
        String line = "";
        Hospital hospital = new Hospital();
        while((line = hospitalReader.readLine()) != null) {
            hospitalList.add(hospital);
        }
        hospitalReader.close();
    }



    public static void main (String[] args) throws IOException {
        ServerApplication serverApplication = new ServerApplication();
        serverApplication.run();
    }
}
