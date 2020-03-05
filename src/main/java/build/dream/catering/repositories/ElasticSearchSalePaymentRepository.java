package build.dream.catering.repositories;

import build.dream.catering.domains.ElasticSearchSalePayment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchSalePaymentRepository extends ElasticsearchRepository<ElasticSearchSalePayment, Long> {
}
