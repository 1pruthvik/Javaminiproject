package hospital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AdmissionCounter: Multithreaded admission counter.
 *
 * Demonstrates:
 *   - Extending Thread (requirement: Multithreading)
 *   - Competing for a shared resource (BedManager)
 *   - Handling custom BedNotAvailableException
 *   - Robust try-catch-finally block execution
 *   - Meaningful thread naming and logging
 */
public class AdmissionCounter extends Thread {

    private final String counterType;
    private final BedManager bedManager;
    private final List<InPatient> patientRequests;
    private final List<InPatient> admittedPatients = new ArrayList<>();
    private final List<InPatient> rejectedPatients = new ArrayList<>();

    /**
     * Constructor accepting an array of patient names and generating InPatient admission requests.
     *
     * @param counterType  "Emergency" or "General"
     * @param bedManager   shared BedManager resource
     * @param patientNames array of patient names requesting admission
     */
    public AdmissionCounter(String counterType, BedManager bedManager, String[] patientNames) {
        super(counterType + "-Admission-Thread");
        this.counterType = counterType;
        this.bedManager = bedManager;
        this.patientRequests = new ArrayList<>();

        // Generate realistic InPatient requests for each name
        int baseAge = counterType.equalsIgnoreCase("Emergency") ? 52 : 36;
        for (int i = 0; i < patientNames.length; i++) {
            String name = patientNames[i];
            int days = counterType.equalsIgnoreCase("Emergency") ? (2 + i) : (3 + (i % 2));
            double dailyRate = counterType.equalsIgnoreCase("Emergency") ? 2500.0 : 1500.0;
            double docCharge = counterType.equalsIgnoreCase("Emergency") ? 1000.0 : 500.0;
            double treatCharge = counterType.equalsIgnoreCase("Emergency") ? 1500.0 : 750.0;
            this.patientRequests.add(new InPatient(name, baseAge + (i * 3), days, dailyRate, docCharge, treatCharge));
        }
    }

    /**
     * Constructor accepting pre-constructed InPatient admission requests.
     */
    public AdmissionCounter(String counterType, BedManager bedManager, List<InPatient> patientRequests) {
        super(counterType + "-Admission-Thread");
        this.counterType = counterType;
        this.bedManager = bedManager;
        this.patientRequests = new ArrayList<>(patientRequests);
    }

    @Override
    public void run() {
        System.out.printf("[%s] Started admission processing queue of %d patient(s).%n",
                getName(), patientRequests.size());

        for (InPatient patient : patientRequests) {
            boolean admissionSuccessful = false;
            try {
                // Synchronized bed allocation protecting the complete critical section
                int bedsLeft = bedManager.allocateBed(patient);

                // Assign appropriate doctor via Treatable interface
                String doctor = counterType.equalsIgnoreCase("Emergency")
                        ? "Dr. Adams (Emergency Care Specialist)"
                        : "Dr. Clark (Internal Medicine Consultant)";
                patient.assignDoctor(doctor);

                admissionSuccessful = true;
                admittedPatients.add(patient);

                System.out.printf("[%s] [SUCCESS] Admitted %s (ID: %d) -> Assigned Bed #%d | Doctor: %s | Bill: $%.2f | Beds Left: %d%n",
                        getName(), patient.getName(), patient.getId(), patient.getBedNumber(),
                        patient.getDoctorName(), patient.calculateBill(), bedsLeft);

            } catch (BedNotAvailableException e) {
                // Custom exception handled here
                rejectedPatients.add(patient);
                System.out.printf("[%s] [REJECTED] Admission failed for %s (ID: %d) -> %s%n",
                        getName(), patient.getName(), patient.getId(), e.getMessage());

            } finally {
                // The finally block executes regardless of success or exception
                System.out.printf("[%s] [FINALLY] Request completed for %s (ID: %d, Result: %s).%n",
                        getName(), patient.getName(), patient.getId(),
                        admissionSuccessful ? "ADMITTED" : "DENIED");
            }

            // Yield / small pause to allow genuine concurrent thread interleaving
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.printf("[%s] Thread interrupted during delay.%n", getName());
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("[%s] Finished all requests. Total admitted: %d, Total rejected: %d.%n",
                getName(), admittedPatients.size(), rejectedPatients.size());
    }

    public List<InPatient> getAdmittedPatients() {
        return Collections.unmodifiableList(admittedPatients);
    }

    public List<InPatient> getRejectedPatients() {
        return Collections.unmodifiableList(rejectedPatients);
    }

    public String getCounterType() {
        return counterType;
    }
}
