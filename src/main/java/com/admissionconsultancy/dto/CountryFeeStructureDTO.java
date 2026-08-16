package com.admissionconsultancy.dto;

import lombok.Data;

@Data
public class CountryFeeStructureDTO {
    private String universityName;

    // Native/Local Currency (Display formatted string e.g., "400,000 RUB" or "6,000 GEL")
    private String tuitionFeeNative;
    private String hostelFeeNative;
    private String nativeCurrencyCode; // e.g., "RUB", "GEL", "EUR"

    // USD Values (Normalized for global filtering, sorting, and comparison charts)
    private Double annualTuitionUsd;
    private Double annualHostelUsd;
}