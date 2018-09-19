package edu.uab.cvc.huntingwords.models;

/**
 * Created by carlosb on 23/04/18.
 */

public class ClusterDifferentResult {

    private static final int SAME = 0;
    private static final int DIFFERENT = 1;
    private final String clusterName;
    private String imageName;


    private ClusterDifferentResult(String clusterName, String imageName) {
        this.clusterName = clusterName;
        this.imageName = imageName;
    }
    private ClusterDifferentResult(String cluster, int result) {
        if (result == SAME) {
            this.imageName = "eq";
        } else {
            this.imageName = "diff";
        }
        this.clusterName = cluster;
    }


    public static ClusterDifferentResult newImageDifferent(String cluster, String imageName) {
        return new ClusterDifferentResult(cluster, imageName);
    }
    public static ClusterDifferentResult newSameImage(String cluster) {
        return new ClusterDifferentResult(cluster,SAME);
    }
    public static ClusterDifferentResult newAllDifferent(String cluster) {
        return new ClusterDifferentResult(cluster,DIFFERENT);
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getImageName() {
        return imageName;
    }
}
