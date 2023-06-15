package com.ecom.catalog.admin.infrastructure.services.impl;

import com.ecom.catalog.admin.domain.product.ProductImage;
import com.ecom.catalog.admin.infrastructure.services.StorageService;
import com.ecom.catalog.admin.infrastructure.utils.HashingUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AwsS3Service implements StorageService {

    private final String bucket;
    private final S3Client client;

    public AwsS3Service(final String bucket, final S3Client client) {
        this.bucket = bucket;
        this.client = client;
    }

    @Override
    public Optional<ProductImage> get(final String name) {
        final var request = GetObjectRequest.builder()
                .bucket(this.bucket)
                .key(name)
                .build();
        return Optional.ofNullable(this.client.getObjectAsBytes(request))
                .map(file -> ProductImage.with(
                        HashingUtils.checksum(file.asByteArray()),
                        file.asByteArray(),
                        name,
                        name,
                        true));
    }

    @Override
    public void store(final Set<ProductImage> images) {
        List<CompletableFuture<PutObjectResponse>> futures = new ArrayList<>();
        for (ProductImage image : images) {
            final var location = image.getLocation();
            final var content = image.getContent();

            final var inputStream = new ByteArrayInputStream(content);

            final var request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(location)
                    .build();

            CompletableFuture<PutObjectResponse> response =
                    CompletableFuture.supplyAsync(() -> this.client.putObject(request,
                            RequestBody.fromInputStream(inputStream, content.length)));
            futures.add(response);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void delete(final String name) {
        final var request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(name)
                .build();
        this.client.deleteObject(request);
    }

    @Override
    public void deleteAll(final Collection<String> names) {
        final var namesToDelete = names.stream()
                .map(name -> ObjectIdentifier.builder()
                        .key(name)
                        .build())
                .collect(Collectors.toList());

        final var filesToDelete = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder()
                        .objects(namesToDelete).build())
                .build();
        this.client.deleteObjects(filesToDelete);
    }

}
