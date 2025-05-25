package motorph.gui.employee;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import motorph.Attendance;
import motorph.FileHandler;
import java.time.LocalDate;
import java.time.Month;
import javax.swing.JOptionPane;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class EmpAttendancePanel extends javax.swing.JPanel {

    private FileHandler fileHandler;
    private DefaultTableModel tableModel; 
    private String loggedInEmployeeId; 

    // Constructor
    public EmpAttendancePanel(String employeeId) {
        this.loggedInEmployeeId = employeeId;
        initComponents();
        fileHandler = new FileHandler();
        initializeTable(); 

        // Hide elements not needed in the employee view
        employeeIdComboBox.setVisible(false);
        jLabelEmployeeFilter.setVisible(false);

        populateMonthFilter(); // Fill the month dropdown
        // Add listener to month dropdown to update week options
        monthComboBox.addActionListener(e -> populateWeekFilter());
        // Load and display attendance data for the logged in employee
        filterAttendanceData();
    }

    // Sets up the columns for the attendance table
    private void initializeTable() {
        tableModel = new DefaultTableModel();
        jTable1.setModel(tableModel); 

        // Add table columns
        tableModel.addColumn("Employee ID");
        tableModel.addColumn("Date");
        tableModel.addColumn("Time In");
        tableModel.addColumn("Time Out");

        // Initial data load is handled by filterAttendanceData()
    }

    // Populates the month dropdown with months having attendance data for the employee
    private void populateMonthFilter() {
        try {
            // Get attendance records only for the logged-in employee
            List<Attendance> records = fileHandler.getAllAttendanceRecords().stream()
                .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            // Collect unique months from the filtered records
            Set<Month> months = records.stream()
                                       .map(record -> record.getDate().getMonth())
                                       .collect(Collectors.toSet());

            monthComboBox.removeAllItems(); 
            monthComboBox.addItem("All Months"); 

            // Sort months chronologically and add to dropdown
            months.stream()
                  .sorted() 
                  .forEach(month -> monthComboBox.addItem(month.toString()));
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error loading months: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

     // Populates the week dropdown based on the selected month
     private void populateWeekFilter() {
        Object selectedMonthItem = monthComboBox.getSelectedItem();
        weekComboBox.removeAllItems(); 
        weekComboBox.addItem("All Weeks"); 

        // If "All Months" or no month is selected, no specific weeks are listed
        if (selectedMonthItem == null || "All Months".equals(selectedMonthItem.toString())) {
            return;
        }

        try {
            // Get the selected month
            Month selectedMonth = Month.valueOf(selectedMonthItem.toString().toUpperCase());
            // Get attendance records for the logged-in employee
            List<Attendance> records = fileHandler.getAllAttendanceRecords().stream()
                 .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            // Get unique dates for the selected month and employee, then sort
            List<LocalDate> dates = records.stream()
                .filter(record -> record.getDate().getMonth() == selectedMonth)
                .map(Attendance::getDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

            // Group dates into weeks
            int weekCount = 0;
            List<LocalDate> currentWeek = new ArrayList<>();

            for (LocalDate date : dates) {
                currentWeek.add(date);
                if (currentWeek.size() >= 5) {
                    weekCount++;
                    weekComboBox.addItem("Week " + weekCount);
                    currentWeek.clear();
                }
            }

            if (!currentWeek.isEmpty()) {
                weekCount++;
                weekComboBox.addItem("Week " + weekCount);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading weeks: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    // Filters and displays attendance data based on selected criteria
    private void filterAttendanceData() {
        try {
            // Get selected filter values
            String monthStr = monthComboBox.getSelectedItem() != null ?
                monthComboBox.getSelectedItem().toString() : "All Months";
            String weekStr = weekComboBox.getSelectedItem() != null ?
                weekComboBox.getSelectedItem().toString() : "All Weeks";

            // Start with all records for the logged-in employee
            List<Attendance> filteredRecords = fileHandler.getAllAttendanceRecords().stream()
                .filter(record -> record.getEmployeeId() != null && record.getEmployeeId().equals(this.loggedInEmployeeId))
                .collect(Collectors.toList());

            Month selectedMonth = null;
            if (!"All Months".equals(monthStr)) {
                try {
                    selectedMonth = Month.valueOf(monthStr.toUpperCase());
                    final Month finalSelectedMonth = selectedMonth; 
                    filteredRecords = filteredRecords.stream()
                        .filter(record -> record.getDate().getMonth() == finalSelectedMonth)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Invalid month selected",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Filter by Week if a specific week is selected and a month is chosen
            if (selectedMonth != null && !"All Weeks".equals(weekStr)) {
                try {
                    // Extract week number from the string
                    final int weekNum = Integer.parseInt(weekStr.replace("Week ", ""));

                    // Get all unique dates for this employee in the selected month and sort them
                    List<LocalDate> allDates = filteredRecords.stream()
                        .map(Attendance::getDate)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                    // Group dates into weeks (5 days per week)
                    Map<Integer, List<LocalDate>> weekGroups = new HashMap<>();
                    int currentWeek = 1;
                    List<LocalDate> currentWeekDates = new ArrayList<>();

                    for (LocalDate date : allDates) {
                        currentWeekDates.add(date);
                        if (currentWeekDates.size() >= 5) {
                            weekGroups.put(currentWeek, new ArrayList<>(currentWeekDates));
                            currentWeek++;
                            currentWeekDates.clear();
                        }
                    }

                    // Add any remaining dates to the last week group
                    if (!currentWeekDates.isEmpty()) {
                        weekGroups.put(currentWeek, currentWeekDates);
                    }

                    // Get the list of dates belonging to the selected week
                    List<LocalDate> weekDates = weekGroups.get(weekNum);
                    if (weekDates != null) {
                        // Filter the records to include only those within the selected week's dates
                        filteredRecords = filteredRecords.stream()
                            .filter(record -> weekDates.contains(record.getDate()))
                            .collect(Collectors.toList());
                    } else {
                         // If no dates found for the selected week, clear the results
                         filteredRecords.clear();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid week format",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update the table with the filtered records
            tableModel.setRowCount(0); // Clear table
            for (Attendance record : filteredRecords) {
                tableModel.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel14 = new javax.swing.JPanel();
        attendanceTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        filterButton = new javax.swing.JToggleButton();
        weekComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();
        employeeIdComboBox = new javax.swing.JComboBox<>();
        jLabelEmployeeFilter = new javax.swing.JLabel();

        setBackground(new java.awt.Color(239, 239, 239));
        setMaximumSize(null);
        setName(""); // NOI18N

        jPanel14.setBackground(new java.awt.Color(139, 0, 0));
        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        attendanceTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        attendanceTitle.setForeground(new java.awt.Color(24, 59, 78));
        attendanceTitle.setText("Attendance");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        filterButton.setText("Filter");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        weekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        weekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weekComboBoxActionPerformed(evt);
            }
        });

        monthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        employeeIdComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabelEmployeeFilter.setText("Label1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabelEmployeeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(200, 200, 200)
                                .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(filterButton)))))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterButton)
                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEmployeeFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        filterAttendanceData();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void weekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weekComboBoxActionPerformed

    }//GEN-LAST:event_weekComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attendanceTitle;
    private javax.swing.JComboBox<String> employeeIdComboBox;
    private javax.swing.JToggleButton filterButton;
    private javax.swing.JLabel jLabelEmployeeFilter;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
