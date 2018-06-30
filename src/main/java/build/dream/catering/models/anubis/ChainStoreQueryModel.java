package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ChainStoreQueryModel extends BasicModel {
    @NotEmpty
    private List<String> chainStoreCodes;

    public List<String> getChainStoreCodes() {
        return chainStoreCodes;
    }

    public void setChainStoreCodes(List<String> chainStoreCodes) {
        this.chainStoreCodes = chainStoreCodes;
    }
}
