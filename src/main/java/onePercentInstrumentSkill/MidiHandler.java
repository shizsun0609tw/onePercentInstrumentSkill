package onePercentInstrumentSkill;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;

public class MidiHandler{
	public class Note {
	    private double second = 0.0;
	    private long tick = 0;
	    private int Channel = 0;
	    private Boolean on = false;
	    private String name = "C4";
	    private int key = 60;
	    private int velocity = 127;
	    private int bpm = 60;
	    
		public Note(int bpm, long tick, int Channel, Boolean on, String name, int key, int velocity) {
			second = (tick*60.0)/(bpm*1024);
			this.tick = tick;
			this.Channel = Channel;
			this.on =on;
			this.name = name;
			this.key = key;
			this.velocity = velocity;
			this.bpm = bpm;
		}
		public double getSecond() {
			return this.second;
		}
		public long getTick() {
			return this.tick;
		}
		public Boolean getSwitch() {
			return this.on;
		}
		public int getKey() {
			return this.key;
		}
	}
	private ArrayList<Note> notes = new ArrayList<Note>();
	private Note lastNote;
	private double lastNoteSecond = 0.0;
	public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
	public MidiHandler(String path, int bpm) throws InvalidMidiDataException, IOException {
		Note tempNote = null;
		Sequence sequence = MidiSystem.getSequence(new File(path));
        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            //System.out.println("Track " + trackNumber + ": size = " + track.size());
            //System.out.println();
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
                //System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
                    	
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if(sm.getCommand() == NOTE_ON) {
                        	System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        	tempNote = new Note(bpm, event.getTick(), sm.getChannel(), true, noteName + octave, key, velocity);
                        	lastNote = tempNote;
                        }
                        else if (sm.getCommand() == NOTE_OFF) {
                        	System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        	tempNote = new Note(bpm, event.getTick(), sm.getChannel(), false, noteName + octave, key, velocity);
                        	lastNote = tempNote;
                        } 
                        notes.add(tempNote);
                    } else {
                        //System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    //System.out.println("Other message: " + message.getClass());
                }   
            }
            //System.out.println();
        }
        lastNoteSecond = lastNote.getSecond();
	}
	public double getLastSecond() {
		return lastNoteSecond;
	}
	public Note getNote(int index) throws IndexOutOfBoundsException{
		try {
			return notes.get(index);
		} catch (Exception e){
			System.err.println(e);
			return null;
		}
	}
	public static void main(String[] args) throws InvalidMidiDataException, IOException, IndexOutOfBoundsException, NullPointerException {
		MidiHandler mh = new MidiHandler("src/main/resources/test_midi.midi", 120);
		try {
			System.out.println(mh.getNote(5).getSecond());
			System.out.println(mh.getLastSecond());
		}catch(Exception e) {
			System.err.println(e);
		}
		System.out.println("Done !");
	}
}
