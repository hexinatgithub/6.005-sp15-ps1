/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Testing strategy for writtenBy:
     * 
     * partition on tweets size:
     * 	size = 1
     * 	size > 1
     * 
     * partition on author:
     * 	only one tweet is written by author
     * 	some tweets is written by author
     * 	all tweets is written by author
     * 
     * Testing strategy for inTimespan:
     * 
     * partition on tweets size:
     * 	size = 1
     * 	size > 1
     * 
     * partition on tweets in timespan:
     * 	no tweet in timespan
     * 	some tweets in timespan, some are not
     * 	all tweets in timespan
     * 
     * Testing strategy for containing:
     * 
     * partition on tweets size:
     * 	size = 1
     * 	size > 1
     * 
     * partition on words size:
     * 	size = 1
     * 	size > 1
     * 
     * partition on tweets containing words:
     * 	no tweet containing words
     * 	some tweet containing all words
     * 	some tweet containing some words
     * 	all tweet containing all words
     * 	all tweet containing some words
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T12:01:00Z");

    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "don't talk when you don't known", d3);
    private static final Tweet tweet4 = new Tweet(4, "alex", "test-first is sounds reasonable", d4);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /*
     * covers size > 1
     * only one tweet is written by author 
     */ 
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    /*
     * covers size > 1
     * all tweets in timespan
     */
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    /*
     * covers tweets size > 1
     * words size = 1
     * all tweet containing all words
     */
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

    /*
     * covers size = 1
     * all tweets is written by author
     */
    @Test
    public void testWrittenByOneTweetNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alex");

        assertEquals("expected empty list", 0, writtenBy.size());
    }

    /*
     * covers size > 1
     * some tweets is written by author
     */
    @Test
    public void testWrittenByMultipleTweetsPartResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");

        assertEquals("expected two tweets", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet3));
    }
    
    /*
     * covers size = 1
     * no tweet in timespan
     */
    @Test
    public void testInTimespanOneTweetNoResults() {
        Instant testStart = Instant.parse("2016-02-17T11:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    /*
     * covers size > 1
     * some tweets in timespan, some are not
     */
    @Test
    public void testInTimespanMultipleTweetsOneResult() {
        Instant testStart = Instant.parse("2016-02-17T11:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected list size", 1, inTimespan.size());
        assertTrue("expected list to contain tweet", inTimespan.contains(tweet2));
    }
    
    /*
     * covers tweets size = 1
     * words size > 1
     * all tweet containing some words
     */
    @Test
    public void testContainingAllContainSomeWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk", "reasonable"));
        
        assertEquals("expected list size", 1, containing.size());
        assertTrue("expected list to contain tweet", containing.contains(tweet1));
    }
    
    /*
     * covers tweets size > 1
     * words size > 1
     * some tweet containing some words
     */
    @Test
    public void testContainingSomeContainSomeWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("reasonable", "minutes"));
        
        assertEquals("expected list size", 2, containing.size());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    /*
     * covers tweets size > 1
     * words size > 1
     * some tweet containing all words
     */
    @Test
    public void testContainingSomeContainAllWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet4), Arrays.asList("reasonable", "is"));
        
        assertEquals("expected list size", 2, containing.size());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet4)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    /*
     * covers tweets size > 1
     * words size > 1
     * no tweet containing words
     */
    @Test
    public void testContainingContainNoWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3, tweet4), Arrays.asList("complex"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
}
