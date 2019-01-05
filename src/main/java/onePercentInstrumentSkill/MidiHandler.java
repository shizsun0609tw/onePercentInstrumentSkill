package onePercentInstrumentSkill;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
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
	    
		public Note(int bpm, long tick, int Channel, Boolean on, String name, int key, int velocity) throws FileNotFoundException {
			second = (tick*60.0)/(bpm*PPQ);
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
		public String getName() {
			return this.name;
		}
		public int getVelocity() {
			return this.velocity;
		}
	}
	private ArrayList<Note> notes = new ArrayList<Note>();
	private Note lastNote;
	private double lastNoteSecond = 0.0;
	private int size = 0;
	private int PPQ;
	private int bpm;
	public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final int SET_TEMPO = 0x51;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private PrintWriter exPrinter;
    
	public MidiHandler(String path) throws InvalidMidiDataException, IOException {
		this.exPrinter = new PrintWriter(new File("./tmp/MidiHandler.txt"));
		
		Note tempNote = null;
		Sequence sequence = MidiSystem.getSequence(new File(path));
        int trackNumber = 0;
        PPQ = sequence.getResolution();
        // find bpm info
        Track[] tracks = sequence.getTracks();
        for (int i = 0; i < tracks[0].size(); i++) {
            MidiEvent event = tracks[0].get(i);
            MidiMessage message = event.getMessage();
            if (message instanceof MetaMessage) {
                MetaMessage mm = (MetaMessage) message;
                if(mm.getType()==SET_TEMPO){
                    // now what?
                    byte[] bpmData = mm.getData();
                    int tempo = (bpmData[0] & 0xff) << 16 | (bpmData[1] & 0xff) << 8 | (bpmData[2] & 0xff);
                    bpm = 60000000 / tempo;
                    break;
                    
                }
            }
        }
        
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            //Select.setMessage("Track " + trackNumber + ": size = " + track.size());
            //Select.setMessage();
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
                        	// Select.setMessage("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        	tempNote = new Note(bpm, event.getTick(), sm.getChannel(), true, noteName + octave, key, velocity);
                        	lastNote = tempNote;
                        }
                        else if (sm.getCommand() == NOTE_OFF) {
                        	// Select.setMessage("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        	tempNote = new Note(bpm, event.getTick(), sm.getChannel(), false, noteName + octave, key, velocity);
                        	lastNote = tempNote;
                        } 
                        notes.add(tempNote);
                    } else {
                        //Select.setMessage("Command:" + sm.getCommand());
                    }
                } else {
                    //Select.setMessage("Other message: " + message.getClass());
                }   
            }
            //Select.setMessage();
        }
        lastNoteSecond = lastNote.getSecond();
        size = notes.size();
	}
	public double getLastSecond() {
		return lastNoteSecond;
	}
	public Note getNote(int index){
		try {
			return notes.get(index);
		} catch (Exception e){
			e.printStackTrace(exPrinter);
			exPrinter.flush();
			return null;
		}
	}
	public int getSize() {
		return size;
	}

}
