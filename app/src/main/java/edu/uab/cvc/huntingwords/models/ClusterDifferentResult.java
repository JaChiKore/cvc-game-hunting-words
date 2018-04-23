package edu.uab.cvc.huntingwords.models;

/**
 * Created by carlosb on 23/04/18.
 */

public class ClusterDifferentResult {


    public enum Result {
        SAME,
        DIFFERENT,
        ONE_IMAGE
    };
    private final String clusterName;
    private String imageName;
    private Result result;


    private ClusterDifferentResult(String clusterName, String imageName) {
        this.clusterName = clusterName;
        this.imageName = imageName;
        this.result = Result.ONE_IMAGE;
    }
    private ClusterDifferentResult(String cluster, Result result) {
        this.result = result;
        this.clusterName = cluster;
    }


    public static ClusterDifferentResult newImageDifferent(String cluster, String imageName) {
        return new ClusterDifferentResult(cluster, imageName);
    }
    public static ClusterDifferentResult newSameImage(String cluster) {
        return new ClusterDifferentResult(cluster,Result.SAME);
    }
    public static ClusterDifferentResult newAllDifferent(String cluster) {
        return new ClusterDifferentResult(cluster,Result.DIFFERENT);
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getImageName() {
        return imageName;
    }
}
