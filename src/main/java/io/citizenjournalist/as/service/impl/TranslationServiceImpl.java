package io.citizenjournalist.as.service.impl;

import io.citizenjournalist.as.domain.Translation;
import io.citizenjournalist.as.repository.TranslationRepository;
import io.citizenjournalist.as.service.TranslationService;
import io.citizenjournalist.as.service.dto.TranslationDTO;
import io.citizenjournalist.as.service.mapper.TranslationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Translation}.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService {

    private final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private final TranslationRepository translationRepository;

    private final TranslationMapper translationMapper;

    public TranslationServiceImpl(TranslationRepository translationRepository, TranslationMapper translationMapper) {
        this.translationRepository = translationRepository;
        this.translationMapper = translationMapper;
    }

    @Override
    public Mono<TranslationDTO> save(TranslationDTO translationDTO) {
        log.debug("Request to save Translation : {}", translationDTO);
        return translationRepository.save(translationMapper.toEntity(translationDTO)).map(translationMapper::toDto);
    }

    @Override
    public Mono<TranslationDTO> update(TranslationDTO translationDTO) {
        log.debug("Request to update Translation : {}", translationDTO);
        return translationRepository.save(translationMapper.toEntity(translationDTO)).map(translationMapper::toDto);
    }

    @Override
    public Mono<TranslationDTO> partialUpdate(TranslationDTO translationDTO) {
        log.debug("Request to partially update Translation : {}", translationDTO);

        return translationRepository
            .findById(translationDTO.getId())
            .map(existingTranslation -> {
                translationMapper.partialUpdate(existingTranslation, translationDTO);

                return existingTranslation;
            })
            .flatMap(translationRepository::save)
            .map(translationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TranslationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Translations");
        return translationRepository.findAllBy(pageable).map(translationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return translationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TranslationDTO> findOne(Long id) {
        log.debug("Request to get Translation : {}", id);
        return translationRepository.findById(id).map(translationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Translation : {}", id);
        return translationRepository.deleteById(id);
    }
}
