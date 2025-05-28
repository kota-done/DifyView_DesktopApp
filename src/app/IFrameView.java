package app;

import javax.swing.JFrame;

public class IFrameView extends JFrame{
	public  void setView(String title,int width ,int height) {
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);//閉じるボタンの処理
		setSize(width, height);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
