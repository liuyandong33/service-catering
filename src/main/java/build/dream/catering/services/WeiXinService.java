package build.dream.catering.services;

import build.dream.catering.models.weixin.CreateMemberCardModel;
import build.dream.catering.models.weixin.PayGiftCardModel;
import build.dream.catering.utils.WeiXinUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.saas.domains.WeiXinPublicAccount;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.OutUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
public class WeiXinService {
    @Transactional(readOnly = true)
    public ApiRest createMemberCard(CreateMemberCardModel createMemberCardModel, MultipartFile backgroundPicFile, MultipartFile logoFile) throws IOException {
        BigInteger tenantId = createMemberCardModel.getTenantId();
        WeiXinPublicAccount weiXinPublicAccount = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        Validate.notNull(weiXinPublicAccount, "未配置微信公众号，不能创建会员卡！");

        String appId = weiXinPublicAccount.getAppId();
        String appSecret = weiXinPublicAccount.getAppSecret();

        String accessToken = WeiXinUtils.obtainAccessToken(appId, appSecret);

        String uploadImgUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg";
        String backgroundPicUrl = null;
        if (backgroundPicFile != null) {
            Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
            uploadBackgroundPicRequestParameters.put("buffer", backgroundPicFile);
            uploadBackgroundPicRequestParameters.put("access_token", accessToken);

            String uploadBackgroundPicResult = OutUtils.doPostMultipart(uploadImgUrl, uploadBackgroundPicRequestParameters, null);
            JSONObject uploadBackgroundPicResultJsonObject = JSONObject.fromObject(uploadBackgroundPicResult);
            Validate.isTrue(!uploadBackgroundPicResultJsonObject.has("errcode"), uploadBackgroundPicResultJsonObject.optString("errmsg"));

            backgroundPicUrl = uploadBackgroundPicResultJsonObject.getString("url");
        }

        Map<String, Object> uploadLogoRequestParameters = new HashMap<String, Object>();
        uploadLogoRequestParameters.put("buffer", logoFile);
        uploadLogoRequestParameters.put("access_token", accessToken);

        String uploadLogoResult = OutUtils.doPostMultipart(uploadImgUrl, uploadLogoRequestParameters, null);
        JSONObject uploadLogoResultJsonObject = JSONObject.fromObject(uploadLogoResult);
        Validate.isTrue(!uploadLogoResultJsonObject.has("errcode"), uploadLogoResultJsonObject.optString("errmsg"));

        String logoUrl = uploadLogoResultJsonObject.getString("url");

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

        String createMemberCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/create?access_token=" + accessToken, GsonUtils.toJson(createMemberCardRequestBody), null);
        JSONObject createMemberCardResultJsonObject = JSONObject.fromObject(createMemberCardResult);
        Validate.isTrue(createMemberCardResultJsonObject.getInt("errcode") == 0, createMemberCardResultJsonObject.getString("errmsg"));

        String cardId = createMemberCardResultJsonObject.getString("card_id");

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

        String activateUserFormResult = OutUtils.doPost("https://api.weixin.qq.com/card/membercard/activateuserform/set?access_token=" + accessToken, GsonUtils.toJson(activateUserFormRequestBody), null);
        JSONObject activateUserFormResultJsonObject = JSONObject.fromObject(activateUserFormResult);
        Validate.isTrue(activateUserFormResultJsonObject.getInt("errcode") == 0, activateUserFormResultJsonObject.getString("errmsg"));

        Map<String, Object> actionInfoCard = new HashMap<String, Object>();
        actionInfoCard.put("card_id", cardId);

        Map<String, Object> actionInfo = new HashMap<String, Object>();
        actionInfo.put("card", actionInfoCard);

        Map<String, Object> createQRcodeRequestBody = new HashMap<String, Object>();
        createQRcodeRequestBody.put("action_name", "QR_CARD");
        createQRcodeRequestBody.put("action_info", actionInfo);

        String createQrCodeResult = OutUtils.doPost("https://api.weixin.qq.com/card/qrcode/create?access_token=" + accessToken, GsonUtils.toJson(createQRcodeRequestBody), null);
        JSONObject createQrCodeResultJsonObject = JSONObject.fromObject(createQrCodeResult);
        Validate.isTrue(createQrCodeResultJsonObject.getInt("errcode") == 0, createQrCodeResultJsonObject.getString("errmsg"));

        String url = createQrCodeResultJsonObject.getString("url");
        String showQrCodeUrl = createQrCodeResultJsonObject.getString("show_qrcode_url");

        Map<String, Object> weiXinMemberCard = new HashMap<String, Object>();
        weiXinMemberCard.put("tenantId", tenantId);
        weiXinMemberCard.put("appId", appId);
        weiXinMemberCard.put("appSecret", appSecret);
        weiXinMemberCard.put("cardId", cardId);
        weiXinMemberCard.put("url", url);
        weiXinMemberCard.put("showQrCodeUrl", showQrCodeUrl);
        weiXinMemberCard.put("createTime", new Date());
        weiXinMemberCard.put("createUserId", createMemberCardModel.getUserId());
        weiXinMemberCard.put("lastUpdateTime", new Date());
        weiXinMemberCard.put("lastUpdateUserId", createMemberCardModel.getUserId());
        weiXinMemberCard.put("lastUpdateRemark", null);
        weiXinMemberCard.put("deleted", false);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("创建会员卡成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    public ApiRest payGiftCard(PayGiftCardModel payGiftCardModel) throws IOException {
        BigInteger tenantId = payGiftCardModel.getTenantId();
        WeiXinPublicAccount weiXinPublicAccount = WeiXinUtils.obtainWeiXinPublicAccount(tenantId.toString());
        Validate.notNull(weiXinPublicAccount, "未配置微信公众号，不能开通支付即会员！");

        String appId = weiXinPublicAccount.getAppId();
        String appSecret = weiXinPublicAccount.getAppSecret();

        String accessToken = WeiXinUtils.obtainAccessToken(appId, appSecret);

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("mchid_list", payGiftCardModel.getMchIdList());
        baseInfo.put("begin_time", payGiftCardModel.getBeginTime().getTime() / 1000);
        baseInfo.put("end_time", payGiftCardModel.getEndTime().getTime() / 1000);

        Map<String, Object> memberRule = new HashedMap<String, Object>();
        memberRule.put("card_id", "");
        memberRule.put("least_cost", payGiftCardModel.getLeastCost());
        memberRule.put("max_cost", payGiftCardModel.getMaxCost());

        Map<String, Object> ruleInfo = new HashMap<String, Object>();
        ruleInfo.put("type", "RULE_TYPE_PAY_MEMBER_CARD");
        ruleInfo.put("base_info", baseInfo);
        ruleInfo.put("member_rule", memberRule);

        Map<String, Object> payGiftCardRequestBody = new HashMap<String, Object>();
        payGiftCardRequestBody.put("rule_info", ruleInfo);

        String payGiftCardResult = OutUtils.doPost("https://api.weixin.qq.com/card/paygiftcard/add?access_token=" + accessToken, GsonUtils.toJson(payGiftCardRequestBody), null);
        JSONObject payGiftCardResultJsonObject = JSONObject.fromObject(payGiftCardResult);
        Validate.isTrue(payGiftCardResultJsonObject.getInt("errcode") == 0, payGiftCardResultJsonObject.getString("errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("开通支付即会员成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
