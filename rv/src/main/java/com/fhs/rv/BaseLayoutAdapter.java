package com.fhs.rv;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BaseLayoutAdapter extends RecyclerView.Adapter<BaseLayoutAdapter.ViewHolder> {

    private List<BaseMultilItemAdapter> mSubAdapters = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseMultilItemAdapter resultItem = null;
        for (BaseMultilItemAdapter item : mSubAdapters) {
            if (item.getIndex() == viewType) {
                resultItem = item;
                break;
            }
        }
        return resultItem.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        BaseMultilItemAdapter item = findMultiItemByPosition(position);
        if (!checkAdapter(item)) {
            return 0;
        }
        return item.getIndex();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        BaseMultilItemAdapter iMultiItem = findMultiItemByPosition(position);
        if (iMultiItem != viewHolder.mAdapter) {
            return;
        }
        viewHolder.mAdapter.onBindViewHolder(viewHolder, position - iMultiItem.getStartPosition());
    }

    protected BaseMultilItemAdapter findMultiItemByPosition(int position) {
        final int count = mSubAdapters.size();
        if (count == 0) {
            return null;
        }
        BaseMultilItemAdapter result = null;
        int small = 0, height = count - 1, middle;
        while (small <= height) {
            middle = (small + height) / 2;
            result = mSubAdapters.get(middle);
            int endPosition = result.getStartPosition() + result.getItemCount() - 1;
            if (position > endPosition) {
                small = middle + 1;
            } else if (position < result.getStartPosition()) {
                height = middle - 1;
            } else if (position >= result.getStartPosition() && position <= endPosition) {
                break;
            }
        }
        return result;
    }

    public void setMuliItems(List<BaseMultilItemAdapter> items) {
        int count = 0;
        mSubAdapters.clear();
        if (observer != null) {
            unregisterAdapterDataObserver(observer);
        }
        observer = new AdapterDataObserver();
        registerAdapterDataObserver(observer);
        for (int i = 0; i < items.size(); i++) {
            BaseMultilItemAdapter adapter = items.get(i);
            adapter.setMainAdapter(this);
            adapter.setIndex(i);
            adapter.setStartPosition(count);
            count += adapter.getItemCount();
            mSubAdapters.add(adapter);
        }
        notifyDataSetChanged();
    }

    private AdapterDataObserver observer;

    @Override
    public int getItemCount() {
        return getAllItemCount();
    }

    private int getAllItemCount() {
        int count = 0;
        for (BaseMultilItemAdapter item : mSubAdapters) {
            count += item.getItemCount();
        }
        return count;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder) && holder.mAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.mAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mAdapter.onViewDetachedFromWindow(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        BaseMultilItemAdapter mAdapter;
        private SparseArray<View> views;

        public ViewHolder(@NonNull View itemView, BaseMultilItemAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
        }

        public <T extends View> T getView(int id) {
            if (views == null) {
                views = new SparseArray<>();
            }
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return (T) view;
        }

        public ViewHolder setText(int viewId, String value) {
            TextView view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setText(value);
            return ViewHolder.this;
        }

        public ViewHolder setTextColor(int viewId, int textColor) {
            TextView view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setTextColor(textColor);
            return ViewHolder.this;
        }

        public ViewHolder setImageResource(int viewId, int imageResId) {
            ImageView view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setImageResource(imageResId);
            return ViewHolder.this;
        }


        public ViewHolder setBackgroundColor(int viewId, int color) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setBackgroundColor(color);
            return ViewHolder.this;
        }

        public ViewHolder setBackgroundResource(int viewId, int backgroundRes) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setBackgroundResource(backgroundRes);
            return ViewHolder.this;
        }

        public ViewHolder setVisible(int viewId, boolean visible) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return ViewHolder.this;
        }

        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            if (view != null) {
                view.setOnClickListener(listener);
            }
            return ViewHolder.this;
        }

        public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setOnTouchListener(listener);
            return ViewHolder.this;
        }

        public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setOnLongClickListener(listener);
            return ViewHolder.this;
        }

        public ViewHolder setTag(int viewId, Object tag) {
            View view = getView(viewId);
            if (!checkView(view)) {
                return this;
            }
            view.setTag(tag);
            return ViewHolder.this;
        }

        public boolean checkView(View view) {
            return view != null;
        }
    }

    /**
     * 监听子Adapter调用notify类，同时将子类的position映射成宿主Adapter正确的position
     */
    protected class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        AdapterDataObserver() {
        }


        private void updateLayoutHelper(int from) {
            BaseMultilItemAdapter item = findMultiItemByPosition(from);
            int fromPosition = mSubAdapters.indexOf(item) - 1;
            if (fromPosition < 0) {
                fromPosition = 0;
            }
            updateAdaptersItemCount(fromPosition);
        }

        /**
         * @param fromPosition 子Adapter在集合中的坐标位置
         */
        private void updateAdaptersItemCount(int fromPosition) {
            BaseMultilItemAdapter pair;
            pair = mSubAdapters.get(fromPosition);
            if (!checkAdapter(pair)) {
                return;
            }
            int count = pair.getStartPosition();
            for (int i = fromPosition; i < mSubAdapters.size(); i++) {
                pair = mSubAdapters.get(i);
                pair.setStartPosition(count);
                count = pair.getStartPosition() + pair.getItemCount();
            }
        }


        @Override
        public void onChanged() {
            updateLayoutHelper(0);
            //    notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateLayoutHelper(positionStart);
            //  notifyItemRangeChanged(positionStart, getAllItemCount());
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateLayoutHelper(positionStart);
            // notifyItemRangeChanged(positionStart, getAllItemCount());
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int position = fromPosition <= toPosition ? fromPosition : toPosition;
            updateLayoutHelper(position);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            updateLayoutHelper(positionStart);
        }
    }

    protected boolean checkAdapter(BaseMultilItemAdapter pair) {
        return pair != null;
    }
}
