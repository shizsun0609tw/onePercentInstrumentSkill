package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class VideoProcessTest {
	public static void main(String[] args) {
		try {
			MidiHandler handlerTest = new MidiHandler("src/test/resources/video_test_midi.midi");
			VideoProcess processTest = new VideoProcess("src/test/resources/", handlerTest);
			processTest.start();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
