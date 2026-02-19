

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MazeGame extends JFrame {
    int[][] maze;
    GamePanel gamePanel;

    // 플레이어 객체로 변경
    Player player1;
    Player player2;  // AI

    // 타이머들
    Timer trapTimer;
    Timer gameTimer;      // 게임 시간 측정
    Timer aiTimer;        // AI 자동 이동
    Timer itemTimer;

    int gameSeconds = 0;  // 경과 시간
    int aiGameSeconds = 0;

    //리플레이를 위한 버퍼 and 파일을 저장할 경로
    private final StringBuffer buffer = new StringBuffer();
    private final String path = "D:\\secu_extend\\secu_exten\\src\\secu\\all_log\\game_save";
    private int file_count = 0;
    private final String txt = ".txt";
    private  Path filePath = Paths.get(path + txt);


    final int[][] directions = {
            {-1, 0},  // UP 0
            {0, 1},   // RIGHT 1
            {1, 0},   // DOWN 2
            {0, -1}   // LEFT 3
    };

    private static StringBuffer pr_buffer = new StringBuffer();
    private static StringBuffer ai_buffer = new StringBuffer();

    public MazeGame(int[][] mazeData){
        this.maze = mazeData;
        inputBuffer(mazeData);

        setTitle("미로 찾기 게임");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPlayers();    // 플레이어 2명 초기화
        initTimers();     // 타이머 초기화

        gamePanel = new GamePanel();
        add(gamePanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // 게임 시작!
        gameTimer.start();
        aiTimer.start();

        setVisible(true);
    }

    void inputBuffer(int[][] maze){
        buffer.append(maze.length);
        buffer.append("e");
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                buffer.append(maze[i][j]);
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }

        buffer.append("e");
    }

    void chekFile(Path file){

    }

    // 플레이어 2명 초기화
    void initPlayers(){
        int startCount = 0;

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 0) {
                    if (startCount == 0) {
                        // 첫 번째 스타트 지점 → 플레이어1
                        player1 = new Player(i, j, 1);
                        maze[i][j] = 1;
                        startCount++;
                    } else if (startCount == 1) {
                        // 두 번째 스타트 지점 → 플레이어2 (AI)
                        player2 = new Player(i, j, 2);
                        maze[i][j] = 2;
                        startCount++;
                        return;
                    }
                }
            }
        }
    }

    // 타이머 초기화
    void initTimers() {
        // 게임 시간 타이머 (1초마다)
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameSeconds++;
                System.out.println("게임 시간: " + gameSeconds + "초");
            }
        });

        // AI 이동 타이머 (0.5초마다)
        aiTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiGameSeconds++;
                if (!player2.isArrived()) {
                    moveAI();  // AI 자동 이동
                    gamePanel.repaint();
                }
            }
        });

        trapTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean anyActive = false;

                if(player1.isHastrap()){
                    player1.setTrapcountdown(player1.getTrapcountdown()-1);
                    System.out.println("P1 트랩해제까지 남은 시간: " + player1.getItemTimeLeft());

                    if (player1.getTrapcountdown() <= 0) {
                        player1.setHastrap(false);
                        System.out.println("P1 트랩헤제");
                    } else {
                        anyActive = true;
                    }
                }

                if(player2.isHastrap()){
                    player2.setTrapcountdown(player2.getTrapcountdown()-1);
                    System.out.println("P2 트랩해제까지 남은 시간: " + player2.getItemTimeLeft());

                    if (player2.getTrapcountdown() <= 0) {
                        player2.setHastrap(false);
                        System.out.println("P2 트랩헤제");
                    } else {
                        anyActive = true;
                    }
                }

                // 둘 다 효과 없으면 타이머 정지
                if (!anyActive) {
                    trapTimer.stop();
                }

                gamePanel.repaint();


            }
        });

        itemTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean anyActive = false;

                // 플레이어1 체크
                if (player1.isItemActive()) {
                    player1.setItemTimeLeft(player1.getItemTimeLeft() - 1);
                    System.out.println("P1 아이템 남은 시간: " + player1.getItemTimeLeft());

                    if (player1.getItemTimeLeft() <= 0) {
                        player1.setItemActive(false);
                        player1.setVisionRange(1);
                        System.out.println("P1 아이템 효과 종료!");
                    } else {
                        anyActive = true;
                    }
                }

                // 플레이어2(AI) 체크
                if (player2.isItemActive()) {
                    player2.setItemTimeLeft(player2.getItemTimeLeft() - 1);
                    System.out.println("P2 아이템 남은 시간: " + player2.getItemTimeLeft());

                    if (player2.getItemTimeLeft() <= 0) {
                        player2.setItemActive(false);
                        player2.setVisionRange(1);
                        System.out.println("P2 아이템 효과 종료!");
                    } else {
                        anyActive = true;
                    }
                }

                // 둘 다 효과 없으면 타이머 정지
                if (!anyActive) {
                    itemTimer.stop();
                }

                gamePanel.repaint();
            }

        });
    }

    // AI 이동 로직 (일단 랜덤으로)
    void moveAI() {
        int dir = player2.getDiraction();

        int right = (dir + 1) % 4;
        int left  = (dir + 3) % 4;
        int back  = (dir + 2) % 4;

        if (moveToPosition(right)) {
            dir = right;
        } else if (moveToPosition(dir)) {
            // 그대로
        } else if (moveToPosition(left)) {
            dir = left;
        } else if (moveToPosition(back)) {
            dir = back;
        } else {
            aiTimer.stop();
            System.out.println("사방이 막힘");
            return;
        }
        player2.setDiraction(dir);

        switch (dir){
            case 0:
                ai_buffer.append("↑");
                break;
            case 1:
                ai_buffer.append("→");
                break;
            case 2:
                ai_buffer.append("↓");
                break;
            case 3:
                ai_buffer.append("←");
                break;
        }

        int newRow = player2.getRow() + directions[dir][0];
        int newCol = player2.getCol() + directions[dir][1];

        maze[player2.getRow()][player2.getCol()] = 3;

        player2.setRow(newRow);
        player2.setCol(newCol);

        //아이템 체크
        if (maze[newRow][newCol] == 6) {
            ai_buffer.append("6");
            activateItem(player2);  // 아이템 효과 발동
        }
        if (maze[newRow][newCol] == 2) {
            ai_buffer.append("2");
        }

        //도착 체크
        if (maze[newRow][newCol] == 9) {
            ai_buffer.append("e");
            player2.setArrived(true);
            player2.setFinishTime(gameSeconds);
            System.out.println("AI도착: "+aiGameSeconds+"초");
            aiTimer.stop();
            checkGameEnd();
            return;
        }

        // 새 위치를 플레이어2로
        maze[newRow][newCol] = 2;

    }
    boolean moveToPosition(int newDir) {

        int newRow = player2.getRow() + directions[newDir][0];
        int newCol = player2.getCol() + directions[newDir][1];
        return canMove(newRow, newCol);


    }

    // 플레이어1 키보드 입력 처리
    void handleKeyPress(int keyCode) {
        if (player1.isArrived()) return;  // 이미 도착했으면 무시
        if (player1.isHastrap()) return; //트랩에 걸린 상태면 무시

        int newRow = player1.getRow();
        int newCol = player1.getCol();

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newRow--;
                pr_buffer.append("↑");
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newRow++;
                pr_buffer.append("↓");
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newCol--;
                pr_buffer.append("←");
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newCol++;
                pr_buffer.append("→");
                break;
            case KeyEvent.VK_R:
                activateItem(player2);
                trapTimer.stop();
                break;
            default:
                return;
        }

        if(canMove(newRow, newCol)){
        //아이템
            if (maze[newRow][newCol] == 6) {
                pr_buffer.append("6");
                activateItem(player1);
            }
//            트랩
            if (maze[newRow][newCol] == 5) {
                pr_buffer.append("5");
                activeTrap(player1);
            }
            if (maze[newRow][newCol] == 2) {
                pr_buffer.append("2");

            }

            maze[player1.getRow()][player1.getCol()] = 3;

            player1.setRow(newRow);
            player1.setCol(newCol);

            if(maze[newRow][newCol] == 9){
                pr_buffer.append("e");
                player1.setArrived(true);
                player1.setFinishTime(gameSeconds);
                JOptionPane.showMessageDialog(this, "플레이어1 도착 시간: "+ gameSeconds);
                checkGameEnd();
                return;
            }

            maze[newRow][newCol] = 1;
            gamePanel.repaint();
        }
    }

    // 게임 종료 체크
    void checkGameEnd() {
        if (player1.isArrived() && player2.isArrived()) {
            gameTimer.stop();
            aiTimer.stop();

            String message = "게임 종료!\n\n";
            message += "플레이어1 시간: " + player1.getFinishTime() + "초\n";
            message += "플레이어2(AI) 시간: " + player2.getFinishTime() + "초\n\n";

            if (player1.getFinishTime() < player2.getFinishTime()) {
                message += "플레이어1 승리! 🎉";
            } else if (player1.getFinishTime() > player2.getFinishTime()) {
                message += "플레이어2(AI) 승리! 🤖";
            } else {
                message += "무승부!";
            }

            buffer.append(pr_buffer.toString());
            buffer.append(ai_buffer.toString());
            buffer.append(player1.getFinishTime()+"e");
            buffer.append(player2.getFinishTime()+"e");


            while (Files.exists(filePath)){
                filePath = Paths.get(path + file_count + txt);
                file_count++;
            }

            try {
                Files.writeString(filePath,buffer.toString());

            }catch (Exception e){
                System.out.println(e.getMessage());
            }


            this.dispose();
            new EndScreen(player1.getFinishTime(),player2.getFinishTime());

            JOptionPane.showMessageDialog(this, message);
        }
    }

    boolean canMove(int row, int col){
        if (row < 0 || row > maze.length || col < 0 || col > maze[0].length) {
            return false;
        }

        if (maze[row][col] == 4) {
            return false;
        }

        // 다른 플레이어와 충돌 방지
        if (maze[row][col] == 1 || maze[row][col] == 2) {
            return false;
        }

        return true;
    }


    void activeTrap(Player player){
        System.out.println("트랩을 밟았어요");

        player.hastrap = true;
        player.trapcountdown = 5;

        if (!trapTimer.isRunning()){
            trapTimer.start();
        }
    }

    void activateItem(Player player){
        System.out.println("아이템 획득");

        player.hasItem = true;
        player.countdown =5;
        player.range = 2;

        if (!itemTimer.isRunning()) {
            itemTimer.start();
        }
    }

    boolean isInFogRange(int row, int col, Player player) {
        int rowDiff = Math.abs(row - player.getRow());
        int colDiff = Math.abs(col - player.getCol());
        return rowDiff <= player.getVisionRange() && colDiff <= player.getVisionRange();
    }

    class GamePanel extends JPanel{
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // 미로 크기를 창 크기에 맞춤 (정사각형 셀 유지)
            int cellSize = Math.min(panelWidth / (maze[0].length + 3), panelHeight / (maze.length + 2));

            // 미로 실제 픽셀 크기
            int mazePixelWidth = cellSize * maze[0].length;
            int mazePixelHeight = cellSize * maze.length;

            // 미로를 중앙에 배치
            int mazeStartX = (panelWidth - mazePixelWidth) / 2;
            int mazeStartY = (panelHeight - mazePixelHeight) / 2 - 20; // 상태UI 공간만큼 위로

            // UI 영역을 미로 아래에 배치 (겹침 방지)
            int uiHeight = 180;
            int uiWidth = 200;
            int uiX = (panelWidth - uiWidth) / 2;
            int uiY = mazeStartY + mazePixelHeight + 10;

            // 미로 그리기
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    int x = mazeStartX + j * cellSize;
                    int y = mazeStartY + i * cellSize;

                    boolean isVisible = isInFogRange(i, j, player1)
                            || isInFogRange(i, j, player2);

                    if(!isVisible){
                        g.setColor(new Color(100, 100, 100));
                        g.fillRect(x, y, cellSize, cellSize);
                        g.setColor(Color.GRAY);
                        g.drawRect(x, y, cellSize, cellSize);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, cellSize / 3));
                        g.drawString("?", x + cellSize/3, y + cellSize/2 + 5);
                    } else {
                        switch (maze[i][j]) {
                            case 0: g.setColor(Color.GREEN); break;
                            case 1: g.setColor(Color.BLUE); break;
                            case 2: g.setColor(Color.ORANGE); break;
                            case 3: g.setColor(Color.WHITE); break;
                            case 4: g.setColor(Color.BLACK); break;
                            case 5: g.setColor(Color.GREEN); break;
                            case 6: g.setColor(Color.YELLOW); break;
                            case 9: g.setColor(Color.RED); break;
                            default: g.setColor(Color.GRAY);
                        }

                        g.fillRect(x, y, cellSize, cellSize);
                        g.setColor(Color.GRAY);
                        g.drawRect(x, y, cellSize, cellSize);
                    }
                }
            }
            // 상태 UI 배경 (미로 아래 중앙)
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(uiX, uiY, uiWidth, uiHeight);
            g.setColor(Color.WHITE);
            g.drawRect(uiX, uiY, uiWidth, uiHeight);

            // 상태 텍스트
            g.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            int textX = uiX + 15;
            int textY = uiY + 25;
            int lineHeight = 20;

            g.drawString("플레이어: " + gameSeconds + "초", textX, textY);
            g.drawString("AI: " + aiGameSeconds + "초", textX, textY + lineHeight);
            g.drawString("P1: " + (player1.isArrived() ? "도착!" : "진행중"), textX, textY + lineHeight * 2);
            g.drawString("AI: " + (player2.isArrived() ? "도착!" : "진행중"), textX, textY + lineHeight * 3);

            if (player1.isItemActive()) {
                g.setColor(Color.YELLOW);
                g.drawString("아이템: " + player1.getItemTimeLeft() + "초", textX, textY + lineHeight * 4);
                g.setColor(Color.WHITE);
            }
            if (player1.isHastrap()) {
                g.setColor(Color.RED);
                g.drawString("트랩: " + player1.getTrapcountdown() + "초", textX, textY + lineHeight * 5);
            }
        }

    }

}