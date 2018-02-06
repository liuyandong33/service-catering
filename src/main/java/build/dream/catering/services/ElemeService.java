package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.eleme.*;
import build.dream.catering.utils.ElemeUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.*;

@Service
public class ElemeService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private ElemeOrderMapper elemeOrderMapper;
    @Autowired
    private ElemeOrderGroupMapper elemeOrderGroupMapper;
    @Autowired
    private ElemeOrderItemMapper elemeOrderItemMapper;
    @Autowired
    private ElemeOrderItemAttributeMapper elemeOrderItemAttributeMapper;
    @Autowired
    private ElemeOrderItemNewSpecMapper elemeOrderItemNewSpecMapper;
    @Autowired
    private ElemeOrderActivityMapper elemeOrderActivityMapper;
    @Autowired
    private ElemeRefundOrderMessageMapper elemeRefundOrderMessageMapper;
    @Autowired
    private ElemeReminderMessageMapper elemeReminderMessageMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private ElemeOrderStateChangeMessageMapper elemeOrderStateChangeMessageMapper;
    @Autowired
    private ElemeDeliveryOrderStateChangeMessageMapper elemeDeliveryOrderStateChangeMessageMapper;
    @Autowired
    private ElemeRefundOrderMessageGoodsItemMapper elemeRefundOrderMessageGoodsItemMapper;
    @Autowired
    private UniversalMapper universalMapper;

    @Transactional(readOnly = true)
    public ApiRest tenantAuthorize(BigInteger tenantId, BigInteger branchId, BigInteger userId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", "=", tenantId);
        searchModel.addSearchCondition("id", "=", branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        Map<String, String> checkIsAuthorizeRequestParameters = new HashMap<String, String>();
        checkIsAuthorizeRequestParameters.put("tenantId", tenantId.toString());
        checkIsAuthorizeRequestParameters.put("branchId", branchId.toString());

        String elemeAccountType = branch.getElemeAccountType().toString();
        checkIsAuthorizeRequestParameters.put("elemeAccountType", elemeAccountType);
        String checkIsAuthorizeResult = ProxyUtils.doGetOriginalWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "checkIsAuthorize", checkIsAuthorizeRequestParameters);
        ApiRest checkIsAuthorizeApiRest = ApiRest.fromJson(checkIsAuthorizeResult);
        Validate.isTrue(checkIsAuthorizeApiRest.isSuccessful(), checkIsAuthorizeApiRest.getError());
        Map<String, Object> checkIsAuthorizeApiRestData = (Map<String, Object>) checkIsAuthorizeApiRest.getData();
        boolean isAuthorize = (boolean) checkIsAuthorizeApiRestData.get("isAuthorize");

        String data = null;
        if (isAuthorize) {
            String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
            data = SystemPartitionUtils.getOutsideUrl(Constants.SERVICE_NAME_POSAPI, "eleme", "bindingStore") + "?serviceName=" + serviceName + "&controllerName=eleme&actionName=bindingStore" + "&tenantId=" + tenantId + "&branchId=" + branchId + "&userId=" + userId;
        } else {
            String elemeUrl = ConfigurationUtils.getConfiguration(Constants.ELEME_SERVICE_URL);
            String elemeAppKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);

            String outServiceOutsideServiceDomain = SystemPartitionUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT);
            data = String.format(Constants.ELEME_TENANT_AUTHORIZE_URL_FORMAT, elemeUrl + "/" + "authorize", "code", elemeAppKey, URLEncoder.encode(outServiceOutsideServiceDomain + "/eleme/tenantAuthorizeCallback", Constants.CHARSET_NAME_UTF_8), tenantId + "Z" + branchId + "Z" + userId + "Z" + elemeAccountType, "all");
        }
        ApiRest apiRest = new ApiRest(data, "生成授权链接成功！");
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveElemeOrder(BigInteger shopId, String message, Integer type, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(message);

        String openId = messageJsonObject.getString("openId");
        String[] array = openId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(array[0]);
        BigInteger branchId = NumberUtils.createBigInteger(array[1]);

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        branchSearchModel.addSearchCondition("shop_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, shopId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        String tenantCode = branch.getTenantCode();

        // 开始保存饿了么订单
        String id = messageJsonObject.getString("id");
        messageJsonObject.remove("id");

        JSONArray phoneList = messageJsonObject.optJSONArray("phoneList");
        messageJsonObject.remove("phoneList");

        JSONArray elemeGroupJsonArray = messageJsonObject.optJSONArray("groups");
        messageJsonObject.remove("groups");

        JSONArray orderActivityJsonArray = messageJsonObject.optJSONArray("orderActivities");
        messageJsonObject.remove("orderActivities");

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        // TODO 上线之前删除
        userId = BigInteger.ONE;


        ElemeOrder elemeOrder = GsonUtils.fromJson(messageJsonObject.toString(), ElemeOrder.class, "yyyy-MM-dd'T'HH:mm:ss");
        elemeOrder.setTenantId(tenantId);
        elemeOrder.setTenantCode(tenantCode);
        elemeOrder.setBranchId(branchId);
        elemeOrder.setCreateUserId(userId);
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("饿了么系统推送新订单，保存订单！");
        elemeOrderMapper.insert(elemeOrder);

        List<ElemeOrderItem> elemeOrderItems = new ArrayList<ElemeOrderItem>();

        int elemeGroupJsonArraySize = elemeGroupJsonArray.size();
        for (int index = 0; index < elemeGroupJsonArraySize; index++) {
            JSONObject elemeGroupJsonObject = elemeGroupJsonArray.getJSONObject(index);
            ElemeOrderGroup elemeOrderGroup = new ElemeOrderGroup();
            elemeOrderGroup.setTenantId(tenantId);
            elemeOrderGroup.setTenantCode(tenantCode);
            elemeOrderGroup.setBranchId(branchId);
            elemeOrderGroup.setElemeOrderId(elemeOrder.getId());
            elemeOrderGroup.setOrderId(id);
            elemeOrderGroup.setName(elemeGroupJsonObject.optString("name"));
            elemeOrderGroup.setType(elemeGroupJsonObject.optString("type"));
            elemeOrderGroup.setCreateUserId(userId);
            elemeOrderGroup.setLastUpdateUserId(userId);
            elemeOrderGroup.setLastUpdateRemark("饿了么系统推送新订单，保存订单分组！");
            elemeOrderGroupMapper.insert(elemeOrderGroup);

            JSONArray elemeOrderItemJsonArray = elemeGroupJsonObject.optJSONArray("items");
            int elemeOrderItemJsonArraySize = elemeOrderItemJsonArray.size();
            for (int elemeOrderItemJsonArrayIndex = 0; elemeOrderItemJsonArrayIndex < elemeOrderItemJsonArraySize; elemeOrderItemJsonArrayIndex++) {
                JSONObject elemeOrderItemJsonObject = elemeOrderItemJsonArray.optJSONObject(elemeOrderItemJsonArrayIndex);
                ElemeOrderItem elemeOrderItem = new ElemeOrderItem();
                elemeOrderItem.setTenantId(tenantId);
                elemeOrderItem.setTenantCode(tenantCode);
                elemeOrderItem.setBranchId(branchId);
                elemeOrderItem.setElemeOrderId(elemeOrder.getId());
                elemeOrderItem.setOrderId(id);
                elemeOrderItem.setElemeOrderGroupId(elemeOrderGroup.getId());
                elemeOrderItem.setElemeItemId(BigInteger.valueOf(elemeOrderItemJsonObject.getLong("id")));
                elemeOrderItem.setSkuId(BigInteger.valueOf(elemeOrderItemJsonObject.getLong("skuId")));
                elemeOrderItem.setName(elemeOrderItemJsonObject.getString("name"));
                elemeOrderItem.setCategoryId(BigInteger.valueOf(elemeOrderItemJsonObject.getLong("categoryId")));
                elemeOrderItem.setPrice(BigDecimal.valueOf(elemeOrderItemJsonObject.optDouble("price")));
                elemeOrderItem.setQuantity(elemeOrderItemJsonObject.optInt("quantity"));
                elemeOrderItem.setTotal(BigDecimal.valueOf(elemeOrderItemJsonObject.optDouble("total")));
                elemeOrderItem.setExtendCode(elemeOrderItemJsonObject.optString("extendCode"));
                elemeOrderItem.setBarCode(elemeOrderItemJsonObject.optString("barCode"));
                elemeOrderItem.setUserPrice(BigDecimal.valueOf(elemeOrderItemJsonObject.getDouble("userPrice")));
                elemeOrderItem.setShopPrice(BigDecimal.valueOf(elemeOrderItemJsonObject.getDouble("shopPrice")));
                elemeOrderItem.setVfoodId(BigInteger.valueOf(elemeOrderItemJsonObject.getLong("vfoodId")));

                Double weight = elemeOrderItemJsonObject.optDouble("weight");
                if (!Double.isNaN(weight)) {
                    elemeOrderItem.setWeight(BigDecimal.valueOf(Double.valueOf(weight.toString())));
                }
                elemeOrderItem.setCreateUserId(userId);
                elemeOrderItem.setLastUpdateUserId(userId);
                elemeOrderItem.setLastUpdateRemark("饿了么系统推送新订单，保存菜品信息！");
                elemeOrderItemMapper.insert(elemeOrderItem);

                JSONArray elemeOrderItemAttributeJsonArray = elemeOrderItemJsonObject.optJSONArray("attributes");
                if (elemeOrderItemAttributeJsonArray != null) {
                    int elemeOrderItemAttributeJsonArraySize = elemeOrderItemAttributeJsonArray.size();
                    for (int elemeOrderItemAttributeJsonArrayIndex = 0; elemeOrderItemAttributeJsonArrayIndex < elemeOrderItemAttributeJsonArraySize; elemeOrderItemAttributeJsonArrayIndex++) {
                        JSONObject elemeOrderItemAttributeJsonObject = elemeOrderItemAttributeJsonArray.optJSONObject(index);
                        ElemeOrderItemAttribute elemeOrderItemAttribute = new ElemeOrderItemAttribute();
                        elemeOrderItemAttribute.setTenantId(tenantId);
                        elemeOrderItemAttribute.setTenantCode(tenantCode);
                        elemeOrderItemAttribute.setBranchId(branchId);
                        elemeOrderItemAttribute.setElemeOrderItemId(elemeOrder.getId());
                        elemeOrderItemAttribute.setOrderId(id);
                        elemeOrderItemAttribute.setElemeOrderItemId(elemeOrderItem.getId());
                        elemeOrderItemAttribute.setName(elemeOrderItemAttributeJsonObject.optString("name"));
                        elemeOrderItemAttribute.setValue(elemeOrderItemAttributeJsonObject.optString("value"));
                        elemeOrderItemAttribute.setCreateUserId(userId);
                        elemeOrderItemAttribute.setLastUpdateUserId(userId);
                        elemeOrderItemAttribute.setLastUpdateRemark("饿了么系统推送新订单，保存菜品属性！");
                        elemeOrderItemAttributeMapper.insert(elemeOrderItemAttribute);
                    }
                }

                JSONArray elemeOrderItemNewSpecJsonArray = elemeOrderItemJsonObject.optJSONArray("newSpecs");
                if (elemeOrderItemNewSpecJsonArray != null) {
                    int elemeOrderItemNewSpecJsonArraySize = elemeOrderItemNewSpecJsonArray.size();
                    for (int elemeOrderItemNewSpecJsonArrayIndex = 0; elemeOrderItemNewSpecJsonArrayIndex < elemeOrderItemNewSpecJsonArraySize; elemeOrderItemNewSpecJsonArrayIndex++) {
                        JSONObject elemeOrderItemNewSpecJsonObject = elemeOrderItemNewSpecJsonArray.optJSONObject(index);
                        ElemeOrderItemNewSpec elemeOrderItemNewSpec = new ElemeOrderItemNewSpec();
                        elemeOrderItemNewSpec.setTenantId(tenantId);
                        elemeOrderItemNewSpec.setTenantCode(tenantCode);
                        elemeOrderItemNewSpec.setBranchId(branchId);
                        elemeOrderItemNewSpec.setElemeOrderId(elemeOrder.getId());
                        elemeOrderItemNewSpec.setOrderId(id);
                        elemeOrderItemNewSpec.setElemeOrderItemId(elemeOrderItem.getId());
                        elemeOrderItemNewSpec.setName(elemeOrderItemNewSpecJsonObject.optString("name"));
                        elemeOrderItemNewSpec.setValue(elemeOrderItemNewSpecJsonObject.optString("value"));
                        elemeOrderItemNewSpec.setCreateUserId(userId);
                        elemeOrderItemNewSpec.setLastUpdateUserId(userId);
                        elemeOrderItemNewSpec.setLastUpdateRemark("饿了么系统推送新订单，保存菜品规格！");
                        elemeOrderItemNewSpecMapper.insert(elemeOrderItemNewSpec);
                    }
                }
            }
        }

        if (orderActivityJsonArray != null) {
            int orderActivityJsonArraySize = orderActivityJsonArray.size();
            for (int orderActivityJsonArrayIndex = 0; orderActivityJsonArrayIndex < orderActivityJsonArraySize; orderActivityJsonArrayIndex++) {
                JSONObject elemeActivityJsonObject = orderActivityJsonArray.optJSONObject(orderActivityJsonArrayIndex);
                ElemeOrderActivity elemeOrderActivity = new ElemeOrderActivity();
                elemeOrderActivity.setTenantId(tenantId);
                elemeOrderActivity.setTenantCode(tenantCode);
                elemeOrderActivity.setBranchId(branchId);
                elemeOrderActivity.setElemeOrderId(elemeOrder.getId());
                elemeOrderActivity.setOrderId(id);
                elemeOrderActivity.setElemeOrderId(elemeOrder.getId());
                elemeOrderActivity.setElemeActivityId(BigInteger.valueOf(elemeActivityJsonObject.optLong("id")));
                elemeOrderActivity.setName(elemeActivityJsonObject.optString("name"));
                elemeOrderActivity.setCategoryId(elemeActivityJsonObject.optInt("categoryId"));
                elemeOrderActivity.setElemePart(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("elemePart")));
                elemeOrderActivity.setRestaurantPart(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("restaurantPart")));
                elemeOrderActivity.setAmount(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("amount")));
                elemeOrderActivity.setCreateUserId(userId);
                elemeOrderActivity.setLastUpdateUserId(userId);
                elemeOrderActivity.setLastUpdateRemark("饿了么系统推送新订单，保存饿了么订单活动！");
                elemeOrderActivityMapper.insert(elemeOrderActivity);
            }
        }
