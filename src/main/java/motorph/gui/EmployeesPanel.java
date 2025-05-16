package motorph.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import motorph.FileHandler;
import motorph.Employee;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.awt.Frame; 

public class EmployeesPanel extends javax.swing.JPanel {

    private FileHandler fileHandler;
    private JDialog addEmployeeDialog;
    private EmployeeDetailsFrame detailsFrame;
    private static final DateTimeFormatter MDY_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public EmployeesPanel() {
        initComponents();
        fileHandler = new FileHandler();
        displayEmployees();
        detailsFrame = new EmployeeDetailsFrame(); 
        setupTableSelectionListener();
        setupAddEmployeeButton(); 
    }

    private void displayEmployees() {
        List<Employee> employees = fileHandler.readEmployees();
        DefaultTableModel model = (DefaultTableModel) employeesPanelTable.getModel();
        model.setRowCount(0);


        String[] columnNames = {"Employee Number", "Last Name", "First Name", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
        model.setColumnIdentifiers(columnNames);

        for (Employee employee : employees) {
            Map<String, String> employeeData = employee.toMap(); 
            
            // Correct keys from Employee.toMap()
            Object[] row = {
                employeeData.get("Employee #"),        
                employeeData.get("Last Name"),         
                employeeData.get("First Name"),        
                employeeData.get("SSS #"),             
                employeeData.get("Philhealth #"),      
                employeeData.get("TIN #"),             
                employeeData.get("Pag-ibig #")         
            };
            
            // Ensure all row data elements are strings or handle nulls appropriately
            for (int i = 0; i < row.length; i++) {
                if (row[i] == null) {
                    row[i] = ""; // Replace null with empty string for display
                } else {
                    // Ensure it's a string
                    row[i] = String.valueOf(row[i]); 
                }
            }
            model.addRow(row);
        }
        viewEmployeeDetailsButton.setEnabled(false); 
    }


    private void setupTableSelectionListener() {
        employeesPanelTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Enable button only if a row is selected
                viewEmployeeDetailsButton.setEnabled(!employeesPanelTable.getSelectionModel().isSelectionEmpty());
            }
        });
    }


    private void viewEmployeeDetails() {
        int selectedRow = employeesPanelTable.getSelectedRow();
        if (selectedRow != -1) { 
            String employeeId = (String) employeesPanelTable.getValueAt(selectedRow, 0);
            Employee employee = fileHandler.getEmployeeById(employeeId); 

            if (employee != null) {
                detailsFrame.populateFields(employee); 
                detailsFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to view details.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void refreshEmployeeTable() {
        displayEmployees();
    }


    private void setupAddEmployeeButton() {
        addEmployeeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEmployeeButtonActionPerformed(evt);
            }
        });
    }
    
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel13 = new javax.swing.JPanel();
        employeesTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        addEmployeeButton = new javax.swing.JButton();
        employeeTable = new javax.swing.JScrollPane();
        employeesPanelTable = new javax.swing.JTable();
        viewEmployeeDetailsButton = new javax.swing.JButton();
        refreshEmployeeTable = new javax.swing.JButton();

<<<<<<< HEAD
        setBackground(new java.awt.Color(243, 243, 224));

        jPanel13.setBackground(new java.awt.Color(221, 168, 83));
=======
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel13.setBackground(new java.awt.Color(102, 204, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
>>>>>>> 7a23e20 (checking & fixing)

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        employeesTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        employeesTitle.setForeground(new java.awt.Color(24, 59, 78));
        employeesTitle.setText("Employees");

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("List of Employees");

        addEmployeeButton.setText("Add Employee");
        addEmployeeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEmployeeButtonActionPerformed(evt);
            }
        });

        employeesPanelTable.setModel(new javax.swing.table.DefaultTableModel(
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
        employeeTable.setViewportView(employeesPanelTable);

        viewEmployeeDetailsButton.setText("View Employee");
        viewEmployeeDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewEmployeeDetailsButtonActionPerformed(evt);
            }
        });

        refreshEmployeeTable.setText("Refresh");
        refreshEmployeeTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshEmployeeTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(addEmployeeButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(employeesTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addComponent(employeeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshEmployeeTable)
                        .addGap(18, 18, 18)
                        .addComponent(viewEmployeeDetailsButton)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(employeesTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
<<<<<<< HEAD
                        .addGap(0, 8, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(viewEmployeeDetailsButton)
                            .addComponent(refreshEmployeeTable))))
                .addGap(18, 18, 18)
                .addComponent(employeeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addEmployeeButton)
                .addGap(0, 55, Short.MAX_VALUE))
=======
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(viewEmployeeDetailsButton)
                            .addComponent(refreshEmployeeTable))))
                .addComponent(employeeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addEmployeeButton)
                .addGap(0, 53, Short.MAX_VALUE))
>>>>>>> 7a23e20 (checking & fixing)
        );
    }// </editor-fold>//GEN-END:initComponents



    private void addEmployeeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEmployeeButtonActionPerformed
        // TODO add your handling code here:
        Frame parentFrame = null;
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            parentFrame = (Frame) window;
        }

        // Create and show the dialog
        addEmployeeDialog = new EmployeeDialog(parentFrame, true, this);
        addEmployeeDialog.setVisible(true);
    }//GEN-LAST:event_addEmployeeButtonActionPerformed

    private void viewEmployeeDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewEmployeeDetailsButtonActionPerformed
        // TODO add your handling code here:
        viewEmployeeDetails();
    }//GEN-LAST:event_viewEmployeeDetailsButtonActionPerformed

    private void refreshEmployeeTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshEmployeeTableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_refreshEmployeeTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEmployeeButton;
    private javax.swing.JScrollPane employeeTable;
    private javax.swing.JTable employeesPanelTable;
    private javax.swing.JLabel employeesTitle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JButton refreshEmployeeTable;
    private javax.swing.JButton viewEmployeeDetailsButton;
    // End of variables declaration//GEN-END:variables
}
