package onePercentInstrumentSkill;

import java.io.*;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;

public class WavMixer{
    private WavFile outputWavFile = null;
    private double duration;
    private int sampleRate;
    private long numFrames;
    private String inputDir;		// "./" is at "{project path}/onePercentInstrumentSkill/"
    private MidiHandler midi;
    private PrintWriter exPrinter;
    private WavFile[] wavData = new WavFile[128];
    private double[][][] wavDataBuffer = new double[128][2][];
    private final static int NOT_FIND = -1;
    
    // inner Class store currently playing note.
	private class Play {
		private int key;
		private long frame;
		private final long frameSize;
		private int velocity;
		public Play(int key, long frame, long frameSize, int velocity) {
			this.key = key;
			this.frame = frame;
			this.frameSize = frameSize;
			this.velocity = velocity;
		}
		
		public void resetFrame() {
			frame = 0;
		}
		
		public Boolean nextFrame() {
			if(frame < frameSize - 1) {
				frame++;
				return true;
			}
			else {
				return false;
			}
		}
		
		public int getKey() {
			return key;
		}
		
		public long getFrame() {
			return frame;
		}
		
		public int getVelocity() {
			return this.velocity;
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
			exPrinter.flush();
			return NOT_FIND;
		}
	}
	
	// Constructor
    public WavMixer(MidiHandler midi, String outputFileName, int sampleRate) throws FileNotFoundException{
    	this.inputDir = "./tmp/";
    	this.sampleRate = sampleRate;
    	this.duration = midi.getLastSecond();
        this.numFrames = (long)(duration * sampleRate);
        this.midi = midi;
        this.exPrinter = new PrintWriter(new File("./tmp/WavMixer.txt"));
        
        String outputDir = "./tmp/";
    	try {
			outputWavFile = WavFile.newWavFile(new File(outputDir+outputFileName), 2, numFrames, 16, sampleRate);
		} catch (IOException e1) {
			exPrinter.println("IOException when opening outputWavFile.");
			e1.printStackTrace(exPrinter);
			exPrinter.flush();
		} catch (WavFileException e1) {
			exPrinter.println("WavFileException when opening outputWavFile.");
			e1.printStackTrace(exPrinter);
			exPrinter.flush();
		}

    }    
    public WavFile getWavFile(){
        return this.outputWavFile;
    }

