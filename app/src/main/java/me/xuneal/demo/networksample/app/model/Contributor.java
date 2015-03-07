package me.xuneal.demo.networksample.app.model;

/**
 * Created by xyz on 2015/2/27.
 */
public class Contributor {
    public final String login;
    public final int contributions;

    Contributor(String login, int contributions) {
        this.login = login;
        this.contributions = contributions;
    }
}
