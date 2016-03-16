package com.makaan.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerJourneyActivity;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.pojo.SerpRequest;


/**
 * A Helper class with various utilities
 * for creating notifications
 */

public class NotificationHelper {

    public static final int REQUEST_CODE_GCM = 100;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_TARGET_URL = "target_url";
    public static final String BUTTON_LABEL = "button_label";

    /**
     * Enum for determining notification type
     */
    public enum NotificationType {
        BROWSER(101), SCREEN_LAUNCHER(102), NONE(103);

        int value;

        NotificationType(int code) {
            value = code;
        }

        public int getValue() {
            return value;
        }

        public static NotificationType fromTypeId(int typeId) {
            for (NotificationType type : NotificationType.values()) {
                if (typeId == type.getValue()) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * Enum for determining activity name
     */
    public enum ScreenType {

        FAVORITE_PROJECT(201), PROJECT_PAGE(202),
        SERP_PAGE(301), LISTING_PAGE(302),
        CITY_PAGE(401), LOCALITY_PAGE(402),
        BUYER_DASHBOARD(501), BUYER_SAVED_SEARCHES(502),
        BUYER_SHORTLIST(503), BUYER_SITE_VISIT(504),
        BUYER_CASHBACK(505), BUYER_LOAN(506),PYR(601);

        int value;

        ScreenType(int code) {
            value = code;
        }

        public int getValue() {
            return value;
        }

        public static ScreenType fromTypeId(int typeId) {
            for (ScreenType type : ScreenType.values()) {
                if (typeId == type.getValue()) {
                    return type;
                }
            }
            return null;
        }
    }


    /**
     * A utility for creating a NotificationCompact.Builder
     *
     * @param context
     * @param title
     * @param plainText
     */
    public static NotificationCompat.Builder getNotificationBuilder(Context context, String title, String plainText) {

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.makaan_logo);
        int height = (int) context.getResources().getDimension(R.dimen.notification_large_icon_height);
        int width = (int) context.getResources().getDimension(R.dimen.notification_large_icon_width);
        Bitmap icon = Bitmap.createScaledBitmap(largeIcon, height, width, false);
        largeIcon.recycle();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.makaan_logo).setContentTitle(title)
                .setContentText(plainText).setWhen(System.currentTimeMillis()).setTicker(title)
                .setLargeIcon(icon)
                .setAutoCancel(true);

        return mBuilder;
    }


    /**
     * common notify wrapper
     *
     * @param context
     * @param attributes
     * @param mBuilder
     */
    public static void notify(Context context, NotificationAttributes attributes, NotificationCompat.Builder mBuilder) {

        if (context == null || mBuilder == null || attributes == null) {
            return;
        }

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = null;

        if (attributes.getBitmap() == null) {

            NotificationCompat.BigTextStyle notiStyle =
                    new NotificationCompat.BigTextStyle().bigText(attributes.getMessage());
            notiStyle.setBigContentTitle(attributes.getTitle());

            notification = mBuilder.setStyle(notiStyle).build();

        } else {
            NotificationCompat.BigPictureStyle notiStyle =
                    NotificationHelper.getNotificationBigPictureStyle(context, attributes.getBitmap());

            notiStyle.setBigContentTitle(attributes.getTitle());
            notiStyle.setSummaryText(attributes.getMessage());

            notification = mBuilder.setStyle(notiStyle).build();
        }

        if (notification != null) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;

            mNotifyMgr.notify(attributes.getNotificationId(), notification);
        }
    }


    /**
     * A method for creating a BigPictureStyle for
     * expandable notifications
     *
     * @param context
     */
    public static NotificationCompat.BigPictureStyle getNotificationBigPictureStyle(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        // Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();

        // Add the big picture to the style.
        notiStyle.bigPicture(bitmap);

        return notiStyle;

    }

