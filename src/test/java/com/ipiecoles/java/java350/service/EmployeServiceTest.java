package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ipiecoles.java.java350.model.Entreprise.PERFORMANCE_BASE;
import static java.lang.String.format;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.85, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        //When/Then
        EntityExistsException e = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When/Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
    }

    //Test intégré calculPerformanceCommercial
    @Test
    public void testCalculPerformanceCommercialObjectifCANull() {
        // Given
        String matricule = "C00001";
        Long caTraite = 105000L;
        Long objectifCA = null;

        // When
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException exception) {
            // Then
            Assertions.assertEquals("L'objectif du C.A ne peut être négatif ou null !",
                    exception.getMessage());
        }

    }

    @Test
    public void testCalculPerformanceCommercialObjectifCAEtCaTraiteNull() {
        // Given
        String matricule = "C00001";
        Long caTraite = null;
        Long objectifCA = 20000L;

        // When
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException exception) {
            // Then
            Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !",
                    exception.getMessage());
        }

    }

    @Test
    public void testCalculPerformanceCommercialMatriculeNull() {
        // Given
        String matricule = null;
        Long caTraite = 25000L;
        Long objectifCA = 20000L;

        // When
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException e2) {
            // Then
            Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !",
                    e2.getMessage());
        }

    }


  @Test
    public void testCalculPerformanceCommercialInexistant() throws EmployeException {
        //Given
        String matricule = "C00000";
        Long caTraite = 10000L;
        Long objectifCA = 8000L;


        //When
      try {
          Employe employe = employeRepository.findByMatricule("C00002");
          employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
          Assertions.fail("Doit lancer un exception");
      } catch (EmployeException e3) {
          //Then
          Assertions.assertEquals("Le matricule " + matricule + " n'existe pas !",
                  e3.getMessage());
      }

    }

    @Test
    public void testCalculPerformanceCommercialCatraiteNagatif() throws EmployeException {
        //Given
        String matricule = "C00003";
        Long caTraite = -100L;
        Long objectifCA = 8000L;


        //When
        try {
            Employe employe = employeRepository.findByMatricule("C00003");
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException e4) {
            //Then
            Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !",
                    e4.getMessage());
        }

    }
    @Test
    public void testCalculPerformanceCommercialObjectifCAeNagatif() throws EmployeException {
        //Given
        String matricule = "C00003";
        Long caTraite = 10000L;
        Long objectifCA = -800L;


        //When
        try {
            Employe employe = employeRepository.findByMatricule("C00003");
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException e5) {
            //Then
            Assertions.assertEquals("L'objectif du C.A ne peut être négatif ou null !",
                    e5.getMessage());
        }

    }

    @Test

  public void testCalculPerformanceCommercialAvecPerformanceDefinit() throws EmployeException {
        //Given
        String matricule = "";
        Long caTraite = 10000L;
        Long objectifCA = 8000L;


        //When
        try {
            Employe employe = employeRepository.findByMatricule("");
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCA);
            Assertions.fail("Doit lancer un exception");
        } catch (EmployeException e6) {
            //Then
            Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !",
                    e6.getMessage());
        }

    }
}




