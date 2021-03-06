package com.codesoom.assignment.application;

import com.codesoom.assignment.ProductNotFountException;
import com.codesoom.assignment.domain.Product;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductService 클래스")
class ProductServiceTest {
    final Long NOT_EXIST_ID = 100L;
    final String NAME = "My Toy";
    final String MAKER = "My Home";
    final Long PRICE = 5000L;
    final String IMAGE_URL = "https://cdn.pixabay.com/photo/2016/10/01/20/54/mouse-1708347_1280.jpg";

    final String UPDATE_NAME = "My New Toy";
    final String UPDATE_MAKER = "My New Home";
    final Long UPDATE_PRICE = 7000L;
    final String UPDATE_IMAGE_URL = "https://cdn.pixabay.com/photo/2016/10/01/20/54/mouse-1708347_12801.jpg";

    @Autowired
    ProductService productService;

    @BeforeEach
    void setUp() {
        productService.clearData();
    }

    //subject
    Product createProduct() {
        Product product = Product.builder()
                .name(NAME)
                .maker(MAKER)
                .price(PRICE)
                .imageUrl(IMAGE_URL)
                .build();
        return productService.create(product);
    }

    //subject
    void verifyFindProduct(Product product) {
        assertThat(product.getName()).isEqualTo(NAME);
        assertThat(product.getMaker()).isEqualTo(MAKER);
        assertThat(product.getPrice()).isEqualTo(PRICE);
        assertThat(product.getImageUrl()).isEqualTo(IMAGE_URL);
    }

    void verifyProducts(Product source, Product expected) {
        assertThat(source.getName()).isEqualTo(expected.getName());
        assertThat(source.getMaker()).isEqualTo(expected.getMaker());
        assertThat(source.getPrice()).isEqualTo(expected.getPrice());
        assertThat(source.getImageUrl()).isEqualTo(expected.getImageUrl());
    }


    @Nested
    @DisplayName("create()")
    class Describe_create {
        Product givenProduct;

        @BeforeEach
        void setUp() {
            givenProduct = Product.builder()
                    .name(NAME)
                    .maker(MAKER)
                    .price(PRICE)
                    .imageUrl(IMAGE_URL)
                    .build();
        }

        Product subject() {
            return productService.create(givenProduct);
        }

        @DisplayName("생성된 product를 반환한다")
        @Test
        void it_returns_created_product() {
            Product product = subject();

            verifyProducts(product, givenProduct);
        }
    }

    @Nested
    @DisplayName("find()")
    class Describe_find {
        @Nested
        @DisplayName("존재하는 product id가 주어진다면")
        class Context_exist_product_id {
            Long givenProductId;
            Product givenProduct;

            @BeforeEach
            void setUp() {
                givenProduct = createProduct();
                givenProductId = givenProduct.getId();
            }

            @DisplayName("주어진 id와 일치하는 product를 반환한다")
            @Test
            void it_returns_product() {
                Product product = productService.find(givenProductId);
                verifyFindProduct(product);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 product id가 주어진다면")
        class Context_not_exist_product_id {
            Long givenProductId = NOT_EXIST_ID;

            @DisplayName("product를 찾을 수 없다는 예외를 던진다")
            @Test
            void it_returns_exception() {
                assertThrows(ProductNotFountException.class, () -> productService.find(givenProductId));
            }
        }
    }

    @Nested
    @DisplayName("findAll()")
    class Describe_findAll {
        @Nested
        @DisplayName("product가 존재한다면")
        class Context_product_exist {
            @BeforeEach
            void setUp() {
                createProduct();
            }

            @Test
            @DisplayName("product 가 포함된 리스트를 반환한다")
            void it_return_product_list() {
                List<Product> list = productService.findAll();
                assertThat(list).isNotEmpty();
            }
        }

        @Nested
        @DisplayName("product가 존재하지 않는다면")
        class Context_product_not_exist {

            @Test
            @DisplayName("빈 리스트를 반환한다")
            void it_return_empty_list() {
                List<Product> list = productService.findAll();
                assertThat(list).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("update()")
    class Describe_update {
        @Nested
        @DisplayName("존재하는 product id와 수정한 product가 주어진다면")
        class Context_exist_product_id {
            Long givenProductId;
            Product source;

            @BeforeEach
            void setUp() {
                Product givenProduct = createProduct();
                givenProductId = givenProduct.getId();

                source = Product.builder()
                        .name(UPDATE_NAME)
                        .maker(UPDATE_MAKER)
                        .price(UPDATE_PRICE)
                        .imageUrl(UPDATE_IMAGE_URL)
                        .build();
            }

            @DisplayName("수정된 product를 반환한다")
            @Test
            void it_returns_product() {
                Product product = productService.update(givenProductId, source);

                assertThat(product.getId()).isEqualTo(givenProductId);
                verifyProducts(product, source);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 product id와 수정한 product가 주어진다면")
        class Context_not_exist_product_id {
            Long givenProductId;
            Product source;

            @BeforeEach
            void setUp() {
                givenProductId = NOT_EXIST_ID;
                source = Product.builder()
                        .name(UPDATE_NAME)
                        .maker(UPDATE_MAKER)
                        .price(UPDATE_PRICE)
                        .imageUrl(UPDATE_IMAGE_URL)
                        .build();
            }

            @DisplayName("product를 찾을 수 없다는 예외를 던진다")
            @Test
            void it_returns_exception() {
                assertThrows(ProductNotFountException.class, () -> productService.update(NOT_EXIST_ID, source));
            }
        }
    }


    @Nested
    @DisplayName("delete()")
    class Describe_delete {
        @Nested
        @DisplayName("존재하는 product id가 주어진다면")
        class Context_exist_product_id {
            Long givenProductId;

            @BeforeEach
            void setUp() {
                Product givenProduct = createProduct();
                givenProductId = givenProduct.getId();
            }

            @DisplayName("주어진 id와 일치하는 product를 삭제한다")
            @Test
            void it_delete_product() {
                productService.delete(givenProductId);
                assertThrows(ProductNotFountException.class, () -> productService.find(givenProductId));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 product id가 주어진다면")
        class Context_not_exist_product_id {
            Long givenProductId = NOT_EXIST_ID;

            @DisplayName("product를 찾을 수 없다는 예외를 던진다")
            @Test
            void it_returns_exception() {
                assertThrows(ProductNotFountException.class, () -> productService.delete(givenProductId));
            }
        }
    }
}
