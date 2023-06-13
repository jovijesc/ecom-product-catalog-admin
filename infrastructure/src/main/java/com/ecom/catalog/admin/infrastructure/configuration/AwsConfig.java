package com.ecom.catalog.admin.infrastructure.configuration;

import com.ecom.catalog.admin.infrastructure.configuration.properties.AwsProperties;
import com.ecom.catalog.admin.infrastructure.configuration.properties.AwsS3Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
@Profile({"!development & !test-integration & !test-e2e"})
public class AwsConfig {

    @Bean
    @ConfigurationProperties("aws")
    public AwsProperties awsProperties() {
        return new AwsProperties();
    }

    @Bean
    @ConfigurationProperties("aws.s3")
    public AwsS3Properties awss3Properties() {
        return new AwsS3Properties();
    }

    @Bean
    public S3AsyncClient s3AsyncClient(final AwsProperties props) {
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(staticCredentialsProvider(props.getAccessKey(), props.getSecretKey()))
                //.endpointOverride(URI.create(LOCAL_STACK_ENDPOINT))
                .region(region(props.getRegion()))
                .forcePathStyle(true)
                .build();
    }

    private StaticCredentialsProvider staticCredentialsProvider(final String accessKey, final String secretKey) {
        AwsCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return StaticCredentialsProvider.create(awsBasicCredentials);
    }

    private Region region(String aRegion) {
        return aRegion != null ? Region.of(aRegion) : Region.US_EAST_1;
    }

}