    /**
     * Gets intent for launching an in-app activity
     *
     * @param context
     * @param attributes {@link NotificationAttributes}
     */
    public static Intent getIntentFromAttribute(Context context, NotificationAttributes attributes) {
        Intent resultIntent = null;

        ScreenType screenType =
                ScreenType.fromTypeId(attributes.getNotificationPayload().getScreenTypeId());
        if (screenType == null) {
            return null;
        }

        switch (screenType) {
            case PROJECT_PAGE:
                resultIntent = getProjectPageIntent(context, attributes.getNotificationPayload());
                break;
            case CITY_PAGE:
                resultIntent = getCityPageIntent(context, attributes.getNotificationPayload());
                break;
            case LOCALITY_PAGE:
                resultIntent = getLocalityPageIntent(context, attributes.getNotificationPayload());
                break;
            case LISTING_PAGE:
                resultIntent = getListingPageIntent(context, attributes.getNotificationPayload());
                break;
            case SERP_PAGE:
                resultIntent = getSerpPageIntent(context, attributes.getNotificationPayload());
                break;
            case BUYER_DASHBOARD:
                resultIntent = getBuyerJourneyIntent(context, attributes.getNotificationPayload());
                break;
            case BUYER_CASHBACK:
            case BUYER_SAVED_SEARCHES:
            case BUYER_SHORTLIST:
            case BUYER_LOAN:
            case BUYER_SITE_VISIT:
                resultIntent = getDashboardIntent(context, attributes.getNotificationPayload());
                break;
            case PYR:
                resultIntent = getPyrIntent(context);
                break;

        }


        return resultIntent;
    }

    private static Intent getProjectPageIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        long projectId = payload.getProjectId();
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.PROJECT_ID, projectId);
        return intent;
    }

    private static Intent getCityPageIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        long cityId = payload.getCityId();
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra(CityActivity.CITY_ID, cityId);
        return intent;
    }

    private static Intent getLocalityPageIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        long localityId = payload.getLocalityId();
        Intent intent = new Intent(context, LocalityActivity.class);
        intent.putExtra(LocalityActivity.LOCALITY_ID, localityId);
        return intent;
    }

    private static Intent getListingPageIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        long listingId = payload.getListingId();
        Intent intent = new Intent(context, PropertyActivity.class);
        intent.putExtra(PropertyActivity.LISTING_ID, listingId);
        return intent;
    }

    private static Intent getSerpPageIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        if(payload.getSerpFilterUrl() == null) {
            return null;
        }
        String serpFilterUrl = payload.getSerpFilterUrl();
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_NOTIFICATION);

        return request.getSerpLaunchIntent(context, serpFilterUrl);
    }

    private static Intent getBuyerJourneyIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        if (context instanceof BuyerJourneyActivity) {
            return null;
        } else {
            return new Intent(context, BuyerJourneyActivity.class);
        }
    }

    private static Intent getDashboardIntent(Context context, NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        Intent intent = new Intent(context, BuyerDashboardActivity.class);
        ScreenType screenType =
                ScreenType.fromTypeId(payload.getScreenTypeId());
        if (screenType == null) {
            return null;
        }

        switch (screenType) {
            case BUYER_CASHBACK:
                intent.putExtra(BuyerDashboardActivity.TYPE, BuyerDashboardActivity.LOAD_FRAGMENT_REWARDS);
                break;

            case BUYER_SAVED_SEARCHES:
                intent.putExtra(BuyerDashboardActivity.TYPE, BuyerDashboardActivity.LOAD_FRAGMENT_SAVE_SEARCH);
                break;

            case BUYER_SHORTLIST:
                intent.putExtra(BuyerDashboardActivity.TYPE, BuyerDashboardActivity.LOAD_FRAGMENT_SHORTLIST);
                break;

            case BUYER_LOAN:
                intent.putExtra(BuyerDashboardActivity.TYPE, BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
                intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.HOME_LOAN);
                break;

            case BUYER_SITE_VISIT:
                intent.putExtra(BuyerDashboardActivity.TYPE, BuyerDashboardActivity.LOAD_FRAGMENT_SITE_VISIT);
                break;
        }


        return intent;
    }

    private static Intent getPyrIntent(Context context){
        Intent pyrIntent = new Intent(context, PyrPageActivity.class);
        return pyrIntent;
    }


}