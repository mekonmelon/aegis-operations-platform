package com.aegis.operations.store.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.Resource;
import com.aegis.operations.store.DeclarationSearchCriteria;
import com.aegis.operations.store.DemoOperationsData;
import com.aegis.operations.store.IncidentSearchCriteria;
import com.aegis.operations.store.OperationsStore;
import com.aegis.operations.store.elasticsearch.document.DashboardStateDocument;
import com.aegis.operations.store.elasticsearch.document.DisasterDeclarationDocument;
import com.aegis.operations.store.elasticsearch.document.IncidentDocument;
import com.aegis.operations.store.elasticsearch.repository.DashboardStateRepository;
import com.aegis.operations.store.elasticsearch.repository.DisasterDeclarationSearchRepository;
import com.aegis.operations.store.elasticsearch.repository.FacilitySearchRepository;
import com.aegis.operations.store.elasticsearch.repository.IncidentDeclarationLinkRepository;
import com.aegis.operations.store.elasticsearch.repository.IncidentSearchRepository;
import com.aegis.operations.store.elasticsearch.repository.RecommendationSearchRepository;
import com.aegis.operations.store.elasticsearch.repository.ResourceSearchRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(name = "aegis.storage", havingValue = "elasticsearch", matchIfMissing = true)
public class ElasticsearchOperationsStore implements OperationsStore, ApplicationRunner {
    private final IncidentSearchRepository incidentRepository;
    private final ResourceSearchRepository resourceRepository;
    private final FacilitySearchRepository facilityRepository;
    private final RecommendationSearchRepository recommendationRepository;
    private final DisasterDeclarationSearchRepository declarationRepository;
    private final IncidentDeclarationLinkRepository linkRepository;
    private final DashboardStateRepository stateRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchOperationsStore(
            IncidentSearchRepository incidentRepository,
            ResourceSearchRepository resourceRepository,
            FacilitySearchRepository facilityRepository,
            RecommendationSearchRepository recommendationRepository,
            DisasterDeclarationSearchRepository declarationRepository,
            IncidentDeclarationLinkRepository linkRepository,
            DashboardStateRepository stateRepository,
            ElasticsearchOperations elasticsearchOperations) {
        this.incidentRepository = incidentRepository;
        this.resourceRepository = resourceRepository;
        this.facilityRepository = facilityRepository;
        this.recommendationRepository = recommendationRepository;
        this.declarationRepository = declarationRepository;
        this.linkRepository = linkRepository;
        this.stateRepository = stateRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfEmpty();
        backfillMissingIncidentSources();
    }

    @Override
    public DashboardData dashboardSnapshot() {
        return new DashboardData(lastUpdated(), searchIncidents(new IncidentSearchCriteria(null, null, null, null, null)),
                resourceSnapshots(), facilitySnapshots(), recommendationSnapshots());
    }

    @Override
    public List<Incident> searchIncidents(IncidentSearchCriteria criteria) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(buildIncidentQuery(criteria))
                .build();

