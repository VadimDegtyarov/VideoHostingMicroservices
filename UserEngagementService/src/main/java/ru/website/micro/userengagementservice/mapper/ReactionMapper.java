package ru.website.micro.userengagementservice.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import ru.website.micro.userengagementservice.dto.ReactionDto;
import ru.website.micro.userengagementservice.model.Reaction;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    ReactionDto toDto(Reaction reaction);

    @Mapping(target = "createdAt", ignore = true)
    Reaction toEntity(ReactionDto dto);
}