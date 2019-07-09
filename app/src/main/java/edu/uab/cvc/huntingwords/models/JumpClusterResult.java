package edu.uab.cvc.huntingwords.models;

/**
 * Created by carlosb on 23/04/18.
 */

public class JumpClusterResult {

    private static final int SAME = 0;
    private static final int DIFFERENT = 1;
    private final String clusterName;
    private String imageName;
    private String answer;

    private JumpClusterResult(String cluster, String name, int result) {
        if (result == SAME) {
            this.answer = "eq";
        } else {
            this.answer = "diff";
        }
        this.clusterName = cluster;
        this.imageName = name;
    }


    public static JumpClusterResult newImageDifferent(String cluster, String imageName) {
        return new JumpClusterResult(cluster,imageName,DIFFERENT);
    }

    public static JumpClusterResult newImageEqual(String cluster, String imageName) {
        return new JumpClusterResult(cluster,imageName,SAME);
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getAnswer() {
        return answer;
    }
}
