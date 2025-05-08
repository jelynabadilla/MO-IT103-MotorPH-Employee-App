package motorph;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class FileHandler {
    private static final String DATA_FOLDER = "data";
    private static final String EMPLOYEE_FILE = DATA_FOLDER + File.separator + "employees.csv";
    private static final String ATTENDANCE_FILE = DATA_FOLDER + File.separator + "attendance.csv";
    
    public static final String EMPLOYEE_HEADER = "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";
    public static final String ATTENDANCE_HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    public FileHandler() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
            ensureFileExists(EMPLOYEE_FILE, EMPLOYEE_HEADER);
            ensureFileExists(ATTENDANCE_FILE, ATTENDANCE_HEADER);
        } catch (IOException e) {
            System.err.println("Error creating data folder or files: " + e.getMessage());
        }
    }

    private void ensureFileExists(String filePath, String header) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(header);
            }
        }
    }

    public List<Employee> readEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length >= 19) {
                    Map<String, String> data = new HashMap<>();
                    data.put("EmployeeID", values[0]);
                    data.put("LastName", values[1]);
                    data.put("FirstName", values[2]);
                    data.put("Birthday", values[3]);
                    data.put("Address", values[4]);
                    data.put("PhoneNumber", values[5]);
                    data.put("SSS", values[6]);
                    data.put("Philhealth", values[7]);
                    data.put("TIN", values[8]);
                    data.put("Pagibig", values[9]);
                    data.put("Status", values[10]);
                    data.put("Position", values[11]);
                    data.put("Supervisor", values[12]);
                    data.put("BasicSalary", values[13]);
                    data.put("RiceSubsidy", values[14]);
                    data.put("PhoneAllowance", values[15]);
                    data.put("ClothingAllowance", values[16]);
                    data.put("GrossRate", values[17]);
                    data.put("HourlyRate", values[18]);
                    
                    employees.add(new Employee(data));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employees file: " + e.getMessage());
        }
        return employees;
    }

    public void saveAllEmployees(List<Employee> employees) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EMPLOYEE_FILE))) {
            writer.println(EMPLOYEE_HEADER);
            for (Employee emp : employees) {
                writer.println(emp.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error saving employee file: " + e.getMessage());
        }
    }

    public List<Attendance> getAllAttendanceRecords() {
        List<Attendance> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (values.length == 6) {
                    // Parse time with optional leading zero
                    LocalTime timeIn = LocalTime.parse(values[4].length() == 4 ? "0" + values[4] : values[4]);
                    LocalTime timeOut = LocalTime.parse(values[5].length() == 4 ? "0" + values[5] : values[5]);
                    
                    // Parse date in MM/dd/yyyy format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate date = LocalDate.parse(values[3], formatter);
                    
                    records.add(new Attendance(values[0], date, timeIn, timeOut));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance file: " + e.getMessage());
        }
        return records;
    }

    public List<Attendance> getAttendanceRecords(String employeeId, LocalDate startDate, LocalDate endDate) {
        return getAllAttendanceRecords().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId) &&
                            !r.getDate().isBefore(startDate) &&
                            !r.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(Attendance::getDate))
                .collect(Collectors.toList());
    }

    public void recordAttendance(String employeeId, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ATTENDANCE_FILE, true))) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            writer.printf("%s,%s,%s,%s,%s,%s%n", 
                employeeId, 
                "", // Last name placeholder
                "", // First name placeholder
                date.format(dateFormatter),
                timeIn,
                timeOut);
        } catch (IOException e) {
            System.err.println("Error recording attendance: " + e.getMessage());
        }
    }

    public Employee findEmployee(String id) {
        List<Employee> employees = readEmployees();
        return employees.stream()
                .filter(emp -> emp.getEmployeeId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public void saveEmployee(Employee employee) {
        List<Employee> employees = readEmployees();
        employees.removeIf(emp -> emp.getEmployeeId().equals(employee.getEmployeeId()));
        employees.add(employee);
        saveAllEmployees(employees);
    }
    
    public boolean deleteEmployee(String id) {
        List<Employee> employees = readEmployees();
        boolean removed = employees.removeIf(emp -> emp.getEmployeeId().equals(id));
        if (removed) {
            saveAllEmployees(employees);
        }
        return removed;
    }
    
    public Attendance findAttendanceRecord(String employeeId, LocalDate date) {
        List<Attendance> records = getAllAttendanceRecords();
        return records.stream()
                .filter(r -> r.getEmployeeId().equals(employeeId) && r.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }
    
    public String getAttendanceFilePath() {
        return ATTENDANCE_FILE;
    }
}