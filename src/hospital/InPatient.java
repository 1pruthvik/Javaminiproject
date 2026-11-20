package hospital;

/**
 * InPatient extends Patient (requirement: Inheritance).
 * An in-patient stays in the hospital, so the bill is:
 *     daysAdmitted * dailyCharge
 */
public class InPatient extends Patient {

    private int daysAdmitted;
    private double dailyCharge;

    public InPatient(String name, int age, int daysAdmitted, double dailyCharge) {
        super(name, age);
        this.daysAdmitted = daysAdmitted;
        this.dailyCharge = dailyCharge;
    }

    public int getDaysAdmitted() { return daysAdmitted; }
    public double getDailyCharge() { return dailyCharge; }

    // Override the abstract method with in-patient billing rule.
    @Override
    public double calculateBill() {
        return daysAdmitted * dailyCharge;
    }

    @Override
    public String toString() {
        return super.toString()
                + ", daysAdmitted=" + daysAdmitted
                + ", calculatedBill=" + calculateBill();
    }
}
