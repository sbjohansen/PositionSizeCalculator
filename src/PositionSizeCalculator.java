import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;  // For sorting

public class PositionSizeCalculator extends JFrame {

    // Input fields for account details
    private JTextField balanceField;
    private JTextField riskField;       // Risk is entered as a percentage (e.g., 1 for 1%)
    private JTextField stopLossField;

    // Combo boxes for entry type and number of entries (max 3)
    private JComboBox<String> entryTypeComboBox;
    private JComboBox<Integer> entryCountComboBox;

    // Panel for entry price fields and list of those fields
    private JPanel entryFieldsPanel;
    private List<JTextField> entryFields = new ArrayList<>();

    // Text area for displaying results
    private JTextArea resultTextArea;

    // Fixed height for each entry field (in pixels)
    private static final int ENTRY_FIELD_HEIGHT = 25;

    // Tracks current dark mode setting
    private boolean darkMode = false;

    public PositionSizeCalculator() {
        setTitle("Position Size Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 800);
        setLocationRelativeTo(null);

        // Set the menu bar (with dark/light toggle and help menu)
        setJMenuBar(createMenuBar());

        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);

        mainPanel.add(createInfoPanel());
        mainPanel.add(createStopLossPanel());
        mainPanel.add(createSettingsPanel());
        mainPanel.add(createEntryPointsPanel());
        mainPanel.add(createCalculatePanel());
        mainPanel.add(createResultsPanel());
        mainPanel.add(createFooterPanel());
    }

    /**
     * Creates the menu bar with a dark mode toggle and a Help menu.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // View Menu with Dark Mode toggle
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeItem = new JCheckBoxMenuItem("Dark Mode");
        darkModeItem.addActionListener(e -> {
            darkMode = darkModeItem.isSelected();
            setDarkMode(darkMode);
        });
        viewMenu.add(darkModeItem);
        menuBar.add(viewMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem usageItem = new JMenuItem("Usage");
        usageItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(usageItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Results"));
        resultTextArea = new JTextArea(10, 30);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        // Set a monospaced font to help with alignment
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Applies the dark or light (default Nimbus) theme.
     */
    private void setDarkMode(boolean darkMode) {
        try {
            // Use Nimbus Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            if (darkMode) {
                UIManager.put("control", new Color(60, 63, 65));
                UIManager.put("info", new Color(60, 63, 65));
                UIManager.put("nimbusBase", new Color(18, 30, 49));
                UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                UIManager.put("nimbusFocus", new Color(115, 164, 209));
                UIManager.put("nimbusGreen", new Color(176, 179, 50));
                UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                UIManager.put("nimbusLightBackground", new Color(43, 43, 43));
                UIManager.put("nimbusOrange", new Color(191, 98, 4));
                UIManager.put("nimbusRed", new Color(169, 46, 34));
                UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                UIManager.put("text", new Color(230, 230, 230));
            } else {
                UIManager.put("control", null);
                UIManager.put("info", null);
                UIManager.put("nimbusBase", null);
                UIManager.put("nimbusAlertYellow", null);
                UIManager.put("nimbusDisabledText", null);
                UIManager.put("nimbusFocus", null);
                UIManager.put("nimbusGreen", null);
                UIManager.put("nimbusInfoBlue", null);
                UIManager.put("nimbusLightBackground", null);
                UIManager.put("nimbusOrange", null);
                UIManager.put("nimbusRed", null);
                UIManager.put("nimbusSelectedText", null);
                UIManager.put("nimbusSelectionBackground", null);
                UIManager.put("text", null);
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the Account Info panel.
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(new TitledBorder("Account Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Account Balance ($):"), gbc);
        gbc.gridx = 1;
        balanceField = new JTextField(10);
        infoPanel.add(balanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Risk (%):"), gbc);
        gbc.gridx = 1;
        riskField = new JTextField(10);
        riskField.setText("1");
        infoPanel.add(riskField, gbc);

        return infoPanel;
    }

    /**
     * Creates the Stop Loss panel.
     */
    private JPanel createStopLossPanel() {
        JPanel stopLossPanel = new JPanel(new GridBagLayout());
        stopLossPanel.setBorder(new TitledBorder("Stop Loss"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        stopLossPanel.add(new JLabel("Stop Loss ($):"), gbc);
        gbc.gridx = 1;
        stopLossField = new JTextField(10);
        stopLossPanel.add(stopLossField, gbc);

        return stopLossPanel;
    }

    /**
     * Creates the Entry Settings panel (for entry type selection).
     */
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(new TitledBorder("Entry Settings"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsPanel.add(new JLabel("Entry Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Single Entry", "Equal-Sized DCA", "Exponential Entries"};
        entryTypeComboBox = new JComboBox<>(types);
        settingsPanel.add(entryTypeComboBox, gbc);

        // Update the entry count model based on the chosen entry type.
        entryTypeComboBox.addActionListener(e -> {
            String type = (String) entryTypeComboBox.getSelectedItem();
            if ("Single Entry".equals(type)) {
                entryCountComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{1}));
                entryCountComboBox.setSelectedItem(1);
                entryCountComboBox.setEnabled(false);
            } else {
                entryCountComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{2, 3}));
                entryCountComboBox.setSelectedItem(2);
                entryCountComboBox.setEnabled(true);
            }
            updateEntryFields((Integer) entryCountComboBox.getSelectedItem());
        });

        return settingsPanel;
    }

    /**
     * Creates the Entry Prices panel with a dropdown to select the number of entries and displays
     * entry price fields (each with fixed height).
     */
    private JPanel createEntryPointsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Entry Prices"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Number of Entries:"));

