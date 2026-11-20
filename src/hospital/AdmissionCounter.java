package hospital;

/**
 * AdmissionCounter is a Thread (requirement: Thread).
 *
 * Two counters run at the same time:
 *   - "Emergency" counter
 *   - "General"   counter
 *
 * Both share ONE BedManager, so bed allocation is synchronized between them.
 * When a bed cannot be allocated, the custom exception is caught.
 */
public class AdmissionCounter extends Thread {

    private final String counterType;     // "Emergency" or "General"
    private final BedManager bedManager;  // shared, synchronized resource
    private final String[] patientNames;

    public AdmissionCounter(String counterType, BedManager bedManager, String[] patientNames) {
        this.counterType = counterType;
        this.bedManager = bedManager;
        this.patientNames = patientNames;
        this.setName(counterType + " Counter");
    }

    @Override
    public void run() {
        for (String patientName : patientNames) {
            Patient patient = null;
            try {
                // Synchronized bed allocation (shared between threads)
                int bedsLeft = bedManager.allocateBed();

                // Create the proper patient type for this counter.
                if (counterType.equalsIgnoreCase("Emergency")) {
                    patient = new InPatient(patientName, 50, 2, 2000.0);
                } else {
                    patient = new OutPatient(patientName, 40, 500.0, 200.0);
                }

                patient.assignDoctor("Dr. OnCall"); // Treatable interface

                System.out.println("[" + counterType + "] Admitted " + patient.getName()
                        + " (ID " + patient.getId() + ") | Bill = " + patient.calculateBill()
                        + " | Beds left = " + bedsLeft);

            } catch (BedNotAvailableException e) {
                // Custom exception caught here
                System.out.println("[" + counterType + "] Could NOT admit " + patientName
                        + " -> " + e.getMessage());

            } finally {
                // Always runs, even when an exception is thrown
                System.out.println("[" + counterType + "] Finished processing " + patientName + ".");
            }
        }
    }
}
