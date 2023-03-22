package com.snayder.dsclients.resources;

import com.snayder.dsclients.dtos.ClientDTO;
import com.snayder.dsclients.services.ClientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.valueOf;

@RestController
@RequestMapping("/clients")
@CrossOrigin("*")
public class ClientResource {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @ApiOperation("Busca paginada de clientes")
    public ResponseEntity<Page<ClientDTO>> findAll(
	        @RequestParam(defaultValue = "0")  @ApiParam(value = "Número da Página") int page,
	        @RequestParam(defaultValue = "10") @ApiParam(value = "Clientes por Página") int size,
	        @RequestParam(defaultValue = "DESC") @ApiParam(value = "Tipo da Ordenação") String direction,
	        @RequestParam(defaultValue = "income") @ApiParam(value = "Informe por qual dado os clientes serão ordenados") String sort) {
       PageRequest pageRequest = PageRequest.of(page, size, valueOf(direction), sort);
       Page<ClientDTO> dtos = this.clientService.findAll(pageRequest);
        
       return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{idClient}")
    @ApiOperation("Busca de um cliente pelo id")
    public ResponseEntity<ClientDTO> findById(
    		@PathVariable @ApiParam(value = "Id do Cliente", example = "1") Long idClient) {
        ClientDTO dto = this.clientService.findById(idClient);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @ApiOperation("Inserção de um cliente")
    public ResponseEntity<ClientDTO> insert(@RequestBody ClientDTO dto) {
        dto = this.clientService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{idClient}")
    @ApiOperation("Atualização de um cliente pelo id")
    public ResponseEntity<ClientDTO> update(
	    	@RequestBody ClientDTO dto,
	    	@PathVariable @ApiParam(value = "Id do Cliente", example = "1") Long idClient) {
    	dto = this.clientService.update(idClient, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{idClient}")
    @ApiOperation("Deleção de um cliente pelo id")
    public ResponseEntity<Void> delete(
    		@PathVariable @ApiParam(value = "Id do Cliente", example = "1") Long idClient) {
        this.clientService.delete(idClient);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("report")
    public ResponseEntity<Void> generateClientsReport(HttpServletRequest req, HttpServletResponse resp,
                                              @RequestParam(defaultValue = "false")  boolean toExcel) throws Exception {
        clientService.generateClientsReport(req.getServletContext(), resp, toExcel);
        return ResponseEntity.noContent().build();
    }

}
