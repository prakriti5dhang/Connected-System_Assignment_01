package lgClientAxis;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import lgClientAxis.ImageClass;

import javax.swing.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;

public class ClientWindow {

	private JFrame frame;
	public static JTextField IP_txt;
	public static JTextField Port_txt;
	public static JTextField Freq_txt;
	public static JToggleButton tglbtn;
	@SuppressWarnings("rawtypes")
	public static JComboBox Res_cb;
	private JLabel IPlbl;
	private JLabel portlbl;
	private JLabel freqlbl;
	private JLabel reslbl;

	/**
	 * Launch the application.
	 */

	public static String Freq, Res;
	public static boolean proceed;
	public static int btnstate = 0;

	private JLabel IP_warning_lbl;
	private JLabel Port_warning_lbl;
	private JLabel Frame_warning_lbl;

	public static int dsp_width, dsp_hight;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow window = new ClientWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public ClientWindow() throws Exception {
		initialize();
		CreateEvents();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 420, 309);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		IP_txt = new JTextField();
		IP_txt.setText("192.168.20.252");
		IP_txt.setBounds(200, 40, 114, 19);
		frame.getContentPane().add(IP_txt);
		IP_txt.setColumns(10);

		Port_txt = new JTextField();
		Port_txt.setText("8082");
		Port_txt.setColumns(10);
		Port_txt.setBounds(200, 90, 114, 19);
		frame.getContentPane().add(Port_txt);

		Freq_txt = new JTextField();
		Freq_txt.setColumns(10);
		Freq_txt.setBounds(200, 140, 114, 19);
		frame.getContentPane().add(Freq_txt);

		Res_cb = new JComboBox();
		/*Res_cb.setModel(new DefaultComboBoxModel(new String[] {
				"Please select frequency:", "1280x960 (4:3)", "1024x768 (4:3)",
				"800x600 (4:3)", "640x480 (4:3)", "480x360 (4:3)",
				"320x240 (4:3)", "240x180 (4:3)", "160x120 (4:3)",
				"1280x720 (16:9)", "800x450 (16:9)", "640x360 (16:9)",
				"480x270 (16:9)", "320x180 (16:9)", "160x90 (16:9)",
				"1280x800 (16:10)", "176x144" }));*/
		Res_cb.setModel(new DefaultComboBoxModel(new String[] {
				"Please select frequency:", "1280x720 (16:9)", "1920x1080 (16:9)",
				"800x600 (4:3)", "320x180 (16:9)", "800x450 (16:9)", 
				"480x270 (16:9)" }));
		
		Res_cb.setBounds(200, 190, 201, 24);
		frame.getContentPane().add(Res_cb);

		tglbtn = new JToggleButton("Start streaming");
		tglbtn.setToolTipText("Press to start streaming the video");
		tglbtn.setBounds(95, 234, 207, 25);
		frame.getContentPane().add(tglbtn);

		IPlbl = new JLabel("Enter IP address:");
		IPlbl.setLabelFor(IP_txt);
		IPlbl.setBounds(12, 40, 167, 15);
		frame.getContentPane().add(IPlbl);

		portlbl = new JLabel("Enter port number:");
		portlbl.setLabelFor(Port_txt);
		portlbl.setBounds(15, 90, 167, 15);
		frame.getContentPane().add(portlbl);

		freqlbl = new JLabel(" Select Number of frames:");
		freqlbl.setLabelFor(Freq_txt);
		freqlbl.setBounds(12, 140, 156, 15);
		frame.getContentPane().add(freqlbl);

		reslbl = new JLabel("Select Image resolutoin:");
		reslbl.setLabelFor(Res_cb);
		reslbl.setBounds(12, 190, 156, 15);
		frame.getContentPane().add(reslbl);

		IP_warning_lbl = new JLabel("Ex.: 127.0.0.1");
		IP_warning_lbl.setFont(new Font("Dialog", Font.PLAIN, 10));
		IP_warning_lbl.setLabelFor(IP_txt);
		IP_warning_lbl.setBounds(200, 25, 186, 15);
		frame.getContentPane().add(IP_warning_lbl);
		IP_warning_lbl.setVisible(false);

