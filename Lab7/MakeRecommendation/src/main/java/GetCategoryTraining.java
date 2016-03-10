import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ting on 3/9/16.
 */
public class GetCategoryTraining {
    public List<Status> GetCategoryData(String keyword) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey("R2v2WMKrF7UGipifRcMkOyjT1")
                .setOAuthConsumerSecret("InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d")
                .setOAuthAccessToken("3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n")
                .setOAuthAccessTokenSecret("IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query;
        query = new Query(keyword +" -filter:retweets -filter:links -filter:replies -filter:images");
        query.setCount(100);
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
        GetCategoryTraining getCategoryTraining = new GetCategoryTraining();
        List<Status> statuses = getCategoryTraining.GetCategoryData("food");

        int i = 0;
        for (Status status : statuses) {
            if (status.getText() != null) {
                i++;
                File foodTextFile = new File("data/categoryTraining/food/" + i + ".txt");
                FileWriter fw = new FileWriter(foodTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }


        GetCategoryTraining getCategoryTraining2 = new GetCategoryTraining();
        List<Status> statuses2 = getCategoryTraining2.GetCategoryData("sport");

        i = 0;
        for (Status status : statuses2) {
            if (status.getText() != null) {
                i++;
                File sportTextFile = new File("data/categoryTraining/sport/" + i + ".txt");
                FileWriter fw = new FileWriter(sportTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }

        GetCategoryTraining getCategoryTraining3 = new GetCategoryTraining();
        List<Status> statuses3 = getCategoryTraining3.GetCategoryData("music");

        i = 0;
        for (Status status : statuses3) {
            if (status.getText() != null) {
                i++;
                File musicTextFile = new File("data/categoryTraining/music/" + i + ".txt");
                FileWriter fw = new FileWriter(musicTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }
        GetCategoryTraining getCategoryTraining4 = new GetCategoryTraining();
        List<Status> statuses4 = getCategoryTraining4.GetCategoryData("travel");

        i = 0;
        for (Status status : statuses4) {
            if (status.getText() != null) {
                i++;
                File travelTextFile = new File("data/categoryTraining/travel/" + i + ".txt");
                FileWriter fw = new FileWriter(travelTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }
        GetCategoryTraining getCategoryTraining5 = new GetCategoryTraining();
        List<Status> statuses5 = getCategoryTraining5.GetCategoryData("book");

        i = 0;
        for (Status status : statuses5) {
            if (status.getText() != null) {
                i++;
                File bookTextFile = new File("data/categoryTraining/book/" + i + ".txt");
                FileWriter fw = new FileWriter(bookTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }

        GetCategoryTraining getCategoryTraining6 = new GetCategoryTraining();
        List<Status> statuses6 = getCategoryTraining6.GetCategoryData("TV");

        i = 0;
        for (Status status : statuses6) {
            if (status.getText() != null) {
                i++;
                File TVTextFile = new File("data/categoryTraining/TV/" + i + ".txt");
                FileWriter fw = new FileWriter(TVTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }
        GetCategoryTraining getCategoryTraining7 = new GetCategoryTraining();
        List<Status> statuses7 = getCategoryTraining7.GetCategoryData("animal");

        i = 0;
        for (Status status : statuses7) {
            if (status.getText() != null) {
                i++;
                File animalTextFile = new File("data/categoryTraining/animal/" + i + ".txt");
                FileWriter fw = new FileWriter(animalTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }

        GetCategoryTraining getCategoryTraining8 = new GetCategoryTraining();
        List<Status> statuses8 = getCategoryTraining2.GetCategoryData("movie");

        i = 0;
        for (Status status : statuses2) {
            if (status.getText() != null) {
                i++;
                File movieTextFile = new File("data/categoryTraining/movie/" + i + ".txt");
                FileWriter fw = new FileWriter(movieTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }
        GetCategoryTraining getCategoryTraining9 = new GetCategoryTraining();
        List<Status> statuses9 = getCategoryTraining9.GetCategoryData("art");

        i = 0;
        for (Status status : statuses9) {
            if (status.getText() != null) {
                i++;
                File artTextFile = new File("data/categoryTraining/art/" + i + ".txt");
                FileWriter fw = new FileWriter(artTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }

        GetCategoryTraining getCategoryTraining10 = new GetCategoryTraining();
        List<Status> statuses10 = getCategoryTraining2.GetCategoryData("");

        i = 0;
        for (Status status : statuses10) {
            if (status.getText() != null) {
                i++;
                File otherTextFile = new File("data/categoryTraining/other/" + i + ".txt");
                FileWriter fw = new FileWriter(otherTextFile);
                fw.write(status.getText());
                fw.close();
            }
        }
    }

}
