package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    Long countSingleSummary(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> singleSummary(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("rows") int rows);

    Long countCategorySummary(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> categorySummary(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("offset") int offset, @Param("rows") int rows);
}
