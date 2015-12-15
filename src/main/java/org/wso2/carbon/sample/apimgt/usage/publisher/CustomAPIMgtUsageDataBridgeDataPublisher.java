package org.wso2.carbon.sample.apimgt.usage.publisher;

/**
 * Created by NADEESHA on 12/14/2015.
 */

import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataBridgeDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.APIMgtUsageDataPublisher;
import org.wso2.carbon.apimgt.usage.publisher.DataPublisherUtil;
import org.wso2.carbon.apimgt.usage.publisher.dto.FaultPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.RequestPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.ResponsePublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.ThrottlePublisherDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeFaultPublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeRequestPublisherDTO;
import org.wso2.carbon.sample.apimgt.usage.publisher.dto.CustomDataBridgeResponsePublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.dto.DataBridgeThrottlePublisherDTO;
import org.wso2.carbon.apimgt.usage.publisher.internal.DataPublisherAlreadyExistsException;
import org.wso2.carbon.apimgt.usage.publisher.internal.UsageComponent;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.agent.thrift.lb.DataPublisherHolder;
import org.wso2.carbon.databridge.agent.thrift.lb.LoadBalancingDataPublisher;
import org.wso2.carbon.databridge.agent.thrift.lb.ReceiverGroup;
import org.wso2.carbon.databridge.commons.exception.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class CustomAPIMgtUsageDataBridgeDataPublisher implements APIMgtUsageDataPublisher {

    private static final Log log = LogFactory.getLog(APIMgtUsageDataBridgeDataPublisher.class);


    private LoadBalancingDataPublisher dataPublisher;

    private static LoadBalancingDataPublisher getDataPublisher()
            throws AgentException, MalformedURLException, AuthenticationException,
            TransportException {

        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();

        //Get LoadBalancingDataPublisher which has been registered for the tenant.
        LoadBalancingDataPublisher loadBalancingDataPublisher = UsageComponent.getDataPublisher(tenantDomain);

        //If a LoadBalancingDataPublisher had not been registered for the tenant.
        if (loadBalancingDataPublisher == null) {

            List<String> receiverGroups = org.wso2.carbon.databridge.agent.thrift.util.DataPublisherUtil.
                    getReceiverGroups(DataPublisherUtil.getApiManagerAnalyticsConfiguration().getBamServerUrlGroups());

            String serverUser = DataPublisherUtil.getApiManagerAnalyticsConfiguration().getBamServerUser();
            String serverPassword = DataPublisherUtil.getApiManagerAnalyticsConfiguration().getBamServerPassword();
            List<ReceiverGroup> allReceiverGroups = new ArrayList<ReceiverGroup>();

            for (String receiverGroupString : receiverGroups) {
                String[] serverURLs = receiverGroupString.split(",");
                List<DataPublisherHolder> dataPublisherHolders = new ArrayList<DataPublisherHolder>();

                for (int i = 0; i < serverURLs.length; i++) {
                    String serverURL = serverURLs[i];
                    DataPublisherHolder dataPublisherHolder =
                            new DataPublisherHolder(null, serverURL, serverUser, serverPassword);
                    dataPublisherHolders.add(dataPublisherHolder);
                }

                ReceiverGroup receiverGroup = new ReceiverGroup((ArrayList) dataPublisherHolders);
                allReceiverGroups.add(receiverGroup);
            }

            //Create new LoadBalancingDataPublisher for the tenant.
            loadBalancingDataPublisher = new LoadBalancingDataPublisher((ArrayList) allReceiverGroups);
            try {
                //Add created LoadBalancingDataPublisher.
                UsageComponent.addDataPublisher(tenantDomain, loadBalancingDataPublisher);
            } catch (DataPublisherAlreadyExistsException e) {
                log.warn("Attempting to register a data publisher for the tenant " + tenantDomain +
                        " when one already exists. Returning existing data publisher");
                return UsageComponent.getDataPublisher(tenantDomain);
            }
        }

        return loadBalancingDataPublisher;
    }

    public void init() {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing APIMgtUsageDataBridgeDataPublisher");
            }

            this.dataPublisher = getDataPublisher();

            //If Request Stream Definition does not exist.
            if (!dataPublisher.isStreamDefinitionAdded(DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getRequestStreamName(), DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getRequestStreamVersion())) {

                //Get Request Stream Definition
                String requestStreamDefinition = DataBridgeRequestPublisherDTO.getStreamDefinition();

                //Add Request Stream Definition.
                dataPublisher.addStreamDefinition(requestStreamDefinition,
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getRequestStreamName(),
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getRequestStreamVersion());
            }

            //If Response Stream Definition does not exist.
            if (!dataPublisher.isStreamDefinitionAdded(DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getResponseStreamName(), DataPublisherUtil.getApiManagerAnalyticsConfiguration()
                    .getResponseStreamVersion())) {

                //Get Response Stream Definition.
                String responseStreamDefinition = CustomDataBridgeResponsePublisherDTO.getStreamDefinition();

                //Add Response Stream Definition.
                dataPublisher.addStreamDefinition(responseStreamDefinition,
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamName(),
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamVersion());

            }

            //If Fault Stream Definition does not exist.
            if (!dataPublisher.isStreamDefinitionAdded(DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getFaultStreamName(), DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getFaultStreamVersion())) {

                //Get Fault Stream Definition
                String faultStreamDefinition = DataBridgeFaultPublisherDTO.getStreamDefinition();

                //Add Fault Stream Definition;
                dataPublisher.addStreamDefinition(faultStreamDefinition,
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getFaultStreamName(),
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getFaultStreamVersion());
            }

            //If Throttle Stream Definition does not exist.
            if (!dataPublisher.isStreamDefinitionAdded(DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getThrottleStreamName(), DataPublisherUtil.getApiManagerAnalyticsConfiguration().
                    getThrottleStreamVersion())) {

                //Get Throttle Stream Definition
                String throttleStreamDefinition = DataBridgeThrottlePublisherDTO.getStreamDefinition();

                //Add Throttle Stream Definition;
                dataPublisher.addStreamDefinition(throttleStreamDefinition,
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getThrottleStreamName(),
                        DataPublisherUtil.getApiManagerAnalyticsConfiguration().getThrottleStreamVersion());
            }
        } catch (Exception e) {
            log.error("Error initializing APIMgtUsageDataBridgeDataPublisher", e);
        }
    }

    public void publishEvent(RequestPublisherDTO requestPublisherDTO) {
        DataBridgeRequestPublisherDTO dataBridgeRequestPublisherDTO = new DataBridgeRequestPublisherDTO(requestPublisherDTO);
        try {
            //Publish Request Data
            dataPublisher.publish(DataPublisherUtil.getApiManagerAnalyticsConfiguration().getRequestStreamName(),
                    DataPublisherUtil.getApiManagerAnalyticsConfiguration().getRequestStreamVersion(),
                    System.currentTimeMillis(), new Object[]{"external"}, null,
                    (Object[]) dataBridgeRequestPublisherDTO.createPayload());
        } catch (AgentException e) {
            log.error("Error while publishing Request event", e);
        }
    }

    public void publishEvent(ResponsePublisherDTO customResponsePublisherDTO) {
        CustomDataBridgeResponsePublisherDTO dataBridgeResponsePublisherDTO = new CustomDataBridgeResponsePublisherDTO(customResponsePublisherDTO);
        try {
            DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamName();
            DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamVersion();
            dataBridgeResponsePublisherDTO.createPayload();
            //Publish Response Data
            dataPublisher.publish(DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamName(),
                    DataPublisherUtil.getApiManagerAnalyticsConfiguration().getResponseStreamVersion(),
                    System.currentTimeMillis(), new Object[]{"external"}, null,
                    (Object[]) dataBridgeResponsePublisherDTO.createPayload());

        } catch (AgentException e) {
            log.error("Error while publishing Response event", e);
        }
    }

    public void publishEvent(FaultPublisherDTO faultPublisherDTO) {
        DataBridgeFaultPublisherDTO dataBridgeFaultPublisherDTO = new DataBridgeFaultPublisherDTO(faultPublisherDTO);
        try {
            //Publish Fault Data
            dataPublisher.publish(DataPublisherUtil.getApiManagerAnalyticsConfiguration().getFaultStreamName(),
                    DataPublisherUtil.getApiManagerAnalyticsConfiguration().getFaultStreamVersion(),
                    System.currentTimeMillis(), new Object[]{"external"}, null,
                    (Object[]) dataBridgeFaultPublisherDTO.createPayload());

        } catch (AgentException e) {
            log.error("Error while publishing Fault event", e);
        }
    }

    public void publishEvent(ThrottlePublisherDTO throttPublisherDTO) {
        DataBridgeThrottlePublisherDTO dataBridgeThrottlePublisherDTO = new
                DataBridgeThrottlePublisherDTO(throttPublisherDTO);
        try {
            //Publish Throttle data
            dataPublisher.publish(DataPublisherUtil.getApiManagerAnalyticsConfiguration().getThrottleStreamName(),
                    DataPublisherUtil.getApiManagerAnalyticsConfiguration().getThrottleStreamVersion(),
                    System.currentTimeMillis(), new Object[]{"external"}, null,
                    (Object[]) dataBridgeThrottlePublisherDTO.createPayload());

        } catch (AgentException e) {
            log.error("Error while publishing Throttle exceed event", e);
        }
    }
}
