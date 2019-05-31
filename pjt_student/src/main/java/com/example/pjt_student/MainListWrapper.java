package com.example.pjt_student;

//findViewById 의 성능이슈, 한번은 find한다. find된 viw를 그 다음 이용 시, find하지 않게 저장했다가 획득
//한 항목에 find 대상이 되는 view가 여러개라면 하나하나 따로 저장 획득이 힘들어서...
//wrapper 클래스로 묶어서 퉁으로 저장 획득하려고...

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListWrapper {
    //항목의 모든 view를 선언할 필요는 없다. find 대상이 되는 애들만....
    ImageView studentImageView;
    TextView nameView;
    ImageView phoneView;

    //가정 1 - Adapter에서 view 가 필요할 때 직접 find하지 않고 이 wrapper의 view를 그대로 획득해서 이용...
    //가정 2 - 이 wrapper 객체가 adapter 에서 메모리에 지속시킨다는 가정....
    public MainListWrapper(View root) {
        studentImageView = root.findViewById(R.id.main_item_student_image);
        nameView = root.findViewById(R.id.main_item_name);
        phoneView = root.findViewById(R.id.main_item_contact);
    }
}
