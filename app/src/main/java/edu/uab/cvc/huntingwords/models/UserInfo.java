package edu.uab.cvc.huntingwords.models;

/**
 * Created by carlosb on 10/04/18.
 */

public class UserInfo {
    private  String username;
   private  String ranking;

   public UserInfo() {

   }

    public UserInfo (String username, String ranking) {
            this.username = username;
            this.ranking = ranking;
    }

    public String getUsername() {
        return username;
    }

    public String getRanking() {
        return ranking;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
}
