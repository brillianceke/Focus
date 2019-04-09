package com.ihewro.focus.fragemnt;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihewro.focus.R;
import com.ihewro.focus.adapter.UserFeedPostsVerticalAdapter;
import com.ihewro.focus.bean.Feed;
import com.ihewro.focus.bean.FeedItem;
import com.ihewro.focus.callback.RequestFeedItemListCallback;
import com.ihewro.focus.decoration.DividerItemDecoration;
import com.ihewro.focus.decoration.SuspensionDecoration;
import com.ihewro.focus.task.RequestFeedListDataTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 用户的最新订阅信息文章列表的碎片
 */
public class UserFeedUpdateContentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private SuspensionDecoration mDecoration;
    List<FeedItem> eList = new ArrayList<FeedItem>();

    UserFeedPostsVerticalAdapter adapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;


    public UserFeedUpdateContentFragment() {
    }

    /**
     * 新建一个新的碎片
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return 返回实例
     */
    public static UserFeedUpdateContentFragment newInstance(String param1, String param2) {
        UserFeedUpdateContentFragment fragment = new UserFeedUpdateContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feed_update_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEmptyView();

        bindListener();

        refreshLayout.autoRefresh();
    }

    public void initEmptyView() {
        //初始化列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserFeedPostsVerticalAdapter(eList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), eList));
    }

    /**
     * 获取用户的所有订阅的文章
     */
    public void requestAllData(){
        List<Feed> feedList = LitePal.findAll(Feed.class);
        RequestFeedListDataTask task = new RequestFeedListDataTask(feedList, new RequestFeedItemListCallback() {
            @Override
            public void onFinish(List<FeedItem> feedList) {
                refreshLayout.finishRefresh(true);
                eList.clear();
                eList.addAll(feedList);
                adapter.setNewData(eList);
//                recyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), eList));
            }
        });
        task.run();
    }

    public void bindListener(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestAllData();
            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
