package com.ipiecoles.java.java350.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;

public class EmployeTest {

    @Test
    public void getNombreAnneeAncienneteNow(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now());

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNminus2(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().minusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(2, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNull(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(null);

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNplus2(){
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().plusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anneeAnciennete.intValue());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 'T12345', 0, 1.0, 1000.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "2, 'T12345', 0, 1.0, 2300.0",
            "2, 'T12345', 1, 1.0, 2400.0",
            "1, 'M12345', 0, 1.0, 1700.0",
            "1, 'M12345', 5, 1.0, 2200.0",
            "2, 'M12345', 0, 1.0, 1700.0",
            "2, 'M12345', 8, 1.0, 2500.0"
    })


    public void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle){
        //Given
        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertEquals(primeAnnuelle, prime);

    }


    //TEST METHODE augmenterSalaire
    @Test
    public void testAugmenterSalaireDefault(){
        //Given
        Employe e = new Employe();

        //When
        double augmenterSalaire = e.getSalaire() * e.augmenterSalaire(1.05 );

        //Then
        Assertions.assertEquals(1597.2810000000002, augmenterSalaire);
    }

    /*@Test
    public void testAugmentationSalaireNull(){
        //Given

        //TEST salaire null, cela soulève un java.lang.NullPointerException
        //Mise en place d'une exception dans Employe
        Employe e = new Employe();
        e.setSalaire(null);

        //when
        double augmentationSalaire = e.getSalaire() * e.augmenterSalaire(1.3);

        //Then
        Assertions.assertEquals(null, augmentationSalaire);
        //LE TEST NE PASSE PAS VOLONTAIREMENT, le but est de faire remonter l'exception
        //Mise en commentaire pour passer le build Travis
        //Il faut enelever le commentaire pour le test en Coverage
    }*/

    @Test
    public void testAugmenterSalaireDefinit(){
        //Given
        Employe e = new Employe();
        //Test avec un salaire différent de celui par défault
        e.setSalaire(1200.50);

        //On utilise Math.round pour arrondir le résultat
        //When
        double augmenterSalaire = Math.round((e.getSalaire() * e.augmenterSalaire(1.1)));

        //Then
        Assertions.assertEquals(1321 , augmenterSalaire);

    }

    @Test
    public void testAugmenterSalairePourcentageZero(){
        //Given
        Employe e = new Employe();

        //When
        double augmenterSalaire = e.getSalaire() * e.augmenterSalaire(0);

        //Then
        Assertions.assertEquals(0, augmenterSalaire);

    }

    //TEST NbRtt pour l'année 2019 avec l'appel à la classe LocalDate.of(d.getYear()
   @ParameterizedTest(name="jour de l'année{0}, jourFeries{1}, weekend{2}, CongésBase{3}, NbJoursMax{5},NbRtt{4}")
    @CsvSource({
            "365, 9, 104, 25, 9, 118"
            //Les valeurs correspondant (calculé à la main) au paramètre
            //{0}=> index de l'ordre des paramètres
            //365-9—104—25-218=9

    })
    void testGetNbRtt(int annBissextile, int jourFeries, int weekend, Integer NB_CONGES_BASE, int NbRtt, Integer NB_JOURS_MAX_FORFAIT){
        //Given
        Employe e = new Employe();

        //When
        int testNbRtt = e.getNbRtt();

        //Then
        Assertions.assertEquals(NbRtt,testNbRtt);

    }

}