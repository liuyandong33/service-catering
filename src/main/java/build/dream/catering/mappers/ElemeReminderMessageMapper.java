package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeReminderMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElemeReminderMessageMapper {
    long insert(ElemeReminderMessage elemeReminderMessage);
    long update(ElemeReminderMessage elemeReminderMessage);
}
