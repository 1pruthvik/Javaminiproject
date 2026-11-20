package hospital;

/**
 * BedManager handles the hospital's limited number of beds.
 *
 * The allocateBed() method is "synchronized" so that only ONE thread
 * can change the bed count at a time. This prevents two counters
 * (Emergency and General) from giving away the same bed.
 */
public class BedManager {

    private final int totalBeds;
    private int availableBeds;

    public BedManager(int totalBeds) {
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
    }

    /**
     * Allocate one bed. Synchronized so threads cannot race here.
     * @return the number of beds that remain after allocation
     * @throws BedNotAvailableException if no bed is free
     */
    public synchronized int allocateBed() throws BedNotAvailableException {
        if (availableBeds <= 0) {
            throw new BedNotAvailableException(
                    "No bed available. All " + totalBeds + " beds are currently occupied.");
        }
        availableBeds--;
        return availableBeds;
    }

    /** Give a bed back when a patient is discharged. */
    public synchronized void releaseBed() {
        if (availableBeds < totalBeds) {
            availableBeds++;
        }
    }

    public synchronized int getAvailableBeds() {
        return availableBeds;
    }

    public int getTotalBeds() {
        return totalBeds;
    }
}
