package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.common.catering.domains.*;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.JacksonUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DataService {
    @Transactional(rollbackFor = Exception.class)
    public void saveDietOrder(String dietOrderData) {
        String signature = null;
        try {
            signature = DigestUtils.md5Hex(dietOrderData);
            boolean isNotExists = CacheUtils.setnx(signature, signature);
            if (!isNotExists) {
                return;
            }

            CacheUtils.expire(signature, 30, TimeUnit.DAYS);

            DataHandleHistory dataHandleHistory = new DataHandleHistory();
            dataHandleHistory.setSignature(signature);
            dataHandleHistory.setDataType(Constants.DATA_TYPE_DIET_ORDER);
            dataHandleHistory.setDataContent(dietOrderData);
            dataHandleHistory.setHandleTime(new Date());
            DatabaseHelper.insert(dataHandleHistory);

            Map<String, Object> dataMap = JacksonUtils.readValueAsMap(dietOrderData, String.class, Object.class);
            String dietOrderJson = MapUtils.getString(dataMap, "dietOrder");
            String dietOrderGroupsJson = MapUtils.getString(dataMap, "dietOrderGroups");
            String dietOrderDetailsJson = MapUtils.getString(dataMap, "dietOrderDetails");
            String dietOrderPaymentsJson = MapUtils.getString(dataMap, "dietOrderPayments");
            String dietOrderDetailGoodsAttributesJson = MapUtils.getString(dataMap, "dietOrderDetailGoodsAttributes");
            String dietOrderActivitiesJson = MapUtils.getString(dataMap, "dietOrderActivities");

            DietOrder dietOrder = JacksonUtils.readValue(dietOrderJson, DietOrder.class);
            DatabaseHelper.insert(dietOrder);

            BigInteger dietOrderId = dietOrder.getId();

            List<DietOrderGroup> dietOrderGroups = JacksonUtils.readValueAsList(dietOrderGroupsJson, DietOrderGroup.class);
            Map<String, DietOrderGroup> dietOrderGroupMap = new HashMap<String, DietOrderGroup>();
            for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
                dietOrderGroup.setDietOrderId(dietOrderId);
                dietOrderGroupMap.put(dietOrderGroup.getLocalId(), dietOrderGroup);
            }
            DatabaseHelper.insertAll(dietOrderGroups);

            List<DietOrderDetail> dietOrderDetails = JacksonUtils.readValueAsList(dietOrderDetailsJson, DietOrderDetail.class);
            Map<String, DietOrderDetail> dietOrderDetailMap = new HashMap<String, DietOrderDetail>();
            for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
                DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetail.getLocalDietOrderGroupId());
                dietOrderDetail.setDietOrderId(dietOrderId);
                dietOrderDetail.setDietOrderGroupId(dietOrderGroup.getId());
                dietOrderDetailMap.put(dietOrderDetail.getLocalId(), dietOrderDetail);
            }
            DatabaseHelper.insertAll(dietOrderDetails);

            List<DietOrderPayment> dietOrderPayments = JacksonUtils.readValueAsList(dietOrderPaymentsJson, DietOrderPayment.class);
            for (DietOrderPayment dietOrderPayment : dietOrderPayments) {
                dietOrderPayment.setDietOrderId(dietOrderId);
            }
            DatabaseHelper.insertAll(dietOrderPayments);

            if (StringUtils.isNotBlank(dietOrderDetailGoodsAttributesJson)) {
                List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = JacksonUtils.readValueAsList(dietOrderDetailGoodsAttributesJson, DietOrderDetailGoodsAttribute.class);
                for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
                    DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderGroupId());
                    DietOrderDetail dietOrderDetail = dietOrderDetailMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderDetailId());
                    dietOrderDetailGoodsAttribute.setDietOrderId(dietOrderId);
                    dietOrderDetailGoodsAttribute.setDietOrderGroupId(dietOrderGroup.getId());
                    dietOrderDetailGoodsAttribute.setDietOrderDetailId(dietOrderDetail.getId());
                }
                DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
            }

            if (StringUtils.isNotBlank(dietOrderActivitiesJson)) {
                List<DietOrderActivity> dietOrderActivities = JacksonUtils.readValueAsList(dietOrderActivitiesJson, DietOrderActivity.class);
                for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                    dietOrderActivity.setDietOrderId(dietOrderId);
                }
                DatabaseHelper.insertAll(dietOrderActivities);
            }
        } catch (Exception e) {
            CacheUtils.delete(signature);
            throw e;
        }
    }
}
