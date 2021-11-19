package com.uark.csce.monstertracker.MonsterDetailsActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uark.csce.monstertracker.R;
import com.uark.csce.monstertracker.models.info.AttributesInfo;
import com.uark.csce.monstertracker.models.info.MonsterInfo;
import com.uark.csce.monstertracker.models.info.MonsterStatsInfo;

import java.io.IOException;
import java.io.InputStream;

public class MonsterDetailsFragment extends Fragment implements MonsterDetailsContract.View {
    MonsterDetailsContract.Presenter presenter;
    private MonsterDetailsAdapter adapter;
    private RecyclerView rvMonsterList;
    private View root;

    private AssetManager assetManager;

    public MonsterDetailsFragment() {
        // Required empty public constructor
    }

    public static MonsterDetailsFragment newInstance() {
        MonsterDetailsFragment fragment = new MonsterDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getContext().getAssets();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_monster_details, container, false);

        Button btnLevelAdd = (Button)root.findViewById(R.id.btnLevelAdd);
        Button btnLevelSubtract = (Button)root.findViewById(R.id.btnLevelSubtract);
        Button btnMonsterAddNormal = (Button)root.findViewById(R.id.btnMonsterAddNormal);
        Button btnMonsterAddElite = (Button)root.findViewById(R.id.btnMonsterAddElite);
        btnLevelAdd.setOnClickListener(levelAddAction);
        btnLevelSubtract.setOnClickListener(levelSubtractAction);
        btnMonsterAddNormal.setOnClickListener(monsterAddNormalAction);
        btnMonsterAddElite.setOnClickListener(monsterAddEliteAction);

        ImageView ivHealth = (ImageView)root.findViewById(R.id.ivIconHealth);
        ImageView ivMove = (ImageView)root.findViewById(R.id.ivIconMove);
        ImageView ivAttack = (ImageView)root.findViewById(R.id.ivIconAttack);
        ImageView ivRange = (ImageView)root.findViewById(R.id.ivIconRange);

        try {
            ivHealth.setImageBitmap(getBitmapFromAssets("icons/health.PNG"));
            ivMove.setImageBitmap(getBitmapFromAssets("icons/move.PNG"));
            ivAttack.setImageBitmap(getBitmapFromAssets("icons/attack.PNG"));
            ivRange.setImageBitmap(getBitmapFromAssets("icons/range.PNG"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new MonsterDetailsAdapter();
        rvMonsterList = root.findViewById(R.id.rvMonsterList);
        rvMonsterList.setAdapter(adapter);
        rvMonsterList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        MonsterInfo info = presenter.getMonsterInfo();
        populateInfo(info);
        populateAttributes(info);
    }

    @Override
    public void setPresenter(MonsterDetailsContract.Presenter presenter) {
        this.presenter = presenter;
        adapter.setPresenter(presenter);
    }

    private void populateInfo(MonsterInfo info) {
        // Set up normal and elite info
        MonsterStatsInfo stats = info.getStats().get(presenter.getLevel());
        ((TextView)root.findViewById(R.id.tvNormalHealth)).setText(Integer.toString(stats.getNormal().getHealth()));
        ((TextView)root.findViewById(R.id.tvNormalMove)).setText(Integer.toString(stats.getNormal().getMove()));
        ((TextView)root.findViewById(R.id.tvNormalAttack)).setText(Integer.toString(stats.getNormal().getAttack()));
        ((TextView)root.findViewById(R.id.tvNormalRange)).setText(Integer.toString(stats.getNormal().getRange()));
        ((TextView)root.findViewById(R.id.tvEliteHealth)).setText(Integer.toString(stats.getElite().getHealth()));
        ((TextView)root.findViewById(R.id.tvEliteMove)).setText(Integer.toString(stats.getElite().getMove()));
        ((TextView)root.findViewById(R.id.tvEliteAttack)).setText(Integer.toString(stats.getElite().getAttack()));
        ((TextView)root.findViewById(R.id.tvEliteRange)).setText(Integer.toString(stats.getElite().getRange()));

        ((TextView)root.findViewById(R.id.tvLevel)).setText("Lvl. " + Integer.toString(presenter.getLevel()));

        ImageView ivPortrait = (ImageView)root.findViewById(R.id.ivMonsterPortrait);
        try {
            ivPortrait.setImageBitmap(getBitmapFromAssets("portraits/" + info.getName() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateAttributes(MonsterInfo info) {
        // Reverse case! Time to parse all. of. the. attributes. :(
        AttributesInfo normalAttr = info.getStats().get(presenter.getLevel()).getNormal().getAttributesInfo();
        AttributesInfo eliteAttr = info.getStats().get(presenter.getLevel()).getElite().getAttributesInfo();

        AttributesInfo[] infos = new AttributesInfo[] {normalAttr, eliteAttr};
        StringBuilder[] attributeTexts = new StringBuilder[] {new StringBuilder(), new StringBuilder()};
        for (int i = 0; i < 2; i++) {
            StringBuilder sb = attributeTexts[i];
            if (infos[i].isAttackersGainDisadvantage()) {
                sb.append("Attackers gain Disadvantage\n");
            }
            if (infos[i].isFlying()) {
                sb.append("Flying\n");
            }
            if (infos[i].isImmuneToCurse()) {
                sb.append("Curse\n");
            }
            if (infos[i].isImmuneToDisarm()) {
                sb.append("Disarm\n");
            }
            if (infos[i].isImmuneToImmobilize()) {
                sb.append("Immobilize\n");
            }
            if (infos[i].isImmuneToMuddle()) {
                sb.append("Muddle\n");
            }
            if (infos[i].isImmuneToPoison()) {
                sb.append("Poison\n");
            }
            if (infos[i].isImmuneToStun()) {
                sb.append("Stun\n");
            }
            if (infos[i].isImmuneToWound()) {
                sb.append("Wound\n");
            }
            if (infos[i].getRetaliate() > 0) {
                if (infos[i].getRetaliateRange() > 1) {
                    sb.append("Retaliate: " + infos[i].getRetaliate() + ", Range: " + infos[i].getRetaliateRange() + "\n");
                }
                else {
                    sb.append("Retaliate: " + infos[i].getRetaliate() + "\n");
                }
            }
            if (infos[i].getShield() > 0) {
                sb.append("Shield: " + infos[i].getShield() + "\n");
            }
            if (infos[i].getTarget() > 0) {
                sb.append("Target: " + infos[i].getTarget() + "\n");
            }
            if (infos[i].getPierce() > 0) {
                sb.append("Pierce: " + infos[i].getPierce() + "\n");
            }
        }

        ((TextView)root.findViewById(R.id.tvNormalAttributesList)).setText(attributeTexts[0].toString());
        ((TextView)root.findViewById(R.id.tvEliteAttributesList)).setText(attributeTexts[1].toString());
    }

    private View.OnClickListener levelAddAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.addLevel();
            populateInfo(presenter.getMonsterInfo());
            populateAttributes(presenter.getMonsterInfo());
        }
    };

    private View.OnClickListener levelSubtractAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.subtractLevel();
            populateInfo(presenter.getMonsterInfo());
            populateAttributes(presenter.getMonsterInfo());
        }
    };

    private View.OnClickListener monsterAddNormalAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.addMonster(false);
            rvMonsterList.getAdapter().notifyDataSetChanged();
        }
    };

    private View.OnClickListener monsterAddEliteAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.addMonster(true);
            rvMonsterList.getAdapter().notifyDataSetChanged();
        }
    };

    private View.OnClickListener drawCardAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO
        }
    };

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        InputStream istr = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }
}