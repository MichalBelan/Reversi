package sk.stuba.fei.uim.oop.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.TimerTask;

import sk.stuba.fei.uim.oop.board.Board;
import sk.stuba.fei.uim.oop.enums.Step;

public class AIPlayer extends TimerTask {

    private final Board board;
    private Step movePiece;
    private final int rows;
    private final int cols;

    public AIPlayer(final int rows, final int cols, final Board board){
        this.rows=rows;
        this.cols=cols;
        this.board = board;
    }

    public AIPlayerStep makeTurn(final int rows, final int cols, final Step movePiece, final Step[][] board){
        final ArrayList<AIPlayerStep> list = new ArrayList<>();
        for(int row = 0; row < rows; row ++) {
            for(int col = 0; col < cols; col++) {
                final AIPlayerStep aiPlayerStep = findBestStep(rows, cols, row, col, movePiece, board);
                if (aiPlayerStep != null) list.add(aiPlayerStep);
            }
        }
        if (list.isEmpty()){
            this.board.swapTurns();
            return null;
        }
        return list.stream().max(Comparator.comparing(AIPlayerStep::getDistance)).orElseThrow(NoSuchElementException::new);
    }

    private AIPlayerStep findBestStep(final int rows, final int cols, final int row, final int col, final Step movePiece, final Step[][] board){
        final AIPlayerStep aiPlayerStep = new AIPlayerStep();
        aiPlayerStep.setDistance(0);
        for(int dX = -1; dX < 2; dX++) {
            for(int dY = -1; dY < 2; dY ++) {
                if(dX == 0 && dY == 0) {
                    continue;
                }
                final int checkRow = row + dX;
                final int checkCol = col + dY;
                if(checkRow >= 0 && checkCol >= 0 && checkRow < rows && checkCol < cols) {
                    if(board[checkRow][checkCol] == (movePiece == Step.DARK ? Step.LIGHT : Step.DARK)) {
                        for(int distance = 0; distance < rows; distance++) {   // keep track of the distance
                            final int minorCheckRow = row+distance*dX;
                            final int minorCheckCol = col+distance*dY;
                            if(minorCheckRow < 0 || minorCheckCol < 0  || minorCheckRow > rows - 1 || minorCheckCol > cols - 1) continue;
                            if(board[minorCheckRow][minorCheckCol] == movePiece) {
                                if(distance > aiPlayerStep.getDistance() && board[row][col] == Step.NONE){
                                    aiPlayerStep.setDistance(distance);
                                    aiPlayerStep.setCoordinates(new int[] { row, col });
                                }
                            }
                        }
                    }
                }
            }
        }
        if (aiPlayerStep.getDistance()==0) return null;
        return aiPlayerStep;
    }

    public void setMovePiece(final Step movePiece){
        this.movePiece = movePiece;
    }


    @Override
    public void run() {
        if (!board.computerTurn) return;
        final AIPlayerStep aiPlayerStep = makeTurn(rows, cols, movePiece, board.getBoard());
        if (aiPlayerStep==null){
            board.swapTurns();
            return;
        }
        board.makeAiStep(aiPlayerStep);
    }
}
