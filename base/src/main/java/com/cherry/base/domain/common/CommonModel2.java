package com.cherry.base.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年09月04日 11:34:00
 * ClassName CommonModel2
 * packageName com.cherry.base.domain.common
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonModel2<T1, T2> {

    private T1 t1;

    private T2 t2;

}
