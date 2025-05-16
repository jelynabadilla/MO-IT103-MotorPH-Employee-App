package motorph;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an employee in the MotorPH payroll system.
 * Contains personal details, employment information, and compensation data.
 */
public class Employee {
    // Personal Information
    private String employeeId;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String address;
    private String phoneNumber;

    // Government IDs
    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagibigNumber;

    // Employment Details
    private String status;
    private String position;
    private String supervisor;

    // Compensation
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossRate; // This corresponds to "Gross Semi-monthly Rate"
    private double hourlyRate;

    // Date formatters
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DMY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MDY_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Used for CSV "Birthday"

    /**
     * Default constructor used during object creation from UI or other dynamic sources
     */
    public Employee() {}

    /**
     * Constructs an Employee object using a map of string values.
     * Useful when creating employees from user input or CSV data.
     * The keys in the 'data' map MUST match the headers in FileHandler.EMPLOYEE_HEADER.
     */
    public Employee(Map<String, String> data) {
        this.employeeId = data.get("Employee #");
        this.lastName = data.get("Last Name");
        this.firstName = data.get("First Name");
        this.birthday = parseDate(data.get("Birthday")); // Expects "MM/dd/yyyy"

        String addressValue = data.get("Address");
        if (addressValue != null && addressValue.startsWith("\"") && addressValue.endsWith("\"") && addressValue.length() > 1) {
            addressValue = addressValue.substring(1, addressValue.length() - 1);
        }
        this.address = addressValue;

        this.phoneNumber = data.get("Phone Number");
        this.sssNumber = data.get("SSS #");
        this.philhealthNumber = data.get("Philhealth #");
        this.tinNumber = data.get("TIN #");
        this.pagibigNumber = data.get("Pag-ibig #");
        this.status = data.get("Status");

        String positionValue = data.get("Position");
        if (positionValue != null && positionValue.startsWith("\"") && positionValue.endsWith("\"") && positionValue.length() > 1) {
            positionValue = positionValue.substring(1, positionValue.length() - 1);
        }
        this.position = positionValue;

        String supervisorValue = data.get("Immediate Supervisor"); // Corrected key
        if (supervisorValue != null && supervisorValue.startsWith("\"") && supervisorValue.endsWith("\"") && supervisorValue.length() > 1) {
            supervisorValue = supervisorValue.substring(1, supervisorValue.length() - 1);
        }
        this.supervisor = supervisorValue;

        // Parse numeric fields safely
        this.basicSalary = parseFormattedDouble(data.get("Basic Salary"));
        this.riceSubsidy = parseFormattedDouble(data.get("Rice Subsidy"));
        this.phoneAllowance = parseFormattedDouble(data.get("Phone Allowance"));
        this.clothingAllowance = parseFormattedDouble(data.get("Clothing Allowance"));
        this.grossRate = parseFormattedDouble(data.get("Gross Semi-monthly Rate")); // Corrected key
        this.hourlyRate = parseFormattedDouble(data.get("Hourly Rate"));
    }

