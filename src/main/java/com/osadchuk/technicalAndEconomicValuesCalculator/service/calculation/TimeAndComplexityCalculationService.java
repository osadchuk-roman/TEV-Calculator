package com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.osadchuk.technicalAndEconomicValuesCalculator.model.DeveloperQualification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TimeAndComplexityCalculationService {
	private static final Gson GSON = new Gson();

	@Value("classpath:static/softwareTypes.json")
	private Resource softwareTypesResource;

	@Value("classpath:static/noveltyCoefficient.json")
	private Resource noveltyCoefficientResource;

	@Value("classpath:static/developerQualificationCoefficient.json")
	private Resource developerQualificationCoefficientResource;

	private Map<String, Double> softwareTypesMap;

	private Map<String, Map<String, Map<String, Double>>> noveltyCoefficientTypesMap;

	private List<DeveloperQualification> developerQualificationList;

	@PostConstruct
	public void init() {
		try {
			softwareTypesMap = GSON.fromJson(new JsonReader(new FileReader(softwareTypesResource.getFile())), Map.class);
			noveltyCoefficientTypesMap = GSON.fromJson(new JsonReader(new FileReader(noveltyCoefficientResource.getFile())), Map.class);
			developerQualificationList = GSON.fromJson(new JsonReader(new FileReader(developerQualificationCoefficientResource.getFile())),
					new TypeToken<List<DeveloperQualification>>() {
					}.getType());
		} catch (IOException e) {
			log.error("Could not read resource.", e);
		}
	}

	public Map<String, Object> calculateTimeAndComplexity(Map<String, Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		double Q = calculateQ(initialData);
		resultMap.put("Кількість команд вихідного коду (команд)", Q);

		double nTBK = Q / 1000;
		double t = 3.6 * Math.pow(nTBK, 1.2);
		resultMap.put("Трудомісткість, (люд.-міс.)", Math.round(t * 100) / 100.0d);

		double Prod = Math.abs((1000 * nTBK) / t);
		resultMap.put("Продуктивність (вих.ком./люд.-міс.)", Math.round(Prod * 100) / 100.0d);

		double T = 2.5 * Math.pow(t, 0.32);

		double PL = Math.round((t / T) * 100) / 100.0d;
		resultMap.put("Кількість виконавців (люд.)", PL);

		resultMap.put("Час, потрібний на створення програмного продукту (міс.)", Math.round(T * 100) / 100.0d);

		return resultMap;
	}

	public double calculateT(Map<String, Object> initialData) {
		double nTBK = calculateQ(initialData) / 1000;
		double t = 3.6 * Math.pow(nTBK, 1.2);
		return 2.5 * Math.pow(t, 0.32);
	}

	public double calculateTZAH(Map<String, Object> initialData) {
		return calculateTZAH(initialData, calculateQ(initialData));
	}

	private double calculateTZAH(Map<String, Object> initialData, double Q) {
		double B = 1.3;
		double years = (double) initialData.get("Стаж програміста, років");
		double K = developerQualificationList.stream()
				.filter(devQualification -> years >= devQualification.getFrom() && years < devQualification.getTo())
				.map(DeveloperQualification::getCoefficient)
				.findFirst()
				.orElse(1.0);
		double tPO = (double) initialData.get("Час підготовки опису завдання, год.");
		double tO = (Q * B) / (50 * K);
		double tA = Q / (50 * K);
		double tH = (Q * 1.5) / (50 * K);
		double tHT = (Q * 4.2) / (50 * K);
		double tD = (double) initialData.get("Час на оформлення документації, год.");
		return tPO + tO + tA + tH + tHT + tD;
	}

	private double calculateQ(Map<String, Object> initialData) {
		double C = noveltyCoefficientTypesMap.get(initialData.get("Рівень мови програмування"))
				.get(initialData.get("Рівень складності ПП"))
				.get(initialData.get("Рівень новизни ПП"));

		double q = softwareTypesMap.get(initialData.get("Типи задачі"));

		return Math.abs(C * q);
	}
}
