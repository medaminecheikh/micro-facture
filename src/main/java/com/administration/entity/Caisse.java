package com.administration.entity;

import com.administration.dto.EncaissResponseDTO;
import com.administration.dto.EttUpdateDTO;
import com.administration.dto.UtilisateurUpdateDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Caisse implements Serializable {
    private String idCaisse;
    private int numCaise;
    private String f_Actif;
    private UtilisateurUpdateDTO login;
    private EttUpdateDTO cod_ett;
    private List<EncaissResponseDTO> encaissements;
}