        String currentType = (String) entryTypeComboBox.getSelectedItem();
        if ("Single Entry".equals(currentType)) {
            entryCountComboBox = new JComboBox<>(new Integer[]{1});
            entryCountComboBox.setEnabled(false);
        } else {
            entryCountComboBox = new JComboBox<>(new Integer[]{2, 3});
            entryCountComboBox.setEnabled(true);
        }
        entryCountComboBox.setSelectedItem("Single Entry".equals(currentType) ? 1 : 2);
        entryCountComboBox.addActionListener(e -> updateEntryFields((Integer) entryCountComboBox.getSelectedItem()));
        topPanel.add(entryCountComboBox);
        panel.add(topPanel, BorderLayout.NORTH);

        entryFieldsPanel = new JPanel();
        entryFieldsPanel.setLayout(new BoxLayout(entryFieldsPanel, BoxLayout.Y_AXIS));
        panel.add(entryFieldsPanel, BorderLayout.CENTER);

        updateEntryFields((Integer) entryCountComboBox.getSelectedItem());

        return panel;
    }

    /**
     * Updates the entryFieldsPanel based on the selected number of entries.
     * Preserves previously entered values (if count is reduced, only the first values are kept).
     */
    private void updateEntryFields(int count) {
        List<String> oldValues = new ArrayList<>();
        for (JTextField field : entryFields) {
            oldValues.add(field.getText());
        }
        entryFieldsPanel.removeAll();
        entryFields.clear();

        for (int i = 0; i < count; i++) {
            JPanel row = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Entry " + (i + 1) + ":");
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(200, ENTRY_FIELD_HEIGHT));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, ENTRY_FIELD_HEIGHT));
            if (i < oldValues.size()) {
                field.setText(oldValues.get(i));
            }
            row.add(label, BorderLayout.WEST);
            row.add(field, BorderLayout.CENTER);
            entryFieldsPanel.add(row);
            entryFields.add(field);
        }
        entryFieldsPanel.revalidate();
        entryFieldsPanel.repaint();
    }

    /**
     * Creates the panel containing the Calculate button.
     */
    private JPanel createCalculatePanel() {
        JPanel panel = new JPanel();
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculatePositions());
        panel.add(calculateButton);
        return panel;
    }

    /**
     * Displays a help dialog explaining how to use the calculator.
     */
    private void showHelpDialog() {
        String helpText = "How to Use Position Size Calculator:\n\n" +
                "1. Account Info:\n" +
                "   - Enter your Account Balance and Risk (%).\n" +
                "     (e.g., entering '1' means 1% risk)\n\n" +
                "2. Stop Loss:\n" +
                "   - Enter your Stop Loss price.\n\n" +
                "3. Entry Settings:\n" +
                "   - Select the Entry Type:\n" +
                "       • Single Entry: Uses one entry price to calculate position size.\n" +
                "       • Equal-Sized DCA: Averages multiple entry prices and divides the total position equally.\n" +
                "       • Exponential Entries: Uses weighted ratios (e.g., 0.5, 1.0, 1.5 for three entries) for a weighted average entry and allocates the position accordingly.\n\n" +
                "   - Note: For DCA and Exponential Entries, you must have at least 2 entries.\n\n" +
                "4. Entry Prices:\n" +
                "   - Choose the number of entries (2 or 3 for DCA/Exponential, or 1 for Single Entry) and enter each entry price.\n\n" +
                "5. Calculation:\n" +
                "   - Click 'Calculate' to compute the position size.\n" +
                "     The tool automatically handles Long positions (entry > stop loss) and Short positions (entry < stop loss) by using the absolute difference in the formula.\n\n" +
                "The results will be displayed in the Results section.";
        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates the Footer panel with copyright information and a disclaimer.
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        String copyright = "© " + Year.now().getValue() + " SBJ";
        JLabel copyrightLabel = new JLabel(copyright);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(copyrightLabel);

        String disclaimer = "This tool may contain bugs. Use at your own responsibility.";
        JLabel disclaimerLabel = new JLabel(disclaimer);
        disclaimerLabel.setFont(disclaimerLabel.getFont().deriveFont(Font.ITALIC, 10f));
        disclaimerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(disclaimerLabel);

        return panel;
    }

    /**
     * Performs the position size calculations based on the chosen entry type.
     * Uses the absolute difference between the entry price and stop loss for both Long and Short positions.
     */
    private void calculatePositions() {
        try {
            double accountBalance = Double.parseDouble(balanceField.getText().trim());
            double riskPercent = Double.parseDouble(riskField.getText().trim()) / 100.0;
            double stopLoss = Double.parseDouble(stopLossField.getText().trim());
            String entryType = (String) entryTypeComboBox.getSelectedItem();
            int count = (Integer) entryCountComboBox.getSelectedItem();

            double[] entries = new double[count];
            for (int i = 0; i < count; i++) {
                entries[i] = Double.parseDouble(entryFields.get(i).getText().trim());
            }

            // --- New Sorting Logic for Multi-Entry Strategies ---
            if (count > 1) {
                // Determine whether all entries are above or below the stop loss.
                boolean allAbove = true;
                boolean allBelow = true;
                for (double entry : entries) {
                    if (entry <= stopLoss) {
                        allAbove = false;
                    }
                    if (entry >= stopLoss) {
                        allBelow = false;
                    }
                }
                if (!allAbove && !allBelow) {
                    throw new Exception("For multi-entry strategies, all entries must be either above or below the stop loss.");
                }
                boolean isLong = allAbove;  // if all entries are above stopLoss, it's a long trade.
                if (isLong) {
                    // For long positions, sort in descending order (highest to lowest).
                    Arrays.sort(entries);  // sorts ascending...
                    for (int i = 0, j = count - 1; i < j; i++, j--) {
                        double temp = entries[i];
                        entries[i] = entries[j];
                        entries[j] = temp;
                    }
                } else {
                    // For short positions, sort in ascending order (lowest to highest).
                    Arrays.sort(entries);
                }
            }
            // --- End Sorting Logic ---

            StringBuilder sb = new StringBuilder();

            if ("Single Entry".equals(entryType) || count == 1) {
                double effectiveEntry = entries[0];
                if (Math.abs(effectiveEntry - stopLoss) == 0) {
                    throw new Exception("Entry price equals stop loss; denominator is zero.");
                }
                String tradeType = (effectiveEntry > stopLoss) ? "Long" : "Short";
                double posSize = (accountBalance * riskPercent * effectiveEntry) / Math.abs(effectiveEntry - stopLoss);
                sb.append("Trade Type: ").append(tradeType).append("\n");
                sb.append("----------------------------------------\n");
                sb.append(String.format("%-10s %12.6f\n", "Entry", posSize));
                sb.append("\n");
                sb.append(String.format("Entry Price: %.6f\n", effectiveEntry));
            } else if ("Equal-Sized DCA".equals(entryType)) {
                double sum = 0.0;
                for (double e : entries) {
                    sum += e;
                }
                double avgEntry = sum / count;
                if (Math.abs(avgEntry - stopLoss) == 0) {
                    throw new Exception("Average entry equals stop loss; denominator is zero.");
                }
                String tradeType = (avgEntry > stopLoss) ? "Long" : "Short";
                double totalPosSize = (accountBalance * riskPercent * avgEntry) / Math.abs(avgEntry - stopLoss);
                double posSizePerEntry = totalPosSize / count;
                sb.append("Trade Type: ").append(tradeType).append("\n");
                sb.append(String.format("Average Entry Price: $%.6f%n", avgEntry));
                sb.append(String.format("Total Position Size: $%.6f%n", totalPosSize));
                sb.append("----------------------------------------\n");
                sb.append(String.format("%-10s %12s %12s%n", "Entry", "Price", "Allocation"));
                for (int i = 0; i < count; i++) {
                    sb.append(String.format("%-10s %12.6f %12.6f%n", "Entry " + (i + 1), entries[i], posSizePerEntry));
                }
            } else if ("Exponential Entries".equals(entryType)) {
                double[] ratios;
                if (count == 2) {
                    ratios = new double[]{0.5, 1.0};
                } else if (count == 3) {
                    ratios = new double[]{0.5, 1.0, 1.5};
                } else {
                    ratios = new double[]{1.0};  // Should not occur since count is restricted to 2 or 3
                }
                double sumRatios = 0.0;
                double weightedSum = 0.0;
                for (int i = 0; i < count; i++) {
                    sumRatios += ratios[i];
                    weightedSum += entries[i] * ratios[i];
                }
                double weightedAvg = weightedSum / sumRatios;
                if (Math.abs(weightedAvg - stopLoss) == 0) {
                    throw new Exception("Weighted average entry equals stop loss; denominator is zero.");
                }
                String tradeType = (weightedAvg > stopLoss) ? "Long" : "Short";
                double totalPosSize = (accountBalance * riskPercent * weightedAvg) / Math.abs(weightedAvg - stopLoss);
                sb.append("Trade Type: ").append(tradeType).append("\n");
                sb.append(String.format("Weighted Average Entry: $%.6f%n", weightedAvg));
                sb.append(String.format("Total Position Size: $%.6f%n", totalPosSize));
                sb.append("----------------------------------------\n");
                sb.append(String.format("%-10s %12s %10s %12s%n", "Entry", "Price", "Ratio", "Allocation"));
                for (int i = 0; i < count; i++) {
                    double entryAllocation = totalPosSize * (ratios[i] / sumRatios);
                    sb.append(String.format("%-10s %12.6f %10.2f %12.6f%n", "Entry " + (i + 1), entries[i], ratios[i], entryAllocation));
                }
            }
            resultTextArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred. Please check your inputs.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            PositionSizeCalculator calculator = new PositionSizeCalculator();
            calculator.setVisible(true);
        });
    }
}
