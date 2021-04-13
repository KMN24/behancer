package com.elegion.test.behancer.ui.projects;

import android.view.View;

import com.elegion.test.behancer.BuildConfig;
import com.elegion.test.behancer.common.BasePresenter;
import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.utils.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProjectPresenter extends BasePresenter {

    private ProjectsView mProjectsView;
    private Storage mStorage;

    public ProjectPresenter(ProjectsView projectsView, Storage storage) {
        mProjectsView = projectsView;
        mStorage = storage;
    }

    public void getProjects(){
        mCompositeDisposable.add(ApiUtils.getApiService().getProjects(BuildConfig.API_QUERY)
                .doOnSuccess(response -> mStorage.insertProjects(response))
                .onErrorReturn(throwable ->
                        ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.getClass()) ? mStorage.getProjects() : null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mProjectsView.showRefresh())
                .doFinally(() -> mProjectsView.hideRefresh())
                .subscribe(
                        response -> mProjectsView.showProject(response.getProjects()),
                        throwable -> mProjectsView.showError()));
    }

    public void openProfileFragment(String username){
        mProjectsView.openProfileFragment(username);
    }
}
