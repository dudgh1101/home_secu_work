

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MazeLoader {

    /**
     * 텍스트 파일에서 미로를 읽어옵니다
     *
     2 D:\secu_extend\secu_exten\src\secu\maze.txt
     *
     */
    public static int[][] loadFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        if (lines.isEmpty()) {
            throw new IOException("파일이 비어있습니다!");
        }

        int size = lines.size();
        int[][] maze = new int[size][size];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // 공백 제거
            line = line.replace(" ", "");

            if (line.length() != size) {
                throw new IOException("미로가 정사각형이 아닙니다!");
            }

            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                maze[i][j] = c - '0';  // 문자를 숫자로 변환
            }
        }

        return maze;
    }
}