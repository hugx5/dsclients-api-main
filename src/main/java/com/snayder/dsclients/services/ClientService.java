package com.snayder.dsclients.services;

import com.snayder.dsclients.dtos.ClientDTO;
import com.snayder.dsclients.entities.Client;
import com.snayder.dsclients.entities.mapper.ClientMapper;
import com.snayder.dsclients.repositories.ClientRepository;
import com.snayder.dsclients.services.exceptions.DatabaseViolationException;
import com.snayder.dsclients.services.exceptions.ResourceNotFoundException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ClientMapper clientMapper;
    
    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(PageRequest pageRequest) {
        Page<Client> clients = this.clientRepository.findAll(pageRequest);
        return clients.map(clientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long idClient) {
        Client client = this.clientRepository.findById(idClient)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));

        return clientMapper.toDTO(client);
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        try {
            Client client = this.clientRepository.save(new Client(dto));
            return new ClientDTO(client);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DatabaseViolationException("O CPF informado já está vinculado à outro cliente");
        }
    }

    @Transactional
    public ClientDTO update(Long idClient, ClientDTO dto) {
        try {
            Client client = this.clientRepository.getById(idClient);

            convertToClient(client, dto);
            client = this.clientRepository.saveAndFlush(client);

            return new ClientDTO(client);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DatabaseViolationException("O CPF informado já está vinculado à outro cliente");
        }
        catch (EntityNotFoundException ex) {
            throw new ResourceNotFoundException("Cliente não encontrado para atualização!");
        }
    }

    public void delete(Long idClient) {
        try {
            this.clientRepository.deleteById(idClient);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Cliente não encotrado para exclusão!");
        }
    }

    public void generateClientsReport(ServletContext context, HttpServletResponse resp, boolean toExcel) throws Exception {
        Map<String, Object> params = new HashMap<>();

        reportService.generateReport(
                context,
                resp,
                "rel_clients",
                params,
                new JRBeanCollectionDataSource(clientRepository.findAll()),
                toExcel
        );
    }

    
	private void convertToClient(Client client, ClientDTO dto) {
		client.setName(dto.getName());
		client.setIncome(dto.getIncome());
		client.setCpf(dto.getCpf());
		client.setBirthDate(dto.getBirthDate());
	}

}
