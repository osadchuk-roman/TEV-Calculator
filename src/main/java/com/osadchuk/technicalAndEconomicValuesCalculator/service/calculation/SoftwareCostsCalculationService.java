package com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SoftwareCostsCalculationService {

	private final SalaryCalculationService salaryCalculationService;

	private final PCMaintenanceCostsCalculationService pcMaintenanceCostsCalculationService;

	@Autowired
	public SoftwareCostsCalculationService(SalaryCalculationService salaryCalculationService,
	                                       PCMaintenanceCostsCalculationService pcMaintenanceCostsCalculationService) {
		this.salaryCalculationService = salaryCalculationService;
		this.pcMaintenanceCostsCalculationService = pcMaintenanceCostsCalculationService;
	}

	public Map<String, Object> calculateSoftwareCost(Map<String, Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();

		double cPP = calculateCPP(initialData);
		resultMap.put("Собівартість програмного продукту (грн.)", Math.round(cPP * 100) / 100.0d);

		return resultMap;
	}

	public double calculateCPP(Map<String, Object> initialData) {
		return salaryCalculationService.calculateZpVykOcn(initialData)
				+ salaryCalculationService.calculateZpVykDod(initialData)
				+ salaryCalculationService.calculateBECB(initialData)
				+ pcMaintenanceCostsCalculationService.calculateBPP(initialData);
	}

}
