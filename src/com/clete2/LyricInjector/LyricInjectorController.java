package com.clete2.LyricInjector;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

public class LyricInjectorController {
	private JLabel threadStatusLabel;
	private ExecutorService lyricThreadPool;
	private MusicScanner musicScanner;

	public LyricInjectorController() {
		this.initalize();
	}

	private void initalize() {
		this.lyricThreadPool = null;
		this.musicScanner = new MusicScanner();
		
		this.threadStatusLabel = new JLabel(this.getThreadStatusLabelText());
		new LyricInjectorView(this, threadStatusLabel);
	}

	public void injectLyrics(final String path) {
		Runnable lyricInjection = new Runnable() {
			public void run() {

				injectLyricsForPath(path);
			}
		};
		new Thread(lyricInjection).start();
	}

	public String getThreadStatusLabelText() {
		String status;

		if(this.lyricThreadPool == null
				|| this.lyricThreadPool.isShutdown() 
				|| this.lyricThreadPool.isTerminated()) {
			status = "Idle";
		} else {
			status = "Active";
		}

		return status;
	}
	
	private void injectLyricsForPath(String path) {
		// Create a thread pool that will inject lyrics into audio files
		// Start up 3 times as many threads as logical processors
		this.lyricThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
		
		// Get all of the audio files from the given path and store each path in an ArrayList
		//ArrayList<Path> audioPaths = musicScanner.getAudioListFromPath("/Users/Clete2/Desktop/Music/");
		ArrayList<Path> audioPaths = musicScanner.getAudioListFromPath(path);
		// Store lyric injectors for each Path
		ArrayList<LyricInjector> lyricInjectors = new ArrayList<LyricInjector>();

		// Create and store all lyric injectors
		for(Path audioPath : audioPaths) {
			lyricInjectors.add(new LyricInjector(audioPath));
		}

		try {
			// 30 second timeout
			this.lyricThreadPool.invokeAll(lyricInjectors, 30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			this.lyricThreadPool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		this.lyricThreadPool.shutdown();
		this.lyricThreadPool = null;
	}
}
