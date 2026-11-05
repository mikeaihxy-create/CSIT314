package idp.test.us;

import idp.boundary.*;
import idp.database.Database;
import idp.dto.*;
import idp.entity.*;
import idp.enumeration.*;
import idp.session.SessionManager;
import idp.test.IdpTestCase;
import idp.util.Clock;
import idp.util.Messages;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class Us01CreateUserAccountTest extends IdpTestCase {

    @Test
    public void testCreateUserAccount() {
        signIn(admin());
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("newuser");
        page.setEmail("newuser@idp.test");
        page.setPassword("Secret123");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertTrue(result.success());
        assertEquals(Messages.ACCOUNT_CREATED, result.message());
        assertEquals(Messages.ACCOUNT_CREATED, page.getMessage());
        assertNotNull(result.account());
        assertEquals("newuser", result.account().getUsername());
        assertEquals("newuser@idp.test", result.account().getEmail());
        assertEquals(Role.CUSTOMER, result.account().getRole());
        assertEquals(AccountStatus.ACTIVE, result.account().getStatus());
        assertNotEquals("Secret123", result.account().getPasswordHash());
        assertEquals(2, Database.get().accounts().size());
    }

    @Test
    public void testCreateUserAccountFailsWhenActorIsNotUserAdmin() {
        signIn(designer());
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("newuser");
        page.setEmail("newuser@idp.test");
        page.setPassword("Secret123");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.NOT_AUTHORISED, result.message());
        assertNull(result.account());
        assertEquals(1, Database.get().accounts().size());
    }

    @Test
    public void testCreateUserAccountFailsWhenDetailsAreIncomplete() {
        signIn(admin());
        uaCreateAccountPg blank = new uaCreateAccountPg();
        blank.setUsername("");
        blank.setEmail("blank@idp.test");
        blank.setPassword("Secret123");
        blank.setRole("CUSTOMER");
        assertFalse(blank.create().success());
        assertEquals(Messages.CHECK_DETAILS, blank.getMessage());
        uaCreateAccountPg shortName = new uaCreateAccountPg();
        shortName.setUsername("ab");
        shortName.setEmail("short@idp.test");
        shortName.setPassword("Secret123");
        shortName.setRole("CUSTOMER");
        assertFalse(shortName.create().success());
        assertEquals(Messages.CHECK_DETAILS, shortName.getMessage());
        assertEquals(1, Database.get().accounts().size());
    }

    @Test
    public void testCreateUserAccountFailsWhenPasswordIsTooShort() {
        signIn(admin());
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("newuser");
        page.setEmail("newuser@idp.test");
        page.setPassword("12345");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
        assertEquals(1, Database.get().accounts().size());
    }

    @Test
    public void testCreateUserAccountFailsWhenEmailIsMalformed() {
        signIn(admin());
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("newuser");
        page.setEmail("newuser.idp.test");
        page.setPassword("Secret123");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHECK_DETAILS, result.message());
    }

    @Test
    public void testCreateUserAccountFailsWhenUsernameIsTaken() {
        UserAccount admin = admin();
        account("taken", "taken@idp.test", PASSWORD, Role.CUSTOMER);
        signIn(admin);
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("taken");
        page.setEmail("fresh@idp.test");
        page.setPassword("Secret123");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.USERNAME_OR_EMAIL_USED, result.message());
        assertEquals(2, Database.get().accounts().size());
    }

    @Test
    public void testCreateUserAccountFailsWhenEmailIsTaken() {
        UserAccount admin = admin();
        account("first", "shared@idp.test", PASSWORD, Role.CUSTOMER);
        signIn(admin);
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("second");
        page.setEmail("shared@idp.test");
        page.setPassword("Secret123");
        page.setRole("CUSTOMER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.USERNAME_OR_EMAIL_USED, result.message());
    }

    @Test
    public void testCreateUserAccountFailsWhenRoleIsUnknown() {
        signIn(admin());
        uaCreateAccountPg page = new uaCreateAccountPg();
        page.setUsername("newuser");
        page.setEmail("newuser@idp.test");
        page.setPassword("Secret123");
        page.setRole("SUPERUSER");
        CreateAccountResult result = page.create();
        assertFalse(result.success());
        assertEquals(Messages.CHOOSE_ROLE, result.message());
    }
}