    private double parseFormattedDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            System.err.println("Attempted to parse a null or empty string to double. Returning 0.0.");
            return 0.0;
        }
        try {
            // Remove commas and any non-numeric characters except decimal point and potential negative sign at the start
            String cleanValue = value.replaceAll("[^\\d.-]", "");
            if (cleanValue.isEmpty()) {
                 System.err.println("Numeric value became empty after cleaning: " + value + ". Returning 0.0.");
                 return 0.0;
            }
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric value: '" + value + "'. Returning 0.0. Error: " + e.getMessage());
            return 0.0; // Default value if parsing fails
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            // Primary format expected from CSV
            return LocalDate.parse(dateStr, MDY_FORMATTER); // MM/dd/yyyy
        } catch (DateTimeParseException e1) {
            try {
                // Fallback to ISO_LOCAL_DATE
                return LocalDate.parse(dateStr, ISO_FORMATTER); // yyyy-MM-dd
            } catch (DateTimeParseException e2) {
                // System.err.println("Failed to parse date '" + dateStr + "' with yyyy-MM-dd: " + e2.getMessage());
                try {
                    // Fallback to DMY_FORMATTER
                    return LocalDate.parse(dateStr, DMY_FORMATTER); // dd/MM/yyyy
                } catch (DateTimeParseException e3) {
                    System.err.println("Unable to parse date: '" + dateStr +
                            "'. Expected formats: MM/dd/yyyy, yyyy-MM-dd, or dd/MM/yyyy. Error: " + e3.getMessage());
                    return null;
                }
            }
        }
    }


    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getBirthday() { return birthday; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagibigNumber() { return pagibigNumber; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getSupervisor() { return supervisor; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getGrossRate() { return grossRate; }
    public double getHourlyRate() { return hourlyRate; }


    // Setters
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    /**
     * Parses and sets birthday from a string input
     */
    public void setBirthday(String birthdayStr) { this.birthday = parseDate(birthdayStr); }

    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }
    public void setPhilhealthNumber(String philhealthNumber) { this.philhealthNumber = philhealthNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public void setPagibigNumber(String pagibigNumber) { this.pagibigNumber = pagibigNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setPosition(String position) { this.position = position; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }

    /**
     * Sets basic salary and updates hourly rate based on standard work schedule (22 days x 8 hours)
     */
    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
        this.hourlyRate = basicSalary / (22 * 8);
    }

    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setGrossRate(double grossRate) { this.grossRate = grossRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }


    /**
     * Converts the employee object into a Map representation.
     * Uses the CSV header names as keys for consistency with FileHandler.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Employee #", employeeId);
        map.put("Last Name", lastName);
        map.put("First Name", firstName);
        map.put("Birthday", birthday != null ? birthday.format(MDY_FORMATTER) : "");
        map.put("Address", address);
        map.put("Phone Number", phoneNumber);
        map.put("SSS #", sssNumber);
        map.put("Philhealth #", philhealthNumber);
        map.put("TIN #", tinNumber);
        map.put("Pag-ibig #", pagibigNumber);
        map.put("Status", status);
        map.put("Position", position);
        map.put("Immediate Supervisor", supervisor);
        map.put("Basic Salary", String.format("%.2f", basicSalary)); // Formatting to 2 decimal places
        map.put("Rice Subsidy", String.format("%.2f", riceSubsidy));
        map.put("Phone Allowance", String.format("%.2f", phoneAllowance));
        map.put("Clothing Allowance", String.format("%.2f", clothingAllowance));
        map.put("Gross Semi-monthly Rate", String.format("%.2f", grossRate));
        map.put("Hourly Rate", String.format("%.2f", hourlyRate));
        return map;
    }

    /**
     * Converts the employee object into a CSV-formatted string.
     * This method is useful if you were to manually construct CSV lines,
     * but FileHandler.saveAllEmployees uses direct field access.
     */
    public String toCSV() {
        String formattedBirthday = birthday != null ? birthday.format(MDY_FORMATTER) : "";
        return String.join(",",
                safeGet(employeeId),
                safeGet(lastName),
                safeGet(firstName),
                formattedBirthday,
                safeGet(address), 
                safeGet(phoneNumber),
                safeGet(sssNumber),
                safeGet(philhealthNumber),
                safeGet(tinNumber),
                safeGet(pagibigNumber),
                safeGet(status),
                safeGet(position), 
                safeGet(supervisor), 
                String.format("%.2f", basicSalary),
                String.format("%.2f", riceSubsidy),
                String.format("%.2f", phoneAllowance),
                String.format("%.2f", clothingAllowance),
                String.format("%.2f", grossRate),
                String.format("%.2f", hourlyRate)
        );
    }

    private String safeGet(String s) {
        return s == null ? "" : s;
    }
}