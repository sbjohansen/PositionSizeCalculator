import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PositionSizeCalculator extends JFrame {
    public PositionSizeCalculator() {
        super("Trade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set overall window size.
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Set up the menu bar with a Help menu.
        setJMenuBar(createMenuBar());

        // Create a top-level tabbed pane with two modes.
        JTabbedPane modeTabbedPane = new JTabbedPane();

        // Each mode uses its own container with multiple calculation tabs.
        PositionCalculatorContainer posContainer = new PositionCalculatorContainer();
        ProfitCalculatorContainer profitContainer = new ProfitCalculatorContainer();

        modeTabbedPane.addTab("Position Calculator", posContainer);
        modeTabbedPane.addTab("Profit Calculator", profitContainer);

        // Create a disclaimer panel to show at the bottom.
        JPanel disclaimerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel disclaimerLabel = new JLabel("© " + java.time.Year.now().getValue() + " SBJ - This tool might contain bugs. Use at your own risk.");
        disclaimerPanel.add(disclaimerLabel);

        // Use a BorderLayout in the main content pane.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(modeTabbedPane, BorderLayout.CENTER);
        getContentPane().add(disclaimerPanel, BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu.
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Help menu.
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Usage Instructions");
        helpItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showHelpDialog() {
        String helpText =
                "Trade Calculator Instructions:\n\n" +
                        "Position Calculator:\n" +
                        "  • Enter your Account Balance and Risk (%) in the Account Information panel.\n" +
                        "  • Enter your Stop Loss in the separate Stop Loss panel.\n" +
                        "  • Choose your Entry Type and enter one or more entry prices (all assumed triggered).\n" +
                        "  • The calculation will display the total Position Size (in USD), Total Risk, and the USD allocation for each entry.\n\n" +
                        "Profit Calculator:\n" +
                        "  • Enter your Account Balance and Risk (%) in the Trade Details panel.\n" +
                        "  • Enter your Stop Loss in the Stop Loss panel.\n" +
                        "  • Mark which entry prices were actually triggered in the Entry Settings panel.\n" +
                        "  • Optionally, enter a global Trade Actual Close Price in the Trade Close Price panel.\n" +
                        "  • In the Take Profit Setup panel, for each TP, enter the planned TP Price, the Close % (of the original position), and mark if that TP was triggered.\n" +
                        "  • The calculation will compute the profit from each triggered TP and, if a global close price is provided, assume the remaining open portion is closed at that price.\n" +
                        "  • Finally, the Risk–Reward Ratio (total profit divided by total risk) is displayed.\n\n" +
                        "You can open multiple tabs in either mode using the '+' button and close any tab by clicking its 'X' button.";
        JTextArea textArea = new JTextArea(helpText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Usage Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PositionSizeCalculator frame = new PositionSizeCalculator();
            frame.setVisible(true);
        });
    }
}
