package com.example.pjt_student;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainListAdapter extends ArrayAdapter<StudentVO> {

    Context context;
    ArrayList<StudentVO> datas;
    int resId;  //activity가 전달하는 항목 layout xml 정보...

     public  MainListAdapter(Context context, int resId, ArrayList<StudentVO> datas) {
         super(context, resId);

         this.context = context;
         this.resId = resId;
         this.datas = datas;
     }

     //항목갯수를 판단하기 위해서 자동 호출...
    @Override
    public int getCount() {
        return datas.size();
    }

    //항목이 화면에 보일때 마다 호출
    //한 항목을 구성하기 위한 알고리즘
    //성능이슈 고려해서...
    //position 항목 index
    //View : 항목을 구성하기 위한 view 계층의 root 객체
    //convertView : 해당항목을 위해 재사용할 view가 없다면 null. 그 항목을 출력하기 위해서 리턴 시켰던.. root 객채로 항목 찍고.. 내부적으로 메모리에 유지했다가 다음번에 전달
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resId, null);

            MainListWrapper wrapper = new MainListWrapper(convertView);

            //android 모든 view 에는 개발자 임의의 데이터 저장 가능..
            //그 view 객체가 메모리에 지속만 된다면 언제든지 획득 가능
            convertView.setTag(wrapper);

        }

        MainListWrapper wrapper = (MainListWrapper) convertView.getTag();

        ImageView studentImageView = wrapper.studentImageView;
        TextView nameView = wrapper.nameView;
        final ImageView phoneView = wrapper.phoneView;

        final StudentVO vo = datas.get(position);

        nameView.setText(vo.name);

        //OOM 고려해서 개발해야 한다. 이미지 원본 데이터 사이즈를 줄여서 로딩....
        if(vo.photo != null && !vo.photo.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeFile(vo.photo);

            if(bitmap != null) {
                studentImageView.setImageBitmap(bitmap);
            }
        } else {
            studentImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_student_small, null));
        }

        studentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogRoot = inflater.inflate(R.layout.dialog_student_image, null);
                ImageView dialogImageView = dialogRoot.findViewById(R.id.dialog_image);

                if(vo.photo != null && !vo.photo.equals("")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    Bitmap bitmap = BitmapFactory.decodeFile(vo.photo);

                    if(bitmap != null) {
                        dialogImageView.setImageBitmap(bitmap);
                    }
                } else {
                    dialogImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_student_large, null));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogRoot);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        phoneView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(vo.phone != null && !vo.phone.equals("")) {
                    //call app의 activity를 intent로...
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + vo.phone));
                    context.startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(context, R.string.main_list_phone_error, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return convertView;

    }
}
