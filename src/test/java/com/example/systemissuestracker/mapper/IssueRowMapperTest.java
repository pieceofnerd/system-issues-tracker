package com.example.systemissuestracker.mapper;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class IssueRowMapperTest {

    private IssueRowMapper mapper;
    private Issue issue;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mapper = new IssueRowMapper();
        now = LocalDateTime.now().withNano(0);
        issue = new Issue(
                "AD-2",
                "Sample Description",
                "AD-1",
                IssueStatus.OPEN,
                now.minusDays(1),
                now
        );
    }

    @Test
    void mapIssueToRow() {
        List<Object> row = mapper.issueToRow(issue);

        assertEquals(6, row.size());
        assertEquals("AD-2", row.get(0));
        assertEquals("Sample Description", row.get(1));
        assertEquals("AD-1", row.get(2));
        assertEquals("OPEN", row.get(3));
        assertEquals(now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), row.get(4));
        assertEquals(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), row.get(5));
    }

    @Test
    void mapIssueToRowHandlesNullParentId() {
        Issue issueWithoutParent = issue.withParentId(null);
        List<Object> row = mapper.issueToRow(issueWithoutParent);
        assertEquals("", row.get(2));
    }

    @Test
    void mapRowToIssue() {
        List<Object> row = Arrays.asList(
                "AD-2",
                "Sample Description",
                "AD-1",
                "OPEN",
                now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        Issue issue = mapper.rowToIssue(row);

        assertEquals(this.issue, issue);
    }

    @Test
    void mapRowToIssueHandlesEmptyAndNullParentId() {
        List<Object> rowWithEmptyParent = Arrays.asList("AD-2", "D", "", "OPEN", now.toString(), now.toString());
        List<Object> rowWithNullParent = Arrays.asList("AD-2", "D", null, "OPEN", now.toString(), now.toString());

        Issue issue1 = mapper.rowToIssue(rowWithEmptyParent);
        Issue issue2 = mapper.rowToIssue(rowWithNullParent);

        assertNull(issue1.getParentId());
        assertNull(issue2.getParentId());
    }
}
