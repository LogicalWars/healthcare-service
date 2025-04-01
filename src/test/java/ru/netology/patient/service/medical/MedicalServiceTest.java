package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedicalServiceTest {

    private final List<PatientInfo> listOfPatientInfo = List.of(
            new PatientInfo("0", "David", "Jef", LocalDate.of(1980, 11, 26),
                    new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))),
            new PatientInfo("1", "David", "Jef", LocalDate.of(1980, 11, 26),
                    new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)))
            );

    private final PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    private final SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
    private final MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);


    @Test
    void test_check_blood_pressure_when_above() {
        //arrange
        Mockito.when(patientInfoRepository.getById("0")).thenReturn(listOfPatientInfo.get(0));
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(listOfPatientInfo.get(1));

        String message = String.format("Warning, patient with id: %s, need help", listOfPatientInfo.get(0).getId());

        //act
        medicalService.checkBloodPressure("0", new BloodPressure(130, 80));
        medicalService.checkBloodPressure("1", new BloodPressure(120, 80));

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService, Mockito.times(1)).send(message);
        Mockito.verify(sendAlertService).send(argument.capture());
        assertEquals(message, argument.getValue());
    }

    @Test
    void test_check_blood_pressure_when_null_patient_info() {
        //arrange
        Mockito.when(patientInfoRepository.getById("0")).thenReturn(null);

        //assert
        Assertions.assertThrows(RuntimeException.class, () -> medicalService.checkBloodPressure("0", new BloodPressure(130, 80)));
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void test_check_temperature_when_above() {
        //arrange
        Mockito.when(patientInfoRepository.getById("0")).thenReturn(listOfPatientInfo.get(0));
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(listOfPatientInfo.get(1));

        String message = String.format("Warning, patient with id: %s, need help", listOfPatientInfo.get(0).getId());

        //act
        medicalService.checkTemperature("0", new BigDecimal("30.9"));
        medicalService.checkTemperature("1", new BigDecimal("36.6"));

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService, Mockito.times(1)).send(message);
        Mockito.verify(sendAlertService).send(argument.capture());
        assertEquals(message, argument.getValue());
    }

    @Test
    void test_check_temperature_when_null_patient_info() {
        //arrange
        Mockito.when(patientInfoRepository.getById("0")).thenReturn(null);

        //assert
        Assertions.assertThrows(RuntimeException.class, () -> medicalService.checkTemperature("0", new BigDecimal("30.9")));
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }
}
