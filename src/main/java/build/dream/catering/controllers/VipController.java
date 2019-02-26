package build.dream.catering.controllers;

import build.dream.catering.models.vip.*;
import build.dream.catering.services.VipService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/vip")
public class VipController {
    /**
     * 保存会员类型
     *
     * @return
     */
    @RequestMapping(value = "/saveVipType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipTypeModel.class, serviceClass = VipService.class, serviceMethodName = "saveVipType", error = "保存会员类型失败")
    public String saveVipType() {
        return null;
    }

    /**
     * 获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainVipInfoModel.class, serviceClass = VipService.class, serviceMethodName = "obtainVipInfo", error = "获取会员信息失败")
    public String obtainVipInfo() {
        return null;
    }

    /**
     * 保存会员信息
     *
     * @return
     */
    @RequestMapping(value = "/saveVipInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipInfoModel.class, serviceClass = VipService.class, serviceMethodName = "saveVipInfo", error = "获取会员信息失败")
    public String saveVipInfo() {
        return null;
    }

    /**
     * 修改会员隔离级别
     *
     * @return
     */
    @RequestMapping(value = "/changeVipSharedType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ChangeVipSharedTypeModel.class, serviceClass = VipService.class, serviceMethodName = "changeVipSharedType", error = "获取会员信息失败")
    public String changeVipSharedType() {
        return null;
    }

    /**
     * 获取会员类型
     *
     * @return
     */
    @RequestMapping(value = "/listVipTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListVipTypesModel.class, serviceClass = VipService.class, serviceMethodName = "listVipTypes", error = "获取会员类型失败")
    public String listVipTypes() {
        return null;
    }

    /**
     * 删除会员类型
     *
     * @return
     */
    @RequestMapping(value = "/deleteVipType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteVipTypeModel.class, serviceClass = VipService.class, serviceMethodName = "deleteVipType", error = "删除会员类型失败")
    public String deleteVipType() {
        return null;
    }

    /**
     * 删除会员分组
     *
     * @return
     */
    @RequestMapping(value = "/deleteVipGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteVipGroupModel.class, serviceClass = VipService.class, serviceMethodName = "deleteVipGroup", error = "删除会员类型失败")
    public String deleteVipGroup() {
        return null;
    }

    /**
     * 获取会员分组
     *
     * @return
     */
    @RequestMapping(value = "/listVipGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListVipGroupsModel.class, serviceClass = VipService.class, serviceMethodName = "listVipGroups", error = "获取会员分组失败")
    public String listVipGroups() {
        return null;
    }

    /**
     * 保存会员分组
     *
     * @return
     */
    @RequestMapping(value = "/saveVipGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipGroupModel.class, serviceClass = VipService.class, serviceMethodName = "saveVipGroup", error = "保存会员分组失败")
    public String saveVipGroup() {
        return null;
    }

    @RequestMapping(value = "/listVipInfos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListVipInfosModel.class, serviceClass = VipService.class, serviceMethodName = "listVipInfos", error = "获取会员信息失败")
    public String listVipInfos() {
        return null;
    }
}
