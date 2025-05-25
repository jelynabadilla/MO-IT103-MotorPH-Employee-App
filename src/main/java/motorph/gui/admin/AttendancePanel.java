package motorph.gui.admin;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import motorph.Attendance;
import motorph.FileHandler; 
import java.time.LocalDate;
import java.time.Month;
import javax.swing.JOptionPane;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Comparator; 
import java.util.Map;
import java.util.HashMap;
import motorph.gui.MainApplication;


public class AttendancePanel extends javax.swing.JPanel {
    private FileHandler fileHandler; 
    private DefaultTableModel tableModel; 
    private MainApplication mainApp; 


    public AttendancePanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        initComponents(); 
        fileHandler = new FileHandler(); 
        initializeTable(); 
        populateEmployeeFilter(); 
        populateMonthFilter(); 
        // Action listener to update week filter when month changes
        monthComboBox.addActionListener(e -> populateWeekFilter());
    }

    // Sets up the table columns
    private void initializeTable() {
        tableModel = new DefaultTableModel();
        jTable1.setModel(tableModel);

        // Add columns to the table model
        tableModel.addColumn("Employee ID");
        tableModel.addColumn("Date");
        tableModel.addColumn("Time In");
        tableModel.addColumn("Time Out");

        loadAttendanceData(); // Load all attendance data initially
    }

    // Loads all attendance data into the table
    private void loadAttendanceData() {
        try {
            List<Attendance> attendanceRecords = fileHandler.getAllAttendanceRecords();
            tableModel.setRowCount(0); 

            // Add each record to the table model
            for (Attendance record : attendanceRecords) {
                Object[] rowData = {
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            // Show error message if data loading fails
            JOptionPane.showMessageDialog(this, "Error loading attendance data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print error details
        }
    }

    // Fills the employee ID filter dropdown
    private void populateEmployeeFilter() {
        try {
            // Get all attendance records to find unique employee IDs
            List<Attendance> attendanceRecords = fileHandler.getAllAttendanceRecords();
            employeeIdComboBox.removeAllItems(); 
            employeeIdComboBox.addItem("All"); 

            // Collect unique employee IDs
            Set<String> uniqueEmployeeIds = new HashSet<>();
            for (Attendance record : attendanceRecords) {
                uniqueEmployeeIds.add(record.getEmployeeId());
            }

            // Convert unique IDs to a list and sort numerically
            List<String> sortedEmployeeIds = new ArrayList<>(uniqueEmployeeIds);
            Collections.sort(sortedEmployeeIds, new Comparator<String>() {
                @Override
                public int compare(String id1, String id2) {
                    try {
                        Integer intId1 = Integer.parseInt(id1);
                        Integer intId2 = Integer.parseInt(id2);
                        return intId1.compareTo(intId2);
                    } catch (NumberFormatException e) {
                        // If parsing fails, compare as strings
                        return id1.compareTo(id2);
                    }
                }
            });

            // Add sorted IDs to the dropdown
            for (String employeeId : sortedEmployeeIds) {
                employeeIdComboBox.addItem(employeeId);
            }

        } catch (Exception e) {
            // Show error message if populating filter fails
            JOptionPane.showMessageDialog(this, "Error populating employee filter: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    // Fills the month filter dropdown
    private void populateMonthFilter() {
        try {
            // Get all attendance records to find unique months
            List<Attendance> records = fileHandler.getAllAttendanceRecords();
            // Use a Set to get unique months from the dates
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
             // Show error message if populating filter fails
             JOptionPane.showMessageDialog(this, "Error loading months: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    // Fills the week filter dropdown based on the selected month
     private void populateWeekFilter() {
        Object selectedMonthItem = monthComboBox.getSelectedItem();
        weekComboBox.removeAllItems(); 
        weekComboBox.addItem("All Weeks"); 

        // If no month is selected or "All Months" is selected, no specific weeks are listed
        if (selectedMonthItem == null || "All Months".equals(selectedMonthItem.toString())) {
            return;
        }

        try {
            // Get the selected month
            Month selectedMonth = Month.valueOf(selectedMonthItem.toString().toUpperCase());
            // Get all attendance records
            List<Attendance> records = fileHandler.getAllAttendanceRecords();

            // Get all unique dates for the selected month and sort them
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
                if (currentWeek.size() >= 5) { // Group dates into 5 for weeks
                    weekCount++;
                    weekComboBox.addItem("Week " + weekCount);
                    currentWeek.clear(); // Start a new week
                }
            }

            // Add any remaining dates as the last week
            if (!currentWeek.isEmpty()) {
                weekCount++;
                weekComboBox.addItem("Week " + weekCount);
            }

        } catch (Exception e) {
            // Show error message if populating filter fails
            JOptionPane.showMessageDialog(this, "Error loading weeks: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    // Filters the attendance data displayed in the table based on selected criteria
    private void filterAttendanceData() {
        try {
            // Get selected values from filters
            String employeeId = employeeIdComboBox.getSelectedItem() != null ?
                employeeIdComboBox.getSelectedItem().toString() : "All";
            String monthStr = monthComboBox.getSelectedItem() != null ?
                monthComboBox.getSelectedItem().toString() : "All Months";
            String weekStr = weekComboBox.getSelectedItem() != null ?
                weekComboBox.getSelectedItem().toString() : "All Weeks";

            // Start with all records
            List<Attendance> allRecords = fileHandler.getAllAttendanceRecords();
            List<Attendance> filteredRecords = new ArrayList<>(allRecords);

            // Filter by Employee ID if "All" is not selected
            if (!"All".equals(employeeId)) {
                filteredRecords = filteredRecords.stream()
                    .filter(record -> record.getEmployeeId().equals(employeeId))
                    .collect(Collectors.toList());
            }

            // Filter by Month if "All Months" is not selected
            final Month selectedMonth;
            if (!"All Months".equals(monthStr)) {
                try {
                    selectedMonth = Month.valueOf(monthStr.toUpperCase());
                    filteredRecords = filteredRecords.stream()
                        .filter(record -> record.getDate().getMonth() == selectedMonth)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Invalid month selected",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            } else {
                selectedMonth = null; 
            }

            // Filter by Week if "All Weeks" is not selected and a month is selected
            if (selectedMonth != null && !"All Weeks".equals(weekStr)) {
                try {
                    // Parse the week number
                    final int weekNum = Integer.parseInt(weekStr.replace("Week ", ""));

                    // Get all dates for this employee in the selected month from the already filtered records
                    List<LocalDate> allDates = filteredRecords.stream()
                        .map(Attendance::getDate)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                    if (!allDates.isEmpty()) {
                        // Group dates into weeks (5 dates per week)
                        Map<Integer, List<LocalDate>> weekGroups = new HashMap<>();
                        int currentWeek = 1;
                        List<LocalDate> currentWeekDates = new ArrayList<>();

                        for (LocalDate date : allDates) {
                            currentWeekDates.add(date);
                            if (currentWeekDates.size() >= 5) { // Group dates into chunks of 5
                                weekGroups.put(currentWeek, new ArrayList<>(currentWeekDates));
                                currentWeek++;
                                currentWeekDates.clear();
                            }
                        }

                        // Add any remaining dates to the last week
                        if (!currentWeekDates.isEmpty()) {
                            weekGroups.put(currentWeek, currentWeekDates);
                        }

                        // Get the list of dates that belong to the selected week
                        List<LocalDate> weekDates = weekGroups.get(weekNum);
                        if (weekDates != null) {
                            // Filter records to include only those with dates in the selected week
                            filteredRecords = filteredRecords.stream()
                                .filter(record -> weekDates.contains(record.getDate()))
                                .collect(Collectors.toList());
                        } else {
                             filteredRecords.clear(); // No records for this week
                        }
                    } else {
                         filteredRecords.clear(); // No dates available for the month
                    }
                } catch (NumberFormatException e) {
                    // Show error for invalid week format
                    JOptionPane.showMessageDialog(this, "Invalid week format",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop filtering
                }
            }

            // Update the table with the filtered data
            tableModel.setRowCount(0); 
            for (Attendance record : filteredRecords) {
                tableModel.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
                });
            }

        } catch (Exception e) {
            // Show error message if filtering fails
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
        employeeIdComboBox = new javax.swing.JComboBox<>();
        filterButton = new javax.swing.JToggleButton();
        weekComboBox = new javax.swing.JComboBox<>();
        monthComboBox = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(239, 239, 239));
        setMaximumSize(null);

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

        employeeIdComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(filterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(employeeIdComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterButton)
                    .addComponent(monthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(weekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JPanel jPanel14;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> monthComboBox;
    private javax.swing.JComboBox<String> weekComboBox;
    // End of variables declaration//GEN-END:variables
}
