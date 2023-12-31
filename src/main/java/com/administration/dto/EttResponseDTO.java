package com.administration.dto;

import lombok.Data;

import java.util.List;

@Data
public class EttResponseDTO {
    private String idEtt;
    private String codEtt;
    private String COD_CFRX;
    private String des_SRC_ENC;
    private String prfx_SRC_ENC;
    private String adr;
    private int is_BSCS;
    private List<UtilisateurUpdateDTO> utilisateurs;
    private List<CaisseUpdateDTO> caisses;
    private DregionalResponseDTO dregional;
    private long totalElements;
}
