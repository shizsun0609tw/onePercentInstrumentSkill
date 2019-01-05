package onePercentInstrumentSkill;

public class NoteTable {
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static final String[] NOTE_TABLE = new String[128];
    
    public static void initNoteTable() {
		for(int i = 0; i <= 127; i++) {
			if(i >= 21) NOTE_TABLE[i] = NOTE_NAMES[i%12]+(i/12 - 1);
			else NOTE_TABLE[i] = null;
		}
    }
}
