package build.dream.catering.configurations;

import build.dream.catering.constants.Constants;
import build.dream.common.utils.ConfigurationUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetAddress;

@Configuration
public class ElasticSearchConfiguration {
    @Bean
    public TransportClient transportClient() throws IOException {
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        String clusterName = ConfigurationUtils.getConfiguration(Constants.ELASTICSEARCH_CLUSTER_NAME);
        String clusterNodes = ConfigurationUtils.getConfiguration(Constants.ELASTICSEARCH_CLUSTER_NODES);

        Settings settings = Settings.builder().put("cluster.name", clusterName).build();

        TransportClient transportClient = new PreBuiltTransportClient(settings);

        String[] nodes = clusterNodes.split(",");
        for (String node : nodes) {
            String[] array = node.split(":");
            String ipAddress = array[0];
            int port = Integer.parseInt(array[1]);

            String[] addresses = ipAddress.split("\\.");
            byte[] addr = new byte[4];
            for (int index = 0; index < addresses.length; index++) {
                addr[index] = (byte) (Integer.parseInt(addresses[index]) & 0xff);
            }
            InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByAddress(addr), port);
            transportClient.addTransportAddress(inetSocketTransportAddress);
        }
        return transportClient;
    }
}
