package com.osadchuk.technicalAndEconomicValuesCalculator.service.calculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PCMaintenanceCostsCalculationService {
	private final TimeAndComplexityCalculationService timeAndComplexityCalculationService;

	@Autowired
	public PCMaintenanceCostsCalculationService(TimeAndComplexityCalculationService timeAndComplexityCalculationService) {
		this.timeAndComplexityCalculationService = timeAndComplexityCalculationService;
	}

	public Map<String, Object> calculatePCMaintenanceCosts(Map<String, Object> initialData) {
		Map<String, Object> resultMap = new LinkedHashMap<>();
		double bPP = calculateBPP(initialData);
		resultMap.put("Загальні витрати на утримання і експлуатацію ПЕОМ при виконанні проекту (грн.)", Math.round(bPP * 100) / 100.0d);

		return resultMap;
	}

	public double calculateBPP(Map<String, Object> initialData) {
		double tROB = 12 * 20 * (double) initialData.get("Середня тривалість робочого дня, год.") * 0.9;
		double C = (double) initialData.get("Вартість одного кВт, грн..");
		double pEOM = (double) initialData.get("Сумарна потужність ПЕОМ, кВт/год.");
		double pOCB = (double) initialData.get("Сумарна потужність, яка йде на освітлення, кВт/год.");
		double bB = (double) initialData.get("Балансова вартість комплексу ПЕОМ, грн.");
		double nP = (double) initialData.get("Термін використання ПЕОМ, роки");

		double bPC = tROB * C * pEOM;
		double bOCB = tROB * C * pOCB;

		double bEH = bPC + bOCB;

		double zpZAH = calculateZpZahForAll(initialData);

		double bObslEcbZAH = (zpZAH * 34.7) / 100;

		double bM = bB * 0.02;
		double bProf = bB * 0.03;
		double A = bB / nP;

		double bSYM = bEH + bM + bProf + A + zpZAH + bObslEcbZAH;

		double cMHour = bSYM / tROB;

		return cMHour * timeAndComplexityCalculationService.calculateTZAH(initialData);
	}

	private double calculateZpZahForAll(Map<String, Object> initialData) {
		return calculateZpObslOcnEngnr(initialData) + calculateZpObslDodEngnr(initialData)
				+ calculateZpObslOcnOper(initialData) + calculateZpObslDodOper(initialData)
				+ calculateZpObslOcnSysProg(initialData) + calculateZpObslDodSysProg(initialData);
	}

	private double calculateZpObslOcnEngnr(Map<String, Object> initialData) {
		double Prem = (double) initialData.get("Середній відсоток премії, %");
		double z1Engnr = (double) initialData.get("Щомісячна зарплатня інженера 1-го розряду, грн.");
		double kTEngnr = (double) initialData.get("Тарифний коефіцієнт (інженер)");
		double nObslEngnr = (double) initialData.get("Норма обслуговування (інженер)");
		return ((z1Engnr * kTEngnr) / nObslEngnr) * (1 + Prem / 100) * timeAndComplexityCalculationService.calculateT(initialData);
	}

	private double calculateZpObslOcnOper(Map<String, Object> initialData) {
		double Prem = (double) initialData.get("Середній відсоток премії, %");
		double z1Oper = (double) initialData.get("Щомісячна зарплатня оператора, грн.");
		double kTOper = (double) initialData.get("Тарифний коефіцієнт (оператор)");
		double nObslOper = (double) initialData.get("Норма обслуговування (оператор)");
		return ((z1Oper * kTOper) / nObslOper) * (1 + Prem / 100) * timeAndComplexityCalculationService.calculateT(initialData);
	}

	private double calculateZpObslOcnSysProg(Map<String, Object> initialData) {
		double Prem = (double) initialData.get("Середній відсоток премії, %");
		double z1SysProg = (double) initialData.get("Щомісячна зарплатня системного програміста, грн.");
		double kTSysProg = (double) initialData.get("Тарифний коефіцієнт (системний програміст)");
		double nObslSysProg = (double) initialData.get("Норма обслуговування (системний програміст)");
		return ((z1SysProg * kTSysProg) / nObslSysProg) * (1 + Prem / 100) * timeAndComplexityCalculationService.calculateT(initialData);
	}

	private double calculateZpObslDodEngnr(Map<String, Object> initialData) {
		double D = (double) initialData.get("Відсоток додаткової заробітної праці, %");
		return calculateZpObslOcnEngnr(initialData) * (D / 100);
	}

	private double calculateZpObslDodOper(Map<String, Object> initialData) {
		double D = (double) initialData.get("Відсоток додаткової заробітної праці, %");
		return calculateZpObslOcnOper(initialData) * (D / 100);
	}

	private double calculateZpObslDodSysProg(Map<String, Object> initialData) {
		double D = (double) initialData.get("Відсоток додаткової заробітної праці, %");
		return calculateZpObslOcnSysProg(initialData) * (D / 100);
	}
}
