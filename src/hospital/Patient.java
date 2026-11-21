package hospital;

/**
 * Patient: Abstract base class representing a hospital patient.
 *
 * Implements Treatable interface so any patient can be assigned a doctor.
 * Demonstrates:
 *   - Abstract class and abstract method (calculateBill)
 *   - Static variable (admissionCounter for successfully admitted patients)
 *   - Unique patient ID (user-provided or auto-sequenced)
 *   - "this" keyword for field initialization
 *   - Input validation (non-negative age, valid name, positive ID)
 *   - Information hiding and encapsulation
 */
public abstract class Patient implements Treatable {

    // Thread-safe sequence generator for unique patient IDs across all requests
    private static int idSequence = 0;

    // Static admission counter tracking successfully admitted patients only
    private static int admissionCounter = 0;

    private final int id;
    private String name;
    private int age;
    private String doctorName;
    private boolean admitted = false;

    /**
     * Constructs a patient with user-specified unique ID, validated name, and age.
     *
     * @param id   unique patient ID (must be > 0)
     * @param name patient's full name (non-null, non-empty)
     * @param age  patient's age (non-negative)
     */
    public Patient(int id, String name, int age) {
        if (id <= 0) {
            throw new IllegalArgumentException("Patient ID must be greater than zero: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be null or empty.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Patient age cannot be negative: " + age);
        }

        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.doctorName = null;

        synchronized (Patient.class) {
            if (id > idSequence) {
                idSequence = id;
            }
        }
    }

    /**
     * Constructs a patient with auto-generated unique ID, validated name, and age.
     *
     * @param name patient's full name (non-null, non-empty)
     * @param age  patient's age (non-negative)
     */
    public Patient(String name, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be null or empty.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Patient age cannot be negative: " + age);
        }

        synchronized (Patient.class) {
            this.id = ++idSequence;
        }
        this.name = name.trim();
        this.age = age;
        this.doctorName = null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public boolean isAdmitted() {
        return admitted;
    }

    /**
     * Marks this patient as successfully admitted and increments the static admission counter.
     */
    public synchronized void markAdmitted() {
        if (!this.admitted) {
            this.admitted = true;
            incrementAdmissionCounter();
        }
    }

    /**
     * Static counter for successfully admitted patients.
     */
    public static synchronized int getAdmissionCounter() {
        return admissionCounter;
    }

    /**
     * Increments the shared static admission counter.
     */
    public static synchronized void incrementAdmissionCounter() {
        admissionCounter++;
    }

    /**
     * Resets the static admission counter and id sequence (useful for testing).
     */
    public static synchronized void resetCounters() {
        admissionCounter = 0;
        idSequence = 0;
    }

    /**
     * Implementation of Treatable interface.
     */
    @Override
    public void assignDoctor(String doctorName) {
        if (doctorName == null || doctorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be null or empty.");
        }
        this.doctorName = doctorName.trim();
    }

    /**
     * Abstract method: Each subclass provides its own billing formula.
     *
     * @return calculated total bill
     */
    public abstract double calculateBill();

    /**
     * Meaningful display of common patient details.
     */
    public void displayDetails() {
        System.out.println("Patient ID       : " + id);
        System.out.println("Patient Name     : " + name);
        System.out.println("Patient Age      : " + age);
        System.out.println("Assigned Doctor  : " + (doctorName != null ? doctorName : "Not Assigned"));
        System.out.println("Admission Status : " + (admitted ? "Successfully Admitted" : "Pending / Not Admitted"));
    }

    @Override
    public String toString() {
        String doc = (doctorName == null) ? "Not assigned" : doctorName;
        return String.format("Patient[ID=%d, name='%s', age=%d, doctor='%s', admitted=%b]",
                id, name, age, doc, admitted);
    }
}
