package build.dream.catering.utils;

import build.dream.common.erp.catering.domains.Vip;
import build.dream.common.utils.SearchModel;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class VipUtils {
    public static long insert(Vip vip) {
        return DatabaseHelper.insert(vip);
    }

    public static long update(Vip vip) {
        return DatabaseHelper.update(vip);
    }

    public static long update(Vip vip, String recordBonus) {
        String userCardCode = vip.getUserCardCode();
        String cardId = vip.getCardId();
        if (StringUtils.isNotBlank(userCardCode) && StringUtils.isNotBlank(cardId)) {
            WeiXinUtils.updateMemberBonusSafe(vip.getTenantId().toString(), userCardCode, cardId, vip.getBonus(), null, recordBonus);
        }
        return DatabaseHelper.update(vip);
    }

    public static Vip find(SearchModel searchModel) {
        return DatabaseHelper.find(Vip.class, searchModel);
    }

    public static List<Vip> findAll(SearchModel searchModel) {
        return DatabaseHelper.findAll(Vip.class, searchModel);
    }

    public static long count(SearchModel searchModel) {
        return DatabaseHelper.count(Vip.class, searchModel);
    }

    public static List<Vip> findAllPaged(SearchModel searchModel) {
        return DatabaseHelper.findAllPaged(Vip.class, searchModel);
    }
}
