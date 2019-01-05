package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class WavMixerTest {
    public static void main(String[] args) {
    	MidiHandler midi;
		try {
			midi = new MidiHandler("src/test/resources/Guren_no_Yumiya.midi");
	    	String outputFilePath = "wavOutput.wav";
	    	
	    	WavMixer temp = new WavMixer(midi, outputFilePath, 48000);
	    	//temp.loadWavFile();
	    	//temp.walkThroughAllFrame();
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
