package net.huray.omronsdk.utils;

import net.huray.omronsdk.ble.enumerate.OHQGender;
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Const {
    public static final String EXTRA_DEVICE_TYPE = "EXTRA_DEVICE_TYPE";

    public static final String PREF_NAME = "Phd.Pref";
    public static final String PREF_OMRON_BP_DEVICE_ADDRESS = "PREF_OMRON_BP_DEVICE_ADDRESS";
    public static final String PREF_OMRON_WEIGHT_DEVICE_ADDRESS = "PREF_OMRON_WEIGHT_DEVICE_ADDRESS";
    public static final String PREF_OMRON_WEIGHT_DEVICE_USER_INDEX = "PREF_OMRON_WEIGHT_DEVICE_USER_INDEX";
    public static final String PREF_OMRON_WEIGHT_DEVICE_SEQ = "PREF_OMRON_WEIGHT_DEVICE_SEQNUM";
    public static final String PREF_OMRON_WEIGHT_DEVICE_DB_CHANGE_KEY = "PREF_OMRON_WEIGHT_DEVICE_DB_CHANGE_KEY";

    // For Test Only
    public static Map<OHQUserDataKey, Object> getDemoUser() {
        final Map<OHQUserDataKey, Object> userData = new HashMap<>();
        userData.put(OHQUserDataKey.DateOfBirthKey, "2001-01-01");
        userData.put(OHQUserDataKey.HeightKey, new BigDecimal("170.5"));
        userData.put(OHQUserDataKey.GenderKey, OHQGender.Male);

        return userData;
    }
}
