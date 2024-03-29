package tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import shared.models.User;

/**
 * Tests the User class
 * -Tests constructing a User with a UUID and a String username
 * 		this also tests the getUuid method
 * -Tests constructing a User with and without a whitespace in the username
 * 		this also tests the getName and setName methods
 * -Tests the equals method
 * 
 * @author rcha
 *
 */
public class UserTest {

    @Test
    public void getUidTest_nonRandomUUID() {
        /*
         * Tests the construction of a user with a specific UUID.
         * Tests the getUuid method
         */

        User user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", "Nick");
        assertEquals("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", user.getUid()
                .toString());
    }

    @Test
    public void getNameTest_valid() {
        /*
         * Tests if it is possible to create users with white space in the usernames
         * Tests the getName and setName methods
         * 
         */

        User user = new User("Nick");
        assertEquals("Nick", user.getName());
        assertNotSame("nick", user.getName()); // Capital makes difference

        user = new User("Nicholas Lastname");
        assertEquals("Nicholas Lastname", user.getName());

        // Tests the other constructor too.
        user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", "Nick");
        assertEquals("Nick", user.getName());
        assertNotSame("nick", user.getName()); // Capital makes difference

        user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b",
                "Nicholas Lastname");
        assertEquals("Nicholas Lastname", user.getName());

        // Tests setName
        user.setName("Nick");
        assertEquals("Nick", user.getName());
        user.setName("Nicholas Lastname");
        assertEquals("Nicholas Lastname", user.getName());

    }

    @Test
    public void equalsTest() {
        /*
         * Creates a user with random UUID, then creates a user using this UUID,
         * and see if they are equal. Also, tests two non equal objects
         */

        // ** Same UUID, same names
        User user1 = new User("Nick");
        User user2 = new User(user1.getUid().toString(), "Nick");
        assertTrue(user1.equals(user2));

        // ** Same UUID, different names
        user2 = new User(user1.getUid().toString(), "Nock");
        assertTrue(user1.equals(user2)); // Equals depends only on UUID

        /*
         *  ** Different UUID, same names Exits only when user2 UUID is different
         * than user1 UUID, actually the chance of random generated UUID are
         * equal is very very low.
         */
        while (user2.getUid().equals(user1.getUid())) {
            user2 = new User("Nick");
        }

        // Must be different, since have different UUID.
        assertFalse(user1.equals(user2));

        // ** Different UUID, different names
        user2.setName("Nock");
        assertFalse(user1.equals(user2));
    }
}
