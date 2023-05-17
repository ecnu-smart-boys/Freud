package org.ecnusmartboys.infrastructure.convertor;

import java.util.List;

/**
 * <p>继承此类实现DO/DTO类与Entity类之间的转换。</p>
 *
 * @param <D> DO/DTO类
 * @param <E> Entity类
 */
public interface BaseConvertor<D, E> {
    String COMPONENT_MODEL = "spring";
    E toEntity(D dto);

    D fromEntity(E entity);

    List<E> toEntity(List<D> dtoList);

    List<D> fromEntity(List<E> entityList);
}
