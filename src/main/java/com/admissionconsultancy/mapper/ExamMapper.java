package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.ExamDTO;
import com.admissionconsultancy.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    ExamDTO toDTO(Exam exam);

    void updateEntityFromDTO(ExamDTO examDTO, @MappingTarget Exam exam);
}