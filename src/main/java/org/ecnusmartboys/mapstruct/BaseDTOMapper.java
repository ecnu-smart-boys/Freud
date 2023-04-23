package org.ecnusmartboys.mapstruct;

import java.util.List;

/**
 * <p>用于MapStruct的DTO类与Entity类之间转换的抽象类。</p>
 *
 * <p>继承此类实现DTO类与Entity类之间的转换。</p>
 *
 * @param <D> DTO类
 * @param <E> Entity类
 */
public interface BaseDTOMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    List<E> toEntity(List<D> dtoList);

    List<D> toDto(List<E> entityList);
}
