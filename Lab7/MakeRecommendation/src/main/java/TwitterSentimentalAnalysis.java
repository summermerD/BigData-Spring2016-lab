import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ting on 3/8/16.
 */

public class TwitterSentimentalAnalysis {
    public List<Status> getTestingData() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey("R2v2WMKrF7UGipifRcMkOyjT1")
                .setOAuthConsumerSecret("InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d")
                .setOAuthAccessToken("3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n")
                .setOAuthAccessTokenSecret("IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query;
        query = new Query(" -filter:retweets -filter:links -filter:replies -filter:images");
        query.setCount(5);
        query.setLocale("en");
        query.setLang("en");
        try {
            QueryResult queryResult = twitter.search(query);
            return queryResult.getTweets();
        } catch (TwitterException e) {
            // ignore
            e.printStackTrace();
        }
        return Collections.emptyList();

    }

    public static void main(String[] args) throws IOException {
        TwitterSentimentalAnalysis twitterSentimentalAnalysis = new TwitterSentimentalAnalysis();
        List<Status> statuses = twitterSentimentalAnalysis.getTestingData();
        String a = "";
        int i = 0;
        for (Status status : statuses) {
            if (status.getText() != null) {
                i++;
                File newTextFile = new File("data/testing/1.txt");

                FileWriter fw = new FileWriter(newTextFile);
                fw.write(status.getText());
                fw.close();

                SentimentAnalyzer doAnalysis = new SentimentAnalyzer();

                int rate = doAnalysis.findSentiment(status.getText()).getRate();

                TwitterCategoryAnalysis twitterCategoryAnalysis = new TwitterCategoryAnalysis();
                int category = twitterCategoryAnalysis.CategoryAnalysis();

                int usrId = (int)((status.getId() >>> 32) ^ status.getId());
                int time = (int)((status.getCreatedAt().getTime() >>> 32) ^ status.getCreatedAt().getTime());
                a += usrId + "::" + Integer.toString(category) + "::" + Integer.toString(rate) + "::" + time + "\n";
                System.out.println(a);

            }
        }

        try
        {
            String filename= "data/results/rating.txt";
            FileWriter fw2 = new FileWriter(filename,true); //the true will append the new data
            fw2.write(a);//appends the string to the file
            fw2.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }




    }

}
