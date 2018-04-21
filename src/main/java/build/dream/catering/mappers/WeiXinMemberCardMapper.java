package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.WeiXinMemberCard;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeiXinMemberCardMapper {
    long insert(WeiXinMemberCard weiXinMemberCard);

    long update(WeiXinMemberCard weiXinMemberCard);

    WeiXinMemberCard find(SearchModel searchModel);

    List<WeiXinMemberCard> findAll(SearchModel searchModel);

    long count(SearchModel searchModel);

    List<WeiXinMemberCard> findAllPaged(SearchModel searchModel);
}
