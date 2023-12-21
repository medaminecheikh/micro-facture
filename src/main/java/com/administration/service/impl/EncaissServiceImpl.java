package com.administration.service.impl;

import com.administration.dto.*;
import com.administration.entity.*;
import com.administration.openfeign.CaisseRestController;
import com.administration.repo.*;
import com.administration.service.IEncaissService;
import com.administration.service.mappers.CaisseMappers;
import com.administration.service.mappers.EncaissMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class EncaissServiceImpl implements IEncaissService {
    EncaissMapper encaissMapper;
    CaisseMappers caisseMappers;
    EncaissRepo encaissRepo;
    CaisseRestController caisseRestController;
    FactureRepo factureRepo;


    @Override
    public Encaissement addEncaiss(EncaissRequestDTO encaissement) {

        encaissement.setDateEnc(new Date());
        Encaissement encaissementSAVE = encaissMapper.EncaissRequestDTOEncaiss(encaissement);
        encaissRepo.save(encaissementSAVE);
        return encaissementSAVE;
    }

    @Override
    public EncaissResponseDTO getEncaissById(String id) {
        Encaissement encaissement = encaissRepo.findById(id).orElse(null);
        if (encaissement != null) {
            encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
            return encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
        } else {
            return null;
        }
    }

    @Override
    public List<EncaissResponseDTO> getEncaissementByFacture(String idFact) {
        InfoFacture facture = factureRepo.findById(idFact).orElse(null);

        if (facture != null) {
            List<Encaissement> encaissements = facture.getEncaissements();
            if (encaissements != null) {
                List<EncaissResponseDTO> encaissResponseDTOs = new ArrayList<>();
                encaissements.forEach(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    EncaissResponseDTO responseDTO = encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
                    encaissResponseDTOs.add(responseDTO);
                });
                return encaissResponseDTOs;
            }
        }

        return Collections.emptyList();
    }


    @Override
    public List<EncaissResponseDTO> getEncaissementByCaisse(String idCaisse) {
        try {
            CaisseResponseDTO caisse = caisseRestController.getCaisseById(idCaisse).getBody();
            if (caisse != null) {
                List<Encaissement> encaissement = encaissRepo.findByCaisse(idCaisse);
                return encaissement.stream().map(encaissement1 -> encaissMapper.EncaissTOEncaissResponseDTO(encaissement1))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEncaisse(EncaissUpdateDTO dto) {
        Encaissement encaissement = encaissRepo.findById(dto.getIdEncaissement()).orElse(null);
        if (encaissement != null) {
            encaissMapper.updateEncaissFromDto(dto, encaissement);
            encaissRepo.save(encaissement);
        }
    }

    @Override
    public void deleteEncaisse(String idEncaiss) {
        encaissRepo.deleteById(idEncaiss);
    }


    @Override
    public EncaissResponseDTO affectEncaisseToCaisse(String idEncaiss, String idCai) {
        try {
            Encaissement encaissement = encaissRepo.findById(idEncaiss)
                    .orElseThrow(() -> new EntityNotFoundException("Encaissement not found with id: " + idEncaiss));
            CaisseResponseDTO caisse = caisseRestController.getCaisseById(idCai).getBody();

            if (caisse != null) {
                encaissement.setCaisseId(caisse.getIdCaisse());
            }
            encaissRepo.save(encaissement);

            return encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
        } catch (Exception e) {
            // Log the error for debugging purposes
            log.error("Failed to affect caisse to encaissement: {}", e.getMessage());
            // You might want to throw a custom exception here or handle it in another way.
            throw new RuntimeException("Failed to affect caisse to encaissement: " + e.getMessage());
        }
    }


    @Override
    public List<EncaissResponseDTO> getAllEncaissement() {
        List<Encaissement> encaissementList = encaissRepo.findAll();
        return encaissementList.stream()
                .map(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    return encaissMapper.EncaissTOEncaissResponseDTO(encaissement);}).collect(Collectors.toList());
    }


    @Override
    public List<EncaissResponseDTO> getEncaissementsForCaisseInCurrentMonth(String caisseId) {
        // Calculate the start and end date of the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = calendar.getTime();
        List<Encaissement> encaissementList = encaissRepo.findEncaissementsForCaisseInCurrentMonth(caisseId, startDate, endDate);
        // Fetch the list of Encaissements for the specified Caisse within the current month
        return encaissementList.stream().map(encaissement -> encaissMapper.EncaissTOEncaissResponseDTO(encaissement)).collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> searchEncaiss(String produit, String identifiant, String modePaiement, String typeIdent, Double montantEnc, String refFacture, PageRequest pageable) {
        Page<Encaissement> encaissements = encaissRepo.searchEncaiss(produit, identifiant, modePaiement, typeIdent, montantEnc, refFacture, pageable);
        long count = encaissements.getTotalElements();
        return encaissements.stream()
                .map(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    EncaissResponseDTO dto = encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
                    dto.setTotalElements(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> searchEncaissWeek(String produit, String identifiant, String etatEncaissement, String typeIdent, Double montantEnc, String refFacture, PageRequest pageable) {
        Page<Encaissement> encaissements = encaissRepo.searchEncaissThisWeek(produit, identifiant, etatEncaissement, typeIdent, montantEnc, refFacture, pageable);
        long count = encaissements.getTotalElements();
        return encaissements.stream()
                .map(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    EncaissResponseDTO dto = encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
                    dto.setTotalElements(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> searchEncaissMonth(String produit, String identifiant, String etatEncaissement, String typeIdent, Double montantEnc, String refFacture, PageRequest pageable) {
        Page<Encaissement> encaissements = encaissRepo.searchEncaissThisMonth(produit, identifiant, etatEncaissement, typeIdent, montantEnc, refFacture, pageable);
        long count = encaissements.getTotalElements();
        return encaissements.stream()
                .map(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    EncaissResponseDTO dto = encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
                    dto.setTotalElements(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> searchEncaissYear(String produit, String identifiant, String etatEncaissement, String typeIdent, Double montantEnc, String refFacture, PageRequest pageable) {
        Page<Encaissement> encaissements = encaissRepo.searchEncaissThisYear(produit, identifiant, etatEncaissement, typeIdent, montantEnc, refFacture, pageable);
        long count = encaissements.getTotalElements();
        return encaissements.stream()
                .map(encaissement -> {
                   encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    EncaissResponseDTO dto = encaissMapper.EncaissTOEncaissResponseDTO(encaissement);
                    dto.setTotalElements(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> encaissYear() {
        List<Encaissement> encaissements = encaissRepo.getEncaissementYearly();
        return encaissements.stream()
                .map(encaissement -> {
                    encaissement.setCaisse(getCaisseForEncaissement(encaissement.getCaisseId()));
                    return encaissMapper.EncaissTOEncaissResponseDTO(encaissement);})
                .collect(Collectors.toList());
    }

    @Override
    public List<EncaissResponseDTO> getEncaissForCaisseById(String id) {
        try {
            List<Encaissement> encaissements = encaissRepo.findByCaisse(id);
            log.info("encaissements: {}", encaissements);
            return encaissements.stream()
                    .map(encaissement -> encaissMapper.EncaissTOEncaissResponseDTO(encaissement))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving encaissements for caisse id {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public Caisse getCaisseForEncaissement(String caisseId) {
        try {
            ResponseEntity<CaisseResponseDTO> responseEntity = caisseRestController.getCaisseById(caisseId);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CaisseResponseDTO caisseResponseDTO = responseEntity.getBody();

                if (caisseResponseDTO != null) {
                    return caisseMappers.CaisseResponseDTOTOCaisse(caisseResponseDTO);
                } else {
                    // Handle the case where the response body is null
                    log.warn("CaisseResponseDTO is null for id: {}", caisseId);
                    return null;
                }
            } else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Caisse not found, return null
                return null;
            } else {
                // Handle non-successful status code
                throw new RuntimeException("Failed to get caisse for encaissement. HTTP Status: " + responseEntity.getStatusCodeValue());
            }
        } catch (FeignException e) {
            // Handle Feign client exceptions (e.g., communication errors)
            // Log the error for debugging purposes
            log.error("Feign client error while getting caisse for encaissement: {}", e.getMessage());
            throw new RuntimeException("Failed to get caisse for encaissement. Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            // Log the error for debugging purposes
            log.error("Error getting caisse for encaissement: {}", e.getMessage());
            throw new RuntimeException("Failed to get caisse for encaissement. Error: " + e.getMessage());
        }
    }

}
