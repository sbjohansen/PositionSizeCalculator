import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ProfitCalculatorPanel extends JPanel {
    private JTextField balanceField;
    private JTextField riskField;
    private JTextField stopLossField;
    private EntrySettingsPanel entrySettingsPanel;
    private JTextField actualClosePriceField; // Global trade actual close price (optional)
    private ProfitTPPanel profitTPPanel;
    private JTextArea resultsArea;

    public ProfitCalculatorPanel() {
        // Create a left-panel for inputs.
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Trade Details Panel (Account and Risk)
        JPanel tradeDetailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        tradeDetailsPanel.setBorder(new TitledBorder("Trade Details"));
        tradeDetailsPanel.add(new JLabel("Account Balance:"));
        balanceField = new JTextField(10);
        tradeDetailsPanel.add(balanceField);
        tradeDetailsPanel.add(new JLabel("Risk (%):"));
        riskField = new JTextField(10);
        tradeDetailsPanel.add(riskField);
        inputPanel.add(tradeDetailsPanel);

        // Stop Loss Panel
        JPanel stopLossPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        stopLossPanel.setBorder(new TitledBorder("Stop Loss"));
        stopLossPanel.add(new JLabel("Stop Loss:"));
        stopLossField = new JTextField(10);
        stopLossPanel.add(stopLossField);
        inputPanel.add(stopLossPanel);

        // Entry Settings Panel (post-trade: user marks triggered entries)
        entrySettingsPanel = new EntrySettingsPanel(false);
        inputPanel.add(entrySettingsPanel);

        // Trade Actual Close Price Panel
        JPanel closePricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        closePricePanel.setBorder(new TitledBorder("Trade Close Price (optional)"));
        closePricePanel.add(new JLabel("Trade Actual Close Price:"));
        actualClosePriceField = new JTextField(10);
        closePricePanel.add(actualClosePriceField);
        inputPanel.add(closePricePanel);

        // Profit TP Panel
        profitTPPanel = new ProfitTPPanel();
        inputPanel.add(profitTPPanel);

        // Calculate Button
        JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JButton calcButton = new JButton("Calculate Profit");
        calcPanel.add(calcButton);
        inputPanel.add(calcPanel);
        calcButton.addActionListener(e -> calculateProfit());

        // Results Area (right panel)
        resultsArea = new JTextArea(20, 30);
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        JScrollPane resultsScroll = new JScrollPane(resultsArea);

        // Use JSplitPane to display inputs on the left and results on the right.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, resultsScroll);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private void calculateProfit() {
        try {
            double balance = Double.parseDouble(balanceField.getText().trim());
            double riskPct = Double.parseDouble(riskField.getText().trim()) / 100.0;
            double stopLoss = Double.parseDouble(stopLossField.getText().trim());

            List<Double> entries = entrySettingsPanel.getEntryPrices();
            if (entries.isEmpty()) {
                throw new Exception("Please mark at least one entry as triggered.");
            }
            String entryType = entrySettingsPanel.getSelectedEntryType();
            double avgEntry = 0.0;
            StringBuilder entryDetails = new StringBuilder();
            entryDetails.append("Entry Type: ").append(entryType).append("\n");
            if ("Single Entry".equals(entryType) || entries.size() == 1) {
                avgEntry = entries.get(0);
                entryDetails.append("Entry Price: ").append(String.format("%.4f", avgEntry)).append("\n");
            } else if ("Equal-Sized DCA".equals(entryType)) {
                double sum = 0.0;
                entryDetails.append("Triggered Entries:\n");
                for (int i = 0; i < entries.size(); i++) {
                    double e = entries.get(i);
                    sum += e;
                    entryDetails.append("  E").append(i+1).append(": ").append(String.format("%.4f", e)).append("\n");
                }
                avgEntry = sum / entries.size();
                entryDetails.append("Average Entry: ").append(String.format("%.4f", avgEntry)).append("\n");
            } else if ("Exponential Entries".equals(entryType)) {
                double[] ratios = (entries.size() == 2) ? new double[]{0.5, 1.0} : new double[]{0.5, 1.0, 1.5};
                double weightedSum = 0.0, sumRatios = 0.0;
                entryDetails.append("Triggered Entries:\n");
                for (int i = 0; i < entries.size(); i++) {
                    double e = entries.get(i);
                    weightedSum += e * ratios[i];
                    sumRatios += ratios[i];
                    entryDetails.append("  E").append(i+1).append(": ").append(String.format("%.4f", e))
                            .append(" (ratio=").append(ratios[i]).append(")\n");
                }
                avgEntry = weightedSum / sumRatios;
                entryDetails.append("Weighted Avg Entry: ").append(String.format("%.4f", avgEntry)).append("\n");
            }

            boolean isLong = avgEntry > stopLoss;
            if (Math.abs(avgEntry - stopLoss) < 1e-9) {
                throw new Exception("Entry equals Stop Loss. Risk undefined.");
            }

            double posSizeUSD = (balance * riskPct * avgEntry) / Math.abs(avgEntry - stopLoss);
            double totalShares = posSizeUSD / avgEntry;
            double riskUSD = Math.abs(avgEntry - stopLoss) * totalShares;

            List<ProfitTPPanel.TPRowData> tpDataList = profitTPPanel.getTPData();
            double triggeredPctSum = 0.0;
            StringBuilder tpDetails = new StringBuilder();
            tpDetails.append("Take Profit Details:\n");
            double profitFromTPs = 0.0;
            for (int i = 0; i < tpDataList.size(); i++) {
                ProfitTPPanel.TPRowData tp = tpDataList.get(i);
                double tpPct = Double.parseDouble(tp.closePctStr);
                double plannedTP = Double.parseDouble(tp.tpPriceStr);
                if (tp.triggered) {
                    triggeredPctSum += tpPct;
                    double profitSegment = (plannedTP - avgEntry) * (totalShares * (tpPct / 100.0));
                    if (!isLong) {
                        profitSegment = (avgEntry - plannedTP) * (totalShares * (tpPct / 100.0));
                    }
                    profitFromTPs += profitSegment;
                    tpDetails.append(String.format("  TP%d (Triggered): Planned Price = %.2f, Close%% = %.2f%%, Profit = $%.2f\n",
                            i + 1, plannedTP, tpPct, profitSegment));
                } else {
                    tpDetails.append(String.format("  TP%d (Not Triggered): Planned Price = %.2f, Close%% = %.2f%%\n",
                            i + 1, plannedTP, tpPct));
                }
            }
            double remainingPct = 100.0 - triggeredPctSum;
            double profitFromRemaining = 0.0;
            if (!actualClosePriceField.getText().trim().isEmpty()) {
                double globalClosePrice = Double.parseDouble(actualClosePriceField.getText().trim());
                if (remainingPct > 0) {
                    profitFromRemaining = (globalClosePrice - avgEntry) * (totalShares * (remainingPct / 100.0));
                    if (!isLong) {
                        profitFromRemaining = (avgEntry - globalClosePrice) * (totalShares * (remainingPct / 100.0));
                    }
                    tpDetails.append(String.format("  Remaining (%.2f%%) closed at Global Close Price = %.2f, Profit = $%.2f\n",
                            remainingPct, globalClosePrice, profitFromRemaining));
                }
            }
            double totalProfit = profitFromTPs + profitFromRemaining;
            double riskReward = (riskUSD != 0) ? totalProfit / riskUSD : 0.0;

            StringBuilder res = new StringBuilder();
            res.append("===== Profit Calculation =====\n\n");
            res.append(entryDetails.toString()).append("\n");
            res.append(String.format("Stop Loss: %.4f\n", stopLoss));
            res.append(String.format("Trade Type: %s\n", isLong ? "Long" : "Short"));
            res.append(String.format("Position Size (USD): %.2f\n", posSizeUSD));
            res.append(String.format("Total Risk (USD): %.2f\n", riskUSD));
            res.append(String.format("Risk–Reward Ratio: %.2f\n", riskReward));
            res.append("----------------------------------------\n");
            res.append(tpDetails.toString());
            res.append("----------------------------------------\n");
            if (!actualClosePriceField.getText().trim().isEmpty()) {
                res.append(String.format("Total Actual Profit: $%.2f\n", totalProfit));
            } else {
                res.append(String.format("Total Planned Profit: $%.2f\n", totalProfit));
            }

            resultsArea.setText(res.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
