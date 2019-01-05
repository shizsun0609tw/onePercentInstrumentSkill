package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class VideoProcessTest {
	public static void main(String[] args) {
		try {
			System.out.println("hi");
			MidiHandler handlerTest = new MidiHandler("src/test/resources/This_Game.midi");
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
