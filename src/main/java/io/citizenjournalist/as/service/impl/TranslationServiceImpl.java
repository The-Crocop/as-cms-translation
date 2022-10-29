package io.citizenjournalist.as.service.impl;

import com.google.cloud.translate.v3beta1.LocationName;
import com.google.cloud.translate.v3beta1.TranslateTextRequest;
import com.google.cloud.translate.v3beta1.TranslationServiceClient;
import io.citizenjournalist.as.domain.Translation;
import io.citizenjournalist.as.repository.TranslationRepository;
import io.citizenjournalist.as.service.TranslationService;
import io.citizenjournalist.as.service.dto.TranslationDTO;
import io.citizenjournalist.as.service.mapper.TranslationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service Implementation for managing {@link Translation}.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService {

    private final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private final TranslationRepository translationRepository;

    private final TranslationMapper translationMapper;

    private final TranslationServiceClient translationServiceClient;

    private final LocationName parent;

    public TranslationServiceImpl(
        @Value("${google.project-id}") String projectId,
        TranslationRepository translationRepository,
        TranslationMapper translationMapper,
        TranslationServiceClient translationServiceClient
    ) {
        this.translationRepository = translationRepository;
        this.translationMapper = translationMapper;
        this.translationServiceClient = translationServiceClient;
        this.parent = LocationName.of(projectId, "global");
    }

    @Override
    public Mono<TranslationDTO> save(TranslationDTO translationDTO) {
        log.debug("Request to save Translation : {}", translationDTO);

        var request = TranslateTextRequest
            .newBuilder()
            .setParent(parent.toString())
            .setMimeType("text/plain")
            .setTargetLanguageCode(translationDTO.getLanguage().getShortName())
            .addContents(translationDTO.getInputText())
            .build();
        return Mono
            .fromCallable(() -> translationServiceClient.translateText(request))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(translateTextResponse -> {
                var translation = translateTextResponse.getTranslations(0);
                translationDTO.setDetectedLanguage(translation.getDetectedLanguageCode());
                translationDTO.setOutputText(translation.getTranslatedText());
                if (!translationDTO.getPersist()) {
                    return Mono.just(translationDTO);
                } else {
                    return translationRepository.save(translationMapper.toEntity(translationDTO)).map(translationMapper::toDto);
                }
            });
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
