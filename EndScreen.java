

import javax.swing.*;
import java.awt.*;

public class EndScreen<E> extends JFrame {

    public EndScreen(E second,E aiSecond){
        setTitle("미로 찾기 게임 - 엔딩");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));


        // 버튼 2: 종료
        JButton exitBtn = new JButton("사용자의 시간 : "+second+"초  종료, ai시간 : "+ aiSecond);
        exitBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        exitBtn.addActionListener(e -> System.exit(0));
        add(exitBtn);

        setVisible(true);

    }

    public static void main(String[] args) {
    }
}
