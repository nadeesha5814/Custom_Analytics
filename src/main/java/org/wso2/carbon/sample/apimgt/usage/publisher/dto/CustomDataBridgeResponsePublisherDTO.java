package org.wso2.carbon.sample.apimgt.usage.publisher.dto;

/**
 * Created by NADEESHA on 12/14/2015.
 */

import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.apimgt.usage.publisher.dto.ResponsePublisherDTO;

public class CustomDataBridgeResponsePublisherDTO extends ResponsePublisherDTO {
    String customValue;


    public CustomDataBridgeResponsePublisherDTO(ResponsePublisherDTO responsePublisherDTO) {
        setConsumerKey(responsePublisherDTO.getConsumerKey());
        setContext(responsePublisherDTO.getContext());
        setApi_version(responsePublisherDTO.getApi_version());
        setApi(responsePublisherDTO.getApi());
        setResourcePath(responsePublisherDTO.getResourcePath());
        setMethod(responsePublisherDTO.getMethod());
        setVersion(responsePublisherDTO.getVersion());
        setResponseTime(responsePublisherDTO.getResponseTime());
        setServiceTime(responsePublisherDTO.getServiceTime());
        setBackendTime(responsePublisherDTO.getBackendTime());
        setUsername(responsePublisherDTO.getUsername());
        setEventTime(responsePublisherDTO.getEventTime());
        setTenantDomain(responsePublisherDTO.getTenantDomain());
        setHostName(DataPublisherUtil.getHostAddress());
        setApiPublisher(responsePublisherDTO.getApiPublisher());
        setApplicationName(responsePublisherDTO.getApplicationName());
        setApplicationId(responsePublisherDTO.getApplicationId());
        setCacheHit(responsePublisherDTO.getCacheHit());
        setResponseSize(responsePublisherDTO.getResponseSize());
        setProtocol(responsePublisherDTO.getProtocol());


    }

    public static String getStreamDefinition() {

        return "{" +
                "  'name':'" +
                DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamName() + "'," +
                "  'version':'" +
                DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamVersion() + "'," +
                "  'nickName': 'API Manager Response Data'," +
                "  'description': 'Response Data'," +
                "  'metaData':[" +
                "          {'name':'clientType','type':'STRING'}" +
                "  ]," +
                "  'payloadData':[" +
                "          {'name':'consumerKey','type':'STRING'}," +
                "          {'name':'context','type':'STRING'}," +
                "          {'name':'api_version','type':'STRING'}," +
                "          {'name':'api','type':'STRING'}," +
                "          {'name':'resourcePath','type':'STRING'}," +
                "          {'name':'method','type':'STRING'}," +
                "          {'name':'version','type':'STRING'}," +
                "          {'name':'response','type':'INT'}," +
                "          {'name':'responseTime','type':'LONG'}," +
                "          {'name':'serviceTime','type':'LONG'}," +
                "          {'name':'backendTime','type':'LONG'}," +
                "          {'name':'username','type':'STRING'}," +
                "          {'name':'eventTime','type':'LONG'}," +
                "          {'name':'tenantDomain','type':'STRING'}," +
                "          {'name':'hostName','type':'STRING'}," +
                "          {'name':'apiPublisher','type':'STRING'}," +
                "          {'name':'applicationName','type':'STRING'}," +
                "          {'name':'applicationId','type':'STRING'}," +
                "          {'name':'cacheHit','type':'BOOL'}," +
                "          {'name':'responseSize','type':'LONG'}," +
                "          {'name':'protocol','type':'STRING'}," +
                "          {'name':'customValue','type':'STRING'}" +
                "  ]" +

                "}";
    }

    public String getCustomValue() {
        // Create the logic to get the custom value, you can create your own response DTO and include ur logic there
        String sampleVal = "THIS IS MY CUSTOM VALUE - Nadeesha";
        return sampleVal;
    }

    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }

    public Object createPayload() {
        return new Object[]{getConsumerKey(), getContext(), getApi_version(), getApi(),
                getResourcePath(), getMethod(),
                getVersion(), getResponse(), getResponseTime(), getServiceTime(), getBackendTime(), getUsername(),
                getEventTime(), getTenantDomain(), getHostName(),
                getApiPublisher(), getApplicationName(), getApplicationId(), getCacheHit(),
                getResponseSize(), getProtocol(), getCustomValue()};
    }
}
