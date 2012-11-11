package com.clete2.LyricInjector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class LyricInjectorView implements ActionListener {

	private JFrame frame;
	private JTextField browseTextField;
	private JButton btnInjectLyrics;
	private JLabel threadStatusLabel;
	private LyricInjectorController lyricInjectorController;
	private Timer threadStatusTimer;

	/**
	 * Create the application.
	 * @param lyricInjectorController 
	 */
	public LyricInjectorView(LyricInjectorController lyricInjectorController, JLabel threadStatusLabel) {
		this.threadStatusLabel = threadStatusLabel;
		this.lyricInjectorController = lyricInjectorController;
		this.threadStatusTimer = new Timer(1000, this);
		this.initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Directory:");
		lblNewLabel.setBounds(6, 6, 62, 28);
		frame.getContentPane().add(lblNewLabel);

		browseTextField = new JTextField();
		browseTextField.setBounds(80, 6, 228, 28);
		browseTextField.setText(lyricInjectorController.getInitialFilePath());
		frame.getContentPane().add(browseTextField);
		browseTextField.setColumns(10);

		JButton btnNewButton = new JButton("Browse...");
		btnNewButton.setBounds(320, 7, 100, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(frame);
				if(result == JFileChooser.APPROVE_OPTION) {
					browseTextField.setText(fileChooser.getSelectedFile().toString());
				}
			}
		});
		frame.getContentPane().add(btnNewButton);

		threadStatusLabel.setBounds(80, 45, 228, 28);
		frame.getContentPane().add(threadStatusLabel);

		btnInjectLyrics = new JButton("Inject Lyrics!");
		btnInjectLyrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadStatusTimer.start();
				lyricInjectorController.injectLyrics(browseTextField.getText());
			}
		});
		btnInjectLyrics.setBounds(320, 46, 124, 28);
		frame.getContentPane().add(btnInjectLyrics);

		JLabel lblStatus_1 = new JLabel("Status:");
		lblStatus_1.setBounds(6, 52, 62, 16);
		frame.getContentPane().add(lblStatus_1);
	}

	@Override
	/**
	 * Action listener to update the thread status label.
	 */
	public void actionPerformed(ActionEvent e) {
		this.threadStatusLabel.setText(this.lyricInjectorController.getThreadStatusLabelText());
	}
}
