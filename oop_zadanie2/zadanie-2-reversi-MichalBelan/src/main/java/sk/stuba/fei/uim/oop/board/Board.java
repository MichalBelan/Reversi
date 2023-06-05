package sk.stuba.fei.uim.oop.board;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import sk.stuba.fei.uim.oop.enums.Step;
import sk.stuba.fei.uim.oop.ui.Reversi;
import sk.stuba.fei.uim.oop.ai.AIPlayer;
import sk.stuba.fei.uim.oop.ai.AIPlayerStep;

public class Board extends JPanel {

    private int rows;
    private int cols;
    private final JComboBox<String> comboBox;
    private final JLabel darkScoreLbl;
    private final JLabel lightScoreLbl;
    private final JLabel currentPlayerLbl;
    private final JPanel boardPanel;
    private final Step[][] board;
    private Step movePiece;
    private int darkScore = 2;
    private int lightScore = 2;
    public boolean computerTurn = false;
    private final AIPlayer aiPlayer;
    private final Reversi reversi;
    private final Timer aiPlayerMoveTimer;
    private final Timer boardHighlighterTimer;
    private final BoardHighlighter boardHighlighter;
    private final String sizes[] = { "6x6", "8x8", "10x10", "12x12"};

    public Board(final int rows, final int cols, final Reversi reversi) {
        super(new BorderLayout());
        this.rows = rows;
        this.reversi = reversi;
        this.cols = cols;
        board = new Step[rows][cols];
        setBorder(new TitledBorder("REVERSI"));
        setOpaque(true);
        final JPanel topPanel = new JPanel(new FlowLayout());
        final JButton btnNewGame = new JButton("Start New Game");
        btnNewGame.addActionListener(e -> newGame(rows, cols));
        currentPlayerLbl = new JLabel("Current Player : DARK", JLabel.RIGHT);
        currentPlayerLbl.setFont(new Font("Lucida Calligraphy", Font.BOLD, 18));
        topPanel.add(btnNewGame);
        topPanel.add(currentPlayerLbl);
        comboBox = new JComboBox<>(sizes);
        switch (rows){
            case 6:
                comboBox.setSelectedIndex(0);
                break;
            case 8:
                comboBox.setSelectedIndex(1);
                break;
            case 10:
                comboBox.setSelectedIndex(2);
                break;
            case 12:
                comboBox.setSelectedIndex(3);
                break;
            default:
                break;
        }
        comboBox.addItemListener(this::itemStateChanged);
        topPanel.add(comboBox);
        add(topPanel, BorderLayout.NORTH);
        boardPanel = new JPanel(new GridLayout(rows, cols));
        for(int row = 0; row < rows; row++) {
            for(int col=0; col < cols; col++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setSize(560 / rows, 560 / cols);
                cell.setBackground(new Color(50, 230, 50));
                cell.setBorder(BorderFactory.createLineBorder(Color.gray));
                boardPanel.add(cell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
        final JPanel scorePanel = new JPanel(new FlowLayout());
        darkScoreLbl = new JLabel("Dark : " + darkScore);
        lightScoreLbl = new JLabel("Light: " + lightScore);
        final JButton btnPass = new JButton("Left your turn");
        btnPass.addActionListener(e -> {
            computerTurn = !computerTurn;
            swapTurns();
        });
        scorePanel.add(darkScoreLbl);
        scorePanel.add(btnPass);
        scorePanel.add(lightScoreLbl);
        add(scorePanel, BorderLayout.SOUTH);
        aiPlayer = new AIPlayer(rows,cols, this);
        aiPlayerMoveTimer = new Timer();
        aiPlayerMoveTimer.schedule(aiPlayer, 3000, 3000);
        boardHighlighter = new BoardHighlighter(rows, cols, this);
        boardHighlighterTimer = new Timer();
        boardHighlighterTimer.schedule(boardHighlighter, 500, 500);
        newGame(rows,cols);
        updateGUI();
    }

    public void makeAiStep(final AIPlayerStep aiPlayerStep){
        doFlip(aiPlayerStep.getCoordinates()[0], aiPlayerStep.getCoordinates()[1], movePiece, true);
        placePiece(aiPlayerStep.getCoordinates()[0], aiPlayerStep.getCoordinates()[1], movePiece);
        updateGUI();
        checkWin(darkScore, lightScore);
        computerTurn = false;
        swapTurns();
    }

    private void newGame(final int rows, final int cols) {
        this.rows = rows;
        this.cols = cols;
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                board[row][col] = Step.NONE;
            }
        }
        movePiece = Step.DARK;
        placePiece(rows/2 - 1,cols/2 - 1, Step.LIGHT); updateGUI();
        placePiece(rows/2 - 1,cols/2, Step.DARK); updateGUI();
        placePiece(rows/2,cols/2, Step.LIGHT); updateGUI();
        placePiece(rows/2,cols/2 - 1, Step.DARK); updateGUI();
        final MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(final MouseEvent e) {}
            @Override
            public void mouseMoved(final MouseEvent e) {
                if (computerTurn)
                    return;
                boardHighlighter.setMovePiece(movePiece);
                final int row = e.getX() / (592 / rows);
                final int col = e.getY() / (592 / cols);
                if (doFlip(row, col, movePiece, false) && board[row][col] == Step.NONE) {
                    addRespectivePics(row, col, "highlight");
                }
            }
        };
        boardPanel.addMouseMotionListener(mouseMotionListener);
        boardPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if(computerTurn) return;
                final int row = e.getX() / (592/rows);
                final int col = e.getY() / (592/cols);
                if(doFlip(row, col, movePiece, false)) {
                    doFlip(row, col, movePiece, true);
                    placePiece(row, col, movePiece);
                    updateGUI();
                    computerTurn = true;
                    checkWin(darkScore, lightScore);
                    swapTurns();
                }
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void placePiece(final int row, final int col, final Step color) {
        if(board[row][col] == Step.NONE)
            board[row][col] = color;
    }

    private void updateGUI() {
        darkScore = 0; lightScore = 0;
        for(int row = 0; row < rows; row ++) {
            for(int col = 0; col < cols; col++) {
                final JPanel panel = (JPanel)boardPanel.getComponent(coordToindex(row, col));
                panel.removeAll();
            }
        }
        for(int row = 0; row < rows; row ++) {
            for(int col = 0; col < cols; col++) {
                if(doFlip(row, col, movePiece, false) /*&& !computerTurn*/)
                    addRespectivePics(row, col, "transparent");

                if(board[row][col] == Step.DARK) {
                    addRespectivePics(row, col, Step.DARK.toString().toLowerCase());
                    darkScore++;
                    darkScoreLbl.setText("Dark : " + darkScore);
                }
                if(board[row][col] == Step.LIGHT) {
                    addRespectivePics(row, col, Step.LIGHT.toString().toLowerCase());
                    lightScore++;
                    lightScoreLbl.setText("Light : " + lightScore);
                }
            }
        }
    }

    public boolean doFlip(final int row, final int col, final Step piece, final boolean putDown) {
        boolean isValid = false;
        for(int dX = -1; dX < 2; dX++) {
            for(int dY = -1; dY < 2; dY ++) {
                if(dX == 0 && dY == 0) { continue; }
                final int checkRow = row + dX;
                final int checkCol = col + dY;
                if(checkRow >= 0 && checkCol >= 0 && checkRow < rows && checkCol < cols) {
                    if(board[checkRow][checkCol] == (piece == Step.DARK ? Step.LIGHT : Step.DARK)) {
                        for(int distance = 0; distance < rows; distance++) {
                            final int minorCheckRow = row+distance*dX;
                            final int minorCheckCol = col+distance*dY;
                            if(minorCheckRow < 0 || minorCheckCol < 0  || minorCheckRow > rows - 1 || minorCheckCol > cols - 1)
                                continue;
                            if(board[minorCheckRow][minorCheckCol] == piece) {
                                if(putDown) {
                                    for(int distance2 = 1; distance2 < distance; distance2 ++) {
                                        final int flipRow = row+distance2*dX;
                                        final int flipCol = col+distance2*dY;
                                        board[flipRow][flipCol] = piece;
                                    }
                                }
                                isValid = true; break;
                            }
                        }
                    }
                }
            }
        }
        return isValid;
    }

    public void addRespectivePics(final int row, final int col, final String colorName) {
        final ImageIcon picture = createImageIcon("images/" + colorName + ".png");
        final JLabel picLbl = new JLabel(picture);
        final JPanel panel = (JPanel)boardPanel.getComponent(coordToindex(row, col));
        panel.removeAll();
        panel.add(picLbl);
        boardPanel.updateUI();
    }

    private void itemStateChanged(final ItemEvent e)
    {
        if (e.getSource() == comboBox) {
            switch (comboBox.getSelectedIndex()){
                case 1:
                    setVisible(false);
                    reversi.dispose();
                    new Reversi(8,8);
                    break;
                case 2:
                    setVisible(false);
                    reversi.dispose();
                    new Reversi(10,10);
                    break;
                case 3:
                    setVisible(false);
                    reversi.dispose();
                    new Reversi(12,12);
                    break;
                default:
                    setVisible(false);
                    reversi.dispose();
                    new Reversi(6,6);
            }
        }
    }

    public void swapTurns() {
        movePiece = (movePiece == Step.DARK ? Step.LIGHT : Step.DARK);
        updateGUI();
        currentPlayerLbl.setText("Current Player :" + movePiece.toString());
        if (computerTurn) {
            aiPlayer.setMovePiece(movePiece);
            checkWin(darkScore, lightScore);
        }
    }

    private int coordToindex(final int row, final int col) {
        return (col * cols) + row;
    }

    private void stopTimers(){
        aiPlayerMoveTimer.cancel();
        boardHighlighterTimer.cancel();
    }

    private void checkWin(final int totalDark, final int totalLight) {     // counter the scores and return the winner
        if(totalDark + totalLight == rows * cols && totalDark > totalLight){
            display("Black Player Wins!");
            stopTimers();
        }
        if(totalDark + totalLight == rows * cols && totalLight > totalDark){
            display("White Player Wins!");
            stopTimers();
        }
        if(totalDark + totalLight == rows * cols && totalLight == totalDark) {
            display("It's a Tie!");
            stopTimers();
        }
        if (darkScore<=0) {
            display("White Player Wins!");
            stopTimers();
        }
        if (lightScore<=0) {
            display("Black Player Wins!");
            stopTimers();
        }
    }

    private void display(final String msg) {
        JOptionPane.showMessageDialog(null, msg, "Display",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private ImageIcon createImageIcon(final String path) {
        final URL imgURL = getClass().getClassLoader().getResource(path);
        if(imgURL != null) return new ImageIcon(imgURL);
        else {
            System.err.println("Couldn't find the file: " + path);
            return null;
        }
    }

    public Step[][] getBoard() {
        return board;
    }
}
