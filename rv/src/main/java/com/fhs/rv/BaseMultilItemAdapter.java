package com.fhs.rv;

import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseMultilItemAdapter<T> {
    private boolean mHasStableIds = false;
    protected int mStartPosition;
    private int mIndex;
    private final List<T> data = new ArrayList<>();
    private BaseLayoutAdapter mMainAdapter;

    public void setMainAdapter(BaseLayoutAdapter mainAdapter) {
        this.mMainAdapter = mainAdapter;
    }

    public int getItemCount() {
        return data.size();
    }

    protected abstract void onBindViewHolder(@NonNull BaseLayoutAdapter.ViewHolder viewHolder, final int position);


    public BaseLayoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutIds(), parent, false);
        return new BaseLayoutAdapter.ViewHolder(view, this);
    }

    protected abstract int getItemLayoutIds();


    public void setHasStableIds(boolean hasStableIds) {
        if (this.hasObservers()) {
            throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
        } else {
            this.mHasStableIds = hasStableIds;
        }
    }

    public void addDataList(List<T> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        int size = this.getData().size();
        this.getData().addAll(data);
        if (size == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(size, data.size());
        }
    }


    public List<T> getData() {
        return data;
    }

    public void addData(T data) {
        addDataList(Collections.singletonList(data));
    }

    public long getItemId(int position) {
        return -1L;
    }


    public final boolean hasStableIds() {
        return this.mHasStableIds;
    }

    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    }

    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return false;
    }

    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
    }

    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    }

    public final boolean hasObservers() {
        return this.mObservable.hasObservers();
    }

    private final AdapterDataObservable mObservable = new AdapterDataObservable();

    public final void notifyDataSetChanged() {
        this.mObservable.notifyChanged();
    }

    public final void notifyItemChanged(int position) {
        this.mObservable.notifyItemRangeChanged(position, 1);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemChanged(getStartPosition() + position, 1);
        }
    }

    public final void notifyItemChanged(int position, @Nullable Object payload) {
        this.mObservable.notifyItemRangeChanged(position, 1, payload);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemChanged(getStartPosition() + position, payload);
        }
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        this.mObservable.notifyItemRangeChanged(positionStart, itemCount);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemRangeChanged(getStartPosition() + positionStart, itemCount);
        }
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        this.mObservable.notifyItemRangeChanged(positionStart, itemCount, payload);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemRangeChanged(getStartPosition() + positionStart, itemCount, payload);
        }
    }

    public final void notifyItemInserted(int position) {
        this.mObservable.notifyItemRangeInserted(position, 1);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemInserted(getStartPosition() + position);
        }
    }


    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        this.mObservable.notifyItemRangeInserted(positionStart, itemCount);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemRangeInserted(getStartPosition() + positionStart, itemCount);
        }
    }

    public final void notifyItemRemoved(int position) {
        this.mObservable.notifyItemRangeRemoved(position, 1);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemRangeRemoved(getStartPosition() + position, 1);
        }
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        this.mObservable.notifyItemRangeRemoved(positionStart, itemCount);
        if (mMainAdapter != null) {
            mMainAdapter.notifyItemRangeRemoved(getStartPosition() + positionStart, itemCount);
        }
    }

    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        this.mObservable.registerObserver(observer);
    }

    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        this.mObservable.unregisterObserver(observer);
    }


    public static final int MATCH = 1;
    public static final int WARP = 0;

    /**
     * 宫格列表的时候占位
     *
     * @return
     */
    public int getItemMatchType() {
        return WARP;
    }

    public int getIndex() {
        return mIndex;
    }

    protected void setIndex(int index) {
        mIndex = index;
    }

    public int getStartPosition() {
        return mStartPosition;
    }

    protected void setStartPosition(int startPosition) {
        this.mStartPosition = startPosition;
    }

    protected static class AdapterDataObservable extends Observable<RecyclerView.AdapterDataObserver> {

        AdapterDataObservable() {

        }


        public boolean hasObservers() {
            return !this.mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                this.mObservers.get(i).onChanged();
            }

        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            this.notifyItemRangeChanged(positionStart, itemCount, (Object) null);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                this.mObservers.get(i).onItemRangeChanged(positionStart, itemCount, payload);
            }

        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                this.mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
            }

        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                this.mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
            }

        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                this.mObservers.get(i).onItemRangeMoved(fromPosition, toPosition, 1);
            }

        }
    }
}
