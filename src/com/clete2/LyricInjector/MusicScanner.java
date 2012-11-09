/**
 * 
 */
package com.clete2.LyricInjector;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

public class MusicScanner {
	public ArrayList<Path> getAudioListFromPath(String pathStart) {
		final ArrayList<Path> audioList = new ArrayList<Path>();
		final MimeUtil2 mimeUtil = new MimeUtil2();
		mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		Path pathToScan = Paths.get(pathStart);
		
		try{
			Files.walkFileTree(pathToScan, new SimpleFileVisitor<Path>() {  
				@SuppressWarnings("static-access")
				@Override  
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs)  
						throws IOException {  
					MimeType fileMime = mimeUtil.getMostSpecificMimeType(mimeUtil.getMimeTypes(filePath.toString()));
					if(fileMime.toString().toLowerCase().startsWith("audio/")) {
						audioList.add(filePath);
					}
			return FileVisitResult.CONTINUE;
				}  
			});
		} catch(IOException e) {
			// TODO: Log exceptions
		}
		return audioList;
	}
}
