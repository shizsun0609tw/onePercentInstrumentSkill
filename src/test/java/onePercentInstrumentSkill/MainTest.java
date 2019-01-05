package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class MainTest {
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		final String midiName = "This_Game.midi";
		final String folderName = "src/test/resources/";
		final String outputName = "MergeTest";
		
		MidiHandler handlerTest = new MidiHandler(folderName + midiName);
		WavMixer wavTest = new WavMixer(folderName, handlerTest, "wavOutput.wav", 48000);
		VideoProcess videoTest = new VideoProcess(folderName, handlerTest);
		MergeFiles mergeTest = new MergeFiles("./", outputName);
		
		// Start loading wav files & mixing audio
    	wavTest.start();
    	// Start loading video files & editing video
    	videoTest.start();
    	// Merge audio and video
    	mergeTest.start();
	}
}
