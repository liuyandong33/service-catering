package build.dream.catering.repositories;

import build.dream.catering.domains.MongoSaleDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoSaleDetailRepository extends MongoRepository<MongoSaleDetail, Long> {

}
