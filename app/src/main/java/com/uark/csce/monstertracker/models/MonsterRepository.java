package com.uark.csce.monstertracker.models;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.uark.csce.monstertracker.models.info.CardInfo;
import com.uark.csce.monstertracker.models.info.MonsterInfo;
import com.uark.csce.monstertracker.models.info.Scenario;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonsterRepository {
    // Load-once informational data
    private List<Scenario> scenarios;
    private List<MonsterInfo> monsterInfos;

    // This is instance data. A dictionary of lists of monsters, keyed by monster info name.
    // This data changes frequently and is shared across multiple activities
    private Map<String, List<Monster>> monsterInstanceData;
    private Map<String, CardInfo> currentCardData;

    private static volatile MonsterRepository INSTANCE;
    Gson gson;

    public static MonsterRepository getInstance(@NonNull Context context){
        if(INSTANCE == null){
            synchronized (MonsterRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new MonsterRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    private MonsterRepository(@NonNull Context context) {
        gson = new GsonBuilder().create();
        AssetManager assetManager = context.getAssets();

        loadScenarios(assetManager);
        loadMonsterInfo(assetManager);

        for(MonsterInfo info : monsterInfos) {
            info.setup();
        }

        monsterInstanceData = new HashMap<String, List<Monster>>();
        currentCardData = new HashMap<String, CardInfo>();
    }

    // Instance data access and manipulation. This will throw exceptions if the key isn't found
    public void clearInstanceData() {
        monsterInstanceData = new HashMap<String, List<Monster>>();
    }

    public List<Monster> getMonsters(String monsterInfoName) {
        return monsterInstanceData.get(monsterInfoName);
    }

    public CardInfo getCurrentCard(String monsterInfoName) {
        return currentCardData.get(monsterInfoName);
    }

    public void addMonsterInfo(String monsterInfoName) {
        monsterInstanceData.put(monsterInfoName, new ArrayList<Monster>());
    }

    public void addMonster(String monsterInfoName) {
        MonsterInfo info = getMonsterInfo(monsterInfoName);
        Monster m = new Monster(info, 0, MonsterType.Normal);
        monsterInstanceData.get(monsterInfoName).add(m);
    }

    // Informational data access access
    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public Scenario getScenario(String scenarioName) {
        // I miss Linq .Select()...
        for (int i = 0; i < scenarios.size(); i++) {
            if (scenarios.get(i).getName().equals(scenarioName)) {
                return scenarios.get(i);
            }
        }
        throw new IllegalArgumentException("Cannot find a scenario with name: " + scenarioName);
    }

    public List<MonsterInfo> getMonsterInfos() {
        return monsterInfos;
    }

    public MonsterInfo getMonsterInfo(String monsterName) {
        // ... still miss Linq .Select()...
        for (int i = 0; i < monsterInfos.size(); i++) {
            if (monsterInfos.get(i).getName().equals(monsterName)) {
                return monsterInfos.get(i);
            }
        }
        throw new IllegalArgumentException("Cannot find a monster with name: " + monsterName);
    }

    private void loadScenarios(AssetManager assetManager) {
        try {
            InputStream in = assetManager.open("monster/scenarios.json");
            Reader reader = new InputStreamReader(in);

            scenarios = gson.fromJson(reader, new TypeToken<List<Scenario>>() {
            }.getType());

            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMonsterInfo(AssetManager assetManager) {
        try {
            InputStream in = assetManager.open("monster/monsters.json");
            Reader reader = new InputStreamReader(in);

            monsterInfos = gson.fromJson(reader, new TypeToken<List<MonsterInfo>>() { }.getType());
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
