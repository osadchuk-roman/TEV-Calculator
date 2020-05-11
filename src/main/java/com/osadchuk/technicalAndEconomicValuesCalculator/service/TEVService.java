package com.osadchuk.technicalAndEconomicValuesCalculator.service;

import com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation.AllSoftwareCostsCalculationService;
import com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation.PCMaintenanceCostsCalculationService;
import com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation.SalaryCalculationService;
import com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation.SoftwareCostsCalculationService;
import com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation.TimeAndComplexityCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TEVService {

	private final TimeAndComplexityCalculationService timeAndComplexityCalculationService;

	private final SalaryCalculationService salaryCalculationService;

	private final PCMaintenanceCostsCalculationService pcMaintenanceCostsCalculationService;

	private final SoftwareCostsCalculationService softwareCostsCalculationService;

	private final AllSoftwareCostsCalculationService allSoftwareCostsCalculationService;

	@Autowired
	public TEVService(TimeAndComplexityCalculationService timeAndComplexityCalculationService,
	                  SalaryCalculationService salaryCalculationService,
	                  PCMaintenanceCostsCalculationService pcMaintenanceCostsCalculationService,
	                  SoftwareCostsCalculationService softwareCostsCalculationService,
	                  AllSoftwareCostsCalculationService allSoftwareCostsCalculationService) {
		this.timeAndComplexityCalculationService = timeAndComplexityCalculationService;
		this.salaryCalculationService = salaryCalculationService;
		this.pcMaintenanceCostsCalculationService = pcMaintenanceCostsCalculationService;
		this.softwareCostsCalculationService = softwareCostsCalculationService;
		this.allSoftwareCostsCalculationService = allSoftwareCostsCalculationService;
	}

	public Map<String, Object> calculateTEV(Map<java.lang.String, java.lang.Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();

		resultMap.putAll(timeAndComplexityCalculationService.calculateTimeAndComplexity(initialData));
		resultMap.putAll(salaryCalculationService.calculateSalary(initialData));
		resultMap.putAll(pcMaintenanceCostsCalculationService.calculatePCMaintenanceCosts(initialData));
		resultMap.putAll(softwareCostsCalculationService.calculateSoftwareCost(initialData));
		resultMap.putAll(allSoftwareCostsCalculationService.calculateSoftwareCost(initialData));

		return resultMap;
	}
}
