package hospital;

/**
 * Custom checked exception (requirement: Custom Exception).
 * Thrown when there are no free beds left in the hospital.
 */
public class BedNotAvailableException extends Exception {
    public BedNotAvailableException(String message) {
        super(message);
    }
}
