package com.osadchuk.technicalAndEconomicValuesCalculator.controller;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.osadchuk.technicalAndEconomicValuesCalculator.service.TEVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Controller
public class MainController {

	private static final String UPLOAD_DIR = "upload/";

	private static final Gson GSON = new Gson();

	@Value("classpath:static/variant20.json")
	private Resource initialDataResource;

	@Value("classpath:upload")
	private Resource uploadResource;

	private final TEVService tevService;

	@Autowired
	public MainController(TEVService tevService) {
		this.tevService = tevService;
	}

	@GetMapping
	public String main(Model model) {
		return "main";
	}

	@PostMapping("/uploadFile")
	public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
		Path path = Paths.get(file.getOriginalFilename());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		Map<String, Object> initialDataMap = GSON.fromJson(new JsonReader(new FileReader(file.getOriginalFilename())), Map.class);
		Map<String, Object> resultMap = tevService.calculateTEV(initialDataMap);
		model.addAttribute("resultMap", resultMap);
		model.addAttribute("filename", file.getOriginalFilename());
		return "main";
	}
}
