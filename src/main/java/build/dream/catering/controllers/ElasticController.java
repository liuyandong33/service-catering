package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/elastic")
public class ElasticController {
    @Autowired
    private TransportClient transportClient;

    @RequestMapping(value = "/createIndex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String createIndex() throws IOException {
        AdminClient adminClient = transportClient.admin();
        IndicesAdminClient indicesAdminClient = adminClient.indices();

        CreateIndexRequestBuilder createIndexRequestBuilder = indicesAdminClient.prepareCreate("goods_info");
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("code").field("type", "text").endObject()
                .startObject("name").field("type", "text").endObject()
                .endObject()
                .endObject();
        createIndexRequestBuilder.addMapping("goods", contentBuilder);

        Settings settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 3)
                .build();
        createIndexRequestBuilder.setSettings(settings);
        CreateIndexResponse createIndexResponse = createIndexRequestBuilder.execute().actionGet();
        return Constants.SUCCESS;
    }
}
