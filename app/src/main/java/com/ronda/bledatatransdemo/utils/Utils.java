package com.ronda.bledatatransdemo.utils;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/01/17
 * Version: v1.0
 */

public class Utils {

    public static String getProperties(BluetoothGattCharacteristic characteristic){

        StringBuilder properties = new StringBuilder();

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_BROADCAST)){
            appendWithAnd(properties, "Broadcast");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_READ)){
            appendWithAnd(properties, "Read");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)){
            appendWithAnd(properties, "Write_no_response");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_WRITE)){
            appendWithAnd(properties, "Write");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_NOTIFY)){
            appendWithAnd(properties, "Notify");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_INDICATE)){
            appendWithAnd(properties, "Indicate");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)){
            appendWithAnd(properties, "Signed_write");
        }

        if (getGattCharacteristicsPropertices(characteristic.getProperties(), BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS)){
            appendWithAnd(properties, "Extended_props");
        }

        return properties.toString();
    }

    public static boolean getGattCharacteristicsPropertices(int property, int targetProperty){
        if ((property & targetProperty) == targetProperty){
            return true;
        }
        return false;
    }

    private static void appendWithAnd(StringBuilder builder, String value){
        if (builder.length() == 0){
            builder.append(value);
        }
        else{
            builder.append(" & "+value);
        }
    }
}
