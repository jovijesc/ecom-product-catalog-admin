package com.ecom.catalog.admin.infrastructure.configuration.properties.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class AwsS3Properties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AwsProperties.class);
    private String bucket;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug(toString());
    }

    @Override
    public String toString() {
        return "AwsS3Properties{" +
                "bucket='" + bucket + '\'' +
                '}';
    }
}
