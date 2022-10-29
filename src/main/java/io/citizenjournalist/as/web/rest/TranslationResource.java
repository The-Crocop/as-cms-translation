package io.citizenjournalist.as.web.rest;

import io.citizenjournalist.as.repository.TranslationRepository;
import io.citizenjournalist.as.service.TranslationService;
import io.citizenjournalist.as.service.dto.TranslationDTO;
import io.citizenjournalist.as.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link io.citizenjournalist.as.domain.Translation}.
 */
@RestController
@RequestMapping("/api")
public class TranslationResource {

    private final Logger log = LoggerFactory.getLogger(TranslationResource.class);

    private static final String ENTITY_NAME = "asCmsTranslationTranslation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TranslationService translationService;

    private final TranslationRepository translationRepository;

    public TranslationResource(TranslationService translationService, TranslationRepository translationRepository) {
        this.translationService = translationService;
        this.translationRepository = translationRepository;
    }

    /**
     * {@code POST  /translations} : Create a new translation.
     *
     * @param translationDTO the translationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new translationDTO, or with status {@code 400 (Bad Request)} if the translation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/translations")
    public Mono<ResponseEntity<TranslationDTO>> createTranslation(@Valid @RequestBody TranslationDTO translationDTO)
        throws URISyntaxException {
        log.debug("REST request to save Translation : {}", translationDTO);
        if (translationDTO.getId() != null) {
            throw new BadRequestAlertException("A new translation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return translationService
            .save(translationDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/translations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /translations/:id} : Updates an existing translation.
     *
     * @param id the id of the translationDTO to save.
     * @param translationDTO the translationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated translationDTO,
     * or with status {@code 400 (Bad Request)} if the translationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the translationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/translations/{id}")
    public Mono<ResponseEntity<TranslationDTO>> updateTranslation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TranslationDTO translationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Translation : {}, {}", id, translationDTO);
        if (translationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, translationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return translationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return translationService
                    .update(translationDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /translations/:id} : Partial updates given fields of an existing translation, field will ignore if it is null
     *
     * @param id the id of the translationDTO to save.
     * @param translationDTO the translationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated translationDTO,
     * or with status {@code 400 (Bad Request)} if the translationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the translationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the translationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/translations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TranslationDTO>> partialUpdateTranslation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TranslationDTO translationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Translation partially : {}, {}", id, translationDTO);
        if (translationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, translationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return translationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TranslationDTO> result = translationService.partialUpdate(translationDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /translations} : get all the translations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of translations in body.
     */
    @GetMapping("/translations")
    public Mono<ResponseEntity<List<TranslationDTO>>> getAllTranslations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Translations");
        return translationService
            .countAll()
            .zipWith(translationService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /translations/:id} : get the "id" translation.
     *
     * @param id the id of the translationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the translationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/translations/{id}")
    public Mono<ResponseEntity<TranslationDTO>> getTranslation(@PathVariable Long id) {
        log.debug("REST request to get Translation : {}", id);
        Mono<TranslationDTO> translationDTO = translationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(translationDTO);
    }

    /**
     * {@code DELETE  /translations/:id} : delete the "id" translation.
     *
     * @param id the id of the translationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/translations/{id}")
    public Mono<ResponseEntity<Void>> deleteTranslation(@PathVariable Long id) {
        log.debug("REST request to delete Translation : {}", id);
        return translationService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
