package hospital;

/**
 * OutPatient: Represents a patient who visits the hospital without occupying a bed.
 *
 * Demonstrates:
 *   - Inheritance (extends Patient)
 *   - Method overriding (calculateBill, displayDetails)
 *   - Polymorphism
 *
 * Bill Formula:
 *   Total Bill = Consultation Charges + Treatment Charges + Diagnostic Charges
 */
public class OutPatient extends Patient {

    private double consultationCharges;
    private double treatmentCharges;
    private double diagnosticCharges;

    /**
     * Full constructor with explicit user-provided patient ID.
     */
    public OutPatient(int id, String name, int age, double consultationCharges,
                      double treatmentCharges, double diagnosticCharges) {
        super(id, name, age);
        validateCharges(consultationCharges, treatmentCharges, diagnosticCharges);
        this.consultationCharges = consultationCharges;
        this.treatmentCharges = treatmentCharges;
        this.diagnosticCharges = diagnosticCharges;
    }

    /**
     * Full constructor with auto-generated ID.
     */
    public OutPatient(String name, int age, double consultationCharges,
                      double treatmentCharges, double diagnosticCharges) {
        super(name, age);
        validateCharges(consultationCharges, treatmentCharges, diagnosticCharges);
        this.consultationCharges = consultationCharges;
        this.treatmentCharges = treatmentCharges;
        this.diagnosticCharges = diagnosticCharges;
    }

    /**
     * Backward-compatible 4-argument constructor.
     */
    public OutPatient(String name, int age, double consultationFee, double medicineCost) {
        this(name, age, consultationFee, medicineCost, 0.0);
    }

    private static void validateCharges(double consult, double treat, double diag) {
        if (consult < 0) {
            throw new IllegalArgumentException("Consultation charges cannot be negative: " + consult);
        }
        if (treat < 0) {
            throw new IllegalArgumentException("Treatment charges cannot be negative: " + treat);
        }
        if (diag < 0) {
            throw new IllegalArgumentException("Diagnostic charges cannot be negative: " + diag);
        }
    }

    // Getters
    public double getConsultationCharges() {
        return consultationCharges;
    }

    public double getConsultationFee() {
        return consultationCharges;
    }

    public double getTreatmentCharges() {
        return treatmentCharges;
    }

    public double getMedicineCost() {
        return treatmentCharges;
    }

    public double getDiagnosticCharges() {
        return diagnosticCharges;
    }

    /**
     * Outpatient bill calculation:
     * Total = Consultation Charges + Treatment Charges + Diagnostic Charges
     */
    @Override
    public double calculateBill() {
        return consultationCharges + treatmentCharges + diagnosticCharges;
    }

    @Override
    public void displayDetails() {
        System.out.println("========== OUT-PATIENT DETAILS =========");
        super.displayDetails();
        System.out.println("Patient Type     : Out-Patient (No Bed Required)");
        System.out.printf("Consultation Fee : $%.2f%n", consultationCharges);
        System.out.printf("Treatment Charges: $%.2f%n", treatmentCharges);
        System.out.printf("Diagnostic Fees  : $%.2f%n", diagnosticCharges);
        System.out.printf("Total Bill       : $%.2f%n", calculateBill());
        System.out.println("========================================");
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(", OutPatient[consultation=%.2f, treatment=%.2f, diagnostic=%.2f, bill=%.2f]",
                consultationCharges, treatmentCharges, diagnosticCharges, calculateBill());
    }
}
