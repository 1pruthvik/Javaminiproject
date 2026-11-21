package hospital;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * HospitalMain: Interactive, Menu-Driven Hospital Patient Management System.
 *
 * All inputs are dynamically collected from the console.
 * Demonstrates:
 *   1. Classes and Objects
 *   2. Static Variables (admission counter tracking successful admissions)
 *   3. Abstract Classes and Methods (Patient, calculateBill)
 *   4. Inheritance (InPatient, OutPatient extending Patient)
 *   5. Method Overriding (calculateBill, displayDetails)
 *   6. Interfaces (Treatable with assignDoctor)
 *   7. Packages (hospital)
 *   8. User-defined Exceptions (BedNotAvailableException)
 *   9. Structured try-catch-finally
 *  10. Multithreading (Emergency & General Admission Threads)
 *  11. Synchronization (atomic BedManager bed allocation)
 *  12. Shared Resource Management
 */
public class HospitalMain {

    private static String hospitalName;
    private static BedManager bedManager;
    private static final Map<Integer, Patient> patientRegistry =
            Collections.synchronizedMap(new LinkedHashMap<Integer, Patient>());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("================================================================================");
        System.out.println("                 HOSPITAL PATIENT MANAGEMENT SYSTEM (P11)");
        System.out.println("================================================================================\n");

        // ---------------------------------------------------------------------
        // 1. HOSPITAL CONFIGURATION (DYNAMIC STARTUP)
        // ---------------------------------------------------------------------
        hospitalName = promptNonEmptyString(scanner, "Enter Hospital Name: ");
        int totalBeds = promptInt(scanner, "Enter Total Number of Inpatient Beds: ", 1, Integer.MAX_VALUE);

        bedManager = new BedManager(totalBeds);

        System.out.printf("%n[System Initialized] Hospital: '%s' | Capacity: %d Inpatient Beds%n%n",
                hospitalName, totalBeds);

        // ---------------------------------------------------------------------
        // 2. MAIN MENU LOOP
        // ---------------------------------------------------------------------
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = promptInt(scanner, "Enter your choice: ", 1, 12);
            System.out.println();

