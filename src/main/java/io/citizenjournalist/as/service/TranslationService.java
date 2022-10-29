package io.citizenjournalist.as.service;

import io.citizenjournalist.as.service.dto.TranslationDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link io.citizenjournalist.as.domain.Translation}.
 */
public interface TranslationService {
    /**
     * Save a translation.
     *
     * @param translationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TranslationDTO> save(TranslationDTO translationDTO);

    /**
     * Updates a translation.
     *
     * @param translationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TranslationDTO> update(TranslationDTO translationDTO);

    /**
     * Partially updates a translation.
     *
     * @param translationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TranslationDTO> partialUpdate(TranslationDTO translationDTO);

    /**
     * Get all the translations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TranslationDTO> findAll(Pageable pageable);

    /**
     * Returns the number of translations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" translation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TranslationDTO> findOne(Long id);

    /**
     * Delete the "id" translation.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
