package build.dream.catering.utils;

import build.dream.catering.mappers.VipMapper;
import build.dream.common.erp.catering.domains.Vip;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.SearchModel;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class VipUtils {
    private static VipMapper VIP_MAPPER;

    private static VipMapper obtainVipMapper() {
        if (VIP_MAPPER == null) {
            VIP_MAPPER = ApplicationHandler.getBean(VipMapper.class);
        }
        return VIP_MAPPER;
    }

    public static long insert(Vip vip) {
        return obtainVipMapper().insert(vip);
    }

    public static long update(Vip vip) {
        return obtainVipMapper().update(vip);
    }

    public static long update(Vip vip, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            WeiXinUtils.updateMemberBonusSafe(vip.getTenantId().toString(), userCardCode, cardId, vip.getBonus(), null, recordBonus);
        }
        return obtainVipMapper().update(vip);
    }

    public static Vip find(SearchModel searchModel) {
        return obtainVipMapper().find(searchModel);
    }

    public static List<Vip> findAll(SearchModel searchModel) {
        return obtainVipMapper().findAll(searchModel);
    }

    public static long count(SearchModel searchModel) {
        return obtainVipMapper().count(searchModel);
    }

    public static List<Vip> findAllPaged(SearchModel searchModel) {
        return obtainVipMapper().findAllPaged(searchModel);
    }
}
