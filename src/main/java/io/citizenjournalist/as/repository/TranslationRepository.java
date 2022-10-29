package io.citizenjournalist.as.repository;

import io.citizenjournalist.as.domain.Translation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Translation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TranslationRepository extends ReactiveCrudRepository<Translation, Long>, TranslationRepositoryInternal {
    Flux<Translation> findAllBy(Pageable pageable);

    @Override
    <S extends Translation> Mono<S> save(S entity);

    @Override
    Flux<Translation> findAll();

    @Override
    Mono<Translation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TranslationRepositoryInternal {
    <S extends Translation> Mono<S> save(S entity);

    Flux<Translation> findAllBy(Pageable pageable);

    Flux<Translation> findAll();

    Mono<Translation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Translation> findAllBy(Pageable pageable, Criteria criteria);

}
