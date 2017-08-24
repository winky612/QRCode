package com.wk.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wk on 2017/8/24.
 */

public class QRCodeActivity extends AppCompatActivity {
    @BindView(R.id.btn_code)
    Button btnCode;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.btn_logocode)
    Button btnLogocode;
    @BindView(R.id.btn_scan)
    Button btnScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
    }

    /**
     * @param content 生成二维码的文本内容(你要把哪一个文本用二维码图片表示出来)
     * @param width   生成的二维码图片的宽
     * @param height  高
     * @return
     */
    public Bitmap generateBitmap(String content, int width, int height) {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //通过该方法对文本内容进行编码
        //encode方法的返回值是一个BitMatrix，
        // 你可以把BitMatrix理解成一个二维数组，这个二维数组的每一个元素都表示一个像素点是否有数据
        //如果BitMatrix上的点表示 该点有数据(true)，那么对应在Bitmap上的像素点就是黑色，否则(false)就是白色
        //第五个参数可选，可以用来设置文本的编码

        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            //定义一个int数组用来存放Bitmap中所有像素点的颜色(遍历)
            int pixels[] = new int[width * height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (encode.get(i, j)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }

            //createBitmap方法共接收6个参数，第一个参数表示Bitmap中所有像素点的颜色，
            // 第二个参数表示像素点的偏移量，第三个参数表示Bitmap每行有多少个像素点，
            // 第四个参数表示生成的Bitmap的宽度，第五个参数表示生成的Bitmap的高度，
            // 第六个参数表示生成的Bitmap的色彩模式，因为二维码只有黑白两种颜色，
            // 所以我们可以不用考虑透明度，直接使用RGB_565即可。
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param qrBitmap   生成的二维码的Bitmap图片
     * @param logoBitmap logo图片
     * @return
     */
    public Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {

        int qrBitmapHeight = qrBitmap.getHeight();
        int qrBitmapWidth = qrBitmap.getWidth();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();

        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(blankBitmap);//画在blankBitmap上
        //使用drawBitmap方法先将原本的二维码图片绘制出来
        canvas.drawBitmap(qrBitmap, 0, 0, null);//第一个是你要绘制的Bitmap对象，第二个和第三个是你要绘制的Bitmap的左上角的坐标，第四个参数是一个画笔
        canvas.save();
        float scaleSize = 1.0f;

        //一般情况下logo的宽高为二维码原图宽高的1/5（中心logo图片不宜过大，否则会影响到二维码的识别）
        //确定缩放比
        while ((logoBitmapHeight / scaleSize) > qrBitmapHeight / 5 || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
            scaleSize *= 2;
        }

        //前两个参数表示宽高的缩放比例，大于1表示放大，小于1表示缩小，后两个参数表示缩放的中心点
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);

        canvas.restore();

        return blankBitmap;


    }

    @OnClick({R.id.btn_code, R.id.btn_logocode,R.id.btn_scan})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                Bitmap qrBitmap = generateBitmap("about:cehome", 400, 400);
//                ivCode.setImageBitmap(qrBitmap);
                break;
            case R.id.btn_logocode:
                Bitmap qrBitmap2 = generateBitmap("about:cehome", 400, 400);
                Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                Bitmap bmp = addLogo(qrBitmap2, logoBitmap);
                ivCode.setImageBitmap(bmp);
                break;
            case R.id.btn_scan:
                startActivity(new Intent(this, CaptureActivity.class));
                break;
        }


    }


}
