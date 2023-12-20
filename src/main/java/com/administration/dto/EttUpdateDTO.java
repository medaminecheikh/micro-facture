package com.administration.dto;

import lombok.Data;

@Data
public class EttUpdateDTO {
    private String idEtt;
    private String codEtt;
    private String COD_CFRX;
    private String des_SRC_ENC;
    private String prfx_SRC_ENC;
    private String adr;
    private int is_BSCS;
}
