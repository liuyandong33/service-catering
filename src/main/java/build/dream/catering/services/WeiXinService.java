package build.dream.catering.services;

import build.dream.catering.constants.ConfigurationKeys;
import build.dream.catering.constants.Constants;
import build.dream.catering.models.weixin.*;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.WeiXinMemberCard;
import build.dream.common.domains.catering.WeiXinMenu;
import build.dream.common.domains.saas.WeiXinAuthorizerInfo;
import build.dream.common.domains.saas.WeiXinAuthorizerToken;
import build.dream.common.models.weixin.CreateMenuModel;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class WeiXinService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest createMemberCard(CreateMemberCardModel createMemberCardModel, MultipartFile backgroundPicFile, MultipartFile logoFile) {
        Long tenantId = createMemberCardModel.obtainTenantId();
        Long userId = createMemberCardModel.obtainUserId();
        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未配置微信公众号，不能创建会员卡！");

        String authorizerAppId = weiXinAuthorizerInfo.getAuthorizerAppId();
        String componentAppId = weiXinAuthorizerInfo.getComponentAppId();
        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, authorizerAppId);
        String accessToken = weiXinAuthorizerToken.getAuthorizerAccessToken();

        String uploadImgUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg";
        String backgroundPicUrl = null;
        if (backgroundPicFile != null) {
            Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
            uploadBackgroundPicRequestParameters.put("buffer", backgroundPicFile);
            uploadBackgroundPicRequestParameters.put("access_token", accessToken);

            String uploadBackgroundPicResult = OutUtils.doPostWithMultipartForm(uploadImgUrl, uploadBackgroundPicRequestParameters);
            Map<String, Object> uploadBackgroundPicResultMap = JacksonUtils.readValueAsMap(uploadBackgroundPicResult, String.class, Object.class);
            ValidateUtils.isTrue(!uploadBackgroundPicResultMap.containsKey("errcode"), MapUtils.getString(uploadBackgroundPicResultMap, "errmsg"));

            backgroundPicUrl = MapUtils.getString(uploadBackgroundPicResultMap, "url");
        }

        Map<String, Object> uploadLogoRequestParameters = new HashMap<String, Object>();
        uploadLogoRequestParameters.put("buffer", logoFile);
        uploadLogoRequestParameters.put("access_token", accessToken);

        String uploadLogoResult = OutUtils.doPostWithMultipartForm(uploadImgUrl, uploadLogoRequestParameters);
        Map<String, Object> uploadLogoResultMap = JacksonUtils.readValueAsMap(uploadLogoResult, String.class, Object.class);
        ValidateUtils.isTrue(!uploadLogoResultMap.containsKey("errcode"), MapUtils.getString(uploadLogoResultMap, "errmsg"));

        String logoUrl = MapUtils.getString(uploadLogoResultMap, "url");

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("logo_url", logoUrl);
        baseInfo.put("code_type", "CODE_TYPE_QRCODE");
        baseInfo.put("brand_name", createMemberCardModel.getBrandName());
        baseInfo.put("title", createMemberCardModel.getTitle());
        baseInfo.put("color", createMemberCardModel.getColor());
        baseInfo.put("notice", createMemberCardModel.getNotice());
        baseInfo.put("description", createMemberCardModel.getDescription());
        Map<String, Object> sku = new HashMap<String, Object>();
        sku.put("quantity", 100000000);
        baseInfo.put("sku", sku);

        Map<String, Object> dateInfo = new HashMap<String, Object>();
        dateInfo.put("type", "DATE_TYPE_PERMANENT");
        baseInfo.put("date_info", dateInfo);

        String servicePhone = createMemberCardModel.getServicePhone();
        if (StringUtils.isNotBlank(servicePhone)) {
            baseInfo.put("service_phone", servicePhone);
        }

        baseInfo.put("custom_url_name", "立即使用");
        baseInfo.put("custom_url", "http://weixin.qq.com");
        baseInfo.put("custom_url_sub_title", "6个汉字tips");
        baseInfo.put("promotion_url_name", "营销入口1");
        baseInfo.put("promotion_url", "http://weixin.qq.com");
        baseInfo.put("get_limit", 1);
        baseInfo.put("can_give_friend", false);

        Map<String, Object> memberCard = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(backgroundPicUrl)) {
            memberCard.put("background_pic_url", backgroundPicUrl);
        }
        memberCard.put("base_info", baseInfo);
        memberCard.put("prerogative", "prerogative");
        memberCard.put("auto_activate", false);
        memberCard.put("wx_activate", true);
        memberCard.put("wx_activate_after_submit", true);
        memberCard.put("wx_activate_after_submit_url", "http://www.baidu.com");
        memberCard.put("supply_bonus", true);
        memberCard.put("supply_balance", false);

        Map<String, Object> customField1 = new HashMap<String, Object>();
        customField1.put("name_type", "FIELD_NAME_TYPE_LEVEL");
        customField1.put("url", "http://www.qq.com");
        memberCard.put("custom_field1", customField1);

        Map<String, Object> customCell1 = new HashMap<String, Object>();
        customCell1.put("name", "使用入口2");
        customCell1.put("tips", "激活后显示");
        customCell1.put("url", "http://www.xxx.com");
        memberCard.put("custom_cell1", customCell1);

        Map<String, Object> bonusRule = new HashMap<String, Object>();
        bonusRule.put("cost_money_unit", 100);
        bonusRule.put("increase_bonus", 1);
        bonusRule.put("max_increase_bonus", 200);
        bonusRule.put("init_increase_bonus", 10);
        bonusRule.put("cost_bonus_unit", 5);
        bonusRule.put("reduce_money", 100);
        bonusRule.put("least_money_to_use_bonus", 1000);
        bonusRule.put("max_reduce_bonus", 50);
        memberCard.put("bonus_rule", bonusRule);

        memberCard.put("discount", 10);

        Map<String, Object> useCondition = new HashMap<String, Object>();
        useCondition.put("accept_category", "鞋类");
        useCondition.put("reject_category", "阿迪达斯");
        useCondition.put("can_use_with_other_discount", true);

        Map<String, Object> advancedInfo = new HashMap<String, Object>();
        advancedInfo.put("use_condition", useCondition);

        Map<String, Object> abstractInfo = new HashMap<String, Object>();
        abstractInfo.put("abstract", "微信餐厅推出多种新季菜品，期待您的光临");
        List<String> iconUrlList = new ArrayList<String>();
        iconUrlList.add("http://mmbiz.qpic.cn/mmbiz/p98FjXy8LacgHxp3sJ3vn97bGLz0ib0Sfz1bjiaoOYA027iasqSG0sjpiby4vce3AtaPu6cIhBHkt6IjlkY9YnDsfw/0");
        abstractInfo.put("icon_url_list", iconUrlList);
        advancedInfo.put("abstract", abstractInfo);

        Map<String, Object> textImage1 = new HashMap<String, Object>();
        textImage1.put("image_url", "http://mmbiz.qpic.cn/mmbiz/p98FjXy8LacgHxp3sJ3vn97bGLz0ib0Sfz1bjiaoOYA027iasqSG0sjpiby4vce3AtaPu6cIhBHkt6IjlkY9YnDsfw/0");
        textImage1.put("text", "此菜品精选食材，以独特的烹饪方法，最大程度地刺激食客的味蕾");

        Map<String, Object> textImage2 = new HashMap<String, Object>();
        textImage2.put("image_url", "http://mmbiz.qpic.cn/mmbiz/p98FjXy8LacgHxp3sJ3vn97bGLz0ib0Sfz1bjiaoOYA027iasqSG0sjpiby4vce3AtaPu6cIhBHkt6IjlkY9YnDsfw/0");
        textImage2.put("text", "此菜品迎合大众口味，老少皆宜，营养均衡");

        List<Map<String, Object>> textImageList = new ArrayList<Map<String, Object>>();
        textImageList.add(textImage1);
        textImageList.add(textImage2);
        advancedInfo.put("text_image_list", textImageList);

        memberCard.put("advanced_info", advancedInfo);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEMBER_CARD");
        card.put("member_card", memberCard);

        Map<String, Object> createMemberCardRequestBody = new HashMap<String, Object>();
        createMemberCardRequestBody.put("card", card);

        String weiXinApiUrl = ConfigurationUtils.getConfiguration(ConfigurationKeys.WEI_XIN_API_URL);
        String createMemberCardUrl = weiXinApiUrl + Constants.WEI_XIN_CARD_CREATE_URI + "?access_token=" + accessToken;

        String createMemberCardResult = OutUtils.doPostWithRequestBody(createMemberCardUrl, JacksonUtils.writeValueAsString(createMemberCardRequestBody), Constants.CHARSET_NAME_UTF_8, Constants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        Map<String, Object> createMemberCardResultMap = JacksonUtils.readValueAsMap(createMemberCardResult, String.class, Object.class);
        ValidateUtils.isTrue(MapUtils.getIntValue(createMemberCardResultMap, "errcode") == 0, MapUtils.getString(createMemberCardResultMap, "errmsg"));

        String cardId = MapUtils.getString(createMemberCardResultMap, "card_id");

        // 设置开卡字段
        Map<String, Object> serviceStatement = new HashMap<String, Object>();
        serviceStatement.put("name", "会员守则");
        serviceStatement.put("url", "https://www.qq.com");

        Map<String, Object> bindOldCard = new HashMap<String, Object>();
        bindOldCard.put("name", "老会员绑定");
        bindOldCard.put("url", "https://www.qq.com");

        Map<String, Object> richField1 = new HashMap<String, Object>();
        richField1.put("type", "FORM_FIELD_RADIO");
        richField1.put("name", "兴趣");
        richField1.put("values", new String[]{"钢琴", "舞蹈", "足球"});

        Map<String, Object> richField2 = new HashMap<String, Object>();
        richField2.put("type", "FORM_FIELD_SELECT");
        richField2.put("name", "喜好");
        richField2.put("values", new String[]{"郭敬明", "韩寒", "南派三叔"});

        Map<String, Object> richField3 = new HashMap<String, Object>();
        richField3.put("type", "FORM_FIELD_CHECK_BOX");
        richField3.put("name", "职业");
        richField3.put("values", new String[]{"赛车手", "旅行家"});

        List<Map<String, Object>> richFieldList = new ArrayList<Map<String, Object>>();
        richFieldList.add(richField1);
        richFieldList.add(richField2);
        richFieldList.add(richField3);

        Map<String, Object> requiredForm = new HashMap<String, Object>();
        requiredForm.put("can_modify", false);
        requiredForm.put("rich_field_list", richFieldList);
        requiredForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_MOBILE"});

        Map<String, Object> optionalForm = new HashMap<String, Object>();
        optionalForm.put("can_modify", false);
        optionalForm.put("common_field_id_list", new String[]{"USER_FORM_INFO_FLAG_LOCATION", "USER_FORM_INFO_FLAG_BIRTHDAY"});
        optionalForm.put("custom_field_list", new String[]{"喜欢的电影"});

        Map<String, Object> activateUserFormRequestBody = new HashMap<String, Object>();
        activateUserFormRequestBody.put("card_id", cardId);
        activateUserFormRequestBody.put("service_statement", serviceStatement);
        activateUserFormRequestBody.put("bind_old_card", bindOldCard);
        activateUserFormRequestBody.put("required_form", requiredForm);
        activateUserFormRequestBody.put("optional_form", optionalForm);

        String activateUserFormUrl = weiXinApiUrl + Constants.WEI_XIN_CARD_MEMBER_CARD_ACTIVATE_USER_FORM_SET_URI + "?access_token=" + accessToken;

        String activateUserFormResult = OutUtils.doPostWithRequestBody(activateUserFormUrl, JacksonUtils.writeValueAsString(activateUserFormRequestBody), Constants.CHARSET_NAME_UTF_8, Constants.CONTENT_TYPE_APPLICATION_JSON_UTF8);

        Map<String, Object> activateUserFormResultMap = JacksonUtils.readValueAsMap(activateUserFormResult, String.class, Object.class);
        ValidateUtils.isTrue(MapUtils.getIntValue(activateUserFormResultMap, "errcode") == 0, MapUtils.getString(activateUserFormResultMap, "errmsg"));

        Map<String, Object> actionInfoCard = new HashMap<String, Object>();
        actionInfoCard.put("card_id", cardId);

        Map<String, Object> actionInfo = new HashMap<String, Object>();
        actionInfo.put("card", actionInfoCard);

        Map<String, Object> createQRcodeRequestBody = new HashMap<String, Object>();
        createQRcodeRequestBody.put("action_name", "QR_CARD");
        createQRcodeRequestBody.put("action_info", actionInfo);

        String createQrCodeUrl = weiXinApiUrl + Constants.WEI_XIN_CARD_QRCODE_CREATE_URI + "?access_token=" + accessToken;

        String createQrCodeResult = OutUtils.doPostWithRequestBody(createQrCodeUrl, JacksonUtils.writeValueAsString(createQRcodeRequestBody), Constants.CHARSET_NAME_UTF_8, Constants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        Map<String, Object> createQrCodeResultMap = JacksonUtils.readValueAsMap(createQrCodeResult, String.class, Object.class);
        ValidateUtils.isTrue(MapUtils.getIntValue(createQrCodeResultMap, "errcode") == 0, MapUtils.getString(createQrCodeResultMap, "errmsg"));

        String url = MapUtils.getString(createQrCodeResultMap, "url");
        String showQrCodeUrl = MapUtils.getString(createQrCodeResultMap, "show_qrcode_url");

        WeiXinMemberCard weiXinMemberCard = new WeiXinMemberCard();
        weiXinMemberCard.setTenantId(tenantId);
        weiXinMemberCard.setAppId(authorizerAppId);
        weiXinMemberCard.setCardId(cardId);
        weiXinMemberCard.setUrl(url);
        weiXinMemberCard.setShowQrCodeUrl(showQrCodeUrl);
        weiXinMemberCard.setCreatedUserId(userId);
        weiXinMemberCard.setUpdatedUserId(userId);
        weiXinMemberCard.setUpdatedRemark("创建微信会员卡！");
        DatabaseHelper.insert(weiXinMemberCard);

        return ApiRest.builder().data(weiXinMemberCard).message("创建会员卡成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest addPayGiftCard(AddPayGiftCardModel addPayGiftCardModel) {
        Long tenantId = addPayGiftCardModel.obtainTenantId();
        List<String> mchIdList = addPayGiftCardModel.getMchIdList();
        Date beginTime = addPayGiftCardModel.getBeginTime();
        Date endTime = addPayGiftCardModel.getEndTime();
        Long weiXinCardId = addPayGiftCardModel.getWeiXinCardId();
        Integer leastCost = addPayGiftCardModel.getLeastCost();
        Integer maxCost = addPayGiftCardModel.getMaxCost();

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未授权微信公众号，不能开通支付即会员！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, weiXinCardId);
        WeiXinMemberCard weiXinMemberCard = DatabaseHelper.find(WeiXinMemberCard.class, searchModel);
        ValidateUtils.notNull(weiXinMemberCard, "微信会员卡不存在！");

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(weiXinAuthorizerInfo.getComponentAppId(), weiXinAuthorizerInfo.getAuthorizerAppId());
        String accessToken = weiXinAuthorizerToken.getAuthorizerAccessToken();

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("mchid_list", mchIdList);
        baseInfo.put("begin_time", beginTime.getTime() / 1000);
        baseInfo.put("end_time", endTime.getTime() / 1000);

        Map<String, Object> memberRule = new HashMap<String, Object>();
        memberRule.put("card_id", weiXinMemberCard.getCardId());
        memberRule.put("least_cost", leastCost);
        memberRule.put("max_cost", maxCost);

        Map<String, Object> ruleInfo = new HashMap<String, Object>();
        ruleInfo.put("type", "RULE_TYPE_PAY_MEMBER_CARD");
        ruleInfo.put("base_info", baseInfo);
        ruleInfo.put("member_rule", memberRule);

        Map<String, Object> payGiftCardRequestBody = new HashMap<String, Object>();
        payGiftCardRequestBody.put("rule_info", ruleInfo);

        String weiXinApiUrl = ConfigurationUtils.getConfiguration(ConfigurationKeys.WEI_XIN_API_URL);
        String weiXinAddPayGiftCardUrl = weiXinApiUrl + Constants.WEI_XIN_CARD_PAY_GIFT_CARD_ADD_URI + "?access_token=" + accessToken;

        String payGiftCardResult = OutUtils.doPostWithRequestBody(weiXinAddPayGiftCardUrl, JacksonUtils.writeValueAsString(payGiftCardRequestBody), Constants.CHARSET_NAME_UTF_8, Constants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        Map<String, Object> payGiftCardResultMap = JacksonUtils.readValueAsMap(payGiftCardResult, String.class, Object.class);
        ValidateUtils.isTrue(MapUtils.getIntValue(payGiftCardResultMap, "errcode") == 0, MapUtils.getString(payGiftCardResultMap, "errmsg"));

        return ApiRest.builder().message("开通支付即会员成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteWeiXinMemberCard(DeleteWeiXinMemberCardModel deleteWeiXinMemberCardModel) {
        Long tenantId = deleteWeiXinMemberCardModel.obtainTenantId();
        Long userId = deleteWeiXinMemberCardModel.obtainUserId();
        Long weiXinCardId = deleteWeiXinMemberCardModel.getWeiXinCardId();

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未授权微信公众号，不能删除微信会员卡！");

        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(weiXinAuthorizerInfo.getComponentAppId(), weiXinAuthorizerInfo.getAuthorizerAppId());
        String accessToken = weiXinAuthorizerToken.getAuthorizerAccessToken();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, weiXinCardId);
        WeiXinMemberCard weiXinMemberCard = DatabaseHelper.find(WeiXinMemberCard.class, searchModel);
        ValidateUtils.notNull(weiXinMemberCard, "微信会员卡不存在！");

        Map<String, Object> deleteCardRequestBody = new HashMap<String, Object>();
        deleteCardRequestBody.put("card_id", weiXinMemberCard.getCardId());

        String weiXinApiUrl = ConfigurationUtils.getConfiguration(ConfigurationKeys.WEI_XIN_API_URL);
        String deleteCardUrl = weiXinApiUrl + Constants.WEI_XIN_CARD_DELETE_URI + "?access_token=" + accessToken;

        String deleteCardResult = OutUtils.doPostWithRequestBody(deleteCardUrl, JacksonUtils.writeValueAsString(deleteCardRequestBody), Constants.CHARSET_NAME_UTF_8, Constants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        Map<String, Object> deleteCardResultMap = JacksonUtils.readValueAsMap(deleteCardResult, String.class, Object.class);
        ValidateUtils.isTrue(MapUtils.getIntValue(deleteCardResultMap, "errcode") == 0, MapUtils.getString(deleteCardResultMap, "errmsg"));

        weiXinMemberCard.setDeleted(true);
        weiXinMemberCard.setUpdatedUserId(userId);
        weiXinMemberCard.setUpdatedRemark("删除微信会员卡！");
        DatabaseHelper.update(weiXinMemberCard);

        return ApiRest.builder().message("删除微信会员卡成功！").successful(true).build();
    }

    /**
     * 查询会员卡列表
     *
     * @param listWeiXinMemberCardsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listWeiXinMemberCards(ListWeiXinMemberCardsModel listWeiXinMemberCardsModel) {
        Long tenantId = listWeiXinMemberCardsModel.obtainTenantId();
        Long branchId = listWeiXinMemberCardsModel.obtainBranchId();
        int page = listWeiXinMemberCardsModel.getPage();
        int rows = listWeiXinMemberCardsModel.getRows();

        SearchModel searchModel = new SearchModel(true);
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchModel.setSearchConditions(searchConditions);
        long count = DatabaseHelper.count(WeiXinMemberCard.class, searchModel);

        List<WeiXinMemberCard> weiXinMemberCards = new ArrayList<WeiXinMemberCard>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            pagedSearchModel.setSearchConditions(searchConditions);
            weiXinMemberCards = DatabaseHelper.findAllPaged(WeiXinMemberCard.class, pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", weiXinMemberCards);
        return new ApiRest(data, "查询会员卡列表成功！");
    }

    /**
     * 获取微信授权信息
     *
     * @param obtainWeiXinAuthorizerInfoModel
     * @return
     * @throws IOException
     */
    public ApiRest obtainWeiXinAuthorizerInfo(ObtainWeiXinAuthorizerInfoModel obtainWeiXinAuthorizerInfoModel) {
        Long tenantId = obtainWeiXinAuthorizerInfoModel.obtainTenantId();
        Long branchId = obtainWeiXinAuthorizerInfoModel.obtainBranchId();

        Map<String, String> obtainWeiXinAuthorizerInfoRequestParameters = new HashMap<String, String>();
        obtainWeiXinAuthorizerInfoRequestParameters.put("tenantId", tenantId.toString());
        obtainWeiXinAuthorizerInfoRequestParameters.put("branchId", branchId.toString());

        ApiRest obtainWeiXinAuthorizerInfoResult = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "weiXin", "obtainWeiXinAuthorizerInfo", obtainWeiXinAuthorizerInfoRequestParameters);
        ValidateUtils.isTrue(obtainWeiXinAuthorizerInfoResult.isSuccessful(), obtainWeiXinAuthorizerInfoResult.getError());
        return ApiRest.builder().data(obtainWeiXinAuthorizerInfoResult.getData()).message("获取微信授权信息成功！").successful(true).build();
    }

    /**
     * 处理授权回调
     *
     * @param authCallbackModel
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleAuthCallback(AuthCallbackModel authCallbackModel) {
        Long tenantId = authCallbackModel.getTenantId();
        String componentAppId = authCallbackModel.getComponentAppId();
        String authCode = authCallbackModel.getAuthCode();

        Map<String, String> handleAuthCallbackRequestParameters = new HashMap<String, String>();
        handleAuthCallbackRequestParameters.put("tenantId", tenantId.toString());
        handleAuthCallbackRequestParameters.put("componentAppId", componentAppId);
        handleAuthCallbackRequestParameters.put("authCode", authCode);

        ApiRest handleAuthCallbackResult = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "weiXin", "handleAuthCallback", handleAuthCallbackRequestParameters);
        ValidateUtils.isTrue(handleAuthCallbackResult.isSuccessful(), handleAuthCallbackResult.getError());

        Map<String, Object> data = (Map<String, Object>) handleAuthCallbackResult.getData();
        Map<String, Object> infoMap = MapUtils.getMap(data, "weiXinAuthorizerInfo");
        Map<String, Object> tokenMap = MapUtils.getMap(data, "weiXinAuthorizerToken");

        return infoMap;
    }

    /**
     * 保存微信菜单
     *
     * @param saveWeiXinMenuModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveWeiXinMenu(SaveWeiXinMenuModel saveWeiXinMenuModel) {
        Long tenantId = saveWeiXinMenuModel.obtainTenantId();
        String tenantCode = saveWeiXinMenuModel.obtainTenantCode();
        Long userId = saveWeiXinMenuModel.obtainUserId();
        SaveWeiXinMenuModel.Button first = saveWeiXinMenuModel.getFirst();
        SaveWeiXinMenuModel.Button second = saveWeiXinMenuModel.getSecond();
        SaveWeiXinMenuModel.Button third = saveWeiXinMenuModel.getThird();

        saveWeiXinMenu(tenantId, tenantCode, userId, first);
        saveWeiXinMenu(tenantId, tenantCode, userId, second);
        saveWeiXinMenu(tenantId, tenantCode, userId, third);
        return ApiRest.builder().message("保存微信菜单成功！").successful(true).build();
    }

    private void saveWeiXinMenu(Long tenantId, String tenantCode, Long userId, SaveWeiXinMenuModel.Button button) {
        if (button == null) {
            return;
        }
        Long id = button.getId();
        if (id == null) {
            WeiXinMenu weiXinMenu = buildWeiXinMenu(tenantId, tenantCode, userId, button);
            DatabaseHelper.insert(weiXinMenu);

            List<SaveWeiXinMenuModel.SubButton> subButtons = button.getSubButtons();
            if (CollectionUtils.isNotEmpty(subButtons)) {
                Long parentId = weiXinMenu.getId();
                List<WeiXinMenu> subWeiXinMenus = new ArrayList<WeiXinMenu>();
                for (SaveWeiXinMenuModel.SubButton subButton : subButtons) {
                    subWeiXinMenus.add(buildSubWeiXinMenu(tenantId, tenantCode, userId, parentId, subButton));
                }
                DatabaseHelper.insertAll(subWeiXinMenus);
            }
        } else {
            WeiXinMenu weiXinMenu = DatabaseHelper.find(WeiXinMenu.class, TupleUtils.buildTuple3(WeiXinMenu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(WeiXinMenu.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id));
            ValidateUtils.notNull(weiXinMenu, "微信菜单不存在！");
            weiXinMenu = buildWeiXinMenu(weiXinMenu, button, userId);
            DatabaseHelper.update(weiXinMenu);

            List<SaveWeiXinMenuModel.SubButton> subButtons = button.getSubButtons();
            if (CollectionUtils.isNotEmpty(subButtons)) {
                List<Long> weiXinMenuIds = new ArrayList<Long>();
                for (SaveWeiXinMenuModel.SubButton subButton : subButtons) {
                    Long weiXinMenuId = subButton.getId();
                    if (weiXinMenuId != null) {
                        weiXinMenuIds.add(weiXinMenuId);
                    }
                }
                List<WeiXinMenu> subWeiXinMenus = DatabaseHelper.findAll(WeiXinMenu.class, TupleUtils.buildTuple3(WeiXinMenu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(WeiXinMenu.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, weiXinMenuIds));
                Map<Long, WeiXinMenu> weiXinMenuMap = new HashMap<Long, WeiXinMenu>();
                for (WeiXinMenu subWeiXinMenu : subWeiXinMenus) {
                    weiXinMenuMap.put(subWeiXinMenu.getId(), subWeiXinMenu);
                }

                Long parentId = weiXinMenu.getId();
                for (SaveWeiXinMenuModel.SubButton subButton : subButtons) {
                    Long weiXinMenuId = subButton.getId();
                    if (weiXinMenuId != null) {
                        WeiXinMenu subWeiXinMenu = weiXinMenuMap.get(weiXinMenuId);
                        ValidateUtils.notNull(subWeiXinMenu, "微信菜单不存在！");

                        weiXinMenu = buildWeiXinMenu(weiXinMenu, subButton, userId);

                        DatabaseHelper.update(subWeiXinMenu);
                    } else {
                        WeiXinMenu subWeiXinMenu = buildSubWeiXinMenu(tenantId, tenantCode, userId, parentId, subButton);
                        DatabaseHelper.insert(subWeiXinMenu);
                    }
                }
            }
        }
    }

    private WeiXinMenu buildWeiXinMenu(WeiXinMenu weiXinMenu, SaveWeiXinMenuModel.Button button, Long userId) {
        String name = button.getName();
        String type = button.getType();
        String messageContent = button.getMessageContent();
        String mediaId = button.getMediaId();
        String url = button.getUrl();
        String pagePath = button.getPagePath();
        String miniProgramAppId = button.getMiniProgramAppId();

        weiXinMenu.setName(name);
        weiXinMenu.setType(type);
        weiXinMenu.setMessageContent(StringUtils.isNotBlank(messageContent) ? messageContent : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setMediaId(StringUtils.isNotBlank(mediaId) ? mediaId : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setUrl(StringUtils.isNotBlank(url) ? url : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setPagePath(StringUtils.isNotBlank(pagePath) ? pagePath : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setMiniProgramAppId(StringUtils.isNotBlank(miniProgramAppId) ? miniProgramAppId : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setUpdatedUserId(userId);
        return weiXinMenu;
    }

    private WeiXinMenu buildWeiXinMenu(WeiXinMenu weiXinMenu, SaveWeiXinMenuModel.SubButton subButton, Long userId) {
        String name = subButton.getName();
        String type = subButton.getType();
        String messageContent = subButton.getMessageContent();
        String mediaId = subButton.getMediaId();
        String url = subButton.getUrl();
        String pagePath = subButton.getPagePath();
        String miniProgramAppId = subButton.getMiniProgramAppId();

        weiXinMenu.setName(name);
        weiXinMenu.setType(type);
        weiXinMenu.setMessageContent(StringUtils.isNotBlank(messageContent) ? messageContent : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setMediaId(StringUtils.isNotBlank(mediaId) ? mediaId : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setUrl(StringUtils.isNotBlank(url) ? url : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setPagePath(StringUtils.isNotBlank(pagePath) ? pagePath : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setMiniProgramAppId(StringUtils.isNotBlank(miniProgramAppId) ? miniProgramAppId : Constants.VARCHAR_DEFAULT_VALUE);
        weiXinMenu.setUpdatedUserId(userId);
        return weiXinMenu;
    }

    private WeiXinMenu buildWeiXinMenu(Long tenantId, String tenantCode, Long userId, SaveWeiXinMenuModel.Button button) {
        String name = button.getName();
        String type = button.getType();
        String messageContent = button.getMessageContent();
        String mediaId = button.getMediaId();
        String url = button.getUrl();
        String pagePath = button.getPagePath();
        String miniProgramAppId = button.getMiniProgramAppId();

        WeiXinMenu weiXinMenu = WeiXinMenu.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .parentId(0L)
                .name(name)
                .type(type)
                .messageContent(StringUtils.isNotBlank(messageContent) ? messageContent : Constants.VARCHAR_DEFAULT_VALUE)
                .mediaId(StringUtils.isNotBlank(mediaId) ? mediaId : Constants.VARCHAR_DEFAULT_VALUE)
                .url(StringUtils.isNotBlank(url) ? url : Constants.VARCHAR_DEFAULT_VALUE)
                .pagePath(StringUtils.isNotBlank(pagePath) ? pagePath : Constants.VARCHAR_DEFAULT_VALUE)
                .miniProgramAppId(StringUtils.isNotBlank(miniProgramAppId) ? miniProgramAppId : Constants.VARCHAR_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        return weiXinMenu;
    }

    private WeiXinMenu buildSubWeiXinMenu(Long tenantId, String tenantCode, Long userId, Long parentId, SaveWeiXinMenuModel.SubButton subButton) {
        String name = subButton.getName();
        String type = subButton.getType();
        String messageContent = subButton.getMessageContent();
        String mediaId = subButton.getMediaId();
        String url = subButton.getUrl();
        String pagePath = subButton.getPagePath();
        String miniProgramAppId = subButton.getMiniProgramAppId();

        WeiXinMenu weiXinMenu = WeiXinMenu.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .parentId(parentId)
                .name(name)
                .type(type)
                .messageContent(StringUtils.isNotBlank(messageContent) ? messageContent : Constants.VARCHAR_DEFAULT_VALUE)
                .mediaId(StringUtils.isNotBlank(mediaId) ? mediaId : Constants.VARCHAR_DEFAULT_VALUE)
                .url(StringUtils.isNotBlank(url) ? url : Constants.VARCHAR_DEFAULT_VALUE)
                .pagePath(StringUtils.isNotBlank(pagePath) ? pagePath : Constants.VARCHAR_DEFAULT_VALUE)
                .miniProgramAppId(StringUtils.isNotBlank(miniProgramAppId) ? miniProgramAppId : Constants.VARCHAR_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        return weiXinMenu;
    }

    /**
     * 推送菜单
     *
     * @param pushMenuModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest pushMenu(PushMenuModel pushMenuModel) {
        Long tenantId = pushMenuModel.obtainTenantId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(WeiXinMenu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        List<WeiXinMenu> weiXinMenus = DatabaseHelper.findAll(WeiXinMenu.class, searchModel);

        List<WeiXinMenu> firstLevelWeiXinMenus = new ArrayList<WeiXinMenu>();
        Map<Long, List<WeiXinMenu>> weiXinMenuMap = new HashMap<Long, List<WeiXinMenu>>();
        for (WeiXinMenu weiXinMenu : weiXinMenus) {
            Long parentId = weiXinMenu.getParentId();
            if (parentId == 0) {
                firstLevelWeiXinMenus.add(weiXinMenu);
            } else {
                List<WeiXinMenu> weiXinMenuList = weiXinMenuMap.get(parentId);
                if (CollectionUtils.isEmpty(weiXinMenuList)) {
                    weiXinMenuList = new ArrayList<WeiXinMenu>();
                    weiXinMenuMap.put(parentId, weiXinMenuList);
                }
                weiXinMenuList.add(weiXinMenu);
            }
        }

        List<CreateMenuModel.Button> buttons = new ArrayList<CreateMenuModel.Button>();
        for (WeiXinMenu weiXinMenu : firstLevelWeiXinMenus) {
            List<WeiXinMenu> childWeiXinMenus = weiXinMenuMap.get(weiXinMenu.getId());
            if (CollectionUtils.isEmpty(childWeiXinMenus)) {
                buttons.add(buildButton(tenantId, weiXinMenu));
            } else {
                CreateMenuModel.Button button = new CreateMenuModel.Button();
                button.setName(weiXinMenu.getName());

                List<CreateMenuModel.SubButton> subButtonModels = new ArrayList<CreateMenuModel.SubButton>();
                for (WeiXinMenu childWeiXinMenu : childWeiXinMenus) {
                    subButtonModels.add(buildSubButton(tenantId, childWeiXinMenu));
                }
                button.setSubButtons(subButtonModels);
                buttons.add(button);
            }
        }

        CreateMenuModel createMenuModel = new CreateMenuModel();
        createMenuModel.setButtons(buttons);

        WeiXinAuthorizerInfo weiXinAuthorizerInfo = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        ValidateUtils.notNull(weiXinAuthorizerInfo, "未检测到微信授权信息！");

        String componentAppId = weiXinAuthorizerInfo.getComponentAppId();
        String authorizerAppId = weiXinAuthorizerInfo.getAuthorizerAppId();
        WeiXinAuthorizerToken weiXinAuthorizerToken = WeiXinUtils.obtainWeiXinAuthorizerToken(componentAppId, authorizerAppId);
        WeiXinUtils.createMenu(weiXinAuthorizerToken.getAuthorizerAccessToken(), createMenuModel);
        return ApiRest.builder().message("推送菜单成功！").successful(true).build();
    }

    private CreateMenuModel.Button buildButton(Long tenantId, WeiXinMenu weiXinMenu) {
        CreateMenuModel.Button button = new CreateMenuModel.Button();
        String type = weiXinMenu.getType();
        String partitionCode = ConfigurationUtils.getConfiguration(ConfigurationKeys.PARTITION_CODE);

        button.setType(type);
        button.setName(weiXinMenu.getName());
        if (Constants.WEI_XIN_MENU_TYPE_CLICK.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_VIEW.equals(type)) {
            button.setUrl(weiXinMenu.getUrl());
        } else if (Constants.WEI_XIN_MENU_TYPE_SCANCODE_PUSH.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_SCANCODE_WAITMSG.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_SYSPHOTO.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_PHOTO_OR_ALBUM.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_WEIXIN.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_LOCATION_SELECT.equals(type)) {
            button.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_MEDIA_ID.equals(type)) {
            button.setMediaId(weiXinMenu.getMediaId());
        } else if (Constants.WEI_XIN_MENU_TYPE_VIEW_LIMITED.equals(type)) {
            button.setMediaId(weiXinMenu.getMediaId());
        } else if (Constants.WEI_XIN_MENU_TYPE_MINIPROGRAM.equals(type)) {
            button.setPagePath(weiXinMenu.getPagePath());
            button.setUrl(weiXinMenu.getUrl());
        }
        return button;
    }

    private CreateMenuModel.SubButton buildSubButton(Long tenantId, WeiXinMenu weiXinMenu) {
        CreateMenuModel.SubButton subButton = new CreateMenuModel.SubButton();
        String type = weiXinMenu.getType();
        String partitionCode = ConfigurationUtils.getConfiguration(ConfigurationKeys.PARTITION_CODE);

        subButton.setType(type);
        subButton.setName(weiXinMenu.getName());
        if (Constants.WEI_XIN_MENU_TYPE_CLICK.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_VIEW.equals(type)) {
            subButton.setUrl(weiXinMenu.getUrl());
        } else if (Constants.WEI_XIN_MENU_TYPE_SCANCODE_PUSH.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_SCANCODE_WAITMSG.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_SYSPHOTO.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_PHOTO_OR_ALBUM.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_PIC_WEIXIN.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_LOCATION_SELECT.equals(type)) {
            subButton.setKey(partitionCode + "_" + tenantId + "_" + weiXinMenu.getId());
        } else if (Constants.WEI_XIN_MENU_TYPE_MEDIA_ID.equals(type)) {
            subButton.setMediaId(weiXinMenu.getMediaId());
        } else if (Constants.WEI_XIN_MENU_TYPE_VIEW_LIMITED.equals(type)) {
            subButton.setMediaId(weiXinMenu.getMediaId());
        } else if (Constants.WEI_XIN_MENU_TYPE_MINIPROGRAM.equals(type)) {
            subButton.setPagePath(weiXinMenu.getPagePath());
            subButton.setUrl(weiXinMenu.getUrl());
        }
        return subButton;
    }

    /**
     * 查询微信菜单
     *
     * @param listMenusModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listMenus(ListMenusModel listMenusModel) {
        Long tenantId = listMenusModel.obtainTenantId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(WeiXinMenu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        List<WeiXinMenu> weiXinMenus = DatabaseHelper.findAll(WeiXinMenu.class, searchModel);
        List<WeiXinMenu> firstLevelWeiXinMenus = new ArrayList<WeiXinMenu>();
        Map<Long, List<WeiXinMenu>> weiXinMenuMap = new HashMap<Long, List<WeiXinMenu>>();
        for (WeiXinMenu weiXinMenu : weiXinMenus) {
            Long parentId = weiXinMenu.getParentId();
            if (parentId == 0) {
                firstLevelWeiXinMenus.add(weiXinMenu);
            } else {
                List<WeiXinMenu> weiXinMenuList = weiXinMenuMap.get(parentId);
                if (CollectionUtils.isEmpty(weiXinMenuList)) {
                    weiXinMenuList = new ArrayList<WeiXinMenu>();
                    weiXinMenuMap.put(parentId, weiXinMenuList);
                }
                weiXinMenuList.add(weiXinMenu);
            }
        }

        List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
        for (WeiXinMenu weiXinMenu : firstLevelWeiXinMenus) {
            Map<String, Object> menu = buildMenu(weiXinMenu);

            List<WeiXinMenu> subWeiXinMenus = weiXinMenuMap.get(weiXinMenu.getId());
            if (CollectionUtils.isNotEmpty(subWeiXinMenus)) {
                List<Map<String, Object>> subMenus = new ArrayList<Map<String, Object>>();
                for (WeiXinMenu subWeiXinMenu : subWeiXinMenus) {
                    subMenus.add(buildMenu(subWeiXinMenu));
                }
                menu.put("subMenus", subMenus);
            }

            menus.add(menu);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        if (CollectionUtils.isNotEmpty(menus)) {
            int size = menus.size();
            if (size == 1) {
                data.put("first", menus.get(0));
            } else if (size == 2) {
                data.put("first", menus.get(0));
                data.put("second", menus.get(1));
            } else if (size == 3) {
                data.put("first", menus.get(0));
                data.put("second", menus.get(1));
                data.put("third", menus.get(2));
            }
        }

        return ApiRest.builder().data(data).message("查询微信菜单成功！").successful(true).build();
    }

    private Map<String, Object> buildMenu(WeiXinMenu weiXinMenu) {
        Map<String, Object> menu = new HashMap<String, Object>();
        menu.put("name", weiXinMenu.getName());

        if (StringUtils.isNotBlank(weiXinMenu.getType())) {
            menu.put("type", weiXinMenu.getType());
            menu.put("messageContent", weiXinMenu.getMessageContent());
            menu.put("mediaId", weiXinMenu.getMediaId());
            menu.put("url", weiXinMenu.getUrl());
            menu.put("pagePath", weiXinMenu.getPagePath());
            menu.put("miniProgramAppId", weiXinMenu.getMiniProgramAppId());
        }
        return menu;
    }

    /**
     * 获取消息内容
     *
     * @param obtainMessageContentModel
     * @return
     */
    public ApiRest obtainMessageContent(ObtainMessageContentModel obtainMessageContentModel) {
        Long tenantId = obtainMessageContentModel.getTenantId();
        Long weiXinMenuId = obtainMessageContentModel.getWeiXinMenuId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(WeiXinMenu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(WeiXinMenu.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, weiXinMenuId);
        WeiXinMenu weiXinMenu = DatabaseHelper.find(WeiXinMenu.class, searchModel);

        String data = "";
        if (weiXinMenu != null) {
            data = weiXinMenu.getMessageContent();
        }
        return ApiRest.builder().data(data).message("获取消息内容成功！").successful(true).build();
    }
}
