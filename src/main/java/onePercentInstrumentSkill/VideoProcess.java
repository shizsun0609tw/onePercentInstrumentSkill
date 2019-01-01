package onePercentInstrumentSkill;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import java.io.File;
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
	private final static int WIDTH = 960;						// video width
	private final static int HEIGHT = 720;						// video height
	private final static int ROW = 3;							// num of rows
	private final static int COL = 3;							// num of cols
	private final static int FPS = 60;							// frame per second
	
	// constructor
	public VideoProcess(String folderPath, MidiHandler midiHandler, String outputFileName) {
		fileList = new ArrayList<File>();
		frameList = new ArrayList<ArrayList<BufferedImage>>();
		myFolderPath = Paths.get(folderPath);
		myMidi = midiHandler;
		fileName = outputFileName;
	}
	
	// start to generate video
	public void start() {
		getVideo();
		getFrame();
		combineFrame();
		generateVideo();
	}
	
	// get file from path
	private void getVideo() {
		for(int i = 0; i < 1; i++) {
			try {
				fileList.add(new File(input));
			} 
			catch(Exception e) {
				fileList.add(null); 
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
				if(exist(fileList.get(i))) {
					grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(fileList.get(i)));
					while(null !=  (picture = grab.getNativeFrame())) {
						hold.add(resize(AWTUtil.toBufferedImage(picture)));
					}
					frameList.add(new ArrayList<BufferedImage>(hold));
					hold.clear();
				}
			}
		}
		catch(Exception e) {
			frameList.add(null);
			System.out.println("GetFrame error");
			System.out.println(e);
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
			System.out.println(e);
			return null;
		}
	}
	
	// inner class Block
	private class Block {
		private int key;
		private int pos;
		private int frame;
		
		public Block(int key, int pos, int frame) {
			this.key = key;
			this.pos = pos;
			this.frame = frame;
		}
		
		public void resetFrame() {
			frame = 0;
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
	private int findElm(ArrayList<Block> playList, int index) {
		try {
			for(int i = 0; i < playList.size(); i++) {
				if(playList.get(i).getKey() == index) {
					return i;
				}
			}
			return -1;
		}
		catch (Exception e) {
			System.out.println("FindElm error");
			System.out.println(e);
			return -1;
		}
	}
	
	private int randomPos(ArrayList<Block> playList) {
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
			System.out.println("Rnd error");
			System.out.println(e);
			return 0;
		}
	}
	
	// deep copy bufferedImage
	private BufferedImage deepCopy(BufferedImage source) {
		try {
		 BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		 Graphics2D g = b.createGraphics();
		 g.drawImage(source, 0, 0, null);
		 g.dispose();
		 return b;
		}
		catch(Exception e) {
			System.out.println("DeepCopy error");
			System.out.println(e);
			return null;
		}
	}
	
	// combine frame to outputList
	private void combineFrame() {
		ArrayList<Block> playList = new ArrayList<Block>();
		int find, key, pos;
		try {
			for(int frame = 0, index = 0; frame < myMidi.getLastSecond() *  FPS; frame++) { 		// combine every frame by graphic
				try {
					if(frame >= myMidi.getNote(index).getSecond() * FPS) {		// if midiEvent happen
						find = findElm(playList, myMidi.getNote(index).getKey());
						
						if(myMidi.getNote(index).getSwitch()) {			// music on
							if(find == -1) {
								playList.add(new Block(myMidi.getNote(index).getKey(), 0, randomPos(playList)));
							}else {
								playList.get(find).resetFrame();
							}
						}else {											// music off
							if(find != -1) {
								playList.remove(find);
							}
						}
						index++;
					}
				}
				catch (Exception e){
					System.out.println("Event error");
					System.out.println(e);
				}
				
				BufferedImage temp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
				try {
					for(int play = 0; play < playList.size(); play++) {
						key = playList.get(play).getKey();
						pos = playList.get(play).getPos();
						frame = playList.get(play).getFrame();
						
						Graphics2D g = temp.createGraphics();
						g.drawImage(frameList.get(key).get(frame), 
										pos % 3 * WIDTH, pos / 3 * HEIGHT, null);
						g.dispose();
					}
					outputList.add(deepCopy(temp));
				}
				catch (Exception e){
					System.out.println("Graphic error");
					System.out.println(e);
				}
			}
		}
		catch(Exception e) {
			System.out.println("Combine Error");
			System.out.println(e);
		}
	}
	
	// generate output video
	private void generateVideo() {
		FileChannelWrapper out = null;
		try {
			out = NIOUtils.writableFileChannel(fileName);
			
			AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(FPS, 1));
			for (int i = 0; i < outputList.size(); i++) {
				encoder.encodeImage(outputList.get(i));
			}
			// Finalize the encoding, i.e. clear the buffers, write the header, etc.
			encoder.finish();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			NIOUtils.closeQuietly(out);
		}
	}
	
	//check if this object exists 
	private boolean exist(Object obj) {
		return (obj != null ? true : false); 
	}
}
