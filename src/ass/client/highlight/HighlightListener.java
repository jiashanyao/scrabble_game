package ass.client.highlight;

import ass.client.ClientConsole;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class HighlightListener extends MouseMotionAdapter {

    int mouse_row;
    int mouse_col;
    int char_row;
    int char_col;
    JTable gameTable;
    HighlightRender hr_cross;
    HighlightRender hr_row;
    HighlightRender hr_col;

    public HighlightListener(JTable gameTable, int char_row, int char_col) {
        this.gameTable = gameTable;
        this.char_row = char_row;
        this.char_col = char_col;
        this.hr_cross = new HighlightRender(gameTable, char_row, char_col, 0);
        this.hr_row = new HighlightRender(gameTable, char_row, char_col, 1);
        this.hr_col = new HighlightRender(gameTable, char_col, char_col, 2);
    }

    @Override public void mouseMoved(MouseEvent me) {
        mouse_row = gameTable.rowAtPoint(me.getPoint());
        mouse_col = gameTable.columnAtPoint(me.getPoint());
        if (mouse_row == char_row && mouse_col == char_col) {
            for (int i = 0; i < gameTable.getColumnCount(); i++) {
                gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_cross);
            }

        } else if (mouse_row == char_row) {
            for (int i = 0; i < gameTable.getColumnCount(); i++) {
                gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_row);
            }

        } else if (mouse_col == char_col) {
            for (int i = 0; i < gameTable.getColumnCount(); i++) {
                gameTable.getColumnModel().getColumn(i).setCellRenderer(hr_col);
            }
        } else {

            for (int i = 0; i < gameTable.getColumnCount(); i++) {
                gameTable.getColumnModel().getColumn(i).setCellRenderer(ClientConsole.GAME_CELL_RENDER);
            }
        }
        gameTable.repaint();
    }
}
