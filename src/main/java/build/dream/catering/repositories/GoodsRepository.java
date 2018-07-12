package build.dream.catering.repositories;

import build.dream.common.erp.catering.domains.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods, BigInteger> {
    Optional<Goods> findById(BigInteger id);
}
