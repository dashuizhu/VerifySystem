package verifysystem.company.com.verifysystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  @author 毛关松 QQ:1626550278
 *  created at 2016/4/27 16:51 
 */
public abstract class StandardRecyclerAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<T> mDataList = new ArrayList<>();
    protected AdapterView.OnItemClickListener onItemClickListener;

    public StandardRecyclerAdapter(Context context, List<T> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<T> mDataList) {
        this.mDataList = mDataList;
    }


    @Override
    public abstract void onBindViewHolder(VH holder, int position);


    @Override
    public int getItemCount() {
        return mDataList == null?0:mDataList.size();
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<T> getDatas() {
        return mDataList;
    }
}
