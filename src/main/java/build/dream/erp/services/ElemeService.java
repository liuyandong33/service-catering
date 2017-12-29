package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import build.dream.erp.models.eleme.DoBindingStoreModel;
import build.dream.erp.models.eleme.ObtainElemeDeliveryOrderStateChangeMessageModel;
import build.dream.erp.models.eleme.PullElemeOrderModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ElemeService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private ElemeOrderMapper elemeOrderMapper;
    @Autowired
    private ElemeGroupMapper elemeGroupMapper;
    @Autowired
    private ElemeOrderItemMapper elemeOrderItemMapper;
    @Autowired
    private ElemeOrderItemAttributeMapper elemeOrderItemAttributeMapper;
    @Autowired
    private ElemeOrderItemNewSpecMapper elemeOrderItemNewSpecMapper;
    @Autowired
    private ElemeActivityMapper elemeActivityMapper;
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
        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("shop_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, shopId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "shopId为" + shopId + "的门店不存在！");
        // 开始保存饿了么订单
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
        elemeOrder.setTenantId(branch.getTenantId());
        elemeOrder.setTenantCode(branch.getTenantCode());
        elemeOrder.setBranchId(branch.getId());
        elemeOrder.setBranchCode(branch.getCode());
        elemeOrder.setCreateUserId(userId);
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("饿了么系统推送新订单，保存订单！");
        elemeOrderMapper.insert(elemeOrder);

        List<ElemeOrderItem> elemeOrderItems = new ArrayList<ElemeOrderItem>();

        int elemeGroupJsonArraySize = elemeGroupJsonArray.size();
        for (int index = 0; index < elemeGroupJsonArraySize; index++) {
            JSONObject elemeGroupJsonObject = elemeGroupJsonArray.getJSONObject(index);
            ElemeGroup elemeGroup = new ElemeGroup();
            elemeGroup.setElemeOrderId(elemeOrder.getId());
            elemeGroup.setName(elemeGroupJsonObject.optString("name"));
            elemeGroup.setType(elemeGroupJsonObject.optString("type"));
            elemeGroup.setCreateUserId(userId);
            elemeGroup.setLastUpdateUserId(userId);
            elemeGroup.setLastUpdateRemark("饿了么系统推送新订单，保存分组");
            elemeGroupMapper.insert(elemeGroup);
            JSONArray elemeOrderItemJsonArray = elemeGroupJsonObject.optJSONArray("items");
            int elemeOrderItemJsonArraySize = elemeOrderItemJsonArray.size();
            for (int elemeOrderItemJsonArrayIndex = 0; elemeOrderItemJsonArrayIndex < elemeOrderItemJsonArraySize; elemeOrderItemJsonArrayIndex++) {
                JSONObject elemeOrderItemJsonObject = elemeOrderItemJsonArray.optJSONObject(elemeOrderItemJsonArrayIndex);
                ElemeOrderItem elemeOrderItem = new ElemeOrderItem();
                elemeOrderItem.setElemeGroupId(elemeGroup.getId());
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

                Object weight = elemeOrderItemJsonObject.opt("weight");
                if (weight != null) {
                    elemeOrderItem.setWeight(BigDecimal.valueOf(Double.valueOf(weight.toString())));
                }
                elemeOrderItem.setCreateUserId(userId);
                elemeOrderItem.setLastUpdateUserId(userId);
                elemeOrderItem.setLastUpdateRemark("饿了么系统推送新订单，保存菜品属性！");
                elemeOrderItemMapper.insert(elemeOrderItem);

                JSONArray elemeOrderItemAttributeJsonArray = elemeOrderItemJsonObject.optJSONArray("attributes");
                if (elemeOrderItemAttributeJsonArray != null) {
                    int elemeOrderItemAttributeJsonArraySize = elemeOrderItemAttributeJsonArray.size();
                    for (int elemeOrderItemAttributeJsonArrayIndex = 0; elemeOrderItemAttributeJsonArrayIndex < elemeOrderItemAttributeJsonArraySize; elemeOrderItemAttributeJsonArrayIndex++) {
                        JSONObject elemeOrderItemAttributeJsonObject = elemeOrderItemAttributeJsonArray.optJSONObject(index);
                        ElemeOrderItemAttribute elemeOrderItemAttribute = new ElemeOrderItemAttribute();
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
                ElemeActivity elemeActivity = new ElemeActivity();
                elemeActivity.setElemeOrderId(elemeOrder.getId());
                elemeActivity.setElemeActivityId(BigInteger.valueOf(elemeActivityJsonObject.optLong("id")));
                elemeActivity.setName(elemeActivityJsonObject.optString("name"));
                elemeActivity.setCategoryId(elemeActivityJsonObject.optInt("categoryId"));
                elemeActivity.setElemePart(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("elemePart")));
                elemeActivity.setRestaurantPart(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("restaurantPart")));
                elemeActivity.setAmount(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("amount")));
                elemeActivity.setCreateUserId(userId);
                elemeActivity.setLastUpdateUserId(userId);
                elemeActivity.setLastUpdateRemark("饿了么系统推送新订单，保存饿了么订单活动！");
                elemeActivityMapper.insert(elemeActivity);
            }
        }
        CacheUtils.hset(elemeOrder.getOrderId(), type.toString(), uuid);
        CacheUtils.expire(elemeOrder.getOrderId(), 48, TimeUnit.HOURS);
        publishElemeOrderMessage(branch.getTenantCode(), branch.getCode(), elemeOrder.getId(), type, uuid);
    }

    /**
     * 处理饿了么退单消息
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

        ElemeRefundOrderMessage elemeRefundOrderMessage = new ElemeRefundOrderMessage();
        elemeRefundOrderMessage.setElemeOrderId(elemeOrder.getId());
        elemeRefundOrderMessage.setOrderId(elemeOrder.getOrderId());
        elemeRefundOrderMessage.setRefundStatus(messageJsonObject.optString("refundStatus"));
        elemeRefundOrderMessage.setReason(messageJsonObject.optString("reason"));
        elemeRefundOrderMessage.setShopId(shopId);
        elemeRefundOrderMessage.setRefundType(messageJsonObject.optString("refundType"));
        elemeRefundOrderMessage.setTotalPrice(BigDecimal.valueOf(messageJsonObject.optDouble("totalPrice")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageJsonObject.getLong("updateTime") * 1000);
        elemeRefundOrderMessage.setUpdateTime(calendar.getTime());
        elemeRefundOrderMessage.setTenantId(elemeOrder.getTenantId());
        elemeRefundOrderMessage.setBranchId(elemeOrder.getBranchId());
        elemeRefundOrderMessage.setCreateUserId(userId);
        elemeRefundOrderMessage.setLastUpdateUserId(userId);
        elemeRefundOrderMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么退单信息！");
        elemeRefundOrderMessageMapper.insert(elemeRefundOrderMessage);

        JSONArray goodsList = messageJsonObject.getJSONArray("goodsList");
        int size = goodsList.size();
        for (int index = 0; index < size; index++) {
            JSONObject itemJsonObject = goodsList.getJSONObject(index);
            ElemeRefundOrderMessageGoodsItem elemeRefundOrderMessageGoodsItem = new ElemeRefundOrderMessageGoodsItem();
            elemeRefundOrderMessageGoodsItem.setElemeRefundOrderMessageId(elemeRefundOrderMessage.getId());
            elemeRefundOrderMessageGoodsItem.setName(itemJsonObject.optString("name"));
            elemeRefundOrderMessageGoodsItem.setQuantity(itemJsonObject.optInt("quantity"));
            elemeRefundOrderMessageGoodsItem.setPrice(BigDecimal.valueOf(itemJsonObject.optDouble("price")));
            elemeRefundOrderMessageGoodsItemMapper.insert(elemeRefundOrderMessageGoodsItem);
        }

        if (type == 20 || type == 21 || type == 24 || type == 25 || type == 26 || type == 30 || type == 31 || type == 34 || type == 35 || type == 36) {
            publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type, uuid);
        }
    }

    /**
     * 处理饿了么催单消息
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
            publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type, uuid);
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
        publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type, uuid);
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
        publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type, uuid);
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
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullElemeOrderModel.getTenantId());
        elemeOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullElemeOrderModel.getBranchId());
        elemeOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullElemeOrderModel.getElemeOrderId());
        ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "订单不存在！");

        SearchModel elemeGroupSearchModel = new SearchModel(true);
        elemeGroupSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullElemeOrderModel.getElemeOrderId());
        List<ElemeGroup> elemeGroups = elemeGroupMapper.findAll(elemeGroupSearchModel);

        List<BigInteger> elemeGroupIds = new ArrayList<BigInteger>();
        for (ElemeGroup elemeGroup : elemeGroups) {
            elemeGroupIds.add(elemeGroup.getId());
        }
        SearchModel elemeOrderItemSearchModel = new SearchModel(true);
        elemeOrderItemSearchModel.addSearchCondition("eleme_group_id", Constants.SQL_OPERATION_SYMBOL_IN, elemeGroupIds);
        List<ElemeOrderItem> elemeOrderItems = elemeOrderItemMapper.findAll(elemeOrderItemSearchModel);

        List<BigInteger> elemeOrderItemIds = new ArrayList<BigInteger>();
        Map<BigInteger, List<ElemeOrderItem>> elemeOrderItemMap = new HashMap<BigInteger, List<ElemeOrderItem>>();
        for (ElemeOrderItem elemeOrderItem : elemeOrderItems) {
            elemeOrderItemIds.add(elemeOrderItem.getId());
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeOrderItem.getElemeGroupId());
            if (elemeOrderItemList == null) {
                elemeOrderItemList = new ArrayList<ElemeOrderItem>();
                elemeOrderItemMap.put(elemeOrderItem.getElemeGroupId(), elemeOrderItemList);
            }
            elemeOrderItemList.add(elemeOrderItem);
        }

        SearchModel elemeOrderItemNewSpecSearchModel = new SearchModel(true);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("eleme_order_item_id", Constants.SQL_OPERATION_SYMBOL_IN, elemeOrderItemIds);
        List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecs = elemeOrderItemNewSpecMapper.findAll(elemeOrderItemNewSpecSearchModel);
        Map<BigInteger, List<ElemeOrderItemNewSpec>> elemeOrderItemNewSpecMap = new HashMap<BigInteger, List<ElemeOrderItemNewSpec>>();
        for (ElemeOrderItemNewSpec elemeOrderItemNewSpec : elemeOrderItemNewSpecs) {
            List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecList = elemeOrderItemNewSpecMap.get(elemeOrderItemNewSpec.getElemeOrderItemId());
            if (elemeOrderItemNewSpecList == null) {
                elemeOrderItemNewSpecList = new ArrayList<ElemeOrderItemNewSpec>();
                elemeOrderItemNewSpecMap.put(elemeOrderItemNewSpec.getElemeOrderItemId(), elemeOrderItemNewSpecList);
            }
            elemeOrderItemNewSpecList.add(elemeOrderItemNewSpec);
        }

        SearchModel elemeOrderItemAttributeSearchModel = new SearchModel(true);
        elemeOrderItemAttributeSearchModel.addSearchCondition("eleme_order_item_id", Constants.SQL_OPERATION_SYMBOL_IN, elemeOrderItemIds);
        List<ElemeOrderItemAttribute> elemeOrderItemAttributes = elemeOrderItemAttributeMapper.findAll(elemeOrderItemAttributeSearchModel);
        Map<BigInteger, List<ElemeOrderItemAttribute>> elemeOrderItemAttributeMap = new HashMap<BigInteger, List<ElemeOrderItemAttribute>>();
        for (ElemeOrderItemAttribute elemeOrderItemAttribute : elemeOrderItemAttributes) {
            List<ElemeOrderItemAttribute> elemeOrderItemAttributeList = elemeOrderItemAttributeMap.get(elemeOrderItemAttribute.getElemeOrderItemId());
            if (elemeOrderItemAttributeList == null) {
                elemeOrderItemAttributeList = new ArrayList<ElemeOrderItemAttribute>();
                elemeOrderItemAttributeMap.put(elemeOrderItemAttribute.getElemeOrderItemId(), elemeOrderItemAttributeList);
            }
            elemeOrderItemAttributeList.add(elemeOrderItemAttribute);
        }

        SearchModel elemeActivitySearchModel = new SearchModel(true);
        elemeActivitySearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, elemeOrder.getId());
        List<ElemeActivity> elemeActivities = elemeActivityMapper.findAll(elemeActivitySearchModel);

        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (ElemeGroup elemeGroup : elemeGroups) {
            Map<String, Object> elemeGroupMap = new HashMap<String, Object>();
            elemeGroupMap.put("name", elemeGroup.getName());
            elemeGroupMap.put("type", elemeGroup.getType());
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeGroup.getId());
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
            for (ElemeActivity elemeActivity : elemeActivities) {
                Map<String, Object> elemeActivityMap = new HashMap<String, Object>();
                elemeActivityMap.put("id", elemeActivity.getElemeActivityId());
                elemeActivityMap.put("name", elemeActivity.getName());
                elemeActivityMap.put("categoryId", elemeActivity.getCategoryId());
                elemeActivityMap.put("elemePart", elemeActivity.getElemePart());
                elemeActivityMap.put("restaurantPart", elemeActivity.getRestaurantPart());
                elemeActivityMap.put("amount", elemeActivity.getAmount());
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
        String lastUpdateRemark = "门店(" + doBindingStoreModel.getBranchId() + ")绑定饿了么(" + doBindingStoreModel.getShopId() + ")，清除绑定关系！";
        branchMapper.clearBindingStore(doBindingStoreModel.getShopId(), doBindingStoreModel.getUserId(), lastUpdateRemark);
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, doBindingStoreModel.getBranchId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, doBindingStoreModel.getTenantId());
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        branch.setShopId(doBindingStoreModel.getShopId());
        branchMapper.update(branch);

        Map<String, String> saveElemeBranchMappingRequestParameters = new HashMap<String, String>();
        saveElemeBranchMappingRequestParameters.put("tenantId", doBindingStoreModel.getTenantId().toString());
        saveElemeBranchMappingRequestParameters.put("branchId", doBindingStoreModel.getBranchId().toString());
        saveElemeBranchMappingRequestParameters.put("shopId", doBindingStoreModel.getShopId().toString());
        saveElemeBranchMappingRequestParameters.put("userId", doBindingStoreModel.getUserId().toString());

        ApiRest saveElemeBranchMappingApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "saveElemeBranchMapping", saveElemeBranchMappingRequestParameters);
        Validate.isTrue(saveElemeBranchMappingApiRest.isSuccessful(), saveElemeBranchMappingApiRest.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("饿了么门店绑定成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 发布饿了么订单消息
     * @param tenantCode：商户编码
     * @param branchCode：门店编码
     * @param elemeOrderId：订单ID
     * @param type：消息类型
     * @return
     */
    private void publishElemeOrderMessage(String tenantCode, String branchCode, BigInteger elemeOrderId, Integer type, String uuid) throws IOException {
        String elemeMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.ELEME_MESSAGE_CHANNEL_TOPIC);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("tenantCode", tenantCode);
        messageJsonObject.put("branchCode", branchCode);
        messageJsonObject.put("type", type);
        messageJsonObject.put("elemeOrderId", elemeOrderId);
        messageJsonObject.put("uuid", uuid);
        QueueUtils.convertAndSend(elemeMessageChannelTopic, messageJsonObject.toString());
    }
}
