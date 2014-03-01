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
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uphyca.idobata.Idobata;
import com.uphyca.idobata.IdobataError;
import com.uphyca.idobata.model.Seed;
import com.uphyca.robota.InjectionUtils;
import com.uphyca.robota.R;
import com.uphyca.robota.data.api.ApiToken;
import com.uphyca.robota.data.api.Enabled;
import com.uphyca.robota.data.api.Main;
import com.uphyca.robota.data.api.Networking;
import com.uphyca.robota.data.prefs.BooleanPreference;
import com.uphyca.robota.data.prefs.StringPreference;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class BotActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
    }

    public static class BotFragment extends Fragment {

        @Inject
        Idobata mIdobata;

        @Inject
        @Networking
        Executor mExecutor;

        @Inject
        @Main
        Executor mDispatcher;

        @Inject
        @ApiToken
        StringPreference mApiTokenPreference;

        @Inject
        @Enabled
        BooleanPreference mEnabledPreference;

        @InjectView(R.id.api_token)
        EditText mApiToken;

        @InjectView(R.id.update)
        Button mUpdateButton;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            InjectionUtils.getObjectGraph(getActivity())
                          .inject(this);
            mApiToken.setText(mApiTokenPreference.get());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_bot, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ButterKnife.inject(this, view);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.reset(this);
        }

        @OnClick(R.id.update)
        void updateAPIToken() {
            getActivity().setProgressBarIndeterminate(true);
            getActivity().setProgressBarIndeterminateVisibility(true);

            mUpdateButton.setEnabled(false);

            mApiTokenPreference.set(mApiToken.getText()
                                             .toString());

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Seed seed = mIdobata.getSeed();
                        mDispatcher.execute(new Runnable() {
                            @Override
                            public void run() {
                                onSuccess();
                            }
                        });
                    } catch (final IdobataError idobataError) {
                        idobataError.printStackTrace();
                        mDispatcher.execute(new Runnable() {
                            @Override
                            public void run() {
                                onError(idobataError);
                            }
                        });
                    } finally {
                        mDispatcher.execute(new Runnable() {
                            @Override
                            public void run() {
                                mUpdateButton.setEnabled(true);
                                getActivity().setProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                }
            });
        }

        private void onSuccess() {
            String text = getResources().getString(R.string.authorized);
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT)
                 .show();
        }

        private void onError(Exception e) {
            mApiTokenPreference.delete();
            mEnabledPreference.set(false);
            String text = getResources().getString(R.string.authentication_failed, e.getMessage());
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT)
                 .show();
        }
    }
}
