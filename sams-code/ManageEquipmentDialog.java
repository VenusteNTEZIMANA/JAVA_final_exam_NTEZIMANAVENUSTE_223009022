package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;  // <-- THIS IMPORT WAS MISSING!

/**
 * ManageEquipmentDialog - Admin tool for managing sports equipment inventory
 * Modern UI | Full CRUD | Search & Filter
 * Java 1.7 compatible
 */
public class ManageEquipmentDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private DBHelper db;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private User currentUser;

    public ManageEquipmentDialog(JFrame parent, User user) {
        super(parent, "Manage Equipment", true);
        this.currentUser = user;
        this.db = DBHelper.getInstance();

        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 204));
        header.setPreferredSize(new Dimension(900, 80));

        JLabel lblTitle = new JLabel("Manage Equipment", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Track and manage team gear & inventory", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(200, 220, 255));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(400, 40));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Quantity", "Status", "Location"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        btnPanel.setBackground(new Color(240, 248, 255));

        JButton addBtn = createStyledButton("Add Equipment", new Color(33, 150, 243));
        JButton editBtn = createStyledButton("Edit Selected", new Color(76, 175, 80));
        JButton delBtn = createStyledButton("Delete Selected", new Color(244, 67, 54));
        JButton closeBtn = createStyledButton("Close", new Color(158, 158, 158));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // Load data
        refreshTable();

        // ===== ACTION LISTENERS =====
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEquipment();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editEquipment();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEquipment();
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // Double-click to view details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow != -1) {
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        showEquipmentDetails(modelRow);
                    }
                }
            }
        });
    }

    private JButton createStyledButton(String text, Color baseColor) {
        final Color base = baseColor;
        final Color hover = base.brighter();
        final Color pressed = base.darker();

        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 50));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Restore hover color when mouse is released (while still over the button)
                // Or use base if you want it to go back to original only when not hovering
                btn.setBackground(hover);
            }
        });
        return btn;
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Equipment> equipments = db.getAllEquipments();  // Now properly generic!

        for (Equipment eq : equipments) {
            model.addRow(new Object[]{
                eq.getEquipmentId(),
                eq.getName(),
                eq.getQuantity(),
                eq.getStatus() != null ? eq.getStatus() : "Unknown",
                eq.getLocation() != null ? eq.getLocation() : "Not set"
            });
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void addEquipment() {
        JTextField nameField = new JTextField(20);
        JTextField qtyField = new JTextField(10);
        JTextField statusField = new JTextField(20);
        JTextField locationField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Status/Condition:"));
        panel.add(statusField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Equipment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Equipment name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(qtyField.getText().trim());
                if (quantity < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity! Must be a non-negative number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = statusField.getText().trim();
            String location = locationField.getText().trim();

            Equipment newEq = new Equipment(0, name, quantity, 
                    status.isEmpty() ? "Good" : status, 
                    location.isEmpty() ? "Storage" : location);

            boolean success = db.addEquipment(newEq);

            JOptionPane.showMessageDialog(this, 
                    success ? "Equipment added successfully!" : "Failed to add equipment.",
                    "Result", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (success) refreshTable();
        }
    }

    private void editEquipment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an equipment item to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (Integer) model.getValueAt(modelRow, 0);

        Equipment current = db.getEquipmentById(id);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Could not load equipment details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(current.getName(), 20);
        JTextField qtyField = new JTextField(String.valueOf(current.getQuantity()), 10);
        JTextField statusField = new JTextField(current.getStatus(), 20);
        JTextField locationField = new JTextField(current.getLocation(), 20);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Status/Condition:"));
        panel.add(statusField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Equipment ID: " + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(qtyField.getText().trim());
                if (quantity < 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = statusField.getText().trim();
            String location = locationField.getText().trim();

            Equipment updated = new Equipment(id, name, quantity, status, location);
            boolean success = db.updateEquipment(updated);

            JOptionPane.showMessageDialog(this, 
                    success ? "Equipment updated!" : "Update failed.",
                    "Result", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (success) refreshTable();
        }
    }

    private void deleteEquipment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an equipment item to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (Integer) model.getValueAt(modelRow, 0);
        String name = (String) model.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n" + name + " (ID: " + id + ")",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = db.deleteEquipment(id);
            JOptionPane.showMessageDialog(this, 
                    success ? "Equipment deleted." : "Delete failed.",
                    "Result", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (success) refreshTable();
        }
    }

    private void showEquipmentDetails(int modelRow) {
        String details = "<html><b>Name:</b> " + model.getValueAt(modelRow, 1) + "<br>" +
                         "<b>Quantity:</b> " + model.getValueAt(modelRow, 2) + "<br>" +
                         "<b>Status:</b> " + model.getValueAt(modelRow, 3) + "<br>" +
                         "<b>Location:</b> " + model.getValueAt(modelRow, 4) + "</html>";

        JOptionPane.showMessageDialog(this, details, "Equipment Details", JOptionPane.INFORMATION_MESSAGE);
    }
}