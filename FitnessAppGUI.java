import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;

public class FitnessAppGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// Declare GUI components
    private JLabel nameLabel, ageLabel, weightLabel, workoutLabel, summaryLabel;
    private JTextField nameField, ageField, weightField;
    private JComboBox<String> workoutComboBox;
    private JTextArea summaryArea;
    private JButton submitButton, clearButton;

    // Map to hold workouts associated with names
    private HashMap<String, ArrayList<String>> userWorkouts;

    // Database connection variables
    private Connection connection;

    // Constructor to set up the GUI
    public FitnessAppGUI() {
        userWorkouts = new HashMap<>();

        // Set up the frame
        setTitle("Fitness App");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));

        // Initialize the components
        nameLabel = new JLabel("Name:");
        ageLabel = new JLabel("Age:");
        weightLabel = new JLabel("Weight (kg):");
        workoutLabel = new JLabel("Workout Type:");
        summaryLabel = new JLabel("Summary:");

        nameField = new JTextField();
        ageField = new JTextField();
        weightField = new JTextField();

        // Options for workout types
        String[] workouts = {"Cardio", "Strength", "Yoga", "Crossfit"};
        workoutComboBox = new JComboBox<>(workouts);

        summaryArea = new JTextArea(5, 20);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);

        submitButton = new JButton("Submit");
        clearButton = new JButton("Clear");

        // Add action listeners for buttons
        submitButton.addActionListener(this);
        clearButton.addActionListener(this);

        // Add components to the frame
        add(nameLabel);
        add(nameField);
        add(ageLabel);
        add(ageField);
        add(weightLabel);
        add(weightField);
        add(workoutLabel);
        add(workoutComboBox);
        add(submitButton);
        add(clearButton);
        add(summaryLabel);
        add(new JScrollPane(summaryArea));

        // Initialize database connection
        initializeDBConnection();

        // Make the window visible
        setVisible(true);
    }

    // Initialize database connection
    private void initializeDBConnection() {
        String url = "jdbc:mysql://localhost:3306/fitness_app";
        String username = "root"; // Replace with your MySQL username
        String password = "Wlgxz@145"; // Replace with your MySQL password

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Action handling for button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            // Get the user's input
            String name = nameField.getText();
            String age = ageField.getText();
            String weight = weightField.getText();
            String workout = (String) workoutComboBox.getSelectedItem();

            // Insert the data into the database
            saveToDatabase(name, age, weight, workout);

            // Display the data in the summary area
            String entry = "Age: " + age + ", Weight: " + weight + " kg, Workout: " + workout;
            userWorkouts.putIfAbsent(name, new ArrayList<>());
            userWorkouts.get(name).add(entry);

            // Build the summary
            StringBuilder summary = new StringBuilder("All Users and Workouts:\n");
            for (String user : userWorkouts.keySet()) {
                summary.append("Workouts for ").append(user).append(":\n");
                for (String workoutEntry : userWorkouts.get(user)) {
                    summary.append("  - ").append(workoutEntry).append("\n");
                }
                summary.append("----------------------\n");
            }
            summaryArea.setText(summary.toString());
        } else if (e.getSource() == clearButton) {
            // Clear all fields and summary area
            nameField.setText("");
            ageField.setText("");
            weightField.setText("");
            workoutComboBox.setSelectedIndex(0);
            summaryArea.setText("");
            userWorkouts.clear();
        }
    }

    // Save user data to the MySQL database
    private void saveToDatabase(String name, String age, String weight, String workout) {
        String query = "INSERT INTO user_workouts (name, age, weight, workout_type) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, Integer.parseInt(age));
            statement.setFloat(3, Float.parseFloat(weight));
            statement.setString(4, workout);
            statement.executeUpdate();
            System.out.println("Data saved to database successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        new FitnessAppGUI();
    }
}