package hospital;

/**
 * Custom checked exception (requirement: User-defined Exception).
 * Thrown when an inpatient admission is attempted without an available bed.
 */
public class BedNotAvailableException extends Exception {

    public BedNotAvailableException(String message) {
        super(message);
    }

    public BedNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
