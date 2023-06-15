package com.ecom.catalog.admin.infrastructure.configuration;

import com.ecom.catalog.admin.infrastructure.configuration.properties.aws.AwsS3Properties;
import com.ecom.catalog.admin.infrastructure.configuration.properties.storage.StorageProperties;
import com.ecom.catalog.admin.infrastructure.services.StorageService;
import com.ecom.catalog.admin.infrastructure.services.impl.AwsS3Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(value = "storage.product-catalog")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean( name = "storageService")
    @ConditionalOnMissingBean
    public StorageService awsStorageAPI(
            final AwsS3Properties props,
            final S3Client client
    ) {
        return new AwsS3Service(props.getBucket(), client);
    }

}
