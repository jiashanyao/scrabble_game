package ass.client.highlight;

import ass.client.ClientConsole;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HighlightRender extends DefaultTableCellRenderer {

    private int char_row;
    private int char_col;
    private int type;
    private JTable gameTable;

    /**
     * set highlight color in specific area(according to type)
     *
     * @param char_row
     * @param char_col
     * @param type     0:cross, 1: same row, 2: same column
     */
    HighlightRender(JTable gameTable, int char_row, int char_col, int type) {
        this.gameTable = gameTable;
        this.char_row = char_row;
        this.char_col = char_col;
        this.type = type;
    }

    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        int[] highlightRange_cols = getHighlightRange(this.gameTable, this.char_row, char_col, 0);
        int[] highlightRange_rows = getHighlightRange(this.gameTable, this.char_row, char_col, 1);

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        // cross
        if (type == 0) {
            if (row == this.char_row && col >= highlightRange_cols[0] && col <= highlightRange_cols[1]) {
                l.setBackground(Color.GREEN);
            } else if (col == this.char_col && row >= highlightRange_rows[0] && row <= highlightRange_rows[1]) {
                l.setBackground(Color.GREEN);
            } else {
                l.setBackground(new Color(255, 255, 204));
            }
        }
        // same row
        else if (type == 1) {
            if (row == this.char_row && col >= highlightRange_cols[0] && col <= highlightRange_cols[1]) {
                l.setBackground(Color.GREEN);
            } else {
                l.setBackground(new Color(255, 255, 204));
            }
        }// same column
        else if (type == 2) {
            if (col == this.char_col && row >= highlightRange_rows[0] && row <= highlightRange_rows[1]) {
                l.setBackground(Color.GREEN);
            } else {
                l.setBackground(new Color(255, 255, 204));
            }
        }

        l.setHorizontalAlignment(SwingConstants.CENTER);

        return l;
    }

    /**
     * get the highlight range according to character position
     *
     * @param char_row
     * @param char_col
     * @param type     (0: same row, 1: same column)
     * @return start and coordinate
     */
    public static int[] getHighlightRange(JTable gameTable, int char_row, int char_col, int type) {
        int[] result = new int[2];
        //Same row
        if (type == 0) {
            for (int i = char_col; i >= 0; i--) {
                if (gameTable.getModel().getValueAt(char_row, i).toString().isEmpty()) {
                    result[0] = i + 1;
                    break;
                } else if (i == 0) {
                    result[0] = i;
                }
            }
            for (int j = char_col; j < ClientConsole.BOARD_SIZE; j++) {
                if (gameTable.getModel().getValueAt(char_row, j).toString().isEmpty()) {
                    result[1] = j - 1;
                    break;
                } else if (j == ClientConsole.BOARD_SIZE - 1) {
                    result[1] = j;
                }
            }
        } else if (type == 1) {
            //Same column
            for (int i = char_row; i >= 0; i--) {
                if (gameTable.getModel().getValueAt(i, char_col).toString().isEmpty()) {
                    result[0] = i + 1;
                    break;
                } else if (i == 0) {
                    result[0] = i;
                }
            }
            for (int j = char_row; j < ClientConsole.BOARD_SIZE; j++) {
                if (gameTable.getModel().getValueAt(j, char_col).toString().isEmpty()) {
                    result[1] = j - 1;
                    break;
                } else if (j == ClientConsole.BOARD_SIZE - 1) {
                    result[1] = j;
                }
            }
        }
        return result;
    }
}
