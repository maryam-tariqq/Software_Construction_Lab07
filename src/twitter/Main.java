/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the main program.
 * 
 * You may change this class if you wish, but you don't have to.
 */
public class Main {

    /** Local file of sample tweets (the MIT tweet server is offline). */
    public static final String TWEETS_FILE = "tweets.txt";

    /**
     * Read tweets from a text file with one tweet per line in the format
     * id|author|timestamp|text. Blank lines and lines starting with # are skipped.
     */
    private static List<Tweet> readTweetsFromFile(String filename) throws IOException {
        List<Tweet> tweets = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\|", 4); // text may itself contain '|'
            long id = Long.parseLong(parts[0].trim());
            String author = parts[1].trim();
            Instant timestamp = Instant.parse(parts[2].trim());
            String text = parts[3];
            tweets.add(new Tweet(id, author, text, timestamp));
        }
        return tweets;
    }

    /**
     * Main method of the program. Reads a sample of tweets from tweets.txt and
     * prints some facts about it.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            assert false;
            throw new Error("Always run main and tests with assertions enabled");
        } catch (AssertionError ae) { }

        final List<Tweet> tweets;
        try {
            tweets = readTweetsFromFile(TWEETS_FILE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        // display some characteristics about the tweets
        System.err.println("loaded " + tweets.size() + " tweets");

        final Timespan span = Extract.getTimespan(tweets);
        System.err.println("ranging from " + span.getStart() + " to " + span.getEnd());

        final Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        System.err.println("covers " + mentionedUsers.size() + " Twitter users");

        // infer the follows graph
        final Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        System.err.println("follows graph has " + followsGraph.size() + " nodes");

        // print the top-N influencers
        final int count = 10;
        final List<String> influencers = SocialNetwork.influencers(followsGraph);
        for (String username : influencers.subList(0, Math.min(count, influencers.size()))) {
            System.out.println(username);
        }
    }

}
