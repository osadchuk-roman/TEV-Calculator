package com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class SalaryCalculationService {
	private final TimeAndComplexityCalculationService timeAndComplexityCalculationService;

	@Autowired
	public SalaryCalculationService(TimeAndComplexityCalculationService timeAndComplexityCalculationService) {
		this.timeAndComplexityCalculationService = timeAndComplexityCalculationService;
	}

	public Map<String, Object> calculateSalary(Map<String, Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();

		double zpVykOcn = calculateZpVykOcn(initialData);
		resultMap.put("Основна заробітна платня виконавця (грн.)", Math.round(zpVykOcn * 100) / 100.0d);


		double zpVykDod = calculateZpVykDod(initialData);
		resultMap.put("Додаткова заробітна платня (грн.)", Math.round(zpVykDod * 100) / 100.0d);


		return resultMap;
	}

	public double calculateZpVykOcn(Map<String, Object> initialData) {
		double z1 = (double) initialData.get("Щомісячна зарплатня інженера 1-го розряду, грн.");
		log.debug("z1: {}", z1);
		double kT = (double) initialData.get("Тарифний коефіцієнт");
		log.debug("kT: {}", kT);
		double tZAH = timeAndComplexityCalculationService.calculateTZAH(initialData);
		log.debug("tZAH: {}", tZAH);
		double chP = 20;
		double tPD = (double) initialData.get("Середня тривалість робочого дня, год.");
		log.debug("tPD: {}", tPD);
		double Prem = (double) initialData.get("Середній відсоток премії, %");

		return ((z1 * kT * tZAH) / (chP * tPD)) * (1 + Prem / 100);
	}

	public double calculateZpVykDod(Map<String, Object> initialData) {
		double D = (double) initialData.get("Відсоток додаткової заробітної праці, %");
		return calculateZpVykOcn(initialData) * (D / 100);
	}

	public double calculateZZAH(Map<String, Object> initialData) {
		return calculateZpVykOcn(initialData) + calculateZpVykDod(initialData);
	}

	public double calculateBECB(Map<String, Object> initialData) {
		return (calculateZZAH(initialData) * 34.7) / 100;
	}
}
