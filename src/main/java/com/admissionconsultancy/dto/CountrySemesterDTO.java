package com.admissionconsultancy.dto;

import lombok.Data;

import java.util.List;

@Data
public class CountrySemesterDTO {
    private String semester;
    private List<String> subjects;
}
