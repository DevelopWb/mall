package com.juntai.mall.base.widght;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.mall.base.R;
import com.juntai.mall.base.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @aouther tobato
 * @description 描述  仿微信底部弹框
 * @date 2020/4/16 13:59
 */
public class BaseBottomDialog extends DialogFragment implements View.OnClickListener {

    private List<String> arrays = new ArrayList<>();
    private RecyclerView mBaseBottomDialogRv;
    private OnItemClick onItemClick;
    /**
     * 取消
     */
    private TextView mBaseBottomDialogCancelTv;
    private BottomDialogAdapter adapter;

    public BaseBottomDialog setOnBottomDialogCallBack(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
        return this;
    }

    public void setData(List<String> arrays) {
        this.arrays = arrays;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.base_bottom_dialog, null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 设置宽度为屏宽、位置靠近屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView(View view) {
        mBaseBottomDialogRv = (RecyclerView) view.findViewById(R.id.base_bottom_dialog_rv);
        mBaseBottomDialogCancelTv = (TextView) view.findViewById(R.id.base_bottom_dialog_cancel_tv);
        mBaseBottomDialogCancelTv.setOnClickListener(this);
        adapter = new BottomDialogAdapter(R.layout.single_text_layout);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBaseBottomDialogRv.setAdapter(adapter);
        addDivider(true,mBaseBottomDialogRv,false,false);
        mBaseBottomDialogRv.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (onItemClick != null) {
                    onItemClick.onItemClick(adapter,view,position);
                }
            }
        });
        adapter.setNewData(arrays);
    }
    /**
     * 添加分割线
     *
     * @param recyclerView
     * @param haveTopLine
     * @param isHorizontalDivider 水平分割线
     * @param haveEndLine         最后一个item下是否划线
     */
    public void addDivider(boolean isHorizontalDivider, RecyclerView recyclerView, boolean haveTopLine, boolean haveEndLine) {
        DividerItemDecoration dividerItemDecoration;
        if (isHorizontalDivider) {
            dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST,
                    R.drawable.divider_hor_line_sp);
        } else {
            dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST);
        }
        if (haveTopLine) {
            if (haveEndLine) {
                dividerItemDecoration.setDividerMode(DividerItemDecoration.ALL);
            } else {
                dividerItemDecoration.setDividerMode(DividerItemDecoration.START);
            }
        } else {
            if (haveEndLine) {
                dividerItemDecoration.setDividerMode(DividerItemDecoration.END);
            } else {
                dividerItemDecoration.setDividerMode(DividerItemDecoration.INSIDE);

            }
        }
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.base_bottom_dialog_cancel_tv) {
            dismiss();
        }
    }

    public  interface OnItemClick{
        void  onItemClick(BaseQuickAdapter adapter, View view, int position);
    }
}
