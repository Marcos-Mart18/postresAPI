package com.postres.controller.general;

import com.postres.dto.RolDTO;
import com.postres.service.service.RolService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {

        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<RolDTO>> listAll() throws ServiceException {

        return ResponseEntity.ok(rolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> listById(@PathVariable Long id) throws ServiceException {
        RolDTO rolDTO = rolService.findById(id);
        return ResponseEntity.ok(rolDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RolDTO> create(@RequestBody RolDTO rolDTO) throws ServiceException {
        RolDTO rolDTO1 = rolService.create(rolDTO);
        return new ResponseEntity<>(rolDTO1, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> update(@PathVariable Long id, @RequestBody RolDTO rolDTO) throws ServiceException {
        RolDTO rolDTO1 = rolService.update(id,rolDTO);
        return ResponseEntity.ok(rolDTO1);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ServiceException {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
