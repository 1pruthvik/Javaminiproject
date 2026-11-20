package hospital;

/**
 * HospitalMain is the entry point of the project.
 * It demonstrates all required concepts step by step:
 *   1. Class + static counter + inheritance + interface
 *   2. Custom exception with try-catch-finally
 *   3. Threads with synchronized bed allocation
 */
public class HospitalMain {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      HOSPITAL PATIENT MANAGEMENT SYSTEM");
        System.out.println("=================================================\n");

        // ---------------------------------------------------------------
        // 1. CLASS + STATIC COUNTER + INHERITANCE + INTERFACE
        // ---------------------------------------------------------------
        System.out.println("--- 1. Patient Admission & Billing ---");
        InPatient in1 = new InPatient("Alice", 45, 5, 2000.0);
        in1.assignDoctor("Dr. Smith");

        OutPatient out1 = new OutPatient("Bob", 30, 500.0, 300.0);
        out1.assignDoctor("Dr. Jones");

        InPatient in2 = new InPatient("Carol", 60, 3, 2500.0);
        in2.assignDoctor("Dr. Lee");

        System.out.println(in1);
        System.out.println("InPatient Bill  = " + in1.calculateBill());
        System.out.println(out1);
        System.out.println("OutPatient Bill = " + out1.calculateBill());
        System.out.println(in2);
        System.out.println("InPatient Bill  = " + in2.calculateBill());
        System.out.println("Total patients admitted (static counter) = "
                + Patient.getAdmissionCounter());

        // ---------------------------------------------------------------
        // 2. CUSTOM EXCEPTION (try-catch-finally)
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. Custom Exception Demo (BedNotAvailableException) ---");
        BedManager demoBeds = new BedManager(2);
        try {
            System.out.println("Allocating bed... beds left = " + demoBeds.allocateBed());
            System.out.println("Allocating bed... beds left = " + demoBeds.allocateBed());
            System.out.println("Allocating another bed (should fail)...");
            demoBeds.allocateBed(); // throws BedNotAvailableException
        } catch (BedNotAvailableException e) {
            System.out.println("Caught exception: " + e.getMessage());
        } finally {
            System.out.println("Finally: admission resources cleaned up.");
        }

        // ---------------------------------------------------------------
        // 3. THREADS with synchronized bed allocation
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. Multi-threaded Admission (synchronized beds) ---");
        BedManager sharedBeds = new BedManager(3); // only 3 beds shared by both counters

        AdmissionCounter emergency = new AdmissionCounter("Emergency", sharedBeds,
                new String[]{"Eve", "Frank", "Grace", "Heidi"});
        AdmissionCounter general = new AdmissionCounter("General", sharedBeds,
                new String[]{"Ivan", "Judy", "Karl", "Liam", "Mona"});

        emergency.start();
        general.start();

        try {
            emergency.join(); // wait for emergency counter to finish
            general.join();   // wait for general counter to finish
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted: " + e.getMessage());
        }

        System.out.println("\nFinal available beds = " + sharedBeds.getAvailableBeds());
        System.out.println("Total patients admitted overall (static counter) = "
                + Patient.getAdmissionCounter());
        System.out.println("\n=================================================");
        System.out.println("              END OF PROGRAM");
        System.out.println("=================================================");
    }
}
