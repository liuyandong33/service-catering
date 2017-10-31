package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.*;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
    private ElemeItemMapper elemeItemMapper;
    @Autowired
    private ElemeItemAttributeMapper elemeItemAttributeMapper;
    @Autowired
    private ElemeItemNewSpecMapper elemeItemNewSpecMapper;
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

    @Transactional(readOnly = true)
    public ApiRest tenantAuthorize(BigInteger tenantId, BigInteger branchId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", "=", tenantId);
        searchModel.addSearchCondition("id", "=", branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        Map<String, String> checkIsAuthorizeRequestParameters = new HashMap<String, String>();
        checkIsAuthorizeRequestParameters.put("tenantId", tenantId.toString());
        checkIsAuthorizeRequestParameters.put("branchId", branchId.toString());
        checkIsAuthorizeRequestParameters.put("branchType", branch.getType().toString());
        String checkIsAuthorizeResult = ProxyUtils.doGetOriginalWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "checkIsAuthorize", checkIsAuthorizeRequestParameters);
        ApiRest checkIsAuthorizeApiRest = ApiRest.fromJson(checkIsAuthorizeResult);
        Validate.isTrue(checkIsAuthorizeApiRest.isSuccessful(), checkIsAuthorizeApiRest.getError());
        Map<String, Object> checkIsAuthorizeApiRestData = (Map<String, Object>) checkIsAuthorizeApiRest.getData();
        boolean isAuthorize = (boolean) checkIsAuthorizeApiRestData.get("isAuthorize");

        String data = null;
        if (isAuthorize) {
            String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
            data = SystemPartitionUtils.getOutsideServiceDomain(serviceName) + "/eleme/bindingRestaurant?tenantId=" + tenantId + "&branchId=" + branchId;
        } else {
            String elemeUrl = ConfigurationUtils.getConfiguration(Constants.ELEME_SERVICE_URL);
            String elemeAppKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);
            String tenantType = checkIsAuthorizeApiRestData.get("tenantType").toString();

            String outServiceOutsideServiceDomain = SystemPartitionUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT);
            data = String.format(Constants.ELEME_TENANT_AUTHORIZE_URL_FORMAT, elemeUrl + "/" + "authorize", "code", elemeAppKey, URLEncoder.encode(outServiceOutsideServiceDomain + "/eleme/tenantAuthorizeCallback", Constants.CHARSET_UTF_8), tenantType + "Z" + tenantId + "Z" + branch.getType() + "Z" + branchId, "all");
        }
        ApiRest apiRest = new ApiRest(data, "生成授权链接成功！");
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveElemeOrder(BigInteger shopId, String message, Integer type) throws IOException {
        ApiRest apiRest = null;
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Boolean returnValue = CacheUtils.setnx(key, key);
            if (!returnValue) {
                apiRest = new ApiRest();
                apiRest.setMessage("保存订单成功！");
                apiRest.setSuccessful(true);
            } else {
                CacheUtils.expire(key, 30 * 60, TimeUnit.SECONDS);
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

                List<ElemeItem> elemeItems = new ArrayList<ElemeItem>();

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
                    JSONArray elemeItemJsonArray = elemeGroupJsonObject.optJSONArray("items");
                    int elemeItemJsonArraySize = elemeItemJsonArray.size();
                    for (int elemeItemJsonArrayIndex = 0; elemeItemJsonArrayIndex < elemeItemJsonArraySize; elemeItemJsonArrayIndex++) {
                        JSONObject elemeItemJsonObject = elemeItemJsonArray.optJSONObject(elemeItemJsonArrayIndex);
                        ElemeItem elemeItem = new ElemeItem();
                        elemeItem.setElemeGroupId(elemeGroup.getId());
                        elemeItem.setElemeItemId(BigInteger.valueOf(elemeItemJsonObject.getLong("id")));
                        elemeItem.setSkuId(BigInteger.valueOf(elemeItemJsonObject.getLong("skuId")));
                        elemeItem.setName(elemeItemJsonObject.getString("name"));
                        elemeItem.setCategoryId(BigInteger.valueOf(elemeItemJsonObject.getLong("categoryId")));
                        elemeItem.setPrice(BigDecimal.valueOf(elemeItemJsonObject.optDouble("price")));
                        elemeItem.setQuantity(elemeItemJsonObject.optInt("quantity"));
                        elemeItem.setTotal(BigDecimal.valueOf(elemeItemJsonObject.optDouble("total")));
                        elemeItem.setExtendCode(elemeItemJsonObject.optString("extendCode"));
                        elemeItem.setBarCode(elemeItemJsonObject.optString("barCode"));
                        elemeItem.setUserPrice(BigDecimal.valueOf(elemeItemJsonObject.getDouble("userPrice")));
                        elemeItem.setShopPrice(BigDecimal.valueOf(elemeItemJsonObject.getDouble("shopPrice")));
                        elemeItem.setVfoodId(BigInteger.valueOf(elemeItemJsonObject.getLong("vfoodId")));

                        Object weight = elemeItemJsonObject.opt("weight");
                        if (weight != null) {
                            elemeItem.setWeight(BigDecimal.valueOf(Double.valueOf(weight.toString())));
                        }
                        elemeItem.setCreateUserId(userId);
                        elemeItem.setLastUpdateUserId(userId);
                        elemeItem.setLastUpdateRemark("饿了么系统推送新订单，保存菜品属性！");
                        elemeItemMapper.insert(elemeItem);

                        JSONArray elemeItemAttributeJsonArray = elemeItemJsonObject.optJSONArray("attributes");
                        if (elemeItemAttributeJsonArray != null) {
                            int elemeItemAttributeJsonArraySize = elemeItemAttributeJsonArray.size();
                            for (int elemeItemAttributeJsonArrayIndex = 0; elemeItemAttributeJsonArrayIndex < elemeItemAttributeJsonArraySize; elemeItemAttributeJsonArrayIndex++) {
                                JSONObject elemeItemAttributeJsonObject = elemeItemAttributeJsonArray.optJSONObject(index);
                                ElemeItemAttribute elemeItemAttribute = new ElemeItemAttribute();
                                elemeItemAttribute.setElemeItemId(elemeItem.getId());
                                elemeItemAttribute.setName(elemeItemAttributeJsonObject.optString("name"));
                                elemeItemAttribute.setValue(elemeItemAttributeJsonObject.optString("value"));
                                elemeItemAttribute.setCreateUserId(userId);
                                elemeItemAttribute.setLastUpdateUserId(userId);
                                elemeItemAttribute.setLastUpdateRemark("饿了么系统推送新订单，保存菜品属性！");
                                elemeItemAttributeMapper.insert(elemeItemAttribute);
                            }
                        }

                        JSONArray elemeItemNewSpecJsonArray = elemeItemJsonObject.optJSONArray("newSpecs");
                        if (elemeItemNewSpecJsonArray != null) {
                            int elemeItemNewSpecJsonArraySize = elemeItemNewSpecJsonArray.size();
                            for (int elemeItemNewSpecJsonArrayIndex = 0; elemeItemNewSpecJsonArrayIndex < elemeItemNewSpecJsonArraySize; elemeItemNewSpecJsonArrayIndex++) {
                                JSONObject elemeItemNewSpecJsonObject = elemeItemNewSpecJsonArray.optJSONObject(index);
                                ElemeItemNewSpec elemeItemNewSpec = new ElemeItemNewSpec();
                                elemeItemNewSpec.setElemeItemId(elemeItem.getId());
                                elemeItemNewSpec.setName(elemeItemNewSpecJsonObject.optString("name"));
                                elemeItemNewSpec.setValue(elemeItemNewSpecJsonObject.optString("value"));
                                elemeItemNewSpec.setCreateUserId(userId);
                                elemeItemNewSpec.setLastUpdateUserId(userId);
                                elemeItemNewSpec.setLastUpdateRemark("饿了么系统推送新订单，保存菜品规格！");
                                elemeItemNewSpecMapper.insert(elemeItemNewSpec);
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
                        elemeActivity.setMeaning(elemeActivityCategoryIdMeaningMap.get(elemeActivity.getCategoryId()));
                        elemeActivity.setAmount(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("amount")));
                        elemeActivity.setCreateUserId(userId);
                        elemeActivity.setLastUpdateUserId(userId);
                        elemeActivity.setLastUpdateRemark("饿了么系统推送新订单，保存饿了么订单活动！");
                        elemeActivityMapper.insert(elemeActivity);
                    }
                }
                publishElemeOrderMessage(branch.getTenantCode(), branch.getCode(), elemeOrder.getId(), type);

                apiRest = new ApiRest();
                apiRest.setMessage("保存订单成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.delete(key);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理饿了么退单消息
     * @param shopId：店铺ID
     * @param message：消息内容
     * @param type：消息类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleElemeRefundOrderMessage(BigInteger shopId, String message, Integer type) throws IOException {
        ApiRest apiRest = null;
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Boolean returnValue = CacheUtils.setnx(key, key);
            if (returnValue) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理退单消息成功！");
                apiRest.setSuccessful(true);
            } else {
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
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messageJsonObject.getLong("updateTime") * 1000);
                elemeRefundOrderMessage.setUpdateTime(calendar.getTime());
                elemeRefundOrderMessage.setTenantId(elemeOrder.getTenantId());
                elemeRefundOrderMessage.setBranchId(elemeOrder.getBranchId());
                elemeRefundOrderMessage.setCreateUserId(userId);
                elemeRefundOrderMessage.setLastUpdateUserId(userId);
                elemeRefundOrderMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么退单信息！");
                elemeRefundOrderMessageMapper.insert(elemeRefundOrderMessage);

                if (type == 20 || type == 21 || type == 24 || type == 25 || type == 26 || type == 30 || type == 31 || type == 34 || type == 35 || type == 36) {
                    publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type);
                }

                apiRest = new ApiRest();
                apiRest.setMessage("处理退单消息成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.delete(key);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理饿了么催单消息
     * @param shopId：饿了么店铺ID
     * @param message：消息内容
     * @param type：消息类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleElemeReminderMessage(BigInteger shopId, String message, Integer type) throws IOException {
        ApiRest apiRest = null;
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Boolean returnValue = CacheUtils.setnx(key, key);
            if (returnValue) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理催单消息成功！");
                apiRest.setSuccessful(true);
            } else {
                SearchModel elemeOrderSearchModel = new SearchModel(true);
                elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
                ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
                Validate.notNull(elemeOrder, "饿了么订单不存在！");

                ElemeReminderMessage elemeReminderMessage = new ElemeReminderMessage();
                elemeReminderMessage.setElemeOrderId(elemeOrder.getId());
                elemeReminderMessage.setOrderId(orderId);
                elemeReminderMessage.setElemeReminderId(BigInteger.valueOf(messageJsonObject.optLong("remindId")));
                elemeReminderMessage.setUserId(BigInteger.valueOf(messageJsonObject.getLong("userId")));
                elemeReminderMessage.setShopId(shopId);
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
                    publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type);
                }

                apiRest = new ApiRest();
                apiRest.setMessage("处理催单消息成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.delete(key);
            throw e;
        }
        return apiRest;
    }

    public ApiRest handleElemeOrderStateChangeMessage(BigInteger shopId, String message, Integer type) throws IOException {
        ApiRest apiRest = null;
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Boolean returnValue = CacheUtils.setnx(key, key);
            if (returnValue) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理订单状态变更消息成功！");
                apiRest.setSuccessful(true);
            } else {
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
                elemeOrderStateChangeMessage.setRole(messageJsonObject.getInt("role"));
                elemeOrderStateChangeMessage.setShopId(BigInteger.valueOf(messageJsonObject.getLong("shopId")));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messageJsonObject.getLong("updateTime"));
                elemeOrderStateChangeMessage.setUpdateTime(calendar.getTime());

                elemeOrderStateChangeMessage.setCreateUserId(userId);
                elemeOrderStateChangeMessage.setLastUpdateUserId(userId);
                elemeOrderStateChangeMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么订单变更消息！");

                elemeOrderStateChangeMessageMapper.insert(elemeOrderStateChangeMessage);
                publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type);

                apiRest = new ApiRest();
                apiRest.setMessage("处理订单状态变更消息成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.delete(key);
            throw e;
        }
        return apiRest;
    }

    public ApiRest handleElemeDeliveryOrderStateChangeMessage(BigInteger shopId, String message, Integer type) throws IOException {
        ApiRest apiRest = null;
        JSONObject messageJsonObject = JSONObject.fromObject(message);
        String orderId = messageJsonObject.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Boolean returnValue = CacheUtils.setnx(key, key);
            if (returnValue) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理订单状态变更消息成功！");
                apiRest.setSuccessful(true);
            } else {
                SearchModel elemeOrderSearchModel = new SearchModel(true);
                elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
                ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
                Validate.notNull(elemeOrder, "饿了么订单不存在！");

                BigInteger userId = CommonUtils.getServiceSystemUserId();
                elemeOrder.setLastUpdateUserId(userId);
                elemeOrder.setLastUpdateRemark("处理饿了么订单状态变更消息，修改订单状态！");
                elemeOrderMapper.update(elemeOrder);

                ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage = new ElemeDeliveryOrderStateChangeMessage();
                elemeDeliveryOrderStateChangeMessage.setElemeOrderId(elemeOrder.getId());
                elemeDeliveryOrderStateChangeMessage.setOrderId(elemeOrder.getOrderId());
                elemeDeliveryOrderStateChangeMessage.setState(messageJsonObject.optString("state"));
                elemeDeliveryOrderStateChangeMessage.setSubState(messageJsonObject.optString("subState"));
                elemeDeliveryOrderStateChangeMessage.setName(messageJsonObject.optString("name"));
                elemeDeliveryOrderStateChangeMessage.setPhone(messageJsonObject.optString("phone"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messageJsonObject.getLong("updateAt"));
                elemeDeliveryOrderStateChangeMessage.setUpdateTime(calendar.getTime());
                elemeDeliveryOrderStateChangeMessage.setCreateUserId(userId);
                elemeDeliveryOrderStateChangeMessage.setLastUpdateUserId(userId);
                elemeDeliveryOrderStateChangeMessage.setLastUpdateRemark("处理饿了么回调，保存饿了么运单状态变更消息！");

                elemeDeliveryOrderStateChangeMessageMapper.insert(elemeDeliveryOrderStateChangeMessage);
                publishElemeOrderMessage(elemeOrder.getTenantCode(), elemeOrder.getBranchCode(), elemeOrder.getId(), type);

                apiRest = new ApiRest();
                apiRest.setMessage("处理订单状态变更消息成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.delete(key);
            throw e;
        }
        return apiRest;
    }

    public ApiRest handleElemeShopStateChangeMessage(BigInteger shopId, String message, Integer type) {
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("处理成功");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    public ApiRest handleAuthorizationStateChangeMessage(BigInteger shopId, String message, Integer type) {
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public Branch findBranchInfo(BigInteger tenantId, BigInteger branchId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_IN, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        return branch;
    }

    @Transactional(readOnly = true)
    public GoodsCategory findGoodsCategoryInfo(BigInteger tenantId, BigInteger branchId, BigInteger categoryId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_IN, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_IN, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, categoryId);
        GoodsCategory goodsCategory = goodsCategoryMapper.find(searchModel);
        Validate.notNull(goodsCategory, "分类信息不存在！");
        return goodsCategory;
    }

    public ElemeOrder findElemeOrderInfo(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_IN, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_IN, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, elemeOrderId);
        ElemeOrder elemeOrder = elemeOrderMapper.find(searchModel);
        Validate.notNull(elemeOrder, "订单不存在！");
        return elemeOrder;
    }

    /**
     * 发布饿了么订单消息
     * @param tenantCode：商户编码
     * @param branchCode：门店编码
     * @param elemeOrderId：订单ID
     * @param type：消息类型
     * @return
     */
    private void publishElemeOrderMessage(String tenantCode, String branchCode, BigInteger elemeOrderId, Integer type) throws IOException {
        String elemeOrderMessageChannel = ConfigurationUtils.getConfiguration(Constants.ELEME_ORDER_MESSAGE_CHANNEL);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("tenantCodeAndBranchCode", tenantCode + "_" + branchCode);
        messageJsonObject.put("type", type);
        messageJsonObject.put("elemeOrderId", elemeOrderId);
        QueueUtils.convertAndSend(elemeOrderMessageChannel, messageJsonObject.toString());
    }

    private static Map<Integer, String> elemeActivityCategoryIdMeaningMap = null;
    static {
        elemeActivityCategoryIdMeaningMap = new HashMap<Integer, String>();
        elemeActivityCategoryIdMeaningMap.put(11, "食物活动");
        elemeActivityCategoryIdMeaningMap.put(12, "餐厅活动");
        elemeActivityCategoryIdMeaningMap.put(15, "商家代金券抵扣");
        elemeActivityCategoryIdMeaningMap.put(200, "限时抢购");
    }
}
