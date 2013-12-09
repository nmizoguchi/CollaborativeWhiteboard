package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import shared.models.User;

public class UserTest {

    // equals(Object)
    // hashCode()

    @Test
    public void getUidTest_nonRandomUUID() {
        /*
         * Tests the construction of a user with a specific UUID.
         */

        User user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", "Nick");
        assertEquals("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", user.getUid()
                .toString());
    }

    @Test
    public void getNameTest_valid() {
        /*
         * Tests if it is possible to create users with single or composite
         * names.
         */

        User user = new User("Nick");
        assertEquals("Nick", user.getName());
        assertNotEquals("nick", user.getName()); // Capital makes difference

        user = new User("Nicholas Lastname");
        assertEquals("Nicholas Lastname", user.getName());

        // Tests the other constructor too.
        user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", "Nick");
        assertEquals("Nick", user.getName());
        assertNotEquals("nick", user.getName()); // Capital makes difference

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
