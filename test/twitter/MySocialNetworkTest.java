package twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MySocialNetworkTest {
	/*
	 * Testing strategy for guessFollowsGraph:
	 * 
	 * partition on closure:
	 * 	A not indirect follow others
	 * 	A indirect follow others by one person
	 * 	A indirect follow others by at least two person
	 */
	
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
	
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "@black is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "@jack rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "black", "@bbitdiddle Good job", d2);
    
    /*
     * covers A not indirect follow others
     */
	@Test
	public void testGuessFollowsGraphNoIndirect() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));
        assertFalse("expected not influenced", followsGraph.get("jack").contains("alyssa"));
        assertFalse("expected not influenced", followsGraph.get("black").contains("bbitdiddle"));
	}
	
    /*
     * covers A indirect follow others by one person
     */
	@Test
	public void testGuessFollowsGraphIndirectFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet3));
        assertTrue("expected indirect influenced", followsGraph.get("bbitdiddle").contains("alyssa"));
	}
	
    /*
     * covers A indirect follow others by at least two person
     */
	@Test
	public void testGuessFollowsGraphTransitiveFollow() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet3));
        assertTrue("expected indirect influenced", followsGraph.get("bbitdiddle").contains("alyssa"));
        assertTrue("expected transitive influenced", followsGraph.get("jack").contains("alyssa"));
	}
}
