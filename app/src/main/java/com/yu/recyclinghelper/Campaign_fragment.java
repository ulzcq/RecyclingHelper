package com.yu.recyclinghelper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yu.recyclinghelper.Adapter.CampaignItemClickListener;
import com.yu.recyclinghelper.Adapter.Campaign_adapter;
import com.yu.recyclinghelper.VO.CampaignData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Campaign_fragment extends Fragment{
    ArrayList<CampaignData> items = new ArrayList<CampaignData>();
    private RecyclerView recyclerView;
    private Campaign_adapter adapter;
    private FragmentTransaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.campaign_fragment,container,false);

        final Context context=rootView.getContext();
        recyclerView = rootView.findViewById(R.id.campaign_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new Campaign_adapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CampaignItemClickListener() { //게시글 누르면
            public void onItemClick(Campaign_adapter.ViewHolder holder, View view, int position) {
                CampaignData item = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("campaignItem", item);
                Fragment fragment = new Campaign_explain();
                fragment.setArguments(bundle);
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("campaign");
                transaction.commit();
//                Intent intent = new Intent(getContext(), Campaign_activity.class);
//                intent.putExtras(bundle);//putExtras로 Bundle 데이터를 넘겨주고 여기에서getExtras로 데이터를 참조한다.
//                startActivity(intent);
            }
        });
        getData();

        return rootView;
    }
    private void getData() {
        // 임의의 데이터
        List<String> listTitle = Arrays.asList(
                "Think Eat Save!",
                "Surfurs Againt Sewage (SAS)",
                "[참새클럽] 플라스틱방앗간",
                "바다를 9해줘",
                "Breathe Life"
        );
        List<Integer> listImage = Arrays.asList(
                R.drawable.thinkeatsave,
                R.drawable.sas,
                R.drawable.plastic_gristmill,
                R.drawable.whale,
                R.drawable.breathelife
        );
        List<String> listContent = Arrays.asList(
                "무의식적으로 버려지는 음식을 줄이고 절약하는 캠페인",
                "바다 환경을 깨끗하게 유지하기 위해 영구의 해변청소 캠페인",
                "버려지는 플라스틱들을 모아 새로운 제품으로 탄생시키는 캠페인",
                "국내외 해양오염 문제의 심각성을 공유하고 해양환경 보호를 위한 캠페인",
                "깨끗한 공기를 위한 흑탄소, 지면에서 발생하는 오존과 메탄을 줄이기 캠페인"
        );
        List<String> listExplain = Arrays.asList(
                "UN 농업식량기구에 의하면 전 세계 식품 생산의 약 3분의 1(13억톤😮)이 식품 생산과 소비 시스템에서 손실되거나 낭비되고 있습니다.\n\n'Think Eat Save'는 UN식량농업기구, UN환경계획등으로 이루어진"
                +"Save food Initiative에서 만들어진 캠페인입니다.\n\n"
                +"무의식적으로 버려지는 음식을 줄이고(Think)\n\n음식을 먹는 우리 모두가 참여해야 하며(Eat)\n\n음식물 쓰레기를 줄이고 음식 절약을 위해 효과적으로 저장하자(Save)라는 의미를 담고 있습니다.\n\n"
                +"공식 사이트에 방문하여 이와 관련된 유용한 팁들을 확인하고 환경보호에 참여해보세요.",

                "SAS는 매년 4월, 10월에 영국 해변을 청소하는 캠페인입니다.\n Surfers Againgt Sewage는 환경 단체 중 하나로,\n\n 1990년 영국의 세인트 아그네스와 포스토완 마을의 서퍼들🏄‍에 의해 만들어졌습니다."
                +"마치 생하수에서 수영하는 듯한 느낌을 받아 바다를 깨끗하게 유지해야겠다고 환경캠페인을 시작했다고 합니다.\n\n"
                +"깨끗한 바다와 함께 서핑하고 싶지 않나요?\n\n 해변 청소 캠페인에 참여해보세요.",

                "플라스틱이 재활용되는 경우가 매우 적다는 사실을 알고 계시나요?\n\n 참새클럽을 통해 모은 플라스틱은 가치있는 제품으로 탄생됩니다✨\n\n매년 바다에 흘러가는 플라스틱 800만톤.\n\n"
                + "현재 바다에 버려진 플라스틱 1.5억톤\n\n그러나, 플라스틱이 썩는데 걸리는 시간은 무려 500년이 걸린다는 사실!\n\n캠페인을 통해 플라스틱 쓰레기를 가치있는 제품으로 재탄생시키고 해양오염을 줄이는데 동참합시다💞",

                "바다 이용 성수기를 앞두고 점차 심회되는 국내외 해양오염 문제의 심각성을 공유하고 해양환경 보호를 위한 여러분의 참여를 확대하기 위해 마련되었습니다.\n\n"
                +"캠페인의 9가지 약속은 다음과 같습니다.\n\n"
                +"1) 일회용 마스크 사용 후 끈을 잘라 휴지통에 버리기\n"
                +"2) 담배꽁초 버리지 않기\n"
                +"3) 생활 쓰레기 분리수거\n"
                +"4) 커피스틱, 빨대 사용 줄이기\n"
                +"5) 비닐봉지 보다 장바구니 사용\n"
                +"6) 일회용품 사용 줄이기\n"
                +"7) 야외 활동 후 쓰레기 되가져 오기\n"
                +"8) 그물 등 어구류 버리지 않기\n"
                +"9) 바닷가에서 불꽃놀이 하지 않기\n\n"
                +"9가지 약속 잘 지키고 깨끗한 바다를 즐겨요🌊\n\n 바다 풍경 또는 바다와 함께한 사진과 개인의 바다사랑 실천 약속 한 가지를 \"#바다를_구해줘\" 핵심태그와 함께\n개인 소셜 미디어 계정에 올리는 방식으로 누구나 함께할 수 있습니다.",

                "Breathe Life는 깨끗한 공기🌬를 위해 만들어진 글로벌 캠페인입니다.\n\n대기환경은 우리 신체 건강과 기구 환경 둘 다 영향을 미치고 있음을 알림으로써 흑탄소, 지면에서 발생하는 오존과 메탄을 줄이는 것을 강조합니다.\n\n"
                +"전 세계에 있는 69개의 도시가 참여하며 2030년까지 WHO 대기질 목표 달성을 위해 도시별 모범 사례를 공유하고 진행상황을 제공합니다.\n\n"
                +"최근에는 서울시가 발표한 경유차 퇴출 추진계획이 모범사례로 채택되었습니다.\n\n"
                +"공식 사이트에 접속하고 숨쉬기 편한 공기 만드는 캠페인에 참여해보세요🙆‍"
        );
        List<String> listURL = Arrays.asList(
                "https://www.unep.org/thinkeatsave/",
                "https://www.sas.org.uk/?gclid=EAIaIQobChMI9dGr-uLf8gIVGBdgCh1sgginEAAYASAAEgKjQ_D_BwE",
                "https://ppseoul.com/mill",
                "http://www.kcg.go.kr/kcg/na/ntt/selectNttInfo.do?nttSn=30413",
                "https://breathelife2030.org/"
        );


        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            CampaignData data = new CampaignData();
            data.setTitle(listTitle.get(i));
            data.setResId(listImage.get(i));
            data.setContent(listContent.get(i));
            data.setExplain(listExplain.get(i));
            data.setUrl(listURL.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}