//        CacheUtils.hset(elemeOrder.getOrderId(), type.toString(), uuid);
//        CacheUtils.expire(elemeOrder.getOrderId(), 48, TimeUnit.HOURS);
        publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
    }

    /**
     * 处理饿了么退单消息
     *
     * @param shopId：店铺ID
     * @param message：消息内容
     * @param type：消息类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeRefundOrderMessage(BigInteger shopId, String message, Integer type, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        String refundStatus = messageJsonObject.optString("refundStatus");
        elemeOrder.setRefundStatus(refundStatus);

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("处理退单消息回调！");
        elemeOrderMapper.update(elemeOrder);

        BigInteger tenantId = elemeOrder.getTenantId();
        String tenantCode = elemeOrder.getTenantCode();
        BigInteger branchId = elemeOrder.getBranchId();
        BigInteger elemeOrderId = elemeOrder.getId();

        ElemeRefundOrderMessage elemeRefundOrderMessage = new ElemeRefundOrderMessage();
        elemeRefundOrderMessage.setTenantId(tenantId);
        elemeRefundOrderMessage.setTenantCode(tenantCode);
        elemeRefundOrderMessage.setBranchId(branchId);
        elemeRefundOrderMessage.setElemeOrderId(elemeOrder.getId());
        elemeRefundOrderMessage.setOrderId(orderId);
        elemeRefundOrderMessage.setRefundStatus(messageJsonObject.optString("refundStatus"));
        elemeRefundOrderMessage.setReason(messageJsonObject.optString("reason"));
        elemeRefundOrderMessage.setShopId(shopId);
        elemeRefundOrderMessage.setRefundType(messageJsonObject.optString("refundType"));
        elemeRefundOrderMessage.setTotalPrice(BigDecimal.valueOf(messageJsonObject.optDouble("totalPrice")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageJsonObject.getLong("updateTime") * 1000);
        elemeRefundOrderMessage.setUpdateTime(calendar.getTime());
        elemeRefundOrderMessage.setCreateUserId(userId);
        elemeRefundOrderMessage.setLastUpdateUserId(userId);
        elemeRefundOrderMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么退单信息！");
        elemeRefundOrderMessageMapper.insert(elemeRefundOrderMessage);

        JSONArray goodsList = messageJsonObject.getJSONArray("goodsList");
        int size = goodsList.size();
        List<ElemeRefundOrderMessageGoodsItem> elemeRefundOrderMessageGoodsItems = new ArrayList<ElemeRefundOrderMessageGoodsItem>();
        for (int index = 0; index < size; index++) {
            JSONObject itemJsonObject = goodsList.getJSONObject(index);
            ElemeRefundOrderMessageGoodsItem elemeRefundOrderMessageGoodsItem = new ElemeRefundOrderMessageGoodsItem();
            elemeRefundOrderMessageGoodsItem.setTenantId(tenantId);
            elemeRefundOrderMessageGoodsItem.setTenantCode(tenantCode);
            elemeRefundOrderMessageGoodsItem.setBranchId(branchId);
            elemeRefundOrderMessageGoodsItem.setElemeOrderId(elemeOrderId);
            elemeRefundOrderMessageGoodsItem.setOrderId(orderId);
            elemeRefundOrderMessageGoodsItem.setElemeRefundOrderMessageId(elemeRefundOrderMessage.getId());
            elemeRefundOrderMessageGoodsItem.setName(itemJsonObject.optString("name"));
            elemeRefundOrderMessageGoodsItem.setQuantity(itemJsonObject.optInt("quantity"));
            elemeRefundOrderMessageGoodsItem.setPrice(BigDecimal.valueOf(itemJsonObject.optDouble("price")));
            elemeRefundOrderMessageGoodsItems.add(elemeRefundOrderMessageGoodsItem);
        }
        if (CollectionUtils.isNotEmpty(elemeRefundOrderMessageGoodsItems)) {
            elemeRefundOrderMessageGoodsItemMapper.insertAll(elemeRefundOrderMessageGoodsItems);
        }

        if (type == 20 || type == 21 || type == 24 || type == 25 || type == 26 || type == 30 || type == 31 || type == 34 || type == 35 || type == 36) {
            publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
        }
    }

    /**
     * 处理饿了么催单消息
     *
     * @param shopId：饿了么店铺ID
     * @param message：消息内容
     * @param type：消息类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeReminderMessage(BigInteger shopId, String message, Integer type, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        ElemeReminderMessage elemeReminderMessage = new ElemeReminderMessage();
        elemeReminderMessage.setElemeOrderId(elemeOrder.getId());
        elemeReminderMessage.setOrderId(orderId);
        elemeReminderMessage.setShopId(shopId);
        elemeReminderMessage.setReminderId(BigInteger.valueOf(messageJsonObject.optLong("remindId")));
        elemeReminderMessage.setUserId(BigInteger.valueOf(messageJsonObject.getLong("userId")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageJsonObject.getLong("updateTime") * 1000);
        elemeReminderMessage.setUpdateTime(calendar.getTime());
        elemeReminderMessage.setTenantId(elemeOrder.getTenantId());
        elemeReminderMessage.setBranchId(elemeOrder.getBranchId());

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        elemeReminderMessage.setCreateUserId(userId);
        elemeReminderMessage.setLastUpdateUserId(userId);
        elemeReminderMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么催单信息！");
        elemeReminderMessageMapper.insert(elemeReminderMessage);
        if (type == 45) {
            publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleElemeOrderStateChangeMessage(BigInteger shopId, String message, Integer type, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");


        String state = elemeOrder.getStatus();
        elemeOrder.setStatus(state);

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("处理饿了么订单状态变更消息，修改订单状态！");
        elemeOrderMapper.update(elemeOrder);

        ElemeOrderStateChangeMessage elemeOrderStateChangeMessage = new ElemeOrderStateChangeMessage();
        elemeOrderStateChangeMessage.setElemeOrderId(elemeOrder.getId());
        elemeOrderStateChangeMessage.setOrderId(orderId);
        elemeOrderStateChangeMessage.setState(messageJsonObject.getString("state"));
        elemeOrderStateChangeMessage.setShopId(BigInteger.valueOf(messageJsonObject.getLong("shopId")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageJsonObject.getLong("updateTime"));
        elemeOrderStateChangeMessage.setUpdateTime(calendar.getTime());
        elemeOrderStateChangeMessage.setRole(messageJsonObject.getInt("role"));
        elemeOrderStateChangeMessage.setTenantId(elemeOrder.getTenantId());
        elemeOrderStateChangeMessage.setBranchId(elemeOrder.getBranchId());
        elemeOrderStateChangeMessage.setCreateUserId(userId);
        elemeOrderStateChangeMessage.setLastUpdateUserId(userId);
        elemeOrderStateChangeMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么订单变更消息！");

        elemeOrderStateChangeMessageMapper.insert(elemeOrderStateChangeMessage);
        publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleElemeDeliveryOrderStateChangeMessage(BigInteger shopId, String message, Integer type, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        // TODO 上线之前删除
        userId = BigInteger.ZERO;
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("处理饿了么订单状态变更消息，修改订单状态！");
        elemeOrderMapper.update(elemeOrder);

        ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage = new ElemeDeliveryOrderStateChangeMessage();
        elemeDeliveryOrderStateChangeMessage.setElemeOrderId(elemeOrder.getId());
        elemeDeliveryOrderStateChangeMessage.setOrderId(elemeOrder.getOrderId());
        elemeDeliveryOrderStateChangeMessage.setShopId(shopId);
        elemeDeliveryOrderStateChangeMessage.setState(messageJsonObject.optString("state"));
        elemeDeliveryOrderStateChangeMessage.setSubState(messageJsonObject.optString("subState"));
        elemeDeliveryOrderStateChangeMessage.setName(messageJsonObject.optString("name"));
        elemeDeliveryOrderStateChangeMessage.setPhone(messageJsonObject.optString("phone"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageJsonObject.getLong("updateAt"));
        elemeDeliveryOrderStateChangeMessage.setUpdateTime(calendar.getTime());
        elemeDeliveryOrderStateChangeMessage.setTenantId(elemeOrder.getTenantId());
        elemeDeliveryOrderStateChangeMessage.setBranchId(elemeOrder.getBranchId());
        elemeDeliveryOrderStateChangeMessage.setCreateUserId(userId);
        elemeDeliveryOrderStateChangeMessage.setLastUpdateUserId(userId);
        elemeDeliveryOrderStateChangeMessage.setLastUpdateRemark("处理饿了么回调，保存饿了么运单状态变更消息！");

        elemeDeliveryOrderStateChangeMessageMapper.insert(elemeDeliveryOrderStateChangeMessage);
        publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
    }

    public void handleElemeShopStateChangeMessage(BigInteger shopId, String message, Integer type, String uuid) {

    }

    public void handleAuthorizationStateChangeMessage(BigInteger shopId, String message, Integer type, String uuid) {

    }

    @Transactional(readOnly = true)
    public Branch findBranchInfo(BigInteger tenantId, BigInteger branchId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        return branch;
    }

    @Transactional(readOnly = true)
    public GoodsCategory findGoodsCategoryInfo(BigInteger tenantId, BigInteger branchId, BigInteger categoryId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, categoryId);
        GoodsCategory goodsCategory = goodsCategoryMapper.find(searchModel);
        Validate.notNull(goodsCategory, "分类信息不存在！");
        return goodsCategory;
    }

    @Transactional(readOnly = true)
    public ElemeOrder findElemeOrderInfo(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(searchModel);
        Validate.notNull(elemeOrder, "订单不存在！");
        return elemeOrder;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainElemeDeliveryOrderStateChangeMessage(ObtainElemeDeliveryOrderStateChangeMessageModel obtainElemeDeliveryOrderStateChangeMessageModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainElemeDeliveryOrderStateChangeMessageModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainElemeDeliveryOrderStateChangeMessageModel.getBranchId());
        searchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainElemeDeliveryOrderStateChangeMessageModel.getElemeOrderId());
        List<ElemeDeliveryOrderStateChangeMessage> elemeDeliveryOrderStateChangeMessages = elemeDeliveryOrderStateChangeMessageMapper.findAll(searchModel);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(elemeDeliveryOrderStateChangeMessages);
        apiRest.setMessage("获取饿了么订单运单状态变更信息成功");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest pullElemeOrder(PullElemeOrderModel pullElemeOrderModel) {
        BigInteger tenantId = pullElemeOrderModel.getTenantId();
        BigInteger branchId = pullElemeOrderModel.getBranchId();
        BigInteger elemeOrderId = pullElemeOrderModel.getElemeOrderId();
        // 查询订单
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "订单不存在！");

        // 查询订单分组
        SearchModel elemeOrderGroupSearchModel = new SearchModel(true);
        elemeOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderGroupSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        List<ElemeOrderGroup> elemeOrderGroups = elemeOrderGroupMapper.findAll(elemeOrderGroupSearchModel);

        // 查询所有订单分组明细
        SearchModel elemeOrderItemSearchModel = new SearchModel(true);
        elemeOrderItemSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderItemSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderItemSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        List<ElemeOrderItem> elemeOrderItems = elemeOrderItemMapper.findAll(elemeOrderItemSearchModel);

        // 查询出所有的商品规格
        SearchModel elemeOrderItemNewSpecSearchModel = new SearchModel(true);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecs = elemeOrderItemNewSpecMapper.findAll(elemeOrderItemNewSpecSearchModel);

        // 查询出所有的商品属性
        SearchModel elemeOrderItemAttributeSearchModel = new SearchModel(true);
        elemeOrderItemAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderItemAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderItemAttributeSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrderId);
        List<ElemeOrderItemAttribute> elemeOrderItemAttributes = elemeOrderItemAttributeMapper.findAll(elemeOrderItemAttributeSearchModel);

        // 查询出订单包含的所有活动
        SearchModel elemeOrderActivitySearchModel = new SearchModel(true);
        elemeOrderActivitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        elemeOrderActivitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        elemeOrderActivitySearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrder.getId());
        List<ElemeOrderActivity> elemeActivities = elemeOrderActivityMapper.findAll(elemeOrderActivitySearchModel);

        // 封装订单分组与订单明细之间的 map
        Map<BigInteger, List<ElemeOrderItem>> elemeOrderItemMap = new HashMap<BigInteger, List<ElemeOrderItem>>();
        for (ElemeOrderItem elemeOrderItem : elemeOrderItems) {
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeOrderItem.getElemeOrderGroupId());
            if (elemeOrderItemList == null) {
                elemeOrderItemList = new ArrayList<ElemeOrderItem>();
                elemeOrderItemMap.put(elemeOrderItem.getElemeOrderGroupId(), elemeOrderItemList);
            }
            elemeOrderItemList.add(elemeOrderItem);
        }

        // 封装订单明细与商品规格之间的 map
        Map<BigInteger, List<ElemeOrderItemNewSpec>> elemeOrderItemNewSpecMap = new HashMap<BigInteger, List<ElemeOrderItemNewSpec>>();
        for (ElemeOrderItemNewSpec elemeOrderItemNewSpec : elemeOrderItemNewSpecs) {
            List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecList = elemeOrderItemNewSpecMap.get(elemeOrderItemNewSpec.getElemeOrderItemId());
            if (elemeOrderItemNewSpecList == null) {
                elemeOrderItemNewSpecList = new ArrayList<ElemeOrderItemNewSpec>();
                elemeOrderItemNewSpecMap.put(elemeOrderItemNewSpec.getElemeOrderItemId(), elemeOrderItemNewSpecList);
            }
            elemeOrderItemNewSpecList.add(elemeOrderItemNewSpec);
        }

        // 封装订单明细与商品属性之间的 map
        Map<BigInteger, List<ElemeOrderItemAttribute>> elemeOrderItemAttributeMap = new HashMap<BigInteger, List<ElemeOrderItemAttribute>>();
        for (ElemeOrderItemAttribute elemeOrderItemAttribute : elemeOrderItemAttributes) {
            List<ElemeOrderItemAttribute> elemeOrderItemAttributeList = elemeOrderItemAttributeMap.get(elemeOrderItemAttribute.getElemeOrderItemId());
            if (elemeOrderItemAttributeList == null) {
                elemeOrderItemAttributeList = new ArrayList<ElemeOrderItemAttribute>();
                elemeOrderItemAttributeMap.put(elemeOrderItemAttribute.getElemeOrderItemId(), elemeOrderItemAttributeList);
            }
            elemeOrderItemAttributeList.add(elemeOrderItemAttribute);
        }

        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (ElemeOrderGroup elemeOrderGroup : elemeOrderGroups) {
            Map<String, Object> elemeGroupMap = new HashMap<String, Object>();
            elemeGroupMap.put("name", elemeOrderGroup.getName());
            elemeGroupMap.put("type", elemeOrderGroup.getType());
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeOrderGroup.getId());
            for (ElemeOrderItem elemeOrderItem : elemeOrderItemList) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", elemeOrderItem.getElemeItemId());
                item.put("skuId", elemeOrderItem.getSkuId());
                item.put("name", elemeOrderItem.getName());
                item.put("categoryId", elemeOrderItem.getCategoryId());
                item.put("price", elemeOrderItem.getPrice());
                item.put("quantity", elemeOrderItem.getQuantity());
                item.put("total", elemeOrderItem.getTotal());

                List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecList = elemeOrderItemNewSpecMap.get(elemeOrderItem.getId());
                List<Map<String, String>> newSpecs = new ArrayList<Map<String, String>>();
                if (CollectionUtils.isNotEmpty(elemeOrderItemNewSpecList)) {
                    for (ElemeOrderItemNewSpec elemeOrderItemNewSpec : elemeOrderItemNewSpecList) {
                        Map<String, String> newSpec = new HashMap<String, String>();
                        newSpec.put("name", elemeOrderItemNewSpec.getName());
                        newSpec.put("value", elemeOrderItemNewSpec.getValue());
                        newSpecs.add(newSpec);
                    }
                }
                item.put("newSpecs", newSpecs);

                List<ElemeOrderItemAttribute> elemeOrderItemAttributeList = elemeOrderItemAttributeMap.get(elemeOrderItem.getId());
                List<Map<String, String>> attributes = new ArrayList<Map<String, String>>();
                if (CollectionUtils.isNotEmpty(elemeOrderItemAttributeList)) {
                    for (ElemeOrderItemAttribute elemeOrderItemAttribute : elemeOrderItemAttributeList) {
                        Map<String, String> attribute = new HashMap<String, String>();
                        attribute.put("name", elemeOrderItemAttribute.getName());
                        attribute.put("value", elemeOrderItemAttribute.getValue());
                        attributes.add(attribute);
                    }
                }
                item.put("attributes", attributes);

                item.put("extendCode", elemeOrderItem.getExtendCode());
                item.put("barCode", elemeOrderItem.getBarCode());
                item.put("weight", elemeOrderItem.getWeight());
                item.put("userPrice", elemeOrderItem.getUserPrice());
                item.put("shopPrice", elemeOrderItem.getShopPrice());
                item.put("vfoodId", elemeOrderItem.getVfoodId());
                items.add(item);
            }
            elemeGroupMap.put("items", items);
            groups.add(elemeGroupMap);
        }
        Map<String, Object> elemeOrderMap = BeanUtils.beanToMap(elemeOrder);
        elemeOrderMap.put("groups", groups);
        List<Map<String, Object>> orderActivities = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(elemeActivities)) {
            for (ElemeOrderActivity elemeOrderActivity : elemeActivities) {
                Map<String, Object> elemeActivityMap = new HashMap<String, Object>();
                elemeActivityMap.put("id", elemeOrderActivity.getElemeActivityId());
                elemeActivityMap.put("name", elemeOrderActivity.getName());
                elemeActivityMap.put("categoryId", elemeOrderActivity.getCategoryId());
                elemeActivityMap.put("elemePart", elemeOrderActivity.getElemePart());
                elemeActivityMap.put("restaurantPart", elemeOrderActivity.getRestaurantPart());
                elemeActivityMap.put("amount", elemeOrderActivity.getAmount());
                orderActivities.add(elemeActivityMap);
            }
        }
        elemeOrderMap.put("orderActivities", orderActivities);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(elemeOrderMap);
        apiRest.setMessage("拉取饿了么订单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest doBindingStore(DoBindingStoreModel doBindingStoreModel) throws IOException {
        BigInteger tenantId = doBindingStoreModel.getTenantId();
        BigInteger branchId = doBindingStoreModel.getBranchId();
        BigInteger shopId = doBindingStoreModel.getShopId();
        BigInteger userId = doBindingStoreModel.getUserId();

        String lastUpdateRemark = "门店(" + branchId + ")绑定饿了么(" + shopId + ")，清除绑定关系！";
        UpdateModel updateModel = new UpdateModel(true);
        updateModel.setTableName("branch");
        updateModel.addContentValue("shop_id", null);
        updateModel.addContentValue("last_update_user_id", userId);
        updateModel.addContentValue("last_update_remark", lastUpdateRemark);
        updateModel.addSearchCondition("shop_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, shopId);
        universalMapper.universalUpdate(updateModel);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        branch.setShopId(doBindingStoreModel.getShopId());
        branchMapper.update(branch);

        Map<String, String> saveElemeBranchMappingRequestParameters = new HashMap<String, String>();
        saveElemeBranchMappingRequestParameters.put("tenantId", tenantId.toString());
        saveElemeBranchMappingRequestParameters.put("branchId", branchId.toString());
        saveElemeBranchMappingRequestParameters.put("shopId", shopId.toString());
        saveElemeBranchMappingRequestParameters.put("userId", userId.toString());

        ApiRest saveElemeBranchMappingApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "saveElemeBranchMapping", saveElemeBranchMappingRequestParameters);
        Validate.isTrue(saveElemeBranchMappingApiRest.isSuccessful(), saveElemeBranchMappingApiRest.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("饿了么门店绑定成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 发布饿了么订单消息
     *
     * @param tenantId：商户ID
     * @param branchId：门店ID
     * @param elemeOrderId：饿了么订单ID
     * @param type：消息类型
     * @param uuid：消息唯一ID
     * @throws IOException
     */
    private void publishElemeOrderMessage(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId, Integer type, String uuid) throws IOException {
        String elemeMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.ELEME_MESSAGE_CHANNEL_TOPIC);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("tenantId", tenantId);
        messageJsonObject.put("branchId", branchId);
        messageJsonObject.put("type", type);
        messageJsonObject.put("elemeOrderId", elemeOrderId);
        messageJsonObject.put("uuid", uuid);
        QueueUtils.convertAndSend(elemeMessageChannelTopic, messageJsonObject.toString());
    }

    /**
     * 批量查询订单
     *
     * @param batchGetOrdersModel
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public ApiRest batchGetOrders(BatchGetOrdersModel batchGetOrdersModel) throws Exception {
        BigInteger tenantId = batchGetOrdersModel.getTenantId();
        BigInteger branchId = batchGetOrdersModel.getBranchId();

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, batchGetOrdersModel.getElemeOrderIds());
        List<ElemeOrder> elemeOrders = elemeOrderMapper.findAll(searchModel);
        Validate.notEmpty(elemeOrders, "订单不存在！");

        List<String> orderIds = new ArrayList<String>();
        for (ElemeOrder elemeOrder : elemeOrders) {
            orderIds.add(elemeOrder.getOrderId());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderIds", orderIds);

        ApiRest callElemeSystemApiRest = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.mgetOrders", params);
        Validate.isTrue(callElemeSystemApiRest.isSuccessful(), callElemeSystemApiRest.getError());

        return new ApiRest(callElemeSystemApiRest.getData(), "批量查询订单成功！");
    }

    /**
     * 批量获取订单配送记录
     *
     * @param batchGetDeliveryStatesModel
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public ApiRest batchGetDeliveryStates(BatchGetDeliveryStatesModel batchGetDeliveryStatesModel) throws Exception {
        BigInteger tenantId = batchGetDeliveryStatesModel.getTenantId();
        BigInteger branchId = batchGetDeliveryStatesModel.getBranchId();

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, batchGetDeliveryStatesModel.getElemeOrderIds());
        List<ElemeOrder> elemeOrders = elemeOrderMapper.findAll(searchModel);
        Validate.notEmpty(elemeOrders, "订单不存在！");

        List<String> orderIds = new ArrayList<String>();
        for (ElemeOrder elemeOrder : elemeOrders) {
            orderIds.add(elemeOrder.getOrderId());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderIds", orderIds);

        ApiRest callElemeSystemApiRest = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.batchGetDeliveryStates", params);
        Validate.isTrue(callElemeSystemApiRest.isSuccessful(), callElemeSystemApiRest.getError());

        return new ApiRest(callElemeSystemApiRest.getData(), "批量获取订单配送记录成功！");
    }
}
