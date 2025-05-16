package motorph.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import motorph.PayrollCalculator;
import motorph.FileHandler;
import motorph.Employee;
import java.util.ArrayList;

public class PayrollFrame extends JFrame {

    private PayrollCalculator payrollCalculator;
    private FileHandler fileHandler;
    private Employee currentEmployee;
    private List<Employee> employeeList;

    public PayrollFrame() {
        initComponents();
        setBackground(new java.awt.Color(243, 243, 224));
        jPanel2.setBackground(new java.awt.Color(243, 243, 224));
        fileHandler = new FileHandler();
        payrollCalculator = new PayrollCalculator(fileHandler);

        System.out.println("PayrollFrame constructor started.");

        try {
            employeeList = fileHandler.readEmployees();
            if (employeeList == null || employeeList.isEmpty()) {
                System.err.println("Employee list is empty or null after loading.");
                employeeList = new ArrayList<>(); 
            } else {
                System.out.println("Successfully loaded " + employeeList.size() + " employees. Size: " + employeeList.size());
            }
            populateEmployeeComboBox();
            populateMonthComboBox();
            populateWeekComboBox();
            System.out.println("ComboBoxes populated.");
        } catch (Exception e) {
            System.err.println("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }

        employeeDetailsTextArea.setText("Select an employee and click Calculate to view details.");
        resultTextArea.setText("Payroll results will appear here.");
        
        setupComputeButton();
        System.out.println("PayrollFrame constructor finished.");
    }

    private void populateEmployeeComboBox() {
        System.out.println("populateEmployeeComboBox() called.");
        employeeComboBox.removeAllItems();
        if (employeeList != null && !employeeList.isEmpty()) {
            for (Employee employee : employeeList) {
                employeeComboBox.addItem(employee.getEmployeeId() + " - " + employee.getLastName() + ", " + employee.getFirstName());
            }
            System.out.println(employeeComboBox.getItemCount() + " employees added to combo box.");
        } else {
            employeeComboBox.addItem("No employees found");
            System.out.println("No employees found added to combo box.");
        }
    }

    private void populateMonthComboBox() {
        System.out.println("populateMonthComboBox() called.");
        monthComboBox.removeAllItems();
        List<YearMonth> months = payrollCalculator.getAllAvailableMonths();
        if (months != null && !months.isEmpty()) {
            for (YearMonth month : months) {
                monthComboBox.addItem(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            }
            System.out.println(monthComboBox.getItemCount() + " months added to combo box.");
        } else {
            monthComboBox.addItem("No months available");
            System.out.println("No months available added to combo box.");
        }
    }

    private void populateWeekComboBox() {
        System.out.println("populateWeekComboBox() called.");
        weekComboBox.removeAllItems();
        weekComboBox.addItem("All Weeks");
        weekComboBox.addItem("Week 1");
        weekComboBox.addItem("Week 2");
        weekComboBox.addItem("Week 3");
        weekComboBox.addItem("Week 4");
        System.out.println("Week options added to combo box.");
    }

    private void setupComputeButton() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndDisplayPayroll();
            }
        });
    }

    private void calculateAndDisplayPayroll() {
        System.out.println("calculateAndDisplayPayroll() called.");
        if (employeeComboBox.getSelectedItem() == null || monthComboBox.getSelectedItem() == null) {
            resultTextArea.setText("Please select an employee and a month.");
            System.out.println("Employee or month not selected.");
            return;
        }

        try {
            String selectedItemText = employeeComboBox.getSelectedItem().toString();
            if (selectedItemText.equals("No employees found")) {
                resultTextArea.setText("No valid employee selected.");
                employeeDetailsTextArea.setText("No employee details available.");
                return;
            }
            
            // Parse the employee ID from the combo box selection
            String selectedEmployeeId = selectedItemText.split(" - ")[0];
            System.out.println("Selected employee ID: " + selectedEmployeeId);
            
            // Parse the month from the combo box selection
            String selectedMonthString = monthComboBox.getSelectedItem().toString();
            System.out.println("Selected month string: " + selectedMonthString);
            
            // Parse the month string to YearMonth
            YearMonth selectedMonth = parseYearMonth(selectedMonthString);
            System.out.println("Parsed month: " + selectedMonth);
            
            // Find the employee by ID
            currentEmployee = findEmployee(selectedEmployeeId);
            if (currentEmployee == null) {
                resultTextArea.setText("Employee not found: " + selectedEmployeeId);
                System.out.println("Employee not found in list: " + selectedEmployeeId);
                return;
            }
            
            updateEmployeeDetails();

            // Get selected week
            int selectedWeek = weekComboBox.getSelectedIndex();
            System.out.println("Selected week index: " + selectedWeek);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            payrollCalculator.processPayroll(currentEmployee.getEmployeeId(), selectedMonth, selectedWeek);

            System.out.flush();
            System.setOut(old);

            String payrollResults = baos.toString();
            if (payrollResults == null || payrollResults.trim().isEmpty()) {
                resultTextArea.setText("No payroll data available for the selected criteria.");
            } else {
                resultTextArea.setText(payrollResults);
            }

        } catch (Exception ex) {
            resultTextArea.setText("Error processing payroll: " + ex.getMessage());
            System.err.println("Error in calculateAndDisplayPayroll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void updateEmployeeDetails() {
        if (currentEmployee != null) {
            String details = "Employee ID: " + currentEmployee.getEmployeeId() + "\n" +
                             "Name: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "\n" +
                             "Status: " + currentEmployee.getStatus() + "\n" +
                             "Position: " + currentEmployee.getPosition();
            employeeDetailsTextArea.setText(details);
            System.out.println("Employee details updated for: " + currentEmployee.getEmployeeId());
        } else {
            employeeDetailsTextArea.setText("Employee details not available.");
            System.out.println("No employee details to display (currentEmployee is null)");
        }
    }


    private YearMonth parseYearMonth(String monthString) {
        System.out.println("parseYearMonth() called with: " + monthString);
        if (monthString == null || monthString.isEmpty() || monthString.equals("No months available")) {
            System.err.println("Invalid month string: " + monthString);
            return YearMonth.now();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return YearMonth.parse(monthString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing month with standard format: " + e.getMessage());

            try {
                DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
                return YearMonth.parse(monthString, shortFormatter);
            } catch (DateTimeParseException e2) {
                System.err.println("Error parsing month with short format: " + e2.getMessage());

                try {
                    String[] parts = monthString.split(" ");
                    if (parts.length == 2) {
                        int year = Integer.parseInt(parts[1]);
                        int month = getMonthNumber(parts[0]);
                        if (month > 0 && month <= 12) {
                            return YearMonth.of(year, month);
                        }
                    }
                } catch (Exception e3) {
                    System.err.println("Error parsing month with manual extraction: " + e3.getMessage());
                }

                JOptionPane.showMessageDialog(this, 
                    "Invalid month format: " + monthString + 
                    "\nExpected format: 'Month Year' (e.g., January 2023)",
                    "Date Format Error", 
                    JOptionPane.ERROR_MESSAGE);
                return YearMonth.now();
            }
        }
    }


    private int getMonthNumber(String monthName) {
        monthName = monthName.toLowerCase().trim();
        switch (monthName) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
            default: return -1;
        }
    }

    private Employee findEmployee(String employeeId) {
        System.out.println("findEmployee() called with employeeId: " + employeeId);
        if (employeeList == null) {
            System.err.println("Employee list is null");
            return null;
        }
        
        for (Employee emp : employeeList) {
            if (emp != null && emp.getEmployeeId() != null && emp.getEmployeeId().equals(employeeId)) {
                System.out.println("Employee found: " + emp.getFirstName() + " " + emp.getLastName());
                return emp;
            }
        }
        System.out.println("Employee not found: " + employeeId);
        return null;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        payrollTitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        calculateButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        employeeComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();
        weekComboBox = new javax.swing.JComboBox<>();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        employeeDetailsTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(243, 243, 224));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        payrollTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        payrollTitle.setForeground(new java.awt.Color(24, 59, 78));
        payrollTitle.setText("Payroll");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Employee");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Month");

        calculateButton.setText("Calculate");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Week");

        employeeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        monthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        monthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthComboBoxActionPerformed(evt);
            }
        });

        weekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        weekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weekComboBoxActionPerformed(evt);
            }
        });

        jPanel15.setBackground(new java.awt.Color(139, 0, 0));
        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        resultTextArea.setEditable(false);
        resultTextArea.setColumns(20);
        resultTextArea.setRows(5);
        jScrollPane1.setViewportView(resultTextArea);

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Payroll Report");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Employee Details:");

        employeeDetailsTextArea.setEditable(false);
        employeeDetailsTextArea.setColumns(20);
        employeeDetailsTextArea.setRows(5);
        jScrollPane4.setViewportView(employeeDetailsTextArea);

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Employe Picture");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(employeeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(calculateButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payrollTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(employeeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calculateButton)
                        .addGap(37, 37, 37)
                        .addComponent(jLabel5)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(261, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        // TODO add your handling code here:
        calculateAndDisplayPayroll();
    }//GEN-LAST:event_calculateButtonActionPerformed

    private void monthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monthComboBoxActionPerformed

    private void weekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weekComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_weekComboBoxActionPerformed


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PayrollFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateButton;
    private javax.swing.JComboBox<String> employeeComboBox;
    private javax.swing.JTextArea employeeDetailsTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JLabel payrollTitle;
    private javax.swing.JTextArea resultTextArea;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
