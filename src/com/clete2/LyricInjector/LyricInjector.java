package com.clete2.LyricInjector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class LyricInjector implements Callable<Boolean> {
	private LyricWiki lyricWiki;
	private Path audioFilePath;

	public LyricInjector(Path audioFilePath) {
		this.lyricWiki = new LyricWiki();
		this.audioFilePath = audioFilePath;
	}

	/**
	 * Given a Path for a file, injects lyrics by using the tag to find the song online.
	 * @param audioFilePath Path to a single music file.
	 */
	public void injectLyrics(Path audioFilePath) {
		AudioFile audioFile;
		try {
			audioFile = AudioFileIO.read(audioFilePath.toFile());
			Tag audioFileTag = audioFile.getTag();

			String lyrics = lyricWiki.getLyrics(audioFileTag.getFirst(FieldKey.ARTIST),
					audioFileTag.getFirst(FieldKey.TITLE)).trim();

			if(lyrics.equals("") || lyrics == null) {
				System.out.println(audioFileTag.getFirst(FieldKey.ARTIST) +" - "+
						audioFileTag.getFirst(FieldKey.TITLE) +
						" - No lyrics found.");
			} else if(lyrics.equals(audioFileTag.getFirst(FieldKey.LYRICS))) {
				System.out.println(audioFileTag.getFirst(FieldKey.ARTIST) +" - "+
						audioFileTag.getFirst(FieldKey.TITLE) +
						" - Not setting lyrics as they are identical to the ones already set.");
			} else {
				System.out.println(audioFileTag.getFirst(FieldKey.ARTIST) +" - "+
						audioFileTag.getFirst(FieldKey.TITLE) +
						" - Setting lyrics.");
				audioFileTag.setField(FieldKey.LYRICS, lyrics);
				audioFile.commit();
			}
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException | 
				KeyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public Boolean call() throws Exception {
		this.injectLyrics(this.audioFilePath);
		// TODO: Change return!
		return null;
	}
}
