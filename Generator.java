
/*

현재 좀더 강화된 생성규칙 구현 완료
앞으로 구현할 것 -> 벽 두르기 , 범위 안넘어가면서 강화규칙 유지하기

길을 배치할때 일정 확률로 플레이어 배치 플레이어수++

*
*
* */


import javax.swing.*;

import java.awt.*;
import java.util.Random;

public class Generator extends JFrame {
    //아이템
    private final Random random = new Random();
    private int placed = 0;
    private int attempts = 0;
    //트랩
    private int trapPlaced = 0;
    private int trapAttempts = 0;
    //시작지점
    private int startPlaced = 0;
    private int startAttempts = 0;
    //종료 지점
    private int endPlaced = 0;
    private int endAttempts = 0;

    private final Container container = getContentPane();
    private final JPanel[][] cells;
    private final int rows;
    private final int cols;
    private final boolean[][] maze;
    private final int[][] testMaze;
    private final int[][] directions = {
            {1, 0},  // Down
            {0, 1},  // Right
            {-1, 0}, // Up
            {0, -1}  // Left
    };

    public Generator(int rows, int cols, int cellSize) {
        this.rows = rows;
        this.cols = cols;
        this.testMaze = new int[rows][cols];
        this.maze = new boolean[rows][cols];
        this.cells = new JPanel[rows][cols];

        setTitle("Maze Generator");
        setSize(cols * cellSize, rows * cellSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        init();
        generateMaze();
    }

    public int[][] newGenerator(){
        return testMaze;
    }

    private void init() {
        container.setLayout(new GridLayout(rows, cols));
        container.setBackground(Color.BLACK);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cells[row][col] = new JPanel();
                container.add(cells[row][col]);
                cells[row][col].setBackground(Color.BLACK);
            }
        }
        //전부 벽으로 초기화
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                testMaze[row][col] = 4;
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.out.print(testMaze[row][col]);
            }
            System.out.println();
        }

        container.revalidate();
        container.repaint();
    }

    private void paintCellAndRefresh(int row, int col, Color color) {
        cells[row][col].setBackground(color);
        if(color == Color.RED){
            testMaze[row][col] = 9;
        } else if (color == Color.GREEN) {
            testMaze[row][col] = 0;
        }
        else if (color == Color.WHITE) {
            testMaze[row][col] = 3;
        }

        container.revalidate();
        container.repaint();
    }

    private void generateMaze() {
        dfs(0, 0); // DFS 알고리즘으로 미로 생성
        placeStart(testMaze,rows,2);
        placeItems(testMaze,rows,3);
        placeTrap(testMaze,rows,3);
        placeEnd(testMaze,rows,1);
    }

    private void dfs(int x, int y) {
        maze[x][y] = true;
        testMaze[x][y] =3;
        shuffleArray(directions);

        for (int[] direction : directions) {

            /*
                nx, ny를 1칸 바로 옆의 칸으로 설정하면 미로의 벽이 없어짐
                nx, ny를 2칸 다음의 칸으로 설정하여 벽이 없어지는 것을 방지
             */

        int nx = x + direction[0] * 2;
        int ny = y + direction[1] * 2;

            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !maze[nx][ny]) {
            maze[nx][ny] = true;
            maze[x + direction[0]][y + direction[1]] = true;

            try {
                Thread.sleep(1); // 미로가 생성되는 속도를 조절
                }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

// 2칸 떨어진 다음 칸과 그 사이 칸을 칠함
            paintCellAndRefresh(nx, ny, Color.WHITE);
            paintCellAndRefresh(x + direction[0], y + direction[1], Color.WHITE);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    System.out.print(testMaze[row][col]);
                }
                System.out.println();
                }


                dfs(nx, ny);
            }
                    }
                    }

    private void placeItems(int[][] maze, int size, int count) {

        while (placed < count && attempts < rows*cols) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3) {  // 길 위에만 배치
                maze[x][y] = 6;  // 아이템
                System.out.println("아이템 배치");
                placed++;
            }
            attempts++;
        }
    }
    private void placeStart(int[][] maze, int size, int count) {

        while (startPlaced < count && startAttempts < rows*cols) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3) {  // 길 위에만 배치
                maze[x][y] = 0;  // 시작
                paintCellAndRefresh(x, y, Color.GREEN);
                System.out.println("시작지점 배치");
                startPlaced++;
            }
            startAttempts++;
        }
    }
    private void placeEnd(int[][] maze, int size, int count) {

        while (endPlaced < count && endAttempts < rows*cols) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3) {  // 길 위에만 배치
                maze[x][y] = 0;  // 시작
                paintCellAndRefresh(x, y, Color.RED);
                System.out.println("도착지점 배치");
                endPlaced++;
            }
            endAttempts++;
        }
    }

    private void placeTrap(int[][] maze, int size, int count) {


        while (trapPlaced < count && trapAttempts < rows*cols) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3 || maze[x][y] == 6) {  //아이템이나 길 위에만 배치
                maze[x][y] = 5;  // 트랩
                System.out.println("트랩 배치");
                trapPlaced++;
            }
            trapAttempts++;
        }
    }
    public void printMaze(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.out.print(testMaze[row][col]);
            }
            System.out.println();
        }
    }

// 방향 배열을 랜덤하게 섞음
private void shuffleArray(int[][] arr) {
    for (int i = 0; i < arr.length; i++) {
        int j = (int) (Math.random() * (i + 1));
        int[] temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

// 화면 크기 조절 시 화면을 다시 그림
@Override
public void paint(Graphics g) {
    super.paint(g);
}

public static void main(String[] args) {
    Generator generator = new Generator(10, 10, 50);
//    System.out.println("testMaze");
//    generator.printMaze();
    }
}