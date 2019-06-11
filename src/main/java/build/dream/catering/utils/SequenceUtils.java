package build.dream.catering.utils;

import build.dream.catering.mappers.SequenceMapper;
import build.dream.common.utils.ApplicationHandler;

public class SequenceUtils {
    private static SequenceMapper sequenceMapper = null;

    private static SequenceMapper obtainSequenceMapper() {
        if (sequenceMapper == null) {
            sequenceMapper = ApplicationHandler.getBean(SequenceMapper.class);
        }
        return sequenceMapper;
    }

    public static Integer nextValue(String sequenceName) {
        return obtainSequenceMapper().nextValue(sequenceName);
    }

    public static Integer currentValue(String sequenceName) {
        return obtainSequenceMapper().currentValue(sequenceName);
    }

    public static Integer currentValueToday(String sequenceName) {
        return obtainSequenceMapper().currentValueToday(sequenceName);
    }

    public static Integer nextValueToday(String sequenceName) {
        return obtainSequenceMapper().nextValueToday(sequenceName);
    }
}
