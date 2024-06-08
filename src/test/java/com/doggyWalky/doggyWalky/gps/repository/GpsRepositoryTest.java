package com.doggyWalky.doggyWalky.gps.repository;

import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
        "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})
@DirtiesContext
class GpsRepositoryTest {

    @Autowired
    private GpsRepository gpsRepository;

    @DisplayName("인덱싱 적용 테스트")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void test() {
        long startTime = System.currentTimeMillis();
        Page<GpsResponseDto> gpsListByJobPostId = gpsRepository.findGpsListByJobPostId(33L, PageRequest.of(10, 100));
        long endTime = System.currentTimeMillis();
        assertThat(gpsListByJobPostId.getContent().size()).isEqualTo(100);

        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " ms");
    }

}