            switch (choice) {
                case 1:
                    handleRegisterInpatient(scanner);
                    break;
                case 2:
                    handleRegisterOutpatient(scanner);
                    break;
                case 3:
                    handleDisplayAllPatients();
                    break;
                case 4:
                    handleSearchPatientById(scanner);
                    break;
                case 5:
                    handleAssignDoctor(scanner);
                    break;
                case 6:
                    handleCalculatePatientBill(scanner);
                    break;
                case 7:
                    handleDisplayHospitalStatistics();
                    break;
                case 8:
                    handleDisplayBedAvailability();
                    break;
                case 9:
                    handleStartAdmissionThread(scanner, "Emergency");
                    break;
                case 10:
                    handleStartAdmissionThread(scanner, "General");
                    break;
                case 11:
                    handleDisplayAdmissionCounter();
                    break;
                case 12:
                    running = false;
                    System.out.println("================================================================================");
                    System.out.printf("Exiting %s Patient Management System. Goodbye!%n", hospitalName);
                    System.out.println("================================================================================");
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please select a valid number between 1 and 12.");
            }
            System.out.println();
        }
    }

    private static void printMainMenu() {
        System.out.println("========================================");
        System.out.println("       HOSPITAL PATIENT MANAGEMENT      ");
        System.out.println("========================================");
        System.out.println("1.  Register Inpatient");
        System.out.println("2.  Register Outpatient");
        System.out.println("3.  Display All Patients");
        System.out.println("4.  Search Patient by ID");
        System.out.println("5.  Assign Doctor");
        System.out.println("6.  Calculate Patient Bill");
        System.out.println("7.  Display Hospital Statistics");
        System.out.println("8.  Display Bed Availability");
        System.out.println("9.  Start Emergency Admission Thread");
        System.out.println("10. Start General Admission Thread");
        System.out.println("11. Display Admission Counter");
        System.out.println("12. Exit");
        System.out.println("========================================");
    }

    // -------------------------------------------------------------------------
    // OPTION 1: REGISTER INPATIENT
    // -------------------------------------------------------------------------
    private static void handleRegisterInpatient(Scanner scanner) {
        System.out.println("--- Register Inpatient ---");
        int id = promptUniquePatientId(scanner);
        String name = promptNonEmptyString(scanner, "Enter Patient Name: ");
        int age = promptInt(scanner, "Enter Patient Age: ", 0, 150);
        int days = promptInt(scanner, "Enter Admission Days: ", 0, 3650);
        double roomCharge = promptDouble(scanner, "Enter Daily Room Charges ($): ", 0.0);
        double docCharge = promptDouble(scanner, "Enter Doctor Consultation Charges ($): ", 0.0);
        double treatCharge = promptDouble(scanner, "Enter Treatment Charges ($): ", 0.0);

        InPatient inPatient = new InPatient(id, name, age, days, roomCharge, docCharge, treatCharge);

        boolean admissionSucceeded = false;
        try {
            // Synchronized bed allocation
            int bedsLeft = bedManager.allocateBed(inPatient);
            admissionSucceeded = true;
            patientRegistry.put(id, inPatient);

            System.out.printf("%n[SUCCESS] InPatient '%s' (ID: %d) successfully admitted to Bed #%d.%n",
                    inPatient.getName(), inPatient.getId(), inPatient.getBedNumber());
            System.out.printf("Available Inpatient Beds Remaining: %d / %d%n",
                    bedsLeft, bedManager.getTotalBeds());
            System.out.printf("Total Inpatient Bill: $%.2f%n", inPatient.calculateBill());

        } catch (BedNotAvailableException e) {
            // Handled exception
            System.out.printf("%n[ADMISSION FAILED] %s%n", e.getMessage());
            System.out.println("The patient could not be admitted. No record added to active registry.");
        } finally {
            System.out.printf("[FINALLY] Finished admission attempt for Patient ID: %d (Result: %s)%n",
                    id, admissionSucceeded ? "SUCCESS" : "REJECTED");
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 2: REGISTER OUTPATIENT
    // -------------------------------------------------------------------------
    private static void handleRegisterOutpatient(Scanner scanner) {
        System.out.println("--- Register Outpatient ---");
        int id = promptUniquePatientId(scanner);
        String name = promptNonEmptyString(scanner, "Enter Patient Name: ");
        int age = promptInt(scanner, "Enter Patient Age: ", 0, 150);
        double consultCharge = promptDouble(scanner, "Enter Consultation Charges ($): ", 0.0);
        double treatCharge = promptDouble(scanner, "Enter Treatment Charges ($): ", 0.0);
        double diagCharge = promptDouble(scanner, "Enter Diagnostic Charges ($): ", 0.0);

        OutPatient outPatient = new OutPatient(id, name, age, consultCharge, treatCharge, diagCharge);
        // Outpatients do not consume inpatient beds
        outPatient.markAdmitted();
        patientRegistry.put(id, outPatient);

        System.out.printf("%n[SUCCESS] OutPatient '%s' (ID: %d) successfully registered.%n",
                outPatient.getName(), outPatient.getId());
        System.out.println("Note: Outpatient registration does NOT consume inpatient beds.");
        System.out.printf("Total Outpatient Bill: $%.2f%n", outPatient.calculateBill());
    }

    // -------------------------------------------------------------------------
    // OPTION 3: DISPLAY ALL PATIENTS
    // -------------------------------------------------------------------------
    private static void handleDisplayAllPatients() {
        System.out.println("--- Registered Patients Directory ---");
        if (patientRegistry.isEmpty()) {
            System.out.println("No patients are currently registered in the system.");
            return;
        }

        System.out.printf("Total Registered Patients: %d%n%n", patientRegistry.size());
        synchronized (patientRegistry) {
            for (Patient p : patientRegistry.values()) {
                p.displayDetails();
                System.out.println();
            }
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 4: SEARCH PATIENT BY ID
    // -------------------------------------------------------------------------
    private static void handleSearchPatientById(Scanner scanner) {
        System.out.println("--- Search Patient by ID ---");
        int id = promptInt(scanner, "Enter Patient ID to search: ", 1, Integer.MAX_VALUE);
        Patient patient = patientRegistry.get(id);

        if (patient != null) {
            System.out.println("\n[Patient Record Found]");
            patient.displayDetails();
        } else {
            System.out.printf("%n[Not Found] No registered patient found with ID: %d.%n", id);
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 5: ASSIGN DOCTOR (Treatable Interface)
    // -------------------------------------------------------------------------
    private static void handleAssignDoctor(Scanner scanner) {
        System.out.println("--- Assign Doctor (Treatable Interface) ---");
        int id = promptInt(scanner, "Enter Patient ID: ", 1, Integer.MAX_VALUE);
        Patient patient = patientRegistry.get(id);

        if (patient == null) {
            System.out.printf("[Not Found] No registered patient found with ID: %d.%n", id);
            return;
        }

        System.out.printf("Current Assigned Doctor: %s%n",
                patient.getDoctorName() != null ? patient.getDoctorName() : "None");
        String doctorName = promptNonEmptyString(scanner, "Enter Doctor Name to assign: ");

        // Invoke Treatable interface method
        patient.assignDoctor(doctorName);
        System.out.printf("%n[SUCCESS] Assigned '%s' to Patient '%s' (ID: %d).%n",
                doctorName, patient.getName(), patient.getId());
    }

    // -------------------------------------------------------------------------
    // OPTION 6: CALCULATE PATIENT BILL
    // -------------------------------------------------------------------------
    private static void handleCalculatePatientBill(Scanner scanner) {
        System.out.println("--- Calculate Patient Bill ---");
        int id = promptInt(scanner, "Enter Patient ID: ", 1, Integer.MAX_VALUE);
        Patient patient = patientRegistry.get(id);

        if (patient == null) {
            System.out.printf("[Not Found] No registered patient found with ID: %d.%n", id);
            return;
        }

        System.out.println("\n================ BILL STATEMENT ================");
        System.out.printf("Patient ID      : %d%n", patient.getId());
        System.out.printf("Patient Name    : %s%n", patient.getName());
        System.out.printf("Patient Age     : %d%n", patient.getAge());
        System.out.printf("Doctor Assigned : %s%n",
                patient.getDoctorName() != null ? patient.getDoctorName() : "None");

        if (patient instanceof InPatient) {
            InPatient in = (InPatient) patient;
            System.out.println("Category        : In-Patient");
            System.out.printf("Assigned Bed    : Bed #%d%n", in.getBedNumber());
            System.out.printf("Admission Days  : %d days%n", in.getDaysAdmitted());
            System.out.printf("Daily Room Rate : $%.2f%n", in.getDailyRoomCharges());
            System.out.printf("Total Room Rent : $%.2f%n", in.getDaysAdmitted() * in.getDailyRoomCharges());
            System.out.printf("Doctor Charges  : $%.2f%n", in.getDoctorCharges());
            System.out.printf("Treatment Fee   : $%.2f%n", in.getTreatmentCharges());
            System.out.println("Formula         : (Days * Daily Rate) + Doctor Charges + Treatment Charges");
        } else if (patient instanceof OutPatient) {
            OutPatient out = (OutPatient) patient;
            System.out.println("Category        : Out-Patient (No bed occupied)");
            System.out.printf("Consultation Fee: $%.2f%n", out.getConsultationCharges());
            System.out.printf("Treatment Charges: $%.2f%n", out.getTreatmentCharges());
            System.out.printf("Diagnostic Fees : $%.2f%n", out.getDiagnosticCharges());
            System.out.println("Formula         : Consultation + Treatment + Diagnostics");
        }

        // Polymorphic invocation of calculateBill()
        double totalBill = patient.calculateBill();
        System.out.println("------------------------------------------------");
        System.out.printf("FINAL PAYABLE AMOUNT : $%.2f%n", totalBill);
        System.out.println("================================================");
    }

    // -------------------------------------------------------------------------
    // OPTION 7: DISPLAY HOSPITAL STATISTICS
    // -------------------------------------------------------------------------
    private static void handleDisplayHospitalStatistics() {
        System.out.println("--- Hospital System Statistics ---");
        System.out.printf("Hospital Name               : %s%n", hospitalName);
        System.out.printf("Total Inpatient Bed Capacity: %d%n", bedManager.getTotalBeds());
        System.out.printf("Currently Occupied Beds     : %d%n", bedManager.getOccupiedBeds());
        System.out.printf("Available Inpatient Beds    : %d%n", bedManager.getAvailableBeds());

        int inPatientCount = 0;
        int outPatientCount = 0;
        synchronized (patientRegistry) {
            for (Patient p : patientRegistry.values()) {
                if (p instanceof InPatient) inPatientCount++;
                else if (p instanceof OutPatient) outPatientCount++;
            }
        }

        System.out.printf("Registered InPatients       : %d%n", inPatientCount);
        System.out.printf("Registered OutPatients      : %d%n", outPatientCount);
        System.out.printf("Total Active Patients       : %d%n", patientRegistry.size());
        System.out.printf("Total Successful Admissions : %d (via Patient.getAdmissionCounter())%n",
                Patient.getAdmissionCounter());

        boolean invariantValid = (bedManager.getOccupiedBeds() + bedManager.getAvailableBeds()) == bedManager.getTotalBeds();
        System.out.printf("Bed Invariant Integrity     : [%s]%n", invariantValid ? "VALID" : "CORRUPTED");
    }

    // -------------------------------------------------------------------------
    // OPTION 8: DISPLAY BED AVAILABILITY
    // -------------------------------------------------------------------------
    private static void handleDisplayBedAvailability() {
        System.out.println("--- Bed Allocation Status ---");
        System.out.printf("Total Beds: %d | Occupied: %d | Available: %d%n%n",
                bedManager.getTotalBeds(), bedManager.getOccupiedBeds(), bedManager.getAvailableBeds());

        for (int i = 1; i <= bedManager.getTotalBeds(); i++) {
            boolean occupied = bedManager.isBedOccupied(i);
            InPatient occupant = bedManager.getBedOccupant(i);
            if (occupied && occupant != null) {
                System.out.printf("  [Bed #%d] OCCUPIED -> Patient: %s (ID: %d, Doctor: %s)%n",
                        i, occupant.getName(), occupant.getId(),
                        occupant.getDoctorName() != null ? occupant.getDoctorName() : "Not Assigned");
            } else if (occupied) {
                System.out.printf("  [Bed #%d] OCCUPIED%n", i);
            } else {
                System.out.printf("  [Bed #%d] AVAILABLE [FREE]%n", i);
            }
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 9 & 10: DYNAMIC MULTITHREADED ADMISSION
    // -------------------------------------------------------------------------
    private static void handleStartAdmissionThread(Scanner scanner, String counterType) {
        System.out.printf("--- Start %s Admission Thread ---%n", counterType);
        int requestCount = promptInt(scanner, "Enter number of patient admission requests to generate: ", 1, 100);

        List<InPatient> requests = new ArrayList<>();
        System.out.printf("%nEnter details for each of the %d %s admission request(s):%n",
                requestCount, counterType);

        for (int i = 1; i <= requestCount; i++) {
            System.out.printf("%n-- %s Request #%d --%n", counterType, i);
            int id = promptUniquePatientIdInBatch(scanner, requests);
            String name = promptNonEmptyString(scanner, "Enter Patient Name: ");
            int age = promptInt(scanner, "Enter Age: ", 0, 150);
            int days = promptInt(scanner, "Enter Estimated Admission Days: ", 0, 365);
            double roomCharge = promptDouble(scanner, "Enter Daily Room Rate ($): ", 0.0);
            double docCharge = promptDouble(scanner, "Enter Doctor Consultation Charges ($): ", 0.0);
            double treatCharge = promptDouble(scanner, "Enter Treatment Charges ($): ", 0.0);

            requests.add(new InPatient(id, name, age, days, roomCharge, docCharge, treatCharge));
        }

        System.out.printf("%n[Thread Launch] Starting %s Admission Thread for %d patient(s)...%n",
                counterType, requests.size());

        AdmissionCounter thread = new AdmissionCounter(counterType, bedManager, requests, patientRegistry);

        if (counterType.equalsIgnoreCase("Emergency")) {
            thread.setPriority(Thread.MAX_PRIORITY);
        } else {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        thread.start();

        // Coordinate with menu: join thread to complete admission processing cleanly
        try {
            thread.join();
            System.out.println("[Thread Completed] Admission thread processing finished successfully.");
        } catch (InterruptedException e) {
            System.err.println("Admission thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Summary report
        System.out.println("\n------------------------------------------------");
        System.out.printf("%s Admission Thread Report:%n", counterType);
        System.out.printf("Total Requests Processed : %d%n", thread.getRequestCount());
        System.out.printf("Successful Admissions    : %d%n", thread.getAdmittedPatients().size());
        System.out.printf("Failed Admissions        : %d%n", thread.getRejectedPatients().size());
        System.out.printf("Current Occupied Beds    : %d%n", bedManager.getOccupiedBeds());
        System.out.printf("Remaining Available Beds : %d%n", bedManager.getAvailableBeds());
        System.out.printf("Total Successful Counter : %d%n", Patient.getAdmissionCounter());
        System.out.println("------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // OPTION 11: DISPLAY ADMISSION COUNTER
    // -------------------------------------------------------------------------
    private static void handleDisplayAdmissionCounter() {
        System.out.println("--- Static Admission Counter ---");
        System.out.printf("Current Value: %d%n", Patient.getAdmissionCounter());
        System.out.println("Note: This static counter reflects only successfully admitted/registered patients.");
        System.out.println("Failed admission attempts due to bed unavailability do NOT increment this counter.");
    }

    // -------------------------------------------------------------------------
    // INPUT VALIDATION HELPERS
    // -------------------------------------------------------------------------
    private static String promptNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[Validation Error] Input cannot be blank. Please try again.");
        }
    }

    private static int promptInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[Validation Error] Please enter an integer between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[Validation Error] Invalid number format. Please enter a valid integer.");
            }
        }
    }

    private static double promptDouble(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= min) {
                    return value;
                }
                System.out.printf("[Validation Error] Value cannot be less than %.2f.%n", min);
            } catch (NumberFormatException e) {
                System.out.println("[Validation Error] Invalid monetary value. Please enter a valid numeric amount.");
            }
        }
    }

    private static int promptUniquePatientId(Scanner scanner) {
        while (true) {
            int id = promptInt(scanner, "Enter Patient ID: ", 1, Integer.MAX_VALUE);
            if (!patientRegistry.containsKey(id)) {
                return id;
            }
            System.out.printf("[Validation Error] Patient ID %d already exists in the system. IDs must be unique.%n", id);
        }
    }

    private static int promptUniquePatientIdInBatch(Scanner scanner, List<InPatient> batch) {
        while (true) {
            int id = promptInt(scanner, "Enter Patient ID: ", 1, Integer.MAX_VALUE);
            if (patientRegistry.containsKey(id)) {
                System.out.printf("[Validation Error] Patient ID %d already exists in the hospital registry.%n", id);
                continue;
            }
            boolean existsInBatch = false;
            for (InPatient p : batch) {
                if (p.getId() == id) {
                    existsInBatch = true;
                    break;
                }
            }
            if (existsInBatch) {
                System.out.printf("[Validation Error] Patient ID %d has already been entered in this batch.%n", id);
                continue;
            }
            return id;
        }
    }
}
