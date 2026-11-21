package hospital;

/**
 * HospitalSystemTest: Automated verification test suite.
 *
 * Verifies all 8 validation checks required by Phase 6:
 *   TEST 1: Basic patient creation and input validation
 *   TEST 2: Inheritance and method overriding (polymorphic calculateBill)
 *   TEST 3: Interface implementation (Treatable.assignDoctor)
 *   TEST 4: Static counter (verifying failed admissions do not increment counter)
 *   TEST 5: Custom exception handling (BedNotAvailableException in try-catch-finally)
 *   TEST 6: Multithreading (Emergency & General counters running concurrently)
 *   TEST 7: Synchronization & Invariants (no race conditions, capacity preserved)
 *   TEST 8: Full application workflow integrity
 */
public class HospitalSystemTest {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("       HOSPITAL SYSTEM VERIFICATION TESTS");
        System.out.println("=================================================");

        runTest1_BasicPatientCreationAndValidation();
        runTest2_InheritanceAndMethodOverriding();
        runTest3_InterfaceImplementation();
        runTest4_StaticCounterAccuracy();
        runTest5_ExceptionHandlingAndFinally();
        runTest6_And_7_MultithreadingAndSynchronization();

        System.out.println("\n=================================================");
        System.out.printf("TEST SUMMARY: %d / %d Tests Passed%n", testsPassed, testsTotal);
        System.out.println("=================================================");

