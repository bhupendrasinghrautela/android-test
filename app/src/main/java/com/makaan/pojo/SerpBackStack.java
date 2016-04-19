package com.makaan.pojo;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.util.Stack;

/**
 * Created by rohitgarg on 2/9/16.
 */
public class SerpBackStack {
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_MAP = 2;
    Stack<SingleSerpBackstack> singleSerpBackstacks = new Stack<>();

    public SerpRequest addToBackstack(SerpRequest request, int type) {
        if(singleSerpBackstacks.size() > 0) {
            if(singleSerpBackstacks.peek().type == type) {
                try {
                    return singleSerpBackstacks.peek().addToBackstack(request.clone());
                } catch (CloneNotSupportedException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    return null;
                }
            } else {
                singleSerpBackstacks.add(new SingleSerpBackstack(type));
                return addToBackstack(request, type);
            }
        } else {
            singleSerpBackstacks.add(new SingleSerpBackstack(type));
            return addToBackstack(request, type);
        }
    }

    public boolean popFromBackstack(Context context) {
        if(singleSerpBackstacks.size() > 0) {
            boolean success = singleSerpBackstacks.peek().popFromBackstack(context);
            if(!success) {
                do {
                    singleSerpBackstacks.pop();
                    if (singleSerpBackstacks.size() > 0) {
                        if (singleSerpBackstacks.peek().serpRequests.size() > 0) {
                            SerpRequest request = singleSerpBackstacks.peek().serpRequests.peek();
                            request.setIsFromBackstack(true);
                            request.launchSerp(context);
                            return true;
                        }
                    } else {
                        return false;
                    }
                } while (singleSerpBackstacks.size() > 0);
            } else {
                return true;
            }
        }
        return false;
    }

    public SerpRequest peek() {
        if(singleSerpBackstacks.size() > 0) {
            return singleSerpBackstacks.peek().peek();
        }
        return null;
    }

    public int peekType() {
        if(singleSerpBackstacks.size() > 0) {
            return singleSerpBackstacks.peek().type;
        }
        return -1;
    }

    class SingleSerpBackstack {
        Stack<SerpRequest> serpRequests = new Stack<>();
        int type;

        SingleSerpBackstack(int type) {
            this.type = type;
        }

        public SerpRequest addToBackstack(SerpRequest request) {
            if(request == null) {
                return null;
            }
            if(!request.isFromBackstack() || request.getBackStackType() != type) {
                request.setBackStackType(type);
                request.setIsFromBackstack(false);
                serpRequests.add(request);
                return request;
            }
            return null;
        }

        public boolean popFromBackstack(Context context) {
            if(serpRequests.size() > 0) {
                serpRequests.pop();
                if(serpRequests.size() > 0) {
                    SerpRequest request = serpRequests.peek();
                    request.setIsFromBackstack(true);
                    request.launchSerp(context);
                } else {
                    return false;
                }
                return true;
            }
            return false;
        }

        public SerpRequest peek() {
            if(serpRequests.size() > 0) {
                return serpRequests.peek();
            }
            return null;
        }
    }
}
