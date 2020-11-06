package main.domain;

// 자가진단
public class SelfDiagnosis {
    private String symptom;
    private String period;
    private String MedicationBeingTaken;

    public SelfDiagnosis(String symptom, String period, String medicationBeingTaken) {
        this.symptom = symptom;
        this.period = period;
        MedicationBeingTaken = medicationBeingTaken;
    }
}
