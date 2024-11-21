package com.educationCenter.Articler_Database_Handler;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ArticleDatabaseHelperTest {

    private ArticleDatabaseHelper dbHelper;

    @BeforeEach
    void setUp() {
        dbHelper = new ArticleDatabaseHelper();
        assertDoesNotThrow(() -> dbHelper.articleConnectToDatabase()); // Ensure connection is established
    }

    @AfterEach
    void tearDown() throws Exception {
        assertDoesNotThrow(() -> dbHelper.articleCloseConnection()); // Close connection after each test
    }

    @Test
    void articleConnectToDatabase() {
        assertDoesNotThrow(() -> dbHelper.articleConnectToDatabase());
    }

    @Test
    void specialAccessGroupsTable() {
        assertDoesNotThrow(() -> dbHelper.specialAccessGroupsTable());
    }

    @Test
    void addNewGroup() {
        assertDoesNotThrow(() -> dbHelper.addNewGroup("Test Group", 1));
    }

    @Test
    void deleteGroup() {
        assertDoesNotThrow(() -> dbHelper.addNewGroup("Delete Group", 2));
        assertDoesNotThrow(() -> dbHelper.DeleteGroup("Delete Group"));
    }

    @Test
    void deleteUserAccessGroup() {
        assertDoesNotThrow(() -> dbHelper.addNewGroup("User Access Group", 3));
        assertDoesNotThrow(() -> dbHelper.DeleteUserAcessGroup("User Access Group", 3));
    }

    @Test
    void getGroupAdmin() {
        assertDoesNotThrow(() -> dbHelper.addNewGroup("Admin Group", 4));
        assertDoesNotThrow(() -> {
            int[] admins = dbHelper.getGroupAdmin("Admin Group");
            assertEquals(4, admins[0]);
        });
    }

    @Test
    void addSpecialAccess() {
        assertDoesNotThrow(() -> dbHelper.addSpecialAccess(1, "Special Group", "read"));
    }

    @Test
    void backupToFile() {
        assertDoesNotThrow(() -> dbHelper.backupToFile("backup_test"));
    }

    @Test
    void backupByGrouping() {
        assertDoesNotThrow(() -> dbHelper.backupByGrouping("backup_group_test", "test_group"));
    }


    @Test
    void loadFromFile() {
        assertDoesNotThrow(() -> dbHelper.loadFromFile("./backups/backup_test.zip"));
    }

    @Test
    void deleteAllFiles() {
        assertDoesNotThrow(() -> ArticleDatabaseHelper.deleteAllFiles("./backups/"));
    }

    @Test
    void isDatabaseEmpty() {
        assertDoesNotThrow(() -> (dbHelper.isDatabaseEmpty()));
    }

    @Test
    void createArticle() {
        assertDoesNotThrow(() -> dbHelper.createArticle(
                "Test Title", "Test Body", "Test Author", "Test Abstract",
                "Test Keywords", "Test References", "Beginner", "Test Group"
        ));
    }

    @Test
    void checkSensByKey() {
        assertDoesNotThrow(() -> assertEquals(0, dbHelper.checkSensByKey(999))); // Expect 0 for non-existent key
    }

    @Test
    void deleteArticleByKey() {
        assertDoesNotThrow(() -> dbHelper.deleteArticleByKey(999)); // Expect no exception for non-existent key
    }

    @Test
    void editArticleByKey() {
        assertDoesNotThrow(() -> dbHelper.editArticleByKey(1, "Edited Title", "Edited Body", "level", "author", "keywords", "grouping", "links"));
    }

    @Test
    void articleCloseConnection() {
        assertDoesNotThrow(() -> dbHelper.articleCloseConnection());
    }
}
