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
	
	public static void start() throws InvalidMidiDataException, IOException {
		final String midiName = "This_Game.midi";
		final String folderName = "src/test/resources/";
		final String outputName = "MergeTest";
		
		// This will make tmp folder @./tmp/ directly.
		
		MidiHandler handlerTest = new MidiHandler(folderName + midiName);
		SplitFiles splitTest = new SplitFiles(folderName, handlerTest);
		// Warning: If we don't run wavTest.start(), the wavOutput.wav will be a blank file.
		// because "new WavMixer(args...)" will always cover the exist file.
		WavMixer wavTest = new WavMixer(handlerTest, "wavOutput.wav", 48000);
		VideoProcess videoTest = new VideoProcess(folderName, handlerTest);
		MergeFiles mergeTest = new MergeFiles("./", outputName);
		
		// Start split mp4 file to wav, store at ./tmp/
		splitTest.start();
		// Start loading wav files & mixing audio
    	wavTest.start();
    	// Start loading video files & editing video
    	videoTest.start();
    	// Merge audio and video
    	mergeTest.start();
    	tmpFolder.deleteTmpFolder();
	}
	
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		OnePercentInstrumentSkill opis = new OnePercentInstrumentSkill();
		Select gui = new Select();
		gui.GUI();
	}
}
