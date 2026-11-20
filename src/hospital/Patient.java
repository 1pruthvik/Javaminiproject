package hospital;

/**
 * Patient (requirement: Class with ID, name, age + "this" + static admission counter).
 *
 * This class is declared abstract so that it cannot be created directly.
 * InPatient and OutPatient extend it and provide their own calculateBill().
 *
 * It implements Treatable so every patient can be assigned a doctor.
 */
public abstract class Patient implements Treatable {

    // Static admission counter: shared by ALL patients, counts total admissions.
    private static int admissionCounter = 0;

    private int id;
    private String name;
    private int age;
    private String doctorName;

    public Patient(String name, int age) {
        this.name = name;          // "this" refers to the current object's field
        this.age = age;            // (requirement: usage of "this")
        this.id = ++admissionCounter; // each new patient gets the next ID
        this.doctorName = null;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDoctorName() { return doctorName; }

    // Treatable interface method
    @Override
    public void assignDoctor(String doctorName) {
        this.doctorName = doctorName;
    }

    // Static getter for the shared counter
    public static int getAdmissionCounter() {
        return admissionCounter;
    }

    // Abstract method: every subclass must provide its own billing rule.
    public abstract double calculateBill();

    @Override
    public String toString() {
        String doc = (doctorName == null) ? "Not assigned" : doctorName;
        return "Patient[ID=" + id + ", name=" + name + ", age=" + age + ", doctor=" + doc + "]";
    }
}
