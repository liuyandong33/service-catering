package build.dream.catering.services;

import build.dream.catering.models.assemble.SaveAssembleActivityModel;
import build.dream.common.api.ApiRest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssembleService {
    /**
     * 保存拼团活动
     *
     * @param saveAssembleActivityModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveAssembleActivity(SaveAssembleActivityModel saveAssembleActivityModel) {
        return null;
    }
}
