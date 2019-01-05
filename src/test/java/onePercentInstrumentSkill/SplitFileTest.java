package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class SplitFileTest {
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		String folderPath = "src/test/resources/";
		String midiFile = "This_Game.midi";
		MidiHandler midi = new MidiHandler(folderPath+midiFile);
		SplitFiles split = new SplitFiles(folderPath, midi);
		
		split.start();
	}
}
