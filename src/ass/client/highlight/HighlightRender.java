package ass.client.highlight;

import ass.client.ClientConsole;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HighlightRender extends DefaultTableCellRenderer {

    private int char_row;
    private int char_col;
    private int type;
    private JTable gameTable;
    private int[][] highlightRange;

    private Color hlColor = new Color(102, 76, 36);
    private Color hlFont = Color.WHITE;
    private Color bkColor = new Color(255, 255, 204);
    private Color dfFont = Color.BLACK;

    /**
     * set highlight color in specific area(according to type)
     *
     * @param char_row
     * @param char_col
     * @param type     0:cross, 1: same row, 2: same column
     */
    public HighlightRender(JTable gameTable, int char_row, int char_col, int type) {
        this.gameTable = gameTable;
        this.char_row = char_row;
        this.char_col = char_col;
        this.type = type;
        this.highlightRange =  calculateHighlightRange(gameTable, char_row, char_col);
    }

    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        int[] highlightRange_cellsOfRow = this.highlightRange[0];
        int[] highlightRange_cellsOfCol = this.highlightRange[1];

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        // cross
        if (type == 0) {
            if (row == this.char_row && col >= highlightRange_cellsOfRow[0] && col <= highlightRange_cellsOfRow[1]) {
                l.setBackground(hlColor);
                l.setForeground(hlFont);
            } else if (col == this.char_col && row >= highlightRange_cellsOfCol[0] && row <= highlightRange_cellsOfCol[1]) {
                l.setBackground(hlColor);
                l.setForeground(hlFont);
            } else {
                l.setBackground(bkColor);
                l.setForeground(dfFont);
            }
        }
        // same row
        else if (type == 1) {
            if (row == this.char_row && col >= highlightRange_cellsOfRow[0] && col <= highlightRange_cellsOfRow[1]) {
                l.setBackground(hlColor);
                l.setForeground(hlFont);
            } else {
                l.setBackground(bkColor);
                l.setForeground(dfFont);
            }
        }// same column
        else if (type == 2) {
            if (col == this.char_col && row >= highlightRange_cellsOfCol[0] && row <= highlightRange_cellsOfCol[1]) {
                l.setBackground(hlColor);
                l.setForeground(hlFont);
            } else {
                l.setBackground(bkColor);
                l.setForeground(dfFont);
            }
        }

        l.setHorizontalAlignment(SwingConstants.CENTER);

        return l;
    }

    /**
     * Calculate the range of highlight according to character position
     * @param gameTable
     * @param char_row
     * @param char_col
     * @return result[0]: cellsOfRow, result[1]: cellsOfCol
     */
    public static int[][] calculateHighlightRange(JTable gameTable, int char_row, int char_col){
        int[][] result = new int[2][2];
        int[] cellsOfRow = new int[2];
        int[] cellsOfCol = new int[2];

        // get the range of the row
        for (int i = char_col; i >= 0; i--) {
            Object value = gameTable.getModel().getValueAt(char_row, i);
            if (null == value || StringUtils.isBlank(value.toString())) {
                cellsOfRow[0] = i + 1;
                break;
            } else if (i == 0) {
                cellsOfRow[0] = i;
            }
        }
        for (int i = char_col; i < ClientConsole.BOARD_SIZE; i++) {
            Object value = gameTable.getModel().getValueAt(char_row, i);
            if (null == value || StringUtils.isBlank(value.toString())) {
                cellsOfRow[1] = i - 1;
                break;
            } else if (i == ClientConsole.BOARD_SIZE - 1) {
                cellsOfRow[1] = i;
            }
        }

        // get the range of the column
        for (int i = char_row; i >= 0; i--) {
            Object value = gameTable.getModel().getValueAt(i, char_col);
            if (null == value || StringUtils.isBlank(value.toString())) {
                cellsOfCol[0] = i + 1;
                break;
            } else if (i == 0) {
                cellsOfCol[0] = i;
            }
        }
        for (int i = char_row; i < ClientConsole.BOARD_SIZE; i++) {
            Object value = gameTable.getModel().getValueAt(i, char_col);
            if (null == value || StringUtils.isBlank(value.toString())) {
                cellsOfCol[1] = i - 1;
                break;
            } else if (i == ClientConsole.BOARD_SIZE - 1) {
                cellsOfCol[1] = i;
            }
        }
        result[0] = cellsOfRow;
        result[1] = cellsOfCol;
        return result;
    }

    /**
     * select the highlight range according to mouse position
     * @param char_row char axis of row
     * @param char_col char axis of column
     * @param ms_row mouse axis of row
     * @param ms_col mouse axis of column
     * @return result[0]: cells of one row, result[1]: cells of one column
     */
    public static int[][] selectHighlightRange(JTable gameTable, int char_row, int char_col, int ms_row, int ms_col) {
        int[][] result = new int[2][2];
        int[][] tmp = calculateHighlightRange(gameTable, char_row, char_col);
        // Cross
        if (char_row == ms_row && char_col == ms_col) {
            // one character
            if(tmp[0][0] == tmp[0][1] && tmp[1][0] == tmp[1][1]){
                result[0] = tmp[0];
                result[1] = null;
            } else if(tmp[0][0] == tmp[0][1]){
                // one column
                result[0] = null;
                result[1] = tmp[1];
            } else if(tmp[1][0] == tmp[1][1]){
                // one row
                result[0] = tmp[0];
                result[1] = null;
            } else {
                // two cross strings
                result = tmp;
            }

        } else if (char_row == ms_row) {
            // Same row
            result[0] = tmp[0];
            result[1] = null;
        } else if (char_col == ms_col) {
            // Same Column
            result[0] = null;
            result[1] = tmp[1];
        }
        return result;
    }

    /**
     * Display highlight
     * @param char_row
     * @param char_col
     * @param highlightStr
     */
    public static void displayHighlightString(JTable gameTable, int char_row, int char_col, String[] highlightStr){
        HighlightRender hr_cross = new HighlightRender(gameTable, char_row, char_col, 0);
        HighlightRender hr_row = new HighlightRender(gameTable, char_row, char_col, 1);
        HighlightRender hr_col = new HighlightRender(gameTable, char_col, char_col, 2);
        if (highlightStr != null && highlightStr.length == 2) {
            if(StringUtils.isNotBlank(highlightStr[0]) && StringUtils.isNotBlank(highlightStr[1])) {
                for (int i = 0; i < gameTable.getColumnCount(); i++) {
                    gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_cross);
                }

            } else if (StringUtils.isNotBlank(highlightStr[0])) {
                for (int i = 0; i < gameTable.getColumnCount(); i++) {
                    gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_row);
                }
            } else if (StringUtils.isNotBlank(highlightStr[1])) {
                for (int i = 0; i < gameTable.getColumnCount(); i++) {
                    gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_col);
                }
            }

        } else {
            for (int i = 0; i < gameTable.getColumnCount(); i++) {
                gameTable.getColumnModel().getColumn(i).setCellRenderer(ClientConsole.GAME_CELL_RENDER);
            }
        }
        gameTable.repaint();
    }
}
