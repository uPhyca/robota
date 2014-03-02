
package com.uphyca.robota.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.uphyca.robota.engine.Robota.EXTRA_BODY;
import static com.uphyca.robota.engine.Robota.EXTRA_BODY_PLAIN;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_API_TOKEN;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_ICON_URL;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_BOT_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_CREATED_AT;
import static com.uphyca.robota.engine.Robota.EXTRA_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_IMAGE_URLS;
import static com.uphyca.robota.engine.Robota.EXTRA_MENTIONS;
import static com.uphyca.robota.engine.Robota.EXTRA_MULTILINE;
import static com.uphyca.robota.engine.Robota.EXTRA_ORGANIZATION_SLUG;
import static com.uphyca.robota.engine.Robota.EXTRA_ROOM_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_ROOM_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_ICON_URL;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_ID;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_NAME;
import static com.uphyca.robota.engine.Robota.EXTRA_SENDER_TYPE;

public abstract class EngineBase extends BroadcastReceiver {

    private static final String HELP_PATTERN_TEMPLATE = "^[@]?%s[:,]?\\s*(?:help\\s*((.*)?)$)";
    private static final String HELP_RESULT_TEMPLATE = "%s %s - %s";
    private static final String ERROR_MESSAGE_TEMPLATE = "No available commands match %s";

    private static final Help HELP = new Help("help", "Displays all of the help commands that hubot knows about.");
    private static final Help QUERY_HELP = new Help("help <query>", "Displays all help commands that match <query>.");

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Robota.ACTION_MESSAGE_CREATED)) {
            try {
                handleMessageCreated(context, intent);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    protected abstract String onMessageReceived(Context context, Bot bot, TextMessage textMessage);

    protected abstract Help describe(Context context);

    private void handleMessageCreated(Context context, Intent intent) {

        Bot bot = parseBot(intent);
        TextMessage textMessage = parseTextMessage(intent);

        if (handleHelp(context, bot, textMessage)) {
            return;
        }

        if (getResultData() != null) {
            return;
        }

        String result = onMessageReceived(context, bot, textMessage);
        if (result != null) {
            setResultData(result);
        }
    }

    private boolean handleHelp(Context context, Bot bot, TextMessage textMessage) {
        Pattern pt = Pattern.compile(String.format(HELP_PATTERN_TEMPLATE, bot.getName()), Pattern.CASE_INSENSITIVE);
        Matcher mt = pt.matcher(textMessage.getText());
        if (!mt.find()) {
            return false;
        }
        String query = mt.group(1);

        if (TextUtils.isEmpty(query) || HELP.getEvent()
                                            .matches(query)) {
            pushHelp(bot, query, HELP);
        }

        if (TextUtils.isEmpty(query) || QUERY_HELP.getEvent()
                                                  .matches(query)) {
            pushHelp(bot, query, QUERY_HELP);
        }

        Help help = describe(context);

        if (hasDescribed(help) && TextUtils.isEmpty(query) || help.getEvent()
                                                                  .matches(query)) {
            pushHelp(bot, query, help);
        }

        pushErrorMessageIfNecessary(query);

        return true;
    }

    private void pushErrorMessageIfNecessary(String query) {
        String resultData = getResultData();
        if (resultData != null) {
            return;
        }

        String errorMessage = String.format(ERROR_MESSAGE_TEMPLATE, query);
        setResultData(errorMessage);
    }

    private boolean hasDescribed(Help help) {
        return help != null && help.getEvent() != null && help.getDescription() != null;
    }

    private void pushHelp(Bot bot, String query, Help help) {
        String resultData = getResultData();
        String helpMessage = String.format(HELP_RESULT_TEMPLATE, bot.getName(), help.getEvent(), help.getDescription());

        if (resultData != null && resultData.contains(helpMessage)) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        String errorMessage = String.format(ERROR_MESSAGE_TEMPLATE, query);

        if (resultData != null && !resultData.equals(errorMessage)) {
            builder.append(resultData);
            builder.append('\n');
        }

        builder.append(helpMessage.replaceAll("[\\r\\n]+", " "));
        String sortedResult = sortHelp(builder.toString());
        setResultData(sortedResult);
    }

    private String sortHelp(String help) {
        String[] lines = help.split("\n");
        TreeSet<String> sorter = new TreeSet<String>();
        sorter.addAll(Arrays.asList(lines));
        StringBuilder builder = new StringBuilder();
        for (String each : sorter) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(each);
        }
        return builder.toString();
    }

    private Bot parseBot(Intent intent) {

        long id = intent.getLongExtra(EXTRA_BOT_ID, 0);
        String name = intent.getStringExtra(EXTRA_BOT_NAME);
        String apiToken = intent.getStringExtra(EXTRA_BOT_API_TOKEN);
        String iconUrl = intent.getStringExtra(EXTRA_BOT_ICON_URL);

        return new Bot().setId(id)
                        .setName(name)
                        .setApiToken(apiToken)
                        .setIconUrl(iconUrl);
    }

    private TextMessage parseTextMessage(Intent intent) {

        long id = intent.getLongExtra(EXTRA_ID, 0);
        String body = intent.getStringExtra(EXTRA_BODY);
        String bodyPlain = intent.getStringExtra(EXTRA_BODY_PLAIN);
        String[] imageUrls = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);
        boolean multiline = intent.getBooleanExtra(EXTRA_MULTILINE, false);
        long[] mentions = intent.getLongArrayExtra(EXTRA_MENTIONS);
        String createdAt = intent.getStringExtra(EXTRA_CREATED_AT);
        long roomId = intent.getLongExtra(EXTRA_ROOM_ID, 0);
        String roomName = intent.getStringExtra(EXTRA_ROOM_NAME);
        String organizationSlug = intent.getStringExtra(EXTRA_ORGANIZATION_SLUG);
        String senderType = intent.getStringExtra(EXTRA_SENDER_TYPE);
        long senderId = intent.getLongExtra(EXTRA_SENDER_ID, 0);
        String senderName = intent.getStringExtra(EXTRA_SENDER_NAME);
        String senderIconUrl = intent.getStringExtra(EXTRA_SENDER_ICON_URL);

        User user = new User().setId(senderId)
                              .setName(senderName);

        MessageBundle bundle = new MessageBundle().setId(id)
                                                  .setBody(body)
                                                  .setBodyPlain(bodyPlain)
                                                  .setImageUrls(Arrays.asList(imageUrls))
                                                  .setMultiline(multiline)
                                                  .setMentions(asList(mentions))
                                                  .setCreatedAt(createdAt)
                                                  .setRoomId(roomId)
                                                  .setRoomName(roomName)
                                                  .setOrganizationSlug(organizationSlug)
                                                  .setSenderType(senderType)
                                                  .setSenderId(senderId)
                                                  .setSenderName(senderName)
                                                  .setSenderIconUrl(senderIconUrl);
        return new TextMessage().setUser(user)
                                .setText(bodyPlain)
                                .setData(bundle);
    }

    private static List<Long> asList(long[] array) {
        ArrayList<Long> list = new ArrayList<Long>();
        for (long each : array) {
            list.add(each);
        }
        return list;
    }
}
