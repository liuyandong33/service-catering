package build.dream.catering.repositories;

import build.dream.catering.domains.ElasticSearchBranch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchBranchRepository extends ElasticsearchRepository<ElasticSearchBranch, Long> {

}
