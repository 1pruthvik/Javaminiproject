package hospital;

/**
 * Treatable interface (requirement: Interface in package hospital).
 * Any patient class implementing this interface can be assigned a doctor.
 */
public interface Treatable {
    void assignDoctor(String doctorName);
}
