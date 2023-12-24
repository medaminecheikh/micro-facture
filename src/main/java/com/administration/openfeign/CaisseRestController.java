package com.administration.openfeign;

import com.administration.dto.CaisseResponseDTO;
import com.administration.entity.Caisse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "MICROADMIN")
public interface CaisseRestController {


    @GetMapping("/caisse/getCaisseForEnc/{id}")
    @CircuitBreaker(name = "getCaisseForEnc", fallbackMethod = "fallbackgetCaisseForEnc")
    ResponseEntity<CaisseResponseDTO> getCaisseById(@PathVariable String id);

    // Fallback method
    default ResponseEntity<CaisseResponseDTO> fallbackgetCaisseForEnc(String id, Throwable throwable) {
        CaisseResponseDTO caisseResponseDTO = new CaisseResponseDTO();
        caisseResponseDTO.setF_Actif("Service is down");
        caisseResponseDTO.setIdCaisse("Service is down");
        // Handle the fallback logic here
        // You can log the error or provide a default response
        // For simplicity, returning null in this example
        return ResponseEntity.ok(caisseResponseDTO);
    }

    @GetMapping("/caisse/getall")
    ResponseEntity<List<Caisse>> listCaisses();

    @GetMapping("/caisse/etts/{id}")
    ResponseEntity<List<Caisse>> getCaissesByEttId(@PathVariable String id);
}
