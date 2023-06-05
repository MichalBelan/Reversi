package sk.stuba.fei.uim.oop.board;

import java.util.TimerTask;

import sk.stuba.fei.uim.oop.enums.Step;

public class BoardHighlighter extends TimerTask {

    private final Board board;
    private final int rows;
    private final int cols;
    private Step movePiece;

    public BoardHighlighter(final int rows, final int cols, final Board board){
        this.board = board;
        this.rows = rows;
        this.cols = cols;
    }

    public void setMovePiece(final Step movePiece){
        this.movePiece = movePiece;
    }

    @Override
    public void run() {
        if (board.computerTurn) return;
        for(int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(board.doFlip(row, col, movePiece, false) && board.getBoard()[row][col] == Step.NONE) {
                    board.addRespectivePics(row, col, "transparent");
                }
            }
        }
    }
}
