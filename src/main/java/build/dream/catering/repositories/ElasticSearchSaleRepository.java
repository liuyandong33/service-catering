package build.dream.catering.repositories;

import build.dream.catering.domains.ElasticSearchSale;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchSaleRepository extends ElasticsearchRepository<ElasticSearchSale, Long> {
}