		Port_warning_lbl = new JLabel("Must enter, Range <1023> to <65535>");
		Port_warning_lbl.setLabelFor(Port_txt);
		Port_warning_lbl.setFont(new Font("Dialog", Font.PLAIN, 10));
		Port_warning_lbl.setBounds(200, 75, 201, 15);
		frame.getContentPane().add(Port_warning_lbl);
		Port_warning_lbl.setVisible(false);

		Frame_warning_lbl = new JLabel("Range <0> to <1000>");
		Frame_warning_lbl.setLabelFor(Freq_txt);
		Frame_warning_lbl.setFont(new Font("Dialog", Font.PLAIN, 10));
		Frame_warning_lbl.setBounds(200, 125, 186, 15);
		frame.getContentPane().add(Frame_warning_lbl);
		Frame_warning_lbl.setVisible(false);
	}

	// A function that has all of the ActionListener functions
	public void CreateEvents() throws Exception {

		// This function gets triggered when any action is made over the
		// ToggleButton
		// "tglbtn", and mainly used to check if the button is selected or not.
		tglbtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				Runnable r1; // Declaring a runnable variable r1
				r1 = new ImageClass(); // Initializing r1 to a new object from
										// the class ImageClass.
				Thread t1 = new Thread(r1); // Creating a thread called t1

				// Checking if the action made is (selecting the tglbtn), and
				// starts the stream
				// if its selected and stops it if its not.
				if (tglbtn.isSelected()) {
					Check_Fields();

					if (proceed) {
						Set_Res_Freq();
						tglbtn.setText("Stop streaming");
						tglbtn.setToolTipText("Press to stop streaming the video");
						Disable_Boxes();
						t1.start();

					} // if (proceed) ends here.
					else {
						tglbtn.setSelected(false);
						tglbtn.setText("Start streaming");
						tglbtn.setToolTipText("Press to start streaming the video");
					} // else ends here
				} // if (tglbtn.isSelected()) ends here
				else {
					tglbtn.setText("Start streaming");
					tglbtn.setToolTipText("Press to start streaming the video");
					Enable_Boxes();
					t1.stop();
				}
			}
		});

		// This function checks on the entered characters for the IP_txt text
		// box, and
		// it makes sure it only takes digits and the period characters.
		IP_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char check_char = e.getKeyChar();
				if (!(Character.isDigit(check_char)
						|| check_char == KeyEvent.VK_PERIOD
						|| check_char == KeyEvent.VK_DECIMAL
						|| check_char == KeyEvent.VK_BACK_SPACE || check_char == KeyEvent.VK_DELETE)) {
					IP_txt.getToolkit().beep();
					IP_warning_lbl.setVisible(true);
					e.consume();
				}
			}
		});// IP_txt.addKeyListener function ends here

		// This function restricts the user to only enter numbers in Port_txt
		// text box
		// for the port number.
		Port_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char check_char = e.getKeyChar();
				if (!(Character.isDigit(check_char)
						|| check_char == KeyEvent.VK_BACK_SPACE || check_char == KeyEvent.VK_DELETE)) {
					Port_txt.getToolkit().beep();
					Port_warning_lbl.setVisible(true);
					e.consume();
				}
			}
		});// Port_txt.addKeyListener function ends here.

		// This function restricts the user to only enter numbers in Freq_txt
		// for the
		// Frames Per Second (fps).
		Freq_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char check_char = e.getKeyChar();
				if (!(Character.isDigit(check_char)
						|| check_char == KeyEvent.VK_BACK_SPACE || check_char == KeyEvent.VK_DELETE)) {
					Port_txt.getToolkit().beep();
					Frame_warning_lbl.setVisible(true);
					e.consume();
				}
			}
		});// Freq_txt.addKeyListener function ends here.

	}// CreateEvents ends here

	// This function it sets the values of Freq_txt and the selected item from
	// Res_cb to the variables (Freq & Res) respectively,
	// and its also used to set the values of the dsp_width & dsp_hight
	// according to
	// the choice the user had from the Resolution comboBox.
	public void Set_Res_Freq() {
		Freq = Freq_txt.getText();
		switch (Res_cb.getSelectedIndex()) {
		case 1:
			Res = "1280x720";
			dsp_width = 1280;
			dsp_hight = 720;
			break;
		case 2:
			Res = "1920x1080";
			dsp_width = 1920;
			dsp_hight = 1080;
			break;
		case 3:
			Res = "800x600";
			dsp_width = 800;
			dsp_hight = 600;
			break;
		case 4:
			Res = "320x180";
			dsp_width = 320;
			dsp_hight = 180;
			break;
		case 5:
			Res = "800x450";
			dsp_width = 800;
			dsp_hight = 450;
			break;
		
		
		case 6:
			Res = "480x270";
			dsp_width = 480;
			dsp_hight = 270;
			break;
		
		
		}
	}// Set_Res_Freq function ends here.

	// Check_Fields function makes sure that the client filled all of the fields
	// and
	// incase she/he didn't it will ask her/him if he wants to
	// choose the default values.
	public void Check_Fields() {
		int def_set;
		if (IP_txt.getText().isEmpty() || Port_txt.getText().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							null,
							"Please make sure you fill and provide a correct IP address and port number of an active Axis camera!",
							"Incorrect Camera info!", 0);
			proceed = false;
		} else if (Freq_txt.getText().isEmpty()
				&& Res_cb.getSelectedIndex() == 0) {
			def_set = JOptionPane
					.showConfirmDialog(
							null,
							"Please notice that if you don't choose the frame per seconds and the resolution\nthe default values will be considered\nFPS = 25\nResolution = 800x600\nDo you want to proceed ?",
							"Attention!", JOptionPane.YES_NO_OPTION);
			if (def_set == 0) {
				Freq = "25";
				Res = "800x600";
				dsp_width = 800;
				dsp_hight = 600;
				Freq_txt.setText("25");
				Res_cb.setSelectedIndex(3);
				proceed = true;
			} else
				proceed = false;
		} else if (Freq_txt.getText().isEmpty()) {
			def_set = JOptionPane
					.showConfirmDialog(
							null,
							"Please notice that if you don't choose the frame per seconds, the default value will be considered\nFPS = 25\nDo you want to proceed ?",
							"Attention!", JOptionPane.YES_NO_OPTION);
			if (def_set == 0) {
				Freq = "25";
				Freq_txt.setText("25");
				proceed = true;
			} else
				proceed = false;
		} else if (Res_cb.getSelectedIndex() == 0) {
			def_set = JOptionPane
					.showConfirmDialog(
							null,
							"Please notice that if you don't choose the resolution, the default values will be considered\nResolution = 800x600\nDo you want to proceed ?",
							"Attention!", JOptionPane.YES_NO_OPTION);
			if (def_set == 0) {
				Res = "800x600";
				Res_cb.setSelectedIndex(3);
				dsp_width = 800;
				dsp_hight = 600;
				proceed = true;
			} else
				proceed = false;
		} else {
			Set_Res_Freq();
			proceed = true;
		}
}

	// This function is responsible for enabling all the fields on the client
	// interface so it could be editable.
	// It is called when the stream stops.
	public void Enable_Boxes() {
		IP_txt.setEnabled(true);
		Port_txt.setEnabled(true);
		Freq_txt.setEnabled(true);
		Res_cb.setEnabled(true);
	}

	// This function disables all the fields, so they won't be editable.
	// It is used when the stream is working.
	public void Disable_Boxes() {
		IP_txt.setEnabled(false);
		Port_txt.setEnabled(false);
		Freq_txt.setEnabled(false);
		Res_cb.setEnabled(false);
	}
}