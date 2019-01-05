package onePercentInstrumentSkill;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
	private PrintWriter exPrinter;
	private FileChannelWrapper out;
	private AWTSequenceEncoder encoder;	
	private final MidiHandler myMidi;
	private final Path myFolderPath;							// input folder
	private final Random rnd = new Random();
	private final BufferedImage background;
	private final static String outputPath = "./tmp/";			// output folder
	private final static String fileName = "mp4Output";
	private final static int WIDTH = 960;						// video width
	private final static int HEIGHT = 540;						// video height
	private final static int ROW = 3;							// num of rows
	private final static int COL = 3;							// num of cols
	private final static int FPS = 60;							// frame per second
	private final static int NOT_FIND = -1;
	
	// constructor
	public VideoProcess(String folderPath, MidiHandler midiHandler, String backgroundName) throws IOException{
		fileList = new ArrayList<File>();
		frameList = new ArrayList<ArrayList<BufferedImage>>();
		exPrinter = new PrintWriter(new File(outputPath + "VideoProcessException.txt"));
		File temp = new File(backgroundName);
		if(temp.exists()) {
			background = ImageIO.read(temp);
		}else {
			background = null;
		}
		
		myFolderPath = Paths.get(folderPath);
		myMidi = midiHandler;
		out = null;
	}
		
	// start to generate video
	public void start() throws IOException {
		initEncode();
		
		getVideo();	
		getFrame();
		combineFrame();
		
		finishEncode();
		
		System.out.println("Success!");
		exPrinter.flush();
	}
	
	// init encode
	private void initEncode() throws IOException {
		out = NIOUtils.writableFileChannel(outputPath+fileName + ".mp4");
		encoder = new AWTSequenceEncoder(out, Rational.R(FPS, 1));
	}
	
	private void finishEncode() {
		try {
			// Finalize the encoding, i.e. clear the buffers, write the header, etc.
			encoder.finish();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exPrinter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exPrinter);
		} finally {
			NIOUtils.closeQuietly(out);
		}
	}
	
	// get file from path
	private void getVideo() {
		for(int i = 0; i < NoteTable.NOTE_TABLE.length; i++) {
			try {
				if(exist(NoteTable.NOTE_TABLE[i])) {
					File temp = new File(myFolderPath + "/" + NoteTable.NOTE_TABLE[i] + ".mp4");
					
					if(temp.exists()) {
						fileList.add(temp);
					}
					else {
						fileList.add(null);
						exPrinter.println(NoteTable.NOTE_TABLE[i] + ".mp4 is not found");
					}
				} else {
					fileList.add(null);
				}
			}
			catch(Exception e) {
				e.printStackTrace(exPrinter);
			}
			frameList.add(null);
		}
	}
	
	// get frame from every file to save in frameList
	private void getFrame() {
		
		try {
			int cores = Runtime.getRuntime().availableProcessors();
			ArrayList<Thread> t = new ArrayList<Thread>(); 
			
			for(int i = 0; i < cores; i++) t.add(new Thread());
			
			for(int i = 0; i < fileList.size(); i++) {
				if(exist(fileList.get(i))) {
					int label = 0;

					for(int j = 0; j < t.size(); j++) {
						if(!t.get(j).isAlive()) {
							t.set(j, new Thread(new MyRunnable(i)));
							label = 1;
							t.get(j).start();
							break;
						}
					}
					if(label == 0) {
						i--;
						t.get(0).join();
					}
				}
			}
			
			for(int i = 0; i < t.size(); i++) {
				t.get(i).join();
			}
			
			Runtime.getRuntime().gc();
		}
		catch(Exception e) {
			e.printStackTrace(exPrinter);
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
			e.printStackTrace(exPrinter);
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
			e.printStackTrace(exPrinter);
			return NOT_FIND;
		}
	}
	
	// generate random block position
	private int randomPos(ArrayList<Play> playList) {
		int temp = rnd.nextInt(ROW * COL);
		int count = 0;
		for(int i = 0; i < playList.size(); i++) {
			count++;
			if(count > 100) {
				return temp;
			}
			if(temp == playList.get(i).getPos()) {
				temp = rnd.nextInt(ROW * COL);
				i = -1;
			}
		}
		return temp;
	}
	
	// deep copy bufferedImage ArrayList
	private ArrayList<BufferedImage> deepCopy(ArrayList<BufferedImage> source){
		try {
			ArrayList<BufferedImage> cp = new ArrayList<BufferedImage>();
			for(int i = 0; i < source.size(); i++) {
				cp.add(deepCopy(source.get(i)));
			}
			return cp;
		}
		catch(Exception e){
			e.printStackTrace(exPrinter);
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
			e.printStackTrace(exPrinter);
			return null;
		}
	}
	
	// combine frame to outputList
	private void combineFrame() {
		ArrayList<Play> playList = new ArrayList<Play>();
		int find, noteKey, notePos, noteFrame;
		try {
			// combine every frame by graphic
			for(int frame = 0, index = 0; frame < myMidi.getLastSecond() *  FPS; frame++) { 		
				// if midiEvent happen
				while(frame >= myMidi.getNote(index).getSecond() * FPS && index < myMidi.getSize()) {
					find = findElm(playList, myMidi.getNote(index).getKey());
					// music on.
					if(myMidi.getNote(index).getSwitch() && exist(frameList.get(myMidi.getNote(index).getKey()))) {	
						if(find != NOT_FIND) {
							playList.get(find).resetFrame();
						}else {
							playList.add(new Play(myMidi.getNote(index).getKey(),
										randomPos(playList),
										0,
										frameList.get(myMidi.getNote(index).getKey()).size()));
						}
						System.out.println("playList NOTE " + myMidi.getNote(index).getName() + " turn on at " + frame + " frame!");
					// music off
					}else {															
						if(find != NOT_FIND) {
							playList.remove(find);
							System.out.println("playList NOTE " + myMidi.getNote(index).getName() +  " turn off at " + frame + " frame!");
						}
					}
					index++;
				}
			
				// combine image by graphic to generate output image list
				BufferedImage temp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
				
				Graphics2D g = temp.createGraphics();
				
				if(exist(background)) {
					g.drawImage(background, 0, 0, null);
				}	
				for(int play = 0; play < playList.size(); play++) {
					noteKey = playList.get(play).getKey();
					notePos = playList.get(play).getPos();
					noteFrame = playList.get(play).getFrame();
					
					g.drawImage(frameList.get(noteKey).get(noteFrame), 
									(notePos % COL) * (WIDTH / COL), (notePos / ROW) * (HEIGHT / ROW), null);
					playList.get(play).nextFrame();
				}
				
				g.dispose();
				
				// write image to video
				try {
					writeVideo(deepCopy(temp));
					System.out.println("output frame " + frame + " combine success");
				}
				catch(Exception e) {
					e.printStackTrace(exPrinter);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace(exPrinter);
		}
	}
	
	// write to video
	private void writeVideo(BufferedImage temp) {
		try {
			encoder.encodeImage(temp);
			// Finalize the encoding, i.e. clear the buffers, write the header, etc.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exPrinter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(exPrinter);
		}
	}
	
	//check if this object exists 
	private boolean exist(Object obj) {
		return (obj != null ? true : false); 
	}
	
	// MultiThread grab
	private class MyRunnable implements Runnable { 
		private int i;
		public MyRunnable(int index){
			this.i = index;
		}
		
		public void run() {
			try {
				FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(fileList.get(i)));
				ArrayList<BufferedImage> hold = new ArrayList<BufferedImage>();
				Picture picture;
				
				int count = 0;
				while(null !=  (picture = grab.getNativeFrame())) {
					hold.add(resize(AWTUtil.toBufferedImage(picture)));
					count ++;
					System.out.println("-------" + fileList.get(i).getName() + " frame " + count  + " grab success!");
				}
				
				frameList.set(i, deepCopy(hold));
				System.out.println("-------" + fileList.get(i).getName() + " grab success!");
			hold.clear();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(exPrinter);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(exPrinter);
			} catch (JCodecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(exPrinter);
			};
		}
	}
	
}
