package onePercentInstrumentSkill;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class OnePercentInstrumentSkill {
	private static MakeTmpFolder tmpFolder;

	public OnePercentInstrumentSkill() throws FileNotFoundException {
		 this.tmpFolder = new MakeTmpFolder();
		 // Initial NoteTable
		 NoteTable.initNoteTable();
	}
	
	public static void start(String midiPath, String folderPath, String backgroundPath) throws InvalidMidiDataException, IOException {

		final String outputName = "Output";
		
		// This will make tmp folder @./tmp/ directly.
		
		MidiHandler handler = new MidiHandler(midiPath);
		SplitFiles split = new SplitFiles(folderPath, handler);
		// Warning: If we don't run wav.start(), the wavOutput.wav will be a blank file.
		// because "new WavMixer(args...)" will always cover the exist file.
		WavMixer wav = new WavMixer(handler, "wavOutput.wav");
		VideoProcess video = new VideoProcess(folderPath, handler, backgroundPath);
		MergeFiles merge = new MergeFiles("./", outputName);
		
		// Start split mp4 file to wav, store at ./tmp/
		split.start();
		// Start loading wav files & mixing audio
    	wav.start();
    	// Start loading video files & editing video
    	video.start();
    	// Merge audio and video
    	merge.start();
    	tmpFolder.deleteTmpFolder();
	}
	
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		if(args.length == 0) {
			try {
				 Runtime.getRuntime().exec(new String[] {"java", "-Xmx6g", "-jar", "OnePercentInstrumentSkill.jar", "test"});  
			}catch(IOException e){
				e.printStackTrace();
			}
			System.exit(0);
		}
		OnePercentInstrumentSkill opis = new OnePercentInstrumentSkill();
		Select gui = new Select();
		gui.GUI();
		Select.setMessage(String.format("set heap size %d MB", Runtime.getRuntime().maxMemory() / 1024 / 1024));
	}
}
