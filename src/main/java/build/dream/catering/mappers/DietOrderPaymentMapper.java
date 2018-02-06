package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrderPayment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderPaymentMapper {
    long insert(DietOrderPayment dietOrderPayment);
    long insertAll(List<DietOrderPayment> dietOrderPayments);
}
