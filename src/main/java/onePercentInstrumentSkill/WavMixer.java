package onePercentInstrumentSkill;

import java.io.*;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;

public class WavMixer{
    private WavFile outputWavFile = null;
    private double duration;
    private int sampleRate;
    private long numFrames;
    private WavFile[] wavData = new WavFile[128];
    private double[][][] wavDataBuffer = new double[128][2][];
    private Boolean[] isKeyPlaying = new Boolean[128];
    private MidiHandler midi;
    private final static int NOT_FIND = -1;
    // store note status in whole midi file
    // use to check if the note should currently play or not
    // inner Class of WavMixer
    public class NoteStatus{
    	private int status = 0; // status > 0 => isPlaying
    	private long totalNoteFrame;	// how much frame in this note
    	private long currentNoteFrame;
    	private String noteName;
    	public NoteStatus(String noteName, long totalNoteFrame) {
    		this.status = 0;
    		this.totalNoteFrame = totalNoteFrame;
    		this.currentNoteFrame = 0;
    		this.noteName = noteName;
    	}
    	public int getStatus() {
    		return status;
    	}
    	public int onOnce(){
    		status += 1;
    		setCurrentFrameToZero();
    		return status;
    	}
    	public int offOnce() {
    		if(status > 0) status -= 1;
    		return status;
    	}
    	public long getTotalFrame() {
    		return totalNoteFrame;
    	}
    	public long getCurrentFrame() {
    		return currentNoteFrame;
    	}
    	public long setCurrentFrameToZero() {
    		currentNoteFrame = 0;
    		return currentNoteFrame;
    	}
    	public void setCurrentFrame(long set) {
    		currentNoteFrame = set;
    	}
    	public String getName() {
    		return noteName;
    	}
    	
    }
    // inner Class store currently playing note.
	private class Play {
		private int key;
		private long frame;
		private final long frameSize;
		
		public Play(int key, long frame, long frameSize) {
			this.key = key;
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
		
		public long getFrame() {
			return frame;
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
			e.printStackTrace();
			return NOT_FIND;
		}
	}
    public WavMixer() {
    	try {
			MidiHandler mh = new MidiHandler("src/main/resources/test.midi", 120);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public WavMixer(MidiHandler midi, String outputFilePath, int sampleRate){
    	this.sampleRate = sampleRate;
    	this.duration = midi.getLastSecond();
        this.numFrames = (long)(duration * sampleRate);
        this.midi = midi;
    	try {
			outputWavFile = WavFile.newWavFile(new File(outputFilePath), 2, numFrames, 16, sampleRate);
		} catch (IOException e1) {
			System.err.println("IOException when opening outputWavFile.");
			e1.printStackTrace();
		} catch (WavFileException e1) {
			System.err.println("WavFileException when opening outputWavFile.");
			e1.printStackTrace();
		}

    }    
    public WavFile getWavFile(){
        return this.outputWavFile;
    }

    public void loadWavFile() {
    	String dirPath = "src/test/resources/";
    	for(int i = 0; i < 128; i++) {
			if(MidiHandler.NOTE_TABLE[i] != null) {
	    		try {
	    			// open wav data file
	    			File tempFile = new File(dirPath+MidiHandler.NOTE_TABLE[i]+".wav");
					wavData[i] = WavFile.openWavFile(tempFile);
				} catch(FileNotFoundException fnfe) {
					continue;
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WavFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
						e.printStackTrace();
					} catch (WavFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
    	}
    }
    
    public void walkThroughAllFrame(String outputPath){
    	if(outputWavFile != null) {
	    	ArrayList<Play> playList = new ArrayList<Play>();	    	
	    	int find, noteKey, noteFrame;
	    	
	    	for(int frame = 0, index = 0;frame < numFrames; frame++) {
	    		// if the midi event occur at this frame
	    		if(frame >= midi.getNote(index).getSecond()*sampleRate && index < midi.getSize()) {
	    			find = findElm(playList, midi.getNote(index).getKey());
	    			// if key == on && key exist
	    			if(midi.getNote(index).getSwitch() && wavData[midi.getNote(index).getKey()] != null) {
	    				// if the note is already playing, restart playing it.
	    				if(find != NOT_FIND) {
	    					playList.get(find).resetFrame();
	    				}
	    				// if the note is off
	    				else {
	    					// add this note to current frame 
	    					playList.add(new Play(midi.getNote(index).getKey(),
	    							0,
	    							wavData[midi.getNote(index).getKey()].getNumFrames()));
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
	    			frameBuffer[0][0] += (wavDataBuffer[noteKey][0][noteFrame]/2);
	    			frameBuffer[1][0] += (wavDataBuffer[noteKey][1][noteFrame]/2);
	    			playList.get(play).nextFrame();
	    		}
	    		// write buffer to output
	    		try {
					outputWavFile.writeFrames(frameBuffer, 1);
				} catch (IOException e) {
					System.err.println("outputWavFile IOException @frame: "+frame+", @index: "+index);
					e.printStackTrace();
				} catch (WavFileException e) {
					System.err.println("outputWavFile WavFileException @frame: "+frame+", @index: "+index);
					e.printStackTrace();
				}
	
	    	}
    	}
    	else {
    		System.out.println("WavMixer Error: Output File haven't been open!");
    	}
        return;
    }

}