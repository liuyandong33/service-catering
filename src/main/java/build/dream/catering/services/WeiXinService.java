package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.weixin.CreateMemberCardModel;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ProxyUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinService {
    @Transactional(readOnly = true)
    public ApiRest createMemberCard(CreateMemberCardModel createMemberCardModel, MultipartFile backgroundPicFile, MultipartFile logoFile) throws IOException {
        String accessToken = null;

        String backgroundPicUrl = null;
        if (backgroundPicFile != null) {
            Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
            uploadBackgroundPicRequestParameters.put("buffer", backgroundPicFile);
            uploadBackgroundPicRequestParameters.put("access_token", accessToken);
            ApiRest uploadBackgroundPicApiRest = ProxyUtils.doPostWithRequestParametersAndFiles(Constants.SERVICE_NAME_OUT, "proxy", "doPostMultipart", uploadBackgroundPicRequestParameters);
            Validate.isTrue(uploadBackgroundPicApiRest.isSuccessful(), uploadBackgroundPicApiRest.getError());

            String uploadBackgroundPicResult = uploadBackgroundPicApiRest.getData().toString();
            JSONObject uploadBackgroundPicResultJsonObject = JSONObject.fromObject(uploadBackgroundPicResult);
            Validate.isTrue(uploadBackgroundPicResultJsonObject.has("errcode"), uploadBackgroundPicResultJsonObject.optString("errmsg"));

            backgroundPicUrl = uploadBackgroundPicResultJsonObject.getString("url");
        }

        Map<String, Object> uploadBackgroundPicRequestParameters = new HashMap<String, Object>();
        uploadBackgroundPicRequestParameters.put("buffer", logoFile);
        uploadBackgroundPicRequestParameters.put("access_token", accessToken);
        ApiRest uploadLogoApiRest = ProxyUtils.doPostWithRequestParametersAndFiles(Constants.SERVICE_NAME_OUT, "proxy", "doPostMultipart", uploadBackgroundPicRequestParameters);
        Validate.isTrue(uploadLogoApiRest.isSuccessful(), uploadLogoApiRest.getError());

        String uploadLogoResult = uploadLogoApiRest.getData().toString();
        JSONObject uploadLogoResultJsonObject = JSONObject.fromObject(uploadLogoResult);
        Validate.isTrue(uploadLogoResultJsonObject.has("errcode"), uploadLogoResultJsonObject.optString("errmsg"));

        String logoUrl = uploadLogoResultJsonObject.getString("url");

        Map<String, Object> baseInfo = new HashMap<String, Object>();
        baseInfo.put("logo_url", logoUrl);
        baseInfo.put("brand_name", createMemberCardModel.getBrandName());
        baseInfo.put("code_type", "CODE_TYPE_TEXT");
        baseInfo.put("title", createMemberCardModel.getTitle());
        baseInfo.put("color", createMemberCardModel.getColor());
        baseInfo.put("notice", createMemberCardModel.getNotice());

        Map<String, Object> advancedInfo = new HashMap<String, Object>();

        String servicePhone = createMemberCardModel.getServicePhone();
        if (StringUtils.isNotBlank(servicePhone)) {
            baseInfo.put("service_phone", servicePhone);
        }
        baseInfo.put("description", createMemberCardModel.getDescription());

        Map<String, Object> memberCard = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(backgroundPicUrl)) {
            memberCard.put("background_pic_url", backgroundPicUrl);
        }
        memberCard.put("base_info", baseInfo);
        memberCard.put("advanced_info", advancedInfo);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEMBER_CARD");
        card.put("member_card", memberCard);

        Map<String, Object> createMemberCardRequestBody = new HashMap<String, Object>();
        createMemberCardRequestBody.put("card", card);

        Map<String, String> doPostRequestParameters = new HashMap<String, String>();
        doPostRequestParameters.put("url", "https://api.weixin.qq.com/card/create?access_token=" + accessToken);
        doPostRequestParameters.put("requestBody", GsonUtils.toJson(createMemberCardRequestBody));

        ApiRest doPostApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "proxy", "doPost", doPostRequestParameters);
        Validate.isTrue(doPostApiRest.isSuccessful(), doPostApiRest.getError());
        String createMemberCardResult = doPostApiRest.getData().toString();
        JSONObject createMemberCardResultJsonObject = JSONObject.fromObject(createMemberCardResult);
        Validate.isTrue(createMemberCardResultJsonObject.getInt("errcode") == 0, createMemberCardResultJsonObject.getString("errmsg"));

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("创建会员卡成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
