package hospital;

/**
 * BedManager: Manages shared hospital bed allocation.
 *
 * Demonstrates:
 *   - Multithreading and Synchronization
 *   - Critical Section protection
 *   - Shared Resource Management
 *   - Bed slot occupant tracking
 *   - Throwing custom checked exception (BedNotAvailableException)
 *
 * Concurrency Guarantee:
 *   The allocateBed operation is fully synchronized and atomic:
 *   1. Checks bed availability
 *   2. Allocates a unique bed number
 *   3. Updates occupied and available bed counts
 *   4. Marks patient admission state and updates static counter
 *
 * Invariants maintained at all times:
 *   - occupiedBeds >= 0
 *   - availableBeds >= 0
 *   - occupiedBeds <= totalBeds
 *   - occupiedBeds + availableBeds == totalBeds
 */
public class BedManager {

    private final int totalBeds;
    private int availableBeds;
    private int occupiedBeds;
    private final boolean[] bedOccupancy;
    private final InPatient[] bedSlots;

    /**
     * Initializes the bed manager with a fixed capacity.
     *
     * @param totalBeds total number of beds in the hospital (must be > 0)
     */
    public BedManager(int totalBeds) {
        if (totalBeds <= 0) {
            throw new IllegalArgumentException("Total beds must be greater than zero: " + totalBeds);
        }
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
        this.occupiedBeds = 0;
        this.bedOccupancy = new boolean[totalBeds];
        this.bedSlots = new InPatient[totalBeds];
    }

    /**
     * Synchronized atomic bed allocation for an InPatient.
     * Protects the complete critical section:
     *   1. Checks availability
     *   2. Allocates specific bed
     *   3. Updates bed counts
     *   4. Updates patient and admission state
     *
     * @param patient the inpatient requesting admission
     * @return remaining available beds after allocation
     * @throws BedNotAvailableException if all beds are occupied
     */
    public synchronized int allocateBed(InPatient patient) throws BedNotAvailableException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null for bed allocation.");
        }

        // 1. Checking bed availability
        if (availableBeds <= 0) {
            throw new BedNotAvailableException(
                    "Bed allocation failed for patient '" + patient.getName() + "'. " +
                    "All " + totalBeds + " inpatient beds are currently occupied."
            );
        }

        // 2. Allocating a bed (find the first unoccupied bed slot)
        int assignedBed = -1;
        for (int i = 0; i < totalBeds; i++) {
            if (!bedOccupancy[i]) {
                bedOccupancy[i] = true;
                bedSlots[i] = patient;
                assignedBed = i + 1; // 1-based indexing for bed number
                break;
            }
        }

        patient.setBedNumber(assignedBed);

        // 3. Updating occupied and available bed counts
        occupiedBeds++;
        availableBeds--;

        // 4. Updating successful admission state
        patient.markAdmitted();

        return availableBeds;
    }

    /**
     * Overloaded synchronized bed allocation without explicit patient instance.
     * Maintained for backwards compatibility.
     *
     * @return remaining available beds
     * @throws BedNotAvailableException if no beds are available
     */
    public synchronized int allocateBed() throws BedNotAvailableException {
        if (availableBeds <= 0) {
            throw new BedNotAvailableException(
                    "No bed available. All " + totalBeds + " beds are currently occupied."
            );
        }

        for (int i = 0; i < totalBeds; i++) {
            if (!bedOccupancy[i]) {
                bedOccupancy[i] = true;
                break;
            }
        }

        occupiedBeds++;
        availableBeds--;
        Patient.incrementAdmissionCounter();
        return availableBeds;
    }

    /**
     * Releases a bed occupied by a specific InPatient upon discharge.
     *
     * @param patient the inpatient being discharged
     */
    public synchronized void releaseBed(InPatient patient) {
        if (patient != null && patient.getBedNumber() > 0 && patient.getBedNumber() <= totalBeds) {
            int slot = patient.getBedNumber() - 1;
            if (bedOccupancy[slot]) {
                bedOccupancy[slot] = false;
                bedSlots[slot] = null;
                patient.setBedNumber(0);
                occupiedBeds--;
                availableBeds++;
            }
        }
    }

    /**
     * Releases an unspecified bed (for backwards compatibility).
     */
    public synchronized void releaseBed() {
        if (occupiedBeds > 0) {
            for (int i = totalBeds - 1; i >= 0; i--) {
                if (bedOccupancy[i]) {
                    bedOccupancy[i] = false;
                    bedSlots[i] = null;
                    break;
                }
            }
            occupiedBeds--;
            availableBeds++;
        }
    }

    /**
     * Returns the patient occupying a specific bed (1-based bed number).
     */
    public synchronized InPatient getBedOccupant(int bedNumber) {
        if (bedNumber >= 1 && bedNumber <= totalBeds) {
            return bedSlots[bedNumber - 1];
        }
        return null;
    }

    /**
     * Checks if a specific bed is occupied (1-based bed number).
     */
    public synchronized boolean isBedOccupied(int bedNumber) {
        if (bedNumber >= 1 && bedNumber <= totalBeds) {
            return bedOccupancy[bedNumber - 1];
        }
        return false;
    }

    // Synchronized getters for shared resource state
    public synchronized int getAvailableBeds() {
        return availableBeds;
    }

    public synchronized int getOccupiedBeds() {
        return occupiedBeds;
    }

    public int getTotalBeds() {
        return totalBeds;
    }
}
