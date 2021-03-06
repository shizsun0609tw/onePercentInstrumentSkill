package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class MidiHandlerTest {
	public static void main(String[] args) throws InvalidMidiDataException, IOException, IndexOutOfBoundsException, NullPointerException {
		MidiHandler mh = new MidiHandler("/Users/jasperlin1996/Downloads/dataFromIPhone/jasperA0toC8/Eromanga sensei op.mid");
		int maxkey = 0;
		try {
			for(int i = 0; i < mh.getSize(); i++) {
				System.out.println(i+", Note: "+mh.getNote(i).getName()+", on/off: "+mh.getNote(i).getSwitch()+", @Second: "+mh.getNote(i).getSecond()+ ", BPM: "+mh.getNote(i).getBPM()+", Channel: "+mh.getNote(i).getChannel());
				//if(mh.getNote(i).getKey() > maxkey){maxkey = mh.getNote(i).getKey();}
				System.out.println(mh.getLastSecond());
			}
			System.out.println(mh.getSize());

		}catch(Exception e) {
			System.err.println(e);
		}
		System.out.println("Done !");
	}
}
