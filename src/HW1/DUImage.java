package HW1;

// This is a helper class for loading in png/jpg images

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

//import com.sun.image.codec.jpeg.*;

public class DUImage {
	private BufferedImage im;

	// Make an image from the given filename
	public DUImage(String filename) {
		File f = new File(filename);

		try {
			this.im = ImageIO.read(f);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Make a blank image given the size
	public DUImage(int x, int y) {
		this.im = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);

		// Create a graphics context for the new BufferedImage
		Graphics2D g2 = this.im.createGraphics();

		// Fill in the image with white
		g2.setColor(Color.white);
		g2.fillRect(0, 0, x, y);
	}

	public int getWidth() {
		return im.getWidth();
	}

	public int getHeight() {
		return im.getHeight();
	}

	public int getRed(int x, int y) {
		int value = 0;

		if ((x >= 0) && (y >= 0) && (x < im.getWidth()) && (y < im.getHeight())) {
			value = ((im.getRGB(x, y) >> 16) & 255);
		}

		return value;
	}

	public int getGreen(int x, int y) {
		int value = 0;

		if ((x >= 0) && (y >= 0) && (x < im.getWidth()) && (y < im.getHeight())) {
			value = ((im.getRGB(x, y) >> 8) & 255);
		}

		return value;
	}

	public int getBlue(int x, int y) {
		int value = 0;

		if ((x >= 0) && (y >= 0) && (x < im.getWidth()) && (y < im.getHeight())) {
			value = ((im.getRGB(x, y) >> 0) & 255);
		}

		return value;
	}

	public void setRGB(int x, int y, int red, int green, int blue) {
		int pixel = (red << 16) + (green << 8) + (blue);

		im.setRGB(x, y, pixel);
	}

	public void draw(Graphics g) {
		g.drawImage(im, 0, 0, null);
	}
	
	public void write(String filename) {
		if (filename.endsWith(".png")) {
			writePNG(filename);
		} else if (filename.endsWith(".jpg")) {
			//writeJPG(filename);
			System.out.println("JPEGs are not supported for writing");
		} else {
			System.out.println("Filetype not supported");
		}
	}

	private void writePNG(String filename) {
		
		File f = new File(filename);
		try {
			// Write out the image to the file as a png
			ImageIO.write(this.im, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
		
	// JPEGS are no longer supported by this code for writing
	// since the libraries are no longer included in the base java install
//	private void writeJPG(String filename) {
//			
//		// Make an output stream
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(filename);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// Get the JPEG encoding parameters from the buffered image
//		JPEGEncodeParam jpegpara = JPEGCodec.getDefaultJPEGEncodeParam(im);
//
//		// Make any modifications to the encoding parameters here
//		// ...
//
//		// Make a JPEG encoder given an output stream and the above jpeg
//		// parameters
//		JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(fos, jpegpara);
//
//		// Write my buffered image to the file using the above JPEGEncoder
//		try {
//			enc.encode(this.im);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
