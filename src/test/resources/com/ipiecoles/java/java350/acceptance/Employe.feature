Feature: Employe

  Scenario: Embauche d'un employé
    When J'embauche une personne appelée 'John' 'Doe' diplômée d'un 'CAP' en tant que 'Technicien' avec un taux d'activité de 1.0
    Then J'obtiens bien un nouvel employé appelé 'John' 'Doe' portant le matricule 'T00001' et touchant un salaire de 1521.22 €
