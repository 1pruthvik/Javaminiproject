# Hospital Patient Management System (P11)

A robust, enterprise-grade, fully interactive Java OOP and Multithreading application implementing a complete Hospital Patient Management System.

The application is completely **user-driven and dynamic**: it does not rely on hardcoded patient records, fixed billing values, or static configurations. Every value (hospital name, bed capacity, patient data, financial charges, and multithreaded requests) is collected at runtime via an interactive console menu with bulletproof input validation.

---

## Architecture & Core Java Concepts

1. **Classes and Objects**: Encapsulated domain models (`Patient`, `InPatient`, `OutPatient`, `BedManager`, `AdmissionCounter`).
2. **Static Variables**: Thread-safe global `admissionCounter` tracking only successfully admitted patients, and `idSequence` for unique patient IDs.
3. **Abstract Classes & Methods**: `Patient` is an abstract base class defining common state and the contract `abstract double calculateBill()`.
4. **Inheritance**: `InPatient` and `OutPatient` extend `Patient`, reusing base properties and constructor via `super()`.
5. **Method Overriding & Polymorphism**: Both subclasses override `calculateBill()` with specialized formulas and override `displayDetails()`.
6. **Interfaces**: `Treatable` interface in package `hospital` enforces doctor assignment via `assignDoctor(String)`.
7. **Package Structure**: Clean package organization under `package hospital;`.
8. **User-defined Exceptions**: `BedNotAvailableException` thrown when bed capacity is exceeded.
9. **Exception Handling (`try-catch-finally`)**: Structured exception handling ensuring `finally` executes for every admission request regardless of success or failure.
10. **Multithreading**: Concurrent execution of `Emergency` and `General` admission threads (`AdmissionCounter extends Thread`).
11. **Synchronization**: Atomic critical section in `BedManager.allocateBed(InPatient)` protecting bed availability checks, slot allocation, bed count updates, and admission confirmation.
12. **Shared Resource Management**: Competing threads safely coordinate over a single shared `BedManager` without race conditions or capacity violations.
13. **Central Patient Registry**: Thread-safe `Map<Integer, Patient>` storing all registered inpatients and outpatients with unique IDs.
14. **User-Driven Console Input**: Interactive 12-option menu with robust input validation handling negative values, non-numeric inputs, and duplicate patient IDs.

---

## Billing Formulas

- **InPatient Bill**: `(Number of Days Admitted × Daily Room Charges) + Doctor Consultation Charges + Treatment Charges`
- **OutPatient Bill**: `Consultation Charges + Treatment Charges + Diagnostic Charges`

*Note: Outpatients do not consume hospital inpatient beds.*

---

## Interactive Menu Options

```text
========================================
       HOSPITAL PATIENT MANAGEMENT      
========================================
1.  Register Inpatient
2.  Register Outpatient
3.  Display All Patients
4.  Search Patient by ID
5.  Assign Doctor
6.  Calculate Patient Bill
7.  Display Hospital Statistics
8.  Display Bed Availability
9.  Start Emergency Admission Thread
10. Start General Admission Thread
11. Display Admission Counter
12. Exit
========================================
```

---

## Project Structure

```text
Java_Mini_project/
├── .gitignore
├── LICENSE
├── README.md
├── run.sh
└── src/
    └── hospital/
        ├── Treatable.java                # Interface for doctor assignment
        ├── BedNotAvailableException.java # Custom checked exception
        ├── Patient.java                  # Abstract base class
        ├── InPatient.java                # InPatient subclass (occupies beds)
        ├── OutPatient.java               # OutPatient subclass (no bed consumed)
        ├── BedManager.java               # Synchronized shared bed allocator
        ├── AdmissionCounter.java         # Thread subclass for concurrent admissions
        ├── HospitalMain.java             # Interactive menu-driven entry point
        └── HospitalSystemTest.java       # Automated verification test suite
```

---

## How to Compile and Run (Ordered Step-by-Step)

### Prerequisites
- Java JDK 8 or newer installed (`javac -version` and `java -version`).

---

### Step 1: Open Terminal in Project Root
Ensure your terminal (PowerShell, Command Prompt, or Bash) is open in the project root:
```text
Java_Mini_project/
```

