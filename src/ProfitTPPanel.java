import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProfitTPPanel extends JPanel {
    private JComboBox<Integer> tpCountCombo;
    private JPanel tpFieldsPanel;
    private List<TPRow> tpRows = new ArrayList<>();

    public ProfitTPPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new TitledBorder("Take Profit Setup"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        topPanel.add(new JLabel("Number of TPs:"));
        tpCountCombo = new JComboBox<>(new Integer[]{0,1,2,3,4,5});
        tpCountCombo.setSelectedIndex(0);
        tpCountCombo.addActionListener(e -> updateTPRows());
        topPanel.add(tpCountCombo);
        add(topPanel);

        tpFieldsPanel = new JPanel();
        tpFieldsPanel.setLayout(new BoxLayout(tpFieldsPanel, BoxLayout.Y_AXIS));
        add(tpFieldsPanel);

        updateTPRows();
    }

    private void updateTPRows() {
        int count = (int) tpCountCombo.getSelectedItem();
        List<TPRowData> oldData = new ArrayList<>();
        for (TPRow row : tpRows) {
            oldData.add(new TPRowData(row.getTPPrice(), row.getClosePct(), row.isTriggered()));
        }
        tpFieldsPanel.removeAll();
        tpRows.clear();
        for (int i = 0; i < count; i++) {
            TPRow row = new TPRow("TP " + (i+1) + " Price:", "Close %:");
            if (i < oldData.size()) {
                TPRowData data = oldData.get(i);
                row.setTPPrice(data.tpPriceStr);
                row.setClosePct(data.closePctStr);
                row.setTriggered(data.triggered);
            }
            tpRows.add(row);
            tpFieldsPanel.add(row);
        }
        revalidate();
        repaint();
    }

    public List<TPRowData> getTPData() {
        List<TPRowData> result = new ArrayList<>();
        for (TPRow row : tpRows) {
            String tpPriceStr = row.getTPPrice().trim();
            String closePctStr = row.getClosePct().trim();
            try {
                Double.parseDouble(tpPriceStr);
                Double.parseDouble(closePctStr);
                result.add(new TPRowData(tpPriceStr, closePctStr, row.isTriggered()));
            } catch (NumberFormatException ex) { }
        }
        return result;
    }

    public static class TPRowData {
        public String tpPriceStr;
        public String closePctStr;
        public boolean triggered;
        public TPRowData(String tpPriceStr, String closePctStr, boolean triggered) {
            this.tpPriceStr = tpPriceStr;
            this.closePctStr = closePctStr;
            this.triggered = triggered;
        }
    }

    private static class TPRow extends JPanel {
        private JTextField tpPriceField;
        private JTextField closePctField;
        private JCheckBox triggeredBox;

        public TPRow(String tpLabel, String pctLabel) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
            add(new JLabel(tpLabel));
            tpPriceField = new JTextField(8);
            add(tpPriceField);
            add(new JLabel(pctLabel));
            closePctField = new JTextField(5);
            add(closePctField);
            triggeredBox = new JCheckBox("Triggered", false);
            add(triggeredBox);
        }

        public String getTPPrice() {
            return tpPriceField.getText();
        }

        public void setTPPrice(String text) {
            tpPriceField.setText(text);
        }

        public String getClosePct() {
            return closePctField.getText();
        }

        public void setClosePct(String text) {
            closePctField.setText(text);
        }

        public boolean isTriggered() {
            return triggeredBox.isSelected();
        }

        public void setTriggered(boolean b) {
            triggeredBox.setSelected(b);
        }
    }
}
