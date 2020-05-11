package com.osadchuk.technicalAndEconomicValuesCalculator.controller;

import com.osadchuk.technicalAndEconomicValuesCalculator.service.DocxFileWriterService;
import com.osadchuk.technicalAndEconomicValuesCalculator.util.FileSystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadRestController {

	private final DocxFileWriterService docxFileWriterService;

	@Autowired
	public DownloadRestController(DocxFileWriterService docxFileWriterService) {
		this.docxFileWriterService = docxFileWriterService;
	}

	@GetMapping
	public ResponseEntity<Resource> download(HttpServletRequest request, @RequestParam String filename) throws IOException {// Load file as Resource

		Resource resource = docxFileWriterService.createResultFile(filename);
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			log.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		FileSystemUtil.deleteFile(resource.getFilename(), 5);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
