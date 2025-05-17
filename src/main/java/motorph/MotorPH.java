// Import necessary Java libraries for file handling, date/time operations, and collections.
package motorph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MotorPH {

    public static void main(String[] args) {
        // Initialize Scanner for user input and system components
        Scanner scanner = new Scanner(System.in);
        FileHandler fileHandler = new FileHandler();
        PayrollCalculator payroll = new PayrollCalculator(fileHandler);

        printSectionHeader("MOTORPH PAYROLL SYSTEM");

        // Main loop for the system menu
        while (true) {
            System.out.println("MAIN MENU");
            System.out.println("1. Employee Management");
            System.out.println("2. Attendance Management");
            System.out.println("3. Payroll Calculation");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    employeeMenu(scanner, fileHandler); // Navigate to Employee Management
                    break;
                case "2":
                    attendanceMenu(scanner, fileHandler); // Navigate to Attendance Management
                    break;
                case "3":
                    payrollMenu(scanner, payroll, fileHandler); // Navigate to Payroll Calculation
                    break;
                case "0":
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Prints a formatted section header for UI consistency
     */
    private static void printSectionHeader(String title) {
        System.out.println("----------------------------------------------------");
        System.out.println("---------------- " + title + " ----------------");
        System.out.println("----------------------------------------------------");
    }

    /**
     * Prints a consistent footer line for section formatting
     */
    private static void printSectionFooter() {
        System.out.println("----------------------------------------------------");
    }

    /**
     * Displays and handles user interaction for Employee Management
     */
    private static void employeeMenu(Scanner scanner, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("EMPLOYEE MANAGEMENT");
            System.out.println("1. View All Employees");
            System.out.println("2. View Specific Employee");
            System.out.println("3. Add New Employee");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllEmployees(fileHandler);
                    break;
                case "2":
                    viewSpecificEmployee(scanner, fileHandler);
                    break;
                case "3":
                    addEmployee(scanner, fileHandler);
                    break;
                case "4":
                    updateEmployee(scanner, fileHandler);
                    break;
                case "5":
                    deleteEmployee(scanner, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays all employees in tabular format
     */
    private static void viewAllEmployees(FileHandler fileHandler) {
        List<Employee> employees = fileHandler.readEmployees();
        if (employees.isEmpty()) {
            printSectionHeader("EMPLOYEE LIST");
            System.out.println("No employees found.");
            printSectionFooter();
            return;
        }

        printSectionHeader("EMPLOYEE LIST");
        System.out.printf("%-8s %-20s %-20s %-30s %s\n",
                "ID", "Last Name", "First Name", "Position", "Basic Salary");

        for (Employee emp : employees) {
            System.out.printf("%-8s %-20s %-20s %-30s PHP %,.2f\n",
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getPosition(),
                    emp.getBasicSalary());
        }
        printSectionFooter();
    }

    /**
     * Displays detailed info about a specific employee by ID
     */
    private static void viewSpecificEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("VIEW EMPLOYEE DETAILS");
        System.out.print("Enter Employee ID: ");
        String id = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(id); // Corrected: Was findEmployee

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        System.out.println("\nEmployee Details:");
        System.out.printf("%-20s: %s\n", "Employee ID", employee.getEmployeeId());
        System.out.printf("%-20s: %s, %s\n", "Name", employee.getLastName(), employee.getFirstName());
        System.out.printf("%-20s: %s\n", "Birthday", formatDate(employee.getBirthday()));
        System.out.printf("%-20s: %s\n", "Address", employee.getAddress());
        System.out.printf("%-20s: %s\n", "Phone Number", employee.getPhoneNumber());
        System.out.printf("%-20s: %s\n", "Status", employee.getStatus());
        System.out.printf("%-20s: %s\n", "Position", employee.getPosition());
        System.out.printf("%-20s: %s\n", "Supervisor", employee.getSupervisor());
        System.out.printf("%-20s: PHP %,.2f\n", "Basic Salary", employee.getBasicSalary());
        System.out.printf("%-20s: PHP %,.2f\n", "Rice Subsidy", employee.getRiceSubsidy());
        System.out.printf("%-20s: PHP %,.2f\n", "Phone Allowance", employee.getPhoneAllowance());
        System.out.printf("%-20s: PHP %,.2f\n", "Clothing Allowance", employee.getClothingAllowance());
        System.out.printf("%-20s: PHP %,.2f\n", "Gross Rate", employee.getGrossRate()); // Assumes this is Gross Semi-monthly from CSV
        System.out.printf("%-20s: PHP %,.2f\n", "Hourly Rate", employee.getHourlyRate());

        printSectionFooter();
    }

    /**
     * Helper method to format LocalDate as string, returns "N/A" if null
     */
    private static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "N/A";
    }

    /**
     * Adds a new employee to the system using user input
     */
    private static void addEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("ADD NEW EMPLOYEE");
        Map<String, String> data = new HashMap<>();

        collectInput(scanner, data, "Employee ID", "EmployeeID");
        collectInput(scanner, data, "Last Name", "LastName");
        collectInput(scanner, data, "First Name", "FirstName");
        collectInput(scanner, data, "Birthday (MM/DD/YYYY)", "Birthday");
        collectInput(scanner, data, "Address", "Address");
        collectInput(scanner, data, "Phone Number", "PhoneNumber");
        collectInput(scanner, data, "SSS Number", "SSS");
        collectInput(scanner, data, "PhilHealth Number", "Philhealth");
        collectInput(scanner, data, "TIN Number", "TIN");
        collectInput(scanner, data, "Pag-IBIG Number", "Pagibig");
        collectInput(scanner, data, "Status", "Status");
        collectInput(scanner, data, "Position", "Position");
        collectInput(scanner, data, "Supervisor", "Supervisor");
        collectInput(scanner, data, "Basic Salary", "BasicSalary");
        collectInput(scanner, data, "Rice Subsidy", "RiceSubsidy");
        collectInput(scanner, data, "Phone Allowance", "PhoneAllowance");
        collectInput(scanner, data, "Clothing Allowance", "ClothingAllowance");
        collectInput(scanner, data, "Gross Semi-monthly Rate", "GrossRate"); 
        collectInput(scanner, data, "Hourly Rate", "HourlyRate");

        Employee employee = new Employee(data);
        fileHandler.saveEmployee(employee); 
        System.out.println("\nEmployee added successfully!");
        printSectionFooter();
    }

    /**
     * Utility method to collect user input and store it in a map
     */
    private static void collectInput(Scanner scanner, Map<String, String> data, String prompt, String key) {
        System.out.print(prompt + ": ");
        data.put(key, scanner.nextLine());
    }

    /**
     * Updates an existing employee’s details interactively
     */
    private static void updateEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("UPDATE EMPLOYEE");
        System.out.print("Enter Employee ID to update: ");
        String id = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(id); 

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        displayUpdateMenu(scanner, employee);
        fileHandler.saveEmployee(employee); // saveEmployee will handle updating the existing employee
        System.out.println("\nEmployee updated successfully!");
        printSectionFooter();
    }

    /**
     * Displays the interactive update menu for selected fields
     */
    private static void displayUpdateMenu(Scanner scanner, Employee employee) {
        while (true) {
            System.out.println("\nCurrent Employee Details:");
            System.out.println("1. Last Name: " + employee.getLastName());
            System.out.println("2. First Name: " + employee.getFirstName());
            System.out.println("3. Birthday: " + formatDate(employee.getBirthday()));
            System.out.println("4. Address: " + employee.getAddress());
            System.out.println("5. Phone Number: " + employee.getPhoneNumber());
            System.out.println("6. Basic Salary: PHP " + String.format("%,.2f", employee.getBasicSalary()));
            System.out.println("0. Save Changes");

            System.out.print("\nEnter field number to update (0 to save): ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) break;

            handleFieldUpdate(scanner, choice, employee);
        }
    }

    /**
     * Handles individual field updates based on user selection
     */
    private static void handleFieldUpdate(Scanner scanner, String choice, Employee employee) {
        System.out.print("Enter new value: "); // Prompt for new value for all choices
        switch (choice) {
            case "1": employee.setLastName(scanner.nextLine()); break;
            case "2": employee.setFirstName(scanner.nextLine()); break;
            case "3": employee.setBirthday(scanner.nextLine()); break; // Assumes MM/DD/YYYY format
            case "4": employee.setAddress(scanner.nextLine()); break;
            case "5": employee.setPhoneNumber(scanner.nextLine()); break;
            case "6":
                try {
                    employee.setBasicSalary(Double.parseDouble(scanner.nextLine()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid salary format. Please enter a number.");
                }
                break;
            default: System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Deletes an employee from the database
     */
    private static void deleteEmployee(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("DELETE EMPLOYEE");
        System.out.print("Enter Employee ID to delete: ");
        String id = scanner.nextLine();

        if (fileHandler.deleteEmployee(id)) {
            System.out.println("\nEmployee deleted successfully!");
        } else {
            System.out.println("\nEmployee not found!");
        }
        printSectionFooter();
    }

    /**
     * Displays and handles Attendance Management options
     */
    private static void attendanceMenu(Scanner scanner, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("ATTENDANCE MANAGEMENT");
            System.out.println("1. View Employee Attendance Records");
            System.out.println("2. Add Attendance Record"); // Added option
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewEmployeeAttendance(scanner, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays attendance records for a specific employee filtered by month and week
     */
    private static void viewEmployeeAttendance(Scanner scanner, FileHandler fileHandler) {
        printSectionHeader("VIEW EMPLOYEE ATTENDANCE");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(employeeId); // Corrected: Was findEmployee

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        List<Attendance> allRecords = fileHandler.getAllAttendanceRecords().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .sorted(Comparator.comparing(Attendance::getDate))
                .collect(Collectors.toList());

        if (allRecords.isEmpty()) {
            System.out.println("No attendance records found for this employee.");
            printSectionFooter();
            return;
        }

        displayAvailableMonths(allRecords);
        int monthChoice = Integer.parseInt(scanner.nextLine());

        List<Attendance> filteredRecords = filterBySelectedMonth(allRecords, monthChoice);
        if (filteredRecords == null) return;

        displayWeekOptions();
        int weekChoice = Integer.parseInt(scanner.nextLine());

        displayFilteredAttendance(filteredRecords, weekChoice, employee);
        printSectionFooter();
    }

    /**
     * Displays available months from attendance records
     */
    private static void displayAvailableMonths(List<Attendance> records) {
        List<YearMonth> availableMonths = records.stream()
                .map(r -> YearMonth.from(r.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i+1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }
        System.out.print("Select month: ");
    }

    /**
     * Filters attendance records by selected month index
     */
    private static List<Attendance> filterBySelectedMonth(List<Attendance> records, int choice) {
        List<YearMonth> availableMonths = records.stream()
                .map(r -> YearMonth.from(r.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (choice <= 0 || choice > availableMonths.size()) {
            System.out.println("Invalid month selection.");
            return null;
        }

        YearMonth selectedMonth = availableMonths.get(choice - 1);
        return records.stream()
                .filter(r -> YearMonth.from(r.getDate()).equals(selectedMonth))
                .collect(Collectors.toList());
    }

    /**
     * Displays week selection options
     */
    private static void displayWeekOptions() {
        System.out.println("\nWeek Options:");
        System.out.println("1. Week 1");
        System.out.println("2. Week 2");
        System.out.println("3. Week 3");
        System.out.println("4. Week 4");
        System.out.println("5. All Weeks");
        System.out.print("Select week (1-5): ");
    }

    /**
     * Displays attendance records filtered by selected week
     */
    private static void displayFilteredAttendance(List<Attendance> records, int weekChoice, Employee employee) {
        Map<Integer, List<Attendance>> weeklyRecords = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().get(WeekFields.ISO.weekOfMonth())
                ));

        System.out.println("ATTENDANCE RECORDS FOR " + employee.getLastName() + ", " + employee.getFirstName());

        if (weekChoice == 5) { // All weeks
            weeklyRecords.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> printWeekAttendance(entry.getKey(), entry.getValue()));
        } else if (weekChoice >= 1 && weekChoice <= 4) { // Specific week
            if (weeklyRecords.containsKey(weekChoice)) {
                printWeekAttendance(weekChoice, weeklyRecords.get(weekChoice));
            } else {
                System.out.println("No records found for week " + weekChoice);
            }
        } else {
            System.out.println("Invalid week selection.");
        }
    }

    /**
     * Prints attendance records for a specific week
     */
    private static void printWeekAttendance(int weekNumber, List<Attendance> records) {
        System.out.printf("\nWeek %d (%s to %s):\n",
                weekNumber,
                records.get(0).getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                records.get(records.size()-1).getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        System.out.printf("%-12s %-8s %-8s\n", "Date", "Time In", "Time Out");

        for (Attendance record : records) {
            System.out.printf("%-12s %-8s %-8s\n",
                    record.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    record.getTimeIn(),
                    record.getTimeOut());
        }
    }
    
    /**
     * Displays and handles the Payroll Calculation menu
     */
    private static void payrollMenu(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        while (true) {
            printSectionHeader("PAYROLL CALCULATION");
            System.out.println("1. Calculate for Specific Employee");
            System.out.println("2. Calculate for All Employees");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    calculateEmployeePayroll(scanner, payroll, fileHandler);
                    break;
                case "2":
                    calculateAllEmployeesPayroll(scanner, payroll, fileHandler);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Calculates payroll for a specific employee
     */
    private static void calculateEmployeePayroll(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        printSectionHeader("PAYROLL CALCULATION");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();
        Employee employee = fileHandler.getEmployeeById(employeeId); 

        if (employee == null) {
            System.out.println("Employee not found!");
            printSectionFooter();
            return;
        }

        List<YearMonth> availableMonths = payroll.getAvailableMonths(employeeId);
        if (availableMonths.isEmpty()) {
            System.out.println("No attendance records found for this employee.");
            printSectionFooter();
            return;
        }

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i+1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }
        System.out.print("Select month (number): ");
        int monthChoice = Integer.parseInt(scanner.nextLine()) - 1;
        YearMonth selectedMonth = availableMonths.get(monthChoice);

        System.out.println("\nWeek Options:");
        System.out.println("1. Week 1");
        System.out.println("2. Week 2");
        System.out.println("3. Week 3");
        System.out.println("4. Week 4");
        System.out.println("5. All Weeks");
        System.out.print("Select week (1-5): ");
        int weekChoice = Integer.parseInt(scanner.nextLine());

        payroll.calculateWeeklyPayroll(employeeId, selectedMonth, weekChoice == 5 ? 0 : weekChoice);
        printSectionFooter();
    }

    /**
     * Calculates payroll for all employees
     */
    private static void calculateAllEmployeesPayroll(Scanner scanner, PayrollCalculator payroll, FileHandler fileHandler) {
        printSectionHeader("PAYROLL CALCULATION FOR ALL EMPLOYEES");
        List<YearMonth> availableMonths = payroll.getAllAvailableMonths();

        if (availableMonths.isEmpty()) {
            System.out.println("No attendance records found.");
            printSectionFooter();
            return;
        }

        System.out.println("\nAvailable Months:");
        for (int i = 0; i < availableMonths.size(); i++) {
            System.out.printf("%d. %s%n", i+1, availableMonths.get(i).format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }
        System.out.print("Select month (number): ");
        int monthChoice = Integer.parseInt(scanner.nextLine()) - 1;
        YearMonth selectedMonth = availableMonths.get(monthChoice);

        System.out.println("\nWeek Options:");
        System.out.println("1. Week 1");
        System.out.println("2. Week 2");
        System.out.println("3. Week 3");
        System.out.println("4. Week 4");
        System.out.println("5. All Weeks");
        System.out.print("Select week (1-5): ");
        int weekChoice = Integer.parseInt(scanner.nextLine());

        payroll.calculateAllWeeklyPayroll(selectedMonth, weekChoice == 5 ? 0 : weekChoice);
        printSectionFooter();
    }
}
