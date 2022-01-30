package test.sdc.resilience4j.reactor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

record CatFactDto(
        @JsonProperty("_id") String id,
        String text,
        Instant updatedAt,
        Boolean deleted,
        String source,
        Integer sentCount
) {
}
