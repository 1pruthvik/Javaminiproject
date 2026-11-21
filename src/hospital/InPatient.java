package hospital;

/**
 * InPatient: Represents a patient admitted to a hospital bed.
 *
 * Demonstrates:
 *   - Inheritance (extends Patient)
 *   - Constructor chaining with super()
 *   - Method overriding (calculateBill, displayDetails)
 *   - Polymorphism
 *
 * Bill Formula:
 *   Total Bill = (Number of Days Admitted * Daily Room Charges) + Doctor Charges + Treatment Charges
 */
public class InPatient extends Patient {

    private int daysAdmitted;
    private double dailyRoomCharges;
    private double doctorCharges;
    private double treatmentCharges;
    private int bedNumber = 0; // 0 indicates bed not yet assigned

    /**
     * Full constructor for InPatient with all billing components.
     *
     * @param name             patient name
     * @param age              patient age
     * @param daysAdmitted     number of days in hospital (non-negative)
     * @param dailyRoomCharges daily room charge rate (non-negative)
     * @param doctorCharges    consultation charges (non-negative)
     * @param treatmentCharges medical treatment charges (non-negative)
     */
    public InPatient(String name, int age, int daysAdmitted, double dailyRoomCharges,
                     double doctorCharges, double treatmentCharges) {
        super(name, age);

        if (daysAdmitted < 0) {
            throw new IllegalArgumentException("Days admitted cannot be negative: " + daysAdmitted);
        }
        if (dailyRoomCharges < 0) {
            throw new IllegalArgumentException("Daily room charges cannot be negative: " + dailyRoomCharges);
        }
        if (doctorCharges < 0) {
            throw new IllegalArgumentException("Doctor consultation charges cannot be negative: " + doctorCharges);
        }
        if (treatmentCharges < 0) {
            throw new IllegalArgumentException("Treatment charges cannot be negative: " + treatmentCharges);
        }

        this.daysAdmitted = daysAdmitted;
        this.dailyRoomCharges = dailyRoomCharges;
        this.doctorCharges = doctorCharges;
        this.treatmentCharges = treatmentCharges;
    }

    /**
     * Backward-compatible 4-argument constructor.
     */
    public InPatient(String name, int age, int daysAdmitted, double dailyCharge) {
        this(name, age, daysAdmitted, dailyCharge, 0.0, 0.0);
    }

    // Getters and Setters
    public int getDaysAdmitted() {
        return daysAdmitted;
    }

    public double getDailyRoomCharges() {
        return dailyRoomCharges;
    }

    public double getDailyCharge() {
        return dailyRoomCharges;
    }

    public double getDoctorCharges() {
        return doctorCharges;
    }

    public double getTreatmentCharges() {
        return treatmentCharges;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(int bedNumber) {
        this.bedNumber = bedNumber;
    }

    /**
     * Inpatient bill calculation:
     * Total = (daysAdmitted * dailyRoomCharges) + doctorCharges + treatmentCharges
     */
    @Override
    public double calculateBill() {
        return (daysAdmitted * dailyRoomCharges) + doctorCharges + treatmentCharges;
    }

    @Override
    public void displayDetails() {
        System.out.println("========== IN-PATIENT DETAILS ==========");
        super.displayDetails();
        System.out.println("Patient Type     : In-Patient (Occupies Bed)");
        System.out.println("Assigned Bed     : " + (bedNumber > 0 ? "Bed #" + bedNumber : "No bed allocated"));
        System.out.println("Days Admitted    : " + daysAdmitted);
        System.out.printf("Daily Room Rate  : $%.2f%n", dailyRoomCharges);
        System.out.printf("Doctor Charges   : $%.2f%n", doctorCharges);
        System.out.printf("Treatment Charges: $%.2f%n", treatmentCharges);
        System.out.printf("Total Bill       : $%.2f%n", calculateBill());
        System.out.println("========================================");
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(", InPatient[bed=%d, days=%d, dailyRate=%.2f, docFee=%.2f, treatFee=%.2f, bill=%.2f]",
                bedNumber, daysAdmitted, dailyRoomCharges, doctorCharges, treatmentCharges, calculateBill());
    }
}
