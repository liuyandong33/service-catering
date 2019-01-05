package build.dream.catering.configurations;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchConfiguration {
    @Value(value = "${elasticsearch.cluster.name}")
    private String clusterName;

    @Value(value = "${elasticsearch.cluster.nodes}")
    private String clusterNodes;

    private static final String CLUSTER_NAME = "cluster.name";

    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        TransportClient transportClient = null;
        if (StringUtils.isNotBlank(clusterName) && StringUtils.isNotBlank(clusterNodes)) {
            Settings settings = Settings.builder()
                    .put(CLUSTER_NAME, clusterName)
                    .build();
            transportClient = new PreBuiltTransportClient(settings);

            String[] clusterNodeArray = clusterNodes.split(",");
            for (String clusterNode : clusterNodeArray) {
                String[] hostNameAndPort = clusterNode.split(":");
                InetAddress inetAddress = InetAddress.getByName(hostNameAndPort[0]);
                TransportAddress transportAddress = new InetSocketTransportAddress(inetAddress, Integer.parseInt(hostNameAndPort[1]));
                transportClient.addTransportAddress(transportAddress);
            }
        }
        return transportClient;
    }
}
