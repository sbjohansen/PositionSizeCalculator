import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class PositionSizeCalculator extends JFrame {

    private JTabbedPane tabbedPane;
    private JButton addTabButton; // Button to add a new tab

    public PositionSizeCalculator() {
        setTitle("Position Size Calculator - Multi-Trade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // We reserve a fixed size so that all components are visible.
        setSize(600, 800);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());

        // Main panel with BorderLayout and a fixed gap.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(600, 800));
        add(mainPanel);

        // Top panel with a plus button.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addTabButton = new JButton("+");
        addTabButton.setToolTipText("Add New Tab");
        addTabButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addTabButton.addActionListener(e -> addNewTab());
        topPanel.add(addTabButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Create the tabbed pane.
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the first calculator tab.
        addNewTab();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu.
        JMenu fileMenu = new JMenu("File");
        JMenuItem newTabItem = new JMenuItem("New Tab");
        newTabItem.addActionListener(e -> addNewTab());
        fileMenu.add(newTabItem);
        JMenuItem closeTabItem = new JMenuItem("Close Tab");
        closeTabItem.addActionListener(e -> closeCurrentTab());
        fileMenu.add(closeTabItem);
        menuBar.add(fileMenu);

        // View menu for dark mode.
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeItem = new JCheckBoxMenuItem("Dark Mode");
        darkModeItem.addActionListener(e -> setDarkMode(darkModeItem.isSelected()));
        viewMenu.add(darkModeItem);
        menuBar.add(viewMenu);

        // Help menu.
        JMenu helpMenu = new JMenu("Help");
        JMenuItem usageItem = new JMenuItem("Usage");
        usageItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(usageItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void addNewTab() {
        CalculatorPanel panel = new CalculatorPanel();
        String title = "Trade " + (tabbedPane.getTabCount() + 1);
        tabbedPane.addTab(title, panel);
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setTabComponentAt(index, new TabHeader(title));
        tabbedPane.setSelectedComponent(panel);
    }

    private void closeCurrentTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            tabbedPane.remove(index);
            if (tabbedPane.getTabCount() == 0) {
                addNewTab();
            }
        }
    }

    // Custom tab header with a title and an "X" button.
    private class TabHeader extends JPanel {
        public TabHeader(String title) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JLabel lblTitle = new JLabel(title + "   ");
            add(lblTitle);
            JButton btnClose = new JButton("X");
            btnClose.setMargin(new Insets(0, 0, 0, 0));
            btnClose.setBorder(BorderFactory.createEmptyBorder());
            btnClose.setFocusable(false);
            btnClose.setToolTipText("Close this tab");
            btnClose.addActionListener(e -> {
                int i = tabbedPane.indexOfTabComponent(TabHeader.this);
                if (i != -1) {
                    tabbedPane.remove(i);
                    if (tabbedPane.getTabCount() == 0) {
                        addNewTab();
                    }
                }
            });
            add(btnClose);
        }
    }

    // Sets dark or light mode.
    private void setDarkMode(boolean darkMode) {
        try {
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

    private void showHelpDialog() {
        String helpText = "How to Use Position Size Calculator:\n\n" +
                "1. Account Info: Enter your Account Balance and Risk (%).\n" +
                "2. Stop Loss: Enter your Stop Loss price.\n" +
                "3. Entry Settings: Select the Entry Type and number of entries.\n" +
                "4. Entry Prices: Enter each entry price.\n" +
                "5. Calculation: Click 'Calculate' to see the result.\n\n" +
                "Use the plus (+) button at the top or File -> New Tab to add a new calculator tab.\n" +
                "Close a tab by clicking the X in its header.";
        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    // Inner CalculatorPanel encapsulating the UI and logic for a single trade calculation.
    private class CalculatorPanel extends JPanel {

        // Input fields for account details.
        private JTextField balanceField;
        private JTextField riskField;
        private JTextField stopLossField;

        // Combo boxes for entry type and number of entries.
        private JComboBox<String> entryTypeComboBox;
        private JComboBox<Integer> entryCountComboBox;

        // Panel for entry price fields.
        private JPanel entryFieldsPanel;
        private List<JTextField> entryFields = new ArrayList<>();

        // Text area for displaying results.
        private JTextArea resultTextArea;

        // Fixed height for each entry field (we reserve one text-line height).
        private final int ENTRY_FIELD_HEIGHT = 25; // Adjust as needed

        public CalculatorPanel() {
            // Use BorderLayout with padding.
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Top area: Account Info and Stop Loss side by side.
            JPanel topArea = new JPanel(new GridLayout(1, 2, 10, 10));
            topArea.add(createInfoPanel());
            topArea.add(createStopLossPanel());
            add(topArea, BorderLayout.NORTH);

            // Center area: Two rows — Entry Settings on top and Entry Prices below.
            JPanel centerArea = new JPanel();
            centerArea.setLayout(new BoxLayout(centerArea, BoxLayout.Y_AXIS));
            centerArea.add(createSettingsPanel());
            centerArea.add(Box.createRigidArea(new Dimension(0, 10))); // spacer
            centerArea.add(createEntryPointsPanel());
            add(centerArea, BorderLayout.CENTER);

            // Bottom area: Calculate button, Results, and Footer arranged vertically.
            JPanel bottomArea = new JPanel();
            bottomArea.setLayout(new BoxLayout(bottomArea, BoxLayout.Y_AXIS));
            bottomArea.add(createCalculatePanel());
            bottomArea.add(Box.createRigidArea(new Dimension(0, 10)));
            bottomArea.add(createResultsPanel());
            bottomArea.add(Box.createRigidArea(new Dimension(0, 10)));
            bottomArea.add(createFooterPanel());
            add(bottomArea, BorderLayout.SOUTH);
        }

        private JPanel createInfoPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Account Info"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Account Balance ($):"), gbc);
            gbc.gridx = 1;
            balanceField = new JTextField(10);
            // Force a fixed height (one text line)
            balanceField.setPreferredSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            balanceField.setMaximumSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            panel.add(balanceField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Risk (%):"), gbc);
            gbc.gridx = 1;
            riskField = new JTextField(10);
            riskField.setText("1");
            riskField.setPreferredSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            riskField.setMaximumSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            panel.add(riskField, gbc);
            return panel;
        }

        private JPanel createStopLossPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Stop Loss"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Stop Loss ($):"), gbc);
            gbc.gridx = 1;
            stopLossField = new JTextField(10);
            stopLossField.setPreferredSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            stopLossField.setMaximumSize(new Dimension(100, ENTRY_FIELD_HEIGHT));
            panel.add(stopLossField, gbc);
            return panel;
        }

        private JPanel createSettingsPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Entry Settings"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Entry Type:"), gbc);
            gbc.gridx = 1;
            String[] types = {"Single Entry", "Equal-Sized DCA", "Exponential Entries"};
            entryTypeComboBox = new JComboBox<>(types);
            // Force a consistent height
            entryTypeComboBox.setPreferredSize(new Dimension(150, ENTRY_FIELD_HEIGHT));
            entryTypeComboBox.setMaximumSize(new Dimension(150, ENTRY_FIELD_HEIGHT));
            panel.add(entryTypeComboBox, gbc);
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
            return panel;
        }

        private JPanel createEntryPointsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new TitledBorder("Entry Prices"));
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("Number of Entries:"));
            if ("Single Entry".equals(entryTypeComboBox.getSelectedItem())) {
                entryCountComboBox = new JComboBox<>(new Integer[]{1});
                entryCountComboBox.setEnabled(false);
            } else {
                entryCountComboBox = new JComboBox<>(new Integer[]{2, 3});
                entryCountComboBox.setEnabled(true);
            }
            entryCountComboBox.setSelectedItem("Single Entry".equals(entryTypeComboBox.getSelectedItem()) ? 1 : 2);
            // Fix the size of the combo box
            entryCountComboBox.setPreferredSize(new Dimension(50, ENTRY_FIELD_HEIGHT));
            entryCountComboBox.setMaximumSize(new Dimension(50, ENTRY_FIELD_HEIGHT));
            entryCountComboBox.addActionListener(e -> updateEntryFields((Integer) entryCountComboBox.getSelectedItem()));
            topPanel.add(entryCountComboBox);
            panel.add(topPanel, BorderLayout.NORTH);
            entryFieldsPanel = new JPanel();
            entryFieldsPanel.setLayout(new BoxLayout(entryFieldsPanel, BoxLayout.Y_AXIS));
            panel.add(entryFieldsPanel, BorderLayout.CENTER);
            updateEntryFields((Integer) entryCountComboBox.getSelectedItem());
            return panel;
        }

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
                // Ensure label is fixed width
                label.setPreferredSize(new Dimension(70, ENTRY_FIELD_HEIGHT));
                JTextField field = new JTextField();
                // Force the field to be exactly one text-line high.
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

        private JPanel createCalculatePanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton calculateButton = new JButton("Calculate");
            calculateButton.addActionListener(e -> calculatePositions());
            panel.add(calculateButton);
            return panel;
        }

        private JPanel createResultsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new TitledBorder("Results"));
            resultTextArea = new JTextArea();
            resultTextArea.setEditable(false);
            resultTextArea.setLineWrap(true);
            resultTextArea.setWrapStyleWord(true);
            resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            // Set a fixed preferred size to display, say, 10 lines of text without scrolling.
            resultTextArea.setPreferredSize(new Dimension(400, 150));
            // Wrap in a scroll pane but disable scrollbars.
            JScrollPane scrollPane = new JScrollPane(resultTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            panel.add(scrollPane, BorderLayout.CENTER);
            return panel;
        }

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
                if (count > 1) {
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
                    boolean isLong = allAbove;
                    if (isLong) {
                        Arrays.sort(entries);
                        for (int i = 0, j = count - 1; i < j; i++, j--) {
                            double temp = entries[i];
                            entries[i] = entries[j];
                            entries[j] = temp;
                        }
                    } else {
                        Arrays.sort(entries);
                    }
                }
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
                        ratios = new double[]{1.0};
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PositionSizeCalculator frame = new PositionSizeCalculator();
            frame.setVisible(true);
        });
    }
}
