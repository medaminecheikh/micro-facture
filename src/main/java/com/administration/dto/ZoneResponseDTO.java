package com.administration.dto;

import lombok.Data;

import java.util.List;

@Data
public class ZoneResponseDTO {
    private String idZone;
    private String COD_ZONE ;
    private String DES_ZONE   ;
    private String DES_ZONE_AR;
    private List<DregionalResponseDTO> dregionals;
    private long totalElements;
}
