package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    Long countSingleSummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> singleSummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("rows") int rows);

    Long countCategorySummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> categorySummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("rows") int rows);

    Long countPaymentSummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> paymentSummary(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("rows") int rows);
}
