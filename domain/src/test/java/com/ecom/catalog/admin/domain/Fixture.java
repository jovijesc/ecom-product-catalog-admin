package com.ecom.catalog.admin.domain;

import com.ecom.catalog.admin.domain.category.Category;
import com.ecom.catalog.admin.domain.category.CategoryID;
import com.ecom.catalog.admin.domain.product.*;
import com.ecom.catalog.admin.domain.utils.IdUtils;
import com.github.javafaker.Faker;

import java.util.Set;

public class Fixture {

    private static final Faker FAKER = new Faker();

    public static String fixedChecksum() {
        return "80fie78gui";
    }

    public static String randomChecksum() {
        return FAKER.random().hex();
    }


    public static String checksum() {
        return "80fie78gui";
    }

    public static final class Categories {
        private static final Category ELETRONICOS =
                Category.newCategory("Eletrônicos", "Categoria de Eletrônicos", true);

        private static final Category INFORMATICA =
                Category.newCategory("Informática", "Categoria de Informática", true);

        public static Category eletronicos() {
            return ELETRONICOS.clone();
        }

        public static Category informatica() {
            return INFORMATICA.clone();
        }
    }

    public static final class Stores {

        private static final Store LOJA_ELETROMANIA =
                Store.with(IdUtils.uuid(), "Loja Eletromania");

        public static Store lojaEletromania() {
            return LOJA_ELETROMANIA.clone();
        }
    }

    public static final class ProductImages {

        private static final ProductImage IMG01 = ProductImage.with("checksum123", new byte[]{10,20,30,40,50},"image01.jpg", "/image", true);
        private static final ProductImage IMG02 = ProductImage.with("checksum456", new byte[]{11,21,31,41,51},"image02.jpg", "/image", true);

        private static final ProductImage IMG03 = ProductImage.with("checksum789", new byte[]{12,22,32,42,52},"image03.jpg", "/image", true);

        public static ProductImage img01() {
            return ProductImage.with(IMG01);
        }

        public static ProductImage img02() {
            return ProductImage.with(IMG02);
        }

        public static ProductImage img03() {
            return ProductImage.with(IMG03);
        }

        public static byte[] content() {
            return FAKER.options().option(
                    new byte[]{10,20,30,40,50},
                    new byte[]{11,21,31,41,51},
                    new byte[]{12,22,32,42,52},
                    new byte[]{13,23,33,43,53}
            );
        }

    }

    public static final class Products {

        public static String name() {
            return FAKER.commerce().productName();
        }

        public static Money priceMoney() {
            return Money.with(Double.parseDouble(FAKER.commerce().price()));
        }

        public static Double price() {
            return Double.parseDouble(FAKER.commerce().price());
        }

        public static ProductStatus status() {
            return FAKER.options().option(ProductStatus.values());
        }

        public static Integer stock() {
            return FAKER.random().nextInt(5, 100);
        }

        private static final Product CELULAR = Product.newProduct(
                "Celular",
                description(),
                ProductStatus.ACTIVE,
                priceMoney(),
                stock(),
                Categories.eletronicos().getId(),
                Stores.lojaEletromania(),
                Set.of(ProductImages.img01(), ProductImages.img02()));

        private static final Product NOTEBOOK = Product.newProduct(
                "Notebook",
                description(),
                ProductStatus.ACTIVE,
                priceMoney(),
                stock(),
                Categories.informatica().getId(),
                Stores.lojaEletromania(),
                Set.of(ProductImages.img01(), ProductImages.img03()));

        public static Product celular() {
            return Product.with(CELULAR);
        }

        public static Product notebook() {
            return Product.with(NOTEBOOK);
        }


        public static String description() {
            return FAKER.options().option(
                    """
                            Tela Super Retina XDR de 6,1 polegadas
                            Sistema de câmera avançado para fotos melhores em qualquer luz
                            Modo Cinema, agora em 4K Dolby Vision até 30 qps
                            Modo Ação para vídeos em movimento com mais estabilidade
                            Tecnologia de segurança — Detecção de Acidente, que liga para a emergência se você não puder
                            """,
                            """
                            Tela Super Retina XDR de 6,1 polegadas
                             Sistema de câmera avançado para fotos melhores em qualquer luz
                             Modo Cinema, agora em 4K Dolby Vision até 30 qps
                             Modo Ação para vídeos em movimento com mais estabilidade
                             Tecnologia de segurança — Detecção de Acidente, que liga para a emergência se você não puder
                            """
            );
        }
    }
}
