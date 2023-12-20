package com.administration.openfeign;

import com.administration.dto.CaisseRequestDTO;
import com.administration.dto.CaisseResponseDTO;
import com.administration.dto.CaisseUpdateDTO;
import com.administration.entity.Caisse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microadmin")
public interface CaisseRestController {

    @PostMapping("/caisse/add")
    ResponseEntity<CaisseResponseDTO> addCaisse(@RequestBody CaisseRequestDTO caisseRequestDTO);

    @GetMapping("/caisse/add/{id}")
    ResponseEntity<Caisse> getCaisse(@PathVariable String id);

    @GetMapping("/caisse/getall")
    ResponseEntity<List<Caisse>> listCaisses();

    @PutMapping("/caisse/update")
    ResponseEntity<Void> updateCaisse(@RequestBody CaisseUpdateDTO dto);

    @PutMapping("/caisse/{idCaisse}/utilisateurs/{idUser}")
    ResponseEntity<Void> affecterCaisseToUser(@PathVariable String idCaisse, @PathVariable String idUser);

    @DeleteMapping("/caisse/utilisateurs/{idUser}")
    ResponseEntity<Void> removeUser(@PathVariable String idUser);

    @PutMapping("/caisse/affect/{idCaisse}/etts/{idEtt}")
    ResponseEntity<Void> affecterCaisseToEtt(@PathVariable String idCaisse, @PathVariable String idEtt);

    @DeleteMapping("/caisse/delete/{id}")
    ResponseEntity<Void> deleteCaisse(@PathVariable String id);

    @GetMapping("/caisse/etts/{id}")
    ResponseEntity<List<Caisse>> getCaissesByEttId(@PathVariable String id);
}