        return elasticsearchOperations.search(query, IncidentDocument.class).stream()
                .map(SearchHit::getContent)
                .map(ElasticsearchDocumentMapper::toDomain)
                .sorted(Comparator.comparing(Incident::getReportedAt).reversed())
                .toList();
    }

    @Override
    public List<DisasterDeclaration> searchDeclarations(DeclarationSearchCriteria criteria) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(buildDeclarationQuery(criteria))
                .build();

        return elasticsearchOperations.search(query, DisasterDeclarationDocument.class).stream()
                .map(SearchHit::getContent)
                .map(ElasticsearchDocumentMapper::toDomain)
                .sorted(Comparator.comparing(DisasterDeclaration::getDeclarationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    @Override
    public Optional<Incident> incidentSnapshot(String incidentId) {
        return incidentRepository.findById(incidentId).map(ElasticsearchDocumentMapper::toDomain);
    }

    @Override
    public Optional<DisasterDeclaration> declarationSnapshot(String declarationId) {
        return declarationRepository.findById(declarationId).map(ElasticsearchDocumentMapper::toDomain);
    }

    @Override
    public Optional<Resource> resourceSnapshot(String resourceId) {
        return resourceRepository.findById(resourceId).map(ElasticsearchDocumentMapper::toDomain);
    }

    @Override
    public Optional<Recommendation> recommendationSnapshot(String recommendationId) {
        return recommendationRepository.findById(recommendationId).map(ElasticsearchDocumentMapper::toDomain);
    }

    @Override
    public List<Resource> resourceSnapshots() {
        return toList(resourceRepository.findAll()).stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Facility> facilitySnapshots() {
        return toList(facilityRepository.findAll()).stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Recommendation> recommendationSnapshots() {
        return toList(recommendationRepository.findAll()).stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Incident> incidentSnapshotsBySource(IncidentSource source) {
        return searchIncidents(new IncidentSearchCriteria(null, null, null, null, source));
    }

    @Override
    public List<IncidentDeclarationLink> linksForIncident(String incidentId) {
        return linkRepository.findByIncidentId(incidentId).stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .sorted(Comparator.comparing(IncidentDeclarationLink::getConfidence).reversed())
                .toList();
    }

    @Override
    public List<IncidentDeclarationLink> linksForDeclaration(String declarationId) {
        return linkRepository.findByDeclarationId(declarationId).stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .sorted(Comparator.comparing(IncidentDeclarationLink::getConfidence).reversed())
                .toList();
    }

    @Override
    public void saveIncident(Incident incident) {
        incidentRepository.save(ElasticsearchDocumentMapper.toDocument(incident));
    }

    @Override
    public void saveDeclaration(DisasterDeclaration declaration) {
        declarationRepository.save(ElasticsearchDocumentMapper.toDocument(declaration));
    }

    @Override
    public void saveIncidentDeclarationLink(IncidentDeclarationLink link) {
        linkRepository.save(ElasticsearchDocumentMapper.toDocument(link));
    }

    @Override
    public void saveResource(Resource resource) {
        resourceRepository.save(ElasticsearchDocumentMapper.toDocument(resource));
    }

    @Override
    public void saveRecommendation(Recommendation recommendation) {
        recommendationRepository.save(ElasticsearchDocumentMapper.toDocument(recommendation));
    }

    @Override
    public void deleteIncident(String incidentId) {
        incidentRepository.deleteById(incidentId);
        deleteLinksForIncident(incidentId);
    }

    @Override
    public void deleteLinksForIncident(String incidentId) {
        linkRepository.deleteByIncidentId(incidentId);
    }

    @Override
    public void deleteLinksForDeclaration(String declarationId) {
        linkRepository.deleteByDeclarationId(declarationId);
    }

    @Override
    public void updateLastUpdated() {
        saveLastUpdated(Instant.now());
    }

    @Override
    public void resetWithDemoData() {
        incidentRepository.deleteAll();
        resourceRepository.deleteAll();
        facilityRepository.deleteAll();
        recommendationRepository.deleteAll();
        declarationRepository.deleteAll();
        linkRepository.deleteAll();
        stateRepository.deleteAll();
        seedDemoData();
    }

    public void seedIfEmpty() {
        if (incidentRepository.count() == 0
                && resourceRepository.count() == 0
                && facilityRepository.count() == 0
                && recommendationRepository.count() == 0) {
            seedDemoData();
        }
    }

    private void backfillMissingIncidentSources() {
        List<IncidentDocument> documentsMissingSource = toList(incidentRepository.findAll()).stream()
                .filter(document -> !StringUtils.hasText(document.getSource()))
                .toList();

        if (documentsMissingSource.isEmpty()) {
            return;
        }

        incidentRepository.saveAll(documentsMissingSource.stream()
                .map(ElasticsearchDocumentMapper::toDomain)
                .map(ElasticsearchDocumentMapper::toDocument)
                .toList());
    }

    private void seedDemoData() {
        incidentRepository.saveAll(DemoOperationsData.incidents().stream()
                .map(ElasticsearchDocumentMapper::toDocument)
                .toList());
        resourceRepository.saveAll(DemoOperationsData.resources().stream()
                .map(ElasticsearchDocumentMapper::toDocument)
                .toList());
        facilityRepository.saveAll(DemoOperationsData.facilities().stream()
                .map(ElasticsearchDocumentMapper::toDocument)
                .toList());
        recommendationRepository.saveAll(DemoOperationsData.recommendations().stream()
                .map(ElasticsearchDocumentMapper::toDocument)
                .toList());
        saveLastUpdated(DemoOperationsData.INITIAL_LAST_UPDATED);
    }

    private Instant lastUpdated() {
        return stateRepository.findById(DashboardStateDocument.DASHBOARD_ID)
                .map(DashboardStateDocument::getLastUpdated)
                .orElse(DemoOperationsData.INITIAL_LAST_UPDATED);
    }

    private void saveLastUpdated(Instant lastUpdated) {
        DashboardStateDocument state = new DashboardStateDocument();
        state.setId(DashboardStateDocument.DASHBOARD_ID);
        state.setLastUpdated(lastUpdated);
        stateRepository.save(state);
    }

    private Query buildIncidentQuery(IncidentSearchCriteria criteria) {
        List<Query> must = new ArrayList<>();
        List<Query> filter = new ArrayList<>();

        if (StringUtils.hasText(criteria.search())) {
            must.add(Query.of(query -> query.multiMatch(multiMatch -> multiMatch
                    .query(criteria.search().trim())
                    .fields("title", "location", "description"))));
        }

        if (criteria.severity() != null) {
            filter.add(termQuery("severity", criteria.severity().jsonValue()));
        }
        if (criteria.kind() != null) {
            filter.add(termQuery("kind", criteria.kind().jsonValue()));
        }
        if (criteria.status() != null) {
            filter.add(termQuery("status", criteria.status().jsonValue()));
        }
        if (criteria.source() != null) {
            filter.add(termQuery("source", criteria.source().jsonValue()));
        }

        if (must.isEmpty() && filter.isEmpty()) {
            return Query.of(query -> query.matchAll(matchAll -> matchAll));
        }

        return Query.of(query -> query.bool(bool -> bool.must(must).filter(filter)));
    }

    private Query buildDeclarationQuery(DeclarationSearchCriteria criteria) {
        List<Query> must = new ArrayList<>();
        List<Query> filter = new ArrayList<>();

        if (StringUtils.hasText(criteria.search())) {
            must.add(Query.of(query -> query.multiMatch(multiMatch -> multiMatch
                    .query(criteria.search().trim())
                    .fields("title", "declaredAreas"))));
        }
        if (StringUtils.hasText(criteria.state())) {
            filter.add(termQuery("state", criteria.state().trim().toUpperCase()));
        }
        if (StringUtils.hasText(criteria.incidentType())) {
            filter.add(termQuery("incidentType", criteria.incidentType().trim()));
        }
        if (StringUtils.hasText(criteria.declarationType())) {
            filter.add(termQuery("declarationType", criteria.declarationType().trim()));
        }

        if (must.isEmpty() && filter.isEmpty()) {
            return Query.of(query -> query.matchAll(matchAll -> matchAll));
        }

        return Query.of(query -> query.bool(bool -> bool.must(must).filter(filter)));
    }

    private Query termQuery(String field, String value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> items = new ArrayList<>();
        iterable.forEach(items::add);
        return items;
    }
}
