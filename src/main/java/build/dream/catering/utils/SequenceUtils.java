package build.dream.catering.utils;

import build.dream.catering.mappers.SequenceMapper;
import build.dream.common.utils.ApplicationHandler;

public class SequenceUtils {
    private static SequenceMapper SEQUENCE_MAPPER = null;

    private static SequenceMapper obtainSequenceMapper() {
        if (SEQUENCE_MAPPER == null) {
            SEQUENCE_MAPPER = ApplicationHandler.getBean(SequenceMapper.class);
        }
        return SEQUENCE_MAPPER;
    }

    public static Integer nextValue(String sequenceName) {
        return obtainSequenceMapper().nextValue(sequenceName);
    }

    public static Integer currentValue(String sequenceName) {
        return obtainSequenceMapper().currentValue(sequenceName);
    }
}
