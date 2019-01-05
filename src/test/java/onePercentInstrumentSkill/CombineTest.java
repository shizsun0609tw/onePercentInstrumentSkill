package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class CombineTest {
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		final String midiName = "This_Game.midi";
		final String folderName = "src/test/resources/";
		final String outputName = "MergeTest";
		
		// This will make tmp folder @./tmp/ directly.
		MakeTmpFolder tmpFolder = new MakeTmpFolder();
		MidiHandler handlerTest = new MidiHandler(folderName + midiName);
		SplitFiles splitTest = new SplitFiles(folderName, handlerTest);
		// Warning: If we don't run wavTest.start(), the wavOutput.wav will be a blank file.
		// because "new WavMixer(args...)" will always cover the exist file.
		WavMixer wavTest = new WavMixer(handlerTest, "wavOutput.wav", 48000);
		VideoProcess videoTest = new VideoProcess(folderName, handlerTest, "src/test/resources/backGroundTest.jpg");
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
}
