package onePercentInstrumentSkill;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/* Warning:
 * 1. Currently, we don't know the length of output file.
 */

public class AudioEdit {
	private int numberOfSamples;
	private AudioInputStream[] sounds;
	private AudioFormat format;	// all stream must have same format
	private DataLine.Info dataInfo;
	private static SourceDataLine ausgabe;
	/*
	 * Constructor: AudioEdit(ArrayList<String> filePaths);
	 */
	public AudioEdit(ArrayList<String> filePaths) {
		try {
			numberOfSamples = filePaths.size();
			sounds =  new AudioInputStream[numberOfSamples];
			for(int i = 0; i < numberOfSamples; i++) {
				sounds[i] = AudioSystem.getAudioInputStream(new File(filePaths.get(i)));
			}
			format = sounds[0].getFormat();
			dataInfo = new DataLine.Info(SourceDataLine.class, format);
			ausgabe=(SourceDataLine)AudioSystem.getLine(dataInfo);
		} catch(Exception bug) {
			System.err.println(bug);
		}
	}
}
