package io.citizenjournalist.as.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import io.citizenjournalist.as.IntegrationTest;
import io.citizenjournalist.as.domain.Translation;
import io.citizenjournalist.as.domain.enumeration.Language;
import io.citizenjournalist.as.repository.EntityManager;
import io.citizenjournalist.as.repository.TranslationRepository;
import io.citizenjournalist.as.service.dto.TranslationDTO;
import io.citizenjournalist.as.service.mapper.TranslationMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link TranslationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TranslationResourceIT {

    private static final String DEFAULT_INPUT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_TEXT = "BBBBBBBBBB";

    private static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static final Language UPDATED_LANGUAGE = Language.GERMAN;

    private static final Boolean DEFAULT_PERSIST = false;
    private static final Boolean UPDATED_PERSIST = true;

    private static final String DEFAULT_DETECTED_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_DETECTED_LANGUAGE = "BBBBBBBBBB";

    private static final String DEFAULT_OUTPUT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_OUTPUT_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/translations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private TranslationMapper translationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Translation translation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createEntity(EntityManager em) {
        Translation translation = new Translation()
            .inputText(DEFAULT_INPUT_TEXT)
            .language(DEFAULT_LANGUAGE)
            .persist(DEFAULT_PERSIST)
            .detectedLanguage(DEFAULT_DETECTED_LANGUAGE)
            .outputText(DEFAULT_OUTPUT_TEXT);
        return translation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createUpdatedEntity(EntityManager em) {
        Translation translation = new Translation()
            .inputText(UPDATED_INPUT_TEXT)
            .language(UPDATED_LANGUAGE)
            .persist(UPDATED_PERSIST)
            .detectedLanguage(UPDATED_DETECTED_LANGUAGE)
            .outputText(UPDATED_OUTPUT_TEXT);
        return translation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Translation.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        translation = createEntity(em);
    }

    @Test
    void createTranslation() throws Exception {
        int databaseSizeBeforeCreate = translationRepository.findAll().collectList().block().size();
        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeCreate + 1);
        Translation testTranslation = translationList.get(translationList.size() - 1);
        assertThat(testTranslation.getInputText()).isEqualTo(DEFAULT_INPUT_TEXT);
        assertThat(testTranslation.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testTranslation.getPersist()).isEqualTo(DEFAULT_PERSIST);
        assertThat(testTranslation.getDetectedLanguage()).isEqualTo(DEFAULT_DETECTED_LANGUAGE);
        assertThat(testTranslation.getOutputText()).isEqualTo(DEFAULT_OUTPUT_TEXT);
    }

    @Test
    void createTranslationWithExistingId() throws Exception {
        // Create the Translation with an existing ID
        translation.setId(1L);
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        int databaseSizeBeforeCreate = translationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = translationRepository.findAll().collectList().block().size();
        // set the field null
        translation.setLanguage(null);

        // Create the Translation, which fails.
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPersistIsRequired() throws Exception {
        int databaseSizeBeforeTest = translationRepository.findAll().collectList().block().size();
        // set the field null
        translation.setPersist(null);

        // Create the Translation, which fails.
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTranslations() {
        // Initialize the database
        translationRepository.save(translation).block();

        // Get all the translationList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(translation.getId().intValue()))
            .jsonPath("$.[*].inputText")
            .value(hasItem(DEFAULT_INPUT_TEXT.toString()))
            .jsonPath("$.[*].language")
            .value(hasItem(DEFAULT_LANGUAGE.toString()))
            .jsonPath("$.[*].persist")
            .value(hasItem(DEFAULT_PERSIST.booleanValue()))
            .jsonPath("$.[*].detectedLanguage")
            .value(hasItem(DEFAULT_DETECTED_LANGUAGE))
            .jsonPath("$.[*].outputText")
            .value(hasItem(DEFAULT_OUTPUT_TEXT.toString()));
    }

    @Test
    void getTranslation() {
        // Initialize the database
        translationRepository.save(translation).block();

        // Get the translation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, translation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(translation.getId().intValue()))
            .jsonPath("$.inputText")
            .value(is(DEFAULT_INPUT_TEXT.toString()))
            .jsonPath("$.language")
            .value(is(DEFAULT_LANGUAGE.toString()))
            .jsonPath("$.persist")
            .value(is(DEFAULT_PERSIST.booleanValue()))
            .jsonPath("$.detectedLanguage")
            .value(is(DEFAULT_DETECTED_LANGUAGE))
            .jsonPath("$.outputText")
            .value(is(DEFAULT_OUTPUT_TEXT.toString()));
    }

    @Test
    void getNonExistingTranslation() {
        // Get the translation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTranslation() throws Exception {
        // Initialize the database
        translationRepository.save(translation).block();

        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();

        // Update the translation
        Translation updatedTranslation = translationRepository.findById(translation.getId()).block();
        updatedTranslation
            .inputText(UPDATED_INPUT_TEXT)
            .language(UPDATED_LANGUAGE)
            .persist(UPDATED_PERSIST)
            .detectedLanguage(UPDATED_DETECTED_LANGUAGE)
            .outputText(UPDATED_OUTPUT_TEXT);
        TranslationDTO translationDTO = translationMapper.toDto(updatedTranslation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, translationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
        Translation testTranslation = translationList.get(translationList.size() - 1);
        assertThat(testTranslation.getInputText()).isEqualTo(UPDATED_INPUT_TEXT);
        assertThat(testTranslation.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testTranslation.getPersist()).isEqualTo(UPDATED_PERSIST);
        assertThat(testTranslation.getDetectedLanguage()).isEqualTo(UPDATED_DETECTED_LANGUAGE);
        assertThat(testTranslation.getOutputText()).isEqualTo(UPDATED_OUTPUT_TEXT);
    }

    @Test
    void putNonExistingTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, translationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTranslationWithPatch() throws Exception {
        // Initialize the database
        translationRepository.save(translation).block();

        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();

        // Update the translation using partial update
        Translation partialUpdatedTranslation = new Translation();
        partialUpdatedTranslation.setId(translation.getId());

        partialUpdatedTranslation.language(UPDATED_LANGUAGE).persist(UPDATED_PERSIST).detectedLanguage(UPDATED_DETECTED_LANGUAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTranslation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTranslation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
        Translation testTranslation = translationList.get(translationList.size() - 1);
        assertThat(testTranslation.getInputText()).isEqualTo(DEFAULT_INPUT_TEXT);
        assertThat(testTranslation.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testTranslation.getPersist()).isEqualTo(UPDATED_PERSIST);
        assertThat(testTranslation.getDetectedLanguage()).isEqualTo(UPDATED_DETECTED_LANGUAGE);
        assertThat(testTranslation.getOutputText()).isEqualTo(DEFAULT_OUTPUT_TEXT);
    }

    @Test
    void fullUpdateTranslationWithPatch() throws Exception {
        // Initialize the database
        translationRepository.save(translation).block();

        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();

        // Update the translation using partial update
        Translation partialUpdatedTranslation = new Translation();
        partialUpdatedTranslation.setId(translation.getId());

        partialUpdatedTranslation
            .inputText(UPDATED_INPUT_TEXT)
            .language(UPDATED_LANGUAGE)
            .persist(UPDATED_PERSIST)
            .detectedLanguage(UPDATED_DETECTED_LANGUAGE)
            .outputText(UPDATED_OUTPUT_TEXT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTranslation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTranslation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
        Translation testTranslation = translationList.get(translationList.size() - 1);
        assertThat(testTranslation.getInputText()).isEqualTo(UPDATED_INPUT_TEXT);
        assertThat(testTranslation.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testTranslation.getPersist()).isEqualTo(UPDATED_PERSIST);
        assertThat(testTranslation.getDetectedLanguage()).isEqualTo(UPDATED_DETECTED_LANGUAGE);
        assertThat(testTranslation.getOutputText()).isEqualTo(UPDATED_OUTPUT_TEXT);
    }

    @Test
    void patchNonExistingTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, translationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTranslation() throws Exception {
        int databaseSizeBeforeUpdate = translationRepository.findAll().collectList().block().size();
        translation.setId(count.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(translationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Translation in the database
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTranslation() {
        // Initialize the database
        translationRepository.save(translation).block();

        int databaseSizeBeforeDelete = translationRepository.findAll().collectList().block().size();

        // Delete the translation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, translation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Translation> translationList = translationRepository.findAll().collectList().block();
        assertThat(translationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
