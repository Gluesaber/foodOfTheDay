import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class AddMenu extends JFrame {
    private JTextField nameField, priceField, cuisineField;
    private JCheckBox spicyBox, halalBox, healthyBox, seafoodBox, veganBox;
    private MainAVL mainWindow;  // Reference to MainAVL
    private Consumer<FoodItem> onSubmit;  // This will hold the animation callback

    // Updated constructor with the animation callback
    public AddMenu(MainAVL mainWindow, Consumer<FoodItem> onSubmit) {
        this.mainWindow = mainWindow;
        this.onSubmit = onSubmit;

        setTitle("Add New Food Item");
        setSize(300, 400);
        setLayout(new GridLayout(9, 2, 5, 5));
        setLocationRelativeTo(mainWindow); // Center on Main Window

        // Initialize UI components
        nameField = new JTextField();
        priceField = new JTextField();
        cuisineField = new JTextField();
        spicyBox = new JCheckBox("Spicy");
        halalBox = new JCheckBox("Halal");
        healthyBox = new JCheckBox("Healthy");
        seafoodBox = new JCheckBox("Seafood");
        veganBox = new JCheckBox("Vegan");

        // Add components to the frame
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel("Cuisine:"));
        add(cuisineField);
        add(spicyBox);
        add(halalBox);
        add(healthyBox);
        add(seafoodBox);
        add(veganBox);

        JButton submitButton = new JButton("Add Item");
        add(submitButton);

        // Set up the submit button action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitItem();
            }
        });

        setVisible(true);
    }

    private void submitItem() {
        try {
            String name = nameField.getText();
            int price = Integer.parseInt(priceField.getText());
            String cuisine = cuisineField.getText();
            boolean isSpicy = spicyBox.isSelected();
            boolean isHalal = halalBox.isSelected();
            boolean isHealthy = healthyBox.isSelected();
            boolean isSeafood = seafoodBox.isSelected();
            boolean isVegan = veganBox.isSelected();

            FoodItem item = new FoodItem(name, price, cuisine, isSpicy, isHalal, isHealthy, isSeafood, isVegan);

            // Trigger the animation callback (instead of directly adding it to mainWindow)
            if (onSubmit != null) {
                onSubmit.accept(item); // This calls the animation method in TreePanel
            }

            dispose();  // Close the popup window after submitting
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
