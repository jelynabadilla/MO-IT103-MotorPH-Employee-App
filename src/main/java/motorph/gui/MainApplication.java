package motorph.gui;

import java.awt.CardLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainApplication extends javax.swing.JFrame {

    private JPanel dashboardPanel;
    private JPanel employeesPanel;
    private JPanel payrollPanel;
    private JPanel attendancePanel;
    private CardLayout cardLayout;

    private PayrollFrame payrollFrame;

    public MainApplication() {
        initComponents();
        initializePanels();
        navigationPanel1.setMainApp(this);
        setSize(1000, 600);
    }

    private void initializePanels() {
        // Initialize CardLayout
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Create and add panels
        dashboardPanel = new DashboardPanel();
        employeesPanel = new EmployeesPanel();

        // Create a wrapper panel for PayrollFrame
        payrollPanel = new JPanel();
        payrollPanel.setLayout(new java.awt.BorderLayout());

        // Create PayrollFrame and configure it
        payrollFrame = new PayrollFrame();
        payrollFrame.setVisible(false); //Don't show it as a separate window

        // Get the content pane from PayrollFrame and add it to the wrapper panel
        payrollPanel.add(payrollFrame.getContentPane(), java.awt.BorderLayout.CENTER);

        // Prevent PayrollFrame from closing the entire application when used as a component
        payrollFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        attendancePanel = new AttendancePanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(employeesPanel, "employees");
        contentPanel.add(payrollPanel, "payroll");
        contentPanel.add(attendancePanel, "attendance");

        // Show dashboard by default
        cardLayout.show(contentPanel, "dashboard");
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navigationPanel1 = new motorph.gui.NavigationPanel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navigationPanel1.setMinimumSize(new java.awt.Dimension(160, 200));
        getContentPane().add(navigationPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 600));

        contentPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 840, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainApplication().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private motorph.gui.NavigationPanel navigationPanel1;
    // End of variables declaration//GEN-END:variables
}
