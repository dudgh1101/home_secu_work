
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReplayScreen extends JFrame {


    private String filePath = "D:\\secu_extend\\secu_exten\\src\\secu\\all_log\\";


    public ReplayScreen() {
        setTitle("리플레이 목록");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단: 제목
        JLabel titleLabel = new JLabel("게임 리플레이", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 중앙: 리플레이 파일 목록 또는 "파일이 없습니다"
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        if (hasReplayFiles()) {
            // 리플레이 파일이 있으면 리스트 표시
            showReplayList(centerPanel);
        } else {
            // 파일이 없으면 메시지 표시
            JLabel noFileLabel = new JLabel("저장된 리플레이가 없습니다", SwingConstants.CENTER);
            noFileLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
            noFileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(noFileLabel);
            centerPanel.add(Box.createVerticalGlue());
        }

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 하단: 메뉴로 돌아가기 버튼
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backButton = new JButton("메뉴로 돌아가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        backButton.addActionListener(e -> backToMenu());
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // 리플레이 파일 존재 여부 확인

    // ReplayScreen.java 수정 (디버깅 버전)

    boolean hasReplayFiles() {
        File dir = new File("D:\\secu_extend\\secu_exten\\src\\secu\\all_log");
        File[] files = dir.listFiles((d, name) -> name.startsWith("game_save") && name.endsWith(".txt"));

        // ✅ 디버깅: 찾은 파일 출력
        System.out.println("=== hasReplayFiles 결과 ===");
        if (files != null) {
            System.out.println("총 파일 개수: " + files.length);
            for (File f : files) {
                System.out.println("- " + f.getName());
            }
        } else {
            System.out.println("files가 null!");
        }

        return files != null && files.length > 0;
    }

    void showReplayList(JPanel panel) {
        File dir = new File("D:\\secu_extend\\secu_exten\\src\\secu\\all_log\\");
        File[] files = dir.listFiles((d, name) -> name.startsWith("game_save") && name.endsWith(".txt"));

        // ✅ 디버깅: 찾은 파일 출력
        System.out.println("=== showReplayList 결과 ===");
        if (files != null) {
            System.out.println("총 파일 개수: " + files.length);
            for (File f : files) {
                System.out.println("- " + f.getName());
            }
        } else {
            System.out.println("files가 null!");
        }

        if (files != null) {
            panel.add(Box.createVerticalStrut(10));

            for (File file : files) {
                System.out.println("버튼 생성: " + file.getName());  // ✅ 추가

                JButton fileButton = new JButton(file.getName());
                fileButton.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                fileButton.setMaximumSize(new Dimension(350, 40));
                fileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                fileButton.addActionListener(e -> playReplay(file.getName()));

                panel.add(fileButton);
                panel.add(Box.createVerticalStrut(10));
            }
        }
    }

    // 리플레이 재생 (나중에 구현)
    void playReplay(String fileName) {
//        JOptionPane.showMessageDialog(this, "리플레이 재생: " + fileName);
//        JOptionPane.showMessageDialog(this, "리플레이 경로: " + filePath + fileName);
        this.dispose();
        new MazeReGame(filePath+fileName);
//        -> all_loggame_save.txt라고 출려됨
        // 리플레이 재생 화면으로 이동

        //리플레이(해당파일경로) -> 메즈로더와 같은 원리로 읽고 각각 ai, pr 버퍼에다 넣고 행동


    }

    // 메뉴로 돌아가기
    void backToMenu() {
        this.dispose();  // 현재 창 닫기
        new MenuScreen();
        // 메뉴 화면 열기
    }

    public static void main(String[] args) {
        new ReplayScreen();
    }

}