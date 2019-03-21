package com.fhs.rv;

/**
 * @author Shun
 */
public abstract class BaseFooterAdapter extends BaseMultilItemAdapter<String> {
    public final static int LOADING = 0;
    public final static int COMPLETE = 1;
    public final static int ERROR = 2;

    @Override
    final public int getItemCount() {
        return 1;
    }

    @Override
    protected void onBindViewHolder(BaseLayoutAdapter.ViewHolder viewHolder, int position) {

    }


    @Override
    public int getItemMatchType() {
        return BaseMultilItemAdapter.MATCH;
    }

    /**
     * @param message
     * @param state   {@link BaseFooterAdapter#LOADING}
     */
    public abstract void setMessage(String message, int state);
}
