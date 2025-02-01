import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProfitCalculatorContainer extends JPanel {
    private JTabbedPane tabbedPane;
    private JButton addTabButton;

    public ProfitCalculatorContainer() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Add an initial Profit Calculator tab.
        addNewTab();

        addTabButton = new JButton("+");
        addTabButton.addActionListener(e -> addNewTab());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addTabButton);
        add(topPanel, BorderLayout.NORTH);
    }

    private void addNewTab() {
        ProfitCalculatorPanel panel = new ProfitCalculatorPanel();
        String title = "Calc " + (tabbedPane.getTabCount() + 1);
        tabbedPane.addTab(title, panel);
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setTabComponentAt(index, new TabHeader(title, tabbedPane));
        tabbedPane.setSelectedComponent(panel);
    }

    private static class TabHeader extends JPanel {
        public TabHeader(String title, JTabbedPane pane) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JLabel titleLabel = new JLabel(title + "  ");
            add(titleLabel);
            JButton closeButton = new JButton("X");
            closeButton.setMargin(new Insets(0, 0, 0, 0));
            closeButton.setFocusable(false);
            closeButton.setBorder(BorderFactory.createEmptyBorder());
            closeButton.addActionListener(e -> {
                int i = pane.indexOfTabComponent(this);
                if (i != -1) {
                    pane.remove(i);
                }
            });
            add(closeButton);
        }
    }
}
