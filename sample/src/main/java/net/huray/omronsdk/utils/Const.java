package net.huray.omronsdk.utils;

import net.huray.omronsdk.ble.enumerate.OHQGender;
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Const {
    public static final String EXTRA_DEVICE_TYPE = "EXTRA_DEVICE_TYPE";

    public static final String PREF_NAME = "PREF_NAME.Pref";
    public static final String PREF_HEM_9200T_ADDRESS = "PREF_HEM_9200T_ADDRESS";

    public static final String PREF_HEM_7155T_ADDRESS = "PREF_HEM_7155T_ADDRESS";

    public static final String PREF_HBF_222T_ADDRESS = "PREF_HBF_222T_ADDRESS";
    public static final String PREF_HBF_222T_USER_INDEX = "PREF_HBF_222T_USER_INDEX";
    public static final String PREF_HBF_222T_SEQUENCE_NUMBER = "PREF_HBF_222T_SEQUENCE_NUMBER";
    public static final String PREF_HBF_222T_DB_CHANGE_KEY = "PREF_HBF_222T_DB_CHANGE_KEY";

    // For Test Only
    public static Map<OHQUserDataKey, Object> getDemoUser() {
        final Map<OHQUserDataKey, Object> userData = new HashMap<>();
        userData.put(OHQUserDataKey.DateOfBirthKey, "2001-01-01");
        userData.put(OHQUserDataKey.HeightKey, new BigDecimal("170.5"));
        userData.put(OHQUserDataKey.GenderKey, OHQGender.Male);

        return userData;
    }
}
