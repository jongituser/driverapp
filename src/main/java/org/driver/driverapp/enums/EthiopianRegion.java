package org.driver.driverapp.enums;

/**
 * Ethiopian Administrative Regions
 * Based on the 11 regions and 2 chartered cities of Ethiopia
 */
public enum EthiopianRegion {
    ADDIS_ABABA("Addis Ababa", "Addis Ababa"),
    AFAR("Afar", "Afar"),
    AMHARA("Amhara", "Amhara"),
    BENISHANGUL_GUMUZ("Benishangul-Gumuz", "Benishangul-Gumuz"),
    DIRE_DAWA("Dire Dawa", "Dire Dawa"),
    GAMBELLA("Gambella", "Gambella"),
    HARARI("Harari", "Harari"),
    OROMIA("Oromia", "Oromia"),
    SIDAMA("Sidama", "Sidama"),
    SOMALI("Somali", "Somali"),
    SOUTHERN_NATIONS("Southern Nations, Nationalities, and Peoples'", "SNNPR"),
    SOUTH_WEST_ETHIOPIA("South West Ethiopia", "South West Ethiopia"),
    TIGRAY("Tigray", "Tigray");

    private final String fullName;
    private final String shortName;

    EthiopianRegion(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
