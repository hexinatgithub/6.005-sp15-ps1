/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Testing strategy for getTimespan
     * 
     * partition on size of the tweets:
     *  size = 2
     *  size > 2
     *  
     * partition on timestamp order of the tweets:
     * 	ordered
     * 	unordered
     * 
     * partition on timestamp duplicate of the tweets:
     * 	all is same
     * 	with some same timestamp
     * 	all different
     * 
     * Testing strategy for getMentionedUsers
     * 
     * partition on tweets size:
     * 	size = 1
     * 	size > 1
     * 
     * partition on username mentioned in text:
     * 	no user mentioned
     * 	only one username mentioned
     * 	some usernames mentioned
     * 
     * partition on mentioned times in text:
     * 	no user is mentioned
     * 	one user mentioned only once
     *  one user mentioned multiple times
     *  some usernames mentioned multiple times, some username only mentioned once
     *  all usernames mentioned multiple times
     * 
     * partition on format in text:
     * 	no user mentioned
     * 	some user mentioned, some format invalid
     * 	all format valid
     * 
     * partition on mentioned user in one tweet:
     * 	no user mentioned in one tweet
     *  one users mentioned only once in one tweet
     *  one users mentioned multiple times in one tweet
     * 	multiple users mentioned in one tweet
     * 
     * partition on case-insensitive:
     * 	all usernames is lower-case
     * 	some usernames is lower-case, other is not
     * 	all usernames is upper-case
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "proud", "talk is cheap, show me the code", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * covers tweets size = 2,
     * 	is ordered,
     * 	timestamp is all different.
     */
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    /*
     * covers no user mentioned
     */
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */
    
    /*
     * covers tweets size = 2,
     * 	is ordered,
     *  all timestamp is same.
     */
    @Test
    public void testGetTimespanTweetsWithSameTS() {
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet3));
    	
    	assertEquals("expected start", d2, timespan.getStart());
    	assertEquals("expected end", d2, timespan.getEnd());
    }
    
    /*
     * covers tweets size > 2,
     * 	is unordered,
     *  with some timestamp is same.
     */
    @Test
    public void testGetTimespanTweetsUnorder() {
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1));
    	
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    /*
     * covers only one username mentioned,
     * 	size = 1
     * 	only once,
     * 	all format valid,
     * 	one users mentioned only once in one tweet,
     * 	usernames is lower-case.
     */
    @Test
    public void testGetMentionedUsersOnlyOneUser() {
        final Tweet tweet = new Tweet(1, "proud", "talk is cheap, show me the code @linus", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));

        assertEquals("expected mentioned users", 1, mentionedUsers.size());
        assertTrue("expect mentioned: linus", mentionedUsers.contains("linus"));
    }
    
    /*
     * covers only one username mentioned,
     *  size > 1
     * 	mentioned multiple times,
     * 	all format valid,
     * 	one users mentioned multiple times in one tweet,
     * 	some usernames is lower-case, other is not.
     */
    @Test
    public void testGetMentionedUsersOnlyOneUserWithMultipleTweets() {
        final Tweet tweet1 = new Tweet(1, "proud", "talk is cheap, show me the code @linus @LINuS", d1);
        final Tweet tweet2 = new Tweet(1, "proud", "@linus merge my code please", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));

        assertEquals("expected mentioned users", 1, mentionedUsers.size());
        assertTrue("expect mentioned: linus", mentionedUsers.contains("linus"));
    }
    
    /*
     * covers some usernames mentioned,
     *  size > 1
     * 	some usernames mentioned multiple times, some username only mentioned once,
     * 	some user mentioned, some format invalid,
     *  multiple users mentioned in one tweet
     * 	all usernames is upper-case.
     */
    @Test
    public void testGetMentionedUsersWithInvalidFormat() {
        final Tweet tweet1 = new Tweet(1, "proud", "talk is cheap, show me the code @LINUS @JACK", d1);
        final Tweet tweet2 = new Tweet(2, "proud", "@LINUS merge my code please", d1);
        final Tweet tweet3 = new Tweet(3, "proud", "@LINUS please review my code, connect review@github.com", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3));

        assertEquals("expected mentioned users", 2, mentionedUsers.size());
        assertTrue("expect mentioned: linus", mentionedUsers.contains("linus"));
        assertTrue("expect mentioned: jack", mentionedUsers.contains("jack"));
    }
    
    /*
     * covers some usernames mentioned,
     *  size > 1
     * 	all usernames mentioned multiple times,
     * 	some user mentioned, some format invalid,
     *  multiple users mentioned in one tweet
     * 	all usernames is upper-case.
     */
    @Test
    public void testGetMentionedUsersAllMentionedMultipleTimes() {
        final Tweet tweet1 = new Tweet(1, "proud", "talk is cheap, show me the code @LINUS @JACK", d1);
        final Tweet tweet2 = new Tweet(2, "proud", "@ZESH @JACK merge my code please", d1);
        final Tweet tweet3 = new Tweet(3, "proud", "@LINUS @ZESH please review my code, connect review@github.com", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3));

        assertEquals("expected mentioned users", 3, mentionedUsers.size());
        assertTrue("expect mentioned: linus", mentionedUsers.contains("linus"));
        assertTrue("expect mentioned: zesh", mentionedUsers.contains("zesh"));
        assertTrue("expect mentioned: jack", mentionedUsers.contains("jack"));
    }
    
}
