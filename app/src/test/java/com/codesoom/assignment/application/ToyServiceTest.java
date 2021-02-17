package com.codesoom.assignment.application;

import com.codesoom.assignment.ToyNotFoundException;
import com.codesoom.assignment.domain.Toy;
import com.codesoom.assignment.domain.ToyRepository;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
@DisplayName("ToyService의")
class ToyServiceTest {
    private final Long givenSavedToyId = 1L;
    private final Long givenUnsavedToyId = 100L;
    private final String givenToyName = "장난감 칼";
    private final String givenToyBrand = "코드숨";
    private final int givenToyPrice = 5000;
    private final String givenToyImageUrl = "https://cdn.shopify.com/s/files/1/0940/6942/products/DSC0243_800x.jpg";

    private ToyService toyService;
    private ToyRepository toyRepository;
    private Toy toy;

    @BeforeEach
    void setUp() {
        toyRepository = mock(ToyRepository.class);
        toyService = new ToyService(toyRepository);

        toy = new Toy(givenToyName, givenToyBrand, givenToyPrice, givenToyImageUrl);
        toy.setId(givenSavedToyId);
    }

    private void assertToy(Toy toy) {
        assertThat(toy.getClass()).isEqualTo(Toy.class);
        assertThat(toy.getName()).isEqualTo(givenToyName);
        assertThat(toy.getBrand()).isEqualTo(givenToyBrand);
        assertThat(toy.getPrice()).isEqualTo(givenToyPrice);
        assertThat(toy.getImageUrl()).isEqualTo(givenToyImageUrl);
    }

    @Nested
    @DisplayName("getToys 메서드")
    class Describe_getToys {
        @Nested
        @DisplayName("저장된 toy가 없다면")
        class Context_without_any_toy {
            @BeforeEach
            void setGiven() {
                given(toyRepository.findAll()).willReturn(List.of());
            }

            @Test
            @DisplayName("비어있는 리스트를 리턴한다.")
            void it_return_empty_list() {
                assertThat(toyService.getToys()).isEmpty();

                verify(toyRepository).findAll();
            }
        }

        @Nested
        @DisplayName("저장된 toy가 있다면")
        class Context_with_toy {
            private List<Toy> givenToyList;
            @BeforeEach
            void setSavedToy() {
                givenToyList = new ArrayList<Toy>();
                givenToyList.add(toy);

                given(toyRepository.findAll()).willReturn(givenToyList);
            }

            @Test
            void it_return_toy_list() {
                assertThat(toyService.getToys()).isEqualTo(givenToyList);

                verify(toyRepository).findAll();
            }
        }
    }

    @Nested
    @DisplayName("getToy 메소드는")
    class Describe_getToy {
        private Long givenId;

        @Nested
        @DisplayName("저장된 toy의 id를 가지고 있다면")
        class Context_with_saved_id {
            private Toy found;

            @BeforeEach
            void setSavedId() {
                givenId = givenSavedToyId;

                given(toyRepository.findById(givenId)).willReturn(Optional.of(toy));
            }

            @Test
            @DisplayName("toy를 리턴한다.")
            void it_return_toy() {
                found = toyService.getToy(givenId);

                verify(toyRepository).findById(givenId);

                assertToy(found);
            }
        }

        @Nested
        @DisplayName("저장되지 않은 toy의 id를 가지고 있다면")
        class Context_with_unsaved_id {
            @BeforeEach
            void setUnsavedId() {
                givenId = givenUnsavedToyId;
            }

            @Test
            @DisplayName("toy를 찾을 수 없다는 exception을 던진다.")
            void it_throw_exception() {
                assertThatThrownBy(
                        () -> toyService.getToy(givenId),
                        "toy를 찾을 수 없다는 예외를 던져야 합니다."
                ).isInstanceOf(ToyNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("createTask 메소드는")
    class Describe_createToy {
        private int size;
        private Toy created;

        @Nested
        @DisplayName("toy를 추가하고,")
        class It_create_toy {
            @BeforeEach
            void createToy() {
                size = toyService.getToys().size();

                given(toyRepository.save(any(Toy.class))).will(invocation -> {
                    return invocation.getArgument(0);
                });

                created = toyService.createToy(toy);

                verify(toyRepository).save(any(Toy.class));
            }

            @Test
            @DisplayName("추가된 toy를 리턴한다.")
            void it_return_created_toy() {
                assertToy(created);
            }

            @Test
            @DisplayName("task 리스트의 크기를 1 증가시킨다.")
            void it_count_up_toy_list_size() {
                assertThat(toyService.getToys().size()).isEqualTo(size + 1);
            }
        }
    }

    @Test
    void updateToy() {
    }

    @Test
    void deleteToy() {
    }
}