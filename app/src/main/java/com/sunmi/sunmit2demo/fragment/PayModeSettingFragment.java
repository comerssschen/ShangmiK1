package com.sunmi.sunmit2demo.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sunmi.sunmit2demo.BaseFragment;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.utils.SharePreferenceUtil;

public class PayModeSettingFragment extends BaseFragment {

    private Switch swPaymentReal;

    public final static String IS_REAL_DEAL = "IS_REAL_DEAL";

    public final static boolean default_isRealDeal = false;//是否真实交易,true表示按 显示金额扣款

    @Override
    protected int setView() {
        return R.layout.fragment_pay_mode_setting;
    }

    @Override
    protected void init(View view) {


        swPaymentReal = view.findViewById(R.id.sw_payment_real);


        boolean isRealDeal = (boolean) SharePreferenceUtil.getParam(getContext(), PayModeSettingFragment.IS_REAL_DEAL, default_isRealDeal);

        swPaymentReal.setChecked(!isRealDeal);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        swPaymentReal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.setParam(getContext(), PayModeSettingFragment.IS_REAL_DEAL, !isChecked);

            }
        });

    }

}
