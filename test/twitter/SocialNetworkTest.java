/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.InlineView;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * 
     * Testing strategy for guessFollowsGraph:
     * 
     * partition on tweets size:
     * 	size = 0
     * 	size = 1
     * 	size > 1
     * 
     * partition on graph:
	 *  username is not be mentioned
	 *  username is mentioned only once
	 *  username is mentioned multiple times
     *  
     * Testing strategy for influencers:
     * 
     * partition on graph:
     * 	no author has followers
     * 	at least one author has followers
     */
	
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T10:05:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T11:55:00Z");


    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "alex", "@alyssa please review my code", d2);
    private static final Tweet tweet3 = new Tweet(3, "justin", "@jack please review my code", d3);
    private static final Tweet tweet4 = new Tweet(4, "jack", "@justin please review my code", d1);
    private static final Tweet tweet5 = new Tweet(5, "alyssa", "@justin @jack Great job", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * covers size = 0
     */
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

    /*
     * covers size = 1
     * username is not be mentioned
     */
    @Test
    public void testGuessFollowsGraphOneTweetNotMentioned() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        assertTrue("expected empty graph or not be followed", !followsGraph.containsKey("alyssa") || 
        		followsGraph.get("alyssa").size() == 0);
    }
    
    /*
     * covers size = 1
     * username is mentioned only once
     */
    @Test
    public void testGuessFollowsGraphMentionedOnce() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        assertEquals("expected follows graph", Set.of("alex"), followsGraph.get("alyssa"));
    }
    
    /*
     * covers size > 1
     * username is mentioned only once
     */
    @Test
    public void testGuessFollowsGraphMutualMentioned() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet4));
        assertEquals("expected follows graph", Set.of("jack"), followsGraph.get("justin"));
        assertEquals("expected follows graph", Set.of("justin"), followsGraph.get("jack"));
    }
    
    /*
     * covers size > 1
	 *  username is mentioned multiple times
     */
    @Test
    public void testGuessFollowsGraphMentionedMultipleTimes() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4, tweet5));
        assertEquals("expected follows graph", Set.of("jack", "alyssa"), followsGraph.get("justin"));
        assertEquals("expected follows graph", Set.of("alyssa"), followsGraph.get("jack"));
    }
    
    /*
     * covers no author has followers
     */
    @Test
    public void testInfluencersNoFollowers() {
        Map<String, Set<String>> followsGraph = Map.of("justin", Set.of(), "jack", Set.of());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list size", 2, influencers.size());
        assertTrue("expected no author has followers", influencers.containsAll(List.of("justin", "jack")));
    }
    
    /*
     * covers at least one author has followers
     */
    @Test
    public void testInfluencersHasFollowers() {
        Map<String, Set<String>> followsGraph = Map.of("justin", Set.of(), "jack", Set.of("alex", "justin"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list size", 3, influencers.size());
        assertEquals("expected author have the greatest influence", "jack", influencers.get(0));
        assertTrue("expected author has no follower", influencers.subList(1, 3).containsAll(List.of("justin", "alex")));
    }
}
