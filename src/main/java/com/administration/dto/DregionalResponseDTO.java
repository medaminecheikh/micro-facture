package com.administration.dto;

import lombok.Data;

import java.util.List;

@Data
public class DregionalResponseDTO {
    private String idDr;
    private String cod_DR;
    private String dr;

    private String drAr;

    private ZoneUpdateDTO zone;

    private List<EttUpdateDTO> etts;
    private long totalElements;
}
