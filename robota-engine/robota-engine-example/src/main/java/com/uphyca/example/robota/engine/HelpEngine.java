
package com.uphyca.example.robota.engine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.uphyca.robota.engine.Bot;
import com.uphyca.robota.engine.EngineBase;
import com.uphyca.robota.engine.Robota;
import com.uphyca.robota.engine.TextMessage;

import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpEngine extends EngineBase {

    private static final String HELP_PATTERN_TEMPLATE = "^[@]?%s[:,]?\\s*(?:help$)";
    private static final String HELP_RESULT_TEMPLATE = "%s - %s";

    @Override
    protected String onMessageReceived(Context context, Bot bot, TextMessage textMessage) {
        Pattern pt = Pattern.compile(String.format(HELP_PATTERN_TEMPLATE, bot.getName()), Pattern.CASE_INSENSITIVE);
        Matcher mt = pt.matcher(textMessage.getText());
        if (!mt.find()) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryBroadcastReceivers(new Intent(Robota.ACTION_MESSAGE_CREATED), 0);
        TreeSet<String> messages = new TreeSet<String>();
        for (ResolveInfo each : resolveInfos) {

            ActivityInfo ai = each.activityInfo;
            final Context packageContext;
            try {
                packageContext = context.createPackageContext(ai.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                continue;
            }
            String label = packageContext.getString(ai.labelRes);
            label = label.replaceAll("\\$\\{bot_name\\}", bot.getName());

            String description = packageContext.getString(ai.descriptionRes);
            description = description.replaceAll("\\$\\{bot_name\\}", bot.getName());

            messages.add(String.format(HELP_RESULT_TEMPLATE, label, description));
        }

        StringBuilder builder = new StringBuilder();
        for (String each : messages) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(each.replaceAll("[\\r\\n]+", " "));
        }
        return builder.toString();
    }
}
