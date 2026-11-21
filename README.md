# Hospital Patient Management System (P11)

A robust, enterprise-grade, fully interactive Java OOP and Multithreading demonstration project implementing a complete Hospital Patient Management System.

## Architecture and Core Concepts

1. **Classes and Objects**: Encapsulation of domain models (`Patient`, `InPatient`, `OutPatient`, `BedManager`, `AdmissionCounter`).
2. **Static Variables**: Thread-safe global `admissionCounter` tracking only successfully admitted patients, and `idSequence` for unique patient IDs.
3. **Abstract Classes & Methods**: `Patient` is an abstract base class defining common state and the contract `abstract double calculateBill()`.
4. **Inheritance**: `InPatient` and `OutPatient` extend `Patient`, reusing base properties and adding specialized attributes.
5. **Method Overriding & Polymorphism**: Both subclasses override `calculateBill()` with distinct formulas and override `displayDetails()`.
6. **Interfaces**: `Treatable` interface in package `hospital` enforces doctor assignment via `assignDoctor(String)`.
7. **Package Structure**: Clean package organization under `hospital`.
8. **User-defined Exceptions**: `BedNotAvailableException` thrown when bed capacity is exceeded.
9. **Exception Handling (`try-catch-finally`)**: Structured exception handling ensuring `finally` executes for every admission request.
10. **Multithreading**: Concurrent execution of `Emergency` and `General` admission threads (`AdmissionCounter extends Thread`).
11. **Synchronization**: Atomic critical section in `BedManager.allocateBed(InPatient)` protecting bed availability checks, allocation, bed count updates, and admission confirmation.
12. **Shared Resource Management**: Competing threads safely coordinate over a single shared `BedManager` without race conditions or capacity violations.
13. **Central Patient Registry**: Thread-safe `Map<Integer, Patient>` storing all registered inpatients and outpatients with unique IDs.
14. **User-Driven Console Input**: No hardcoded data. All hospital capacity, patient details, financial charges, and multithreading requests are entered dynamically at runtime through an interactive console menu.

## Billing Formulas

- **InPatient Bill**: `(Days Admitted × Daily Room Charges) + Doctor Charges + Treatment Charges`
- **OutPatient Bill**: `Consultation Charges + Treatment Charges + Diagnostic Charges`

*Note: Outpatients do not consume hospital inpatient beds.*

## Interactive Menu Options

```
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

## Project Structure

```
Java_Mini_project/
├── .gitignore
├── LICENSE
├── README.md
├── run.sh
└── src/
    └── hospital/
        ├── Treatable.java               # Interface
        ├── BedNotAvailableException.java # Custom checked exception
        ├── Patient.java                 # Abstract base class
        ├── InPatient.java               # InPatient subclass
        ├── OutPatient.java              # OutPatient subclass
        ├── BedManager.java              # Synchronized shared bed allocator
        ├── AdmissionCounter.java        # Thread subclass for concurrent admissions
        ├── HospitalMain.java            # Interactive Menu-Driven Main entry point
        └── HospitalSystemTest.java      # Comprehensive automated verification suite
```

## How to Compile and Run

### Windows (PowerShell / Command Prompt):
```powershell
# Compile all sources into bin directory
mkdir -Force bin
javac -d bin src\hospital\*.java

# Run the interactive menu-driven application
java -cp bin hospital.HospitalMain

# Run the automated verification test suite
java -cp bin hospital.HospitalSystemTest
```

### Linux / macOS:
```bash
# Compile and run
mkdir -p bin
javac -d bin src/hospital/*.java
java -cp bin hospital.HospitalMain

# Or use the helper script:
chmod +x run.sh
./run.sh
```

## Automated Test Suite Coverage

The project includes `HospitalSystemTest.java` validating:
- **TEST 1**: Patient creation, unique ID generation, and input validation (rejecting negative age, negative billing rates).
- **TEST 2**: Polymorphic dispatch and billing calculations for both `InPatient` and `OutPatient`.
- **TEST 3**: `Treatable` interface implementation and doctor assignment.
- **TEST 4**: Static counter accuracy — verifying failed admissions do NOT increment the admission counter.
- **TEST 5**: `BedNotAvailableException` throwing, catching, and `finally` block execution.
- **TEST 6 & 7**: Multithreaded concurrency, race condition prevention, and strict bed capacity invariants.
- **TEST 8**: Full end-to-end application workflow.
