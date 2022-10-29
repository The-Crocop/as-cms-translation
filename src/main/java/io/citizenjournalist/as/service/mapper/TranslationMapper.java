package io.citizenjournalist.as.service.mapper;

import io.citizenjournalist.as.domain.Translation;
import io.citizenjournalist.as.service.dto.TranslationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Translation} and its DTO {@link TranslationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TranslationMapper extends EntityMapper<TranslationDTO, Translation> {}
