package com.ronda.bledatatransdemo;

import java.util.HashMap;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/01/16
 * Version: v1.0
 */

public class GattAttributes {
    private static HashMap<String, String> mAttributeMap = new HashMap<>();

    /**
     * Services
     */

    // 有人服务 (目前是只用上了这个服务)
    public final static String USR_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";

    // 读写数据的characteristic
    public final static String NOTIFY_CHARAC = "0003cdd1-0000-1000-8000-00805f9b0131";
    public final static String WRITE_NO_RESPONSE_CHARAC = "0003cdd2-0000-1000-8000-00805f9b0131";

    /**
     * Gatt services
     */
    public static final String GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";

    public static final String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String HEALTH_TEMP_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE = "0000cab5-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE_CUSTOM = "0003cab5-0000-1000-8000-00805f9b0131";
    public static final String RGB_LED_SERVICE = "0000cbbb-0000-1000-8000-00805f9b34fb";
    public static final String RGB_LED_SERVICE_CUSTOM = "0003cbbb-0000-1000-8000-00805f9b0131";
    public static final String LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb";
    public static final String TRANSMISSION_POWER_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static final String BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
    public static final String GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb";
    public static final String RSC_SERVICE = "00001814-0000-1000-8000-00805f9b34fb";
    public static final String BAROMETER_SERVICE = "00040001-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_SERVICE = "00040020-0000-1000-8000-00805f9b0131";
    public static final String ANALOG_TEMPERATURE_SERVICE = "00040030-0000-1000-8000-00805f9b0131";
    public static final String CSC_SERVICE = "00001816-0000-1000-8000-00805f9b34fb";
    public static final String HUMAN_INTERFACE_DEVICE_SERVICE = "00001812-0000-1000-8000-00805f9b34fb";
    public static final String SCAN_PARAMETERS_SERVICE = "00001813-0000-1000-8000-00805f9b34fb";
    // public static final String OTA_UPDATE_SERVICE = "00060000-0000-1000-8000-00805f9b34fb";
    public static final String OTA_UPDATE_SERVICE = "00060000-f8ce-11e4-abf4-0002a5d5c51b";

    static {
        //mAttributeMap.put()
        mAttributeMap.put(USR_SERVICE,"USR Service");
        mAttributeMap.put(HEART_RATE_SERVICE, "Heart Rate Service");
        mAttributeMap.put(GENERIC_ACCESS_SERVICE, "Generic Access Service");
        mAttributeMap.put(GENERIC_ATTRIBUTE_SERVICE, "Generic Attribute Service");
        mAttributeMap
                .put(DEVICE_INFORMATION_SERVICE, "Device Information Service");
        mAttributeMap.put(BATTERY_SERVICE,// "0000180f-0000-1000-8000-00805f9b34fb",
                "Battery Service");
        mAttributeMap.put(IMMEDIATE_ALERT_SERVICE, "Immediate Alert");
        mAttributeMap.put(LINK_LOSS_SERVICE, "Link Loss");
        mAttributeMap.put(TRANSMISSION_POWER_SERVICE, "Tx Power");
        mAttributeMap.put(CAPSENSE_SERVICE, "CapSense Service");
        mAttributeMap.put(CAPSENSE_SERVICE_CUSTOM, "CapSense Service");
        mAttributeMap.put(RGB_LED_SERVICE, "RGB LED Service");
        mAttributeMap.put(RGB_LED_SERVICE_CUSTOM, "RGB LED Service");
        mAttributeMap.put(GLUCOSE_SERVICE, "Glucose Service");
        mAttributeMap.put(BLOOD_PRESSURE_SERVICE, "Blood Pressure Service");
        mAttributeMap.put(RSC_SERVICE, "Running Speed & Cadence Service");
        mAttributeMap.put(BAROMETER_SERVICE, "Barometer Service");
        mAttributeMap.put(ACCELEROMETER_SERVICE, "Accelerometer Service");
        mAttributeMap
                .put(ANALOG_TEMPERATURE_SERVICE, "Analog Temperature Service");
        mAttributeMap.put(CSC_SERVICE, "Cycling Speed & Cadence Service");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = mAttributeMap.get(uuid);
        return name == null ? defaultName : name;
    }
}
