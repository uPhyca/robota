/*
 * Copyright (C) 2014 uPhyca Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uphyca.robota.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.uphyca.idobata.Idobata;
import com.uphyca.idobata.IdobataError;
import com.uphyca.idobata.model.Seed;
import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.R;
import com.uphyca.robota.Robota;
import com.uphyca.robota.data.api.Main;
import com.uphyca.robota.data.api.Networking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class InstalledEnginesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_engines);
    }

    public static class InstalledEnginesListFragment extends ListFragment {

        @Inject
        Idobata mIdobata;

        @Inject
        @Networking
        Executor mExecutor;

        @Inject
        @Main
        Executor mDispatcher;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            InjectionUtils.getObjectGraph(getActivity())
                          .inject(this);

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String botName;
                    try {
                        Seed seed = mIdobata.getSeed();
                        botName = seed.getRecords()
                                      .getBot()
                                      .getName();
                    } catch (IdobataError idobataError) {
                        idobataError.printStackTrace();
                        botName = "robota";
                    }
                    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    String[] from = {
                            "text1", "text2"
                    };
                    int[] to = {
                            android.R.id.text1, android.R.id.text2
                    };

                    PackageManager pm = getActivity().getPackageManager();
                    List<ResolveInfo> resolveInfos = pm.queryBroadcastReceivers(new Intent(Robota.ACTION_MESSAGE_CREATED), 0);

                    for (ResolveInfo each : resolveInfos) {

                        ActivityInfo ai = each.activityInfo;
                        final Context packageContext;
                        try {
                            packageContext = getActivity().createPackageContext(ai.packageName, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                        String label = packageContext.getString(ai.labelRes);
                        label = label.replaceAll("\\$\\{bot_name\\}", botName);

                        String description = packageContext.getString(ai.descriptionRes);
                        description = description.replaceAll("\\$\\{bot_name\\}", botName);

                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put("text1", label);
                        item.put("text2", description);
                        list.add(item);
                    }

                    Collections.sort(list, new Comparator<HashMap<String, String>>() {
                        @Override
                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                            return lhs.get("text1")
                                      .compareTo(rhs.get("text1"));
                        }
                    });

                    final SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, from, to);
                    mDispatcher.execute(new Runnable() {
                        @Override
                        public void run() {
                            setListAdapter(adapter);
                        }
                    });
                }
            });
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
        }
    }
}