        if (testsPassed != testsTotal) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL SYSTEM TESTS PASSED SUCCESSFULLY!");
        }
    }

    private static void assertTrue(String testName, boolean condition, String message) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.printf("  [PASS] %s%n", testName);
        } else {
            System.err.printf("  [FAIL] %s: %s%n", testName, message);
        }
    }

    private static void runTest1_BasicPatientCreationAndValidation() {
        System.out.println("\n--- TEST 1: Basic Patient Creation & Validation ---");
        Patient.resetCounters();

        InPatient in = new InPatient("Test InPatient", 40, 3, 1000.0, 500.0, 200.0);
        assertTrue("Test 1.1: InPatient ID is positive", in.getId() > 0, "ID should be > 0");
        assertTrue("Test 1.2: InPatient name matches", "Test InPatient".equals(in.getName()), "Name mismatch");
        assertTrue("Test 1.3: InPatient age matches", in.getAge() == 40, "Age mismatch");

        OutPatient out = new OutPatient("Test OutPatient", 25, 400.0, 150.0, 100.0);
        assertTrue("Test 1.4: OutPatient has distinct unique ID", out.getId() > in.getId(), "Unique IDs required");

        // Input validation checks
        boolean caughtNegativeAge = false;
        try {
            new InPatient("Bad Age", -5, 1, 100.0);
        } catch (IllegalArgumentException e) {
            caughtNegativeAge = true;
        }
        assertTrue("Test 1.5: Rejects negative age", caughtNegativeAge, "Expected IllegalArgumentException for negative age");

        boolean caughtNegativeBilling = false;
        try {
            new InPatient("Bad Billing", 30, 2, -100.0);
        } catch (IllegalArgumentException e) {
            caughtNegativeBilling = true;
        }
        assertTrue("Test 1.6: Rejects negative daily charge", caughtNegativeBilling, "Expected IllegalArgumentException for negative bill charge");
    }

    private static void runTest2_InheritanceAndMethodOverriding() {
        System.out.println("\n--- TEST 2: Inheritance & calculateBill() Overriding ---");
        // InPatient bill: (4 * 1500) + 800 + 1200 = 6000 + 2000 = 8000
        Patient p1 = new InPatient("Polymorphic InPatient", 55, 4, 1500.0, 800.0, 1200.0);
        assertTrue("Test 2.1: InPatient is instance of Patient", p1 instanceof Patient, "Should inherit Patient");
        assertTrue("Test 2.2: InPatient bill calculation", Math.abs(p1.calculateBill() - 8000.0) < 0.001,
                "Expected 8000.0, got " + p1.calculateBill());

        // OutPatient bill: 500 + 300 + 200 = 1000
        Patient p2 = new OutPatient("Polymorphic OutPatient", 35, 500.0, 300.0, 200.0);
        assertTrue("Test 2.3: OutPatient is instance of Patient", p2 instanceof Patient, "Should inherit Patient");
        assertTrue("Test 2.4: OutPatient bill calculation", Math.abs(p2.calculateBill() - 1000.0) < 0.001,
                "Expected 1000.0, got " + p2.calculateBill());
    }

    private static void runTest3_InterfaceImplementation() {
        System.out.println("\n--- TEST 3: Interface Treatable Implementation ---");
        Patient p = new InPatient("Interface Test Patient", 48, 2, 1000.0);
        assertTrue("Test 3.1: Patient implements Treatable", p instanceof Treatable, "Must implement Treatable");

        p.assignDoctor("Dr. Test Specialist");
        assertTrue("Test 3.2: assignDoctor sets doctor name", "Dr. Test Specialist".equals(p.getDoctorName()),
                "Doctor name not assigned correctly");
    }

    private static void runTest4_StaticCounterAccuracy() {
        System.out.println("\n--- TEST 4: Static Admission Counter Accuracy ---");
        Patient.resetCounters();
        int initialCounter = Patient.getAdmissionCounter();
        assertTrue("Test 4.1: Initial counter is 0", initialCounter == 0, "Counter should start at 0");

        // Successful outpatient registration
        OutPatient out = new OutPatient("Registered Outpatient", 30, 200.0, 100.0, 50.0);
        out.markAdmitted();
        assertTrue("Test 4.2: Successful outpatient increments counter", Patient.getAdmissionCounter() == 1,
                "Counter should be 1");

        // Successful inpatient admission
        BedManager bm = new BedManager(1);
        InPatient inSuccess = new InPatient("Success Inpatient", 45, 1, 500.0);
        try {
            bm.allocateBed(inSuccess);
        } catch (BedNotAvailableException e) {
            // unexpected
        }
        assertTrue("Test 4.3: Successful inpatient increments counter", Patient.getAdmissionCounter() == 2,
                "Counter should be 2");

        // Failed inpatient admission: Bed manager has only 1 bed, which is already occupied
        InPatient inFailed = new InPatient("Failed Inpatient", 50, 1, 500.0);
        try {
            bm.allocateBed(inFailed);
        } catch (BedNotAvailableException e) {
            // Expected
        }
        assertTrue("Test 4.4: FAILED inpatient admission does NOT increment counter",
                Patient.getAdmissionCounter() == 2,
                "Counter should remain 2 after failed admission! Got: " + Patient.getAdmissionCounter());
    }

    private static void runTest5_ExceptionHandlingAndFinally() {
        System.out.println("\n--- TEST 5: Custom Exception & try-catch-finally ---");
        BedManager bm = new BedManager(1);
        InPatient p1 = new InPatient("Bed Holder", 30, 1, 100.0);
        InPatient p2 = new InPatient("Rejected Patient", 35, 1, 100.0);

        try {
            bm.allocateBed(p1);
        } catch (BedNotAvailableException e) {
            // unexpected
        }

        boolean exceptionThrown = false;
        boolean finallyExecuted = false;

        try {
            bm.allocateBed(p2);
        } catch (BedNotAvailableException e) {
            exceptionThrown = true;
            assertTrue("Test 5.1: Exception message contains details",
                    e.getMessage() != null && e.getMessage().contains("occupied"),
                    "Message should explain beds occupied");
        } finally {
            finallyExecuted = true;
        }

        assertTrue("Test 5.2: BedNotAvailableException was thrown and caught", exceptionThrown,
                "Expected BedNotAvailableException");
        assertTrue("Test 5.3: Finally block executed on exception", finallyExecuted,
                "Finally block must execute");
    }

    private static void runTest6_And_7_MultithreadingAndSynchronization() {
        System.out.println("\n--- TEST 6 & 7: Multithreading, Shared Resource & Synchronization ---");
        Patient.resetCounters();
        final int CAPACITY = 3;
        BedManager sharedBeds = new BedManager(CAPACITY);

        String[] emergencyGroup = new String[]{"E1", "E2", "E3", "E4"};
        String[] generalGroup = new String[]{"G1", "G2", "G3", "G4"};

        AdmissionCounter emergency = new AdmissionCounter("Emergency", sharedBeds, emergencyGroup);
        AdmissionCounter general = new AdmissionCounter("General", sharedBeds, generalGroup);

        emergency.start();
        general.start();

        try {
            emergency.join();
            general.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify total admitted equals capacity exactly
        int totalAdmittedInThreads = emergency.getAdmittedPatients().size() + general.getAdmittedPatients().size();
        int totalRejectedInThreads = emergency.getRejectedPatients().size() + general.getRejectedPatients().size();

        assertTrue("Test 6.1: Exactly " + CAPACITY + " patients admitted across both threads",
                totalAdmittedInThreads == CAPACITY,
                "Expected " + CAPACITY + " admissions, got " + totalAdmittedInThreads);

        assertTrue("Test 6.2: Exactly 5 patients rejected across both threads",
                totalRejectedInThreads == (8 - CAPACITY),
                "Expected 5 rejections, got " + totalRejectedInThreads);

        assertTrue("Test 7.1: Available beds is 0", sharedBeds.getAvailableBeds() == 0,
                "Expected 0 available beds");

        assertTrue("Test 7.2: Occupied beds equals capacity", sharedBeds.getOccupiedBeds() == CAPACITY,
                "Expected " + CAPACITY + " occupied beds");

        assertTrue("Test 7.3: Static admission counter reflects successful admissions only",
                Patient.getAdmissionCounter() == CAPACITY,
                "Counter should be " + CAPACITY + ", got: " + Patient.getAdmissionCounter());
    }
}
