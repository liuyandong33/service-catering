package build.dream.catering.repositories;

import build.dream.catering.domains.ElasticSearchSaleDetail;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchSaleDetailRepository extends ElasticsearchRepository<ElasticSearchSaleDetail, Long> {
}
