package app.security.daos;

import app.config.HibernateConfig;
import app.exceptions.ValidationException;
import app.populators.RolePopulator;
import app.populators.UserPopulator;
import app.security.entities.Role;
import app.security.entities.User;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final UserDAO userDAO = UserDAO.getInstance(emf);
    private static final RoleDAO roleDAO = RoleDAO.getInstance(emf);
    private static List<User> users = UserPopulator.populate();
    private static List<Role> roles = RolePopulator.populate();

    @BeforeEach
    void setup()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            // clears previous data
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role ").executeUpdate();

            // persists all users in the list
            users.forEach(userDAO::create);
            // persists all the roles in the list
            roles.forEach(roleDAO::createRole);
        }
    }

    @Test
    void getInstance()
    {
        UserDAO instanceTest = UserDAO.getInstance(emf);
        assertNotNull(instanceTest);
        assertEquals(instanceTest, userDAO);
    }

    @Test
    void create()
    {
        User expected = new User("Test123", "PasswordTest123");
        User actual = userDAO.create(expected);

        assertEquals(expected, actual);
    }

    @Test
    void read()
    {
        String username = "Test123";
        User expected = new User(username, "PasswordTest123");
        userDAO.create(expected);

        User actual = userDAO.read(username);

        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    void getVerifiedUser() throws ValidationException
    {
        String username = users.get(0).getUsername();
        String password = "Password123"; //password has to be plain text to be tested

        UserDTO userDTO = userDAO.getVerifiedUser(username, password);

        assertNotNull(userDTO);

        assertEquals(userDTO.getUsername(), username);
    }

    @Test
    void readAll()
    {
        List<User> expected = userDAO.readAll();

        assertEquals(expected.size(), 2);
    }

    @Test
    void update()
    {
        User expected = users.get(0);

        expected.addRole(new Role("admin"));

        expected = userDAO.update(expected);

        assertEquals(expected.getRoles().size(), 2);
    }

    @Test
    void delete()
    {
    }
}