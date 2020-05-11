package com.osadchuk.technicalAndEconomicValuesCalculator.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class DocxFileWriterService {

	private static final Gson GSON = new Gson();

	private final TEVService tevService;

	@Autowired
	public DocxFileWriterService(TEVService tevService) {
		this.tevService = tevService;
	}

	public Resource createResultFile(String inputFileName) throws FileNotFoundException {
		Map<String, Object> initialDataMap = GSON.fromJson(new JsonReader(new FileReader(inputFileName)), Map.class);
		Map<String, Object> resultMap = tevService.calculateTEV(initialDataMap);

		XWPFDocument document = new XWPFDocument();
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setFontSize(14);
		run.setText("Результати обчислення ТЕП програмного продукту");
		run.addBreak();

		XWPFTable table = document.createTable();
		table.setTableAlignment(TableRowAlign.LEFT);
		table.setWidthType(TableWidthType.PCT);
		table.setWidth("100%");
		XWPFTableRow tableHeader = table.getRow(0);
		tableHeader.getCell(0).setText("Показник");
		tableHeader.addNewTableCell().setText("Значення");

		for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
			XWPFTableRow tableRow = table.createRow();
			tableRow.getCell(0).setText(entry.getKey());
			tableRow.getCell(1).setText(entry.getValue().toString());
		}
		String resultFileName = "result_" + inputFileName;
		resultFileName = resultFileName.replace(".json", ".docx");
		try (FileOutputStream out = new FileOutputStream(new File(resultFileName))) {
			document.write(out);
			File file = new File(resultFileName);
			Resource resource = new UrlResource(file.toURI());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + resultFileName);
			}
		} catch (IOException ex) {
			throw new FileNotFoundException("File not found " + resultFileName);
		}
	}
}
