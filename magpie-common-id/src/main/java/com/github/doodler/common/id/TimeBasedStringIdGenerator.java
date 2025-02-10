package com.github.doodler.common.id;

import java.util.UUID;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * 
 * @Description: TimeBasedStringIdGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public class TimeBasedStringIdGenerator extends AbstractStringIdGenerator {

    @Override
    public String getNextId() {
        TimeBasedGenerator generator =
                Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        UUID uuid = generator.generate();
        return (lastId = uuid.toString());
    }

}
