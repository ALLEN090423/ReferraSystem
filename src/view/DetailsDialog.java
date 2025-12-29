package view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Generic dialog to display full details of a record.
 * This is used to show all CSV fields without cluttering the JTable.
 */
public class DetailsDialog {

    public static void showDetails(Component parent, String title, Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No details available.");
            return;
        }

        JTextArea textArea = new JTextArea(18, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : details.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue() == null ? "" : entry.getValue())
                    .append("\n");
        }

        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 420));

        JOptionPane.showMessageDialog(parent, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
