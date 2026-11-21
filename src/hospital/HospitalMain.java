package hospital;

/**
 * HospitalMain: Entry point for the Hospital Patient Management System.
 *
 * Demonstrates all 12 core project requirements:
 *   1. Classes and Objects
 *   2. Static Variables (admission counter and id sequence)
 *   3. Abstract Classes and Methods (Patient, calculateBill)
 *   4. Inheritance (InPatient, OutPatient extending Patient)
 *   5. Method Overriding (calculateBill, displayDetails)
 *   6. Interfaces (Treatable with assignDoctor)
 *   7. Packages (package hospital)
 *   8. User-defined Exceptions (BedNotAvailableException)
 *   9. try-catch-finally handling
 *  10. Multithreading (Emergency and General admission threads)
 *  11. Synchronization (atomic bed allocation critical section)
 *  12. Shared Resource Management (BedManager shared between threads)
 */
public class HospitalMain {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                 HOSPITAL PATIENT MANAGEMENT SYSTEM (P11)");
        System.out.println("================================================================================\n");

        // ---------------------------------------------------------------------
        // STEP 1: INITIALIZE SHARED BED MANAGER
        // ---------------------------------------------------------------------
        final int TOTAL_HOSPITAL_BEDS = 3; // Configurable small capacity to clearly demonstrate exhaustion
        System.out.printf(">> Initializing Hospital Bed Management with Capacity: %d Beds%n%n", TOTAL_HOSPITAL_BEDS);
        BedManager hospitalBedManager = new BedManager(TOTAL_HOSPITAL_BEDS);

        // ---------------------------------------------------------------------
        // STEP 2 & 3: DEMONSTRATE OUTPATIENT REGISTRATION & BILLING
        // ---------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PHASE A: OutPatient Registration & Billing (Does NOT Occupy Inpatient Beds)");
        System.out.println("--------------------------------------------------------------------------------");

        OutPatient outPatient1 = new OutPatient("Alice Walker", 29, 600.0, 350.0, 250.0);
        // Step 5: Assign doctor through Treatable interface
        outPatient1.assignDoctor("Dr. Gupta (General Physician)");
        outPatient1.markAdmitted(); // Successfully registered outpatient

        OutPatient outPatient2 = new OutPatient("Bob Martin", 42, 500.0, 200.0, 150.0);
        outPatient2.assignDoctor("Dr. Patel (Dermatologist)");
        outPatient2.markAdmitted(); // Successfully registered outpatient

        // Step 6: Display patient bills and details
        outPatient1.displayDetails();
        System.out.println();
        outPatient2.displayDetails();

        System.out.printf("%n[Audit Check] Available Beds remaining: %d / %d (Beds untouched by outpatients)%n",
                hospitalBedManager.getAvailableBeds(), hospitalBedManager.getTotalBeds());
        System.out.printf("[Audit Check] Current Successful Admissions (Static Counter): %d%n%n",
                Patient.getAdmissionCounter());

        // ---------------------------------------------------------------------
        // STEP 4, 5, 6: DEMONSTRATE INPATIENT REGISTRATION & BILLING
        // ---------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PHASE B: Direct InPatient Registration (Allocates Inpatient Bed #1)");
        System.out.println("--------------------------------------------------------------------------------");

        InPatient inPatient1 = new InPatient("Catherine Davis", 64, 4, 2000.0, 1500.0, 2500.0);
        try {
            int bedsRemaining = hospitalBedManager.allocateBed(inPatient1);
            inPatient1.assignDoctor("Dr. Sarah Vance (Cardiologist)");
            System.out.printf("Successfully admitted %s into Bed #%d. Beds remaining: %d%n",
                    inPatient1.getName(), inPatient1.getBedNumber(), bedsRemaining);
        } catch (BedNotAvailableException e) {
            System.out.println("Admission failed: " + e.getMessage());
        }

        System.out.println();
        inPatient1.displayDetails();

        System.out.printf("%n[Audit Check] Available Beds remaining: %d / %d (Occupied: %d)%n",
                hospitalBedManager.getAvailableBeds(), hospitalBedManager.getTotalBeds(),
                hospitalBedManager.getOccupiedBeds());
        System.out.printf("[Audit Check] Current Successful Admissions (Static Counter): %d%n%n",
                Patient.getAdmissionCounter());

        // ---------------------------------------------------------------------
        // STEP 7-14: MULTITHREADED ADMISSION DEMONSTRATION WITH EXCEPTION HANDLING
        // ---------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PHASE C: Concurrent Multithreaded Admission (Emergency vs General Counter)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Available Beds before thread launch: %d%n", hospitalBedManager.getAvailableBeds());
        System.out.println("Launching 2 competing threads with 6 total patient requests:");
        System.out.println(" - Emergency Counter: David (Critical), Emma (Trauma), Frank (Emergency)");
        System.out.println(" - General Counter  : Grace (Ward), Henry (Observation), Isabel (Elective)");
        System.out.println("Since only 2 beds remain, exactly 2 admissions will succeed and 4 will fail.\n");

        String[] emergencyBatch = new String[]{"David Ross", "Emma Watson", "Frank Miller"};
        String[] generalBatch = new String[]{"Grace Hopper", "Henry Ford", "Isabel Archer"};

        AdmissionCounter emergencyCounter = new AdmissionCounter("Emergency", hospitalBedManager, emergencyBatch);
        AdmissionCounter generalCounter = new AdmissionCounter("General", hospitalBedManager, generalBatch);

        // Emergency counter given higher thread priority (demonstrates priority hint)
        emergencyCounter.setPriority(Thread.MAX_PRIORITY);
        generalCounter.setPriority(Thread.NORM_PRIORITY);

        // Start both threads concurrently
        emergencyCounter.start();
        generalCounter.start();

        // Step 14: Wait for both threads to complete using join()
        try {
            emergencyCounter.join();
            generalCounter.join();
            System.out.println("\n[Synchronization] Both admission threads have joined the main thread.");
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted while waiting for admission threads: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // ---------------------------------------------------------------------
        // STEP 15: FINAL HOSPITAL AUDIT & SYSTEM STATISTICS
        // ---------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("PHASE D: Final Hospital System Statistics & Verification Audit");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total Hospital Bed Capacity : %d%n", hospitalBedManager.getTotalBeds());
        System.out.printf("Occupied Beds Count         : %d%n", hospitalBedManager.getOccupiedBeds());
        System.out.printf("Available Beds Count        : %d%n", hospitalBedManager.getAvailableBeds());
        System.out.printf("Bed Invariant Check (Occ+Av): %d == %d [%s]%n",
                hospitalBedManager.getOccupiedBeds() + hospitalBedManager.getAvailableBeds(),
                hospitalBedManager.getTotalBeds(),
                (hospitalBedManager.getOccupiedBeds() + hospitalBedManager.getAvailableBeds() == hospitalBedManager.getTotalBeds()) ? "VALID" : "VIOLATED");

        System.out.printf("Total Successful Admissions : %d (via Patient.getAdmissionCounter())%n",
                Patient.getAdmissionCounter());

        // Inpatient capacity verification
        boolean capacityRespected = hospitalBedManager.getOccupiedBeds() <= hospitalBedManager.getTotalBeds()
                && hospitalBedManager.getAvailableBeds() >= 0;
        System.out.printf("Capacity Limit Preserved    : %b%n", capacityRespected);

        System.out.println("\n================================================================================");
        System.out.println("                       END OF DEMONSTRATION WORKFLOW");
        System.out.println("================================================================================");
    }
}
