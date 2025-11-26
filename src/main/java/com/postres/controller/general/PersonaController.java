package com.postres.controller.general;

import com.postres.dto.PersonaDTO;
import com.postres.service.service.PersonaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {
    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> listAll() throws ServiceException {
        return ResponseEntity.ok(personaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> listById(@PathVariable Long id) throws ServiceException {
        return ResponseEntity.ok(personaService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PersonaDTO> create(@RequestBody PersonaDTO personaDTO) throws ServiceException {
        PersonaDTO created = personaService.create(personaDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> update(@PathVariable Long id, @RequestBody PersonaDTO personaDTO) throws ServiceException {
        PersonaDTO updated = personaService.update(id, personaDTO);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ServiceException {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
