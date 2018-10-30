package lgClientAxis;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ImageClass implements Runnable {
	public static JFrame Stream_Frame = new JFrame("Image Streaming");
	public static JLabel Image_View_Label = new JLabel();

	public void run() {
		try {
			String IP_Addr = ClientWindow.IP_txt.getText();
			int Port_No = Integer.valueOf(ClientWindow.Port_txt.getText());

			Socket socket;
			socket = new Socket(IP_Addr, Port_No);

			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(
					socket.getOutputStream());

			// passing the Frames Per Seconds (fps) to the camera.
			dout.write(ClientWindow.Freq.getBytes());
			dout.flush();
			// Passing the Resolution to the camera.
			dout.write(ClientWindow.Res.getBytes());
			dout.flush();

			// Declaring and preparing the frame where the image stream will be
			// viewed.
			Stream_Frame.getContentPane().add(Image_View_Label,
					BorderLayout.CENTER);
			Stream_Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Stream_Frame.setBounds(450, 0, ClientWindow.dsp_width,
					ClientWindow.dsp_hight + 20);
			Stream_Frame.setResizable(false);
			Image_View_Label.setBounds(0, 0, ClientWindow.dsp_width,
					ClientWindow.dsp_hight);
			Stream_Frame.setVisible(true);

			// The following block of code has the main process of the program,
			// which is to receive and render the image stream for the client.
			while (ClientWindow.tglbtn.isSelected()) {

				int sizeOfImage = 0; // Used to save the size of the images that
										// will be sent to the client.
				int Freq_counter = 1; // An iterator that its value starts at 1
										// and keeps increasing by "1" until it
										// reaches the number of frames.
				while (Freq_counter <= Integer.valueOf(ClientWindow.Freq)) {
					sizeOfImage = din.readInt(); // Receiving the image's size
													// from the camera.
					byte[] readData = new byte[sizeOfImage]; // Declaring and
																// initializing
																// a byte array
																// to save the
																// image as a
																// sequence of
																// bytes.

					// Acknowledging the receipt of the size of the image.
					dout.write("Got it".getBytes());
					dout.flush();

					// Reading the image and save it as byte array in the
					// previously declared array "readData".
					din.readFully(readData, 0, sizeOfImage);

					// Acknowledging the receipt of the image.
					dout.write("Image received".getBytes());
					dout.flush();

					// The process of reforming the image from bytes to an
					// ImageIcon, so it would be ready to be rendered in
					// Image_View_Label Label.
					ImageIcon Img_Icon = new ImageIcon(); // Creating new
															// ImageIcon
															// "Img_Icon" and
															// setting its value
															// to an empty
															// ImageIcon.
					ByteArrayInputStream bais = new ByteArrayInputStream(
							readData); // Creating BAIS "bais" to read the byte
										// array "readData".
					BufferedImage My_Image = ImageIO.read(bais); // Declaring a
																	// BufferedImage
																	// "My_Image"
																	// and
																	// making it
																	// read the
																	// bytes
																	// stream
																	// from the
																	// BAIS
																	// "bais".
					Img_Icon.setImage(My_Image); // Setting the Image to be
													// shown in the ImageIcon
													// "Img_Icon".

					Image_View_Label.setIcon(Img_Icon); // Updating the Icon of
														// Image_View_Label, in
														// which the image
														// stream will be
														// rendered for the
														// client.

					Freq_counter++;
				}// While loop for Frequency ends here.

				// Checking if the user wants to end the stream.
				if (!(ClientWindow.tglbtn.isSelected()))
					break;

			}// While loop for Streaming ends here.
			Stream_Frame.dispose();
			dout.close();
			din.close();
			socket.close();
		} // end of try.
		catch (Exception exc) {
			JOptionPane
					.showMessageDialog(null,
							"Server not found!\nPlease check the Camera's IP and Port.");
			ClientWindow.proceed = false;
			ClientWindow.tglbtn.setText("Start streaming");
			ClientWindow.tglbtn
					.setToolTipText("Press to start streaming the video");
			ClientWindow.tglbtn.setSelected(false);
			ClientWindow.IP_txt.setEnabled(true);
			ClientWindow.Port_txt.setEnabled(true);
			ClientWindow.Freq_txt.setEnabled(true);
			ClientWindow.Res_cb.setEnabled(true);
		} // end of catch.

	}// end of run().
}// ImageThread ends here.
