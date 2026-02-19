
import java.util.Random;

/**
 * 미로를 자동으로 생성하는 클래스
 */
public class MazeGenerator {
    private final Random random;

    public MazeGenerator() {
        this.random = new Random();
    }

    /**
     * N × N 크기의 미로를 생성합니다
     */
    public int[][] generateMaze(int size) {
        if (size < 5) size = 5;

        int[][] maze = new int[size][size];

        // 1단계: 모든 칸을 벽(4)으로 초기화
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = 4;
            }
        }

        // 2단계: 테두리는 벽, 안쪽은 60% 확률로 길(3) 생성
        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {
                if (random.nextDouble() < 0.6) {
                    maze[i][j] = 3;  // 길
                }
            }
        }

        // 3단계: 스타트 지점 2개 배치
        int[] start1 = {1, size / 2};  // 위쪽 중앙
        int[] start2 = {size - 2, size / 2};  // 아래쪽 중앙

        maze[start1[0]][start1[1]] = 0;
        maze[start2[0]][start2[1]] = 0;

        // 스타트 주변을 길로 만들기
        makePathAround(maze, start1[0], start1[1]);
        makePathAround(maze, start2[0], start2[1]);

        // 4단계: 도착지점 배치 (중앙 근처)
        int goalX = size / 2;
        int goalY = size / 2;
        maze[goalX][goalY] = 9;
        makePathAround(maze, goalX, goalY);

        // 5단계: 스타트에서 도착까지 경로 보장
        createPath(maze, start1[0], start1[1], goalX, goalY);
        createPath(maze, start2[0], start2[1], goalX, goalY);

        // 6단계: 아이템 배치 (2~3개)
        placeItems(maze, size, 3);
        placeTrap(maze,size,2);

        return maze;
    }

    /**
     * 특정 위치 주변을 길로 만듭니다
     */
    private void makePathAround(int[][] maze, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx > 0 && nx < maze.length - 1 && ny > 0 && ny < maze[0].length - 1) {
                    if (maze[nx][ny] != 0 && maze[nx][ny] != 9) {
                        maze[nx][ny] = 3;  // 길
                    }
                }
            }
        }
    }

    /**
     * 두 지점 사이에 경로를 생성합니다
     */
    private void createPath(int[][] maze, int startX, int startY, int goalX, int goalY) {
        int currentX = startX;
        int currentY = startY;

        while (!(currentX == goalX && currentY == goalY)) {
            // 목표 방향으로 한 칸씩 이동
            int dx = Integer.compare(goalX, currentX);
            int dy = Integer.compare(goalY, currentY);

            // 랜덤하게 x 또는 y 방향으로 이동
            if (dx != 0 && (dy == 0 || random.nextDouble() < 0.5)) {
                currentX += dx;
            } else if (dy != 0) {
                currentY += dy;
            }

            // 범위 체크
            if (currentX > 0 && currentX < maze.length - 1 &&
                    currentY > 0 && currentY < maze[0].length - 1) {
                if (maze[currentX][currentY] != 0 && maze[currentX][currentY] != 9) {
                    maze[currentX][currentY] = 3;  // 길로 만들기
                }
            }
        }
    }

    /**
     * 아이템을 랜덤하게 배치합니다
     */
    private void placeItems(int[][] maze, int size, int count) {
        int placed = 0;
        int attempts = 0;

        while (placed < count && attempts < 100) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3) {  // 길 위에만 배치
                maze[x][y] = 6;  // 아이템
                placed++;
            }
            attempts++;
        }
    }

    private void placeTrap(int[][] maze, int size, int count) {
        int placed = 0;
        int attempts = 0;

        while (placed < count && attempts < 100) {
            int x = 1 + random.nextInt(size - 2);
            int y = 1 + random.nextInt(size - 2);

            if (maze[x][y] == 3 || maze[x][y] == 6) {  //아이템이나 길 위에만 배치
                maze[x][y] = 5;  // 아이템
                placed++;
            }
            attempts++;
        }
    }
}