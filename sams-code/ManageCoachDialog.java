package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ManageCoachDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private DBHelper db;

    public ManageCoachDialog(JFrame parent) {
        super(parent, "Manage Coaches", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        db = DBHelper.getInstance();

        /* ================= HEADER ================= */
        JLabel title = new JLabel("Manage Coaches", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        /* ================= SEARCH ================= */
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Name:"));
        searchField = new JTextField(25);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        /* ================= TABLE ================= */
        model = new DefaultTableModel(
                new Object[]{"Coach ID","Name","Identifier","Status","Location","Contact","Assigned Since"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();

        /* ================= BUTTONS ================= */
        JPanel btnPanel = new JPanel();

        JButton addBtn = new JButton("Add Coach");
        JButton editBtn = new JButton("Edit Coach");
        JButton delBtn = new JButton("Delete Coach");
        JButton closeBtn = new JButton("Close");

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);

        /* ================= LISTENERS ================= */
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addCoach(); }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editCoach(); }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteCoach(); }
        });

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }

    /* ================= CORE METHODS ================= */

    private void refreshTable() {
        model.setRowCount(0);
        List<Coach> list = db.getAllCoaches();
        for (Coach c : list) {
            model.addRow(new Object[]{
                    c.getCoachId(),
                    c.getName(),
                    c.getIdentifier(),
                    c.getStatus(),
                    c.getLocation(),
                    c.getContact(),
                    c.getAssignedSince()
            });
        }
    }

    private void filter() {
        String text = searchField.getText().trim();
        sorter.setRowFilter(text.length() == 0 ? null :
                RowFilter.regexFilter("(?i)" + text, 1));
    }

    /* ================= ADD ================= */

    private void addCoach() {
        Coach c = showCoachForm(null);
        if (c != null) {
            db.addCoach(c);
            refreshTable();
        }
    }

    /* ================= EDIT ================= */

    private void editCoach() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach first");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        Coach old = new Coach(
                (Integer) model.getValueAt(modelRow, 0),
                (String) model.getValueAt(modelRow, 1),
                (String) model.getValueAt(modelRow, 2),
                (String) model.getValueAt(modelRow, 3),
                (String) model.getValueAt(modelRow, 4),
                (String) model.getValueAt(modelRow, 5)
        );

        Coach updated = showCoachForm(old);
        if (updated != null) {
            db.updateCoach(updated);
            refreshTable();
        }
    }

    /* ================= DELETE ================= */

    private void deleteCoach() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach first");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (Integer) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this coach?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteCoach(id);
            refreshTable();
        }
    }

    /* ================= FORM ================= */

    private Coach showCoachForm(Coach c) {

        JTextField name = new JTextField(c != null ? c.getName() : "");
        JTextField identifier = new JTextField(c != null ? c.getIdentifier() : "");
        JComboBox<String> status = new JComboBox<String>(
                new String[]{"Active","Inactive"});
        if (c != null) status.setSelectedItem(c.getStatus());

        JTextField location = new JTextField(c != null ? c.getLocation() : "");
        JTextField contact = new JTextField(c != null ? c.getContact() : "");

        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("Name:")); p.add(name);
        p.add(new JLabel("Identifier:")); p.add(identifier);
        p.add(new JLabel("Status:")); p.add(status);
        p.add(new JLabel("Location:")); p.add(location);
        p.add(new JLabel("Contact:")); p.add(contact);

        int r = JOptionPane.showConfirmDialog(this, p,
                c == null ? "Add Coach" : "Edit Coach",
                JOptionPane.OK_CANCEL_OPTION);

        if (r != JOptionPane.OK_OPTION) return null;

        if (name.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required");
            return null;
        }

        Coach coach = (c == null)
                ? new Coach(name.getText(), identifier.getText(),
                            status.getSelectedItem().toString(),
                            location.getText(), contact.getText())
                : new Coach(c.getCoachId(), name.getText(),
                            identifier.getText(),
                            status.getSelectedItem().toString(),
                            location.getText(), contact.getText());

        return coach;
    }
}
