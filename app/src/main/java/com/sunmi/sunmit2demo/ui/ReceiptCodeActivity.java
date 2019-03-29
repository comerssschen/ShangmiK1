package com.sunmi.sunmit2demo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.sunmi.sunmit2demo.BaseActivity;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.bean.ArgCreateQRCode;
import com.sunmi.sunmit2demo.bean.ArgQueryOrderState;
import com.sunmi.sunmit2demo.bean.MenusBean;
import com.sunmi.sunmit2demo.bean.OnResponseListener;
import com.sunmi.sunmit2demo.bean.ResultCreateQRCode;
import com.sunmi.sunmit2demo.bean.ResultQueryOrderState;
import com.sunmi.sunmit2demo.utils.ImageUtil;
import com.sunmi.sunmit2demo.utils.OkGoUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者：create by comersss on 2018/11/15 19:47
 * 邮箱：904359289@qq.com
 * 收款码
 */
public class ReceiptCodeActivity extends BaseActivity {
    private String paymoney;
    private ImageView ivQRCode;
    private String type;
    private String out_trade_no;
    private ArrayList<MenusBean> menus;
    private int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        init();
    }

    private void init() {
        TextView tvTips = findViewById(R.id.tv_tips);
        menus = (ArrayList<MenusBean>) getIntent().getSerializableExtra("menus");
        paymoney = getIntent().getStringExtra("paymoney");
        totalCount = getIntent().getIntExtra("count", 0);
        type = getIntent().getStringExtra("type");
        if (ObjectUtils.equals(type, "1")) {
            tvTips.setText("请使用微信扫一扫付款");
        } else {
            tvTips.setText("请使用支付宝扫一扫付款");
        }
        TextView tvPaymoney = findViewById(R.id.tv_paymoney);
        tvPaymoney.setText("￥" + paymoney);
        ivQRCode = findViewById(R.id.iv_qrcode);
        scanQRCode();
    }

    //请求带金额的二维码
    private void scanQRCode() {
        ArgCreateQRCode arg = new ArgCreateQRCode();
        arg.setCash_id("100112053");
        arg.setClient("1");
        arg.setTotal_fee(paymoney);
        arg.setRemark("");
        arg.setPay_type(type);
        OkGoUtils.getInstance().postNoGateWay(ReceiptCodeActivity.this, new Gson().toJson(arg), "/api/pay/precreate", new OnResponseListener() {
            @Override
            public void onResponse(String serverRetData) {
                try {
                    ResultCreateQRCode result = new Gson().fromJson(serverRetData, ResultCreateQRCode.class);
                    Bitmap encode = ImageUtil.encode(result.getQr_code_url());
                    ivQRCode.setImageBitmap(encode);
                    out_trade_no = result.getOut_trade_no();
                    queryOrderState();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("请求失败");
                }
            }

            @Override
            public void onFail(String errMsg) {
                ToastUtils.showShort("请求失败:" + errMsg);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }


    //查询订单状态
    private void queryOrderState() {
        ArgQueryOrderState arg = new ArgQueryOrderState();
        arg.setCash_id("100112053");
        arg.setClient("1");
        arg.setOut_trade_no(out_trade_no);
        OkGoUtils.getInstance().postNoGateWay(ReceiptCodeActivity.this, new Gson().toJson(arg), "/api/pay/query", new OnResponseListener() {
            @Override
            public void onResponse(String serverRetData) {
                try {
                    ResultQueryOrderState result = new Gson().fromJson(serverRetData, ResultQueryOrderState.class);
                    if ("1".equals(result.getState()) || "0".equals(result.getState())) {
                        boolean finishing = ReceiptCodeActivity.this.isFinishing();
                        if (!finishing) {
                            try {
                                Thread.sleep(2000);
                                queryOrderState();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if ("10".equals(result.getState())) {
                        Intent intent = new Intent(ReceiptCodeActivity.this, SucessActivity.class);
                        intent.putExtra("menus", (Serializable) menus);
                        intent.putExtra("count", totalCount);
                        startActivity(intent);
                        finish();
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("请求失败");
                }
            }

            @Override
            public void onFail(String errMsg) {
                ToastUtils.showShort("请求失败:" + errMsg);
            }
        });

    }


}