---

### Step 2: Compile the Java Sources

#### Windows (PowerShell or Command Prompt):
```cmd
javac -d bin src\hospital\*.java
```

#### Linux / macOS:
```bash
mkdir -p bin
javac -d bin src/hospital/*.java
```

---

### Step 3: Run the Interactive Hospital Application

#### Windows (PowerShell or Command Prompt):
```cmd
java -cp bin hospital.HospitalMain
```

#### Linux / macOS:
```bash
java -cp bin hospital.HospitalMain
```

#### Quick One-Liner (Compile & Run Together):
- **Windows (PowerShell):**
  ```powershell
  javac -d bin src\hospital\*.java; java -cp bin hospital.HospitalMain
  ```
- **Windows (Command Prompt / CMD):**
  ```cmd
  javac -d bin src\hospital\*.java && java -cp bin hospital.HospitalMain
  ```
- **Linux / macOS:**
  ```bash
  javac -d bin src/hospital/*.java && java -cp bin hospital.HospitalMain
  ```
  *(Or execute `./run.sh`)*

---

### Step 4: Step-by-Step Interactive Workflow Walkthrough

When `HospitalMain` starts, follow these prompts:

1. **Hospital Setup**:
   - `Enter Hospital Name:` e.g., `City Care Hospital`
   - `Enter Total Number of Inpatient Beds:` e.g., `3`
2. **Main Menu Navigation (Enter choice 1 - 12)**:
   - **`1` (Register Inpatient):** Enter Patient ID, Name, Age, Admission Days, Daily Room Charges, Doctor Consultation Charges, and Treatment Charges. Allocates a bed slot (Bed #1, #2, etc.) if beds are available.
   - **`2` (Register Outpatient):** Enter Patient ID, Name, Age, Consultation Charges, Treatment Charges, and Diagnostic Charges. Confirms registration **without** consuming beds.
   - **`3` (Display All Patients):** Displays every registered patient, their category, assigned doctor, bed allocation, and computed bill.
   - **`4` (Search Patient by ID):** Prompts for an ID and retrieves the complete record.
   - **`5` (Assign Doctor):** Prompts for Patient ID and Doctor Name, updating the record via the `Treatable` interface.
   - **`6` (Calculate Patient Bill):** Displays an itemized financial bill statement with the exact formula breakdown and final payable amount.
   - **`7` (Display Hospital Statistics):** Displays capacity, occupied beds, available beds, active patient counts, bed invariant check (`Occupied + Available == Total`), and static admission counter.
   - **`8` (Display Bed Availability):** Shows real-time occupancy status for each individual bed slot (Bed #1 through Bed #N) along with the occupant's name and ID.
   - **`9` (Start Emergency Admission Thread):** Prompts for the number of requests and individual patient details, then launches the concurrent Emergency thread competing for shared beds.
   - **`10` (Start General Admission Thread):** Prompts for requests and launches the concurrent General thread competing for shared beds.
   - **`11` (Display Admission Counter):** Displays `Patient.getAdmissionCounter()` (reflects successful admissions only; failed admissions do not increment this).
   - **`12` (Exit):** Cleanly terminates the application.

---

### Step 5: Run Automated Verification Tests (Optional)

To execute the automated regression test suite testing all 8 requirements (24 assertion checks):

```cmd
java -cp bin hospital.HospitalSystemTest
```

#### Test Suite Verifications:
- **TEST 1**: Patient creation, unique ID sequencing, and validation (rejection of negative age, negative charges).
- **TEST 2**: Polymorphic dispatch and calculation formulas for `InPatient` and `OutPatient`.
- **TEST 3**: `Treatable` interface implementation and doctor assignment.
- **TEST 4**: Static counter accuracy — verifying failed admissions do NOT increment the admission counter.
- **TEST 5**: `BedNotAvailableException` throwing, catching, and `finally` block execution.
- **TEST 6 & 7**: Multithreaded concurrency, mutual exclusion, race condition prevention, and strict bed capacity invariants.
- **TEST 8**: Full end-to-end workflow integrity.
