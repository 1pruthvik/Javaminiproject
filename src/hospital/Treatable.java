package hospital;

/**
 * Treatable interface (requirement: Interface in package hospital).
 * Any class that implements this can have a doctor assigned to it.
 */
public interface Treatable {
    void assignDoctor(String doctorName);
}
