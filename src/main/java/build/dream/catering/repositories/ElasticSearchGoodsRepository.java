package build.dream.catering.repositories;

import build.dream.catering.domains.ElasticSearchGoods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchGoodsRepository extends ElasticsearchRepository<ElasticSearchGoods, Long> {

}
