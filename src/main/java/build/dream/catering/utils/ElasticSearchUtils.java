package build.dream.catering.utils;

import build.dream.common.utils.JacksonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;

public class ElasticSearchUtils {
    public static SearchResultMapper SEARCH_RESULT_MAPPER = new SearchResultMapper() {
        @Override
        public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
            List<T> ts = new ArrayList<T>();
            SearchHits searchHits = response.getHits();
            for (SearchHit searchHit : searchHits) {
                ts.add(JacksonUtils.readValue(searchHit.getSourceAsString(), clazz));
            }
            if (CollectionUtils.isEmpty(ts)) {
                return null;
            }
            return new AggregatedPageImpl<T>(ts);
        }
    };
}
