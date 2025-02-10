package com.github.doodler.common.tx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @Description: TxRequest
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxRequest {

    private String txName;
    private String txId;

}
