package com.administration.openfeign;

import com.administration.dto.CaisseResponseDTO;
import com.administration.entity.Caisse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "MICROADMIN")
public interface CaisseRestController {



    @GetMapping("/caisse/getCaisseForEnc/{id}")
    ResponseEntity<CaisseResponseDTO> getCaisseById(@PathVariable String id);

    @GetMapping("/caisse/getall")
    ResponseEntity<List<Caisse>> listCaisses();

    @GetMapping("/caisse/etts/{id}")
    ResponseEntity<List<Caisse>> getCaissesByEttId(@PathVariable String id);
}
