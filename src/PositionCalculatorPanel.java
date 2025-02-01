import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PositionCalculatorPanel extends JPanel {
    private JTextField balanceField;
    private JTextField riskField;
    private JTextField stopLossField;
    private EntrySettingsPanel entrySettingsPanel;
    private JTextArea resultsArea;

    public PositionCalculatorPanel() {
        // Create two panels: one for settings (left) and one for results (right)
        // using a JSplitPane.
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        // Account Information Panel
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        accountPanel.setBorder(new TitledBorder("Account Information"));
        accountPanel.add(new JLabel("Account Balance:"));
        balanceField = new JTextField(10);
        accountPanel.add(balanceField);
        accountPanel.add(new JLabel("Risk (%):"));
        riskField = new JTextField(10);
        accountPanel.add(riskField);
        settingsPanel.add(accountPanel);

        // Stop Loss Panel
        JPanel stopLossPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        stopLossPanel.setBorder(new TitledBorder("Stop Loss"));
        stopLossPanel.add(new JLabel("Stop Loss:"));
        stopLossField = new JTextField(10);
        stopLossPanel.add(stopLossField);
        settingsPanel.add(stopLossPanel);

        // Entry Settings Panel (prospective mode: all entries assumed triggered)
        entrySettingsPanel = new EntrySettingsPanel(true);
        settingsPanel.add(entrySettingsPanel);

        // Calculate Button
        JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JButton calcButton = new JButton("Calculate Position");
        calcPanel.add(calcButton);
        settingsPanel.add(calcPanel);
        calcButton.addActionListener(e -> calculatePosition());

        // Results Area (right panel)
        resultsArea = new JTextArea(20, 30);
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        JScrollPane resultsScroll = new JScrollPane(resultsArea);

        // Use a JSplitPane to show settings on the left and results on the right.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, settingsPanel, resultsScroll);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private void calculatePosition() {
        try {
            double balance = Double.parseDouble(balanceField.getText().trim());
            double riskPct = Double.parseDouble(riskField.getText().trim()) / 100.0;
            double stopLoss = Double.parseDouble(stopLossField.getText().trim());
            List<Double> entries = entrySettingsPanel.getEntryPrices();
            if (entries.isEmpty()) {
                throw new Exception("Please provide at least one entry price.");
            }

            // If more than one entry, ensure that all entries are either above or below the stop loss,
            // and sort them in an order that makes sense.
            if (entries.size() > 1) {
                boolean allAbove = true;
                boolean allBelow = true;
                for (Double e : entries) {
                    if (e <= stopLoss) {
                        allAbove = false;
                    }
                    if (e >= stopLoss) {
                        allBelow = false;
                    }
                }
                if (!allAbove && !allBelow) {
                    throw new Exception("For multi-entry strategies, all entries must be either above or below the stop loss.");
                }
                if (allAbove) {
                    // For a long position, sort descending (highest first).
                    Collections.sort(entries, Comparator.reverseOrder());
                } else {
                    // For a short position, sort ascending.
                    Collections.sort(entries);
                }
            }

            // Calculate average entry using the sorted list.
            String entryType = entrySettingsPanel.getSelectedEntryType();
            double avgEntry = 0.0;
            StringBuilder details = new StringBuilder();
            details.append("Entry Type: ").append(entryType).append("\n");
            if ("Single Entry".equals(entryType) || entries.size() == 1) {
                avgEntry = entries.get(0);
                details.append("Entry Price: ").append(String.format("%.4f", avgEntry)).append("\n");
            } else if ("Equal-Sized DCA".equals(entryType)) {
                double sum = 0.0;
                details.append("Entries (sorted):\n");
                for (int i = 0; i < entries.size(); i++) {
                    double e = entries.get(i);
                    sum += e;
                    details.append("  E").append(i + 1).append(": ").append(String.format("%.4f", e)).append("\n");
                }
                avgEntry = sum / entries.size();
                details.append("Average Entry: ").append(String.format("%.4f", avgEntry)).append("\n");
            } else if ("Exponential Entries".equals(entryType)) {
                double[] ratios = (entries.size() == 2) ? new double[]{0.5, 1.0} : new double[]{0.5, 1.0, 1.5};
                double weightedSum = 0.0, sumRatios = 0.0;
                details.append("Entries (sorted):\n");
                for (int i = 0; i < entries.size(); i++) {
                    double e = entries.get(i);
                    weightedSum += e * ratios[i];
                    sumRatios += ratios[i];
                    details.append("  E").append(i + 1).append(": ").append(String.format("%.4f", e))
                            .append(" (ratio=").append(ratios[i]).append(")\n");
                }
                avgEntry = weightedSum / sumRatios;
                details.append("Weighted Average Entry: ").append(String.format("%.4f", avgEntry)).append("\n");
            }

            boolean isLong = avgEntry > stopLoss;
            if (Math.abs(avgEntry - stopLoss) < 1e-9) {
                throw new Exception("Entry price equals Stop Loss. Cannot calculate risk.");
            }
            double posSizeUSD = (balance * riskPct * avgEntry) / Math.abs(avgEntry - stopLoss);
            double riskUSD = Math.abs(avgEntry - stopLoss) * (posSizeUSD / avgEntry);

            StringBuilder res = new StringBuilder();
            res.append("===== Position Calculation =====\n\n");
            res.append(details.toString());
            res.append(String.format("Stop Loss: %.4f\n", stopLoss));
            res.append(String.format("Trade Type: %s\n", isLong ? "Long" : "Short"));
            res.append(String.format("Position Size (USD): %.2f\n", posSizeUSD));
            res.append(String.format("Total Risk (USD): %.2f\n", riskUSD));
            res.append("----------------------------------------\n");
            // Display per-entry allocation along with the entry price.
            if ("Single Entry".equals(entryType) || entries.size() == 1) {
                res.append(String.format("Allocation for Entry 1 (Price: $%.2f): $%.2f\n", avgEntry, posSizeUSD));
            } else if ("Equal-Sized DCA".equals(entryType)) {
                int n = entries.size();
                double alloc = posSizeUSD / n;
                for (int i = 0; i < n; i++) {
                    double entryPrice = entries.get(i);
                    res.append(String.format("Allocation for Entry %d (Price: $%.2f): $%.2f\n", i + 1, entryPrice, alloc));
                }
            } else if ("Exponential Entries".equals(entryType)) {
                int n = entries.size();
                double[] ratios = (n == 2) ? new double[]{0.5, 1.0} : new double[]{0.5, 1.0, 1.5};
                double sumRatios = 0.0;
                for (int i = 0; i < n; i++) {
                    sumRatios += ratios[i];
                }
                for (int i = 0; i < n; i++) {
                    double alloc = posSizeUSD * (ratios[i] / sumRatios);
                    double entryPrice = entries.get(i);
                    res.append(String.format("Allocation for Entry %d (Price: $%.2f): $%.2f\n", i + 1, entryPrice, alloc));
                }
            }
            resultsArea.setText(res.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
