package com.postres.controller.general;

import com.postres.dto.RepartidorDTO;
import com.postres.service.service.RepartidorService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @GetMapping
    public ResponseEntity<List<RepartidorDTO>> listAll() throws ServiceException {
        return ResponseEntity.ok(repartidorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorDTO> listById(@PathVariable Long id) throws ServiceException {
        return ResponseEntity.ok(repartidorService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RepartidorDTO> create(@RequestBody RepartidorDTO repartidorDTO) throws ServiceException {
        RepartidorDTO created = repartidorService.create(repartidorDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorDTO> update(@PathVariable Long id, @RequestBody RepartidorDTO repartidorDTO) throws ServiceException {
        RepartidorDTO updated = repartidorService.update(id, repartidorDTO);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ServiceException {
        repartidorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
