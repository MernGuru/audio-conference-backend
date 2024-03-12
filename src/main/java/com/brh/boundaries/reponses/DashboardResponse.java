package com.brh.boundaries.reponses;

public class DashboardResponse {
    private final long mActiveUsersCount, mSubscribedUsersCount, mGeneratedRevenue;
    String status;

    public long getmActiveUsersCount() {
        return mActiveUsersCount;
    }

    public long getmSubscribedUsersCount() {
        return mSubscribedUsersCount;
    }

    public long getmGeneratedRevenue() {
        return mGeneratedRevenue;
    }

    public String getStatus() {
        return status;
    }

    public DashboardResponse(long a, long b, long c) {
        this.mActiveUsersCount = a;
        this.mSubscribedUsersCount = b;
        this.mGeneratedRevenue = c;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}