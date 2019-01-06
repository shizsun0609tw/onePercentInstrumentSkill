package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class WavMixerTest {
    public static void main(String[] args) {
    	MidiHandler midi;
		try {
			midi = new MidiHandler("src/test/resources/Brave Shine.mid");
	    	String outputFileName = "wavOutput.wav";
	    	// it will output to ./tmp/outputFileName
	    	WavMixer temp = new WavMixer(midi, outputFileName, 48000);
	    	
	    	temp.start();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
