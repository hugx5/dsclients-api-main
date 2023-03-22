package com.snayder.dsclients.entities.mapper;

import org.mapstruct.Mapper;

import com.snayder.dsclients.dtos.ClientDTO;
import com.snayder.dsclients.entities.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {
	
	ClientDTO toDTO(Client client);
	
}
