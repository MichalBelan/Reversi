package sk.stuba.fei.uim.oop.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import sk.stuba.fei.uim.oop.board.Board;

public class Reversi extends JFrame{

    public Reversi(final int rows,final int cols) {
        super("REVERSI");

        setLayout(new BorderLayout());
        createBoard(rows,cols);
    }

    private void createBoard(final int rows, final int cols){
        final JPanel pnlLeft = new Board(rows, cols,this);
        add(pnlLeft, BorderLayout.CENTER);
        setBounds(200, 50, 580, 700);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
