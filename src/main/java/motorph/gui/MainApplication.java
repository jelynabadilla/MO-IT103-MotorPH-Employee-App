package motorph.gui;

import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;
import motorph.gui.admin.DashboardPanel;
import motorph.gui.admin.AttendancePanel;
import motorph.gui.admin.EmployeesPanel;
import motorph.gui.employee.EmpDashboardPanel;
import motorph.gui.employee.EmpEmployeesPanel;
import motorph.gui.employee.EmpAttendancePanel;
import motorph.gui.admin.PayrollFrame;
import motorph.gui.employee.EmpPayrollFrame;


public class MainApplication extends javax.swing.JFrame {
    // Panels for different sections
    private JPanel dashboardPanel;
    private JPanel employeesPanel;
    private JPanel attendancePanel;
    private JPanel payrollPanel;

    private CardLayout cardLayout; // Layout to switch panels
    private boolean isAdmin; // True if admin, false if employee
    private String employeeNumber; // Employee ID

    // Names for panels in CardLayout
    public static final String ADMIN_DASHBOARD = "dashboard";
    public static final String ADMIN_EMPLOYEES = "employees";
    public static final String ADMIN_ATTENDANCE = "attendance";
    public static final String ADMIN_PAYROLL = "payroll";

    public static final String EMP_DASHBOARD = "emp_dashboard";
    public static final String EMP_EMPLOYEES = "emp_employees";
    public static final String EMP_ATTENDANCE = "emp_attendance";
    public static final String EMP_PAYROLL = "emp_payroll";

    // Constructor
    public MainApplication(String role, String employeeNumber) {
        this.isAdmin = "admin".equalsIgnoreCase(role);
        this.employeeNumber = employeeNumber;
        initComponents(); 
        initializePanels(); 
        navigationPanel1.setMainApp(this, isAdmin); 
        // Set window title
        setTitle("MotorPH - " + (isAdmin ? "Admin" : "Employee"));
    }

    // Constructor for Admin role (no employee number)
    public MainApplication(String role) {
        this(role, null);
    }

    // Get the content panel
    public JPanel getContentPanel() {
        return contentPanel;
    }

    // Set up panels based on role
    private void initializePanels() {
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Create panels
        if (isAdmin) {
            dashboardPanel = new DashboardPanel();
            employeesPanel = new EmployeesPanel(contentPanel, cardLayout, this);
            attendancePanel = new AttendancePanel(this);

            // Use content pane of PayrollFrame as a panel
            Container payrollContainer = new PayrollFrame().getContentPane();
            if (payrollContainer instanceof JPanel) {
                 payrollPanel = (JPanel) payrollContainer;
            } else {
                 System.err.println("Error: PayrollFrame content pane is not a JPanel.");
                 payrollPanel = new JPanel();
                 payrollPanel.add(payrollContainer);
            }

        } else { // Employee role
            // Pass employee number to panels
            dashboardPanel = new EmpDashboardPanel(this);
            employeesPanel = new EmpEmployeesPanel(employeeNumber);
            attendancePanel = new EmpAttendancePanel(employeeNumber);

            // Use content pane of EmpPayrollFrame as a panel
            Container empPayrollContainer = new EmpPayrollFrame(employeeNumber).getContentPane();
            if (empPayrollContainer instanceof JPanel) {
                 payrollPanel = (JPanel) empPayrollContainer;
            } else {
                 System.err.println("Error: EmpPayrollFrame content pane is not a JPanel.");
                 payrollPanel = new JPanel();
                 payrollPanel.add(empPayrollContainer);
            }
        }

        // Add panels to CardLayout
        contentPanel.add(dashboardPanel, isAdmin ? ADMIN_DASHBOARD : EMP_DASHBOARD);
        contentPanel.add(employeesPanel, isAdmin ? ADMIN_EMPLOYEES : EMP_EMPLOYEES);
        contentPanel.add(attendancePanel, isAdmin ? ADMIN_ATTENDANCE : EMP_ATTENDANCE);
        contentPanel.add(payrollPanel, isAdmin ? ADMIN_PAYROLL : EMP_PAYROLL);

        // Show default panel
        cardLayout.show(contentPanel, isAdmin ? ADMIN_DASHBOARD : EMP_DASHBOARD);
    }

    // Show a specific panel by name
    public void showPanel(String panelName) {
        if (cardLayout != null && contentPanel != null) {
             cardLayout.show(contentPanel, panelName);
        } else {
             System.err.println("Error: cardLayout or contentPanel is null.");
        }
    }

    // Check if user is admin
    public boolean isAdmin() {
        return isAdmin;
    }

    // Get employee number
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navigationPanel1 = new motorph.gui.NavigationPanel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navigationPanel1.setMinimumSize(new java.awt.Dimension(160, 200));
        getContentPane().add(navigationPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 600));

        contentPanel.setBackground(new java.awt.Color(237, 237, 237));
        contentPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        contentPanel.setPreferredSize(new java.awt.Dimension(840, 600));
        contentPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 840, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // display the form
        java.awt.EventQueue.invokeLater(() -> {
            new MainApplication("admin").setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private motorph.gui.NavigationPanel navigationPanel1;
    // End of variables declaration//GEN-END:variables
}
