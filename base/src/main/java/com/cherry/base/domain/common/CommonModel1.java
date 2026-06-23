package com.cherry.base.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年09月04日 11:06:00
 * ClassName CommonModel1
 * packageName com.cherry.base.domain.model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonModel1<T> {

    private T t1;

}
