/*
 * Copyright (c) 2020. Roman Osadchuk.
 */

package com.osadchuk.technicalAndEconomicValuesCalculator.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileSystemUtil {

	private FileSystemUtil() {
	}

	public static void deleteFile(String filename, int delay) {
		Executors.newScheduledThreadPool(1).schedule(() -> {
			try {
				log.info("Deleting file {}", filename);
				Files.delete(new File(filename).toPath());
			} catch (IOException e) {
				log.error("Failed to delete file {}", filename, e);
			}
		}, delay, TimeUnit.SECONDS);
	}
}
