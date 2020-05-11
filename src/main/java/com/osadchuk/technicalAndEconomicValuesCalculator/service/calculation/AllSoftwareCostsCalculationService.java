package com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AllSoftwareCostsCalculationService {
	private final SoftwareCostsCalculationService softwareCostsCalculationService;

	@Autowired
	public AllSoftwareCostsCalculationService(SoftwareCostsCalculationService softwareCostsCalculationService) {
		this.softwareCostsCalculationService = softwareCostsCalculationService;
	}

	public Map<String, Object> calculateSoftwareCost(Map<String, Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();

		double P = 40;
		double C = softwareCostsCalculationService.calculateCPP(initialData) * (1 + P / 100);
		resultMap.put("Вартість готового програмного продукту (грн.)", Math.round(C * 100) / 100.0d);

		return resultMap;
	}
}
