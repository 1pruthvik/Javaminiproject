package hospital;

/**
 * OutPatient extends Patient (requirement: Inheritance).
 * An out-patient does not stay overnight, so the bill is:
 *     consultationFee + medicineCost
 */
public class OutPatient extends Patient {

    private double consultationFee;
    private double medicineCost;

    public OutPatient(String name, int age, double consultationFee, double medicineCost) {
        super(name, age);
        this.consultationFee = consultationFee;
        this.medicineCost = medicineCost;
    }

    public double getConsultationFee() { return consultationFee; }
    public double getMedicineCost() { return medicineCost; }

    // Override the abstract method with out-patient billing rule.
    @Override
    public double calculateBill() {
        return consultationFee + medicineCost;
    }

    @Override
    public String toString() {
        return super.toString()
                + ", consultationFee=" + consultationFee
                + ", medicineCost=" + medicineCost
                + ", calculatedBill=" + calculateBill();
    }
}
