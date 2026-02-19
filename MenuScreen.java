
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MenuScreen extends JFrame {

    public MenuScreen() {
        setTitle("미로 찾기 게임 - 메인 메뉴");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        // 제목
        JLabel titleLabel = new JLabel("🎮 미로 찾기 게임", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel);

        // 버튼 1: 새 미로 생성
        JButton newMazeBtn = new JButton("새 미로 생성하고 시작");
        newMazeBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        newMazeBtn.addActionListener(e -> startNewMaze());
        add(newMazeBtn);

        // 버튼 2: 파일 불러오기
        JButton newFileBtn = new JButton("파일로드하고 시작");
        newFileBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        newFileBtn.addActionListener(e -> loadFromFile());
        add(newFileBtn);

        //버튼 3: 리플레이 보러가기

        JButton replayButton = new JButton("리플레이");
        replayButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        replayButton.addActionListener(e -> startReplayButton());
        add(replayButton);

        // 버튼 4: 종료
        JButton exitBtn = new JButton("종료");
        exitBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        exitBtn.addActionListener(e -> System.exit(0));
        add(exitBtn);



        setVisible(true);
    }

    // 새 미로 생성 버튼 클릭
    void startNewMaze() {
        // 미로 크기 입력받기
        String input = JOptionPane.showInputDialog(this,
                "미로 크기를 입력하세요 (최소 5):", "10");

        if (input != null) {
            try {
                int size = Integer.parseInt(input);
                if (size < 5) {
                    JOptionPane.showMessageDialog(this, "크기는 최소 5 이상이어야 합니다!");
                    return;
                }

                // 제너레이터로 미로 생성
                int[][] maze = null;
                Generator generator1 = new Generator(size,size,100);
                maze = generator1.newGenerator();

                // 게임 시작
                this.dispose();  // 메뉴 화면 닫기
                new MazeGame(maze);  // 게임 시작

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자를 입력해주세요!");
            }
        }
    }

    void startReplayButton(){
        this.dispose();
        new ReplayScreen();
    }

    //파일에서 불러오기버튼
    void loadFromFile() {
        String filePath = JOptionPane.showInputDialog(this,
                "파일 경로를 입력하세요:", "maze.txt");

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                int[][] maze = MazeLoader.loadFromFile(filePath);

                // 게임 시작
                this.dispose();  // 메뉴 화면 닫기
                new MazeGame(maze);  // 게임 시작

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "파일 읽기 실패: " + ex.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        new MenuScreen();
    }
}