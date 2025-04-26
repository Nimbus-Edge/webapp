package com.cloud.webapp.service.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;


@Service
public class CloudWatchService {

    private final CloudWatchClient cloudWatch;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CloudWatchService() {
        this.cloudWatch = CloudWatchClient.create();
    }

    public void incrementApiCall(String apiName) {
        MetricDatum datum = MetricDatum.builder()
                .metricName("ApiCallCount")
                .unit(StandardUnit.COUNT)
                .value(1.0)
                .dimensions(Dimension.builder()
                        .name("APIName")
                        .value(apiName)
                        .build())
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace("YourApplicationNamespace")
                .metricData(datum)
                .build();

        cloudWatch.putMetricData(request);
        logger.info("Metric data sent for API call: {}", apiName);
    }

    public void recordApiResponseTime(String apiName, long timeInMillis) {
        MetricDatum datum = MetricDatum.builder()
                .metricName("ApiResponseTime")
                .unit(StandardUnit.MILLISECONDS)
                .value((double) timeInMillis)
                .dimensions(Dimension.builder()
                        .name("APIName")
                        .value(apiName)
                        .build())
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace("YourApplicationNamespace")
                .metricData(datum)
                .build();

        cloudWatch.putMetricData(request);
        logger.info("Response time metric sent for API call: {}. Time: {} ms", apiName, timeInMillis);
    }

    public void recordDatabaseQueryTime(String queryName, long timeInMillis) {
        MetricDatum datum = MetricDatum.builder()
                .metricName("DatabaseQueryTime")
                .unit(StandardUnit.MILLISECONDS)
                .value((double) timeInMillis)
                .dimensions(Dimension.builder()
                        .name("QueryName")
                        .value(queryName)
                        .build())
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace("YourApplicationNamespace")
                .metricData(datum)
                .build();

        cloudWatch.putMetricData(request);
        logger.info("Database query time metric sent for query: {}. Time: {} ms", queryName, timeInMillis);
    }
}
