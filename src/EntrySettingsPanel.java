import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntrySettingsPanel extends JPanel {
    private JComboBox<String> entryTypeCombo;
    private JComboBox<Integer> entryCountCombo;
    private JPanel entryFieldsPanel;
    private List<EntryRow> entryRows = new ArrayList<>();
    private boolean isProspective; // true if in Position Calculator mode

    public EntrySettingsPanel(boolean isProspective) {
        this.isProspective = isProspective;
        // Use a vertical BoxLayout with minimal spacing.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new TitledBorder("Entry Settings"));

        // Top row for Entry Type and Count.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        topPanel.add(new JLabel("Entry Type:"));
        entryTypeCombo = new JComboBox<>(new String[] {
                "Single Entry", "Equal-Sized DCA", "Exponential Entries"
        });
        topPanel.add(entryTypeCombo);
        topPanel.add(new JLabel("Count:"));
        entryCountCombo = new JComboBox<>(new Integer[]{1,2,3});
        topPanel.add(entryCountCombo);
        entryTypeCombo.addActionListener(e -> onEntryTypeChanged());
        entryCountCombo.addActionListener(e -> updateEntryRows());
        add(topPanel);

        // Entry fields panel (each row is compact)
        entryFieldsPanel = new JPanel();
        entryFieldsPanel.setLayout(new BoxLayout(entryFieldsPanel, BoxLayout.Y_AXIS));
        add(entryFieldsPanel);

        onEntryTypeChanged();
        updateEntryRows();
    }

    private void onEntryTypeChanged() {
        String type = (String) entryTypeCombo.getSelectedItem();
        if ("Single Entry".equals(type)) {
            entryCountCombo.setModel(new DefaultComboBoxModel<>(new Integer[]{1}));
            entryCountCombo.setEnabled(false);
        } else {
            entryCountCombo.setModel(new DefaultComboBoxModel<>(new Integer[]{2,3}));
            entryCountCombo.setEnabled(true);
        }
        updateEntryRows();
    }

    private void updateEntryRows() {
        int count = (int) entryCountCombo.getSelectedItem();
        List<EntryRowData> oldData = new ArrayList<>();
        for (EntryRow row : entryRows) {
            oldData.add(new EntryRowData(row.getPriceText(), row.isTriggered()));
        }
        entryFieldsPanel.removeAll();
        entryRows.clear();
        for (int i = 0; i < count; i++) {
            EntryRow row = new EntryRow("Entry " + (i+1) + " Price:", isProspective);
            if (i < oldData.size()) {
                row.setPriceText(oldData.get(i).price);
                row.setTriggered(oldData.get(i).triggered);
            }
            entryRows.add(row);
            entryFieldsPanel.add(row);
        }
        // Remove extra vertical spacing.
        entryFieldsPanel.revalidate();
        entryFieldsPanel.repaint();
    }

    public String getSelectedEntryType() {
        return (String) entryTypeCombo.getSelectedItem();
    }

    public List<Double> getEntryPrices() {
        List<Double> result = new ArrayList<>();
        for (EntryRow row : entryRows) {
            if (isProspective || row.isTriggered()) {
                try {
                    result.add(Double.parseDouble(row.getPriceText().trim()));
                } catch (NumberFormatException ex) { }
            }
        }
        return result;
    }

    private static class EntryRow extends JPanel {
        private JTextField priceField;
        private JCheckBox triggeredBox;

        public EntryRow(String labelText, boolean hideCheckbox) {
            // Use FlowLayout with small horizontal gaps.
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
            add(new JLabel(labelText));
            priceField = new JTextField(8);
            add(priceField);
            if (!hideCheckbox) {
                triggeredBox = new JCheckBox("Triggered", true);
                add(triggeredBox);
            } else {
                triggeredBox = new JCheckBox("Triggered", true);
                triggeredBox.setVisible(false);
            }
        }

        public String getPriceText() {
            return priceField.getText();
        }

        public void setPriceText(String text) {
            priceField.setText(text);
        }

        public boolean isTriggered() {
            return triggeredBox.isSelected();
        }

        public void setTriggered(boolean b) {
            triggeredBox.setSelected(b);
        }
    }

    private static class EntryRowData {
        String price;
        boolean triggered;
        public EntryRowData(String price, boolean triggered) {
            this.price = price;
            this.triggered = triggered;
        }
    }
}
