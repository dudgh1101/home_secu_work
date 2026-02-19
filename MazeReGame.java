
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MazeReGame extends JFrame {
    int[][] maze;
    GamePanel gamePanel;
    Player ai;
    boolean aiActive = true;
    Player pr;
    boolean prActive = true;

    Timer aiTimer;
    Timer prTimer;

    //리플레이 관련
    private static int Csize;
    private static char[] prMove;
    private static char[] aiMove;

    private static int nowPr = 0;
    private static int nowAi = 0;

    private static String prSc;
    private static String aiSc;


    // 방향 벡터
    final int[][] directions = {
            {-1, 0},  // UP 0
            {0, 1},   // RIGHT 1
            {1, 0},   // DOWN 2
            {0, -1}   // LEFT 3
    };

//플레이어랑 ai움직임 배열화 성공 다음부터는 배열에 따라 움직이는거랑 플레이어 무브 만들면 됨

    public MazeReGame(String filePath){

        try{
            loadReplay(filePath);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("파일 불러오기 실패");
        }

        setTitle("AI 미로찾기 테스트");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPlayers();
        initTimer();

        gamePanel = new GamePanel();
        add(gamePanel);

        aiTimer.start();
        prTimer.start();
        setVisible(true);
    }

    public void loadReplay(String fileName) throws IOException {
        try {
            // ✅ 1. 파일 읽기 (한 줄로 저장되어 있다고 가정)
            String data = Files.readString(Paths.get(fileName));

            if(data.isEmpty()) throw new IOException("파일이 비어있습니다.");

            // ✅ 2. 파싱
            String[] parts = data.split("e");

            // 파싱 테스트
            for(String de : parts){
                System.out.println(de);
            }

            // ✅ 3. 크기
            int size = Integer.parseInt(parts[0]);
            Csize = size;


            // ✅ 4. 미로 배열
            int[][] mazeArray = new int[size][size];
            String mazeStr = parts[1];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    mazeArray[i][j] = mazeStr.charAt(i * size + j) - '0';
                }
            }
            this.maze = mazeArray;

            // ✅ 5. 이동 배열
            char[] movesArrayP = parts[2].toCharArray();
            char[] movesArrayA = parts[3].toCharArray();
            aiMove = movesArrayA;
            prMove = movesArrayP;

            System.out.println("parts.length : "+parts.length);

            //게임 시간 기록
            prSc = parts[4];
            aiSc = parts[5];

//            System.out.println("이동배열");
//            for (char mo : movesArrayA){
//                System.out.print(mo+" ");
//            }
            System.out.println();
            for (char mo : movesArrayP){
                System.out.print(mo+" ");
            }


            // ✅ 6. 확인 출력
            System.out.println("\n 크기: " + size);
//            System.out.println("미로 배열:");
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    System.out.print(mazeArray[i][j]);
//                }
//                System.out.println();
//            }



        } catch (IOException e) {
            System.out.println("파일 읽기 실패: " + e.getMessage());
        }
    }

    // AI 초기화 (첫 번째 스타트 지점에 배치)
    void initPlayers(){
        int count = 0;

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 0) {
                    if(count==0){
                        pr = new Player(i, j, 1);
                        maze[i][j] = 1;  // AI 표시
                        System.out.println("PR 시작: (" + i + ", " + j + ")");
                        count++;
                    }
                    else{
                        ai = new Player(i, j, 2);
                        maze[i][j] = 2;  // 플레이어 표시
                        System.out.println("AI 시작: (" + i + ", " + j + ")");
                    }
                }
            }
        }
    }

    // 타이머 초기화
    void initTimer(){
        aiTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAI();  // ← 여기에 AI 알고리즘 구현!
                nowAi++;
                gamePanel.repaint();
            }
        });

        prTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePR();
                nowPr++;
                gamePanel.repaint();
            }
        });


    }

    // ========== AI 이동 로직 (여기를 수정!) ==========

    void moveAI() {
        int dir = 0;

        switch (aiMove[nowAi]){
            case '↑' :
                break;
            case '→' :
                dir = 1;
                break;
            case '↓' :
                dir = 2;
                break;
            case '←' :
                dir = 3;
                break;
            case '6':
                System.out.println("아이템 발동");
                return;

        }


        ai.setDiraction(dir);


        int newRow = ai.getRow() + directions[dir][0];
        int newCol = ai.getCol() + directions[dir][1];

        if(canMove(newRow,newCol)){
            maze[ai.getRow()][ai.getCol()] = 3;

            ai.setRow(newRow);
            ai.setCol(newCol);

            if (maze[newRow][newCol]==9) {
                aiTimer.stop();
                System.out.println("ai도착");
//                JOptionPane.showMessageDialog(this, "AI 도착!");
                aiActive=false;
                chekGameEnd();
                return;
            }

            maze[newRow][newCol] = 2;
        }

//        System.out.println("AI 위치: (" + newRow + ", " + newCol + "), 방향: " + dir);
    }

    void movePR() {
        int dir = 0;

        switch (prMove[nowPr]){
            case '↑' :
                break;
            case '→' :
                dir = 1;
                break;
            case '↓' :
                dir = 2;
                break;
            case '←' :
                dir = 3;
                break;
            case '6':
                System.out.println("아이템 발동");
                return;

            case '5':
                return;

        }

        pr.setDiraction(dir);


        int newRow = pr.getRow() + directions[dir][0];
        int newCol = pr.getCol() + directions[dir][1];

        if(canMove(newRow,newCol)){
            maze[pr.getRow()][pr.getCol()] = 3;

            pr.setRow(newRow);
            pr.setCol(newCol);

            if (maze[newRow][newCol]==9) {
                prTimer.stop();
                System.out.println("pr도착");
                prActive = false;
                chekGameEnd();
                return;
            }

            maze[newRow][newCol] = 1;
        }

//        System.out.println("pr 위치: (" + newRow + ", " + newCol + "), 방향: " + dir);
    }

    // 이동 가능 여부
    boolean canMove(int row, int col){
        if (row < 0 || row >= maze.length || col < 0 || col >= maze[0].length) {
            return false;
        }
        if (maze[row][col] == 4) {  // 벽
            return false;
        }
        return true;
    }

    void chekGameEnd(){
        if (!aiActive && !prActive){
            JOptionPane.showMessageDialog(this,"리플레이가 종료되었습니다.");
            JOptionPane.showMessageDialog(this,"플레이어 시간 : "+prSc+", Ai 게임시간 : "+aiSc);
            new EndScreen<String>(prSc,aiSc);
            this.dispose();
        }
    }


    // ========== 화면 표시 ==========

    class GamePanel extends JPanel{
        final int CELL_SIZE = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    int x = j * CELL_SIZE;
                    int y = i * CELL_SIZE;

                    // 색상 지정
                    switch (maze[i][j]) {
                        case 0: g.setColor(Color.GREEN); break;   // 스타트
                        case 1: g.setColor(Color.BLUE); break;   // 플레이어
                        case 2: g.setColor(Color.ORANGE); break;  // AI
                        case 3: g.setColor(Color.WHITE); break;   // 길
                        case 4: g.setColor(Color.BLACK); break;  // 벽
                        case 5:g.setColor(Color.GREEN); break; // 트랩
                        case 6: g.setColor(Color.YELLOW); break;  // 아이템
                        case 9: g.setColor(Color.RED); break;     // 도착
                        default: g.setColor(Color.GRAY);
                    }

                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
            // ✅ AI 방향 숫자 표시
            if(aiActive){
                int x = ai.getCol() * CELL_SIZE;
                int y = ai.getRow() * CELL_SIZE;

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(String.valueOf(ai.getDiraction()), x + 18, y + 32);
            }
        }
    }

//    static void main() {
//
//        String a = "/Users/user/Desktop/secu_project/src/test/game_save1.txt";
//
//        int[][] b = null;
//
//
//        new MazeReGame(a);
//
//    }//main



}