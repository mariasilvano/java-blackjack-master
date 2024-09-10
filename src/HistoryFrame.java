import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HistoryFrame extends JFrame {

    private JTextArea historyTextArea;
    private ArrayList<String> dealerHistory;

    public HistoryFrame(ArrayList<String> dealerHistory) {
        this.dealerHistory = dealerHistory;
        initialize();
    }

    private void initialize() {
        setTitle("History");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        historyTextArea.setLineWrap(true);
        historyTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        add(scrollPane, BorderLayout.CENTER);

        updateHistory();
    }

    public void updateHistory() {
        StringBuilder historyBuilder = new StringBuilder();
        for (String message : dealerHistory) {
            historyBuilder.append(message).append("\n");
        }
        historyTextArea.setText(historyBuilder.toString());
    }

    public void clearHistory() {
        historyTextArea.setText("");
    }
    
}
