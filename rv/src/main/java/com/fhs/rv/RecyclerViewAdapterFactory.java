package com.fhs.rv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterFactory {

    public static class Builder {
        Params params = new Params();

        public Builder(Context context) {
            params.mContext = context;
        }

        public Builder setLayoutManager(RecyclerView.LayoutManager manager) {
            params.manager = manager;
            return this;
        }

        public Builder setEmptyLoadingAdapter(BaseMultilItemAdapter adapter) {
            params.loadingAdapter = adapter;
            return this;
        }


        public Builder setFooterAdapter(BaseMultilItemAdapter adapter, boolean showImmediately) {
            params.footerAdapter = adapter;
            params.showFooterImmediately = showImmediately;
            return this;
        }

        public Builder setDefaultFooter(String loadingMessage, boolean showImmediately) {
            DefaultFooterAdapterBase adapter = new DefaultFooterAdapterBase();
            adapter.setMessage(loadingMessage, null, DefaultFooterAdapterBase.LOADING);
            setFooterAdapter(adapter, showImmediately);
            return this;
        }

        public Builder setOnCreateAdaptersListener(OnCreateAdaptersListener listener) {
            params.listener = listener;
            return this;
        }

        public Builder attachToRecyclerView(RecyclerView recyclerView) {
            params.recyclerView = recyclerView;
            return this;
        }

        public BaseLayoutAdapter build() {
            BaseLayoutAdapter adapter = new BaseLayoutAdapter();
            params.adapters = new ArrayList<>();
            if (params.listener != null) {
                params.listener.onCreate(params.adapters);
            }
            /**
             * 任何时候都显示footer
             */
            if (params.showFooterImmediately && params.footerAdapter != null) {
                params.adapters.add(params.footerAdapter);
            }
            adapter.setMuliItems(params.adapters);
            if (params.recyclerView != null) {
                if (params.manager == null) {
                    params.manager = new LinearLayoutManager(params.mContext);
                }
                params.recyclerView.setLayoutManager(params.manager);
                if (params.manager instanceof GridLayoutManager) {
                    final GridLayoutManager manager = (GridLayoutManager) params.manager;
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            if (params.recyclerView.getAdapter() instanceof BaseLayoutAdapter) {
                                BaseLayoutAdapter layoutAdapter = (BaseLayoutAdapter) params.recyclerView.getAdapter();
                                BaseMultilItemAdapter pair = layoutAdapter.findMultiItemByPosition(position);
                                if (!layoutAdapter.checkAdapter(pair)) {
                                    return 1;
                                }
                                int type = pair.getItemMatchType();
                                int count;
                                if (type == BaseMultilItemAdapter.MATCH || type > manager.getSpanCount()) {
                                    count = manager.getSpanCount();
                                } else if (type <= BaseMultilItemAdapter.WARP) {
                                    count = 1;
                                } else {
                                    count = type;
                                }
                                return count;
                            }
                            return 1;
                        }
                    });
                }
                params.recyclerView.setAdapter(adapter);
            }
            return adapter;
        }
    }


    public static class Params {
        Context mContext;
        RecyclerView recyclerView;
        List<BaseMultilItemAdapter> adapters;
        RecyclerView.LayoutManager manager;
        OnCreateAdaptersListener listener;
        BaseMultilItemAdapter footerAdapter;
        BaseMultilItemAdapter loadingAdapter;
        /**
         * 无数据时候显示footer
         */
        boolean showFooterImmediately;
    }

    public interface OnCreateAdaptersListener {
        void onCreate(List<BaseMultilItemAdapter> adapters);
    }


    public static class DefaultFooterAdapterBase extends BaseMultilItemAdapter<String> {
        public final static int LOADING = 0;
        public final static int COMPLETE = 1;
        public final static int ERROR = 2;
        private String message;
        private Drawable drawable;
        private int state = LOADING;

        public DefaultFooterAdapterBase() {

        }


        @Override
        public int getItemCount() {
            return 1;
        }


        @Override
        public void onBindViewHolder(BaseLayoutAdapter.ViewHolder viewHolder, int position) {
            viewHolder.setText(R.id.footview_text, message);
            ImageView iv = viewHolder.getView(R.id.footer_load_result_iv);
            ProgressBar loadingBar = viewHolder.getView(R.id.footer_loading);
            if (state == LOADING) {
                if (drawable != null) {
                    iv.setImageDrawable(drawable);
                    loadingBar.setVisibility(View.GONE);
                    iv.setVisibility(View.VISIBLE);
                } else {
                    loadingBar.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.GONE);
                }
                viewHolder.itemView.setOnClickListener(null);
            } else if (state == COMPLETE || state == ERROR) {
                loadingBar.setVisibility(View.GONE);
                iv.setImageDrawable(drawable);
                iv.setVisibility(View.VISIBLE);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

        }

        public void setMessage(String message, Drawable drawable, int state) {
            this.message = message;
            this.drawable = drawable;
            this.state = state;
        }

        @Override
        protected int getItemLayoutIds() {
            return R.layout.base_layout_footview;
        }

        @Override
        public int getItemMatchType() {
            return BaseMultilItemAdapter.MATCH;
        }
    }


}
