package onePercentInstrumentSkill;
import java.io.*;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
public class VideoToWav {
	public VideoToWav() {
		File source = new File("src/test/resources/A#4.mp4");
		File target = new File("A#4.wav");
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("pcm_s16le");
		audio.setSamplingRate(48000);
		audio.setChannels(2);
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("wav");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		try {
			System.out.println(encoder.getAudioEncoders());
		} catch (EncoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		VideoToWav temp = new VideoToWav();
	}
}
