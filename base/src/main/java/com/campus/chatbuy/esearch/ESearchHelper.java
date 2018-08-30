package com.campus.chatbuy.esearch;

import com.campus.chatbuy.util.ConfigUtil;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Date;
import java.util.Map;

/**
 * Created by jinku on 2017/10/5.
 */
public class ESearchHelper {

    private static final Logger log = Logger.getLogger(ESearchHelper.class);

    private final static String ES_HOST = ConfigUtil.getValue("ES_HOST");
    private final static int ES_PORT = ConfigUtil.getIntValue("ES_PORT");

    private volatile static ESearchHelper instance = null;
    private static RestHighLevelClient apiRestClient;

    private ESearchHelper() {
        log.info("ESearchHelper init host [" + ES_HOST + "]" + "; port [" + ES_PORT + "]");
        RestClientBuilder builder = RestClient.builder(new HttpHost(ES_HOST, ES_PORT, "http"));
        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(HttpHost host) {
                // 日志报警
                log.error("ESearchHelper onFailure host is [" + host.getHostName() + "]");
            }
        });
        apiRestClient = new RestHighLevelClient(builder.build());
        log.info("ESearchHelper init end");
    }

    public static ESearchHelper getInstance() {
        if (instance == null) {
            synchronized (ESearchHelper.class) {
                if (instance == null) {
                    instance = new ESearchHelper();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void indexOrUpdate(String index, String type, String id, Map<String, Object> dataMap) throws Exception {
        log.info("ESearchHelper indexOrUpdate index [" + index + "];type [" + type + "];id [" + id + "];dataMap [" +
                dataMap + "]");
        UpdateRequest indexRequest = new UpdateRequest(index, type, id).doc(dataMap).upsert(dataMap);
        apiRestClient.update(indexRequest);
        log.info("ESearchHelper indexOrUpdate end");
    }

    public SearchResponse search(SearchRequest searchRequest) throws Exception {
        SearchResponse searchResponse = apiRestClient.search(searchRequest);
        return searchResponse;
    }

}
