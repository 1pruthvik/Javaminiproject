# Hospital Patient Management(Java Mini Project)

A simple, well-structured Java project that demonstrates core Java concepts
for a hospital that admits patients and generates bills.

## Concepts covered

1. **Class + `this` + static counter** — `Patient` has `id`, `name`, `age`.
   The constructor uses `this` and a `static int admissionCounter` to
   auto-assign a unique ID to every patient.
2. **Inheritance (abstract class)** — `Patient` is `abstract`; `InPatient`
   and `OutPatient` extend it and each `@Override` `calculateBill()`.
3. **Interface + package** — `Treatable` interface (with `assignDoctor`)
   lives in the `hospital` package and is implemented by `Patient`.
4. **Custom Exception** — `BedNotAvailableException` is thrown when no bed
   is free, and handled with `try` / `catch` / `finally`.
5. **Threads + synchronization** — `AdmissionCounter` extends `Thread`.
   An `Emergency` counter and a `General` counter admit patients at the
   same time, sharing one `BedManager`. `BedManager.allocateBed()` is
   `synchronized` so the two threads never hand out the same bed.

## Project structure

```
Javaminiproject/
├── src/
│   └── hospital/
│       ├── Treatable.java              # interface (package hospital)
│       ├── BedNotAvailableException.java  # custom exception
│       ├── Patient.java               # abstract base class
│       ├── InPatient.java             # inherits Patient
│       ├── OutPatient.java            # inherits Patient
│       ├── BedManager.java            # synchronized bed allocation
│       ├── AdmissionCounter.java       # Thread subclass
│       └── HospitalMain.java           # main() entry point
└── README.md
```

## How to compile and run

You need the Java JDK (version 8 or newer).

**Linux / macOS:**
```bash
# From the project root (Javaminiproject/)
mkdir -p bin
javac -d bin src/hospital/*.java
java -cp bin hospital.HospitalMain
```

**Windows (Command Prompt):**
```cmd
mkdir bin
javac -d bin src\hospital\*.java
java -cp bin hospital.HospitalMain
```

Or simply use the provided helper script (Linux/macOS):
```bash
chmod +x run.sh
./run.sh
```

## Expected output (summary)

- Prints each admitted patient with their assigned doctor and calculated bill.
- Shows the static admission counter increasing.
- Demonstrates `BedNotAvailableException` being caught in a `try-catch-finally`.
- Runs the Emergency and General counters concurrently; because only 3 beds
  are shared, the later admission attempts fail with the custom exception,
  proving the `synchronized` bed allocation works across threads.
