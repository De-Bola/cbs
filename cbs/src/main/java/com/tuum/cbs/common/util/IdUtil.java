package com.tuum.cbs.common.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
public class IdUtil {

    public static final Instant TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
    private static final String CLASS_NAME = "IdUtil";

    /**
     * for generating random uuids for trx and accounts
     * */
    public static UUID generateUUID() {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " generate a random uuid");
        final UUID randomUUID = UUID.randomUUID();
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " random id generated: " + randomUUID);
        return randomUUID;
    }

    /**
     * for generating random ids of type Long for balances
     * */
    public static Long generateRandomId() {
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " generate a random id");
        String format = String.format("%010d", new BigInteger(generateUUID().toString().replace("-", ""), 16));
        format = format.substring(format.length() - 10);
        LOGGER.info("[" + TIMESTAMP + "]: " + CLASS_NAME + " random id generated: " + format);
        return Long.valueOf(format);
    }
}
