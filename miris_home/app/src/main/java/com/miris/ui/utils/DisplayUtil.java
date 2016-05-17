/*
 * Copyright (C) RECRUIT LIFESTYLE CO., LTD.
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

package com.miris.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.lang.reflect.InvocationTargetException;

public class DisplayUtil {

    private static String       sDeviceType              = null;
    public static final String  DEVICE_TYPE_PHONE        = "P";
    public static final String  DEVICE_TYPE_TABLET       = "T";
    public static final String  SYSTEM_PROPERTIES_CLASS  = "android.os.SystemProperties";

    private DisplayUtil(){}

    public static boolean isOver600dp(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density >= 600;
    }

    public synchronized static String getDeviceType() {

        if (sDeviceType == null) {
            sDeviceType = DEVICE_TYPE_PHONE;
            final String className = SYSTEM_PROPERTIES_CLASS;
            final String method = "get";
            final String arg = "ro.build.characteristics";
            String response = invokeHiddenMethod(className, method, String.class, arg);
            if (response != null && response.contains("tablet")) {
                sDeviceType = DEVICE_TYPE_TABLET;
            }
        }
        return sDeviceType;
    }

    @SuppressWarnings("rawtypes")
    public static String invokeHiddenMethod(String className, String method, Class argType,
                                            String arg) {
        try {
            return (String)Class.forName(className).getMethod(method, argType).invoke(null, arg);
        } catch (NoSuchMethodException e) {
            e.getMessage();
        } catch (ClassNotFoundException e) {
            e.getMessage();
        } catch (IllegalArgumentException e) {
            e.getMessage();
        } catch (IllegalAccessException e) {
            e.getMessage();
        } catch (InvocationTargetException e) {
            e.getMessage();
        }
        return null;
    }

    public static boolean isCellPhone(String input_phone) {
        return (input_phone.replace("-", "")).matches("(01[016789])(\\d{3,4})(\\d{4})");
    }
}
