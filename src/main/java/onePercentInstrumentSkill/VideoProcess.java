package onePercentInstrumentSkill;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;

public class VideoProcess {
	private ArrayList<File> fileList;							// input fileList path
	private ArrayList<ArrayList<BufferedImage>> frameList;		// grab bufferImage from fileList
	private ArrayList<BufferedImage> outputList;				// save bufferImage after combine frame
	private final Path myFolderPath;							// input folder
	private final static int WIDTH = 960;						// video width
	private final static int HEIGHT = 720;						// video height
	private final static int ROW = 3;							// num of rows
	private final static int COL = 3;							// num of cols
	
	// constructor
	public VideoProcess(String folderPath) {
		fileList = new ArrayList<File>();
		frameList = new ArrayList<ArrayList<BufferedImage>>();
		myFolderPath = Paths.get(folderPath);
	}
	
	// start to generate video
	public void start() {
		getVideo();
		getFrame();
	}
	
	// get file from path
	private void getVideo() {
		for(int i = 0; i < 1; i++) {
			try {
				File input = new File("A.mp4");
				// EDIT code to grab file
				fileList.add(input);
			}
			catch(Exception e) {
				// EDIT code if video not found should add null 
				System.out.println("GetVideo error");
				System.out.println(e);
			}
		}
	}
	
	// get frame from every file to save in frameList
	private void getFrame() {
		FrameGrab grab;
		
		try {
			ArrayList<BufferedImage> hold = new ArrayList<BufferedImage>();
			Picture picture;
			for(int i = 0; i < fileList.size(); i++) {
				grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(fileList.get(i)));
				while(null !=  (picture = grab.getNativeFrame())) {
					hold.add(resize(AWTUtil.toBufferedImage(picture)));
				}
				frameList.add(new ArrayList<BufferedImage>(hold));
				hold.clear();
			}
		}
		catch(Exception e) {
			// EDIT code if video not found should add null
			System.out.println("GetFrame error");
			System.out.println(e);
		}
	}	
	
	// resize image to correct size
	private BufferedImage resize(BufferedImage image) {
        Image tmp = image.getScaledInstance(WIDTH / COL, HEIGHT / ROW, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(WIDTH / COL, HEIGHT / ROW, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
	}
	
	// combine frame to outputList
	private void combineFrame() {
		//
	}
	
	//check if this object exists 
	private boolean exist(Object obj) {
		return (obj != null ? true : false); 
	}
}
