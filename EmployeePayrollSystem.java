import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

class Employee {
    private String id;
    private String name;
    private double basicSalary;
    private String department;

    public Employee() {
        this.id = "";
        this.name = "";
        this.basicSalary = 0.0;
        this.department = "";
    }

    public Employee(String id, String name, double basicSalary, String department) {
        this.id = id;
        this.name = name;
        this.basicSalary = basicSalary;
        this.department = department;
    }

    public Employee(Employee other) {
        this.id = other.id;
        this.name = other.name;
        this.basicSalary = other.basicSalary;
        this.department = other.department;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getBasicSalary() { return basicSalary; }
    public String getDepartment() { return department; }

    public double calculateNetSalary() {
        double hra = 0.20 * basicSalary;
        double da = 0.10 * basicSalary;  
        double pf = 0.12 * basicSalary;  
        return basicSalary + (hra + da) - pf;
    }

    public String toFileString() {
        return id + "," + name + "," + basicSalary + "," + department;
    }

    public static Employee fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            String id = parts[0];
            String name = parts[1];
            double salary = Double.parseDouble(parts[2]);
            String dept = parts[3];
            return new Employee(id, name, salary, dept);
        }
        return null;
    }
}

public class EmployeePayrollSystem extends JFrame {

    private JTextField txtId, txtName, txtSalary;
    private JComboBox<String> cmbDepartment;
    private JButton btnAdd, btnSave, btnLoad, btnViewAll;
    private JTable table;
    private DefaultTableModel tableModel;

    private ArrayList<Employee> employeeList;

    private static final String FILE_NAME = "employees.txt";

    public EmployeePayrollSystem() {

        employeeList = new ArrayList<>();

        setTitle("Employee Payroll System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        inputPanel.add(new JLabel("Employee ID:"));
        txtId = new JTextField();
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Basic Salary (₹):"));
        txtSalary = new JTextField();
        inputPanel.add(txtSalary);

        inputPanel.add(new JLabel("Department:"));
        String[] departments = {"IT", "HR", "Finance", "Marketing", "Operations"};
        cmbDepartment = new JComboBox<>(departments);
        inputPanel.add(cmbDepartment);

        btnAdd = new JButton("Add Employee");
        btnSave = new JButton("Save to File");
        btnLoad = new JButton("Load from File");
        btnViewAll = new JButton("View All");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnViewAll);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Department", "Basic Salary", "Net Salary"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee Payroll Records"));

        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addEmployee());
        btnSave.addActionListener(e -> saveToFile());
        btnLoad.addActionListener(e -> loadFromFile());
        btnViewAll.addActionListener(e -> refreshTable());

        loadFromFile();
    }

    private void addEmployee() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String salaryStr = txtSalary.getText().trim();
        String dept = (String) cmbDepartment.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || salaryStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double basicSalary = Double.parseDouble(salaryStr);

            Employee emp = new Employee(id, name, basicSalary, dept);

            employeeList.add(emp);

            clearInputs();

            refreshTable();

            JOptionPane.showMessageDialog(this, "Employee Added Successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric salary!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Employee emp : employeeList) {
                writer.write(emp.toFileString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Data successfully saved to " + FILE_NAME);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; 
        }

        employeeList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Employee emp = Employee.fromFileString(line);
                if (emp != null) {
                    employeeList.add(emp);
                }
            }
            refreshTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee emp : employeeList) {
            Object[] row = {
                emp.getId(),
                emp.getName(),
                emp.getDepartment(),
                String.format("%.2f", emp.getBasicSalary()),
                String.format("%.2f", emp.calculateNetSalary()) 
            };
            tableModel.addRow(row);
        }
    }

    private void clearInputs() {
        txtId.setText("");
        txtName.setText("");
        txtSalary.setText("");
        cmbDepartment.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmployeePayrollSystem().setVisible(true);
        });
    }
}