/**
 * Created by Mayanka on 21-Jul-15.
 * Reference : https://github.com/shekhargulati/day20-stanford-sentiment-analysis-demo
 */
public class TweetWithSentiment {

        private String line;
        private String cssClass;
        private int rate;

        public TweetWithSentiment() {
        }

        public TweetWithSentiment(String line, String cssClass, int rate) {
            super();
            this.line = line;
            this.cssClass = cssClass;
            this.rate = rate;
        }

        public String getLine() {
            return line;
        }

        public String getCssClass() {
            return cssClass;
        }

        public int getRate() {return rate;}

        @Override
        public String toString() {
            return "TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
        }

}
