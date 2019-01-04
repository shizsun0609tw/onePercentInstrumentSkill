package onePercentInstrumentSkill;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public class MidiHandlerTest {
	public static void main(String[] args) throws InvalidMidiDataException, IOException, IndexOutOfBoundsException, NullPointerException {
		MidiHandler mh = new MidiHandler("src/test/resources/Guren_no_Yumiya.midi");
		int maxkey = 0;
		try {
			for(int i = 0; i < mh.getSize(); i++) {
				//System.out.println(i+", Note: "+mh.getNote(i).getName()+", on/off: "+mh.getNote(i).getSwitch()+", @Second: "+mh.getNote(i).getSecond());
				if(mh.getNote(i).getKey() > maxkey){maxkey = mh.getNote(i).getKey();}
				//System.out.println(mh.getLastSecond());
			}

		}catch(Exception e) {
			System.err.println(e);
		}
		System.out.println("Done !");
	}
}
