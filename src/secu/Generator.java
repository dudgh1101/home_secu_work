package secu;

import javax.swing.*;

import java.awt.*;

public class Generator extends JFrame {
    private final Container container = getContentPane();
    private final JPanel[][] cells;
    private final int rows;
    private final int cols;
    private final boolean[][] maze;
    private final int[][] directions = {
            {1, 0},  // Down
            {0, 1},  // Right
            {-1, 0}, // Up
            {0, -1}  // Left
    };

    public Generator(int rows, int cols, int cellSize) {
        this.rows = rows;
        this.cols = cols;
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

        container.revalidate();
        container.repaint();
    }

    private void paintCellAndRefresh(int row, int col, Color color) {
        cells[row][col].setBackground(color);
        container.revalidate();
        container.repaint();
    }

    private void generateMaze() {
        paintCellAndRefresh(0, 0, Color.RED); // 시작점 표시 (빨간색)
        dfs(0, 0); // DFS 알고리즘으로 미로 생성
        paintCellAndRefresh(rows - 2, cols - 2, Color.GREEN); // 도착점 표시 (초록색)
    }

    private void dfs(int x, int y) {
        maze[x][y] = true;
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 2칸 떨어진 다음 칸과 그 사이 칸을 칠함
                paintCellAndRefresh(nx, ny, Color.WHITE);
                paintCellAndRefresh(x + direction[0], y + direction[1], Color.WHITE);

                dfs(nx, ny);
            }
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
        new Generator(10, 10, 100);
    }
}