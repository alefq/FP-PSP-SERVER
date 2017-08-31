package py.org.fundacionparaguaya.pspserver.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import py.org.fundacionparaguaya.pspserver.domain.UserApplicationEntity;
import py.org.fundacionparaguaya.pspserver.service.common.BaseMapper;
import py.org.fundacionparaguaya.pspserver.service.dto.UserApplicationDTO;

/**
 *  Created by jaimeferreira on 8/31/2017
 */
@Component
public class UserApplicationMapper implements BaseMapper<UserApplicationEntity, UserApplicationDTO> {

	private final ModelMapper modelMapper;
	 
	public UserApplicationMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	@Override
	public List<UserApplicationDTO> entityListToDtoList(List<UserApplicationEntity> entityList) {
		 return entityList.stream()
	                .filter(Objects::nonNull)
	                .map(this::entityToDto)
	                .collect(Collectors.toList());
	}

	@Override
	public UserApplicationDTO entityToDto(UserApplicationEntity entity) {
		UserApplicationDTO dto = modelMapper.map(entity, UserApplicationDTO.class);
	    return dto;
	}

	@Override
	public UserApplicationEntity dtoToEntity(UserApplicationDTO dto) {
		return modelMapper.map(dto, UserApplicationEntity.class);
	}

}
