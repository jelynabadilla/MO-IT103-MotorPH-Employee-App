package motorph.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationPanel extends javax.swing.JPanel {
    private MainApplication mainApp;

    public NavigationPanel() {
        initComponents(); 
    }

    public void setMainApp(MainApplication mainApp) {
        this.mainApp = mainApp;
        setupButtonActions();
    }

    private void setupButtonActions() {
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainApp != null) mainApp.showPanel("dashboard");
            }
        });
        
        employeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainApp != null) mainApp.showPanel("employees");
            }
        });
        
         payrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("payrollButton clicked."); 
                if (mainApp != null) {
                    System.out.println("mainApp is: " + mainApp); 
                    mainApp.showPanel("payroll");
                } else {
                    System.err.println("Error: mainApp is null in payrollButton action."); 
                }
            }
        });
            
        attendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainApp != null) mainApp.showPanel("attendance");
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        dashboardButton = new javax.swing.JButton();
        employeesButton = new javax.swing.JButton();
        payrollButton = new javax.swing.JButton();
        attendanceButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(160, 200));

        jPanel1.setBackground(new java.awt.Color(139, 0, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardButton.setBackground(new java.awt.Color(255, 255, 255));
        dashboardButton.setForeground(new java.awt.Color(0, 0, 0));
        dashboardButton.setText("Dashboard");
        jPanel1.add(dashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 140, -1));

        employeesButton.setBackground(new java.awt.Color(255, 255, 255));
        employeesButton.setForeground(new java.awt.Color(0, 0, 0));
        employeesButton.setText("Employees");
        employeesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeesButtonActionPerformed(evt);
            }
        });
        jPanel1.add(employeesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 140, -1));

        payrollButton.setBackground(new java.awt.Color(255, 255, 255));
        payrollButton.setForeground(new java.awt.Color(0, 0, 0));
        payrollButton.setText("Payroll");
        jPanel1.add(payrollButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 140, -1));

        attendanceButton.setBackground(new java.awt.Color(255, 255, 255));
        attendanceButton.setForeground(new java.awt.Color(0, 0, 0));
        attendanceButton.setText("Attendance");
        jPanel1.add(attendanceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 140, -1));

        jPanel2.setBackground(new java.awt.Color(139, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 156, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 136, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 140));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void employeesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeesButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeesButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton attendanceButton;
    private javax.swing.JButton dashboardButton;
    private javax.swing.JButton employeesButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton payrollButton;
    // End of variables declaration//GEN-END:variables
}
