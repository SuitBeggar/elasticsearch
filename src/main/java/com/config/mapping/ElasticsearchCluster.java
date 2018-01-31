package com.config.mapping;

public class ElasticsearchCluster {
    private String clustername;

    private String[ ]  clusternode;

    public String getClustername() {
        return clustername;
    }

    public String[] getClusternode() {
        return clusternode;
    }

    public void setClustername(String clustername) {
        this.clustername = clustername;
    }

    public void setClusternode(String[] clusternode) {
        this.clusternode = clusternode;
    }
}
