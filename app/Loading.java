package app;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Window.Type;
import java.awt.Color;
import java.awt.SystemColor;
import java.util.Timer;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Loading extends JFrame {

	private JPanel contentPane;

	public JProgressBar progressBar;
	public JLabel label;
	public JButton button;
	
	public Loading() {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("\u0417\u0430\u0433\u0440\u0443\u0437\u043A\u0430 \u043A\u043E\u043C\u043F\u043E\u043D\u0435\u043D\u0442\u043E\u0432...");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 495, 183);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		progressBar = new JProgressBar();
		
		progressBar.setBounds(15, 61, 459, 22);
		progressBar.setForeground(new Color(0, 51, 255));
		
		label = new JLabel("\u041E\u0441\u0442\u0430\u043B\u043E\u0441\u044C: ~ ");
		label.setFont(new Font("Calibri Light", Font.PLAIN, 16));
		label.setBounds(15, 28, 203, 22);
		contentPane.setLayout(null);
		
		button = new JButton("\u041E\u0442\u043C\u0435\u043D\u0438\u0442\u044C");

		button.setFont(new Font("Tahoma", Font.PLAIN, 12));
		button.setBounds(198, 109, 92, 34);
		contentPane.add(button);
		contentPane.add(label);
		contentPane.add(progressBar);
	}
}
