package build.dream.catering.repositories;

import build.dream.catering.domains.MongoSaleDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MongoSaleDetailRepository extends MongoRepository<MongoSaleDetail, BigInteger> {

}
