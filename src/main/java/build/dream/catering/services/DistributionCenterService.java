package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.distributioncenter.SaveModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.DistributionCenter;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.common.utils.TupleUtils;
import build.dream.common.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class DistributionCenterService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest save(SaveModel saveModel) {
        BigInteger id = saveModel.getId();
        BigInteger tenantId = saveModel.obtainTenantId();
        String tenantCode = saveModel.obtainTenantCode();
        String name = saveModel.getName();
        Integer status = saveModel.getStatus();
        String provinceCode = saveModel.getProvinceCode();
        String provinceName = saveModel.getProvinceName();
        String cityCode = saveModel.getCityCode();
        String cityName = saveModel.getCityName();
        String districtCode = saveModel.getDistrictCode();
        String districtName = saveModel.getDistrictName();
        String address = saveModel.getAddress();
        String longitude = saveModel.getLongitude();
        String latitude = saveModel.getLatitude();
        String linkman = saveModel.getLinkman();
        String contactPhone = saveModel.getContactPhone();
        BigInteger userId = saveModel.obtainUserId();

        DistributionCenter distributionCenter = null;
        if (id != null) {
            distributionCenter = DatabaseHelper.find(DistributionCenter.class, TupleUtils.buildTuple3(DistributionCenter.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id), TupleUtils.buildTuple3(DistributionCenter.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
            ValidateUtils.notNull(distributionCenter, "配送中心不存在！", Constants.ERROR_CODE_HANDLING_ERROR);
            distributionCenter.setName(name);
            distributionCenter.setStatus(status);
            distributionCenter.setProvinceCode(provinceCode);
            distributionCenter.setProvinceName(provinceName);
            distributionCenter.setCityCode(cityCode);
            distributionCenter.setCityName(cityName);
            distributionCenter.setDistrictCode(districtCode);
            distributionCenter.setDistrictName(districtName);
            distributionCenter.setAddress(address);
            distributionCenter.setLongitude(longitude);
            distributionCenter.setLatitude(latitude);
            distributionCenter.setLinkman(linkman);
            distributionCenter.setContactPhone(contactPhone);
            distributionCenter.setUpdatedUserId(userId);
            distributionCenter.setUpdatedRemark("修改配送中心信息！");
        } else {
            String code = SerialNumberGenerator.nextSerialNumber(4, SequenceUtils.nextValue(tenantCode + "_distribution_center_count"));
            distributionCenter = DistributionCenter.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .code(code)
                    .name(name)
                    .status(status)
                    .provinceCode(provinceCode)
                    .provinceName(provinceName)
                    .cityCode(cityCode)
                    .cityName(cityName)
                    .districtCode(districtCode)
                    .districtName(districtName)
                    .address(address)
                    .longitude(longitude)
                    .latitude(latitude)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增配送中心信息！")
                    .build();
            DatabaseHelper.insert(distributionCenter);
        }

        return ApiRest.builder().data(distributionCenter).message("保存成功！").successful(true).build();
    }
}
