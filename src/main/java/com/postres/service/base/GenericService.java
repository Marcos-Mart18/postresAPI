package com.postres.service.base;

import org.hibernate.service.spi.ServiceException;

import java.util.List;

public interface GenericService<E,DTO,ID>{
    DTO create(DTO dto) throws ServiceException;
    DTO update(ID id,DTO dto) throws ServiceException;
    DTO findById(ID id) throws ServiceException;
    void deleteById(ID id) throws ServiceException;
    List<DTO> findAll() throws ServiceException;
}