    public void loadWavFile() {
    	String dirPath = inputDir;
    	for(int i = 0; i < 128; i++) {
			if(MidiHandler.NOTE_TABLE[i] != null) {
	    		try {
	    			// open wav data file
	    			File tempFile = new File(dirPath+MidiHandler.NOTE_TABLE[i]+".wav");
					wavData[i] = WavFile.openWavFile(tempFile);
				} catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(exPrinter);
					exPrinter.flush();
					continue;
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(exPrinter);
					exPrinter.flush();
				} catch (WavFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(exPrinter);
					exPrinter.flush();
				} 
				// read wav file to each Buffer
				if(null != wavData[i]) {
					try {
						System.out.println(i);
						wavDataBuffer[i][0] = new double[(int)wavData[i].getNumFrames()];
						wavDataBuffer[i][1] = new double[(int)wavData[i].getNumFrames()];
						wavData[i].readFrames(wavDataBuffer[i], (int)wavData[i].getNumFrames());
						System.out.println(i);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace(exPrinter);
						exPrinter.flush();
					} catch (WavFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace(exPrinter);
						exPrinter.flush();
					}
				}
			}
    	}
    }
    
    public void walkThroughAllFrame(){
    	double maxVolumn = 0.0;
    	double minVolumn = 0.0;
    	if(outputWavFile != null) {
	    	ArrayList<Play> playList = new ArrayList<Play>();	    	
	    	int find, noteKey, noteFrame, noteVelocity;

	    	for(int frame = 0, index = 0;frame < numFrames; frame++) {
	    		// if the midi event occur at this frame
	    		while(frame >= midi.getNote(index).getSecond()*sampleRate && index < midi.getSize()) {
	    			find = findElm(playList, midi.getNote(index).getKey());
	    			// if key == on && key exist
	    			if(midi.getNote(index).getSwitch() && wavData[midi.getNote(index).getKey()] != null) {
	    				// if the note is already playing, restart playing it.
	    				if(find != NOT_FIND) {
	    					//playList.get(find).resetFrame();
	    					playList.add(new Play(midi.getNote(index).getKey(),
	    							0,
	    							wavData[midi.getNote(index).getKey()].getNumFrames(),
	    							midi.getNote(index).getVelocity()));
	    				}
	    				// if the note is off
	    				else {
	    					// add this note to current frame 
	    					playList.add(new Play(midi.getNote(index).getKey(),
	    							0,
	    							wavData[midi.getNote(index).getKey()].getNumFrames(),
	    							midi.getNote(index).getVelocity()));
	    				}
	    				System.out.println("playList NOTE " + midi.getNote(index).getName() + " turn on at " + frame + " frame!");
	    			}
	    			// if key == off or key does note exist
	    			else {
	    				// if the note does not exist
	    				if(find != NOT_FIND) {
	    					//playList.remove(find);
	    					System.out.println("playList NOTE " + midi.getNote(index).getName() +  " turn off at " + frame + " frame!");
	    				}
	    			}
	    			index++;
	    			
	    		}
	    		
	    		// for each note in playList, add to outputBuffer(frameBuffer)
	    		double[][] frameBuffer = new double[2][1];
	    		// frameBuffer initialize
	    		frameBuffer[0][0] = 0;
	    		frameBuffer[1][0] = 0;
	    		for(int play = 0; play < playList.size(); play++) {
	    			// add note to buffer and write to audio file
	    			noteKey = playList.get(play).getKey();
	    			noteFrame = (int)playList.get(play).getFrame();
	    			noteVelocity = playList.get(play).getVelocity();
	    			frameBuffer[0][0] += ((wavDataBuffer[noteKey][0][noteFrame]/128.0)*noteVelocity);
	    			frameBuffer[1][0] += ((wavDataBuffer[noteKey][1][noteFrame]/128.0)*noteVelocity);
	    			if(!playList.get(play).nextFrame()){
	    				playList.remove(play);
	    			}
	    		}
	    		if(frameBuffer[0][0] >= 0.99) {
	    			frameBuffer[0][0] = 0.99;
	    		}
	    		if(frameBuffer[1][0] >= 0.99) {
	    			frameBuffer[1][0] = 0.99;
	    		}
	    		if(frameBuffer[0][0] <= -0.99) {
	    			frameBuffer[0][0] = -0.99;
	    		}
	    		if(frameBuffer[1][0] <= -0.99) {
	    			frameBuffer[1][0] = -0.99;
	    		}
	    		if(Math.max(frameBuffer[0][0], frameBuffer[1][0]) > maxVolumn) {
	    			maxVolumn = Math.max(frameBuffer[0][0], frameBuffer[1][0]);
	    		}
	    		if(Math.min(frameBuffer[0][0], frameBuffer[1][0]) < minVolumn) {
	    			minVolumn = Math.max(frameBuffer[0][0], frameBuffer[1][0]);
	    		}
	    		// write buffer to output
	    		try {
					outputWavFile.writeFrames(frameBuffer, 1);
				} catch (IOException e) {
					exPrinter.println("outputWavFile IOException @frame: "+frame+", @index: "+index);
					e.printStackTrace(exPrinter);
					exPrinter.flush();
				} catch (WavFileException e) {
					exPrinter.println("outputWavFile WavFileException @frame: "+frame+", @index: "+index);
					e.printStackTrace(exPrinter);
					exPrinter.flush();
				}
	
	    	}
    	}
    	else {
    		System.out.println("WavMixer Error: Output File haven't been open!");
    	}
    	System.out.println("Max Volumn: "+maxVolumn);
    	System.out.println("Min Volumn: "+minVolumn);
        return;
    }
    
    public void start() {
    	loadWavFile();
    	walkThroughAllFrame();
    }

}