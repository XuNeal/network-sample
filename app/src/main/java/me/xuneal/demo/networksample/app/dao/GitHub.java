package me.xuneal.demo.networksample.app.dao;

import me.xuneal.demo.networksample.app.model.Contributor;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.List;

/**
 * Created by xyz on 2015/2/27.
 */
public interface GitHub {
    @GET("/repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo
    );



}
