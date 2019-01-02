package onePercentInstrumentSkill;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

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
	private final MidiHandler myMidi;
	private final String fileName;
	private final Path myFolderPath;							// input folder
	private static PrintWriter exFilePrinter;
	private final static int WIDTH = 960;						// video width
	private final static int HEIGHT = 720;						// video height
	private final static int ROW = 3;							// num of rows
	private final static int COL = 3;							// num of cols
	private final static int FPS = 60;							// frame per second
	private final static int NOT_FIND = -1;
	
	// constructor
	public VideoProcess(String folderPath, MidiHandler midiHandler, String outputFileName) {
		fileList = new ArrayList<File>();
		frameList = new ArrayList<ArrayList<BufferedImage>>();
		outputList = new ArrayList<BufferedImage>();
		myFolderPath = Paths.get(folderPath);
		myMidi = midiHandler;
		fileName = outputFileName;
		init();
	}
	
	private static void init(){
		try {
			File ex = new File("VideoProcessException.txt");
			exFilePrinter = new PrintWriter(ex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exFilePrinter);
		}
	}
	
	// start to generate video
	public void start() {

		getVideo();	
		getFrame();
		combineFrame();
		//generateVideo();
		System.out.println("Success!");
		exFilePrinter.flush();
	}
	
	// get file from path
	private void getVideo() {
		for(int i = 0; i < MidiHandler.NOTE_TABLE.length; i++) {
			try {
				if(exist(MidiHandler.NOTE_TABLE[i])) {
					File temp = new File(myFolderPath + "/" + MidiHandler.NOTE_TABLE[i] + ".mp4");
					if(temp.exists())fileList.add(temp);
					else {
						fileList.add(null);
						exFilePrinter.println("File " + MidiHandler.NOTE_TABLE[i] + " is not found!");
					}
				} else {
					fileList.add(null);
				}
			}
			catch(Exception e) {
				fileList.add(null); 
				e.printStackTrace(exFilePrinter);
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
				if(exist(fileList.get(i))) {
					grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(fileList.get(i)));
					
					while(null !=  (picture = grab.getNativeFrame())) {
						hold.add(resize(AWTUtil.toBufferedImage(picture)));
					}
					
					frameList.add(deepCopy(hold));
					hold.clear();
				}else {
					frameList.add(null);
				}
			}
		}
		catch(Exception e) {
			frameList.add(null);
			e.printStackTrace(exFilePrinter);
		}
	}
	
	// resize image to correct size
	private BufferedImage resize(BufferedImage image) {
		try {
			Image tmp = image.getScaledInstance(WIDTH / COL, HEIGHT / ROW, Image.SCALE_SMOOTH);
			BufferedImage resized = new BufferedImage(WIDTH / COL, HEIGHT / ROW, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2d = resized.createGraphics();
	        g2d.drawImage(tmp, 0, 0, null);
	        g2d.dispose();
	        return resized;
		}
		catch (Exception e){
			System.out.println("Resize error");
			e.printStackTrace(exFilePrinter);
			return null;
		}
	}
	
	// inner class Play
	private class Play {
		private int key;
		private int pos;
		private int frame;
		private final int frameSize;
		
		public Play(int key, int pos, int frame, int frameSize) {
			this.key = key;
			this.pos = pos;
			this.frame = frame;
			this.frameSize = frameSize;
		}
		
		public void resetFrame() {
			frame = 0;
		}
		
		public void nextFrame() {
			if(frame < frameSize - 1) frame++;
		}
		
		public int getKey() {
			return key;
		}
		
		public int getFrame() {
			return frame;
		}
		
		public int getPos() {
			return pos;
		}
	}
	
	// find element in playList
	private int findElm(ArrayList<Play> playList, int index) {
		try {
			for(int i = 0; i < playList.size(); i++) {
				if(playList.get(i).getKey() == index) {
					return i;
				}
			}
			return NOT_FIND;
		}
		catch (Exception e) {
			e.printStackTrace(exFilePrinter);
			return NOT_FIND;
		}
	}
	
	private int randomPos(ArrayList<Play> playList) {
		try {
			Random rnd = new Random();
			int temp = rnd.nextInt(9);
			for(int i = 0; i < playList.size(); i++) {
				if(temp == playList.get(i).getPos()) {
					temp = rnd.nextInt(9);
					i = 0;
				}
			}
			return temp;
		}
		catch(Exception e) {
			e.printStackTrace(exFilePrinter);
			return 0;
		}
	}
	
	//deep copy bufferedImage ArrayList
	private ArrayList<BufferedImage> deepCopy(ArrayList<BufferedImage> source){
		try {
			ArrayList<BufferedImage> cp = new ArrayList<BufferedImage>();
			for(int i = 0; i < source.size(); i++) {
				cp.add(deepCopy(source.get(i)));
			}
			return cp;
		}
		catch(Exception e){
			e.printStackTrace(exFilePrinter);
			return null;
		}
	}
	
	// deep copy bufferedImage
	private BufferedImage deepCopy(BufferedImage source) {
		try {
			BufferedImage cp = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
			Graphics2D g = cp.createGraphics();
			g.drawImage(source, 0, 0, null);
			g.dispose();
			return cp;
		}
		catch(Exception e) {
			e.printStackTrace(exFilePrinter);
			return null;
		}
	}
	
	// combine frame to outputList
	private void combineFrame() {
		ArrayList<Play> playList = new ArrayList<Play>();
		int find, key, pos;
		try {
			for(int frame = 0, index = 0; frame < myMidi.getLastSecond() *  FPS; frame++) { 		// combine every frame by graphic
				if(frame >= myMidi.getNote(index).getSecond() * FPS) {		// if midiEvent happen
					find = findElm(playList, myMidi.getNote(index).getKey());
					
					if(myMidi.getNote(index).getSwitch()) {			// music on.
						if(find != NOT_FIND) {
							playList.get(find).resetFrame();
						}else {
							//exFilePrinter.println("----------------" + frameList.get(myMidi.getNote(index).getKey()).size());
							//playList.add(new Play(myMidi.getNote(index).getKey(), 0, randomPos(playList), frameList.get(index).size()));
						}
					}else {											// music off
						if(find != NOT_FIND) {
							playList.remove(find);
						}
					}
					index++;
				}
			
				BufferedImage temp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			
				for(int play = 0; play < playList.size(); play++) {
					key = playList.get(play).getKey();
					pos = playList.get(play).getPos();
					frame = playList.get(play).getFrame();
					
					Graphics2D g = temp.createGraphics();
					g.drawImage(frameList.get(key).get(frame), 
									pos % 3 * WIDTH, pos / 3 * HEIGHT, null);
					g.dispose();
					playList.get(play).nextFrame();
				}
				outputList.add(deepCopy(temp));
			}
		}
		catch(Exception e) {
			e.printStackTrace(exFilePrinter);
		}
	}
	
	// generate output video
	private void generateVideo() {
		FileChannelWrapper out = null;
		try {
			out = NIOUtils.writableFileChannel(fileName + ".mp4");
			
			AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(FPS, 1));
			for (int i = 0; i < outputList.size(); i++) {
				encoder.encodeImage(outputList.get(i));
			}
			// Finalize the encoding, i.e. clear the buffers, write the header, etc.
			encoder.finish();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exFilePrinter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exFilePrinter);
		} finally {
			NIOUtils.closeQuietly(out);
		}
	}
	
	//check if this object exists 
	private boolean exist(Object obj) {
		return (obj != null ? true : false); 
	}
}
