package com.ipiecoles.java.java350.service;


import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.*;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ipiecoles.java.java350.model.Entreprise.PERFORMANCE_BASE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void integrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("T12346");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T12346", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employe.getSalaire().doubleValue());
    }

    //Test d'integration d'un cas nominal
    @Test
    public void testIntegrationCalculPerformanceCommercial() throws EmployeException {

        //Given
        String nom = "Dupont";
        String prenom = "Paul";
        Integer performance = 4;
        Double tempsPartiel = 1.0;
        Long caTraite = 160000L;
        Long objectifCa = 120000L;
        employeRepository.save(new Employe(nom, prenom, "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, performance, tempsPartiel));


        // When
        Employe employe = employeRepository.findByMatricule("C12345");
        employeService.calculPerformanceCommercial(employe.getMatricule(), caTraite, objectifCa);

        // Then
        employe = employeRepository.findByMatricule("C12345");

        // Calcul à la main de la performance : 4(performance de base)+ 1(caTraite > objectifCa*1.05)+ 4(caTraite > objectifCa*1.2) =9
        Assertions.assertEquals(8, employe.getPerformance().intValue());
    }

    @Test
    public void testIntegrationCalculPerformanceCommercialPerformanceMoins2() throws EmployeException {

        //Given
        String nom = "Dujardin";
        String prenom = "Jean";
        Integer performance = -2;
        Double tempsPartiel = 1.0;
        Long caTraite = 110000L;
        Long objectifCa = 120000L;
        employeRepository.save(new Employe(nom, prenom, "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, performance, tempsPartiel));


        // When
        Employe employe = employeRepository.findByMatricule("C12345");
        if(caTraite >= objectifCa*0.8 && caTraite < objectifCa*0.95){
           performance = Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance() - 2);
        }
        employeService.calculPerformanceCommercial(employe.getMatricule(), caTraite, objectifCa);

        // Then
        employe = employeRepository.findByMatricule("C12345");

        Assertions.assertEquals(1, employe.getPerformance().intValue());
    }

    @Test
    public void testIntegrationCalculPerformanceCommercialPerformanceBase() throws EmployeException {

        //Given
        String nom = "Rochefort";
        String prenom = "Jean";
        Integer performance = PERFORMANCE_BASE;
        Double tempsPartiel = 1.0;
        Long caTraite = 110000L;
        Long objectifCa = 120000L;
        employeRepository.save(new Employe(nom, prenom, "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, performance, tempsPartiel));


        // When
        Employe employe = employeRepository.findByMatricule("C12345");
        employeService.calculPerformanceCommercial(employe.getMatricule(), caTraite, objectifCa);
        if(caTraite >= objectifCa*0.95 && caTraite <= objectifCa*1.05){
            performance = Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance());
        }


        // Then
        employe = employeRepository.findByMatricule("C12345");

        Assertions.assertEquals(1, employe.getPerformance().intValue());
    }

    @Test
    public void testIntegrationCalculPerformanceCommercialPerformancePlusUn() throws EmployeException {

        //Given
        String nom = "Diaz";
        String prenom = "Cameron";
        Integer performance = +1;
        Double tempsPartiel = 1.0;
        Long caTraite = 110000L;
        Long objectifCa = 120000L;
        employeRepository.save(new Employe(nom, prenom, "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, performance, tempsPartiel));


        // When
        Employe employe = employeRepository.findByMatricule("C12345");
        employeService.calculPerformanceCommercial(employe.getMatricule(), caTraite, objectifCa);
        if(caTraite <= objectifCa*1.2 && caTraite > objectifCa*1.05){



        // Then
        employe = employeRepository.findByMatricule("C12345");

        Assertions.assertEquals(1, employe.getPerformance().intValue());
    }


    }
}