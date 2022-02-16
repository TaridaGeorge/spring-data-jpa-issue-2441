package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void shouldDetectInstitutesIdsAsAnAlias() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query = """
                SELECT 
                	CAST(('{' || string_agg(distinct array_to_string(c.institutes_ids, ','), ',') || '}') AS bigint[]) as institutesIds\s
                FROM
                	city c
                """;
        var getFunctionAliasesMethod = QueryUtils.class.getDeclaredMethod("getFunctionAliases", String.class);
        getFunctionAliasesMethod.setAccessible(true);
        var result = (Set<String>) getFunctionAliasesMethod.invoke(null, query);
        assertTrue(result.contains("institutesIds"));
    }

    @Test
    void shouldOrderByDDTable() {
        String query =
                """
                            SELECT dd.institutesIds FROM (
                                SELECT
                                    CAST(('{' || string_agg(distinct array_to_string(c.institutes_ids, ','), ',') || '}') AS bigint[]) as institutesIds
                                FROM
                                    city c
                            ) dd
                        """;

        var result = QueryUtils.applySorting(query, Sort.by(new Sort.Order(Sort.Direction.ASC, "institutesIds")));
        assertTrue(result.contains("order by dd.institutesIds"));
    }

}
